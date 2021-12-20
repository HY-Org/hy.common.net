package org.hy.common.net.netty.rpc.callable;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.hy.common.Date;
import org.hy.common.net.common.NetError;
import org.hy.common.net.data.Command;
import org.hy.common.net.data.CommunicationRequest;
import org.hy.common.net.data.CommunicationResponse;
import org.hy.common.net.data.LoginRequest;
import org.hy.common.net.data.LoginResponse;
import org.hy.common.net.netty.rpc.ClientRPC;
import org.hy.common.xml.log.Logger;





/**
 * 客户端的公开方法接口的代理
 * 
 * @author      ZhengWei(HY)
 * @createDate  2021-09-29
 * @version     v1.0
 */
public class ClientRPCOperationProxy implements InvocationHandler
{
    
    private static final Logger $Logger = new Logger(ClientRPCOperationProxy.class ,true);
    
    /** 按CPU的核数创建线程池 */
    private  ExecutorService threadPool;
    
    /** 所属的客户端 */
    private ClientRPC        clientRPC;
    
    /** 是否登录成功 */
    private boolean          isLogin;
    
    
    
    public ClientRPCOperationProxy(ClientRPC i_ClientRPC)
    {
        this.clientRPC = i_ClientRPC;
    }
    
    
    
    /**
     * 获取线程池
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-12-08
     * @version     v1.0
     * 
     * @return
     */
    private synchronized ExecutorService getThreadPool()
    {
        if ( this.threadPool == null )
        {
            this.threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        }
        
        return this.threadPool;
    }
    
    
    
    /**
     * 关闭线程池
     * 
     * 将关闭方法开放出来，是为了外界直接通过ClientRPC对象关闭时，也能同时将线程池关闭
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-12-08
     * @version     v1.0
     */
    public synchronized void shutdown()
    {
        if ( this.threadPool != null && !this.threadPool.isShutdown() )
        {
            try
            {
                this.threadPool.shutdown();
            }
            catch (Exception exce)
            {
                $Logger.error(exce);
            }
            
            this.threadPool = null;
        }
    }
    
    
    
    @Override
    public Object invoke(Object i_Proxy ,Method i_Method ,Object [] i_Args) throws Throwable
    {
        if ( "startServer".equals(i_Method.getName()) )
        {
            return this.clientRPC.start() != null;
        }
        else if ( "shutdownServer".equals(i_Method.getName()) )
        {
            this.clientRPC.shutdown();
            return true;
        }
        else if ( "isStartServer".equals(i_Method.getName()) )
        {
            return this.clientRPC.isStart();
        }
        else if ( "logout".equals(i_Method.getName()) )
        {
            this.isLogin = false;
            return true;
        }
        else if ( "login".equals(i_Method.getName()) )
        {
            if ( this.clientRPC.isStart() )
            {
                return this.proxyLogin(i_Args);
            }
            else
            {
                return new LoginResponse().setResult(NetError.$StartNotError);
            }
        }
        else if ( "isLogin".equals(i_Method.getName()) )
        {
            if ( this.clientRPC.isStart() )
            {
                return this.isLogin;
            }
            else
            {
                return false;
            }
        }
        if ( "getClient".equals(i_Method.getName()) )
        {
            return this.clientRPC;
        }
        else
        {
            if ( !this.clientRPC.isStart() )
            {
                // 必须先启动后才能通讯
                return new CommunicationResponse().setResult(NetError.$StartNotError).setEndTime(new Date());
            }
            
            if ( !this.isLogin )
            {
                // 必须先登录才能通讯，哪怕是免登录验证的，也是做匿名登录
                return new CommunicationResponse().setResult(NetError.$LoginNotError).setEndTime(new Date());
            }
        }
        
        CommunicationRequest v_Request = new CommunicationRequest();
        boolean              v_IsFind  = true;
        
        if ( "sendCommand".equals(i_Method.getName()) )
        {
            Command v_Command = new Command();
            
            v_Command.setMethodName((String)i_Args[2]);
            
            if ( i_Args.length == 5 )
            {
                v_Command.setParams(   (Object [])i_Args[3]);
                v_Request.setRetunData((boolean)  i_Args[4]);
            }
            else if ( i_Args.length == 3 )
            {
                v_Command.setParams(new Object[]{});
                v_Request.setRetunData(true);
            }
            else if ( i_Args.length == 4 )
            {
                if ( i_Args[3].getClass() == boolean.class || i_Args[3].getClass() == Boolean.class )
                {
                    v_Command.setParams(new Object[]{});
                    v_Request.setRetunData((boolean)i_Args[3]);
                }
                else
                {
                    v_Command.setParams((Object [])i_Args[3]);
                    v_Request.setRetunData(true);
                }
            }
            
            v_Request.setWaitRequestTimeout((long)i_Args[0]);
            v_Request.setDataXID(         (String)i_Args[1]);
            v_Request.setData(                    v_Command);
            v_Request.setDataOperation(CommunicationRequest.$Operation_Command);
        }
        else if ( "sendObject".equals(i_Method.getName()) )
        {
            if ( i_Args.length == 5 )
            {
                v_Request.setDataExpireTimeLen((long)   i_Args[3]);
                v_Request.setRetunData(        (boolean)i_Args[4]);
            }
            else if ( i_Args.length == 3 )
            {
                v_Request.setDataExpireTimeLen(0);
                v_Request.setRetunData(true);
            }
            else if ( i_Args.length == 4 )
            {
                if ( i_Args[3].getClass() == boolean.class || i_Args[3].getClass() == Boolean.class )
                {
                    v_Request.setDataExpireTimeLen(0);
                    v_Request.setRetunData((boolean)i_Args[3]);
                }
                else
                {
                    v_Request.setDataExpireTimeLen((long)i_Args[3]);
                    v_Request.setRetunData(true);
                }
            }
            
            v_Request.setWaitRequestTimeout((long)i_Args[0]);
            v_Request.setDataXID(         (String)i_Args[1]);
            v_Request.setData(                    i_Args[2]);
            v_Request.setDataOperation(CommunicationRequest.$Operation_Update);
        }
        else if ( "removeObject".equals(i_Method.getName()) )
        {
            v_Request.setWaitRequestTimeout((long)i_Args[0]);
            v_Request.setDataXID(         (String)i_Args[1]);
            v_Request.setRetunData(i_Args.length == 3 ? (boolean)i_Args[2] : true);
            v_Request.setDataOperation(CommunicationRequest.$Operation_Delete);
        }
        else if ( "getObject".equals(i_Method.getName()) )
        {
            v_Request.setWaitRequestTimeout((long)i_Args[0]);
            v_Request.setDataXID(         (String)i_Args[1]);
            v_Request.setDataOperation(CommunicationRequest.$Operation_Select);
        }
        else if ( "getObjects".equals(i_Method.getName()) )
        {
            v_Request.setWaitRequestTimeout((long)i_Args[0]);
            v_Request.setDataXID(         (String)i_Args[1]);
            v_Request.setDataOperation(CommunicationRequest.$Operation_Selects);
        }
        else if ( "getSessionMap".equals(i_Method.getName()) )
        {
            v_Request.setWaitRequestTimeout((long)i_Args[0]);
            v_Request.setDataOperation(CommunicationRequest.$Operation_SessionMap);
        }
        else if ( "send".equals(i_Method.getName()) )
        {
            v_Request = (CommunicationRequest)i_Args[0];
        }
        else
        {
            v_IsFind = false;
        }
        
        if ( v_IsFind )
        {
            return this.send(v_Request);
        }
        else
        {
            return null;
        }
    }
    
    
    
    /**
     * 代理登录
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-09-29
     * @version     v1.0
     * 
     * @param i_Args
     * @return
     * @throws InterruptedException
     * @throws ExecutionException
     */
    private Object proxyLogin(Object [] i_Args) throws InterruptedException, ExecutionException
    {
        ClientRPCCallableLogin v_Handler    = new ClientRPCCallableLogin(this.clientRPC.clientHandler() ,(LoginRequest)i_Args[0]);
        LoginResponse          v_Ret        = null;
        boolean                v_Exception  = false;
        
        try
        {
            v_Ret = this.getThreadPool().submit(v_Handler).get();
        }
        catch (Exception exce)
        {
            v_Exception = true;
            $Logger.error(this.clientRPC.getHostPort() ,exce);
        }
        
        if ( v_Ret == null )
        {
            // 一般超时后返回NULL，也可能是服务端宕机了
            v_Ret = new LoginResponse().setEndTime(new Date()).setResult(v_Exception ? NetError.$ServerUnknownError : NetError.$TimeoutError);
            $Logger.info(this.clientRPC.getHostPort() + " 登录超时：" + v_Ret.getResult() + " -> " + i_Args[0].toString() + " -> " + v_Ret.toString());
            this.clientRPC.shutdown();
        }
        else if ( v_Ret.getResult() == CommunicationResponse.$Succeed )
        {
            this.isLogin = true;
            $Logger.info(this.clientRPC.getHostPort() + " 登录成功：" + i_Args[0].toString() + " -> " + v_Ret.toString());
        }
        else
        {
            $Logger.info(this.clientRPC.getHostPort() + " 登录失败：错误码=" + v_Ret.getResult() + " -> " + i_Args[0].toString() + " -> " + v_Ret.toString());
        }
        
        return v_Ret;
    }
    
    
    
    /**
     * 发送请求
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-09-29
     * @version     v1.0
     * 
     * @param i_Request
     * @return
     * @throws InterruptedException
     * @throws ExecutionException
     */
    private Object send(CommunicationRequest i_Request) throws InterruptedException, ExecutionException
    {
        ClientRPCCallableSend v_Handler   = new ClientRPCCallableSend(this.clientRPC.clientHandler() ,i_Request);
        CommunicationResponse v_Ret       = null;
        boolean               v_Exception = false;
        
        try
        {
            v_Ret = this.getThreadPool().submit(v_Handler).get();
        }
        catch (Exception exce)
        {
            v_Exception = true;
            $Logger.error(exce);
        }
        
        if ( v_Ret == null )
        {
            // 一般超时后返回NULL，也可能是服务端宕机了
            v_Ret = new CommunicationResponse().setEndTime(new Date()).setResult(v_Exception ? NetError.$ServerUnknownError : NetError.$TimeoutError);
            $Logger.info("通讯超时：错误码=" + v_Ret.getResult() + " -> " + i_Request.toString() + " -> " + v_Ret.toString());
            this.clientRPC.shutdown();
        }
        else if ( v_Ret.getResult() == CommunicationResponse.$Succeed )
        {
            $Logger.info("通讯成功：" + i_Request.toString() + " -> " + v_Ret.toString());
        }
        else
        {
            $Logger.info("通讯失败：错误码=" + v_Ret.getResult() + " -> " + i_Request.toString() + " -> " + v_Ret.toString());
        }
        
        return v_Ret;
    }
    
}
