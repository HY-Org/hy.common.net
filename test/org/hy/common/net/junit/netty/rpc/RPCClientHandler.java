package org.hy.common.net.junit.netty.rpc;

import java.util.concurrent.Callable;

import org.hy.common.Date;
import org.hy.common.net.data.CommunicationRequest;
import org.hy.common.net.data.CommunicationResponse;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;





/**
 * 业务的处理器：Handler
 * 
 * <CommunicationResponse> 表示客户端与服务端相互通讯的数据封装类。
 * 
 * @author      ZhengWei(HY)
 * @createDate  2021-09-24
 * @version     v1.0
 */
public class RPCClientHandler extends SimpleChannelInboundHandler<CommunicationResponse> implements Callable<CommunicationResponse>
{
    
    private ChannelHandlerContext ctx;
    
    /** 客户端的请求参数 */
    private long                  params;
    
    /** 服务端的返回结果 */
    private CommunicationResponse response;
    
    

    @Override
    public void channelActive(ChannelHandlerContext i_Ctx) throws Exception
    {
        super.channelActive(i_Ctx);
        this.ctx = i_Ctx;
    }



    /**
     * 注意：channelRead0 和 call 方法是同步的
     */
    @Override
    protected synchronized void channelRead0(ChannelHandlerContext i_Ctx ,CommunicationResponse i_Msg) throws Exception
    {
        this.response = i_Msg;
        this.notify();         // 唤醒等待的线程。即唤醒 call 方法
    }
    
    
    
    /**
     * 注意：channelRead0 和 call 方法是同步的
     * 
     * 被代理对象调用，发送数据给服务端，并等待被唤醒（因为服务器的返回不是直接给的call方法，而是给了channelRead0方法）
     */
    @Override
    public synchronized CommunicationResponse call() throws Exception
    {
        CommunicationRequest v_Request = new CommunicationRequest();
        
        v_Request.setSessionTime(new Date());
        v_Request.setDataExpireTimeLen(this.params);
        v_Request.setEventType(CommunicationRequest.$DefaultEventType);
        v_Request.setDataOperation(CommunicationRequest.$Operation_Select);
        v_Request.setRetunData(true);
        
        this.ctx.writeAndFlush(v_Request);
        this.wait();                         // 等待服务端返回结果后，唤醒我，并继续向下执行
        
        return this.response;
    }
    
    
    
    public void setDataExpireTimeLen(long i_DataExpireTimeLen)
    {
        this.params = i_DataExpireTimeLen;
    }

    
    
    @Override
    public void exceptionCaught(ChannelHandlerContext i_Ctx ,Throwable i_Cause) throws Exception
    {
        super.exceptionCaught(i_Ctx ,i_Cause);
        i_Ctx.close();
    }
    
}
