package org.hy.common.net;

import java.util.Hashtable;
import java.util.Map;

import org.hy.common.Date;
import org.hy.common.ExecuteEvent;
import org.hy.common.ExecuteListener;
import org.hy.common.Help;
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
    
    /** 日志信息。当为null或空字符串时，表示不显示日志 */
    private String                                   logInfo;
    
    
    
    public ClientSocketClusterListener()
    {
        this(null);
    }
    
    
    
    public ClientSocketClusterListener(String i_LogInfo)
    {
        this.logInfo       = i_LogInfo;
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
        
        if ( !Help.isNull(this.logInfo) )
        {
            ClientSocket          v_Client = (ClientSocket)i_Event.getSource();
            CommunicationResponse v_CRet   = (CommunicationResponse)i_Event.getResult();
            StringBuilder         v_Buffer = new StringBuilder();
            
            v_Buffer.append(Date.getNowTime().getFullMilli());
            v_Buffer.append(" 集群通讯已收到 ");
            v_Buffer.append(StringHelp.lpad(this.clusterCount ,4 ," "));
            v_Buffer.append(" 次返回。本次为 ");
            v_Buffer.append(v_Client.getHostName());
            v_Buffer.append(":");
            v_Buffer.append(v_Client.getPort() );
            v_Buffer.append(" ");
            v_Buffer.append(this.logInfo);
            v_Buffer.append(" ");
            v_Buffer.append((v_CRet.getResult()==0 ? "成功" : "失败(" + v_CRet.getResult() + ")"));
            
            System.out.println(v_Buffer.toString());
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
     * 获取：日志信息。当为null或空字符串时，表示不显示日志
     */
    public String getLogInfo()
    {
        return logInfo;
    }


    
    /**
     * 设置：日志信息。当为null或空字符串时，表示不显示日志
     * 
     * @param logInfo 
     */
    public void setLogInfo(String logInfo)
    {
        this.logInfo = logInfo;
    }
    
}
