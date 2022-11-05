package org.hy.common.net.junit.netty.protobuf;

import org.hy.common.xml.log.Logger;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;





/**
 * 业务的处理器：Handler
 * 
 * <String> 表示客户端与服务端相互通讯的数据封装类。
 * 
 * @author      ZhengWei(HY)
 * @createDate  2021-09-17
 * @version     v1.0
 */
public class ProtobufClientInboundHandler extends SimpleChannelInboundHandler<String>
{
    private static final Logger $Logger = new Logger(ProtobufClientInboundHandler.class ,true);
    
    

    @Override
    public void channelActive(ChannelHandlerContext i_Ctx) throws Exception
    {
        i_Ctx.channel().writeAndFlush(DataPOJO.Data.newBuilder().setId(20210917).setName("爱学习").build());
        
        super.channelActive(i_Ctx);
    }
    
    

    @Override
    protected void channelRead0(ChannelHandlerContext i_Ctx ,String i_Msg) throws Exception
    {
        $Logger.info(i_Msg);
    }
    
}
