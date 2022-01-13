package org.hy.common.net.data;

import java.io.Serializable;

import org.hy.common.Date;
import org.hy.common.Help;





/**
 * 通讯连接异常信息。
 * 
 * @author      ZhengWei(HY)
 * @createDate  2022-01-08
 * @version     v1.0
 */
public class NetException implements Serializable
{
    
    private static final long serialVersionUID = -838782530801305127L;

    /** 执行时间。一般执行完成时的时间，或出现异常时的时间 */
    private String  time;
    
    /** 执行异常信息 */
    private String  e;
    
    /** 数据通讯的事件类型。即通知哪一个事件监听者来处理数据通讯。默认为：XJava */
    private String  eventType;
    
    /** 数据的操作类型 */
    private String  dataOperation;
    
    /** 通讯的数据 */
    private Object  data;
    
    /** 数据的XID，即XJava的对象ID */
    private String  dataXID;
    
    /** 通讯结果 */
    private int     responseResult;
    
    
    
    public NetException(final CommunicationRequest i_Request ,final int i_ResponseResult ,final String i_Message ,final Throwable i_Throwable)
    {
        this.time = Date.getNowTime().getFullMilli();
        
        if ( i_Throwable != null )
        {
            if ( !Help.isNull(i_Message) )
            {
                this.e = i_Message + " -> " + i_Throwable.getLocalizedMessage();
            }
            else
            {
                this.e = i_Throwable.getLocalizedMessage();
            }
        }
        else
        {
            this.e = Help.NVL(i_Message);
        }
        
        this.responseResult = i_ResponseResult;
        
        if ( i_Request != null )
        {
            this.eventType     = i_Request.getEventType();
            this.dataOperation = i_Request.getDataOperation();
            this.data          = i_Request.getData();
            this.dataXID       = i_Request.getDataXID();
        }
    }
    
    
    
    /**
     * 获取：执行SQL语句
     */
    public String getTime()
    {
        return time;
    }

    
    /**
     * 设置：执行时间
     * 
     * @param time
     */
    public void setTime(String time)
    {
        this.time = time;
    }

    
    /**
     * 获取：执行SQL语句
     */
    public String getE()
    {
        return e;
    }

    
    /**
     * 设置：执行时间
     * 
     * @param time
     */
    public void setE(String e)
    {
        this.e = e;
    }


    /**
     * 获取：数据通讯的事件类型。即通知哪一个事件监听者来处理数据通讯。默认为：XJava
     */
    public String getEventType()
    {
        return eventType;
    }


    /**
     * 设置：数据通讯的事件类型。即通知哪一个事件监听者来处理数据通讯。默认为：XJava
     * 
     * @param eventType
     */
    public void setEventType(String eventType)
    {
        this.eventType = eventType;
    }


    /**
     * 获取：数据的操作类型
     */
    public String getDataOperation()
    {
        return dataOperation;
    }


    /**
     * 设置：数据的操作类型
     * 
     * @param dataOperation
     */
    public void setDataOperation(String dataOperation)
    {
        this.dataOperation = dataOperation;
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
     * 获取：通讯结果
     */
    public int getResponseResult()
    {
        return responseResult;
    }


    /**
     * 设置：通讯结果
     * 
     * @param responseResult
     */
    public void setResponseResult(int responseResult)
    {
        this.responseResult = responseResult;
    }
    
}
