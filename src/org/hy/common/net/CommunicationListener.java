package org.hy.common.net;

import java.util.EventListener;

import org.hy.common.net.data.CommunicationRequest;
import org.hy.common.net.data.CommunicationResponse;





/**
 * 数据通讯的监听事件接口
 *
 * @author      ZhengWei(HY)
 * @createDate  2017-01-16
 * @version     v1.0
 *              v2.0  2021-09-27  添加：处理数据通讯的过程是否为同步的。当为 false 时，表示服务端开启线程处理
 */
public interface CommunicationListener extends EventListener
{
    
    /**
     *  数据通讯的事件类型。即通知哪一个事件监听者来处理数据通讯（对应 ServerSocket.listeners 的分区标识）
     * 
     *  事件类型区分大小写
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-01-16
     * @version     v1.0
     *
     * @return
     */
    public String getEventType();
    
    
    
    /**
     * 数据通讯事件的执行动作
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-01-16
     * @version     v1.0
     *
     * @param i_RequestData
     * @return
     */
    public CommunicationResponse communication(CommunicationRequest i_RequestData);
    
    
    
    /**
     * 处理数据通讯的过程是否为同步的。当为 false 时，表示服务端开启线程处理
     * 
     * 默认为：同步机制
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-09-27
     * @version     v1.0
     * 
     * @return
     */
    public default boolean isSync()
    {
        return true;
    }
    
}
