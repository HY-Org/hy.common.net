package org.hy.common.net.junit.netty.httpServer;

import org.hy.common.xml.log.Logger;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;





/**
 * 自定义一个处理器：Handler
 * 
 * <HttpObject> 表示客户端与服务端相互通讯的数据封装类
 * 
 * @author      ZhengWei(HY)
 * @createDate  2021-09-10
 * @version     v1.0
 */
public class TomcatInboundHandler extends SimpleChannelInboundHandler<HttpObject>
{
    
    private static final Logger $Logger = new Logger(TomcatInboundHandler.class ,true);
    
    
    
    /**
     * 有读取数据时触发
     * 
     * 注意：费时操作请误在这里直接处理，而是使用Netty任务对列处理（提交到Channel对应的NioEventLoop的TaskQueue/ScheduleQueue中即可）
     * 
     * i_Ctx：是上下文对象，包含管道pipline、通道channel、地址
     * i_Msg：客户端发送的数据
     */
    @Override
    protected void channelRead0(ChannelHandlerContext i_Ctx ,HttpObject i_Msg) throws Exception
    {
        
        if ( i_Msg instanceof HttpRequest )
        {
            HttpRequest v_Request = (HttpRequest) i_Msg;
            $Logger.info(Thread.currentThread().getName() + "：" + i_Ctx.channel().remoteAddress() + "请求资源：" + v_Request.uri());
            
            if ( v_Request.uri().indexOf("favicon.ico") >= 0 )
            {
                return;
            }
            
            ByteBuf          v_ResContent = Unpooled.copiedBuffer("Hello ,我是服务器" ,CharsetUtil.UTF_8);
            FullHttpResponse v_Response   = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1 ,HttpResponseStatus.OK ,v_ResContent);
            
            v_Response.headers().set(HttpHeaderNames.CONTENT_TYPE   ,"text/plain;charset=UTF-8");
            v_Response.headers().set(HttpHeaderNames.CONTENT_LENGTH ,v_ResContent.readableBytes());
            
            i_Ctx.writeAndFlush(v_Response);
        }
        
    }
    
}
