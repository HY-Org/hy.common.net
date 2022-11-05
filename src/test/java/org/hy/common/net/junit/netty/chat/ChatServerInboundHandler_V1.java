package org.hy.common.net.junit.netty.chat;

import org.hy.common.xml.log.Logger;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.concurrent.GlobalEventExecutor;





/**
 * 自定义一个处理器：Handler
 * 
 * <String> 表示客户端与服务端相互通讯的数据封装类
 * 
 * @author      ZhengWei(HY)
 * @createDate  2021-09-13
 * @version     v1.0
 */
public class ChatServerInboundHandler_V1 extends SimpleChannelInboundHandler<String>
{
    private static final Logger $Logger       = new Logger(ChatServerInboundHandler_V1.class ,true);
    
    // 定义一个Channel组，管理所有的Channel
    // GlobalEventExecutor.INSTANCE：是全局的事件执行器，是一个单例
    private static ChannelGroup $ChannelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    
    
    
    /**
     * 当连接建立后，第一个被执行
     */
    @Override
    public void handlerAdded(ChannelHandlerContext i_Ctx) throws Exception
    {
        Channel v_Channel = i_Ctx.channel();
        
        // 将新客户加入的信息推送给其它的在线客户端
        $ChannelGroup.writeAndFlush("客户端" + v_Channel.remoteAddress() + "加入聊天");
        $ChannelGroup.add(v_Channel);
        
        super.handlerAdded(i_Ctx);
    }
    
    
    
    /**
     * 断开连接
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext i_Ctx) throws Exception
    {
        Channel v_Channel = i_Ctx.channel();
        $ChannelGroup.writeAndFlush("客户端" + v_Channel.remoteAddress() + "离开聊天");   // handlerRemoved() 会自动删除断开连接的Channel，不用再手工删除哈
        
        $Logger.info(i_Ctx.channel().remoteAddress() + " 退出了");
        super.handlerRemoved(i_Ctx);
    }

    

    /**
     * 通道就绪事件
     */
    @Override
    public void channelActive(ChannelHandlerContext i_Ctx) throws Exception
    {
        $Logger.info(i_Ctx.channel().remoteAddress() + " 上线了");
        super.channelActive(i_Ctx);
    }

    

    /**
     * 通道非活动的事件
     */
    @Override
    public void channelInactive(ChannelHandlerContext i_Ctx) throws Exception
    {
        $Logger.info(i_Ctx.channel().remoteAddress() + " 离线了");
        super.channelInactive(i_Ctx);
    }
    
    
    
    /**
     * 发生异常
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext i_Ctx ,Throwable i_Cause) throws Exception
    {
        $Logger.error(i_Cause);
        i_Ctx.close();
        super.exceptionCaught(i_Ctx ,i_Cause);
    }



    /**
     * 空闲检测的触发事件
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext i_Ctx ,Object i_Event) throws Exception
    {
        if ( i_Event instanceof IdleStateEvent )
        {
            IdleStateEvent v_Event     = (IdleStateEvent)i_Event;
            String         v_EventName = "";
            
            switch ( v_Event.state() )
            {
                case READER_IDLE:
                    v_EventName = "读空闲";
                    break;
                    
                case WRITER_IDLE:
                    v_EventName = "写空闲";
                    break;
                    
                case ALL_IDLE:
                    v_EventName = "读写空闲";
                    break;
            }
            
            // 有空闲检测事件，即表示通道是活着的
            $Logger.info(v_EventName);
        }
        
        super.userEventTriggered(i_Ctx ,i_Event);
    }



    @Override
    protected void channelRead0(ChannelHandlerContext i_Ctx ,String i_Msg) throws Exception
    {
        Channel v_Channel = i_Ctx.channel();
        
        $ChannelGroup.forEach(fi_Channel ->
        {
            if ( v_Channel != fi_Channel )
            {
                fi_Channel.writeAndFlush("客户" + v_Channel.remoteAddress() + "的消息：" + i_Msg);
            }
        });
        
        v_Channel.writeAndFlush("消息发送成功");
    }
    
}
