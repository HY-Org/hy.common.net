package org.hy.common.net.junit;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;





public class JU_ServerNetty
{

    public static void main(String [] args)
    {
        // java -classpath ".:/opt/lib/*"  org.hy.common.net.junit.JU_ServerNetty
        System.out.println("server starting....");

        ServerBootstrap serverBootstrap = new ServerBootstrap();
        NioEventLoopGroup v_Boss   = new NioEventLoopGroup();    // 接受新连接线程，主要负责创建新连接
        NioEventLoopGroup v_Worker = new NioEventLoopGroup();    // 负责读取数据的线程，主要用于读取数据以及业务逻辑处理
        
        serverBootstrap.group(v_Boss ,v_Worker);
        serverBootstrap.channel(NioServerSocketChannel.class);
        serverBootstrap.childOption(ChannelOption.SO_REUSEADDR, true);
        serverBootstrap.childHandler(new ConnectionCountHandler());
        
        bindPorts(serverBootstrap ,7000 ,7000);
        
        System.out.println("server started!");
    }
    
    
    
    public static void bindPorts(ServerBootstrap i_Server ,int i_BeginPort ,int i_EndPort)
    {
        for (int v_Port=i_BeginPort; v_Port<=i_EndPort; v_Port++) 
        {
            i_Server.bind(v_Port).addListener(new ServerChannelFuture(v_Port));
        }
    }
    
}



class ServerChannelFuture implements ChannelFutureListener
{
    private int port;
    
    
    public ServerChannelFuture(int i_Port)
    {
        this.port = i_Port;
    }
    
    
    @Override
    public void operationComplete(ChannelFuture future) throws Exception
    {
        System.out.println("bind success in port: " + port);
    }
    
}





class ChannelServer extends ChannelInitializer<NioSocketChannel>
{

    protected void initChannel(NioSocketChannel ch)
    {
        ch.pipeline().addLast(new StringDecoder());
        ch.pipeline().addLast(new SimpleChannelInboundHandler<String>() {
            
            @Override
            protected void channelRead0(ChannelHandlerContext ctx ,String msg)
            {
                System.out.println(msg);
            }
        });
    }
}
