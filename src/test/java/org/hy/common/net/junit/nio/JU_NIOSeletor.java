package org.hy.common.net.junit.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Set;

import org.hy.common.xml.log.Logger;
import org.junit.Test;





/**
 * 测试单元：多路复用器Selector
 * 
 * @author      ZhengWei(HY)
 * @createDate  2021-09-01
 * @version     v1.0
 */
public class JU_NIOSeletor
{
    private static final Logger $Logger = new Logger(JU_NIOSeletor.class ,true);
    
    
    
    @Test
    public void test_Server服务端() throws IOException
    {
        // 得到一个多路复用器Selector
        Selector v_Selector = Selector.open();
        
        
        // 获取一个服务主端口的通道
        ServerSocketChannel v_SSChannel = ServerSocketChannel.open();
        // 绑定端口，并启动
        v_SSChannel.socket().bind(new InetSocketAddress(2021));
        // 非阻塞模式
        v_SSChannel.configureBlocking(false);
        
        // 注册接受数据开始的事件
        v_SSChannel.register(v_Selector ,SelectionKey.OP_ACCEPT);
        
        
        while ( true )
        {
            int v_SelectorKey = v_Selector.select(1000);
            if ( v_SelectorKey == 0 )
            {
                $Logger.info("服务器等待了1秒，无连接");
                continue;
            }
            
            // 返回关注事件的集合
            Set<SelectionKey> v_SelectionKeys = v_Selector.selectedKeys();
            v_SelectionKeys.forEach(vi_SKey ->
            {
                // 如果是新客服端的连接事件：SelectionKey.OP_ACCEPT
                if ( vi_SKey.isAcceptable() )
                {
                    try
                    {
                        SocketChannel v_Channel = v_SSChannel.accept();
                        v_Channel.configureBlocking(false);
                        
                        // 注册数据读取事件，并关联一个Buffer
                        v_Channel.register(v_Selector ,SelectionKey.OP_READ ,ByteBuffer.allocate(1024 * 4));
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
                else if ( vi_SKey.isReadable() )
                {
                    // 反向获取到对应的Channel
                    SocketChannel v_Channel = (SocketChannel)vi_SKey.channel();
                    ByteBuffer    v_Buffer  = (ByteBuffer)   vi_SKey.attachment();
                    
                    try
                    {
                        v_Channel.read(v_Buffer);
                        $Logger.info("接受到客户端消息：" + new String(v_Buffer.array()));
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
                
                // 十分重要的操作：手动从集合中移除当前SelectionKey，防止多线程下的重复操作。
                v_SelectionKeys.remove(vi_SKey);
            });
            
            System.out.println();
            v_SelectionKeys.clear();
        }
        
    }
    
    
    
    @Test
    public void test_Client客服端() throws IOException
    {
        SocketChannel v_Channel = SocketChannel.open();
        v_Channel.configureBlocking(false);  // 非阻塞
        
        if ( !v_Channel.connect(new InetSocketAddress("127.0.0.1" ,2021)) )  // 连接也是非阻塞的
        {
            while ( !v_Channel.finishConnect() )
            {
                $Logger.info("等待建立连接");
            }
        }
        
        // 不用指定缓冲区的大小，按数据大小自适配生成缓冲区对象
        ByteBuffer v_Buffer = ByteBuffer.wrap(new String("Hello Server.").getBytes());
        v_Channel.write(v_Buffer);
        
        $Logger.info("按任意键结束");
        System.in.read();
        
        v_Channel.close();
    }
    
}
