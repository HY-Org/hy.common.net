package org.hy.common.net.junit.netty.xjavacloud;

import org.hy.common.Date;





/**
 * 测试单元：XJavaCloud的数据结构（注：可不用实现Java序列化接口）
 * 
 * @author      ZhengWei(HY)
 * @createDate  2021-10-08
 * @version     v1.0
 */
public class DataInfo
{
    
    private String valueString;
    
    private Date   valueDate;
    
    private Object valueObject;

    
    
    public String getValueString()
    {
        return valueString;
    }

    
    public DataInfo setValueString(String valueString)
    {
        this.valueString = valueString;
        return this;
    }

    
    public Date getValueDate()
    {
        return valueDate;
    }

    
    public DataInfo setValueDate(Date valueDate)
    {
        this.valueDate = valueDate;
        return this;
    }

    
    public Object getValueObject()
    {
        return valueObject;
    }

    
    public DataInfo setValueObject(Object valueObject)
    {
        this.valueObject = valueObject;
        return this;
    }


    @Override
    public String toString()
    {
        return this.valueString + " - " + this.valueDate + " - " + this.valueObject;
    }
    
}
