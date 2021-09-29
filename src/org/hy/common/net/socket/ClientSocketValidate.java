package org.hy.common.net.socket;

import org.hy.common.net.data.LoginRequest;





/**
 * 客户端登陆服务端的登陆信息接口 
 *
 * @author      ZhengWei(HY)
 * @createDate  2017-01-15
 * @version     v1.0
 */
public interface ClientSocketValidate
{
    
    /**
     * 客户端的获取登陆信息
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-01-15
     * @version     v1.0
     *
     * @return
     */
    public LoginRequest getLoginRequest();
    
}
