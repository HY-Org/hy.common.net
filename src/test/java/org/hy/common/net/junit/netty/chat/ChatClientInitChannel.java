package org.hy.common.net.junit.netty.chat;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;





/**
 * 自定义一个通道初始化类
 * 
 * @author      ZhengWei(HY)
 * @createDate  2021-09-13
 * @version     v1.0
 */
public class ChatClientInitChannel extends ChannelInitializer<SocketChannel>
{
    
    @Override
    protected void initChannel(SocketChannel i_Channel) throws Exception
    {
        ChannelPipeline v_Pipeline = i_Channel.pipeline();
        
        v_Pipeline.addLast("字符解码器" ,new StringDecoder());
        v_Pipeline.addLast("字符编码器" ,new StringEncoder());
        v_Pipeline.addLast("业务的处理" ,new ChatClientInboundHandler());
    }
    
}
