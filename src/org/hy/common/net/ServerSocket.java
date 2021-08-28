package org.hy.common.net;

import java.util.Hashtable;
import java.util.Map;

import org.hy.common.Date;
import org.hy.common.Execute;
import org.hy.common.Help;
import org.hy.common.xml.log.Logger;





/**
 * Socket服务端。
 * 服务端的主端口号。默认值：1721。
 * 
 * 1721主端口只用于登陆验证，登陆成功后会创建新的临时的通讯端号。
 * 通讯端口才是真正用于数据传输的端号。
 * 这样才能保证主端口上的高频访问时，也不会出现拥塞的瓶颈。
 * 
 * 默认情况下：登陆时，没有什么验证，如验证用户名及密码。
 *           此验证动作通过 org.hy.common.net.ServerSocketValidate 接口提供给外界来实现登陆验证。
 * 
 * 外界可通过添加数据通讯的监听者 org.hy.common.net.CommunicationListener 来实现定制化功能。
 *
 * @author      ZhengWei(HY)
 * @createDate  2017-01-12
 * @version     v1.0
 *              v2.0  2021-08-26  添加： 端口池、打开的端口不再关闭、每次的数据通讯均要票据
 */
public class ServerSocket extends ServerBase
{
    private static final Logger $Logger = new Logger(ServerSocket.class);
    
    /** Socket连接端口池 */
    private PortPool                                       communicationPortPool;
    
    /** 数据端口与数据端口服务的关系 */
    private Map<Integer ,ServerBase>                       communicationServers;
    
    /** 定时关闭超时的服务的时长（单位：秒）。默认为：60秒 */
    private int                                            closeTimeout;
    
    /** 是否启动定时关闭超时服务的线程 */
    private boolean                                        isStartCheckCloseTimeout;
    
    /** 登陆验证接口。当为 null 时不验证，直接登陆成功 */
    private ServerSocketValidate                           validate;
    
    /**
     * 数据通讯事件的监听者集合。
     * 
     * 同样的事件类型，只能有一个监听者。
     */
    private Map<String ,CommunicationListener>             listeners;
    
    /** 默认的数据通讯监听者 */
    private CommunicationListener                          defaultListener;
    
    
    
    public ServerSocket()
    {
        this(1721);
    }
    
    
    
    public ServerSocket(int i_Port)
    {
        this(i_Port ,17000 ,17999);
    }
    
    
    
    public ServerSocket(int i_Port ,int i_MinPort ,int i_MaxPort)
    {
        super(i_Port);
        
        this.communicationPortPool = new PortPool();
        this.communicationPortPool.setMinPort(i_MinPort);
        this.communicationPortPool.setMaxPort(i_MaxPort);
        
        this.closeTimeout             = 60;
        this.isStartCheckCloseTimeout = false;
        this.communicationServers     = new Hashtable<Integer ,ServerBase>();
        this.request                  = new ServerSocketListener(this);
        this.validate                 = null;
        this.listeners                = new Hashtable<String ,CommunicationListener>();
        this.defaultListener          = new XJavaCommunicationListener();
    }
    
    
    
    /**
     * 创建一个用于数据通讯的临时的新的端口服务
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-01-14
     * @version     v1.0
     *
     * @param i_SocketRepuest  接收到请求时的处理类
     * @return                 异常时或无可分配的端口时，返回null
     */
    public ServerBase createCommunicationServer()
    {
        return createCommunicationServer(new ServerSocketCommunication(this));
    }
    
    
    
    /**
     * 创建一个用于数据通讯的临时的新的端口服务
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-01-14
     * @version     v1.0
     *
     * @param i_SocketRepuest  接收到请求时的处理类
     * @return                 异常时或无可分配的端口时，返回null
     */
    public ServerBase createCommunicationServer(SocketRepuest i_SocketRepuest)
    {
        $Logger.debug("ServerSocket：Create communication find next Port.");
        ServerBase v_CommunicationServer = this.getIdleCommunicationServer(i_SocketRepuest);
        
        v_CommunicationServer.setOpenTime(new Date());
        this.checkCloseTimeoutStart();
            
        $Logger.debug("ServerSocket：Create communication port " + v_CommunicationServer.getPort() + ".");
        return v_CommunicationServer;
    }
    
    
    
    /**
     * 获取下一个随机端口号
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-01-12
     * @version     v1.0
     *              v2.0  2019-12-28  优化：不再使用 Help.isOpen() 方法判定端口是否打开？
     *                                       而是直接尝试绑定端口并返回ServerSocket
     *
     * @return
     */
    private ServerBase getIdleCommunicationServer(SocketRepuest i_SocketRepuest)
    {
        Integer    v_CommunicationPort   = this.communicationPortPool.getIdle();
        ServerBase v_CommunicationServer = this.communicationServers.get(v_CommunicationPort);
        
        // 创建一个新的用于数据通讯的临时的服务端Socket监听服务
        if ( v_CommunicationServer == null )
        {
            java.net.ServerSocket v_ServerSocket = Help.getServerSocket(v_CommunicationPort ,true);
            if ( v_ServerSocket == null )
            {
                return this.getIdleCommunicationServer(i_SocketRepuest);
            }
            
            v_CommunicationServer = new ServerBase(v_ServerSocket ,this.communicationPortPool);
            v_CommunicationServer.setRequest(i_SocketRepuest);
            v_CommunicationServer.setAcceptIsThread(false);
            
            if ( !v_CommunicationServer.open() )
            {
                return this.getIdleCommunicationServer(i_SocketRepuest);
            }
            
            synchronized (this)
            {
                this.communicationServers.put(v_CommunicationPort ,v_CommunicationServer);
            }
        }
        
        return v_CommunicationServer;
    }
    
    
    
    /**
     * 启动，定时关闭超时服务的线程
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-01-14
     * @version     v1.0
     *
     */
    private synchronized void checkCloseTimeoutStart()
    {
        if ( this.isStartCheckCloseTimeout )
        {
            return;
        }
        
        this.isStartCheckCloseTimeout = true;
        new Execute(this ,"checkCloseTimeout").startDelayed((this.closeTimeout / 10) * 1000);
    }
    
    
    
    /**
     * 定时关闭超时服务的方法
     * 
     * 注：请勿直接调用本方法，本方法将在 checkCloseTimeoutStart() 方法内被另一个线程启动执行（所以本方法是public的）
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-01-14
     * @version     v1.0
     *
     */
    public void checkCloseTimeout()
    {
        $Logger.debug("启动：Net通讯超时的关闭服务（" + this.getPort() + "）");
        
        int v_WorkCount = 0;
        do
        {
            Date v_Now  = new Date();
            v_WorkCount = 0;
            
            for (ServerBase v_CS : communicationServers.values())
            {
                if ( v_CS != null )
                {
                    if ( !v_CS.isIdle() )
                    {
                        if ( v_Now.getTime() - v_CS.getOpenTime().getTime() >= this.closeTimeout * 1000 )
                        {
                            $Logger.debug("ServerSocket：Port " + v_CS.getPort() + " is timeout close.");
                            v_CS.toIdle();
                        }
                        else
                        {
                            v_WorkCount++;
                        }
                    }
                }
            }
            
            try
            {
                Thread.sleep((this.closeTimeout / 10) * 1000);
            }
            catch (Exception exce)
            {
                // Nothing.
            }
            
            $Logger.debug("探测：Net通讯超时（" + this.getPort() + "），尚有" + v_WorkCount + "个通讯");
        }
        while ( v_WorkCount >= 1 );
        
        $Logger.debug("结束：Net通讯超时的关闭服务（" + this.getPort() + "）");
        this.isStartCheckCloseTimeout = false;
    }
    
    
    
    /**
     * 默认的数据通讯监听者。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-01-16
     * @version     v1.0
     *
     * @return
     */
    public CommunicationListener getDefaultListener()
    {
        return this.defaultListener;
    }
    
    
    
    /**
     * 添加数据通讯的事件监听者
     * 
     * 配合XJava的XML文件配置
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-01-16
     * @version     v1.0
     *
     * @param i_Listener
     */
    public void setListener(CommunicationListener i_Listener)
    {
        this.addListener(i_Listener);
    }
    
    
    
    /**
     * 添加数据通讯的事件监听者
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-01-16
     * @version     v1.0
     *
     * @param i_Listener
     */
    public void addListener(CommunicationListener i_Listener)
    {
        if ( i_Listener == null )
        {
            throw new NullPointerException("CommunicationListener is null.");
        }
        
        if ( Help.isNull(i_Listener.getEventType()) )
        {
            throw new NullPointerException("CommunicationListener.getEventType() is null.");
        }
        
        this.listeners.put(i_Listener.getEventType() ,i_Listener);
    }
    
    
    
    /**
     * 获取事件类型的数据通讯的事件监听者
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-01-16
     * @version     v1.0
     *
     * @param i_EventType  事件类型（区分大小写）
     * @return
     */
    public CommunicationListener getListeners(String i_EventType)
    {
        return this.listeners.get(i_EventType);
    }
    
    
    
    /**
     * 移除数据通讯的事件监听者
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-01-16
     * @version     v1.0
     *
     * @param i_Listener
     */
    public void removeListener(CommunicationListener i_Listener)
    {
        if ( i_Listener == null )
        {
            throw new NullPointerException("CommunicationListener is null.");
        }
        
        if ( Help.isNull(i_Listener.getEventType()) )
        {
            throw new NullPointerException("CommunicationListener.getEventType() is null.");
        }
        
        this.listeners.remove(i_Listener.getEventType());
    }
    
    
    
    /**
     * 移除数据通讯的事件监听者
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-01-16
     * @version     v1.0
     *
     * @param i_Listener
     */
    public void removeListener(String i_EventType)
    {
        if ( Help.isNull(i_EventType) )
        {
            throw new NullPointerException("EventType is null.");
        }
        
        this.listeners.remove(i_EventType);
    }
    
    
    
    /**
     * 清空所有数据通讯的监听者
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-01-16
     * @version     v1.0
     *
     */
    public void clearListeners()
    {
        this.listeners.clear();
    }
    
    
    
    /**
     * 关闭服务端端口的监听服务
     * 同时，也试图关闭所有创建出的临时用于通讯的监听服务。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-01-15
     * @version     v1.0
     */
    @Override
    public synchronized void close()
    {
        if ( !Help.isNull(this.communicationServers) )
        {
            for (ServerBase v_CommunicationServer : this.communicationServers.values())
            {
                v_CommunicationServer.close();
            }
        }
        
        super.close();
    }
    
    
    
    /**
     * 获取：服务端的主端口号。默认值：1721
     */
    @Override
    public int getPort()
    {
        return super.getPort();
    }


    
    /**
     * 设置：服务端的主端口号。默认值：1721
     * 
     * @param port
     */
    @Override
    public void setPort(int i_Port)
    {
        super.setPort(i_Port);
    }


    
    /**
     * 获取：服务端随机创建端口范围的最小端口号（可等于此属性值）。默认值：17000
     */
    public int getMinPort()
    {
        return this.communicationPortPool.getMinPort();
    }


    
    /**
     * 设置：服务端随机创建端口范围的最小端口号（可等于此属性值）。默认值：17000
     * 
     * @param minPort
     */
    public void setMinPort(int minPort)
    {
        this.communicationPortPool.setMinPort(minPort);
    }


    
    /**
     * 获取：服务端随机创建端口范围的最大端口号（可等于此属性值）。默认值：17999
     */
    public int getMaxPort()
    {
        return this.communicationPortPool.getMaxPort();
    }


    
    /**
     * 设置：服务端随机创建端口范围的最大端口号（可等于此属性值）。默认值：17999
     * 
     * @param maxPort
     */
    public void setMaxPort(int maxPort)
    {
        this.communicationPortPool.setMaxPort(maxPort);
    }


    
    /**
     * 获取：定时关闭超时的服务的时长（单位：秒）。默认为：60秒
     */
    public int getCloseTimeout()
    {
        return closeTimeout;
    }


    
    /**
     * 设置：定时关闭超时的服务的时长（单位：秒）。默认为：60秒
     * 
     * @param closeTimeout
     */
    public void setCloseTimeout(int closeTimeout)
    {
        this.closeTimeout = closeTimeout;
    }


    
    /**
     * 获取：登陆验证接口。当为 null 时不验证，直接登陆成功
     */
    public ServerSocketValidate getValidate()
    {
        return validate;
    }


    
    /**
     * 设置：登陆验证接口。当为 null 时不验证，直接登陆成功
     * 
     * @param validate
     */
    public ServerSocket setValidate(ServerSocketValidate validate)
    {
        this.validate = validate;
        return this;
    }
    
}
