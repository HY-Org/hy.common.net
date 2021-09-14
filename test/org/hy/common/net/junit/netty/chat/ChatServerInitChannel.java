package org.hy.common.net.junit.netty.chat;

import java.util.concurrent.TimeUnit;

import org.hy.common.xml.log.Logger;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;





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
        
        /**
         * readerIdleTime：表示多长时间没有读
         * writerIdleTime：表示多长时间没有写
         * allIdleTime：   表示多长时间没有读写
         * 
         * 将会触发一个IdleStateEvent的事件，并且事件会传递到下一个处理器来处理，
         * 通过触发下一个Handler的userEventTrigged方法
         */
        v_ChannelPipeline.addLast("心动测试器" ,new IdleStateHandler(5 ,9 ,13 ,TimeUnit.SECONDS));
        v_ChannelPipeline.addLast("业务的处理" ,new ChatServerInboundHandler_V1());
    }
    
}
