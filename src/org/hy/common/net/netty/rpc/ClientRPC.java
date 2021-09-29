package org.hy.common.net.netty.rpc;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.hy.common.Date;
import org.hy.common.net.NetError;
import org.hy.common.net.data.CommunicationRequest;
import org.hy.common.net.data.CommunicationResponse;
import org.hy.common.net.data.LoginRequest;
import org.hy.common.net.data.LoginResponse;
import org.hy.common.net.data.protobuf.CommunicationProto;
import org.hy.common.net.netty.Client;
import org.hy.common.net.netty.rpc.callable.ClientRPCCallableLogin;
import org.hy.common.net.netty.rpc.callable.ClientRPCCallableSend;
import org.hy.common.net.netty.rpc.callable.ClientRPCOperation;
import org.hy.common.net.netty.rpc.encoder.CommunicationRequestEncoder;
import org.hy.common.net.netty.rpc.encoder.LoginRequestEncoder;
import org.hy.common.xml.log.Logger;

import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;





/**
 * RPC + Protobuf的客户端。
 * 
 * 同时允许用户自定义部分的数据使用不同的数据格式协议（如 Json、Protobuf、编程语言的基本数据类型）
 * 
 * @author      ZhengWei(HY)
 * @createDate  2021-09-25
 * @version     v1.0
 */
public class ClientRPC extends Client<ClientRPC>
{
    
    private static final Logger    $Logger     = new Logger(ClientRPC.class ,true);
    
    // 按CPU的核数创建线程池
    private static ExecutorService $ThreadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    
    /** 业务处理器 */
    private ClientRPCHandler clientHandler;
    
    /** 是否登录成功 */
    private boolean          isLogin;
    
    
    
    @Override
    public void initChannel(SocketChannel i_Channel ,ChannelPipeline i_Pipeline)
    {
        // 编码器采用：先进后出原则。即最后的编码器，优先编码
        i_Pipeline.addLast("编码器3" ,new ProtobufEncoder());
        i_Pipeline.addLast("编码器2" ,new CommunicationRequestEncoder());
        i_Pipeline.addLast("编码器1" ,new LoginRequestEncoder());
        
        // 解码器采用：先进先出原则。即最后的解码器，最后解码
        i_Pipeline.addLast("解码器1" ,new ProtobufDecoder(CommunicationProto.Data.getDefaultInstance()));  // 指定对哪种类型解码
        i_Pipeline.addLast("解码器2" ,this.clientHandler = new ClientRPCHandler(this));
    }
    
    
    
    /**
     * 获取客户端操作接口方法
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-09-28
     * @version     v1.0
     * 
     * @return
     */
    public ClientRPCOperation operation()
    {
        return (ClientRPCOperation)this.getBean(ClientRPCOperation.class ,(iv_Proxy ,iv_Method ,iv_Args) ->
        {
            if ( "login".equals(iv_Method.getName()) )
            {
                ClientRPCCallableLogin v_LoginHandler = new ClientRPCCallableLogin(this.clientHandler ,(LoginRequest)iv_Args[0]);
                LoginResponse v_Ret = $ThreadPool.submit(v_LoginHandler).get();
                
                if ( v_Ret.getResult() == CommunicationResponse.$Succeed )
                {
                    this.isLogin = true;
                    $Logger.info("登录成功：" + iv_Args[0].toString() + " -> " + v_Ret.toString());
                }
                else
                {
                    $Logger.info("登录失败：错误码=" + v_Ret.getResult() + " -> " + iv_Args[0].toString() + " -> " + v_Ret.toString());
                }
                
                return v_Ret;
            }
            else if ( "send".equals(iv_Method.getName()) )
            {
                if ( !isLogin )
                {
                    // 必须先登录才能通讯，哪怕是免登录验证的，也是做匿名登录
                    return new CommunicationResponse().setResult(NetError.$LoginNotError).setEndTime(new Date());
                }
                
                ClientRPCCallableSend v_SendHandler = new ClientRPCCallableSend(this.clientHandler ,(CommunicationRequest)iv_Args[0]);
                CommunicationResponse v_Ret = $ThreadPool.submit(v_SendHandler).get();
                
                if ( v_Ret.getResult() == CommunicationResponse.$Succeed )
                {
                    this.isLogin = true;
                    $Logger.info("通讯成功：" + iv_Args[0].toString() + " -> " + v_Ret.toString());
                }
                else
                {
                    $Logger.info("通讯失败：错误码=" + v_Ret.getResult() + " -> " + iv_Args[0].toString() + " -> " + v_Ret.toString());
                }
                
                return v_Ret;
            }
            else
            {
                return null;
            }
        });
    }
    
    
    
    /**
     * 获取代理对象
     * 
     * @param i_Class
     * @param i_Handler
     * @return
     */
    protected Object getBean(final Class<?> i_Class ,final InvocationHandler i_Handler)
    {
        return Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader() ,new Class<?>[] {i_Class} ,i_Handler);
    }


    
    /**
     * 是否登录成功
     * 
     * @return
     */
    public boolean isLogin()
    {
        return isLogin;
    }
    
    
    protected void setIslogonForFalse()
    {
        this.isLogin = false;
    }
    
}
