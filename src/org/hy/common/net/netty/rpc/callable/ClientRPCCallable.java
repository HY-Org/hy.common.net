package org.hy.common.net.netty.rpc.callable;

import java.util.concurrent.Callable;

import org.hy.common.net.data.protobuf.CommunicationProto.Data;
import org.hy.common.net.netty.rpc.ClientRPCHandler;





/**
 * 业务的处理器的方法调用处理
 * 
 * <R1>  请求对象
 * <R2>  响应对象
 * 
 * @author      ZhengWei(HY)
 * @createDate  2021-09-28
 * @version     v1.0
 */
public abstract class ClientRPCCallable<R1 ,R2> implements Callable<R2>
{
    
    /** 所属的业务处理 */
    private ClientRPCHandler clientHandler;
    
    /** 请求参数 */
    private R1               paramObject;
    
    
    
    /**
     * 解码器。此处并没有用Netty的解码器，原因是：这里是针对于Data内容的、具体值的解码器。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-09-29
     * @version     v1.0
     * 
     * @param i_Data
     * @return
     */
    public abstract R2 decoder(Data i_Data);
    
    
    
    public ClientRPCCallable(ClientRPCHandler i_ClientRPCHandler ,R1 i_Request)
    {
        this.clientHandler = i_ClientRPCHandler;
        this.paramObject   = i_Request;
    }
    
    

    /**
     * 被代理对象调用，发送数据给服务端，并等待被唤醒（因为服务器的返回不是直接给的call方法，而是给了channelRead0方法）
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-09-28
     * @version     v1.0
     */
    @Override
    public R2 call() throws Exception
    {
        Data v_Data = this.clientHandler.send(this.paramObject);
        
        if ( v_Data != null )
        {
            return this.decoder(v_Data);
        }
        else
        {
            // 一般超时后会为NULL
            return null;
        }
    }
    
}
