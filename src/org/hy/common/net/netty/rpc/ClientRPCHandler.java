package org.hy.common.net.netty.rpc;

import org.hy.common.Help;
import org.hy.common.net.data.Timeout;
import org.hy.common.net.data.protobuf.CommunicationProto.Data;
import org.hy.common.net.data.protobuf.DataType;
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
    private static final Logger $Logger = new Logger(ClientRPCHandler.class ,true);
    
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
        $Logger.debug("响应类型：" + DataType.getDataTypeName(i_Msg.getDataTypeValue()));
        this.response = i_Msg;
        this.notify();         // 唤醒等待的线程。即唤醒 call 方法
    }
    


    /**
     * 发消息给服务器
     * 
     * 注意：channelRead0 和 send 方法是同步的
     * 
     * 
     *   超时时长有三个级别，优先级从高到低依次为
     * 
     *     最高级（通讯级）：通讯数据的超时时长，取 Timeout 类的 xxxTimeout 属性
     * 
     *     中等级（应用级）：客户端上配置的超时时长，取 App 类的 timeout 属性
     *                     当最高级为配置时，本级生效。
     * 
     *     最低级（默认级）：当上两级均为配置时，本级生效，取 Timeout 类的可变常量值 $Default_xxx
     * 
     * 
     *   超时时长的取值规则：
     *     0表示永不超时，一直等待
     *     负数或NULL：表示取默认超时时长
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-09-28
     * @version     v1.0
     * 
     * @param i_Data  此处不要明确类的类型。这样可以兼容更复杂的情况。如有自定义编码器的情况
     */
    public synchronized Data send(Object i_Data) throws InterruptedException
    {
        String v_Info = this.clientRPC.getHost() + ":" + this.clientRPC.getPort();
        $Logger.debug(v_Info + " 请求类型：" + i_Data.toString());
        this.ctx.writeAndFlush(i_Data);
        
        $Logger.debug(v_Info + " 等待响应");
        long v_Timeout = Help.NVL(this.clientRPC.getTimeout() ,Timeout.$Default_WaitRequestTimeout);
        if ( i_Data instanceof Timeout )
        {
            v_Timeout = Help.NVL(((Timeout<?>)i_Data).getWaitRequestTimeout() ,v_Timeout);
            if ( v_Timeout < 1000L && v_Timeout != 0L )
            {
                v_Timeout = Timeout.$Default_WaitRequestTimeout;
            }
        }
        
        if ( v_Timeout == 0L )
        {
            this.wait();  // 0表示永不超时，一直等待
        }
        else
        {
            this.wait(v_Timeout);
        }
        
        if ( this.response != null )
        {
            $Logger.debug(v_Info + " 响应结果：" + (this.response.getDataTypeValue() == 1 ? this.response.getLoginResponse().getResult() : this.response.getResponse().getResult()));
        }
        else
        {
            $Logger.warn(v_Info + " 请求类型：" + i_Data.toString() + "，请求超时：" + v_Timeout);
        }
        
        return this.response;
    }
    
    
    
    /**
     * 异常处理，一般是需要关闭通道的
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext i_Ctx ,Throwable i_Cause) throws Exception
    {
        i_Ctx.close();
        this.clientRPC.shutdown();
        
        $Logger.error(i_Cause.getMessage());
    }
    
}
