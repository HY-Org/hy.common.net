package org.hy.common.net.junit.netty.websocket;

import org.hy.common.Date;
import org.hy.common.xml.log.Logger;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;





/**
 * 业务的处理器：Handler
 * 
 * <TextWebSocketFrame> 表示客户端与服务端相互通讯的数据封装类。
 *  TextWebSocketFrame  表示一个文本帧
 * 
 * @author      ZhengWei(HY)
 * @createDate  2021-09-14
 * @version     v1.0
 */
public class WebSocketInboundHandler extends SimpleChannelInboundHandler<TextWebSocketFrame>
{

    private static final Logger $Logger = new Logger(WebSocketInboundHandler.class ,true);
    
    
    
    /**
     * 有读取数据时触发
     * 
     * 注意：费时操作请误在这里直接处理，而是使用Netty任务对列处理（提交到Channel对应的NioEventLoop的TaskQueue/ScheduleQueue中即可）
     * 
     * i_Ctx：是上下文对象，包含管道pipline、通道channel、地址
     * i_Msg：客户端发送的数据
     */
    @Override
    protected void channelRead0(ChannelHandlerContext i_Ctx ,TextWebSocketFrame i_Msg) throws Exception
    {
        $Logger.info(i_Ctx.channel().remoteAddress() + ":" + i_Ctx.channel().id() + "的消息：" + i_Msg.text());
        i_Ctx.channel().writeAndFlush(new TextWebSocketFrame("服务器的时间：" + Date.getNowTime().getFullMilli()));
    }



    @Override
    public void handlerAdded(ChannelHandlerContext i_Ctx) throws Exception
    {
        $Logger.info(i_Ctx.channel().remoteAddress() + ":" + i_Ctx.channel().id() + " 上线了");
        super.handlerAdded(i_Ctx);
    }



    @Override
    public void handlerRemoved(ChannelHandlerContext i_Ctx) throws Exception
    {
        $Logger.info(i_Ctx.channel().remoteAddress() + ":" + i_Ctx.channel().id() + " 退出了");
        super.handlerRemoved(i_Ctx);
    }



    @Override
    public void exceptionCaught(ChannelHandlerContext i_Ctx ,Throwable i_Cause) throws Exception
    {
        super.exceptionCaught(i_Ctx ,i_Cause);
    }
    
}
