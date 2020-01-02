package org.hy.common.net;

import java.net.Socket;

import org.hy.common.Date;
import org.hy.common.Execute;
import org.hy.common.Help;





/**
 * Socket服务端（通用的）
 *
 * @author      ZhengWei(HY)
 * @createDate  2017-01-12
 * @version     v1.0
 *              v2.0  2019-12-28  添加：支持由外界创建及绑定端口的java.net.ServerSocket，并直接使用它。
 */
public class ServerBase
{
    
    /** 服务端的端口号 */
    protected int                   port;
    
    /** 服务端的端口是否打开 */
    protected boolean               isOpen;
    
    /** 服务端的端口打开时间 */
    protected Date                  openTime;
    
    /** Socket服务 */
    protected java.net.ServerSocket server;
    
    /** 接收到请求时的处理类 */
    protected SocketRepuest         request;
    
    /** 是否显示调试日志。默认为：false */
    protected boolean               isLog;
    
    
    
    public ServerBase()
    {
        this(0);
    }
    
    
    
    public ServerBase(int i_Port)
    {
        this.port     = i_Port;
        this.isOpen   = false;
        this.openTime = null;
        this.isLog    = false;
    }
    
    
    
    public ServerBase(java.net.ServerSocket i_Server)
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
        this.isOpen   = false;
        this.openTime = null;
        this.isLog    = false;
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
    public synchronized boolean open()
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
            this.port = i_Port;
            
            if ( this.server == null || this.server.isClosed() || !this.server.isBound() )
            {
                this.server = Help.getServerSocket(this.port ,true);
            }
            
            this.isOpen   = true;
            this.openTime = new Date();
            
            this.log("ServerBase：Port " + this.port + " is open.");
            
            (new Execute(this ,"openListening" ,this)).start();
        }
        catch (Exception exce)
        {
            exce.printStackTrace();
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
                    // 每个处理请求的也是一个独立的线程
                    // 注意：Socket由请求类内部关闭。
                    (new Execute(i_ServerBase.request ,"request" ,new Object[]{i_ServerBase ,v_Socket})).start();
                }
                else
                {
                    // 当没有请求处理类时，不做任何操作，直接关闭Socket请求即可。
                    this.log("ServerBase：Port " + i_ServerBase.port + " Request is not set to the action, will automatically close.");
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
                    exce.printStackTrace();
                }
                else
                {
                    // 一般是关闭Socket引起的，可不处理
                    // Nothing.
                }
            }
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
            try
            {
                if ( !this.server.isClosed() )
                {
                    this.server.close();
                }
            }
            catch (Exception exce)
            {
                exce.printStackTrace();
            }
            finally
            {
                this.server = null;
            }
        }
        
        this.log("ServerBase：Port " + this.port + " is closed.");
    }
    
    
    
    /**
     * 输出调试日志
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-01-15
     * @version     v1.0
     *
     * @param i_Log
     */
    protected void log(String i_Log)
    {
        if ( this.isLog )
        {
            System.out.println(Date.getNowTime().getFullMilli() + "  " + i_Log);
        }
    }
    
    
    
    /**
     * 获取：是否显示调试日志。默认为：false
     */
    public boolean isLog()
    {
        return isLog;
    }



    /**
     * 设置：是否显示调试日志。默认为：false
     * 
     * @param isLog 
     */
    public ServerBase setLog(boolean isLog)
    {
        this.isLog = isLog;
        return this;
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
    
}
