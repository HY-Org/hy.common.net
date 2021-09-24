package org.hy.common.net.junit.netty.rpc;

import org.hy.common.Date;
import org.hy.common.net.data.CommunicationRequest;
import org.hy.common.net.data.CommunicationResponse;
import org.hy.common.xml.log.Logger;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;





/**
 * 业务的处理器：Handler
 * 
 * <CommunicationRequest> 表示客户端与服务端相互通讯的数据封装类。
 * 
 * @author      ZhengWei(HY)
 * @createDate  2021-09-24
 * @version     v1.0
 */
public class RPCServerHandler extends SimpleChannelInboundHandler<CommunicationRequest>
{
    
    private static final Logger $Logger = new Logger(RPCServerHandler.class ,true);
    
    

    @Override
    protected void channelRead0(ChannelHandlerContext i_Ctx ,CommunicationRequest i_Msg) throws Exception
    {
        $Logger.info(i_Msg);
        
        CommunicationResponse v_Response = new CommunicationResponse();
        
        v_Response.setResult(1);
        v_Response.setEndTime(new Date());
        
        i_Ctx.writeAndFlush(v_Response);
    }
    
    
    
    @Override
    public void exceptionCaught(ChannelHandlerContext i_Ctx ,Throwable i_Cause) throws Exception
    {
        super.exceptionCaught(i_Ctx ,i_Cause);
        i_Ctx.close();
    }
    
}
