package org.hy.common.net.netty.rpc.encoder;

import java.util.List;

import org.hy.common.net.data.CommunicationRequest;
import org.hy.common.net.data.protobuf.CommunicationProto.Data;
import org.hy.common.net.data.protobuf.CommunicationProto.Data.DataType;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;





/**
 * 数据通讯类的编码器
 * 
 * 将 CommunicationRequest 转为 CommunicationProto
 * 
 * @author      ZhengWei(HY)
 * @createDate  2021-09-29
 * @version     v1.0
 */
public class CommunicationRequestEncoder extends MessageToMessageEncoder<CommunicationRequest>
{

    @Override
    protected void encode(ChannelHandlerContext i_Ctx ,CommunicationRequest i_Msg ,List<Object> io_Out) throws Exception
    {
        Data.Builder v_DataBuilder = Data.newBuilder();
        
        v_DataBuilder.setDataType(DataType.Request);
        // v_DataBuilder.setRequest(ObjectToProto.toRequest(i_Msg).build());
        
        io_Out.add(v_DataBuilder.build());
    }
    
}
