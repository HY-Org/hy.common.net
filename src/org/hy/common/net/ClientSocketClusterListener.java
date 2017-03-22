package org.hy.common.net;

import java.util.Hashtable;
import java.util.Map;

import org.hy.common.ExecuteEvent;
import org.hy.common.ExecuteListener;
import org.hy.common.net.data.CommunicationResponse;





/**
 * 客户端集群并发执行时，等待多并发执行结果的监听者
 *
 * @author      ZhengWei(HY)
 * @createDate  2017-01-19
 * @version     v1.0
 */
public class ClientSocketClusterListener implements ExecuteListener
{
    
    /** 集群的总体执行结果 */
    private Map<ClientSocket ,CommunicationResponse> clusterResult;
    
    /** 集群执行后返回结果的数量 */
    private int                                      clusterCount;
    
    
    
    public ClientSocketClusterListener()
    {
        clusterResult = new Hashtable<ClientSocket ,CommunicationResponse>();
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
    }


    
    public Map<ClientSocket ,CommunicationResponse> getClusterResult()
    {
        return clusterResult;
    }


    
    public int getClusterCount()
    {
        return clusterCount;
    }
    
}
