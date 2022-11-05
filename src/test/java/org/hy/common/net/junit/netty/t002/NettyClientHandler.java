package org.hy.common.net.junit.netty.t002;


import org.hy.common.xml.log.Logger;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

public class NettyClientHandler extends ChannelInboundHandlerAdapter
{

    private static final Logger $Logger = new Logger(NettyClientHandler.class ,true);
    
    
    
    /**
     * 当通道就绪时触发
     */
    @Override
    public void channelActive(ChannelHandlerContext i_Ctx) throws Exception
    {
        i_Ctx.writeAndFlush(Unpooled.copiedBuffer("你好，服务器" ,CharsetUtil.UTF_8));
    }
    
    
    
    /**
     * 有读取数据时触发
     * 
     * i_Ctx：是上下文对象，包含管道pipline、通道channel、地址
     * i_Msg：客户端发送的数据
     */
    @Override
    public void channelRead(ChannelHandlerContext i_Ctx ,Object i_Msg) throws Exception
    {
        ByteBuf v_Buffer = (ByteBuf) i_Msg;
        
        $Logger.info("收到服务器" + i_Ctx.channel().remoteAddress() + "的回复：" + v_Buffer.toString(CharsetUtil.UTF_8));
    }
    
    
    
    /**
     * 异常处理，一般是需要关闭通道的
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext i_Ctx ,Throwable i_Cause) throws Exception
    {
        $Logger.error(i_Cause);
        i_Ctx.close();
    }
    
}
