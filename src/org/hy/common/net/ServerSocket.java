package org.hy.common.net;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.hy.common.Date;
import org.hy.common.Execute;
import org.hy.common.Help;





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
 */
public class ServerSocket extends ServerBase
{
    
    /** 服务端随机创建端口范围的最小端口号（可等于此属性值）。默认值：17000 */
    private int                                            minPort;
    
    /** 服务端随机创建端口范围的最大端口号（可等于此属性值）。默认值：17999 */
    private int                                            maxPort;
    
    /** 当前分配到哪一个端口 */
    private int                                            indexPort;
    
    /** 
     * 服务端随机创建的临时的用于数据通讯的服务集合
     * 主要目的是：为了定时关闭超时的服务。 
     */
    private List<ServerBase>                               childServers;
    
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
        
        this.minPort                  = i_MinPort;
        this.maxPort                  = i_MaxPort;
        this.indexPort                = 0;
        this.request                  = new ServerSocketListener();
        this.childServers             = new ArrayList<ServerBase>();
        this.closeTimeout             = 60;
        this.isStartCheckCloseTimeout = false;
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
    public synchronized ServerBase createCommunicationServer()
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
    public synchronized ServerBase createCommunicationServer(SocketRepuest i_SocketRepuest)
    {
        java.net.ServerSocket v_ServerSocket = this.getNextPort();
        
        if ( v_ServerSocket == null )
        {
            return null;
        }
        
        // 创建一个新的用于数据通讯的临时的服务端Socket监听服务
        ServerBase v_CommunicationServer = new ServerBase(v_ServerSocket).setLog(this.isLog());
        v_CommunicationServer.setRequest(i_SocketRepuest);
        if ( v_CommunicationServer.open() )
        {
            this.log("ServerSocket：Create communication port " + v_CommunicationServer.getPort() + ".");
            this.childServers.add(v_CommunicationServer);
            this.checkCloseTimeoutStart();
            return v_CommunicationServer;
        }
        else
        {
            return null;
        }
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
        new Execute(this ,"checkCloseTimeout").startDelayed(this.closeTimeout * 1000);
    }
    
    
    
    /**
     * 定时关闭超时服务的方法
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-01-14
     * @version     v1.0
     *
     */
    public synchronized void checkCloseTimeout()
    {
        Date v_Now = new Date();
        
        for (int v_Index=this.childServers.size()-1; v_Index>=0; v_Index--)
        {
            ServerBase v_ChildServer = this.childServers.get(v_Index);
            
            if ( v_ChildServer != null )
            {
                if ( v_Now.getTime() - v_ChildServer.getOpenTime().getTime() >= this.closeTimeout * 1000 )
                {
                    this.childServers.remove(v_Index);
                    
                    if ( v_ChildServer.isOpen() )
                    {
                        this.log("ServerSocket：Port " + v_ChildServer.getPort() + " is timeout close.");
                        v_ChildServer.close();
                        v_ChildServer = null;
                    }
                }
            }
        }
        
        if ( !Help.isNull(this.childServers) )
        {
            // 如还有未超时的数据通讯服务，就再延时启动一次本方法。
            this.checkCloseTimeoutStart();
        }
        else
        {
            this.isStartCheckCloseTimeout = false;
        }
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
    public synchronized java.net.ServerSocket getNextPort()
    {
        int v_Index = this.indexPort;
        
        if ( v_Index < this.minPort )
        {
            v_Index = this.minPort;
        }
        else if ( v_Index >= this.maxPort )
        {
            v_Index = this.minPort;
        }
        else
        {
            v_Index++;
        }
        
        for (; v_Index<=this.maxPort; v_Index++)
        {
            java.net.ServerSocket v_ServerSocket = Help.getServerSocket(v_Index ,true);
            if ( v_ServerSocket != null )
            {
                this.indexPort = v_Index;
                return v_ServerSocket;
            }
        }
        
        for (v_Index=this.minPort; v_Index<this.indexPort; v_Index++)
        {
            java.net.ServerSocket v_ServerSocket = Help.getServerSocket(v_Index ,true);
            if ( v_ServerSocket != null )
            {
                this.indexPort = v_Index;
                return v_ServerSocket;
            }
        }
        
        return null;
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
    public synchronized void close()
    {
        this.checkCloseTimeout();
        super.close();
    }
    
    
    
    /**
     * 获取：当前分配到哪一个端口
     */
    public int getIndexPort()
    {
        return indexPort;
    }



    /**
     * 获取：服务端的主端口号。默认值：1721
     */
    public int getPort()
    {
        return super.getPort();
    }


    
    /**
     * 设置：服务端的主端口号。默认值：1721
     * 
     * @param port 
     */
    public void setPort(int i_Port)
    {
        super.setPort(i_Port);
    }


    
    /**
     * 获取：服务端随机创建端口范围的最小端口号（可等于此属性值）。默认值：17000
     */
    public int getMinPort()
    {
        return minPort;
    }


    
    /**
     * 设置：服务端随机创建端口范围的最小端口号（可等于此属性值）。默认值：17000
     * 
     * @param minPort 
     */
    public void setMinPort(int minPort)
    {
        this.minPort = minPort;
    }


    
    /**
     * 获取：服务端随机创建端口范围的最大端口号（可等于此属性值）。默认值：17999
     */
    public int getMaxPort()
    {
        return maxPort;
    }


    
    /**
     * 设置：服务端随机创建端口范围的最大端口号（可等于此属性值）。默认值：17999
     * 
     * @param maxPort 
     */
    public void setMaxPort(int maxPort)
    {
        this.maxPort = maxPort;
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
     * 设置：是否显示调试日志。默认为：false
     * 
     * @param isLog 
     */
    public ServerSocket setLog(boolean isLog)
    {
        super.setLog(isLog);
        return this;
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
