package org.hy.common.net.junit.netty.thread;

import org.hy.common.net.netty.Server;

import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;





/**
 * 多线程的处理器服务端
 * 
 * @author      ZhengWei(HY)
 * @createDate  2021-09-23
 * @version     v1.0
 */
public class ThreadServer extends Server<ThreadServer>
{
    
    // 全局共享型的线程池：创建业务线程池（16个线程）
    private static final EventExecutorGroup $ServiceThreadPool = new DefaultEventExecutorGroup(16);
    
    

    @Override
    public void initChannel(SocketChannel i_Channel ,ChannelPipeline i_Pipeline)
    {
        i_Pipeline.addLast(new StringDecoder(CharsetUtil.UTF_8));
        i_Pipeline.addLast(new StringEncoder(CharsetUtil.UTF_8));
        i_Pipeline.addLast($ServiceThreadPool ,new ThreadServerHandler_V3());
    }
    
    
    
    public static void main(String [] args)
    {
        new ThreadServer().setPort(2021).start();
    }
    
}
