package org.hy.common.net.junit.netty.protobuf;

import org.hy.common.net.netty.Client;

import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.util.CharsetUtil;





/**
 * Protobuf客户端
 * 
 * @author      ZhengWei(HY)
 * @createDate  2021-09-17
 * @version     v1.0
 */
public class ProtobufClient extends Client<ProtobufClient>
{

    /**
     * 初始通道
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-09-17
     * @version     v1.0
     */
    @Override
    public void initChannel(SocketChannel i_Channel)
    {
        ChannelPipeline v_Pipeline = i_Channel.pipeline();
        
        v_Pipeline.addLast(new StringDecoder(CharsetUtil.UTF_8));
        v_Pipeline.addLast(new ProtobufEncoder());
        v_Pipeline.addLast(new ProtobufClientInboundHandler());
    }
    
    
    
    public static void main(String [] args)
    {
        ProtobufClient v_Client = new ProtobufClient().setHost("127.0.0.1").setPort(2021);
        
        v_Client.start();
    }
    
}
