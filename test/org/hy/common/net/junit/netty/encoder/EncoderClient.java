package org.hy.common.net.junit.netty.encoder;

import org.hy.common.net.netty.Client;

import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;





/**
 * 编解码器的客户端
 * 
 * @author      ZhengWei(HY)
 * @createDate  2021-09-22
 * @version     v1.0
 */
public class EncoderClient extends Client<EncoderClient>
{

    @Override
    public void initChannel(SocketChannel i_Channel)
    {
        ChannelPipeline v_Pipeline = i_Channel.pipeline();
        
        v_Pipeline.addLast(new MyEncoder());
        v_Pipeline.addLast(new EncoderClientHandler());
    }
    
    
    
    public static void main(String [] args)
    {
        new EncoderClient().setHost("127.0.0.1").setPort(2021).start();
    }
    
}
