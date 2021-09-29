package org.hy.common.net.data;

import org.hy.common.Date;
import org.hy.common.xml.SerializableDef;





/**
 * 传输对象：用于登陆验证的响应
 *
 * @author      ZhengWei(HY)
 * @createDate  2017-01-13
 * @version     v1.0
 *              v2.0  2021-09-25  添加：通讯的接口版本
 *                                添加：通讯主机
 *              v3.0  2021-09-29  添加：链式编程
 */
public class LoginResponse extends SerializableDef
{
    
    private static final long serialVersionUID = -3420435311941320659L;
    
    
    /** 通讯的接口版本 */
    protected int  version;
    
    /** 通讯票据 */
    private String token;
    
    /** 通讯主机 */
    private String host;
    
    /** 通讯端口号 */
    private int    port;
    
    /** 通讯的结果类型(0：为成功； 其它为失败编码) */
    private int    result;
    
    /** 通讯结束时间 */
    private Date   endTime;
    
    
    
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
    public LoginResponse setPort(int port)
    {
        this.port = port;
        return this;
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
    public LoginResponse setToken(String token)
    {
        this.token = token;
        return this;
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
    public LoginResponse setVersion(int version)
    {
        this.version = version;
        return this;
    }



    /**
     * 获取：通讯主机
     */
    public String getHost()
    {
        return host;
    }



    /**
     * 设置：通讯主机
     * 
     * @param host
     */
    public LoginResponse setHost(String host)
    {
        this.host = host;
        return this;
    }
    
    
    
    /**
     * 获取：通讯结束时间
     */
    public Date getEndTime()
    {
        return endTime;
    }


    
    /**
     * 设置：通讯结束时间
     * 
     * @param endTime
     */
    public LoginResponse setEndTime(Date endTime)
    {
        this.endTime = endTime;
        return this;
    }
    
    
    
    /**
     * 获取：通讯的结果类型(0：为成功； 其它为失败编码)
     */
    public int getResult()
    {
        return result;
    }


    
    /**
     * 设置：通讯的结果类型(0：为成功； 其它为失败编码)
     * 
     * @param result
     */
    public LoginResponse setResult(int result)
    {
        this.result = result;
        return this;
    }



    @Override
    public String toString()
    {
        return this.result + "";
    }
    
}
