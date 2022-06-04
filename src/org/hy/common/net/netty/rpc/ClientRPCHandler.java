package org.hy.common.net.netty.rpc;

import java.util.Hashtable;
import java.util.Map;

import org.hy.common.Help;
import org.hy.common.StringHelp;
import org.hy.common.net.data.Communication;
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
    
    /**
     * 服务端的返回结果
     * Map.key   消息流水号 Communication.serialNo
     * Map.value 返回结果
     */
    private Map<String ,Data>     responseMap;
    
    
    
    public ClientRPCHandler(ClientRPC i_ClientRPC)
    {
        super();
        this.clientRPC   = i_ClientRPC;
        this.responseMap = new Hashtable<String ,Data>();
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
    
    
    
    public ChannelHandlerContext gatChannelHandlerContext()
    {
        return this.ctx;
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
        String v_ResponseSerialNo = "";
        if ( i_Msg.getDataTypeValue() == DataType.$Response )
        {
            v_ResponseSerialNo = i_Msg.getResponse().getSerialNo();
        }
        else
        {
            v_ResponseSerialNo = i_Msg.getLoginResponse().getSerialNo();
        }
        
        // $Logger.debug(v_ResponseSerialNo + "：响应类型：" + DataType.getDataTypeName(i_Msg.getDataTypeValue()));
        this.responseMap.put(v_ResponseSerialNo ,i_Msg);
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
     *                     当最高级未配置时，本级生效。
     * 
     *     最低级（默认级）：当上两级均未配置时，本级生效，取 Timeout 类的可变常量值 $Default_xxx
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
        String v_ResponseSerialNo = "";
        long   v_Timeout          = Help.NVL(this.clientRPC.getTimeout() ,Communication.$Default_WaitRequestTimeout);
        
        if ( i_Data instanceof Communication )
        {
            Communication<?> v_RequestData = (Communication<?>)i_Data;
            if ( Help.isNull(v_RequestData.getSerialNo()) )      // 允许外界自定义消息流水号
            {
                v_RequestData.setSerialNo(StringHelp.getUUID()); // 自动生成消息流水号
            }
            v_ResponseSerialNo = v_RequestData.getSerialNo();
            
            v_Timeout = Help.NVL(((Communication<?>)i_Data).getWaitRequestTimeout() ,v_Timeout);
            if ( v_Timeout < 1000L && v_Timeout != 0L )
            {
                v_Timeout = Communication.$Default_WaitRequestTimeout;
            }
        }
        
        $Logger.debug(v_ResponseSerialNo + "：请求类型：" + i_Data.toString() + " " + this.clientRPC.getHostPort() + " 超时类型：" + v_Timeout);
        this.ctx.writeAndFlush(i_Data);
        
        if ( v_Timeout == 0L )
        {
            this.wait();  // 0表示永不超时，一直等待
        }
        else
        {
            this.wait(v_Timeout);
        }
        
        Data v_Response = this.responseMap.remove(v_ResponseSerialNo);
        if ( v_Response != null )
        {
            $Logger.debug(v_ResponseSerialNo + "：响应结果：" + (v_Response.getDataTypeValue() == DataType.$LoginResponse ? v_Response.getLoginResponse().getResult() : v_Response.getResponse().getResult()));
        }
        else
        {
            $Logger.warn(v_ResponseSerialNo + "：响应结果：异常");
        }
        
        return v_Response;
    }
    
    
    
    /**
     * 异常处理，一般是需要关闭通道的
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext i_Ctx ,Throwable i_Cause) throws Exception
    {
        i_Ctx.close();
        this.clientRPC.shutdown();
        
        $Logger.error(this.clientRPC.getHost() + ":" + this.clientRPC.getPort() + " " + i_Cause.getMessage());
    }
    
}
