package org.hy.common.net.junit.nio.chat;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Scanner;
import java.util.Set;

import org.hy.common.Execute;
import org.hy.common.Help;
import org.hy.common.xml.log.Logger;

/**
 * 聊天软件的客户端
 * 
 * @author      ZhengWei(HY)
 * @createDate  2021-09-03
 * @version     v1.0
 */
public class ChatClient
{
    private static final Logger $Logger = new Logger(ChatServer.class ,true);
    
    /**  */
    private String        serverHost;
    
    private int           serverPort;
    
    /** 监听是否启动的状态标示 */
    private boolean       isStart;
    
    /** 是否控制服务监听停止命令 */
    private boolean       isStop;
    
    private String        userName;
    
    /** 缓冲区的大小 */
    private int           bufferSize;
    
    private Selector      selector;
    
    private SocketChannel channel;
    
    
    
    public ChatClient()
    {
        this.serverHost = "127.0.0.1";
        this.serverPort = 2021;
        this.bufferSize = 1024 * 4;
        this.isStart    = false;
        this.isStop     = true;
    }
    
    
    
    public synchronized void start()
    {
        if ( this.isStart )
        {
            return;
        }
        
        try
        {
            this.selector = Selector.open();
            this.channel  = SocketChannel.open(new InetSocketAddress(this.serverHost ,this.serverPort));
            this.channel.configureBlocking(false);   // 也是非阻塞的
            
            this.channel.register(this.selector ,SelectionKey.OP_READ);
            
            this.userName = this.channel.getLocalAddress().toString();
            
            this.isStart = true;
            this.isStop  = false;
            new Execute(this ,"listen").start();
            $Logger.info(this.userName + " is 上线");
        }
        catch (Exception e)
        {
            $Logger.error(e);
        }
    }
    
    
    
    /**
     * 发送消息
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-09-03
     * @version     v1.0
     */
    public void send(String i_Message)
    {
        synchronized ( this )
        {
            if ( !this.isStart || this.isStop || Help.isNull(i_Message) )
            {
                return;
            }
        }
        
        try
        {
            this.channel.write(ByteBuffer.wrap((this.userName + "说：" + i_Message).getBytes()));
        }
        catch (Exception e)
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
        synchronized ( this )
        {
            if ( !this.isStart || this.isStop )
            {
                return;
            }
        }
        
        while ( this.isStart && !this.isStop )
        {
            try
            {
                int v_EventCount = this.selector.select();
                if ( v_EventCount <= 0 )
                {
                    return;
                }
                
                Set<SelectionKey> v_SKeys = this.selector.selectedKeys();
                for (SelectionKey vSKey : v_SKeys)
                {
                    if ( vSKey.isReadable() )
                    {
                        this.request(vSKey);
                    }
                }
                
                v_SKeys.clear();
            }
            catch (Exception e)
            {
                $Logger.error(e);
            }
        }
    }
    
    
    
    /**
     * 接受服务端的消息
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-09-03
     * @version     v1.0
     */
    private void request(SelectionKey i_SKey)
    {
        SocketChannel v_Channel = null;
        
        try
        {
            v_Channel = (SocketChannel)i_SKey.channel();
            v_Channel.configureBlocking(false);
            
            ByteBuffer v_Buffer = ByteBuffer.allocate(this.bufferSize);
            v_Channel.read(v_Buffer);
            
            $Logger.info(new String(v_Buffer.array()).trim());
        }
        catch (Exception e)
        {
            $Logger.error(e);
        }
    }
    
    
    
    public static void main(String [] args)
    {
        ChatClient v_Client = new ChatClient();
        v_Client.start();
        
        Scanner v_Scanner = new Scanner(System.in);
        while ( v_Scanner.hasNextLine() )
        {
            String v_Message = v_Scanner.nextLine();
            v_Client.send(v_Message);
        }
        
        v_Scanner.close();
    }
    
}
