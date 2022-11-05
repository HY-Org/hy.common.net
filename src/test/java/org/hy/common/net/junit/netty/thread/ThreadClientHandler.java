package org.hy.common.net.junit.netty.thread;

import org.hy.common.xml.log.Logger;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;





/**
 * 业务的处理器：Handler
 * 
 * <String> 表示客户端与服务端相互通讯的数据封装类。
 * 
 * @author      ZhengWei(HY)
 * @createDate  2021-09-23
 * @version     v1.0
 */
public class ThreadClientHandler extends SimpleChannelInboundHandler<String>
{
    
    private static final Logger $Logger = new Logger(ThreadClientHandler.class ,true);
    
    

    @Override
    public void channelActive(ChannelHandlerContext i_Ctx) throws Exception
    {
        i_Ctx.writeAndFlush("你好服务器");
        
        super.channelActive(i_Ctx);
    }



    @Override
    protected void channelRead0(ChannelHandlerContext i_Ctx ,String i_Msg) throws Exception
    {
        $Logger.info(i_Msg);
    }
    
    
    
    @Override
    public void exceptionCaught(ChannelHandlerContext i_Ctx ,Throwable i_Cause) throws Exception
    {
        $Logger.error(i_Cause);
        super.exceptionCaught(i_Ctx ,i_Cause);
        i_Ctx.close();
    }
    
}
