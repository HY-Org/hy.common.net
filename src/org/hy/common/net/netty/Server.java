package org.hy.common.net.netty;

import org.hy.common.net.junit.netty.chat.ChatServer;
import org.hy.common.xml.log.Logger;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;





/**
 * Netty服务端
 * 
 * @author      ZhengWei(HY)
 * @createDate  2021-09-10
 * @version     v1.0
 */
public abstract class Server<T extends Server<T>> extends App<T>
{
    private static final Logger $Logger = new Logger(ChatServer.class ,true);
    
    
    /** 接受新连接线程，主要负责创建新连接 */
    private EventLoopGroup  bossGroup;
    
    /** 负责读取数据的线程，主要用于读取数据以及业务逻辑处理 */
    private EventLoopGroup  workerGroup;
    
    /** 服务启动配置参数 */
    private ServerBootstrap bootstrap;
    
    
    
    /**
     * 初始通道
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-09-14
     * @version     v1.0
     */
    public abstract void initChannel(SocketChannel i_Channel ,ChannelPipeline i_Pipeline);
    
    
    
    public Server()
    {
        super();
    }
    
    
    
    /**
     * 生成一个默认的配置服务端的启动参数
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-09-14
     * @version     v1.0
     * 
     * @return
     */
    public ServerBootstrap newBootstrap()
    {
        ServerBootstrap v_Bootstrap = new ServerBootstrap();
        
        v_Bootstrap.channel(NioServerSocketChannel.class);                           // 服务器通道实现
        v_Bootstrap.option(     ChannelOption.SO_BACKLOG   ,1024);                   // 线程队列等待的连接个数
        v_Bootstrap.childOption(ChannelOption.SO_KEEPALIVE ,true);                   // 保持活动连接状态
        
        return v_Bootstrap;
    }
    
    
    
    /**
     * 启动服务，配置参数使用默认
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-09-14
     * @version     v1.0
     */
    @Override
    public synchronized void start()
    {
        this.start(this.newBootstrap());
    }
    
    
    
    /**
     * 启动服务，配置参数外界传入
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-09-14
     * @version     v1.0
     * 
     * @param io_Bootstrap
     */
    public synchronized void start(ServerBootstrap io_Bootstrap)
    {
        if ( this.isStart )
        {
            $Logger.warn("服务已启动，请勿重复启动");
            return;
        }
        this.isStart = true;
        
        try
        {
            this.bossGroup   = new NioEventLoopGroup();
            this.workerGroup = new NioEventLoopGroup();
            
            this.bootstrap = io_Bootstrap;
            this.bootstrap.group(this.bossGroup ,this.workerGroup);                  // 设置两个线程组
            this.bootstrap.childHandler(new ServerInitChannel<T>(this));             // 创建一个通道pipeLine对象，给我们的WorkerGroup的EventLoop设置管道处理器
            
            ChannelFuture v_ChannelFuture = this.bootstrap.bind(this.port).sync();   // 异步非阻塞
            
            v_ChannelFuture.addListener(new ChannelFutureListener()
            {
                @Override
                public void operationComplete(ChannelFuture i_ChannelFuture) throws Exception
                {
                    if ( i_ChannelFuture.isSuccess() )
                    {
                        $Logger.info("服务端启动完成，监听端口：" + port);
                    }
                    else
                    {
                        $Logger.error("服务端监听端口失败：" + port);
                    }
                }
            });
            
            v_ChannelFuture.channel().closeFuture().sync();                          // 对关闭通道监听
        }
        catch (Exception e)
        {
            $Logger.error(e);
            this.shutdown();
        }
    }
    
    
    
    /**
     * 关闭服务
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-09-14
     * @version     v1.0
     */
    @Override
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

}
