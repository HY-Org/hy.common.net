package org.hy.common.net.netty.rpc.callable;

import org.hy.common.net.data.CommunicationRequest;
import org.hy.common.net.data.CommunicationResponse;
import org.hy.common.net.data.LoginRequest;
import org.hy.common.net.data.LoginResponse;





/**
 * 客户端的公开方法接口
 * 
 * @author      ZhengWei(HY)
 * @createDate  2021-09-28
 * @version     v1.0
 */
public interface ClientRPCOperation
{
    
    /**
     * 登录操作
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-09-28
     * @version     v1.0
     * 
     * @param i_Request  登录信息。必填参数[userName ,systemName]
     * @return
     */
    public LoginResponse login(LoginRequest i_Request);
    
    
    
    /**
     * 发送通讯数据
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-09-28
     * @version     v1.0
     * 
     * @param i_Request  通讯请求
     * @return
     */
    public CommunicationResponse send(CommunicationRequest i_Request);
    
}
