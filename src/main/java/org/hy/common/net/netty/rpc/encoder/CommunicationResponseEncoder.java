package org.hy.common.net.netty.rpc.encoder;

import java.util.List;

import org.hy.common.net.data.CommunicationResponse;
import org.hy.common.net.data.protobuf.CommunicationProto.Data;
import org.hy.common.net.data.protobuf.CommunicationProto.Data.DataType;
import org.hy.common.net.data.protobuf.CommunicationProtoEncoder;
import org.hy.common.xml.log.Logger;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;





/**
 * 数据通讯类的编码器
 * 
 * 将 CommunicationResponse 转为 CommunicationProto
 * 
 * @author      ZhengWei(HY)
 * @createDate  2021-09-29
 * @version     v1.0
 */
public class CommunicationResponseEncoder extends MessageToMessageEncoder<CommunicationResponse>
{
    private static final Logger $Logger = new Logger(CommunicationResponseEncoder.class ,true);
    
    

    @Override
    protected void encode(ChannelHandlerContext i_Ctx ,CommunicationResponse i_Msg ,List<Object> io_Out) throws Exception
    {
        try
        {
            Data.Builder v_DataBuilder = Data.newBuilder();
            
            v_DataBuilder.setDataType(DataType.Response);
            v_DataBuilder.setResponse(CommunicationProtoEncoder.toResponse(i_Msg).build());
            
            io_Out.add(v_DataBuilder.build());
        }
        catch (Exception exce)
        {
            $Logger.error(exce);
            throw exce;
        }
    }
    
}
