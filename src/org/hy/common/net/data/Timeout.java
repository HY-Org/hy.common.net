package org.hy.common.net.data;

import org.hy.common.xml.SerializableDef;





/**
 * 超时对象。每次通讯均可独立设置超时时长
 *
 * @author      ZhengWei(HY)
 * @createDate  2017-01-13
 * @version     v1.0
 */
public class Timeout<T extends Timeout<T>> extends SerializableDef
{

    private static final long serialVersionUID = 7855918278175306829L;
    
    public  static       long $Default_WaitRequestTimeout = 30 * 1000;
    
    /** 请求等待超时（单位：毫秒） */
    private Long waitRequestTimeout;

    
    
    /**
     * 获取：请求等待超时
     */
    public Long getWaitRequestTimeout()
    {
        return waitRequestTimeout;
    }

    
    
    /**
     * 设置：请求等待超时
     * 
     * @param waitRequestTimeout
     */
    @SuppressWarnings("unchecked")
    public T setWaitRequestTimeout(Long waitRequestTimeout)
    {
        this.waitRequestTimeout = waitRequestTimeout;
        return (T) this;
    }
    
}
