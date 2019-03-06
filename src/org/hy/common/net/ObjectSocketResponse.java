package org.hy.common.net;

import java.io.BufferedInputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import org.hy.common.Date;
import org.hy.common.Help;





/**
 * 传输对象的Socket响应处理类（客户端接收服务端的响应）（通用的）。
 * 
 * 本类并不是最终处理响应的处理类。
 * 本类只负责将Socket数据流转为Java对象，并接着传递这个Java对象给最终的处理类。
 * 
 * 注意: ClientSocketCluster类所有方法的入参timeout与ClientSocket.timeout是有区别的，含义不同。
 *       1. ClientSocketCluster类的timeout重点指，有大量数据通讯时，超过定义时长后，自动结束。一般本类的timeout可以配置大些。
 *       2. ClientSocket.timeout重点指连路是否正常，是在数据通讯前建立连接的等待时长。一般配置较小，如10秒。
 *
 * @author      ZhengWei(HY)
 * @createDate  2017-01-14
 * @version     v1.0
 *              v2.0  2019-03-06  添加：超时时长的机制
 */
public abstract class ObjectSocketResponse implements SocketResponse
{
    
    /** 主机名称 */
    protected String hostName;
    
    /** 端口号 */
    protected int    port;
    
    /**  超时时长（单位：毫秒）。当为0时，表示最大超时时长。默认为：10秒 */
    protected int    timeout;
    
    
    
    public ObjectSocketResponse(String i_HostName ,int i_Port)
    {
        this.hostName = i_HostName;
        this.port     = i_Port;
        this.timeout  = 10 * 1000;
    }
    
    
    
    /**
     * 向服务端发送数据
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-01-14
     * @version     v1.0
     *
     * @param i_RequestData  请求发送给服务端的数据
     * @return               服务端返回的响应数据
     */
    protected Object send(Object i_RequestData)
    {
        Socket             v_Socket = null;  
        ObjectInputStream  v_Input  = null;  
        ObjectOutputStream v_Output = null;
          
        try 
        {  
            if ( i_RequestData == null )
            {
                return null;
            }
            
            if ( !Help.isAllowConnect(this.hostName ,this.port ,this.timeout) )
            {
                System.out.println(Date.getNowTime().getFull() + " 无法与 " + this.hostName + ":" + this.port + " 建立网络通讯连接");
                return null;
            }
            
            v_Socket = Help.getSocket(this.hostName ,this.port);
            if ( v_Socket == null )
            {
                return null;
            }
            
            v_Output = new ObjectOutputStream(v_Socket.getOutputStream());  
            v_Output.writeObject(i_RequestData);  
            v_Output.flush();  
              
            v_Input = new ObjectInputStream(new BufferedInputStream(v_Socket.getInputStream()));
            Object v_ResponseData = v_Input.readObject(); 
            
            return this.response(i_RequestData ,v_ResponseData);
        } 
        catch (Throwable exce)
        {
            exce.printStackTrace();
        }
        finally
        {
            if ( v_Input != null )
            {
                try
                {
                    v_Input.close();
                }
                catch (Exception exce)
                {
                    // Nothing.
                }
                
                v_Input = null;
            }
            
            if ( v_Output != null )
            {
                try
                {
                    v_Output.close();
                }
                catch (Exception exce)
                {
                    // Nothing.
                }
                
                v_Output = null;
            }
            
            if ( v_Socket != null )
            {
                try
                {
                    v_Socket.close();
                }
                catch (Exception exce)
                {
                    // Nothing.
                }
                
                v_Socket = null;
            }
        }
        
        return null;
    }

    
    
    /**
     * 获取：主机名称
     */
    public String getHostName()
    {
        return hostName;
    }

    
    
    /**
     * 设置：主机名称
     * 
     * @param hostName 
     */
    public void setHostName(String hostName)
    {
        this.hostName = hostName;
    }

    
    
    /**
     * 获取：端口号
     */
    public int getPort()
    {
        return port;
    }

    
    
    /**
     * 设置：端口号
     * 
     * @param port 
     */
    public void setPort(int port)
    {
        this.port = port;
    }


    
    /**
     * 获取：超时时长（单位：毫秒）。当为0时，表示最大超时时长。默认为：10秒
     */
    public int getTimeout()
    {
        return timeout;
    }


    
    /**
     * 设置：超时时长（单位：毫秒）。当为0时，表示最大超时时长。默认为：10秒
     * 
     * @param timeout 
     */
    public void setTimeout(int timeout)
    {
        this.timeout = timeout;
    }
    
}
