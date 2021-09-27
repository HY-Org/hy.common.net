package org.hy.common.net.netty.rpc;

import org.hy.common.net.data.protobuf.CommunicationProto;
import org.hy.common.net.netty.Client;

import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;





/**
 * RPC + Protobuf的客户端。
 * 
 * 同时允许用户自定义部分的数据使用不同的数据格式协议（如 Json、Protobuf、编程语言的基本数据类型）
 * 
 * @author      ZhengWei(HY)
 * @createDate  2021-09-25
 * @version     v1.0
 */
public class ClientRPC extends Client<ClientRPC>
{

    @Override
    public void initChannel(SocketChannel i_Channel ,ChannelPipeline i_Pipeline)
    {
        i_Pipeline.addLast(new ProtobufEncoder());
        i_Pipeline.addLast(new ProtobufDecoder(CommunicationProto.Data.getDefaultInstance()));  // 指定对哪种类型解码
    }
    
}
