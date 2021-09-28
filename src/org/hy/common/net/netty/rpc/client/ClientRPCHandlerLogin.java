package org.hy.common.net.netty.rpc.client;

import java.util.concurrent.Callable;

import org.hy.common.net.data.LoginRequest;
import org.hy.common.net.data.LoginResponse;
import org.hy.common.net.data.protobuf.CommunicationProto;
import org.hy.common.net.data.protobuf.CommunicationProto.Data;
import org.hy.common.net.data.protobuf.CommunicationProto.Data.DataType;
import org.hy.common.net.data.protobuf.ObjectToProto;
import org.hy.common.net.data.protobuf.ProtoToObject;
import org.hy.common.net.netty.rpc.ClientRPCHandler;





/**
 * 业务的处理器的方法调用处理：登录方法
 * 
 * @author      ZhengWei(HY)
 * @createDate  2021-09-28
 * @version     v1.0
 */
public class ClientRPCHandlerLogin implements Callable<LoginResponse>
{
    
    /** 所属的业务处理 */
    private ClientRPCHandler myHandler;
    
    /** 请求参数 */
    private LoginRequest     paramObject;
    
    
    
    public ClientRPCHandlerLogin(ClientRPCHandler i_ClientRPCHandler ,LoginRequest i_LoginRequest)
    {
        this.myHandler   = i_ClientRPCHandler;
        this.paramObject = i_LoginRequest;
    }
    
    

    /**
     * 被代理对象调用，发送数据给服务端，并等待被唤醒（因为服务器的返回不是直接给的call方法，而是给了channelRead0方法）
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-09-28
     * @version     v1.0
     */
    @Override
    public LoginResponse call() throws Exception
    {
        CommunicationProto.LoginRequest.Builder v_RequestBuilder = ObjectToProto.toLoginRequest(this.paramObject);
        CommunicationProto.Data.Builder         v_DataBuilder    = CommunicationProto.Data.newBuilder();
        
        v_DataBuilder.setDataType(DataType.LoginRequest);
        v_DataBuilder.setLoginRequest(v_RequestBuilder.build());
        
        Data v_RetData = this.myHandler.send(v_DataBuilder.build());
        
        // 登录响应
        if ( v_RetData.getDataType() == DataType.LoginResponse )
        {
            return ProtoToObject.toLoginResponse(v_RetData.getLoginResponse());
        }
        else
        {
            return null;
        }
    }
    
}
