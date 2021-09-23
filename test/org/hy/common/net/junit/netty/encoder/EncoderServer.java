package org.hy.common.net.junit.netty.encoder;

import org.hy.common.net.netty.Server;

import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;





/**
 * 编解码器的客户端
 * 
 * @author      ZhengWei(HY)
 * @createDate  2021-09-22
 * @version     v1.0
 */
public class EncoderServer extends Server<EncoderServer>
{

    @Override
    public void initChannel(SocketChannel i_Channel ,ChannelPipeline i_Pipeline)
    {
        i_Pipeline.addLast(new MyDecoder());
        i_Pipeline.addLast(new EncoderServerHandler());
    }
    
    
    
    public static void main(String [] args)
    {
        new EncoderServer().setPort(2021).start();
    }
    
}
