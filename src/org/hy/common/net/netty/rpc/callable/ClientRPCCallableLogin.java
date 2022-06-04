package org.hy.common.net.netty.rpc.callable;

import org.hy.common.net.data.LoginRequest;
import org.hy.common.net.data.LoginResponse;
import org.hy.common.net.data.protobuf.CommunicationProto.Data;
import org.hy.common.net.data.protobuf.CommunicationProtoDecoder;
import org.hy.common.net.netty.rpc.ClientRPC;





/**
 * 业务的处理器的方法调用处理：登录方法
 * 
 * @author      ZhengWei(HY)
 * @createDate  2021-09-28
 * @version     v1.0
 */
public class ClientRPCCallableLogin extends ClientRPCCallable<LoginRequest ,LoginResponse>
{
    
    public ClientRPCCallableLogin(ClientRPC i_ClientRPC ,LoginRequest i_Request)
    {
        super(i_ClientRPC ,i_Request);
    }

    
    
    @Override
    public LoginResponse decoder(Data i_Data)
    {
        return CommunicationProtoDecoder.toLoginResponse(i_Data.getLoginResponse());
    }
    
}
