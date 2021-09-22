package org.hy.common.net.junit.netty.encoder;

import org.hy.common.Date;
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
public class EncoderClientHandler extends SimpleChannelInboundHandler<MessageInfo>
{

    private static final Logger $Logger = new Logger(EncoderClientHandler.class ,true);
    
    
    
    @Override
    public void channelActive(ChannelHandlerContext i_Ctx) throws Exception
    {
        for (int x=1; x<=10; x++)
        {
            MessageInfo v_Msg = new MessageInfo(Date.getNowTime().getFullMilli() + ":" + x);
            
            i_Ctx.writeAndFlush(v_Msg);   // 一次性发送10次
        }
        
        super.channelActive(i_Ctx);
    }

    
    
    @Override
    public void exceptionCaught(ChannelHandlerContext i_Ctx ,Throwable i_Cause) throws Exception
    {
        $Logger.error(i_Cause);
        super.exceptionCaught(i_Ctx ,i_Cause);
        i_Ctx.close();
    }

    
    
    @Override
    protected void channelRead0(ChannelHandlerContext i_Ctx ,MessageInfo i_Msg) throws Exception
    {
        
    }
    
}
