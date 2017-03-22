package org.hy.common.net;

import java.io.BufferedInputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;





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
    public void request(ServerBase i_ServerBase ,Socket i_Socket)
    {
        ObjectInputStream  v_Input  = null;  
        ObjectOutputStream v_Output = null;
        
        try
        {
            v_Input  = new ObjectInputStream(new BufferedInputStream(i_Socket.getInputStream()));  

            Object v_RequestData  = v_Input.readObject();  
            Object v_ResponseData = this.request(v_RequestData ,i_ServerBase);
            
            v_Output = new ObjectOutputStream(i_Socket.getOutputStream()); 
            v_Output.writeObject(v_ResponseData);  
            v_Output.flush();
        }
        catch (Exception exce)
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
            
            if ( i_Socket != null )
            {
                try
                {
                    i_Socket.close();
                }
                catch (Exception exce)
                {
                    // Nothing.
                }
                
                i_Socket = null;
            }
        }
    }
    
}
