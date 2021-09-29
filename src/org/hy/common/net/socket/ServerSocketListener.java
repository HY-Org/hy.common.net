package org.hy.common.net.socket;

import org.hy.common.ExpireMap;
import org.hy.common.StringHelp;
import org.hy.common.net.ServerSocket;
import org.hy.common.net.data.LoginRequest;
import org.hy.common.net.data.LoginResponse;
import org.hy.common.xml.log.Logger;





/**
 * ServerSocket服务端的接收到的请求处理类。
 * 主要功能有：
 *   1. 登陆验证
 *   2. 创建新的临时的用于通讯的服务监听端口。
 *   3. 将创建通讯端口告知客户端。
 *
 * @author      ZhengWei(HY)
 * @createDate  2017-01-13
 * @version     v1.0
 *              v2.0  2021-08-26  添加： 端口池、打开的端口不再关闭、每次的数据通讯均要票据
 */
public class ServerSocketListener extends ObjectSocketRequest
{
    private static final Logger $Logger = new Logger(ServerSocketListener.class);
    
    /** 端口票据信息。Map.key为票据号，Map.value为通讯端口号 */
    private static ExpireMap<String ,Integer> $Tokens;
    
    /** 本监听器所属的Socket服务端 */
    private ServerSocket serverSocket;
    
    
    
    /**
     * 缓存票据与端口的映射关系
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-08-26
     * @version     v1.0
     * 
     * @param i_Data
     */
    private synchronized void putToken(LoginResponse i_Data)
    {
        if ( $Tokens == null )
        {
            $Tokens = new ExpireMap<String ,Integer>();
        }
        
        $Tokens.put(i_Data.getToken() ,i_Data.getPort() ,this.serverSocket.getCloseTimeout());
    }
    
    
    
    /**
     * 通过票据查端口号
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-08-26
     * @version     v1.0
     * 
     * @param i_Token  票据号
     * @return
     */
    public static Integer getPort(String i_Token)
    {
        if ( $Tokens == null )
        {
            return null;
        }
        
        return $Tokens.get(i_Token);
    }
    
    
    
    /**
     * 释放票据号
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-08-26
     * @version     v1.0
     * 
     * @param i_Token  票据号
     * @return
     */
    public static void removeToken(String i_Token)
    {
        if ( $Tokens == null )
        {
            return;
        }
        
        $Tokens.remove(i_Token);
    }
    
    
    
    public ServerSocketListener(ServerSocket i_MyServerSocket)
    {
        this.serverSocket = i_MyServerSocket;
    }
    
    
    
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
    @Override
    public Object request(Object i_RequestData ,ServerBase i_ServerBase)
    {
        if ( i_RequestData == null || !(i_RequestData instanceof LoginRequest) )
        {
            return null;
        }
        
        if ( i_ServerBase == null || !(i_ServerBase instanceof ServerSocket) )
        {
            return null;
        }
        
        $Logger.debug("ServerLogin：login begin.");
        
        // 登陆验证
        ServerSocket v_ServerSocket = (ServerSocket)i_ServerBase;
        if ( v_ServerSocket.getValidate() != null )
        {
            if ( !(i_RequestData instanceof LoginRequest) )
            {
                return null;
            }
            
            if ( !v_ServerSocket.getValidate().validate((LoginRequest)i_RequestData) )
            {
                $Logger.debug("ServerLogin：invalid login.");
                return null;
            }
        }
        
        // 创建一个新的用于数据通讯的临时的服务端Socket监听服务
        ServerBase v_CommunicationServer = v_ServerSocket.createCommunicationServer();
        if ( v_CommunicationServer == null )
        {
            $Logger.debug("ServerLogin：not find new port.");
            return null;
        }
        
        LoginResponse v_ResponseData = new LoginResponse();
        v_ResponseData.setPort(v_CommunicationServer.getPort());
        v_ResponseData.setToken(StringHelp.getUUID());
        
        this.putToken(v_ResponseData);
        
        $Logger.debug("ServerLogin：login finish.");
        return v_ResponseData;
    }
    
}
