package org.hy.common.net.junit.netty.protobuf2;

import org.hy.common.net.netty.Client;

import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.util.CharsetUtil;





/**
 * Protobuf客户端（多个数据通讯对象）
 * 
 * @author      ZhengWei(HY)
 * @createDate  2021-09-18
 * @version     v1.0
 */
public class ProtobufMoreObjectClient extends Client<ProtobufMoreObjectClient>
{

    /**
     * 初始通道
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-09-18
     * @version     v1.0
     */
    @Override
    public void initChannel(SocketChannel i_Channel ,ChannelPipeline i_Pipeline)
    {
        i_Pipeline.addLast(new StringDecoder(CharsetUtil.UTF_8));
        i_Pipeline.addLast(new ProtobufEncoder());
        i_Pipeline.addLast(new ProtobufMoreObjectClientInboundHandler());
    }
    
    
    
    public static void main(String [] args)
    {
        ProtobufMoreObjectClient v_Client = new ProtobufMoreObjectClient().setHost("127.0.0.1").setPort(2021);
        
        v_Client.start();
    }
    
}
