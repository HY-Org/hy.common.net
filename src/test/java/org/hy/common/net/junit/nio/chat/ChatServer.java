package org.hy.common.net.junit.nio.chat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Set;

import org.hy.common.Execute;
import org.hy.common.xml.log.Logger;





/**
 * 聊天软件的服务端
 * 
 * @author      ZhengWei(HY)
 * @createDate  2021-09-03
 * @version     v1.0
 */
public class ChatServer
{
    private static final Logger $Logger = new Logger(ChatServer.class ,true);
    
    
    /** 服务器主监听端口 */
    private int                 port;
    
    /** 监听是否启动的状态标示 */
    private boolean             isStart;
    
    /** 是否控制服务监听停止命令 */
    private boolean             isStop;
    
    /** 缓冲区的大小 */
    private int                 bufferSize;
    
    private Selector            selector;
    
    private ServerSocketChannel channel;
    
    
    
    public ChatServer()
    {
        this.port       = 2021;
        this.bufferSize = 1024 * 4;
        this.isStart    = false;
        this.isStop     = true;
    }
    
    
    
    /**
     * 启用服务
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-09-03
     * @version     v1.0
     */
    public synchronized void start()
    {
        if ( this.isStart )
        {
            return;
        }
        
        try
        {
            this.selector = Selector.open();
            this.channel  = ServerSocketChannel.open();
            
            this.channel.bind(new InetSocketAddress(this.port));
            this.channel.configureBlocking(false);                    // 非阻塞
            this.channel.register(selector ,SelectionKey.OP_ACCEPT);
            
            
            this.isStart = true;
            this.isStop  = false;
            new Execute(this ,"listen").start();
            $Logger.info("服务启动完成，监听端口：" + this.port);
        }
        catch (IOException e)
        {
            $Logger.error(e);
        }
    }
    
    
    
    /**
     * 监听事件
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-09-03
     * @version     v1.0
     */
    public void listen()
    {
        while ( this.isStart && !this.isStop )
        {
            try
            {
                int v_EventCount = this.selector.select();
                if ( v_EventCount <= 0 )
                {
                    continue;
                }
                
                Set<SelectionKey> v_SKeys = this.selector.selectedKeys();
                for (SelectionKey v_SKey : v_SKeys)
                {
                    if ( v_SKey.isAcceptable() )
                    {
                        this.accept(v_SKey);    // 连接事件
                    }
                    else if ( v_SKey.isReadable() )
                    {
                        this.request(v_SKey);   // 读取客户端消息的事件
                    }
                }
                
                v_SKeys.clear();
            }
            catch (Exception exce)
            {
                $Logger.error(exce);
            }
        }
        
        $Logger.info("关闭服务处理");
    }
    
    
    
    /**
     * 连接客户端
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-09-03
     * @version     v1.0
     */
    private void accept(SelectionKey i_SKey)
    {
        SocketChannel v_ClientChannel = null;
        
        try
        {
            v_ClientChannel = this.channel.accept();
            v_ClientChannel.configureBlocking(false);
            
            v_ClientChannel.register(this.selector ,SelectionKey.OP_READ);
            
            $Logger.info(v_ClientChannel.getRemoteAddress() + " 上线");
        }
        catch (Exception e)
        {
            $Logger.error("连接时异常" ,e);
        }
    }
    
    
    
    /**
     * 接受客户端的消息
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-09-03
     * @version     v1.0
     */
    private void request(SelectionKey i_SKey)
    {
        SocketChannel v_ClientChannel = null;
        
        try
        {
            v_ClientChannel = (SocketChannel) i_SKey.channel();
            ByteBuffer v_ClientBuffer = ByteBuffer.allocate(this.bufferSize);
            int        v_ReadSize     = v_ClientChannel.read(v_ClientBuffer);
            
            if ( v_ReadSize > 0 )
            {
                String v_Message = new String(v_ClientBuffer.array());
                $Logger.info("from："  + v_ClientChannel.getRemoteAddress() + v_Message);
                
                this.sendToOthers(v_ClientChannel ,v_Message);
            }
        }
        catch (Exception e)
        {
            $Logger.error("接受客户端请求异常" ,e);
            
            i_SKey.cancel();
            
            if ( v_ClientChannel != null )
            {
                try
                {
                    $Logger.info(v_ClientChannel.getRemoteAddress() + " 离线了");
                    v_ClientChannel.close();
                }
                catch (Exception exce)
                {
                    // Nothing.
                }
            }
        }
    }
    
    
    
    /**
     * 接受客户端的消息
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-09-03
     * @version     v1.0
     * 
     * @param i_Sender   发送者
     * @param i_Message  发送的消息
     */
    private void sendToOthers(SocketChannel i_Sender ,String i_Message)
    {
        for (SelectionKey v_SKey : this.selector.keys())
        {
            Channel v_ClientChannel = v_SKey.channel();
            
            if ( v_ClientChannel instanceof SocketChannel && v_ClientChannel != i_Sender )
            {
                try
                {
                    ((SocketChannel)v_ClientChannel).write(ByteBuffer.wrap(i_Message.getBytes()));
                }
                catch (Exception e)
                {
                    $Logger.error(e);
                }
            }
        }
    }

    
    
    public int getPort()
    {
        return port;
    }


    
    public void setPort(int port)
    {
        this.port = port;
    }
    
    
    
    public static void main(String [] args)
    {
        ChatServer v_Server = new ChatServer();
        v_Server.start();
    }
    
}
