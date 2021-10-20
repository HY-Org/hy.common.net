package org.hy.common.net.junit.netty.t002;

import org.hy.common.xml.log.Logger;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;





/**
 * Netty的客户端
 * 
 * @author      ZhengWei(HY)
 * @createDate  2021-09-09
 * @version     v1.0
 */
public class JU_NettyClient
{
    private static final Logger $Logger = new Logger(JU_NettyClient.class ,true);
    
    
    
    public static void main(String [] args)
    {
        // 客户端只需一个事件循环组
        EventLoopGroup v_ClientGroup = new NioEventLoopGroup();
        
        try
        {
            // 配置客户端的启动参数
            Bootstrap v_Bootstarp = new Bootstrap();
            v_Bootstarp.group(v_ClientGroup);                             // 设置线程组
            v_Bootstarp.channel(NioSocketChannel.class);                     // 客户端通道实现
            v_Bootstarp.handler(new ChannelInitializer<SocketChannel>()
            {
                @Override
                protected void initChannel(SocketChannel i_Channel) throws Exception
                {
                    i_Channel.pipeline().addLast(new NettyClientHandler());  // 关联处理器
                }
            });
            
            $Logger.info("客户端启动完成");
            
            ChannelFuture v_ChannelFuture = v_Bootstarp.connect("127.0.0.1" ,2021).sync();   // 异步非阻塞
            
            // 对关闭通道监听
            v_ChannelFuture.channel().closeFuture().sync();
            
            Thread.sleep(1000 * 60 * 10);
        }
        catch (Exception e)
        {
            $Logger.error(e);
        }
        finally
        {
            v_ClientGroup.shutdownGracefully();    // 优雅的关闭
        }
    }
    
}
