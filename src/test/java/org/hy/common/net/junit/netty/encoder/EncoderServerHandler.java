package org.hy.common.net.junit.netty.encoder;

import org.hy.common.xml.log.Logger;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;





/**
 * 业务的处理器：Handler
 * 
 * <MessageInfo> 表示客户端与服务端相互通讯的数据封装类。
 * 
 * @author      ZhengWei(HY)
 * @createDate  2021-09-22
 * @version     v1.0
 */
public class EncoderServerHandler extends SimpleChannelInboundHandler<MessageInfo>
{
    private static final Logger $Logger = new Logger(EncoderServerHandler.class ,true);
    
    
    
    @Override
    protected void channelRead0(ChannelHandlerContext i_Ctx ,MessageInfo i_Msg) throws Exception
    {
        $Logger.info("服务的处理器被调用："+ new String(i_Msg.getDatas() ,"UTF-8"));
    }



    @Override
    public void exceptionCaught(ChannelHandlerContext i_Ctx ,Throwable i_Cause) throws Exception
    {
        $Logger.error(i_Cause);
        super.exceptionCaught(i_Ctx ,i_Cause);
        i_Ctx.close();
    }
    
}
