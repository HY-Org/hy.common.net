package org.hy.common.net;

import org.hy.common.Help;
import org.hy.common.net.data.CommunicationRequest;
import org.hy.common.net.data.CommunicationResponse;





/**
 * ServerSocket服务端的接收到请求后，创建一个新的用于数据通讯的临时的服务端Socket监听服务。
 * 此类为临时服务端Socket接收到的请求的处理类。
 *
 * @author      ZhengWei(HY)
 * @createDate  2017-01-14
 * @version     v1.0
 */
public class ServerSocketCommunication extends ObjectSocketRequest
{
    
    /** 主服务端口 */
    private ServerSocket mainServer;
    
    
    
    public ServerSocketCommunication(ServerSocket i_ServerSocket)
    {
        this.mainServer = i_ServerSocket;
    }
    
    
    
    /**
     * 接收到对象请求的实际处理动作
     * 
     * 此接口将被服务端以多线程的方式触发执行。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-01-14
     * @version     v1.0
     *
     * @param i_RequestData  通过Socket请求接收到的数据，它是一个Java对象
     * @param i_ServerBase   接收请求的服务端Socket服务
     * @return               发送给请求者（Socket客户端）的响应数据，也应是一个Java对象。
     */
    public Object request(Object i_RequestData ,ServerBase i_ServerBase)
    {
        try
        {
            if ( i_RequestData == null || !(i_RequestData instanceof CommunicationRequest) )
            {
                return null;
            }
            
            if ( i_ServerBase == null )
            {
                return null;
            }
            
            CommunicationRequest  v_RequestData  = (CommunicationRequest)i_RequestData;
            CommunicationResponse v_ResponseData = null;
            CommunicationListener v_Listener     = null;
            
            if ( Help.isNull(v_RequestData.getEventType()) )
            {
                v_Listener = this.mainServer.getDefaultListener();
            }
            else
            {
                v_Listener = this.mainServer.getListeners(v_RequestData.getEventType());
                
                if ( v_Listener == null )
                {
                    v_Listener = this.mainServer.getDefaultListener();
                }
            }
            
            try
            {
                this.mainServer.log("ServerCommunication：Port " + i_ServerBase.port + " Event action is " + v_Listener.getEventType() + ".");
                v_ResponseData = v_Listener.communication(v_RequestData);
            }
            catch (Exception exce)
            {
                exce.printStackTrace();
            }
            
            return v_ResponseData;
        }
        finally
        {
            // 关闭监听的用于数据通讯的监听端口服务
            if ( i_ServerBase != null )
            {
                i_ServerBase.close();
            }
        }
    }
    
}
