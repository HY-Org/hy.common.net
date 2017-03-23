package org.hy.common.net;

import org.hy.common.net.data.LoginRequest;
import org.hy.common.net.data.LoginResponse;





/**
 * ServerSocket服务端的接收到的请求处理类。
 * 主要功能有：
 *   1. 登陆验证
 *   2. 创建新的临时的用于通讯的服务监听端口。
 *   3. 将创建通讯端口告知客户端。
 *
 * @author      ZhengWei(HY)
 * @createDate  2017-01-13
 * @version     v1.0
 */
public class ServerSocketListener extends ObjectSocketRequest
{
    
    /**
     * 接收到对象请求的实际处理动作
     * 
     * 此接口将被服务端以多线程的方式触发执行。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-01-13
     * @version     v1.0
     *
     * @param i_RequestData  通过Socket请求接收到的数据，它是一个Java对象
     * @param i_ServerBase   接收请求的服务端Socket服务
     * @return               发送给请求者（Socket客户端）的响应数据，也应是一个Java对象。
     */
    public Object request(Object i_RequestData ,ServerBase i_ServerBase)
    {
        if ( i_RequestData == null || !(i_RequestData instanceof LoginRequest) )
        {
            return null;
        }
        
        if ( i_ServerBase == null || !(i_ServerBase instanceof ServerSocket) )
        {
            return null;
        }
        
        // 登陆验证
        ServerSocket v_ServerSocket = (ServerSocket)i_ServerBase;
        if ( v_ServerSocket.getValidate() != null )
        {
            if ( !(i_RequestData instanceof LoginRequest) )
            {
                return null;
            }
            
            if ( !v_ServerSocket.getValidate().validate((LoginRequest)i_RequestData) )
            {
                v_ServerSocket.log("ServerLogin：Port " + i_ServerBase.port + " invalid login.");
                return null;
            }
        }
        
        // 创建一个新的用于数据通讯的临时的服务端Socket监听服务
        ServerBase v_CommunicationServer = v_ServerSocket.createCommunicationServer();
        if ( v_CommunicationServer == null )
        {
            return null;
        }
        
        LoginResponse v_ResponseData = new LoginResponse();
        v_ResponseData.setPort(v_CommunicationServer.getPort());
        
        return v_ResponseData;
    }
    
}
