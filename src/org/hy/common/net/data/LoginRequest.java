package org.hy.common.net.data;

import org.hy.common.xml.SerializableDef;





/**
 * 传输对象：用于登陆验证的请求
 *
 * @author      ZhengWei(HY)
 * @createDate  2017-01-13
 * @version     v1.0
 *              v2.0  2021-09-25  添加：通讯的接口版本
 *                                添加：IP
 *                                添加：端口
 *                                添加：用户编号
 *                                添加：客户端的系统名称
 */
public class LoginRequest extends SerializableDef
{
    
    private static final long serialVersionUID = 5568766404534113410L;
    
    
    /** 通讯的接口版本 */
    protected int   version;
    
    /** 用户编号 */
    private String  id;
    
    /** 用户名 */
    private String  userName;
    
    /** 密码 */
    private String  password;
    
    /** 客户端的系统名称 */
    private String  systemName;
    
    /** 远程地址 */
    private String  remoteAddress;
    
    /** IP */
    private String  host;
    
    /** 端口 */
    private int     port;
    
    
    
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
    
    
    /**
     * 获取：通讯的接口版本
     */
    public int getVersion()
    {
        return version;
    }


    /**
     * 设置：通讯的接口版本
     * 
     * @param version
     */
    public void setVersion(int version)
    {
        this.version = version;
    }


    /**
     * 获取：IP
     */
    public String getHost()
    {
        return host;
    }


    /**
     * 设置：IP
     * 
     * @param host
     */
    public void setHost(String host)
    {
        this.host = host;
    }


    /**
     * 获取：端口
     */
    public int getPort()
    {
        return port;
    }


    /**
     * 设置：端口
     * 
     * @param port
     */
    public void setPort(int port)
    {
        this.port = port;
    }
    
    
    /**
     * 获取：用户编号
     */
    public String getId()
    {
        return id;
    }

    
    /**
     * 设置：用户编号
     * 
     * @param id
     */
    public void setId(String id)
    {
        this.id = id;
    }


    /**
     * 获取：客户端的系统名称
     */
    public String getSystemName()
    {
        return systemName;
    }


    /**
     * 设置：客户端的系统名称
     * 
     * @param id
     */
    public void setSystemName(String systemName)
    {
        this.systemName = systemName;
    }


    /**
     * 获取：远程地址
     */
    public String getRemoteAddress()
    {
        return remoteAddress;
    }


    /**
     * 设置：远程地址
     * 
     * @param remoteAddress
     */
    public void setRemoteAddress(String remoteAddress)
    {
        this.remoteAddress = remoteAddress;
    }



    @Override
    public String toString()
    {
        return this.userName + ":" + this.systemName + ":" + this.remoteAddress;
    }
    
}
