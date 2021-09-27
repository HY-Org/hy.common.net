package org.hy.common.net.data;

import org.hy.common.Date;
import org.hy.common.xml.SerializableDef;





/**
 * 传输对象：用于通讯数据的基础类
 *
 * @author      ZhengWei(HY)
 * @createDate  2017-01-14
 * @version     v1.0
 *              v2.0  2021-09-25  添加：通讯的接口版本
 */
public class Communication extends SerializableDef
{
    
    private static final long serialVersionUID = 7513185667760947675L;
    
    
    /** 通讯的接口版本 */
    protected int     version;
    
    /** 会话时间(由外界定义及使用，默认为NULL) */
    protected Date    sessionTime;
    
    /** 通讯的时间(内部机制设置此时间，外界无须干预) */
    protected Date    time;
    
    /** 通讯票据 */
    protected String  token;
    
    /** 通讯的数据 */
    protected Object  data;
    
    /** 数据的XID，即XJava的对象ID */
    protected String  dataXID;
    
    /** 是否每次通过 XJava.getObject(id) 获取一个全新的对象实例。默认构造出的对象为"单例" */
    protected boolean dataXIsNew;
    
    /** 数据的过期时长(单位：秒)。小于等于0或为空，表示永远有效 */
    protected long    dataExpireTimeLen;
    
    
    
    public Communication()
    {
        this.time        = new Date();
        this.sessionTime = null;
        this.dataXIsNew  = false;
    }
    
    
    /**
     * 获取：通讯的数据
     */
    public Object getData()
    {
        return data;
    }

    
    
    /**
     * 设置：通讯的数据
     * 
     * @param data
     */
    public void setData(Object data)
    {
        this.data = data;
    }
    

    
    /**
     * 获取：数据的XID，即XJava的对象ID
     */
    public String getDataXID()
    {
        return dataXID;
    }

    
    
    /**
     * 设置：数据的XID，即XJava的对象ID
     * 
     * @param dataXID
     */
    public void setDataXID(String dataXID)
    {
        this.dataXID = dataXID;
    }


    
    /**
     * 获取：是否每次通过 XJava.getObject(id) 获取一个全新的对象实例。默认构造出的对象为"单例"
     */
    public boolean getDataXIsNew()
    {
        return dataXIsNew;
    }


    
    /**
     * 设置：是否每次通过 XJava.getObject(id) 获取一个全新的对象实例。默认构造出的对象为"单例"
     * 
     * @param dataXIsNew
     */
    public void setDataXIsNew(boolean dataXIsNew)
    {
        this.dataXIsNew = dataXIsNew;
    }
    
    
    
    /**
     * 获取：数据的过期时长(单位：秒)
     */
    public long getDataExpireTimeLen()
    {
        return dataExpireTimeLen;
    }

    
    
    /**
     * 设置：数据的过期时长(单位：秒)
     * 
     * @param dataExpireTimeLen
     */
    public void setDataExpireTimeLen(long dataExpireTimeLen)
    {
        this.dataExpireTimeLen = dataExpireTimeLen;
    }


    
    /**
     * 获取：通讯的时间(内部机制设置此时间，外界无须干预)
     */
    public Date getTime()
    {
        return time;
    }


    
    /**
     * 设置：通讯的时间(内部机制设置此时间，外界无须干预)
     * 
     * @param time
     */
    public void setTime(Date time)
    {
        this.time = time;
    }
    

    
    /**
     * 获取：会话时间(由外界定义及使用，默认为NULL)
     */
    public Date getSessionTime()
    {
        return sessionTime;
    }


    
    /**
     * 设置：会话时间(由外界定义及使用，默认为NULL)
     * 
     * @param sessionTime
     */
    public void setSessionTime(Date sessionTime)
    {
        this.sessionTime = sessionTime;
    }

    

    /**
     * 获取：通讯票据
     */
    public String getToken()
    {
        return token;
    }

    

    /**
     * 设置：通讯票据
     * 
     * @param sessionTime
     */
    public void setToken(String token)
    {
        this.token = token;
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


    
    @Override
    public String toString()
    {
        if ( this.data != null )
        {
            if ( this.sessionTime != null )
            {
                return this.sessionTime.getFull() + ": " + this.data.toString();
            }
            else
            {
                return this.data.toString();
            }
        }
        else
        {
            return "";
        }
    }
    
}
