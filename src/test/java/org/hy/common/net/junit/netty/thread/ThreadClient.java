package org.hy.common.net.junit.netty.thread;

import org.hy.common.net.netty.Client;

import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;





/**
 * 多线程的处理器客户端
 * 
 * @author      ZhengWei(HY)
 * @createDate  2021-09-23
 * @version     v1.0
 */
public class ThreadClient extends Client<ThreadClient>
{

    @Override
    public void initChannel(SocketChannel i_Channel ,ChannelPipeline i_Pipeline)
    {
        i_Pipeline.addLast(new StringDecoder(CharsetUtil.UTF_8));
        i_Pipeline.addLast(new StringEncoder(CharsetUtil.UTF_8));
        i_Pipeline.addLast(new ThreadClientHandler());
    }
 
    
    
    public static void main(String [] args)
    {
        new ThreadClient().setHost("127.0.0.1").setPort(2021).start();
    }
    
}
