package org.hy.common.net.data;

import org.hy.common.Help;





/**
 * 传输对象：用于通讯数据的请求
 *
 * @author      ZhengWei(HY)
 * @createDate  2017-01-14
 * @version     v1.0
 *              v2.0  2017-02-07  添加：$Operation_Selects 查询多个数据的类型标记
 *              v3.0  2017-02-28  添加：$Operation_SessionMap 获取Java.getSessionMap()数据
 *              v4.0  2019-02-27  添加：是否返回数据，即通讯CommunicationResponse.data是否返回。
 *                                      首次应用于定时任务的云任务执行上，确保定时任务服务器与云端任务间业务上的解偶性的相对独立。
 */
public class CommunicationRequest extends Communication
{

    private static final long serialVersionUID = 5600700812975586963L;
    
    
    
    /** 默认的事件类型 */
    public static final String $DefaultEventType     = "XJava";
    
    
    /** 数据的操作类型：添加 */
    public static final String $Operation_Insert     = "Insert";
    
    /** 数据的操作类型：更新 */
    public static final String $Operation_Update     = "Update";
    
    /** 数据的操作类型：删除 */
    public static final String $Operation_Delete     = "Delete";
    
    /** 数据的操作类型：查询 */
    public static final String $Operation_Select     = "Select";
    
    /** 数据的操作类型：查询多个 */
    public static final String $Operation_Selects    = "Selects";
    
    /** 数据的操作类型：获取Java.getSessionMap()数据 */
    public static final String $Operation_SessionMap = "SessionMap";
    
    /** 数据的操作类型：命令 */
    public static final String $Operation_Command    = "Command";
    
    
    
    /** 数据通讯的事件类型。即通知哪一个事件监听者来处理数据通讯。默认为：XJava */
    private String eventType;
    
    /** 数据的操作类型 */
    private String dataOperation;
    
    /** 
     * 是否返回数据，即通讯CommunicationResponse.data是否返回。
     * 
     * 默认情况是：true，返回数据。
     * 
     * 在什么情况下是不需要返回数据的呢？
     *   答：在发起请求Client端没有定义与响应Server服务端返回数据的Java对象类型的情况下。
     *       如，Server服务端执行方法后返回R.java对象，但Client端没有定义R对象，
     *       当此属性为false不返回数据时，Client端就不会java.lang.ClassNotFoundException异常。
     */
    private boolean isRetunData;
    
    
    
    public CommunicationRequest()
    {
        super();
        
        this.eventType   = $DefaultEventType;
        this.isRetunData = true;
    }
    
    
    
    public CommunicationRequest(String i_EventType)
    {
        super();
        
        this.eventType   = Help.NVL(i_EventType ,$DefaultEventType);
        this.isRetunData = true;
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
     * 获取：是否返回数据，即通讯CommunicationResponse.data是否返回。
     * 
     * 默认情况是：true，返回数据。
     * 
     * 在什么情况下是不需要返回数据的呢？
     *   答：在发起请求Client端没有定义与响应Server服务端返回数据的Java对象类型的情况下。
     *       如，Server服务端执行方法后返回R.java对象，但Client端没有定义R对象，
     *       当此属性为false不返回数据时，Client端就不会java.lang.ClassNotFoundException异常。
     */
    public boolean isRetunData()
    {
        return isRetunData;
    }


    
    /**
     * 设置：是否返回数据，即通讯CommunicationResponse.data是否返回。
     * 
     * 默认情况是：true，返回数据。
     * 
     * 在什么情况下是不需要返回数据的呢？
     *   答：在发起请求Client端没有定义与响应Server服务端返回数据的Java对象类型的情况下。
     *       如，Server服务端执行方法后返回R.java对象，但Client端没有定义R对象，
     *       当此属性为false不返回数据时，Client端就不会java.lang.ClassNotFoundException异常。
     * 
     * @param isRetunData 
     */
    public void setRetunData(boolean isRetunData)
    {
        this.isRetunData = isRetunData;
    }
    
}
