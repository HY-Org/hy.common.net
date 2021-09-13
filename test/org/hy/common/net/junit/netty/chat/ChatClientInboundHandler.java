package org.hy.common.net.junit.netty.chat;

import org.hy.common.xml.log.Logger;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;





/**
 * 自定义一个处理器：Handler
 * 
 * <String> 表示客户端与服务端相互通讯的数据封装类
 * 
 * @author      ZhengWei(HY)
 * @createDate  2021-09-13
 * @version     v1.0
 */
public class ChatClientInboundHandler extends SimpleChannelInboundHandler<String>
{
    private static final Logger $Logger = new Logger(ChatClientInboundHandler.class ,true);
    
    

    @Override
    protected void channelRead0(ChannelHandlerContext i_Ctx ,String i_Msg) throws Exception
    {
        $Logger.info(i_Msg);
    }
}
