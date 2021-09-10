package org.hy.common.net.junit.netty.t002;

import org.hy.common.xml.log.Logger;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;





/**
 * Netty的服务端
 * 
 * @author      ZhengWei(HY)
 * @createDate  2021-09-09
 * @version     v1.0
 */
public class JU_NettyServer
{
    private static final Logger $Logger = new Logger(JU_NettyServer.class ,true);
    
    
    
    public static void main(String [] args)
    {
        /*
         * 1. 创建两个线程组 BossGroup 和 WorkerGroup
         * 2. BossGroup只处理连接请求，真正的业务处理，会交给WorkerGroup完成
         * 3. 两者都是无限循环
         * 4. BossGroup 和 WorkerGroup 默认的线程数是：CPU核数 * 2
         */
        EventLoopGroup v_BossGroup   = new NioEventLoopGroup();
        EventLoopGroup v_WorkerGroup = new NioEventLoopGroup();
        
        try
        {
            // 配置服务端的启动参数
            ServerBootstrap v_Bootstrap = new ServerBootstrap();
            v_Bootstrap.group(v_BossGroup ,v_WorkerGroup);                        // 设置两个线程组
            v_Bootstrap.channel(NioServerSocketChannel.class);                    // 服务器通道实现
            v_Bootstrap.option(ChannelOption.SO_BACKLOG ,128);                    // 线程队列得到连接个数
            v_Bootstrap.childOption(ChannelOption.SO_KEEPALIVE ,true);            // 保持活动连接状态
            v_Bootstrap.childHandler(new ChannelInitializer<SocketChannel>()      // 创建一个通道pipeLine对象，给我们的WorkerGroup的EventLoop设置管道处理器
            {
                @Override
                protected void initChannel(SocketChannel i_Channel) throws Exception
                {
                    $Logger.info("（客户上线）标示：" + i_Channel.hashCode());
                    i_Channel.pipeline().addLast(new NettyServerHandler());  // 关联处理器
                }}
            );
            
            // 绑定一个端口并同步生成一个ChannelFuture对象
            int           v_Port          = 2021;
            ChannelFuture v_ChannelFuture = v_Bootstrap.bind(v_Port).sync();  // 异步非阻塞
            
            v_ChannelFuture.addListener(new ChannelFutureListener()
            {
                @Override
                public void operationComplete(ChannelFuture i_Future) throws Exception
                {
                    if ( i_Future.isSuccess() )
                    {
                        $Logger.info("服务端启动完成，监听端口：" + v_Port);
                    }
                    else
                    {
                        $Logger.error("服务端监听端口失败：" + v_Port);
                    }
                }}
            );
            
            // 对关闭通道监听
            v_ChannelFuture.channel().closeFuture().sync();
        }
        catch (Exception exce)
        {
            $Logger.error(exce);
        }
        finally
        {
            // 优雅的关闭
            v_BossGroup  .shutdownGracefully();
            v_WorkerGroup.shutdownGracefully();
        }
    }
    
}
