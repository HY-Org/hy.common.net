package org.hy.common.net;

import java.io.BufferedInputStream;
import java.io.EOFException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import org.hy.common.xml.log.Logger;





/**
 * 传输对象的Socket请求处理类（通用的）。
 * 
 * 本类并不是最终处理请求的处理类。
 * 本类只负责将Socket数据流转为Java对象，并接着传递这个Java对象给最终的处理类。
 *
 * @author      ZhengWei(HY)
 * @createDate  2017-01-13
 * @version     v1.0
 */
public abstract class ObjectSocketRequest implements SocketRepuest
{
    private static final Logger $Logger = new Logger(ObjectSocketRequest.class);
    
    
    
    /**
     * 接收到对象请求的实际处理动作
     * 
     * 此接口将被服务端以多线程的方式触发执行。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-01-13
     * @version     v1.0
     *
     * @param i_RequestData  通过Socket请求接收到的数据，它是一个Java对象
     * @param i_ServerBase   接收请求的服务端Socket服务
     * @return               发送给请求者（Socket客户端）的响应数据，也应是一个Java对象。
     */
    public abstract Object request(Object i_RequestData ,ServerBase i_ServerBase);
        
    
    
    /**
     * 当服务端接收到请求时执行的动作。
     * 
     * 此接口将被服务端以多线程的方式触发执行。
     * 
     * 注意：具体的实现类要负责关闭Socket。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-01-13
     * @version     v1.0
     *
     * @param i_ServerBase  接收请求的服务端Socket服务
     * @param i_Socket      接收到的Socket请求
     */
    @Override
    public void request(ServerBase i_ServerBase ,Socket i_Socket)
    {
        InputStream        v_InputSocket = null;
        ObjectInputStream  v_Input       = null;
        ObjectOutputStream v_Output      = null;
        
        try
        {
            if ( i_Socket == null || !i_Socket.isConnected() || i_Socket.isClosed() || i_Socket.isInputShutdown() )
            {
                return;
            }
            
            v_InputSocket = i_Socket.getInputStream();
            if ( v_InputSocket == null )
            {
                return;
            }
            
            v_Input = new ObjectInputStream(new BufferedInputStream(v_InputSocket));

            Object v_RequestData  = v_Input.readObject();
            Object v_ResponseData = this.request(v_RequestData ,i_ServerBase);
            try
            {
                i_Socket.shutdownInput();
            }
            catch (Throwable exce)
            {
                $Logger.error(exce);
            }
            
            $Logger.debug("ServerBase：Port " + i_ServerBase.port + " Response is ready to send datas.");
            
            try
            {
                v_Output = new ObjectOutputStream(i_Socket.getOutputStream());
                if ( v_ResponseData != null )
                {
                    v_Output.writeObject(v_ResponseData);
                }
                v_Output.flush();
                i_Socket.shutdownOutput();
            }
            catch (Throwable exce)
            {
                $Logger.error(exce);
            }
            
            $Logger.debug("ServerBase：Port " + i_ServerBase.port + " Response is finish.");
        }
        catch (EOFException exce)
        {
            // 此异常就不在报出了，可能是第三方软件在扫描端口
        }
        catch (Throwable exce)
        {
            $Logger.warn("服务端接收请求 " + i_Socket.getRemoteSocketAddress().toString() + ":" + i_Socket.getLocalPort() + " 异常.");
            $Logger.error(exce);
        }
        finally
        {
            if ( v_Input != null )
            {
                try
                {
                    v_Input.close();
                }
                catch (Throwable exce)
                {
                    // Nothing.
                }
                
                v_Input = null;
            }
            
            if ( v_InputSocket != null )
            {
                try
                {
                    v_InputSocket.close();
                }
                catch (Throwable exce)
                {
                    // Nothing.
                }
                
                v_InputSocket = null;
            }
            
            if ( v_Output != null )
            {
                try
                {
                    v_Output.close();
                }
                catch (Throwable exce)
                {
                    // Nothing.
                }
                
                v_Output = null;
            }
            
            if ( i_Socket != null )
            {
                try
                {
                    if ( !i_Socket.isClosed() )
                    {
                        i_Socket.close();
                    }
                }
                catch (Throwable exce)
                {
                    // Nothing.
                }
                
                i_Socket = null;
            }
        }
    }
    
}
