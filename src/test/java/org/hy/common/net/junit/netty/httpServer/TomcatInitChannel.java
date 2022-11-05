package org.hy.common.net.junit.netty.httpServer;

import org.hy.common.xml.log.Logger;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;





/**
 * 自定义一个通道初始化类
 * 
 * @author      ZhengWei(HY)
 * @createDate  2021-09-10
 * @version     v1.0
 */
public class TomcatInitChannel extends ChannelInitializer<SocketChannel>
{
    private static final Logger $Logger = new Logger(TomcatInitChannel.class ,true);
    
    

    @Override
    protected void initChannel(SocketChannel i_Channel) throws Exception
    {
        $Logger.info("客户标示：" + i_Channel.hashCode());
        
        ChannelPipeline v_Pipeline = i_Channel.pipeline();
        
        v_Pipeline.addLast("Http引擎"     ,new HttpServerCodec());     // HttpServerCodec 是Netty提供的Http协议的编码解码器
        v_Pipeline.addLast("Http请求处理" ,new TomcatInboundHandler());
    }
}
