package org.hy.common.net.socket;

import org.hy.common.net.common.NetError;
import org.hy.common.net.data.CommunicationResponse;





/**
 * 用于真正数据通讯的Socket客户端
 *
 * @author      ZhengWei(HY)
 * @createDate  2017-01-14
 * @version     v1.0
 */
public class ClientCommunication extends ObjectSocketResponse<ClientCommunication>
{
    
    public ClientCommunication(String i_HostName ,int i_Port)
    {
        super(i_HostName ,i_Port);
    }
    
    
    
    /**
     * 客户端接收到服务端的响应消息时执行的动作。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-01-14
     * @version     v1.0
     *
     * @param i_RequestData   将服务端发送的对象
     * @param i_ResponseData  接收服务端响应的对象
     * @return                实际处理类返回的对象
     */
    @Override
    public Object response(Object i_RequestData ,Object i_ResponseData)
    {
        if ( !(i_ResponseData instanceof CommunicationResponse) )
        {
            CommunicationResponse v_Communication = new CommunicationResponse();
            v_Communication.setResult(NetError.$Server_ResponseDataError);
            return v_Communication;
        }
        
        return i_ResponseData;
    }
    
    
    
    /**
     * 向服务端发送数据
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-01-14
     * @version     v1.0
     *
     * @param i_RequestData  请求发送给服务端的数据
     * @return               服务端返回的响应数据
     */
    @Override
    public Object send(Object i_RequestData)
    {
        return super.send(i_RequestData);
    }
    
}
