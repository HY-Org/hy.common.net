package org.hy.common.net;

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
    
    
    
    public ServerBase()
    {
        this(0);
    }
    
    
    
    public ServerBase(int i_Port)
    {
        this.port           = i_Port;
        this.isOpen         = false;
        this.openTime       = null;
        this.acceptIsThread = true;
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
        this.isOpen         = false;
        this.openTime       = null;
        this.acceptIsThread = true;
        this.myPortPool     = i_MyPortPool;
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
        return this.open(this.port);
    }
    
    
    
    /**
     * 打开服务端端口的监听服务
     * 
     * 将启动一个独立的线程
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-01-12
     * @version     v1.0
     *
     * @param i_Port  服务端的端口号
     * @return        是否监听端口成功
     */
    public synchronized boolean open(int i_Port)
    {
        if ( this.isOpen )
        {
            return false;
        }
        
        try
        {
            $Logger.debug("ServerBase：Port " + this.port + " is ready open.");
            
            this.port = i_Port;
            
            if ( this.server == null || this.server.isClosed() || !this.server.isBound() )
            {
                this.server = Help.getServerSocket(this.port ,true);
            }
            
            this.isOpen   = true;
            this.openTime = new Date();
            
            // 这里必须是一个线程，因为方法内是“死循环”
            (new Execute(this ,"openListening" ,this)).start();
            $Logger.debug("ServerBase：Port " + this.port + " is open.");
        }
        catch (Exception exce)
        {
            $Logger.error(exce);
        }
        
        return this.isOpen;
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
                Socket v_Socket = i_ServerBase.server.accept();  // 等待新请求、否则一直阻塞
                
                if ( i_ServerBase.request != null )
                {
                    if ( this.acceptIsThread )
                    {
                        // 每个处理请求的也是一个独立的线程
                        // 注意：Socket由请求类内部关闭。
                        (new Execute(i_ServerBase.request ,"request" ,new Object[]{i_ServerBase ,v_Socket})).start();
                    }
                    else
                    {
                        i_ServerBase.request.request(i_ServerBase ,v_Socket);
                    }
                }
                else
                {
                    // 当没有请求处理类时，不做任何操作，直接关闭Socket请求即可。
                    $Logger.debug("ServerBase：Port " + i_ServerBase.port + " Request is not set to the action, will automatically close.");
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
    
    
    
    public void toIdle()
    {
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
    public void setOpenTime(Date openTime)
    {
        this.openTime = openTime;
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
