package org.hy.common.net.netty.rpc;

import org.hy.common.net.data.protobuf.CommunicationProto.Data;
import org.hy.common.xml.log.Logger;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;




/**
 * 业务的处理器：Handler
 * 
 * <Data> 表示客户端与服务端相互通讯的数据封装类。
 * 
 * @author      ZhengWei(HY)
 * @createDate  2021-09-28
 * @version     v1.0
 */
public class ClientRPCHandler extends SimpleChannelInboundHandler<Data>
{
    private static final Logger $Logger  = new Logger(ClientRPCHandler.class ,true);
    
    /** 所属的客户端 */
    private ClientRPC             clientRPC;
    
    /** 通讯通道 */
    private ChannelHandlerContext ctx;
    
    /** 服务端的返回结果 */
    private Data                  response;
    
    
    
    public ClientRPCHandler(ClientRPC i_ClientRPC)
    {
        super();
        this.clientRPC = i_ClientRPC;
    }
    

    /**
     * 当通道就绪时触发
     */
    @Override
    public void channelActive(ChannelHandlerContext i_Ctx) throws Exception
    {
        super.channelActive(i_Ctx);
        this.ctx = i_Ctx;
    }
    
    
    
    /**
     * 有读取数据时触发
     * 
     * 注意：费时操作请误在这里直接处理，而是使用Netty任务对列处理（提交到Channel对应的NioEventLoop的TaskQueue/ScheduleQueue中即可）
     * 注意：channelRead0 和 send 方法是同步的
     * 
     * i_Ctx：是上下文对象，包含管道pipline、通道channel、地址
     * i_Msg：服务端发送的数据
     */
    @Override
    protected synchronized void channelRead0(ChannelHandlerContext i_Ctx ,Data i_Msg) throws Exception
    {
        this.response = i_Msg;
        this.notify();         // 唤醒等待的线程。即唤醒 call 方法
    }
    


    /**
     * 发消息给服务器
     * 
     * 注意：channelRead0 和 send 方法是同步的
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-09-28
     * @version     v1.0
     * 
     * @param i_Data  此处不要明确类的类型。这样可以兼容更复杂的情况。如有自定义编码器的情况
     */
    public synchronized Data send(Object i_Data) throws InterruptedException
    {
        this.ctx.writeAndFlush(i_Data);
        this.wait();
        return this.response;
    }
    
    
    
    /**
     * 异常处理，一般是需要关闭通道的
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext i_Ctx ,Throwable i_Cause) throws Exception
    {
        i_Ctx.close();
        this.clientRPC.newOperation();
        
        $Logger.error(i_Cause.getMessage());
    }
    
}
