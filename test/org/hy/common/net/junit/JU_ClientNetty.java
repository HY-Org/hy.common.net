package org.hy.common.net.junit;


import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;





public class JU_ClientNetty
{

    public static void main(String [] args) throws InterruptedException
    {
        new Client().start(7000 ,7000);
        
        
        int i = 0;
        while (i < 100)
        {
            try
            {
                Thread.sleep(1000 * 10);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            System.out.print(".");
            
            i++;
        }
        
//        Bootstrap bootstrap = new Bootstrap();
//        NioEventLoopGroup group = new NioEventLoopGroup();
//        bootstrap.group(group).channel(NioSocketChannel.class).handler(new ChannelInitializer<Channel>() {
//
//            @Override
//            protected void initChannel(Channel ch)
//            {
//                ch.pipeline().addLast(new StringEncoder());
//            }
//        });
//        
//        Channel channel = bootstrap.connect("127.0.0.1" ,8000).channel();
//        while ( true )
//        {
//            channel.writeAndFlush(new Date() + ": hello world!");
//            Thread.sleep(2000);
//        }
    }
}



class Client 
{

    private static final String SERVER_HOST = "10.1.50.78";

    public void start(final int beginPort, int nPort) 
    {
        System.out.println("client starting....");
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        final Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.option(ChannelOption.SO_REUSEADDR, true);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) 
            {
                ch.pipeline().addLast(new StringEncoder());
            }
        });
        int index = 0;
        int port;
        while (!Thread.interrupted()) {
            port = beginPort + index;
            
            if (port <= nPort) 
            {
                try
                {
                    ChannelFuture channelFuture = bootstrap.connect(SERVER_HOST ,port);
                    channelFuture.addListener(new ClientChannelFuture(port));
                    channelFuture.get();
                    channelFuture.channel().closeFuture();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                index++;
            }
            else
            {
                index = 0;
            }
        }
    }
}



class ClientChannelFuture implements ChannelFutureListener
{
    private int port;
    
    
    public ClientChannelFuture(int i_Port)
    {
        this.port = i_Port;
    }
    
    
    @Override
    public void operationComplete(ChannelFuture future) throws Exception
    {
        if (!future.isSuccess()) 
        {
            System.out.println("连接失败(" + this.port + "), 退出!");
        }
        else
        {
            System.out.println("连接成功(" + this.port + ")");
        }
    }
    
}
