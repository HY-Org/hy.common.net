package org.hy.common.net.junit.netty.protobuf2;

import org.hy.common.xml.log.Logger;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;





/**
 * 业务的处理器：Handler
 * 
 * <TextWebSocketFrame> 表示客户端与服务端相互通讯的数据封装类。
 * 
 * @author      ZhengWei(HY)
 * @createDate  2021-09-15s
 * @version     v1.0
 */
public class ProtobufMoreObjectServerInboundHandler extends SimpleChannelInboundHandler<PeoplePOJO.People>
{
    
    private static final Logger $Logger = new Logger(ProtobufMoreObjectServerInboundHandler.class ,true);
    
    

    /**
     * 有读取数据时触发
     * 
     * 注意：费时操作请误在这里直接处理，而是使用Netty任务对列处理（提交到Channel对应的NioEventLoop的TaskQueue/ScheduleQueue中即可）
     * 
     * i_Ctx：是上下文对象，包含管道pipline、通道channel、地址
     * i_Msg：客户端发送的数据
     */
    @Override
    protected void channelRead0(ChannelHandlerContext i_Ctx ,PeoplePOJO.People i_Msg) throws Exception
    {
        $Logger.info(i_Msg);
        
        i_Ctx.channel().writeAndFlush("你好，客户端" + i_Msg.getDataTypeValue());
    }
    
}
