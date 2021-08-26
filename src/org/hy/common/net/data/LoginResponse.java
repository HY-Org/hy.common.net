package org.hy.common.net.data;

import org.hy.common.xml.SerializableDef;





/**
 * 传输对象：用于登陆验证的响应
 *
 * @author      ZhengWei(HY)
 * @createDate  2017-01-13
 * @version     v1.0
 */
public class LoginResponse extends SerializableDef
{
    
    private static final long serialVersionUID = -3420435311941320659L;
    
    
    /** 通讯票据 */
    private String token;
    
    /** 通讯端口号 */
    private int    port;
    
    
    
    /**
     * 获取：通讯端口号
     */
    public int getPort()
    {
        return port;
    }

    
    
    /**
     * 设置：通讯端口号
     * 
     * @param port
     */
    public void setPort(int port)
    {
        this.port = port;
    }



    /**
     * 设置：通讯票据
     * 
     * @param port
     */
    public String getToken()
    {
        return token;
    }



    /**
     * 获取：通讯票据
     */
    public void setToken(String token)
    {
        this.token = token;
    }
    
}
