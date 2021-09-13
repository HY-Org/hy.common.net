package org.hy.common.net.junit.netty.chat;

import java.util.Scanner;

import org.hy.common.xml.log.Logger;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;





/**
 * 聊天软件的客户端
 * 
 * @author      ZhengWei(HY)
 * @createDate  2021-09-13
 * @version     v1.0
 */
public class ChatClient
{
    private static final Logger $Logger = new Logger(ChatServer.class ,true);
    
    private String  host;
    
    private int     port;
    
    private boolean isStart;
    
    EventLoopGroup  clientGroup;
    
    
    
    public ChatClient()
    {
        this.host = "127.0.0.1";
        this.port = 2021;
    }
    
    
    
    public synchronized void start()
    {
        if ( this.isStart )
        {
            return;
        }
        
        this.clientGroup = new NioEventLoopGroup();
        
        try
        {
            Bootstrap v_Booostrap = new Bootstrap();
            v_Booostrap.group(this.clientGroup);
            v_Booostrap.channel(NioSocketChannel.class);
            v_Booostrap.handler(new ChatClientInitChannel());
            
            ChannelFuture v_ChannelFuture = v_Booostrap.connect(this.host ,this.port).sync();
            Channel       v_Channel       = v_ChannelFuture.channel();
            
            $Logger.info("客户端准备完成");
            
            try ( Scanner v_Scanner = new Scanner(System.in) )
            {
                while ( v_Scanner.hasNextLine() )
                {
                    String v_Message = v_Scanner.nextLine();
                    v_Channel.writeAndFlush(v_Message);
                }
            }
        }
        catch (Exception e)
        {
            $Logger.error(e);
            this.shutdown();
        }
    }
    
    
    
    public synchronized void shutdown()
    {
        if ( this.clientGroup != null )
        {
            this.clientGroup.shutdownGracefully();
        }
        
        this.isStart = false;
    }
    
    
    
    public static void main(String [] args)
    {
        new ChatClient().start();
    }
    
}
