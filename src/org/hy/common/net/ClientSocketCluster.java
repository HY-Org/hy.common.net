package org.hy.common.net;

import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.hy.common.Date;
import org.hy.common.Execute;
import org.hy.common.Help;
import org.hy.common.net.data.Command;
import org.hy.common.net.data.CommunicationRequest;
import org.hy.common.net.data.CommunicationResponse;
import org.hy.common.net.socket.ClientSocketClusterListener;





/**
 * Socket客户端的集群执行。
 * 有如下两种方式：
 *   1. 多并发执行。多线程并发执行。
 *   2. 顺次执行。一个执行完，再执行另一个。
 * 
 * 注意: ClientSocketCluster类所有方法的入参timeout与ClientSocket.timeout是有区别的，含义不同。
 *       1. ClientSocketCluster类的timeout重点指，有大量数据通讯时，超过定义时长后，自动结束。一般本类的timeout可以配置大些。
 *       2. ClientSocket.timeout重点指连路是否正常，是在数据通讯前建立连接的等待时长。一般配置较小，如10秒。
 *
 * @author      ZhengWei(HY)
 * @createDate  2017-01-19
 * @version     v1.0
 *              v2.0  2017-02-04  添加：对一系列方法添加 IsWaitReturn 参数。是否等待集群执行结果。
 *                                     当不等待时，此方法执行更快，特别是在集群中某个服务故障时。不等待时，返回0元素个数的Map。
 *              v3.0  2017-02-07  添加：查询每服务上的多个对象getObjectss(...);
 *              v4.0  2019-01-18  添加：显示日志
 *              v5.0  2019-02-27  添加：服务端是否返回执行结果的数据。
 */
public class ClientSocketCluster
{
    
    /**
     * 以并发的方式，向集群服务端发送执行命令
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-01-20
     * @version     v1.0
     *
     * @param i_Cluster        集群服务列表
     * @param i_Timeout        超时终止线程的时长(单位：毫秒)
     * @param i_XID            XJava对象池的ID
     * @param i_Command        执行命令名称（即方法名称）
     * @return
     */
    public static Map<ClientSocket ,CommunicationResponse> sendCommands(List<ClientSocket> i_Cluster ,long i_Timeout ,String i_XID ,String i_Command)
    {
        return ClientSocketCluster.sendCommands(i_Cluster ,i_Timeout ,i_XID ,i_Command ,new Object[]{} ,true ,true ,null);
    }
    
    
    
    /**
     * 以并发的方式，向集群服务端发送执行命令
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-02-04
     * @version     v1.0
     *
     * @param i_Cluster        集群服务列表
     * @param i_Timeout        超时终止线程的时长(单位：毫秒)
     * @param i_XID            XJava对象池的ID
     * @param i_Command        执行命令名称（即方法名称）
     * @param i_IsWaitReturn   是否等待集群执行结果。当不等待时，此方法执行更快，特别是在集群中某个服务故障时。
     *                         不等待时，返回0元素个数的Map。
     * @return
     */
    public static Map<ClientSocket ,CommunicationResponse> sendCommands(List<ClientSocket> i_Cluster ,long i_Timeout ,String i_XID ,String i_Command ,boolean i_IsWaitReturn)
    {
        return ClientSocketCluster.sendCommands(i_Cluster ,i_Timeout ,i_XID ,i_Command ,new Object[]{} ,i_IsWaitReturn ,true ,null);
    }
    
    
    
    /**
     * 以并发的方式，向集群服务端发送执行命令
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-02-04
     * @version     v1.0
     *
     * @param i_Cluster        集群服务列表
     * @param i_Timeout        超时终止线程的时长(单位：毫秒)
     * @param i_XID            XJava对象池的ID
     * @param i_Command        执行命令名称（即方法名称）
     * @param i_IsWaitReturn   是否等待集群执行结果。当不等待时，此方法执行更快，特别是在集群中某个服务故障时。
     *                         不等待时，返回0元素个数的Map。
     * @param i_Log            日志信息。当为null或空字符串时，表示不显示日志
     * @return
     */
    public static Map<ClientSocket ,CommunicationResponse> sendCommands(List<ClientSocket> i_Cluster ,long i_Timeout ,String i_XID ,String i_Command ,boolean i_IsWaitReturn ,String i_Log)
    {
        return ClientSocketCluster.sendCommands(i_Cluster ,i_Timeout ,i_XID ,i_Command ,new Object[]{} ,i_IsWaitReturn ,true ,i_Log);
    }
    
    
    
    /**
     * 以并发的方式，向集群服务端发送执行命令
     * 
     * @author      ZhengWei(HY)
     * @createDate  2019-02-27
     * @version     v1.0
     *
     * @param i_Cluster        集群服务列表
     * @param i_Timeout        超时终止线程的时长(单位：毫秒)
     * @param i_XID            XJava对象池的ID
     * @param i_Command        执行命令名称（即方法名称）
     * @param i_IsWaitReturn   是否等待集群执行结果。当不等待时，此方法执行更快，特别是在集群中某个服务故障时。
     *                         不等待时，返回0元素个数的Map。
     * @param i_ServerIsReturn 服务端是否返回执行结果的数据
     * @return
     */
    public static Map<ClientSocket ,CommunicationResponse> sendCommands(List<ClientSocket> i_Cluster ,long i_Timeout ,String i_XID ,String i_Command ,boolean i_IsWaitReturn ,boolean i_ServerIsReturn)
    {
        return ClientSocketCluster.sendCommands(i_Cluster ,i_Timeout ,i_XID ,i_Command ,new Object[]{} ,i_IsWaitReturn ,i_ServerIsReturn ,null);
    }
    
    
    
    /**
     * 以并发的方式，向集群服务端发送执行命令
     * 
     * @author      ZhengWei(HY)
     * @createDate  2019-02-27
     * @version     v1.0
     *
     * @param i_Cluster        集群服务列表
     * @param i_Timeout        超时终止线程的时长(单位：毫秒)
     * @param i_XID            XJava对象池的ID
     * @param i_Command        执行命令名称（即方法名称）
     * @param i_IsWaitReturn   是否等待集群执行结果。当不等待时，此方法执行更快，特别是在集群中某个服务故障时。
     *                         不等待时，返回0元素个数的Map。
     * @param i_ServerIsReturn 服务端是否返回执行结果的数据
     * @param i_Log            日志信息。当为null或空字符串时，表示不显示日志
     * @return
     */
    public static Map<ClientSocket ,CommunicationResponse> sendCommands(List<ClientSocket> i_Cluster ,long i_Timeout ,String i_XID ,String i_Command ,boolean i_IsWaitReturn ,boolean i_ServerIsReturn ,String i_Log)
    {
        return ClientSocketCluster.sendCommands(i_Cluster ,i_Timeout ,i_XID ,i_Command ,new Object[]{} ,i_IsWaitReturn ,i_ServerIsReturn ,i_Log);
    }
    
    
    
    /**
     * 以并发的方式，向集群服务端发送执行命令
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-01-20
     * @version     v1.0
     *
     * @param i_Cluster        集群服务列表
     * @param i_Timeout        超时终止线程的时长(单位：毫秒)
     * @param i_XID            XJava对象池的ID
     * @param i_Command        执行命令名称（即方法名称）
     * @param i_CommandParams  执行命令参数（即方法参数）
     * @return
     */
    public static Map<ClientSocket ,CommunicationResponse> sendCommands(List<ClientSocket> i_Cluster ,long i_Timeout ,String i_XID ,String i_Command ,Object [] i_CommandParams)
    {
        return ClientSocketCluster.sendCommands(i_Cluster ,i_Timeout ,i_XID ,i_Command ,i_CommandParams ,true ,true ,null);
    }
    
    
    
    /**
     * 以并发的方式，向集群服务端发送执行命令
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-01-20
     * @version     v1.0
     *
     * @param i_Cluster        集群服务列表
     * @param i_Timeout        超时终止线程的时长(单位：毫秒)
     * @param i_XID            XJava对象池的ID
     * @param i_Command        执行命令名称（即方法名称）
     * @param i_CommandParams  执行命令参数（即方法参数）
     * @param i_IsWaitReturn   是否等待集群执行结果。当不等待时，此方法执行更快，特别是在集群中某个服务故障时。
     *                         不等待时，返回0元素个数的Map。
     * @return
     */
    public static Map<ClientSocket ,CommunicationResponse> sendCommands(List<ClientSocket> i_Cluster ,long i_Timeout ,String i_XID ,String i_Command ,Object [] i_CommandParams ,boolean i_IsWaitReturn)
    {
        return ClientSocketCluster.sendCommands(i_Cluster ,i_Timeout ,i_XID ,i_Command ,i_CommandParams ,i_IsWaitReturn ,true ,null);
    }
    
    
    
    /**
     * 以并发的方式，向集群服务端发送执行命令
     * 
     * @author      ZhengWei(HY)
     * @createDate  2019-02-27
     * @version     v1.0
     *
     * @param i_Cluster        集群服务列表
     * @param i_Timeout        超时终止线程的时长(单位：毫秒)
     * @param i_XID            XJava对象池的ID
     * @param i_Command        执行命令名称（即方法名称）
     * @param i_CommandParams  执行命令参数（即方法参数）
     * @param i_IsWaitReturn   是否等待集群执行结果。当不等待时，此方法执行更快，特别是在集群中某个服务故障时。
     *                         不等待时，返回0元素个数的Map。
     * @param i_ServerIsReturn 服务端是否返回执行结果的数据
     * @return
     */
    public static Map<ClientSocket ,CommunicationResponse> sendCommands(List<ClientSocket> i_Cluster ,long i_Timeout ,String i_XID ,String i_Command ,Object [] i_CommandParams ,boolean i_IsWaitReturn ,boolean i_ServerIsReturn)
    {
        return ClientSocketCluster.sendCommands(i_Cluster ,i_Timeout ,i_XID ,i_Command ,i_CommandParams ,i_IsWaitReturn ,i_ServerIsReturn ,null);
    }
    
    
    
    /**
     * 以并发的方式，向集群服务端发送执行命令
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-01-20
     * @version     v1.0
     *              v2.0  2019-01-18  添加：显示日志功能
     *
     * @param i_Cluster        集群服务列表
     * @param i_Timeout        超时终止线程的时长(单位：毫秒)
     * @param i_XID            XJava对象池的ID
     * @param i_Command        执行命令名称（即方法名称）
     * @param i_CommandParams  执行命令参数（即方法参数）
     * @param i_IsWaitReturn   是否等待集群执行结果。当不等待时，此方法执行更快，特别是在集群中某个服务故障时。
     *                         不等待时，返回0元素个数的Map。
     * @param i_Log            日志信息。当为null或空字符串时，表示不显示日志
     * @return
     */
    public static Map<ClientSocket ,CommunicationResponse> sendCommands(List<ClientSocket> i_Cluster ,long i_Timeout ,String i_XID ,String i_Command ,Object [] i_CommandParams ,boolean i_IsWaitReturn ,String i_Log)
    {
        return ClientSocketCluster.sendCommands(i_Cluster ,i_Timeout ,i_XID ,i_Command ,i_CommandParams ,i_IsWaitReturn ,true ,i_Log);
    }
    
    
    
    /**
     * 以并发的方式，向集群服务端发送执行命令
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-01-20
     * @version     v1.0
     *              v2.0  2019-01-18  添加：显示日志功能
     *              v3.0  2019-02-27  添加：服务端是否返回执行结果的数据
     *
     * @param i_Cluster        集群服务列表
     * @param i_Timeout        超时终止线程的时长(单位：毫秒)
     * @param i_XID            XJava对象池的ID
     * @param i_Command        执行命令名称（即方法名称）
     * @param i_CommandParams  执行命令参数（即方法参数）
     * @param i_IsWaitReturn   是否等待集群执行结果。当不等待时，此方法执行更快，特别是在集群中某个服务故障时。
     *                         不等待时，返回0元素个数的Map。
     * @param i_ServerIsReturn 服务端是否返回执行结果的数据
     * @param i_Log            日志信息。当为null或空字符串时，表示不显示日志
     * @return
     */
    public static Map<ClientSocket ,CommunicationResponse> sendCommands(List<ClientSocket> i_Cluster ,long i_Timeout ,String i_XID ,String i_Command ,Object [] i_CommandParams ,boolean i_IsWaitReturn ,boolean i_ServerIsReturn ,String i_Log)
    {
        CommunicationRequest v_RequestData = new CommunicationRequest();
        Command              v_Command     = new Command();
        
        v_Command.setMethodName(i_Command);
        v_Command.setParams(    i_CommandParams);
        
        v_RequestData.setDataXID(      i_XID);
        v_RequestData.setData(         v_Command);
        v_RequestData.setDataOperation(CommunicationRequest.$Operation_Command);
        v_RequestData.setRetunData(    i_ServerIsReturn);
        
        return ClientSocketCluster.sends(i_Cluster ,i_Timeout ,v_RequestData ,i_IsWaitReturn ,i_Log);
    }
    
    
    
    /**
     * 以并发的方式，移除集群服务端的对象（默认从XJava对象池中移除）。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-01-23
     * @version     v1.0
     *
     * @param i_Cluster        集群服务列表
     * @param i_Timeout        超时终止线程的时长(单位：毫秒)
     * @param i_XID            XJava对象池的ID
     * @return
     */
    public static Map<ClientSocket ,CommunicationResponse> removeObjects(List<ClientSocket> i_Cluster ,long i_Timeout ,String i_XID)
    {
        return removeObjects(i_Cluster ,i_Timeout ,i_XID ,true ,true);
    }
    
    
    
    /**
     * 以并发的方式，移除集群服务端的对象（默认从XJava对象池中移除）。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2019-02-27
     * @version     v1.0
     *
     * @param i_Cluster        集群服务列表
     * @param i_Timeout        超时终止线程的时长(单位：毫秒)
     * @param i_XID            XJava对象池的ID
     * @param i_IsWaitReturn   是否等待集群执行结果。当不等待时，此方法执行更快，特别是在集群中某个服务故障时。
     *                         不等待时，返回0元素个数的Map。
     * @return
     */
    public static Map<ClientSocket ,CommunicationResponse> removeObjects(List<ClientSocket> i_Cluster ,long i_Timeout ,String i_XID ,boolean i_IsWaitReturn)
    {
        return removeObjects(i_Cluster ,i_Timeout ,i_XID ,i_IsWaitReturn ,true);
    }
    
    
    
    /**
     * 以并发的方式，移除集群服务端的对象（默认从XJava对象池中移除）。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-02-04
     * @version     v1.0
     *              v2.0  2019-02-27  添加：服务端是否返回执行结果的数据
     *
     * @param i_Cluster        集群服务列表
     * @param i_Timeout        超时终止线程的时长(单位：毫秒)
     * @param i_XID            XJava对象池的ID
     * @param i_IsWaitReturn   是否等待集群执行结果。当不等待时，此方法执行更快，特别是在集群中某个服务故障时。
     *                         不等待时，返回0元素个数的Map。
     * @param i_ServerIsReturn 服务端是否返回执行结果的数据
     * @return
     */
    public static Map<ClientSocket ,CommunicationResponse> removeObjects(List<ClientSocket> i_Cluster ,long i_Timeout ,String i_XID ,boolean i_IsWaitReturn ,boolean i_ServerIsReturn)
    {
        CommunicationRequest v_RequestData = new CommunicationRequest();
        
        v_RequestData.setDataXID(      i_XID);
        v_RequestData.setDataOperation(CommunicationRequest.$Operation_Delete);
        v_RequestData.setRetunData(    i_ServerIsReturn);
        
        return ClientSocketCluster.sends(i_Cluster ,i_Timeout ,v_RequestData ,i_IsWaitReturn);
    }
    
    
    
    /**
     * 以并发的方式，获取集群服务端的对象（默认从XJava对象池中获取）。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-01-23
     * @version     v1.0
     *
     * @param i_Cluster        集群服务列表
     * @param i_Timeout        超时终止线程的时长(单位：毫秒)
     * @param i_XID            XJava对象池的ID
     * @return
     */
    public static Map<ClientSocket ,CommunicationResponse> getObjects(List<ClientSocket> i_Cluster ,long i_Timeout ,String i_XID)
    {
        CommunicationRequest v_RequestData = new CommunicationRequest();
        
        v_RequestData.setDataXID(      i_XID);
        v_RequestData.setDataOperation(CommunicationRequest.$Operation_Select);
        
        return ClientSocketCluster.sends(i_Cluster ,i_Timeout ,v_RequestData);
    }
    
    
    
    /**
     * 以并发的方式，获取集群服务端的对象（默认从XJava对象池中获取）。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-01-23
     * @version     v1.0
     *
     * @param i_Cluster        集群服务列表
     * @param i_Timeout        超时终止线程的时长(单位：毫秒)
     * @param i_XID            XJava对象池的ID标识符的前缀(区分大小写)
     * @return                 Map.Value.getData() 是一个List<CommunicationResponse>()结构的实例。
     * @return
     */
    public static Map<ClientSocket ,CommunicationResponse> getObjectss(List<ClientSocket> i_Cluster ,long i_Timeout ,String i_XID)
    {
        CommunicationRequest v_RequestData = new CommunicationRequest();
        
        v_RequestData.setDataXID(      i_XID);
        v_RequestData.setDataOperation(CommunicationRequest.$Operation_Selects);
        
        return ClientSocketCluster.sends(i_Cluster ,i_Timeout ,v_RequestData);
    }
    
    
    
    /**
     * 以并发的方式，向集群服务端中写入或更新对象（默认将写入XJava对象池）。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-01-23
     * @version     v1.0
     *
     * @param i_Cluster        集群服务列表
     * @param i_Timeout        超时终止线程的时长(单位：毫秒)
     * @param i_XID            XJava对象池的ID
     * @param i_Data           XJava对象
     * @return
     */
    public static Map<ClientSocket ,CommunicationResponse> sendObjects(List<ClientSocket> i_Cluster ,long i_Timeout ,String i_XID ,Object i_Data)
    {
        return ClientSocketCluster.sendObjects(i_Cluster ,i_Timeout ,i_XID ,i_Data ,0 ,true ,true ,null);
    }
    
    
    
    /**
     * 以并发的方式，向集群服务端中写入或更新对象（默认将写入XJava对象池）。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-02-04
     * @version     v1.0
     *
     * @param i_Cluster        集群服务列表
     * @param i_Timeout        超时终止线程的时长(单位：毫秒)
     * @param i_XID            XJava对象池的ID
     * @param i_Data           XJava对象
     * @param i_IsWaitReturn   是否等待集群执行结果。当不等待时，此方法执行更快，特别是在集群中某个服务故障时。
     *                         不等待时，返回0元素个数的Map。
     * @return
     */
    public static Map<ClientSocket ,CommunicationResponse> sendObjects(List<ClientSocket> i_Cluster ,long i_Timeout ,String i_XID ,Object i_Data ,boolean i_IsWaitReturn)
    {
        return ClientSocketCluster.sendObjects(i_Cluster ,i_Timeout ,i_XID ,i_Data ,0 ,i_IsWaitReturn ,true ,null);
    }
    
    
    
    /**
     * 以并发的方式，向集群服务端中写入或更新对象（默认将写入XJava对象池）。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2019-02-27
     * @version     v1.0
     *
     * @param i_Cluster        集群服务列表
     * @param i_Timeout        超时终止线程的时长(单位：毫秒)
     * @param i_XID            XJava对象池的ID
     * @param i_Data           XJava对象
     * @param i_IsWaitReturn   是否等待集群执行结果。当不等待时，此方法执行更快，特别是在集群中某个服务故障时。
     *                         不等待时，返回0元素个数的Map。
     * @param i_ServerIsReturn 服务端是否返回执行结果的数据
     * @return
     */
    public static Map<ClientSocket ,CommunicationResponse> sendObjects(List<ClientSocket> i_Cluster ,long i_Timeout ,String i_XID ,Object i_Data ,boolean i_IsWaitReturn ,boolean i_ServerIsReturn)
    {
        return ClientSocketCluster.sendObjects(i_Cluster ,i_Timeout ,i_XID ,i_Data ,0 ,i_IsWaitReturn ,i_ServerIsReturn ,null);
    }
    
    
    
    /**
     * 以并发的方式，向集群服务端中写入或更新对象（默认将写入XJava对象池）。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2019-02-27
     * @version     v1.0
     *
     * @param i_Cluster        集群服务列表
     * @param i_Timeout        超时终止线程的时长(单位：毫秒)
     * @param i_XID            XJava对象池的ID
     * @param i_Data           XJava对象
     * @param i_IsWaitReturn   是否等待集群执行结果。当不等待时，此方法执行更快，特别是在集群中某个服务故障时。
     *                         不等待时，返回0元素个数的Map。
     * @param i_Log            日志信息。当为null或空字符串时，表示不显示日志
     * @return
     */
    public static Map<ClientSocket ,CommunicationResponse> sendObjects(List<ClientSocket> i_Cluster ,long i_Timeout ,String i_XID ,Object i_Data ,boolean i_IsWaitReturn ,String i_Log)
    {
        return ClientSocketCluster.sendObjects(i_Cluster ,i_Timeout ,i_XID ,i_Data ,0 ,i_IsWaitReturn ,true ,i_Log);
    }
    
    
    
    /**
     * 以并发的方式，向集群服务端中写入或更新对象（默认将写入XJava对象池）。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2019-02-27
     * @version     v1.0
     *
     * @param i_Cluster        集群服务列表
     * @param i_Timeout        超时终止线程的时长(单位：毫秒)
     * @param i_XID            XJava对象池的ID
     * @param i_Data           XJava对象
     * @param i_IsWaitReturn   是否等待集群执行结果。当不等待时，此方法执行更快，特别是在集群中某个服务故障时。
     *                         不等待时，返回0元素个数的Map。
     * @param i_ServerIsReturn 服务端是否返回执行结果的数据
     * @param i_Log            日志信息。当为null或空字符串时，表示不显示日志
     * @return
     */
    public static Map<ClientSocket ,CommunicationResponse> sendObjects(List<ClientSocket> i_Cluster ,long i_Timeout ,String i_XID ,Object i_Data ,boolean i_IsWaitReturn ,boolean i_ServerIsReturn ,String i_Log)
    {
        return ClientSocketCluster.sendObjects(i_Cluster ,i_Timeout ,i_XID ,i_Data ,0 ,i_IsWaitReturn ,i_ServerIsReturn ,i_Log);
    }
    
    
    
    /**
     * 以并发的方式，向集群服务端中写入或更新对象（默认将写入XJava对象池）。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-01-23
     * @version     v1.0
     *
     * @param i_Cluster        集群服务列表
     * @param i_Timeout        超时终止线程的时长(单位：毫秒)
     * @param i_XID            XJava对象池的ID
     * @param i_Data           XJava对象
     * @param i_ExpireTimeLen  数据的过期时长(单位：秒)
     * @return
     */
    public static Map<ClientSocket ,CommunicationResponse> sendObjects(List<ClientSocket> i_Cluster ,long i_Timeout ,String i_XID ,Object i_Data ,long i_ExpireTimeLen)
    {
        return ClientSocketCluster.sendObjects(i_Cluster ,i_Timeout ,i_XID ,i_Data ,i_ExpireTimeLen ,true ,true ,null);
    }
    
    
    
    /**
     * 以并发的方式，向集群服务端中写入或更新对象（默认将写入XJava对象池）。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-01-23
     * @version     v1.0
     *
     * @param i_Cluster        集群服务列表
     * @param i_Timeout        超时终止线程的时长(单位：毫秒)
     * @param i_XID            XJava对象池的ID
     * @param i_Data           XJava对象
     * @param i_ExpireTimeLen  数据的过期时长(单位：秒)
     * @param i_IsWaitReturn   是否等待集群执行结果。当不等待时，此方法执行更快，特别是在集群中某个服务故障时。
     *                         不等待时，返回0元素个数的Map。
     * @return
     */
    public static Map<ClientSocket ,CommunicationResponse> sendObjects(List<ClientSocket> i_Cluster ,long i_Timeout ,String i_XID ,Object i_Data ,long i_ExpireTimeLen ,boolean i_IsWaitReturn)
    {
        return ClientSocketCluster.sendObjects(i_Cluster ,i_Timeout ,i_XID ,i_Data ,i_ExpireTimeLen ,i_IsWaitReturn ,true ,null);
    }
    
    
    
    /**
     * 以并发的方式，向集群服务端中写入或更新对象（默认将写入XJava对象池）。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2019-02-27
     * @version     v1.0
     *
     * @param i_Cluster        集群服务列表
     * @param i_Timeout        超时终止线程的时长(单位：毫秒)
     * @param i_XID            XJava对象池的ID
     * @param i_Data           XJava对象
     * @param i_ExpireTimeLen  数据的过期时长(单位：秒)
     * @param i_IsWaitReturn   是否等待集群执行结果。当不等待时，此方法执行更快，特别是在集群中某个服务故障时。
     *                         不等待时，返回0元素个数的Map。
     * @param i_ServerIsReturn 服务端是否返回执行结果的数据
     * @return
     */
    public static Map<ClientSocket ,CommunicationResponse> sendObjects(List<ClientSocket> i_Cluster ,long i_Timeout ,String i_XID ,Object i_Data ,long i_ExpireTimeLen ,boolean i_IsWaitReturn ,boolean i_ServerIsReturn)
    {
        return ClientSocketCluster.sendObjects(i_Cluster ,i_Timeout ,i_XID ,i_Data ,i_ExpireTimeLen ,i_IsWaitReturn ,i_ServerIsReturn ,null);
    }
    
    
    
    /**
     * 以并发的方式，向集群服务端中写入或更新对象（默认将写入XJava对象池）。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-01-23
     * @version     v1.0
     *              v2.0  2019-01-18  添加：显示日志功能
     *
     * @param i_Cluster        集群服务列表
     * @param i_Timeout        超时终止线程的时长(单位：毫秒)
     * @param i_XID            XJava对象池的ID
     * @param i_Data           XJava对象
     * @param i_ExpireTimeLen  数据的过期时长(单位：秒)
     * @param i_IsWaitReturn   是否等待集群执行结果。当不等待时，此方法执行更快，特别是在集群中某个服务故障时。
     *                         不等待时，返回0元素个数的Map。
     * @param i_Log            日志信息。当为null或空字符串时，表示不显示日志
     * @return
     */
    public static Map<ClientSocket ,CommunicationResponse> sendObjects(List<ClientSocket> i_Cluster ,long i_Timeout ,String i_XID ,Object i_Data ,long i_ExpireTimeLen ,boolean i_IsWaitReturn ,String i_Log)
    {
        return ClientSocketCluster.sendObjects(i_Cluster ,i_Timeout ,i_XID ,i_Data ,i_ExpireTimeLen ,i_IsWaitReturn ,true ,i_Log);
    }
    
    
    
    /**
     * 以并发的方式，向集群服务端中写入或更新对象（默认将写入XJava对象池）。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-01-23
     * @version     v1.0
     *              v2.0  2019-01-18  添加：显示日志功能
     *              v3.0  2019-02-27  添加：服务端是否返回执行结果的数据
     *
     * @param i_Cluster        集群服务列表
     * @param i_Timeout        超时终止线程的时长(单位：毫秒)
     * @param i_XID            XJava对象池的ID
     * @param i_Data           XJava对象
     * @param i_ExpireTimeLen  数据的过期时长(单位：秒)
     * @param i_IsWaitReturn   是否等待集群执行结果。当不等待时，此方法执行更快，特别是在集群中某个服务故障时。
     *                         不等待时，返回0元素个数的Map。
     * @param i_ServerIsReturn 服务端是否返回执行结果的数据
     * @param i_Log            日志信息。当为null或空字符串时，表示不显示日志
     * @return
     */
    public static Map<ClientSocket ,CommunicationResponse> sendObjects(List<ClientSocket> i_Cluster ,long i_Timeout ,String i_XID ,Object i_Data ,long i_ExpireTimeLen ,boolean i_IsWaitReturn ,boolean i_ServerIsReturn ,String i_Log)
    {
        CommunicationRequest v_RequestData = new CommunicationRequest();
        
        v_RequestData.setDataXID(          i_XID);
        v_RequestData.setData(             i_Data);
        v_RequestData.setDataExpireTimeLen(i_ExpireTimeLen);
        v_RequestData.setDataOperation(    CommunicationRequest.$Operation_Update);
        v_RequestData.setRetunData(        i_ServerIsReturn);
        
        return ClientSocketCluster.sends(i_Cluster ,i_Timeout ,v_RequestData ,i_IsWaitReturn ,i_Log);
    }
    
    
    
    /**
     * 以并发的方式，向集群服务端发送数据。
     * 
     * 重写发送方法。
     *   将其分为二个步骤完成：
     *     步骤1. 先登陆，并获取服务端新分配的用于通讯的端口号。
     *     步骤2. 用新的通讯端口发送真正要发送的数据。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-02-04
     * @version     v1.0
     *
     * @param i_Cluster      集群服务列表
     * @param i_Timeout      超时终止线程的时长(单位：毫秒)
     * @param i_RequestData  请求发送给服务端的数据
     * @return
     */
    public static Map<ClientSocket ,CommunicationResponse> sends(List<ClientSocket> i_Cluster ,long i_Timeout ,CommunicationRequest i_RequestData)
    {
        return sends(i_Cluster ,i_Timeout ,i_RequestData ,true);
    }
    
    
    
    /**
     * 以并发的方式，向集群服务端发送数据。
     * 
     * 重写发送方法。
     *   将其分为二个步骤完成：
     *     步骤1. 先登陆，并获取服务端新分配的用于通讯的端口号。
     *     步骤2. 用新的通讯端口发送真正要发送的数据。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-01-19
     * @version     v1.0
     *
     * @param i_Cluster      集群服务列表
     * @param i_Timeout      超时终止线程的时长(单位：毫秒)
     * @param i_RequestData  请求发送给服务端的数据
     * @param i_IsWaitReturn 是否等待集群执行结果。当不等待时，此方法执行更快，特别是在集群中某个服务故障时。
     *                       不等待时，返回0元素个数的Map。
     * @return
     */
    public static Map<ClientSocket ,CommunicationResponse> sends(List<ClientSocket> i_Cluster ,long i_Timeout ,CommunicationRequest i_RequestData ,boolean i_IsWaitReturn)
    {
        return sends(i_Cluster ,i_Timeout ,i_RequestData ,i_IsWaitReturn ,null);
    }
    
    
    
    /**
     * 以并发的方式，向集群服务端发送数据。
     * 
     * 重写发送方法。
     *   将其分为二个步骤完成：
     *     步骤1. 先登陆，并获取服务端新分配的用于通讯的端口号。
     *     步骤2. 用新的通讯端口发送真正要发送的数据。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-01-19
     * @version     v1.0
     *              v2.0  2019-01-18  添加：显示日志功能
     *
     * @param i_Cluster      集群服务列表
     * @param i_Timeout      超时终止线程的时长(单位：毫秒)
     * @param i_RequestData  请求发送给服务端的数据
     * @param i_IsWaitReturn 是否等待集群执行结果。当不等待时，此方法执行更快，特别是在集群中某个服务故障时。
     *                       不等待时，返回0元素个数的Map。
     * @param i_Log          日志信息。当为null或空字符串时，表示不显示日志
     * @return
     */
    public static Map<ClientSocket ,CommunicationResponse> sends(List<ClientSocket> i_Cluster ,long i_Timeout ,CommunicationRequest i_RequestData ,boolean i_IsWaitReturn ,String i_Log)
    {
        if ( i_Timeout <= 1000 )
        {
            throw new RuntimeException("Timeout is not <= 1000 millisecond.");
        }
        
        if ( Help.isNull(i_Cluster) )
        {
            return new Hashtable<ClientSocket ,CommunicationResponse>();
        }
        
        ClientSocketClusterListener v_Listener = new ClientSocketClusterListener(i_Log);
        
        for (ClientSocket v_Client : i_Cluster)
        {
            Execute v_Execute = new Execute(v_Client ,"send" ,i_RequestData);
            
            v_Execute.addListener(v_Listener);
            
            v_Execute.start(i_Timeout);
        }
        
        if ( i_IsWaitReturn )
        {
            int  v_ClusterCount = i_Cluster.size();
            long v_Timeout      = Date.getNowTime().getTime() + i_Timeout * (v_ClusterCount + 2);
            while ( v_Listener.getClusterCount() < v_ClusterCount && Date.getNowTime().getTime() <= v_Timeout)
            {
                // 一直等待集群的执行结果，直到所有服务器均返回结果
                try
                {
                    Thread.sleep(2);
                }
                catch (Exception exce)
                {
                    // Nothing.
                }
            }
            return v_Listener.getClusterResult();
        }
        else
        {
            return new Hashtable<ClientSocket ,CommunicationResponse>();
        }
    }
    
    
    
    /**
     * 以顺次的方式，向集群服务端发送执行命令
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-01-20
     * @version     v1.0
     *
     * @param i_Cluster        集群服务列表
     * @param i_XID            XJava对象池的ID
     * @param i_Command        执行命令名称（即方法名称）
     * @return
     */
    public static Map<ClientSocket ,CommunicationResponse> sendCommands(List<ClientSocket> i_Cluster ,String i_XID ,String i_Command)
    {
        return ClientSocketCluster.sendCommands(i_Cluster ,i_XID ,i_Command ,new Object[]{} ,true);
    }
    
    
    
    /**
     * 以顺次的方式，向集群服务端发送执行命令
     * 
     * @author      ZhengWei(HY)
     * @createDate  2019-02-27
     * @version     v1.0
     *
     * @param i_Cluster        集群服务列表
     * @param i_XID            XJava对象池的ID
     * @param i_Command        执行命令名称（即方法名称）
     * @param i_ServerIsReturn 服务端是否返回执行结果的数据
     * @return
     */
    public static Map<ClientSocket ,CommunicationResponse> sendCommands(List<ClientSocket> i_Cluster ,String i_XID ,String i_Command ,boolean i_ServerIsReturn)
    {
        return ClientSocketCluster.sendCommands(i_Cluster ,i_XID ,i_Command ,new Object[]{} ,i_ServerIsReturn);
    }
    
    
    
    /**
     * 以顺次的方式，向集群服务端发送执行命令
     * 
     * @author      ZhengWei(HY)
     * @createDate  2019-02-27
     * @version     v1.0
     *
     * @param i_Cluster        集群服务列表
     * @param i_XID            XJava对象池的ID
     * @param i_Command        执行命令名称（即方法名称）
     * @param i_CommandParams  执行命令参数（即方法参数）
     * @return
     */
    public static Map<ClientSocket ,CommunicationResponse> sendCommands(List<ClientSocket> i_Cluster ,String i_XID ,String i_Command ,Object [] i_CommandParams)
    {
        return ClientSocketCluster.sendCommands(i_Cluster ,i_XID ,i_Command ,i_CommandParams ,true);
    }
    
    
    
    /**
     * 以顺次的方式，向集群服务端发送执行命令
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-01-20
     * @version     v1.0
     *              v2.0  2019-02-27  添加：服务端是否返回执行结果的数据
     *
     * @param i_Cluster        集群服务列表
     * @param i_XID            XJava对象池的ID
     * @param i_Command        执行命令名称（即方法名称）
     * @param i_CommandParams  执行命令参数（即方法参数）
     * @param i_ServerIsReturn 服务端是否返回执行结果的数据
     * @return
     */
    public static Map<ClientSocket ,CommunicationResponse> sendCommands(List<ClientSocket> i_Cluster ,String i_XID ,String i_Command ,Object [] i_CommandParams ,boolean i_ServerIsReturn)
    {
        CommunicationRequest v_RequestData = new CommunicationRequest();
        Command              v_Command     = new Command();
        
        v_Command.setMethodName(i_Command);
        v_Command.setParams(    i_CommandParams);
        
        v_RequestData.setDataXID(      i_XID);
        v_RequestData.setData(         v_Command);
        v_RequestData.setDataOperation(CommunicationRequest.$Operation_Command);
        v_RequestData.setRetunData(    i_ServerIsReturn);
        
        return ClientSocketCluster.sends(i_Cluster ,v_RequestData);
    }
    
    
    
    /**
     * 以顺次的方式，移除集群服务端的对象（默认从XJava对象池中移除）。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2019-02-27
     * @version     v1.0
     *
     * @param i_Cluster        集群服务列表
     * @param i_XID            XJava对象池的ID
     * @return
     */
    public static Map<ClientSocket ,CommunicationResponse> removeObjects(List<ClientSocket> i_Cluster ,String i_XID)
    {
        return removeObjects(i_Cluster ,i_XID ,false);
    }
    
    
    
    /**
     * 以顺次的方式，移除集群服务端的对象（默认从XJava对象池中移除）。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-01-23
     * @version     v1.0
     *              v2.0  2019-02-27  添加：服务端是否返回执行结果的数据
     *
     * @param i_Cluster        集群服务列表
     * @param i_XID            XJava对象池的ID
     * @param i_ServerIsReturn 服务端是否返回执行结果的数据
     * @return
     */
    public static Map<ClientSocket ,CommunicationResponse> removeObjects(List<ClientSocket> i_Cluster ,String i_XID ,boolean i_ServerIsReturn)
    {
        CommunicationRequest v_RequestData = new CommunicationRequest();
        
        v_RequestData.setDataXID(      i_XID);
        v_RequestData.setDataOperation(CommunicationRequest.$Operation_Delete);
        v_RequestData.setRetunData(    i_ServerIsReturn);
        
        return ClientSocketCluster.sends(i_Cluster ,v_RequestData);
    }
    
    
    
    /**
     * 以顺次的方式，获取集群服务端的对象（默认从XJava对象池中获取）。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-01-23
     * @version     v1.0
     *
     * @param i_XID            XJava对象池的ID
     * @return
     */
    public static Map<ClientSocket ,CommunicationResponse> getObjects(List<ClientSocket> i_Cluster ,String i_XID)
    {
        CommunicationRequest v_RequestData = new CommunicationRequest();
        
        v_RequestData.setDataXID(      i_XID);
        v_RequestData.setDataOperation(CommunicationRequest.$Operation_Select);
        
        return ClientSocketCluster.sends(i_Cluster ,v_RequestData);
    }
    
    
    
    /**
     * 以顺次的方式，获取集群服务端的对象（每台服务都获取多个对象）（默认从XJava对象池中获取）。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-02-07
     * @version     v1.0
     *
     * @param i_XID  XJava对象池的ID标识符的前缀(区分大小写)
     * @return       Map.Value.getData() 是一个List<CommunicationResponse>()结构的实例。
     */
    public static Map<ClientSocket ,CommunicationResponse> getObjectss(List<ClientSocket> i_Cluster ,String i_XID)
    {
        CommunicationRequest v_RequestData = new CommunicationRequest();
        
        v_RequestData.setDataXID(      i_XID);
        v_RequestData.setDataOperation(CommunicationRequest.$Operation_Selects);
        
        return ClientSocketCluster.sends(i_Cluster ,v_RequestData);
    }
    
    
    
    /**
     * 以顺次的方式，向集群服务端中写入或更新对象（默认将写入XJava对象池）。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-01-23
     * @version     v1.0
     *
     * @param i_XID            XJava对象池的ID
     * @param i_Data           XJava对象
     * @return
     */
    public static Map<ClientSocket ,CommunicationResponse> sendObjects(List<ClientSocket> i_Cluster ,String i_XID ,Object i_Data)
    {
        return ClientSocketCluster.sendObjects(i_Cluster ,i_XID ,i_Data ,0 ,true);
    }
    
    
    
    /**
     * 以顺次的方式，向集群服务端中写入或更新对象（默认将写入XJava对象池）。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2019-02-27
     * @version     v1.0
     *
     * @param i_XID            XJava对象池的ID
     * @param i_Data           XJava对象
     * @param i_ServerIsReturn 服务端是否返回执行结果的数据
     * @return
     */
    public static Map<ClientSocket ,CommunicationResponse> sendObjects(List<ClientSocket> i_Cluster ,String i_XID ,Object i_Data ,boolean i_ServerIsReturn)
    {
        return ClientSocketCluster.sendObjects(i_Cluster ,i_XID ,i_Data ,0 ,i_ServerIsReturn);
    }
    
    
    
    /**
     * 以顺次的方式，向集群服务端中写入或更新对象（默认将写入XJava对象池）。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2019-02-27
     * @version     v1.0
     *
     * @param i_XID            XJava对象池的ID
     * @param i_Data           XJava对象
     * @param i_ExpireTimeLen  数据的过期时长(单位：秒)
     * @return
     */
    public static Map<ClientSocket ,CommunicationResponse> sendObjects(List<ClientSocket> i_Cluster ,String i_XID ,Object i_Data ,long i_ExpireTimeLen)
    {
        return ClientSocketCluster.sendObjects(i_Cluster ,i_XID ,i_Data ,i_ExpireTimeLen ,true);
    }
    
    
    
    /**
     * 以顺次的方式，向集群服务端中写入或更新对象（默认将写入XJava对象池）。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-01-23
     * @version     v1.0
     *              v2.0  2019-02-27  添加：服务端是否返回执行结果的数据
     *
     * @param i_XID            XJava对象池的ID
     * @param i_Data           XJava对象
     * @param i_ExpireTimeLen  数据的过期时长(单位：秒)
     * @param i_ServerIsReturn 服务端是否返回执行结果的数据
     * @return
     */
    public static Map<ClientSocket ,CommunicationResponse> sendObjects(List<ClientSocket> i_Cluster ,String i_XID ,Object i_Data ,long i_ExpireTimeLen ,boolean i_ServerIsReturn)
    {
        CommunicationRequest v_RequestData = new CommunicationRequest();
        
        v_RequestData.setDataXID(          i_XID);
        v_RequestData.setData(             i_Data);
        v_RequestData.setDataExpireTimeLen(i_ExpireTimeLen);
        v_RequestData.setDataOperation(    CommunicationRequest.$Operation_Update);
        v_RequestData.setRetunData(        true);
        
        return ClientSocketCluster.sends(i_Cluster ,v_RequestData);
    }
    
    
    
    /**
     * 以顺次的方式，向集群服务端发送数据。
     * 
     * 重写发送方法。
     *   将其分为二个步骤完成：
     *     步骤1. 先登陆，并获取服务端新分配的用于通讯的端口号。
     *     步骤2. 用新的通讯端口发送真正要发送的数据。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-01-19
     * @version     v1.0
     *
     * @param i_Cluster      集群服务列表
     * @param i_RequestData  请求发送给服务端的数据
     * @return
     */
    public static Map<ClientSocket ,CommunicationResponse> sends(List<ClientSocket> i_Cluster ,CommunicationRequest i_RequestData)
    {
        Map<ClientSocket ,CommunicationResponse> v_ClusterResponses = new LinkedHashMap<ClientSocket ,CommunicationResponse>();
        
        if ( Help.isNull(i_Cluster) )
        {
            return v_ClusterResponses;
        }
        
        for (ClientSocket v_Client : i_Cluster)
        {
            CommunicationResponse v_ResponseData = v_Client.send(i_RequestData);
            
            v_ClusterResponses.put(v_Client ,v_ResponseData);
        }
        
        return v_ClusterResponses;
    }
    
    
    
    /**
     * 不允许创建实例
     *
     * @author      ZhengWei(HY)
     * @createDate  2017-01-19
     * @version     v1.0
     *
     */
    private ClientSocketCluster()
    {
        
    }
    
}
