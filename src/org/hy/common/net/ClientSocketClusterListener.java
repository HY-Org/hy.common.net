package org.hy.common.net;

import java.util.Hashtable;
import java.util.Map;

import org.hy.common.Date;
import org.hy.common.ExecuteEvent;
import org.hy.common.ExecuteListener;
import org.hy.common.StringHelp;
import org.hy.common.net.data.CommunicationResponse;





/**
 * 客户端集群并发执行时，等待多并发执行结果的监听者
 *
 * @author      ZhengWei(HY)
 * @createDate  2017-01-19
 * @version     v1.0
 *              v2.0  2019-01-18  添加：显示日志
 */
public class ClientSocketClusterListener implements ExecuteListener
{
    
    /** 集群的总体执行结果 */
    private Map<ClientSocket ,CommunicationResponse> clusterResult;
    
    /** 集群执行后返回结果的数量 */
    private int                                      clusterCount;
    
    /** 是否显示日志 */
    private boolean                                  isLog;
    
    
    
    public ClientSocketClusterListener()
    {
        this(false);
    }
    
    
    
    public ClientSocketClusterListener(boolean i_IsLog)
    {
        this.isLog         = i_IsLog;
        this.clusterResult = new Hashtable<ClientSocket ,CommunicationResponse>();
    }
    
    
    
    /**
     * 执行结果
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-01-19
     * @version     v1.0
     *
     * @param i_Event
     */
    public synchronized void result(ExecuteEvent i_Event)
    {
        this.clusterResult.put((ClientSocket)i_Event.getSource() ,(CommunicationResponse)i_Event.getResult());
        this.clusterCount++;
        
        if ( this.isLog )
        {
            ClientSocket          v_Client = (ClientSocket)i_Event.getSource();
            CommunicationResponse v_CRet   = (CommunicationResponse)i_Event.getResult();
            
            System.out.println(Date.getNowTime().getFullMilli() 
                             + " 集群通讯已收到 " + StringHelp.lpad(this.clusterCount ,4 ," ") + " 次返回。本次通讯为 " 
                             + v_Client.getHostName() 
                             + ":"
                             + v_Client.getPort() 
                             + " "
                             + (v_CRet.getResult()==0 ? "成功" : "失败(" + v_CRet.getResult() + ")"));
        }
    }


    
    public Map<ClientSocket ,CommunicationResponse> getClusterResult()
    {
        return clusterResult;
    }


    
    public int getClusterCount()
    {
        return clusterCount;
    }


    
    /**
     * 获取：是否显示日志
     */
    public boolean isLog()
    {
        return isLog;
    }
    

    
    /**
     * 设置：是否显示日志
     * 
     * @param isLog 
     */
    public void setLog(boolean isLog)
    {
        this.isLog = isLog;
    }
    
}
