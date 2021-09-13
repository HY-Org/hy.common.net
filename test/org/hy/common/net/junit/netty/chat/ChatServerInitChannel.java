package org.hy.common.net.junit.netty.chat;

import org.hy.common.xml.log.Logger;

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
public class ChatServerInitChannel extends ChannelInitializer<SocketChannel>
{

    private static final Logger $Logger = new Logger(ChatServerInitChannel.class ,true);
    
    
    
    @Override
    protected void initChannel(SocketChannel i_Channel) throws Exception
    {
        $Logger.info("客户标示：" + i_Channel.hashCode());
        
        ChannelPipeline v_ChannelPipeline = i_Channel.pipeline();
        
        v_ChannelPipeline.addLast("字符解码器" ,new StringDecoder());
        v_ChannelPipeline.addLast("字符编码器" ,new StringEncoder());
        v_ChannelPipeline.addLast("服务的处理" ,new ChatServerInboundHandler());
    }
}
