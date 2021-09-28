package org.hy.common.net.netty.rpc.client;

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
    
    
    
    
}
