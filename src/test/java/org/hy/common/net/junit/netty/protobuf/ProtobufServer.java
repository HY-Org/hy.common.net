package org.hy.common.net.junit.netty.protobuf;

import org.hy.common.net.netty.Server;

import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;





/**
 * Protobuf服务端
 * 
 * @author      ZhengWei(HY)
 * @createDate  2021-09-15
 * @version     v1.0
 */
public class ProtobufServer extends Server<ProtobufServer>
{

    /**
     * 初始通道
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-09-15
     * @version     v1.0
     */
    @Override
    public void initChannel(SocketChannel i_Channel ,ChannelPipeline i_Pipeline)
    {
        i_Pipeline.addLast(new StringEncoder(CharsetUtil.UTF_8));
        i_Pipeline.addLast(new ProtobufDecoder(DataPOJO.Data.getDefaultInstance()));  // 指定对哪种类型解码
        i_Pipeline.addLast(new ProtobufServerInboundHandler());
    }
    
    
    
    public static void main(String [] args)
    {
        new ProtobufServer().setPort(2021).start();
    }
    
}
