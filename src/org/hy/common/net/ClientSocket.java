package org.hy.common.net;

import org.hy.common.Date;
import org.hy.common.Help;
import org.hy.common.net.common.ClientCluster;
import org.hy.common.net.common.ClientOperation;
import org.hy.common.net.common.NetError;
import org.hy.common.net.data.Command;
import org.hy.common.net.data.CommunicationRequest;
import org.hy.common.net.data.CommunicationResponse;
import org.hy.common.net.data.LoginRequest;
import org.hy.common.net.data.LoginResponse;
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
    private static final Logger $Logger = new Logger(ClientSocket.class);
    
    /** 登陆服务端的登陆信息接口。当服务端不要求登陆验证时，此属性可为null */
    private ClientSocketValidate validate;
    
    
    
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
        
        this.validate = null;
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
        return new LoginResponse();
    }
    
    
    
    /**
     * 向服务端发送执行命令
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-01-17
     * @version     v1.0
     *
     * @param i_XID            XJava对象池的ID
     * @param i_Command        执行命令名称（即方法名称）
     * @return
     */
    @Override
    public CommunicationResponse sendCommand(String i_XID ,String i_Command)
    {
        return this.sendCommand(i_XID ,i_Command ,new Object[]{} ,true);
    }
    
    
    
    /**
     * 向服务端发送执行命令
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-01-17
     * @version     v1.0
     *
     * @param i_XID            XJava对象池的ID
     * @param i_Command        执行命令名称（即方法名称）
     * @param i_ServerIsReturn 服务端是否返回执行结果的数据
     * @return
     */
    @Override
    public CommunicationResponse sendCommand(String i_XID ,String i_Command ,boolean i_ServerIsReturn)
    {
        return this.sendCommand(i_XID ,i_Command ,new Object[]{} ,i_ServerIsReturn);
    }
    
    
    
    /**
     * 向服务端发送执行命令
     * 
     * @author      ZhengWei(HY)
     * @createDate  2019-02-27
     * @version     v1.0
     *
     * @param i_XID            XJava对象池的ID
     * @param i_Command        执行命令名称（即方法名称）
     * @param i_CommandParams  执行命令参数（即方法参数）
     * @return
     */
    @Override
    public CommunicationResponse sendCommand(String i_XID ,String i_Command ,Object [] i_CommandParams)
    {
        return this.sendCommand(i_XID ,i_Command ,i_CommandParams ,true);
    }
    
    
    
    /**
     * 向服务端发送执行命令
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-01-17
     * @version     v1.0
     *              v2.0  2019-02-27  添加：服务端是否返回执行结果的数据
     *
     * @param i_XID            XJava对象池的ID
     * @param i_Command        执行命令名称（即方法名称）
     * @param i_CommandParams  执行命令参数（即方法参数）
     * @param i_ServerIsReturn 服务端是否返回执行结果的数据
     * @return
     */
    @Override
    public CommunicationResponse sendCommand(String i_XID ,String i_Command ,Object [] i_CommandParams ,boolean i_ServerIsReturn)
    {
        CommunicationRequest v_RequestData = new CommunicationRequest();
        Command              v_Command     = new Command();
        
        v_Command.setMethodName(i_Command);
        v_Command.setParams(    i_CommandParams);
        
        v_RequestData.setDataXID(      i_XID);
        v_RequestData.setData(         v_Command);
        v_RequestData.setDataOperation(CommunicationRequest.$Operation_Command);
        v_RequestData.setRetunData(    i_ServerIsReturn);
        
        return this.send(v_RequestData);
    }
    
    
    
    /**
     * 移除服务端的对象（默认从XJava对象池中移除）。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2019-02-27
     * @version     v1.0
     *
     * @param i_XID            XJava对象池的ID
     * @return
     */
    @Override
    public CommunicationResponse removeObject(String i_XID )
    {
        return removeObject(i_XID ,true);
    }

    
    
    /**
     * 移除服务端的对象（默认从XJava对象池中移除）。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-01-23
     * @version     v1.0
     *              v2.0  2019-02-27  添加：服务端是否返回执行结果的数据
     *
     * @param i_XID            XJava对象池的ID
     * @param i_ServerIsReturn 服务端是否返回执行结果的数据
     * @return
     */
    @Override
    public CommunicationResponse removeObject(String i_XID ,boolean i_ServerIsReturn)
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
     *
     * @param i_XID            XJava对象池的ID
     * @return
     */
    @Override
    public CommunicationResponse getObject(String i_XID)
    {
        CommunicationRequest v_RequestData = new CommunicationRequest();
        
        v_RequestData.setDataXID(      i_XID);
        v_RequestData.setDataOperation(CommunicationRequest.$Operation_Select);
        
        return this.send(v_RequestData);
    }
    
    
    
    /**
     * 获取服务端的多个对象（默认从XJava对象池中获取）。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-02-07
     * @version     v1.0
     *
     * @param i_XID  XJava对象池的ID标识符的前缀(区分大小写)
     * @return       CommunicationResponse.getData() 是一个List<CommunicationResponse>()结构的实例。
     */
    @Override
    public CommunicationResponse getObjects(String i_XID)
    {
        CommunicationRequest v_RequestData = new CommunicationRequest();
        
        v_RequestData.setDataXID(      i_XID);
        v_RequestData.setDataOperation(CommunicationRequest.$Operation_Selects);
        
        return this.send(v_RequestData);
    }
    
    
    
    /**
     * 获取服务端的Java.getSessionMap()数据。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-02-28
     * @version     v1.0
     *
     * @return       CommunicationResponse.getData() 是一个ExpireMap<String ,Object>()结构的实例。
     */
    @Override
    public CommunicationResponse getSessionMap()
    {
        CommunicationRequest v_RequestData = new CommunicationRequest();
        
        v_RequestData.setDataOperation(CommunicationRequest.$Operation_SessionMap);
        
        return this.send(v_RequestData);
    }
    
    
    
    /**
     * 向服务端中写入或更新对象（默认将写入XJava对象池）。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-01-23
     * @version     v1.0
     *
     * @param i_XID            XJava对象池的ID
     * @param i_Data           XJava对象
     * @return
     */
    @Override
    public CommunicationResponse sendObject(String i_XID ,Object i_Data)
    {
        return this.sendObject(i_XID ,i_Data ,0 ,true);
    }
    
    
    
    /**
     * 向服务端中写入或更新对象（默认将写入XJava对象池）。
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
    @Override
    public CommunicationResponse sendObject(String i_XID ,Object i_Data ,boolean i_ServerIsReturn)
    {
        return this.sendObject(i_XID ,i_Data ,0 ,i_ServerIsReturn);
    }
    
    
    
    /**
     * 向服务端中写入或更新对象（默认将写入XJava对象池）。
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
    @Override
    public CommunicationResponse sendObject(String i_XID ,Object i_Data ,long i_ExpireTimeLen)
    {
        return this.sendObject(i_XID ,i_Data ,i_ExpireTimeLen ,true);
    }
    
    
    
    /**
     * 向服务端中写入或更新对象（默认将写入XJava对象池）。
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
    @Override
    public CommunicationResponse sendObject(String i_XID ,Object i_Data ,long i_ExpireTimeLen ,boolean i_ServerIsReturn)
    {
        CommunicationRequest v_RequestData = new CommunicationRequest();
        
        v_RequestData.setDataXID(          i_XID);
        v_RequestData.setData(             i_Data);
        v_RequestData.setDataExpireTimeLen(i_ExpireTimeLen);
        v_RequestData.setDataOperation(    CommunicationRequest.$Operation_Update);
        v_RequestData.setRetunData(        i_ServerIsReturn);
        
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
                v_Communication.setResult(   NetError.$LoginValidateError);
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
            v_Communication.setResult(   NetError.$LoginError);
            v_Communication.setStartTime(v_StartTime);
            v_Communication.setEndTime(  new Date());
            return v_Communication;
        }
        
        $Logger.debug("数据通讯端口：" + v_ResponseData.getPort());
        
        // 登陆验证成功后，进行数据通讯
        i_RequestData.setTime(new Date());
        i_RequestData.setToken(v_ResponseData.getToken());
        ClientCommunication   v_ClientCommunication = new ClientCommunication(this.host ,v_ResponseData.getPort());
        CommunicationResponse v_Communication       = (CommunicationResponse)v_ClientCommunication.send(i_RequestData);
        
        if ( v_Communication == null )
        {
            v_Communication = new CommunicationResponse();
            v_Communication.setResult(NetError.$RequestDataError);
        }
        
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
        if ( i_ResponseData == null || !(i_ResponseData instanceof LoginResponse) )
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
    
}
