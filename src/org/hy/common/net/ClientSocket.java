package org.hy.common.net;

import org.hy.common.Date;
import org.hy.common.Help;
import org.hy.common.net.common.ClientCluster;
import org.hy.common.net.common.ClientOperation;
import org.hy.common.net.common.NetError;
import org.hy.common.net.data.ClientTotal;
import org.hy.common.net.data.Command;
import org.hy.common.net.data.Communication;
import org.hy.common.net.data.CommunicationRequest;
import org.hy.common.net.data.CommunicationResponse;
import org.hy.common.net.data.LoginRequest;
import org.hy.common.net.data.LoginResponse;
import org.hy.common.net.data.NetException;
import org.hy.common.net.data.SessionInfo;
import org.hy.common.net.socket.ClientCommunication;
import org.hy.common.net.socket.ClientSocketValidate;
import org.hy.common.net.socket.ObjectSocketResponse;
import org.hy.common.xml.log.Logger;





/**
 * Socket的客户端
 * 
 * 服务端的主端口号。默认值：1721
 * 
 * 1721主端口只用于登陆验证，登陆成功后会创建新的临时的通讯端号。
 * 通讯端口才是真正用于数据传输的端号。
 * 这样才能保证主端口上的高频访问时，也不会出现拥塞的瓶颈。
 * 
 * 当服务端要求登陆验证时，可通过 org.hy.common.net.ClientSocketValidate 接口提供登陆信息。
 * 当服务端不要求登陆验证时，可不实现上面的接口。
 *
 * @author      ZhengWei(HY)
 * @createDate  2017-01-14
 * @version     v1.0
 *              v2.0  2017-02-07  添加：查询多个对象getObjects(...);
 *              v3.0  2017-02-28  添加：获取服务端的Java.getSessionMap()数据
 *              v4.0  2019-02-27  添加：服务端是否返回执行结果的数据。
 *              v5.0  2021-08-26  添加：端口池、打开的端口不再关闭、每次的数据通讯均要票据
 */
public class ClientSocket extends ObjectSocketResponse<ClientSocket> implements ClientOperation ,ClientCluster
{
    private static final long serialVersionUID = -2175532426747739743L;

    private static final Logger $Logger = new Logger(ClientSocket.class);
    
    /** 登陆服务端的登陆信息接口。当服务端不要求登陆验证时，此属性可为null */
    private ClientSocketValidate validate;
    
    /** 通讯连接的统计信息 */
    private SessionInfo          session;
    
    /** 会话时间（单位：秒）。空闲多少时间后，移除统计 */
    protected long               sessionTime;
    
    
    
    public ClientSocket()
    {
        this("127.0.0.1" ,1721);
    }
    
    
    
    public ClientSocket(String i_HostName)
    {
        this(i_HostName ,1721);
    }
    
    
    
    public ClientSocket(String i_HostName ,int i_Port)
    {
        super(i_HostName ,i_Port);
        
        this.validate    = null;
        this.session     = new SessionInfo();
        this.sessionTime = 60L * 60L;
    }
    
    
    
    /**
     * 获取客户端的业务操作接口
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-10-12
     * @version     v1.0
     * 
     * @return
     */
    @Override
    public ClientOperation operation()
    {
        return this;
    }
    
    
    
    /**
     * 获取客户端的主机地址
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-10-12
     * @version     v1.0
     * 
     * @return
     */
    @Override
    public String getHost()
    {
        return super.getHostName();
    }
    
    
    
    /**
     * 获取客户端的主机端口
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-10-12
     * @version     v1.0
     * 
     * @return
     */
    @Override
    public int getPort()
    {
        return super.getPort();
    }
    
    
    
    /**
     * 启动服务
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-12-06
     * @version     v1.0
     */
    @Override
    public boolean startServer()
    {
        return true;
    }



    /**
     * 关闭服务
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-12-06
     * @version     v1.0
     */
    @Override
    public boolean shutdownServer()
    {
        return true;
    }
    
    
    
    /**
     * 是否启动服务
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-12-06
     * @version     v1.0
     * 
     * @return
     */
    @Override
    public boolean isStartServer()
    {
        return true;
    }
    
    
    
    /**
     * 是否登录成功
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-10-12
     * @version     v1.0
     * 
     * @return
     */
    @Override
    public boolean isLogin()
    {
        return true;
    }


    
    /**
     * 登录操作
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-10-12
     * @version     v1.0
     * 
     * @param i_Request  登录信息。必填参数[userName ,systemName]
     * @return
     */
    @Override
    public LoginResponse login(LoginRequest i_Request)
    {
        this.session.setSystemName(i_Request.getSystemName());
        this.session.setUserName(  i_Request.getUserName());
        this.session.setHost(this.getHost());
        this.session.setPort(this.getPort());
        this.session.setLoginTime(new Date());
        this.session.setOnline(true);
        ClientTotal.put(this);
        return new LoginResponse();
    }
    
    
    
    /**
     * 登出操作。一般为异常时，内部自动调用使用
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-10-19
     * @version     v1.0
     */
    @Override
    public boolean logout()
    {
        this.session.setLogoutTime(new Date());
        this.session.setOnline(false);
        return true;
    }
    
    
    
    /**
     * 获取：会话时间（单位：秒）。空闲多少时间后，移除统计
     * 
     * @author      ZhengWei(HY)
     * @createDate  2022-01-07
     * @version     v1.0
     * 
     * @return
     */
    @Override
    public long getSessionTime()
    {
        return sessionTime;
    }



    /**
     * 设置：会话时间（单位：秒）。空闲多少时间后，移除统计
     * 
     * @author      ZhengWei(HY)
     * @createDate  2022-01-07
     * @version     v1.0
     * 
     * @return
     */
    public void setSessionTime(long sessionTime)
    {
        this.sessionTime = sessionTime;
    }
    
    
    
    /**
     * 原本ClientOperation实例是被ClientCluster接口引用的。
     * 
     * 本方法是反向引用本类的ClientCluster实例。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-10-19
     * @version     v1.0
     * 
     * @return
     */
    @Override
    public ClientCluster getClient()
    {
        return this;
    }
    
    
    
    /**
     * 向服务端发送执行命令
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-01-17
     * @version     v1.0
     *              v2.0  2021-12-15  添加：超时时长
     *
     * @param i_Timeout        通讯超时时长(单位：毫秒)。0：表示永不超时，一直等待； 负数：表示取默认超时时长
     * @param i_XID            XJava对象池的ID
     * @param i_Command        执行命令名称（即方法名称）
     * @return
     */
    @Override
    public CommunicationResponse sendCommand(long i_Timeout ,String i_XID ,String i_Command)
    {
        return this.sendCommand(i_Timeout ,i_XID ,i_Command ,new Object[]{} ,true);
    }
    
    
    
    /**
     * 向服务端发送执行命令
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-01-17
     * @version     v1.0
     *              v2.0  2021-12-15  添加：超时时长
     *
     * @param i_Timeout        通讯超时时长(单位：毫秒)。0：表示永不超时，一直等待； 负数：表示取默认超时时长
     * @param i_XID            XJava对象池的ID
     * @param i_Command        执行命令名称（即方法名称）
     * @param i_ServerIsReturn 服务端是否返回执行结果的数据
     * @return
     */
    @Override
    public CommunicationResponse sendCommand(long i_Timeout ,String i_XID ,String i_Command ,boolean i_ServerIsReturn)
    {
        return this.sendCommand(i_Timeout ,i_XID ,i_Command ,new Object[]{} ,i_ServerIsReturn);
    }
    
    
    
    /**
     * 向服务端发送执行命令
     * 
     * @author      ZhengWei(HY)
     * @createDate  2022-01-10
     * @version     v1.0
     *
     * @param i_Timeout          通讯超时时长(单位：毫秒)。0：表示永不超时，一直等待； 负数：表示取默认超时时长
     * @param i_XID              XJava对象池的ID
     * @param i_Command          执行命令名称（即方法名称）
     * @param i_ServerIsReturn   服务端是否返回执行结果的数据
     * @param i_ServerIsNonSync  服务端是否开启异步执行
     * @return
     */
    @Override
    public CommunicationResponse sendCommand(long i_Timeout ,String i_XID ,String i_Command ,boolean i_ServerIsReturn ,boolean i_ServerIsNonSync)
    {
        return this.sendCommand(i_Timeout ,i_XID ,i_Command ,new Object[]{} ,i_ServerIsReturn ,i_ServerIsNonSync);
    }
    
    
    
    /**
     * 向服务端发送执行命令
     * 
     * @author      ZhengWei(HY)
     * @createDate  2019-02-27
     * @version     v1.0
     *              v2.0  2021-12-15  添加：超时时长
     *
     * @param i_Timeout        通讯超时时长(单位：毫秒)。0：表示永不超时，一直等待； 负数：表示取默认超时时长
     * @param i_XID            XJava对象池的ID
     * @param i_Command        执行命令名称（即方法名称）
     * @param i_CommandParams  执行命令参数（即方法参数）
     * @return
     */
    @Override
    public CommunicationResponse sendCommand(long i_Timeout ,String i_XID ,String i_Command ,Object [] i_CommandParams)
    {
        return this.sendCommand(i_Timeout ,i_XID ,i_Command ,i_CommandParams ,true);
    }
    
    
    
    /**
     * 向服务端发送执行命令
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-01-17
     * @version     v1.0
     *              v2.0  2019-02-27  添加：服务端是否返回执行结果的数据
     *              v3.0  2021-12-15  添加：超时时长
     *
     * @param i_Timeout        通讯超时时长(单位：毫秒)。0：表示永不超时，一直等待； 负数：表示取默认超时时长
     * @param i_XID            XJava对象池的ID
     * @param i_Command        执行命令名称（即方法名称）
     * @param i_CommandParams  执行命令参数（即方法参数）
     * @param i_ServerIsReturn 服务端是否返回执行结果的数据
     * @return
     */
    @Override
    public CommunicationResponse sendCommand(long i_Timeout ,String i_XID ,String i_Command ,Object [] i_CommandParams ,boolean i_ServerIsReturn)
    {
        return this.sendCommand(i_Timeout ,i_XID ,i_Command ,i_CommandParams ,true ,false);
    }
    
    
    
    /**
     * 向服务端发送执行命令
     * 
     * @author      ZhengWei(HY)
     * @createDate  2022-01-10
     *
     * @param i_Timeout          通讯超时时长(单位：毫秒)。0：表示永不超时，一直等待； 负数：表示取默认超时时长
     * @param i_XID              XJava对象池的ID
     * @param i_Command          执行命令名称（即方法名称）
     * @param i_CommandParams    执行命令参数（即方法参数）
     * @param i_ServerIsReturn   服务端是否返回执行结果的数据
     * @param i_ServerIsNonSync  服务端是否开启异步执行
     * @return
     */
    @Override
    public CommunicationResponse sendCommand(long i_Timeout ,String i_XID ,String i_Command ,Object [] i_CommandParams ,boolean i_ServerIsReturn ,boolean i_ServerIsNonSync)
    {
        CommunicationRequest v_RequestData = new CommunicationRequest();
        Command              v_Command     = new Command();
        
        v_Command.setMethodName(i_Command);
        v_Command.setParams(    i_CommandParams);
        
        v_RequestData.setDataXID(           i_XID);
        v_RequestData.setData(              v_Command);
        v_RequestData.setDataOperation(     CommunicationRequest.$Operation_Command);
        v_RequestData.setRetunData(         i_ServerIsReturn);
        v_RequestData.setWaitRequestTimeout(i_Timeout);
        v_RequestData.setNonSync(           i_ServerIsNonSync);
        
        return this.send(v_RequestData);
    }
    
    
    
    /**
     * 移除服务端的对象（默认从XJava对象池中移除）。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2019-02-27
     * @version     v1.0
     *              v2.0  2021-12-15  添加：超时时长
     *
     * @param i_Timeout        通讯超时时长(单位：毫秒)。0：表示永不超时，一直等待； 负数：表示取默认超时时长
     * @param i_XID            XJava对象池的ID
     * @return
     */
    @Override
    public CommunicationResponse removeObject(long i_Timeout ,String i_XID )
    {
        return removeObject(i_Timeout ,i_XID ,true);
    }

    
    
    /**
     * 移除服务端的对象（默认从XJava对象池中移除）。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-01-23
     * @version     v1.0
     *              v2.0  2019-02-27  添加：服务端是否返回执行结果的数据
     *              v3.0  2021-12-15  添加：超时时长
     *
     * @param i_Timeout        通讯超时时长(单位：毫秒)。0：表示永不超时，一直等待； 负数：表示取默认超时时长
     * @param i_XID            XJava对象池的ID
     * @param i_ServerIsReturn 服务端是否返回执行结果的数据
     * @return
     */
    @Override
    public CommunicationResponse removeObject(long i_Timeout ,String i_XID ,boolean i_ServerIsReturn)
    {
        CommunicationRequest v_RequestData = new CommunicationRequest();
        
        v_RequestData.setDataXID(      i_XID);
        v_RequestData.setDataOperation(CommunicationRequest.$Operation_Delete);
        v_RequestData.setRetunData(    i_ServerIsReturn);
        
        return this.send(v_RequestData);
    }
    
    
    
    /**
     * 获取服务端的对象（默认从XJava对象池中获取）。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-01-23
     * @version     v1.0
     *              v2.0  2021-12-15  添加：超时时长
     *
     * @param i_Timeout        通讯超时时长(单位：毫秒)。0：表示永不超时，一直等待； 负数：表示取默认超时时长
     * @param i_XID            XJava对象池的ID
     * @return
     */
    @Override
    public CommunicationResponse getObject(long i_Timeout ,String i_XID)
    {
        CommunicationRequest v_RequestData = new CommunicationRequest();
        
        v_RequestData.setDataXID(           i_XID);
        v_RequestData.setDataOperation(     CommunicationRequest.$Operation_Select);
        v_RequestData.setWaitRequestTimeout(i_Timeout);
        
        return this.send(v_RequestData);
    }
    
    
    
    /**
     * 获取服务端的多个对象（默认从XJava对象池中获取）。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-02-07
     * @version     v1.0
     *              v2.0  2021-12-15  添加：超时时长
     *
     * @param i_Timeout        通讯超时时长(单位：毫秒)。0：表示永不超时，一直等待； 负数：表示取默认超时时长
     * @param i_XID            XJava对象池的ID标识符的前缀(区分大小写)
     * @return                 CommunicationResponse.getData() 是一个List<CommunicationResponse>()结构的实例。
     */
    @Override
    public CommunicationResponse getObjects(long i_Timeout ,String i_XID)
    {
        CommunicationRequest v_RequestData = new CommunicationRequest();
        
        v_RequestData.setDataXID(           i_XID);
        v_RequestData.setDataOperation(     CommunicationRequest.$Operation_Selects);
        v_RequestData.setWaitRequestTimeout(i_Timeout);
        
        return this.send(v_RequestData);
    }
    
    
    
    /**
     * 获取服务端的Java.getSessionMap()数据。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-02-28
     * @version     v1.0
     *              v2.0  2021-12-15  添加：超时时长
     *
     * @param i_Timeout        通讯超时时长(单位：毫秒)。0：表示永不超时，一直等待； 负数：表示取默认超时时长
     * @return                 CommunicationResponse.getData() 是一个ExpireMap<String ,Object>()结构的实例。
     */
    @Override
    public CommunicationResponse getSessionMap(long i_Timeout)
    {
        CommunicationRequest v_RequestData = new CommunicationRequest();
        
        v_RequestData.setDataOperation(CommunicationRequest.$Operation_SessionMap);
        v_RequestData.setWaitRequestTimeout(i_Timeout);
        
        return this.send(v_RequestData);
    }
    
    
    
    /**
     * 向服务端中写入或更新对象（默认将写入XJava对象池）。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-01-23
     * @version     v1.0
     *              v2.0  2021-12-15  添加：超时时长
     *
     * @param i_Timeout        通讯超时时长(单位：毫秒)。0：表示永不超时，一直等待； 负数：表示取默认超时时长
     * @param i_XID            XJava对象池的ID
     * @param i_Data           XJava对象
     * @return
     */
    @Override
    public CommunicationResponse sendObject(long i_Timeout ,String i_XID ,Object i_Data)
    {
        return this.sendObject(i_Timeout ,i_XID ,i_Data ,0 ,true);
    }
    
    
    
    /**
     * 向服务端中写入或更新对象（默认将写入XJava对象池）。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2019-02-27
     * @version     v1.0
     *              v2.0  2021-12-15  添加：超时时长
     *
     * @param i_Timeout        通讯超时时长(单位：毫秒)。0：表示永不超时，一直等待； 负数：表示取默认超时时长
     * @param i_XID            XJava对象池的ID
     * @param i_Data           XJava对象
     * @param i_ServerIsReturn 服务端是否返回执行结果的数据
     * @return
     */
    @Override
    public CommunicationResponse sendObject(long i_Timeout ,String i_XID ,Object i_Data ,boolean i_ServerIsReturn)
    {
        return this.sendObject(i_Timeout ,i_XID ,i_Data ,0 ,i_ServerIsReturn);
    }
    
    
    
    /**
     * 向服务端中写入或更新对象（默认将写入XJava对象池）。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2019-02-27
     * @version     v1.0
     *              v2.0  2021-12-15  添加：超时时长
     *
     * @param i_Timeout        通讯超时时长(单位：毫秒)。0：表示永不超时，一直等待； 负数：表示取默认超时时长
     * @param i_XID            XJava对象池的ID
     * @param i_Data           XJava对象
     * @param i_ExpireTimeLen  数据的过期时长(单位：秒)
     * @return
     */
    @Override
    public CommunicationResponse sendObject(long i_Timeout ,String i_XID ,Object i_Data ,long i_ExpireTimeLen)
    {
        return this.sendObject(i_Timeout ,i_XID ,i_Data ,i_ExpireTimeLen ,true);
    }
    
    
    
    /**
     * 向服务端中写入或更新对象（默认将写入XJava对象池）。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-01-23
     * @version     v1.0
     *              v2.0  2019-02-27  添加：服务端是否返回执行结果的数据
     *              v3.0  2021-12-15  添加：超时时长
     *
     * @param i_Timeout        通讯超时时长(单位：毫秒)。0：表示永不超时，一直等待； 负数：表示取默认超时时长
     * @param i_XID            XJava对象池的ID
     * @param i_Data           XJava对象
     * @param i_ExpireTimeLen  数据的过期时长(单位：秒)
     * @param i_ServerIsReturn 服务端是否返回执行结果的数据
     * @return
     */
    @Override
    public CommunicationResponse sendObject(long i_Timeout ,String i_XID ,Object i_Data ,long i_ExpireTimeLen ,boolean i_ServerIsReturn)
    {
        CommunicationRequest v_RequestData = new CommunicationRequest();
        
        v_RequestData.setDataXID(           i_XID);
        v_RequestData.setData(              i_Data);
        v_RequestData.setDataExpireTimeLen( i_ExpireTimeLen);
        v_RequestData.setDataOperation(     CommunicationRequest.$Operation_Update);
        v_RequestData.setRetunData(         i_ServerIsReturn);
        v_RequestData.setWaitRequestTimeout(i_Timeout);
        
        return this.send(v_RequestData);
    }
    
    
    
    /**
     * 向服务端发送数据
     * 
     * 重写发送方法。
     *   将其分为二个步骤完成：
     *     步骤1. 先登陆，并获取服务端新分配的用于通讯的端口号。
     *     步骤2. 用新的通讯端口发送真正要发送的数据。
     *
     * @author      ZhengWei(HY)
     * @createDate  2017-01-14
     * @version     v1.0
     *
     * @param i_RequestData  请求发送给服务端的数据
     * @return               由本类来保证：任何情况下，返回值都不会为null
     */
    @Override
    public CommunicationResponse send(CommunicationRequest i_RequestData)
    {
        Date         v_StartTime   = new Date();
        LoginRequest v_RequestData = null;
        
        // 要求登陆验证
        if ( this.validate != null )
        {
            v_RequestData = this.validate.getLoginRequest();
            
            if ( v_RequestData == null
              || Help.isNull(v_RequestData.getUserName())
              || v_RequestData.getPassword() == null )
            {
                CommunicationResponse v_Communication = new CommunicationResponse();
                v_Communication.setResult(   NetError.$Server_LoginValidateError);
                v_Communication.setStartTime(v_StartTime);
                v_Communication.setEndTime(  new Date());
                return v_Communication;
            }
        }
        // 未要求登陆验证，但登陆验证的动作不可少
        else
        {
            v_RequestData = new LoginRequest();
        }
        
        // 执行登陆验证
        LoginResponse v_ResponseData = (LoginResponse)super.send(v_RequestData);
        
        if ( v_ResponseData == null )
        {
            CommunicationResponse v_Communication = new CommunicationResponse();
            v_Communication.setResult(   NetError.$Server_LoginError);
            v_Communication.setStartTime(v_StartTime);
            v_Communication.setEndTime(  new Date());
            return v_Communication;
        }
        else
        {
            this.session.setSystemName(Help.NVL(v_RequestData.getSystemName() ,"-"));
            this.session.setUserName(  Help.NVL(v_RequestData.getUserName()   ,"-"));
            this.session.setHost(this.getHost());
            this.session.setPort(this.getPort());
            this.session.setLoginTime(v_StartTime);
            this.session.setOnline(true);
        }
        
        $Logger.debug("数据通讯端口：" + v_ResponseData.getPort());
        this.session.addRequestCount();
        this.session.setActiveTime(v_StartTime);
        ClientTotal.put(this);  // 保持会话的有效性
        
        // 登陆验证成功后，进行数据通讯
        i_RequestData.setTime(new Date());
        i_RequestData.setToken(v_ResponseData.getToken());
        ClientCommunication   v_ClientCommunication = new ClientCommunication(this.host ,v_ResponseData.getPort());
        CommunicationResponse v_Communication       = (CommunicationResponse)v_ClientCommunication.send(i_RequestData);
        
        if ( v_Communication == null )
        {
            v_Communication = new CommunicationResponse();
            v_Communication.setResult(NetError.$Client_RequestDataError);
        }
        
        Date v_ETime = new Date();
        if ( v_Communication.getResult() == Communication.$Succeed )
        {
            this.session.setActiveTime(v_ETime);
            this.session.addActiveCount();
            this.session.addActiveTimeLen(v_ETime.getTime() - v_StartTime.getTime());
        }
        else
        {
            this.session.addException(new NetException(i_RequestData ,v_Communication.getResult() ,"通讯失败" ,null));
        }
        this.session.setLogoutTime(v_ETime);
        this.session.setOnline(false);
        
        v_Communication.setStartTime(v_StartTime);
        v_Communication.setEndTime(  new Date());
        return v_Communication;
    }
    
    
    
    /**
     * 客户端接收到服务端的响应消息时执行的动作。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-01-14
     * @version     v1.0
     *
     * @param i_RequestData   将服务端发送的对象
     * @param i_ResponseData  接收服务端响应的对象
     * @return                实际处理类返回的对象
     */
    @Override
    public Object response(Object i_RequestData ,Object i_ResponseData)
    {
        if ( !(i_ResponseData instanceof LoginResponse) )
        {
            return null;
        }
        
        LoginResponse v_ResponseData = (LoginResponse)i_ResponseData;
        if ( v_ResponseData.getPort() <= 0 )
        {
            return null;
        }
        
        return v_ResponseData;
    }
    
    
    
    /**
     * 获取：登陆服务端的登陆信息接口。当服务端不要求登陆验证时，此属性可为null
     */
    public ClientSocketValidate getValidate()
    {
        return validate;
    }


    
    /**
     * 设置：登陆服务端的登陆信息接口。当服务端不要求登陆验证时，此属性可为null
     * 
     * @param validate
     */
    public ClientSocket setValidate(ClientSocketValidate validate)
    {
        this.validate = validate;
        return this;
    }
    
    
    /**
     * 获取：通讯连接的会话信息
     * 
     * @author      ZhengWei(HY)
     * @createDate  2022-01-07
     * @version     v1.0
     * 
     * @return
     */
    @Override
    public SessionInfo getSession()
    {
        return this.session;
    }
    
}
