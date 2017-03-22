package org.hy.common.net.data;

import org.hy.common.xml.SerializableDef;





/**
 * 传输对象：执行命令的命令信息
 *
 * @author      ZhengWei(HY)
 * @createDate  2017-01-16
 * @version     v1.0
 */
public class Command extends SerializableDef
{

    private static final long serialVersionUID = 876944139922883514L;
    
    
    
    /** 方法名称 */
    private String    methodName;
    
    /** 方法入参 */
    private Object [] params;

    
    
    /**
     * 获取：方法名称
     */
    public String getMethodName()
    {
        return methodName;
    }

    
    /**
     * 设置：方法名称
     * 
     * @param methodName 
     */
    public void setMethodName(String methodName)
    {
        this.methodName = methodName;
    }

    
    /**
     * 获取：方法入参
     */
    public Object [] getParams()
    {
        return this.params == null ? new Object[]{} : this.params;
    }

    
    /**
     * 设置：方法入参
     * 
     * @param params 
     */
    public void setParams(Object [] params)
    {
        this.params = params;
    }
    
}
