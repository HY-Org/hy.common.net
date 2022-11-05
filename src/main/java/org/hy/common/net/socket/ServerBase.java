package org.hy.common.net.socket;

import java.net.Socket;

import org.hy.common.Date;
import org.hy.common.Execute;
import org.hy.common.Help;
import org.hy.common.xml.log.Logger;





/**
 * Socket服务端（通用的）
 *
 * @author      ZhengWei(HY)
 * @createDate  2017-01-12
 * @version     v1.0
 *              v2.0  2019-12-28  添加：支持由外界创建及绑定端口的java.net.ServerSocket，并直接使用它。
 *              v3.0  2021-08-23  优化：通过acceptIsThread来控制数据通讯时，不再独立开线程，因为它原本就是一个线程中。
 *              v4.0  2021-08-26  添加： 端口池、打开的端口不再关闭、每次的数据通讯均要票据
 */
public class ServerBase
{
    private static final Logger $Logger = new Logger(ServerBase.class);
    
    /** 服务所属于的端口池（当为主监听端口服务时，可为NULL） */
    protected PortPool              myPortPool;
    
    /** 服务端的端口号 */
    protected int                   port;
    
    /** 服务端的端口是否空闲 */
    protected boolean               isIdle;
    
    /** 服务端的端口是否打开 */
    protected boolean               isOpen;
    
    /** 服务端的端口打开时间 */
    protected Date                  openTime;
    
    /** 服务端接收数据后，对数据的处理是否也是多线程的。默认为：true */
    protected boolean               acceptIsThread;
    
    /** Socket服务 */
    protected java.net.ServerSocket server;
    
    /** 接收到请求时的处理类 */
    protected SocketRepuest         request;
    
    /** 在线Socket的数量 */
    protected int                   onLineSocketCount;
    
    
    
    public ServerBase()
    {
        this(0);
    }
    
    
    
    public ServerBase(int i_Port)
    {
        this.port              = i_Port;
        this.isOpen            = false;
        this.isIdle            = true;
        this.openTime          = null;
        this.acceptIsThread    = true;
        this.onLineSocketCount = 0;
    }
    
    
    
    public ServerBase(java.net.ServerSocket i_Server ,PortPool i_MyPortPool)
    {
        if ( i_Server != null )
        {
            this.server = i_Server;
            this.port   = i_Server.getLocalPort();
        }
        else
        {
            this.port = 0;
        }
        
        this.isOpen            = false;
        this.isIdle            = true;
        this.openTime          = null;
        this.acceptIsThread    = true;
        this.myPortPool        = i_MyPortPool;
        this.onLineSocketCount = 0;
    }
    
    
    /**
     * 在线Socket的数量++
     * 
     * 触发时机：ServerSocket.accept() 之后第一个被触发
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-08-28
     * @version     v1.0
     * 
     * @return  返回在线Socket的数量
     */
    protected synchronized int socketAcceptAfter()
    {
        return ++this.onLineSocketCount;
    }
    
    
    
    /**
     * 在线Socket的数量--
     * 
     * 触发时机：Socket.close() 之后第一个被触发
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-08-28
     * @version     v1.0
     * 
     * @return  返回在线Socket的数量
     */
    protected synchronized int socketCloseAfter()
    {
        return --this.onLineSocketCount;
    }
    
    
    
    /**
     * 获取在线Socket的数量
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-08-28
     * @version     v1.0
     * 
     * @return
     */
    public int getOnLineSocketCount()
    {
        return this.onLineSocketCount;
    }
    
    
    
    /**
     * 打开服务端的端口的监听服务
     * 
     * 将启动一个独立的线程
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-01-12
     * @version     v1.0
     *
     * @return      是否监听端口成功
     */
    public boolean open()
    {
        return open(this);
    }
    
    
    
    /**
     * 打开服务端的端口的监听服务
     * 
     * 将启动一个独立的线程
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-01-12
     * @version     v1.0
     *
     * @return      是否监听端口成功
     */
    public boolean open(int i_Port)
    {
        this.setPort(i_Port);
        return open(this);
    }
    
    
    
    /**
     * 打开服务端端口的监听服务
     * 
     * 将启动一个独立的线程
     * 
     * 注意：本方法可能在多个实例、多个线程中执行，所以要用 static synchronized
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-08-27
     * @version     v1.0
     *
     * @param i_ServerBase  服务端对象（准备打开端口）
     * @return              是否监听端口成功
     */
    public static synchronized boolean open(ServerBase i_ServerBase)
    {
        if ( i_ServerBase.isOpen )
        {
            return false;
        }
        
        try
        {
            $Logger.debug("ServerBase：Port " + i_ServerBase.port + " is ready open.");
            
            if ( i_ServerBase.server == null || i_ServerBase.server.isClosed() || !i_ServerBase.server.isBound() )
            {
                i_ServerBase.server = Help.getServerSocket(i_ServerBase.port ,true);
            }
            
            i_ServerBase.isOpen   = true;
            i_ServerBase.openTime = new Date();
            
            // 这里必须是一个线程，因为方法内是“死循环”
            (new Execute(i_ServerBase ,"openListening" ,i_ServerBase)).start();
            $Logger.debug("ServerBase：Port " + i_ServerBase.port + " is open.");
        }
        catch (Exception exce)
        {
            $Logger.error(exce);
        }
        
        return i_ServerBase.isOpen;
    }
    
    
    
    /**
     * 以独立线程的方式打开服务端端口的监听服务
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-01-12
     * @version     v1.0
     *
     * @param i_ServerBase
     */
    public void openListening(ServerBase i_ServerBase)
    {
        if ( i_ServerBase == null )
        {
            return;
        }
        
        while ( i_ServerBase.isOpen )
        {
            try
            {
                Socket v_Socket      = i_ServerBase.server.accept();  // 等待新请求、否则一直阻塞
                int    v_SocketCount = i_ServerBase.socketAcceptAfter();
                
                if ( i_ServerBase.request != null )
                {
                    if ( this.acceptIsThread )
                    {
                        // 每个处理请求的也是一个独立的线程
                        // 注意：Socket由请求类内部关闭。
                        $Logger.debug("ServerBase：Port " + i_ServerBase.port + " Request is ready read datas.[T]." + v_SocketCount);
                        (new Execute(i_ServerBase.request ,"request" ,new Object[]{i_ServerBase ,v_Socket})).start();
                    }
                    else
                    {
                        $Logger.debug("ServerBase：Port " + i_ServerBase.port + " Request is ready read datas." + v_SocketCount);
                        i_ServerBase.request.request(i_ServerBase ,v_Socket);
                    }
                }
                else
                {
                    // 当没有请求处理类时，不做任何操作，直接关闭Socket请求即可。
                    $Logger.debug("ServerBase：Port " + i_ServerBase.port + " Request is not set to the action, will automatically close." + v_SocketCount);
                    try
                    {
                        if ( !v_Socket.isClosed() )
                        {
                            v_Socket.close();
                        }
                    }
                    catch (Exception exce)
                    {
                        // Nothing.
                    }
                    
                    i_ServerBase.socketCloseAfter();
                    v_Socket = null;
                }
            }
            catch (Throwable exce)
            {
                if ( this.isOpen )
                {
                    $Logger.error(exce);
                }
                else
                {
                    // 一般是关闭Socket引起的，可不处理
                    // Nothing.
                }
            }
        }
        
        i_ServerBase.close();
    }
    
    
    
    public synchronized void toIdle()
    {
        this.isIdle = true;
        
        if ( this.myPortPool != null )
        {
            this.myPortPool.putIdle(this.port);
        }
    }
    
    
    
    /**
     * 关闭服务端端口的监听服务
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-01-12
     * @version     v1.0
     */
    public synchronized void close()
    {
        this.isOpen = false;  // 一定在放在关闭之前，因为关闭可能会引发异常 openListening(...) 方法的异常。
        
        if ( this.server != null )
        {
            $Logger.debug("ServerBase：Port " + this.port + " is ready close.");
            
            try
            {
                if ( !this.server.isClosed() )
                {
                    this.server.close();
                }
                
            }
            catch (Throwable exce)
            {
                $Logger.error(exce);
            }
            finally
            {
                this.server = null;
            }
            
            $Logger.debug("ServerBase：Port " + this.port + " is closed.");
        }
    }
    
    

    /**
     * 获取：服务端的端口号
     */
    public int getPort()
    {
        return port;
    }


    
    /**
     * 设置：服务端的端口号
     * 
     * @param port
     */
    public void setPort(int port)
    {
        if ( this.isOpen )
        {
            return;
        }
        this.port = port;
    }


    
    /**
     * 获取：接收到请求时的处理类
     */
    public SocketRepuest getRequest()
    {
        return request;
    }


    
    /**
     * 设置：接收到请求时的处理类
     * 
     * @param request
     */
    public void setRequest(SocketRepuest request)
    {
        this.request = request;
    }

    
    
    /**
     * 获取：服务端的端口是否空闲
     */
    public synchronized boolean isIdle()
    {
        return isIdle;
    }
    

    
    /**
     * 获取：服务端的端口是否打开
     */
    public boolean isOpen()
    {
        return isOpen;
    }



    /**
     * 设置：服务端的端口是否打开
     * 
     * @param isOpen
     */
    public void setOpen(boolean isOpen)
    {
        this.isOpen = isOpen;
    }


    
    /**
     * 获取：服务端的端口打开时间
     */
    public Date getOpenTime()
    {
        return openTime;
    }


    
    /**
     * 设置：服务端的端口打开时间
     * 
     * @param openTime
     */
    public synchronized void setOpenTime(Date openTime)
    {
        this.openTime = openTime;
        this.isIdle   = false;
    }



    /**
     * 获取：服务端接收数据后，对数据的处理是否也是多线程的。默认为：true
     */
    public boolean isAcceptIsThread()
    {
        return acceptIsThread;
    }



    /**
     * 设置：服务端接收数据后，对数据的处理是否也是多线程的。默认为：true
     * 
     * @param acceptIsThread
     */
    public void setAcceptIsThread(boolean acceptIsThread)
    {
        this.acceptIsThread = acceptIsThread;
    }
    
}
