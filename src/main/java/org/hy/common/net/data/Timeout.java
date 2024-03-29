package org.hy.common.net.data;

import org.hy.common.xml.SerializableDef;





/**
 * 超时对象。每次通讯均可独立设置超时时长
 * 
 *   超时时长有三个级别，优先级从高到低依次为
 * 
 *     最高级（通讯级）：通讯数据的超时时长，取 Timeout 类的 xxxTimeout 属性
 * 
 *     中等级（应用级）：客户端上配置的超时时长，取 App 类的 timeout 属性
 *                     当最高级未配置时，本级生效。
 * 
 *     最低级（默认级）：当上两级均未配置时，本级生效，取 Timeout 类的可变常量值 $Default_xxx
 * 
 * 
 *   超时时长的取值规则：
 *     0表示永不超时，一直等待
 *     负数或NULL：表示取默认超时时长
 *
 * @author      ZhengWei(HY)
 * @createDate  2021-12-18
 * @version     v1.0
 */
public class Timeout<T extends Timeout<T>> extends SerializableDef
{

    private static final long serialVersionUID            = 7855918278175306829L;
    
    /** 可变常量值，不设置 final */
    public  static       long $Default_WaitRequestTimeout = 30L * 1000L;
    
    
    
    /** 请求等待超时（单位：毫秒）。0表示永不超时，一直等待； 负数或NULL：表示取默认超时时长 */
    private Long waitRequestTimeout;
    
    
    
    /**
     * 浅构建
     * 
     * @author      ZhengWei(HY)
     * @createDate  2022-06-12
     * @version     v1.0
     * 
     * @return
     */
    public T build(T io_New)
    {
        io_New.setWaitRequestTimeout(this.getWaitRequestTimeout());
        return io_New;
    }

    
    
    /**
     * 获取：请求等待超时。。0表示永不超时，一直等待； 负数或NULL：表示取默认超时时长
     */
    public Long getWaitRequestTimeout()
    {
        return waitRequestTimeout;
    }

    
    
    /**
     * 设置：请求等待超时。。0表示永不超时，一直等待； 负数或NULL：表示取默认超时时长
     * 
     * @param i_WaitRequestTimeout
     */
    @SuppressWarnings("unchecked")
    public T setWaitRequestTimeout(Long i_WaitRequestTimeout)
    {
        if ( i_WaitRequestTimeout != null )
        {
            if ( i_WaitRequestTimeout >= 0 )
            {
                this.waitRequestTimeout = i_WaitRequestTimeout;
            }
            else
            {
                this.waitRequestTimeout = null;
            }
        }
        else
        {
            this.waitRequestTimeout = null;
        }
        
        return (T) this;
    }
    
}
