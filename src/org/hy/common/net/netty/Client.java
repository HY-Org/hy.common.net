package org.hy.common.net.netty;

import org.hy.common.xml.log.Logger;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
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
    
    private Channel         channel;
    
    
    
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
        return this.start(this.newBootstrap());
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
            this.bootstrap = io_Bootstrap;
            this.bootstrap.group(this.clientGroup);
            this.bootstrap.handler(new ClientInitChannel<T>(this));
            
            ChannelFuture v_ChannelFuture = this.bootstrap.connect(this.host ,this.port).sync();
            this.channel                  = v_ChannelFuture.channel();
            
            $Logger.info("客户端准备完成");
        }
        catch (Exception e)
        {
            $Logger.error(e);
            this.shutdown();
        }
        
        return (T) this;
    }
    
    
    
    /**
     * 关闭程序
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-09-16
     * @version     v1.0
     */
    @Override
    public synchronized void shutdown()
    {
        if ( this.clientGroup != null )
        {
            this.clientGroup.shutdownGracefully();
        }
        
        this.isStart = false;
    }
    
}
