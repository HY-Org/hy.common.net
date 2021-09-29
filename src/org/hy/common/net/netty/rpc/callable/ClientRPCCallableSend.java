package org.hy.common.net.netty.rpc.callable;

import org.hy.common.net.data.CommunicationRequest;
import org.hy.common.net.data.CommunicationResponse;
import org.hy.common.net.data.protobuf.CommunicationProto.Data;
import org.hy.common.net.data.protobuf.CommunicationProtoDecoder;
import org.hy.common.net.netty.rpc.ClientRPCHandler;





/**
 * 业务的处理器的方法调用处理：数据通讯
 * 
 * @author      ZhengWei(HY)
 * @createDate  2021-09-29
 * @version     v1.0
 */
public class ClientRPCCallableSend extends ClientRPCCallable<CommunicationRequest ,CommunicationResponse>
{
    
    public ClientRPCCallableSend(ClientRPCHandler i_ClientRPCHandler ,CommunicationRequest i_Request)
    {
        super(i_ClientRPCHandler ,i_Request);
    }

    
    
    @Override
    public CommunicationResponse decoder(Data i_Data)
    {
        return CommunicationProtoDecoder.toResponse(i_Data.getResponse());
    }
    
}
