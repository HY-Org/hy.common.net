package org.hy.common.net.data;

import org.hy.common.Date;





/**
 * RPC + Protobuf的客户端
 * 
 * @author      ZhengWei(HY)
 * @createDate  2021-09-27
 * @version     v1.0
 */
public class ClientUserInfo
{
    
    /** 用户编号(系统每次随机分配) */
    private int    id;
    
    /** 用户名称 */
    private String userName;
    
    /** 客户端的IP */
    private String host;
    
    /** 登录时间 */
    private Date   loginTime;
    
    /** 退出时间（异常时间） */
    private Date   logoutTime;
    
    /** 最后一次空闲时间 */
    private Date   idleTime;
    
    /** 最后一次有效通讯时间 */
    private Date   activeTime;
    
    /** 有效通讯的累计次数 */
    private long   activeCount;
    
    /** 有效通讯的累计时长 */
    private long   activeTimeLen;

    
    
    /**
     * 获取：用户编号(系统每次随机分配)
     */
    public int getId()
    {
        return id;
    }

    
    /**
     * 设置：用户编号(系统每次随机分配)
     * 
     * @param id
     */
    public void setId(int id)
    {
        this.id = id;
    }

    
    /**
     * 获取：用户名称
     */
    public String getUserName()
    {
        return userName;
    }

    
    /**
     * 设置：用户名称
     * 
     * @param userName
     */
    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    
    /**
     * 获取：客户端的IP
     */
    public String getHost()
    {
        return host;
    }

    
    /**
     * 设置：客户端的IP
     * 
     * @param host
     */
    public void setHost(String host)
    {
        this.host = host;
    }

    
    /**
     * 获取：登录时间
     */
    public Date getLoginTime()
    {
        return loginTime;
    }

    
    /**
     * 设置：登录时间
     * 
     * @param loginTime
     */
    public void setLoginTime(Date loginTime)
    {
        this.loginTime = loginTime;
    }

    
    /**
     * 获取：最后一次空闲时间
     */
    public Date getIdleTime()
    {
        return idleTime;
    }

    
    /**
     * 设置：最后一次空闲时间
     * 
     * @param idleTime
     */
    public void setIdleTime(Date idleTime)
    {
        this.idleTime = idleTime;
    }

    
    /**
     * 获取：最后一次有效通讯时间
     */
    public Date getActiveTime()
    {
        return activeTime;
    }

    
    /**
     * 设置：最后一次有效通讯时间
     * 
     * @param activeTime
     */
    public void setActiveTime(Date activeTime)
    {
        this.activeTime = activeTime;
    }

    
    /**
     * 获取：退出时间（异常时间）
     */
    public Date getLogoutTime()
    {
        return logoutTime;
    }

    
    /**
     * 设置：退出时间（异常时间）
     * 
     * @param logoutTime
     */
    public void setLogoutTime(Date logoutTime)
    {
        this.logoutTime = logoutTime;
    }


    /**
     * 获取：有效通讯的累计次数
     */
    public long getActiveCount()
    {
        return activeCount;
    }


    /**
     * 设置：有效通讯的累计次数
     * 
     * @param activeCount
     */
    public void setActiveCount(long activeCount)
    {
        this.activeCount = activeCount;
    }


    /**
     * 获取：有效通讯的累计时长
     */
    public long getActiveTimeLen()
    {
        return activeTimeLen;
    }


    /**
     * 设置：有效通讯的累计时长
     * 
     * @param activeTimeLen
     */
    public void setActiveTimeLen(long activeTimeLen)
    {
        this.activeTimeLen = activeTimeLen;
    }
    
}
