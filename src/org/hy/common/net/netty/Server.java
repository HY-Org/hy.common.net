package org.hy.common.net.netty;

import org.hy.common.net.common.App;
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
    private static final Logger $Logger = new Logger(Server.class ,true);
    
    
    /** BossGroup分配的线程数。默认的线程数是：CPU核数 * 2 */
    private int             bossMaxThread;
    
    /** WorkerGroup分配的线程数。默认的线程数是：CPU核数 * 2 */
    private int             workerMaxThread;
    
    /** 接受新连接线程，主要负责创建新连接 */
    private EventLoopGroup  bossGroup;
    
    /** 负责读取数据的线程，主要用于读取数据以及业务逻辑处理 */
    private EventLoopGroup  workerGroup;
    
    /** 服务启动配置参数 */
    private ServerBootstrap bootstrap;
    
    /** 线程队列等待的连接个数 */
    private int             waitConnMax;
    
    /**
     * 同一账户多次登录的最大上限，当超过时自动退出之前的任一登录账户。
     * 
     * 默认为：0，表示允许同一账户无限制登录
     * 设置成：1，表示当前最新的登录，将踢出之前的登录
     */
    private int             sameUserOnlineMaxCount;
    
    /** 渠道 */
    private ChannelFuture   channelFuture;
    
    
    
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
        this.bossMaxThread          = 0;         // 0值表示：默认的线程数是：CPU核数 * 2
        this.workerMaxThread        = 0;         // 0值表示：默认的线程数是：CPU核数 * 2
        this.waitConnMax            = 1024 * 4;
        this.sameUserOnlineMaxCount = 0;
        this.bootstrap              = this.newBootstrap();
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
    public synchronized T start()
    {
        return this.start(this.bootstrap);
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
    @SuppressWarnings("unchecked")
    public synchronized T start(ServerBootstrap io_Bootstrap)
    {
        if ( this.isStart )
        {
            $Logger.warn("服务已启动，请勿重复启动");
            return (T) this;
        }
        
        super.start();
        
        try
        {
            /*
             * 1. 创建两个线程组 BossGroup 和 WorkerGroup
             * 2. BossGroup只处理连接请求，真正的业务处理，会交给WorkerGroup完成
             * 3. 两者都是无限循环
             * 4. BossGroup 和 WorkerGroup 默认的线程数是：CPU核数 * 2
             */
            this.bossGroup   = new NioEventLoopGroup(this.bossMaxThread);
            this.workerGroup = new NioEventLoopGroup(this.workerMaxThread);
            
            this.bootstrap = io_Bootstrap;
            this.bootstrap.group(this.bossGroup ,this.workerGroup);                  // 设置两个线程组
            this.bootstrap.childHandler(new ServerInitChannel<T>(this));             // 创建一个通道pipeLine对象，给我们的WorkerGroup的EventLoop设置管道处理器
            this.bootstrap.option(ChannelOption.SO_BACKLOG  ,this.waitConnMax);      // 线程队列等待的连接个数
            
            this.channelFuture = this.bootstrap.bind(this.port).sync();              // 异步非阻塞
            
            this.channelFuture.addListener(new ChannelFutureListener()
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
        }
        catch (Exception e)
        {
            $Logger.error(e);
            this.shutdown();
        }
        
        return (T) this;
    }
    
    
    
    /**
     * 对关闭通道监听
     */
    public void sync()
    {
        if ( this.channelFuture != null )
        {
            try
            {
                this.channelFuture.channel().close().sync();
            }
            catch (Exception e)
            {
                $Logger.error(e);
                this.shutdown();
            }
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
        
        super.shutdown();
    }



    /**
     * 获取：线程队列等待的连接个数
     * 
     * @return
     */
    public int getWaitConnMax()
    {
        return waitConnMax;
    }


    
    /**
     * 设置：线程队列等待的连接个数
     * 
     * @param waitConnMax
     */
    @SuppressWarnings("unchecked")
    public T setWaitConnMax(int waitConnMax)
    {
        this.waitConnMax = waitConnMax;
        return (T) this;
    }



    /**
     * 获取：同一账户多次登录的最大上限，当超过时自动退出之前的任一登录账户。
     * 
     * 默认为：0，表示允许同一账户无限制登录
     * 设置成：1，表示当前最新的登录，将踢出之前的登录
     * 
     * @return
     */
    public int getSameUserOnlineMaxCount()
    {
        return sameUserOnlineMaxCount;
    }



    /**
     * 设置：同一账户多次登录的最大上限，当超过时自动退出之前的任一登录账户。
     * 
     * 默认为：0，表示允许同一账户无限制登录
     * 设置成：1，表示当前最新的登录，将踢出之前的登录
     * 
     * @param waitConnMax
     */
    public void setSameUserOnlineMaxCount(int sameUserOnlineMaxCount)
    {
        this.sameUserOnlineMaxCount = sameUserOnlineMaxCount;
    }



    /**
     * 获取：BossGroup分配的线程数。默认的线程数是：CPU核数 * 2
     * 
     * @return
     */
    public int getBossMaxThread()
    {
        return bossMaxThread;
    }



    
    public void setBossMaxThread(int bossMaxThread)
    {
        this.bossMaxThread = bossMaxThread;
    }



    /**
     * 获取：WorkerGroup分配的线程数。默认的线程数是：CPU核数 * 2
     * 
     * @return
     */
    public int getWorkerMaxThread()
    {
        return workerMaxThread;
    }



    
    public void setWorkerMaxThread(int workerMaxThread)
    {
        this.workerMaxThread = workerMaxThread;
    }

}
