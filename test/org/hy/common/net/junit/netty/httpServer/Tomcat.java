package org.hy.common.net.junit.netty.httpServer;

import org.hy.common.xml.log.Logger;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;





/**
 * Http服务
 * 
 * <HttpObject> 表示客户端与服务端相互通讯的数据封装类
 * 
 * @author      ZhengWei(HY)
 * @createDate  2021-09-10
 * @version     v1.0
 */
public class Tomcat
{
    private static final Logger $Logger = new Logger(Tomcat.class ,true);
    
    private EventLoopGroup bossGroup;
    
    private EventLoopGroup workerGroup;
    
    private boolean        isStart;
    
    private int            port;
    
    
    
    public Tomcat()
    {
        this.isStart = false;
        this.port    = 80;
    }
    
    
    
    public synchronized void start()
    {
        if ( this.isStart )
        {
            return;
        }
        
        this.bossGroup   = new NioEventLoopGroup(5);
        this.workerGroup = new NioEventLoopGroup();
        
        try
        {
            ServerBootstrap v_Bootstrap = new ServerBootstrap();
            v_Bootstrap.group(this.bossGroup ,this.workerGroup);
            v_Bootstrap.channel(NioServerSocketChannel.class);
            v_Bootstrap.childOption(ChannelOption.SO_KEEPALIVE ,true);            // 保持活动连接状态
            v_Bootstrap.childHandler(new TomcatInitChannel());
            
            ChannelFuture v_ChannelFuture = v_Bootstrap.bind(this.port).sync();   // 异步非阻塞
            v_ChannelFuture.addListener(new ChannelFutureListener()
            {
                @Override
                public void operationComplete(ChannelFuture i_Future) throws Exception
                {
                    if ( i_Future.isSuccess() )
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
            
            v_ChannelFuture.channel().closeFuture().sync();                       // 对关闭通道监听
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
        Tomcat v_Tomcat = new Tomcat();
        v_Tomcat.start();
    }
    
}
