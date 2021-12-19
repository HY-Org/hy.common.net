package org.hy.common.net.netty;

import org.hy.common.Help;
import org.hy.common.net.common.App;
import org.hy.common.net.data.Communication;
import org.hy.common.xml.log.Logger;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;





/**
 * Netty客户端
 * 
 * @author      ZhengWei(HY)
 * @createDate  2021-09-16
 * @version     v1.0
 */
public abstract class Client<T extends Client<T>> extends App<T>
{
    
    private static final Logger $Logger = new Logger(Client.class ,true);
    
    private EventLoopGroup  clientGroup;
    
    /** 启动配置参数 */
    private Bootstrap       bootstrap;
    
    /** 通讯渠道 */
    private Channel         channel;
    
    
    
    public Client()
    {
        super();
        this.bootstrap = this.newBootstrap();
    }
    
    
    
    /**
     * 初始通道
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-09-17
     * @version     v1.0
     */
    public abstract void initChannel(SocketChannel i_Channel ,ChannelPipeline i_Pipeline);
    
    
    
    /**
     * 发送消息给服务器
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-09-16
     * @version     v1.0
     * 
     * @param i_Msg
     */
    protected void send(Object i_Msg)
    {
        if ( !this.isStart || this.channel == null )
        {
            return;
        }
        
        this.channel.writeAndFlush(i_Msg);
    }
    
    
    
    /**
     * 生成一个默认的配置客户端的启动参数
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-09-16
     * @version     v1.0
     * 
     * @return
     */
    public Bootstrap newBootstrap()
    {
        Bootstrap v_Bootstrap = new Bootstrap();
        
        v_Bootstrap.channel(NioSocketChannel.class);
        v_Bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
        v_Bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, Help.NVL(this.getTimeout() ,Communication.$Default_WaitRequestTimeout).intValue());
        
        return v_Bootstrap;
    }
    
    
    
    /**
     * 启动程序，配置参数使用默认
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-09-16
     * @version     v1.0
     * 
     * @param io_Bootstrap
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
     * @createDate  2021-09-16
     * @version     v1.0
     * 
     * @param io_Bootstrap
     */
    @SuppressWarnings("unchecked")
    public synchronized T start(Bootstrap io_Bootstrap)
    {
        if ( this.isStart )
        {
            return (T) this;
        }
        
        this.clientGroup = new NioEventLoopGroup();
        
        try
        {
            this.bootstrap = io_Bootstrap.clone(this.clientGroup);
            // this.bootstrap.group(this.clientGroup);
            this.bootstrap.handler(new ClientInitChannel<T>(this));
            
            ChannelFuture v_ChannelFuture = this.bootstrap.connect(this.host ,this.port).sync();
            this.channel                  = v_ChannelFuture.channel();
            
            $Logger.info("通讯准备完成：" + this.getHostPort() + (Help.isNull(this.comment) ? "" : " -> " + this.comment));
            super.start();
            return (T) this;
        }
        catch (Exception e)
        {
            $Logger.error(e);
            this.shutdown();
        }
        
        return null;
    }
    
    
    
    /**
     * 关闭程序
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-09-16
     * @version     v1.0
     */
    @Override
    public synchronized  void shutdown()
    {
        if ( this.channel != null )
        {
            try
            {
                this.channel.close();
            }
            catch (Exception exce)
            {
                $Logger.error(exce);
            }
        }
        
        if ( this.clientGroup != null )
        {
            try
            {
                this.clientGroup.shutdownGracefully();
            }
            catch (Exception exce)
            {
                $Logger.error(exce);
            }
        }
        
        super.shutdown();
        $Logger.info("客户端已停止");
    }
    
}
