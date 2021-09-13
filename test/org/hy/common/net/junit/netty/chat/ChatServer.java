package org.hy.common.net.junit.netty.chat;

import org.hy.common.xml.log.Logger;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;





public class ChatServer
{
    private static final Logger $Logger = new Logger(ChatServer.class ,true);
    
    private boolean        isStart;
    
    private int            port;
    
    private EventLoopGroup bossGroup;
    
    private EventLoopGroup workerGroup;
    
    
    
    public ChatServer()
    {
        this.port = 2021;
    }
    
    
    
    public synchronized void start()
    {
        if ( this.isStart )
        {
            return;
        }
        
        this.bossGroup   = new NioEventLoopGroup(4);
        this.workerGroup = new NioEventLoopGroup();
        
        try
        {
            ServerBootstrap v_Bootstrap = new ServerBootstrap();
            
            v_Bootstrap.group(this.bossGroup ,this.workerGroup);
            v_Bootstrap.channel(NioServerSocketChannel.class);
            v_Bootstrap.option(ChannelOption.SO_BACKLOG ,1024);
            v_Bootstrap.childOption(ChannelOption.SO_KEEPALIVE ,true);
            v_Bootstrap.childHandler(new ChatServerInitChannel());
            
            ChannelFuture v_ChannelFuture = v_Bootstrap.bind(this.port).sync();
            v_ChannelFuture.addListener(new ChannelFutureListener()
            {
                @Override
                public void operationComplete(ChannelFuture i_ChannelFuture) throws Exception
                {
                    if ( i_ChannelFuture.isSuccess() )
                    {
                        $Logger.info("服务端启动完成，监听端口：" + port);
                        isStart = true;
                    }
                    else
                    {
                        $Logger.error("服务端监听端口失败：" + port);
                    }
                }
            });
            
            v_ChannelFuture.channel().closeFuture().sync();
        }
        catch (Exception e)
        {
            $Logger.error(e);
            this.shutdown();
        }
    }
    
    
    
    public synchronized void shutdown()
    {
        if ( this.bossGroup != null )
        {
            this.bossGroup.shutdownGracefully();
        }
        
        if ( this.workerGroup != null )
        {
            this.workerGroup.shutdownGracefully();
        }
        
        this.isStart = false;
    }
    
    
    
    public static void main(String [] args)
    {
        new ChatServer().start();
    }
    
}
