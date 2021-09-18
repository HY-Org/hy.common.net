package org.hy.common.net.junit.netty.protobuf2;

import org.hy.common.net.netty.Server;

import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;





/**
 * Protobuf服务端（多个数据通讯对象）
 * 
 * @author      ZhengWei(HY)
 * @createDate  2021-09-18
 * @version     v1.0
 */
public class ProtobufMoreObjectServer extends Server<ProtobufMoreObjectServer>
{

    /**
     * 初始通道
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-09-18
     * @version     v1.0
     */
    @Override
    public void initChannel(SocketChannel i_Channel)
    {
        ChannelPipeline v_Pipeline = i_Channel.pipeline();
        
        v_Pipeline.addLast(new StringEncoder(CharsetUtil.UTF_8));
        v_Pipeline.addLast(new ProtobufDecoder(PeoplePOJO.People.getDefaultInstance()));  // 指定对哪种类型解码
        v_Pipeline.addLast(new ProtobufMoreObjectServerInboundHandler());
    }
    
    
    
    public static void main(String [] args)
    {
        new ProtobufMoreObjectServer().setPort(2021).start();
    }
    
}
