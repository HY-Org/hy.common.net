package org.hy.common.net.data;

import org.hy.common.xml.SerializableDef;





/**
 * 传输对象：用于登陆验证的请求
 *
 * @author      ZhengWei(HY)
 * @createDate  2017-01-13
 * @version     v1.0
 */
public class LoginRequest extends SerializableDef
{
    
    private static final long serialVersionUID = 5568766404534113410L;
    
    

    /** 用户名 */
    private String userName;
    
    /** 密码 */
    private String password;
    
    
    
    public LoginRequest()
    {
        this(null ,null);
    }
    
    
    
    public LoginRequest(String i_UserName ,String i_Password)
    {
        this.userName = i_UserName;
        this.password = i_Password;
    }

    
    
    /**
     * 获取：用户名
     */
    public String getUserName()
    {
        return userName;
    }

    
    /**
     * 设置：用户名
     * 
     * @param userName 
     */
    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    
    /**
     * 获取：密码
     */
    public String getPassword()
    {
        return password;
    }

    
    /**
     * 设置：密码
     * 
     * @param password 
     */
    public void setPassword(String password)
    {
        this.password = password;
    }
    
}
