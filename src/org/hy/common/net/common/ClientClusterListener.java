package org.hy.common.net.common;

import java.util.Hashtable;
import java.util.Map;

import org.hy.common.ExecuteEvent;
import org.hy.common.ExecuteListener;
import org.hy.common.Help;
import org.hy.common.StringHelp;
import org.hy.common.net.data.CommunicationResponse;
import org.hy.common.xml.log.Logger;





/**
 * 客户端集群并发执行时，等待多并发执行结果的监听者
 *
 * @author      ZhengWei(HY)
 * @createDate  2017-01-19
 * @version     v1.0
 *              v2.0  2019-01-18  添加：显示日志
 *              v3.0  2021-10-12  升级：所有对客户端的操作，均改成ClientCluster接口
 */
public class ClientClusterListener implements ExecuteListener
{
    
    private static final Logger $Logger = new Logger(ClientClusterListener.class);
    
    
    /** 集群的总体执行结果 */
    private Map<ClientCluster ,CommunicationResponse> clusterResult;
    
    /** 集群执行后返回结果的数量 */
    private int                                       clusterCount;
    
    /** 日志信息。当为null或空字符串时，表示不显示日志 */
    private String                                    logInfo;
    
    
    
    public ClientClusterListener()
    {
        this(null);
    }
    
    
    
    public ClientClusterListener(String i_LogInfo)
    {
        this.logInfo       = i_LogInfo;
        this.clusterResult = new Hashtable<ClientCluster ,CommunicationResponse>();
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
    @Override
    public synchronized void result(ExecuteEvent i_Event)
    {
        ClientCluster v_Client = ((ClientOperation)i_Event.getSource()).getClient();
        this.clusterResult.put(v_Client ,(CommunicationResponse)i_Event.getResult());
        this.clusterCount++;
        
        if ( !Help.isNull(this.logInfo) )
        {
            CommunicationResponse v_CRet   = (CommunicationResponse)i_Event.getResult();
            StringBuilder         v_Buffer = new StringBuilder();
            
            v_Buffer.append("集群通讯已收到 ");
            v_Buffer.append(StringHelp.lpad(this.clusterCount ,4 ," "));
            v_Buffer.append(" 次返回。本次为 ");
            v_Buffer.append(v_Client.getHost());
            v_Buffer.append(":");
            v_Buffer.append(v_Client.getPort());
            v_Buffer.append(" ");
            v_Buffer.append(this.logInfo);
            v_Buffer.append(" ");
            v_Buffer.append((v_CRet.getResult()==0 ? "成功" : "失败(" + v_CRet.getResult() + ")"));
            
            $Logger.info(v_Buffer.toString());
        }
    }


    
    public Map<ClientCluster ,CommunicationResponse> getClusterResult()
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
