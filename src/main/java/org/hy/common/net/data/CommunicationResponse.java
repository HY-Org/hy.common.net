package org.hy.common.net.data;

import org.hy.common.Date;





/**
 * 传输对象：用于通讯数据的响应
 *
 * @author      ZhengWei(HY)
 * @createDate  2017-01-14
 * @version     v1.0
 *              v2.0  2021-09-29  添加：链式编程
 */
public class CommunicationResponse extends Communication<CommunicationResponse> implements Comparable<CommunicationResponse>
{
    
    private static final long serialVersionUID = -1271619329628744231L;
    
    
    
    /** 通讯的结果类型(0：为成功； 其它为失败编码) */
    private int  result;
    
    /** 通讯结束时间 */
    private Date endTime;
    
    
    
    public CommunicationResponse()
    {
        super();
        
        this.result = 0;
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
    public CommunicationResponse setResult(int result)
    {
        this.result = result;
        return this;
    }


    
    /**
     * 获取：通讯开始时间
     */
    public Date getStartTime()
    {
        return this.time;
    }


    
    /**
     * 设置：通讯开始时间
     * 
     * @param startTime
     */
    public CommunicationResponse setStartTime(Date startTime)
    {
        this.time = startTime;
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
    public CommunicationResponse setEndTime(Date endTime)
    {
        this.endTime = endTime;
        return this;
    }



    @Override
    public int compareTo(CommunicationResponse i_Other)
    {
        if ( null == i_Other )
        {
            return 1;
        }
        else if ( this == i_Other )
        {
            return 0;
        }
        else if ( this.endTime != null && i_Other.endTime != null  )
        {
            return this.endTime.compareTo(i_Other.endTime);
        }
        else if ( this.endTime != null )
        {
            return 1;
        }
        else if ( i_Other.endTime != null )
        {
            return -1;
        }
        else
        {
            return 0;
        }
    }
    
    
    
    @Override
    public String toString()
    {
        return this.result + "";
    }
    
}
