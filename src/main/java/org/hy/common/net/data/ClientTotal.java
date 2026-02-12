package org.hy.common.net.data;

import java.util.List;

import org.hy.common.ExpireCache;
import org.hy.common.Help;
import org.hy.common.net.common.ClientCluster;





/**
 * 客户端向服务发送请求的统计类
 * 
 * 服务端的统计类见：ServerRPCHandler 类，通过XJava对象池获取
 * 
 * @author      ZhengWei(HY)
 * @createDate  2022-01-07
 * @version     v1.0
 */
public class ClientTotal
{
    /**
     * 客户端向服务发送请求的会话
     */
    private static final ExpireCache<ClientCluster ,SessionInfo> $Sessions = new ExpireCache<ClientCluster ,SessionInfo>();
    
    
    
    /**
     * 获取：会话信息
     * 
     * @author      ZhengWei(HY)
     * @createDate  2022-01-07
     * @version     v1.0
     * 
     * @return
     */
    public static List<SessionInfo> getSessions()
    {
        return Help.toList($Sessions);
    }
    
    
    
    /**
     * 添加或保持会话
     * 
     * @author      ZhengWei(HY)
     * @createDate  2022-01-07
     * @version     v1.0
     * 
     * @param i_Client
     */
    public static void put(ClientCluster i_Client)
    {
        $Sessions.put(i_Client ,i_Client.operation().getSession() ,i_Client.getSessionTime());
    }
    
    
    
    /**
     * 获取某一会话信息
     * 
     * @author      ZhengWei(HY)
     * @createDate  2022-01-07
     * @version     v1.0
     * 
     * @param i_Client
     */
    public static SessionInfo getSession(ClientCluster i_Client)
    {
        return $Sessions.get(i_Client);
    }
    
    
    
    /**
     * 重置统计数据
     * 
     * @author      ZhengWei(HY)
     * @createDate  2022-01-07
     * @version     v1.0
     *
     */
    public static void reset()
    {
        for (SessionInfo v_Session : $Sessions.values())
        {
            v_Session.setRequestCount(0);
            v_Session.setActiveCount(0);
            v_Session.setActiveTimeLen(0);
            v_Session.setActiveTime(null);
            
            if ( v_Session.getNetExceptions() != null )
            {
                v_Session.getNetExceptions().clear();
            }
        }
    }
    
    
    private ClientTotal()
    {
        
    }
    
}
