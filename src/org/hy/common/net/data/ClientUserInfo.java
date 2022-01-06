package org.hy.common.net.data;

import org.hy.common.Date;





/**
 * RPC + Protobuf的客户端
 * 
 * @author      ZhengWei(HY)
 * @createDate  2021-09-27
 * @version     v1.0
 */
public class ClientUserInfo extends LoginRequest
{
    
    private static final long serialVersionUID = 9200380879322001538L;
    
    /** 是否在线 */
    private boolean isOnline;
    
    /** 登录时间 */
    private Date    loginTime;
    
    /** 退出时间（异常时间） */
    private Date    logoutTime;
    
    /** 最后一次空闲时间 */
    private Date    idleTime;
    
    /** 通讯的累计次数 */
    private long    requestCount;
    
    /** 最后一次有效通讯时间 */
    private Date    activeTime;
    
    /** 有效通讯的累计次数 */
    private long    activeCount;
    
    /** 有效通讯的累计时长（单位：毫秒） */
    private long    activeTimeLen;
    
    
    
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
     * 有效通讯的累计次数 ++
     * 
     * @return
     */
    public synchronized long addActiveCount()
    {
        return ++this.activeCount;
    }
    
    
    /**
    * 有效通讯的累计时长 ++
    * 
    * @return
    */
    public synchronized long addActiveTimeLen(long i_ActiveTimeLen)
    {
        return this.activeTimeLen += i_ActiveTimeLen;
    }
    
    
    /**
     * 通讯的累计次数 ++
     * 
     * @return
     */
    public synchronized long addRequestCount()
    {
        return ++this.requestCount;
    }


    /**
     * 获取：有效通讯的累计时长（单位：毫秒）
     */
    public long getActiveTimeLen()
    {
        return activeTimeLen;
    }


    /**
     * 设置：有效通讯的累计时长（单位：毫秒）
     * 
     * @param activeTimeLen
     */
    public void setActiveTimeLen(long activeTimeLen)
    {
        this.activeTimeLen = activeTimeLen;
    }


    /**
     * 获取：是否在线
     */
    public boolean isOnline()
    {
        return isOnline;
    }


    /**
     * 设置：是否在线
     * 
     * @param isOnline
     */
    public void setOnline(boolean isOnline)
    {
        this.isOnline = isOnline;
    }

    
    /**
     * 获取：通讯异常的累计次数
     */
    public long getRequestCount()
    {
        return requestCount;
    }


    /**
     * 获取：通讯异常的累计次数
     */
    public void setRequestCount(long requestCount)
    {
        this.requestCount = requestCount;
    }
    
    
    @Override
    public String toString()
    {
        return super.toString();
    }
    
}
