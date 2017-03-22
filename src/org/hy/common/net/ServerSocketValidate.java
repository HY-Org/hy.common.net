package org.hy.common.net;

import org.hy.common.net.data.LoginRequest;





/**
 * 服务端的登陆验证接口 
 *
 * @author      ZhengWei(HY)
 * @createDate  2017-01-15
 * @version     v1.0
 */
public interface ServerSocketValidate
{
    
    /**
     * 服务端的登陆验证方法
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-01-15
     * @version     v1.0
     *
     * @param i_LoginRequest  登陆信息
     * @return                验证成功时，返回true
     */
    public boolean validate(LoginRequest i_LoginRequest);
    
}
