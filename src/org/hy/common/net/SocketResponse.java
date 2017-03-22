package org.hy.common.net;





/**
 * Socket响应接口（客户端接收到服务端的响应消息时）（通用的）
 *
 * @author      ZhengWei(HY)
 * @createDate  2017-01-14
 * @version     v1.0
 */
public interface SocketResponse
{
    
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
    public Object response(Object i_RequestData ,Object i_ResponseData);
    
}
