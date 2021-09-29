package org.hy.common.net.netty.rpc;

import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.Callable;

import org.hy.common.Date;
import org.hy.common.Help;
import org.hy.common.net.data.ClientUserInfo;
import org.hy.common.net.data.CommunicationRequest;
import org.hy.common.net.data.CommunicationResponse;
import org.hy.common.net.data.LoginRequest;
import org.hy.common.net.data.LoginResponse;
import org.hy.common.net.data.protobuf.CommunicationProto.Data;
import org.hy.common.net.data.protobuf.CommunicationProto.Request;
import org.hy.common.net.data.protobuf.CommunicationProtoDecoder;
import org.hy.common.net.protocol.NetError;
import org.hy.common.net.protocol.ServerEventListener;
import org.hy.common.xml.log.Logger;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;





/**
 * 业务的处理器：Handler
 * 
 * <Data> 表示客户端与服务端相互通讯的数据封装类。
 * 
 * @author      ZhengWei(HY)
 * @createDate  2021-09-25
 * @version     v1.0
 */
public class ServerRPCHandler extends SimpleChannelInboundHandler<Data>
{
    
    private static final Logger                                     $Logger  = new Logger(ServerRPCHandler.class ,true);
    
    /** 登录成功的客户端Channel及客户信息 */
    private static final Map<ChannelHandlerContext ,ClientUserInfo> $Clients = new Hashtable<ChannelHandlerContext ,ClientUserInfo>();
    
    
    
    /** 所属服务 */
    private ServerRPC mainServer;
    
    
    
    public ServerRPCHandler(ServerRPC i_ServerRPC)
    {
        super();
        this.mainServer = i_ServerRPC;
    }
    
    

    /**
     * 有读取数据时触发
     * 
     * 注意：费时操作请误在这里直接处理，而是使用Netty任务对列处理（提交到Channel对应的NioEventLoop的TaskQueue/ScheduleQueue中即可）
     * 
     * i_Ctx：是上下文对象，包含管道pipline、通道channel、地址
     * i_Msg：客户端发送的数据
     */
    @Override
    protected void channelRead0(ChannelHandlerContext i_Ctx ,Data i_Msg) throws Exception
    {
        $Logger.info("接受通讯：" + i_Msg.toString());
        
        ClientUserInfo v_ClientUser = $Clients.get(i_Ctx);
        
        // 未登录
        if ( v_ClientUser == null )
        {
            i_Ctx.writeAndFlush(this.login(i_Ctx ,i_Msg));
        }
        // 已登录
        else
        {
            sendResponse(i_Ctx ,this.mainActive(i_Ctx ,v_ClientUser ,i_Msg));
        }
    }
    
    
    
    /**
     * 发送业务通讯的响应信息
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-09-27
     * @version     v1.0
     * 
     * @param i_Ctx
     * @param i_Response
     */
    private void sendResponse(ChannelHandlerContext i_Ctx ,CommunicationResponse i_Response)
    {
        if ( i_Response != null )
        {
            i_Ctx.writeAndFlush(i_Response);
        }
    }
    
    
    
    /**
     * 客户端退出
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-09-28
     * @version     v1.0
     * 
     * @param i_Ctx
     */
    private void logout(ChannelHandlerContext i_Ctx)
    {
        ClientUserInfo v_ClientUser = $Clients.get(i_Ctx);
        if ( v_ClientUser != null )
        {
            v_ClientUser.setOnline(false);
            v_ClientUser.setLogoutTime(new Date());
        }
    }
    
    
    
    /**
     * 删除相同客户端的过期信息。
     * 
     * 客户端在无法通讯后，客户端的信息是不会删除的，只有当客户端再次登录后，才为删除上次过期信息
     * 删除后，再添加刚登录的客户端信息。所有统计从重新开始计算
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-09-28
     * @version     v1.0
     * 
     * @param i_ClientUser
     */
    private void removeExpireSameClientUser(ClientUserInfo i_ClientUser)
    {
        for (Map.Entry<ChannelHandlerContext ,ClientUserInfo> v_Item : $Clients.entrySet())
        {
            ClientUserInfo v_ClientUser = v_Item.getValue();
            
            if ( v_ClientUser.getUserName()  .equals(i_ClientUser.getUserName())
              && v_ClientUser.getSystemName().equals(i_ClientUser.getSystemName()) )
            {
                $Clients.remove(v_Item.getKey());
                break;
            }
        }
    }
    
    
    
    /**
     * 登录验证
     * 
     * 1. 当要求必须登录验证时，即 ServerRPC.Validate 已实例化，将按 ServerRPCValidate 接口定义的方法验证
     * 2. 当允许游客登录访问时，即 ServerRPC.Validate 为空值时，也是要客户端传输必要信息的，如 LoginRequest[name ,systemName]
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-09-25
     * @version     v1.0
     * 
     * @param i_Ctx
     * @param i_Msg
     * @return
     */
    private LoginResponse login(final ChannelHandlerContext i_Ctx ,final Data i_Msg)
    {
        LoginResponse v_Ret = new LoginResponse();
        
        v_Ret.setVersion(1);
        
        if ( Data.DataType.LoginRequest == i_Msg.getDataType() )
        {
            boolean      v_LoginRet      = true;
            LoginRequest v_CLoginRequest = CommunicationProtoDecoder.toLoginRequest(i_Msg.getLoginRequest() ,i_Ctx.channel().remoteAddress().toString());
            
            // 要求登录验证
            if ( this.mainServer.getValidate() != null )
            {
                try
                {
                    v_LoginRet = this.mainServer.getValidate().validate(v_CLoginRequest);
                }
                catch (Exception exce)
                {
                    $Logger.error("登录验证异常" ,exce);
                }
            }
            // 允许游客访问
            else
            {
                v_LoginRet = !Help.isNull(i_Msg.getLoginRequest().getUserName())
                          && !Help.isNull(i_Msg.getLoginRequest().getSystemName());
            }
            
            if ( v_LoginRet )
            {
                ClientUserInfo v_ClientUser = (ClientUserInfo)v_CLoginRequest;
                v_ClientUser.setLoginTime(new Date());
                v_ClientUser.setOnline(true);
                this.removeExpireSameClientUser(v_ClientUser);
                $Clients.put(i_Ctx ,v_ClientUser);
                
                v_Ret.setPort(this.mainServer.getPort());
                v_Ret.setResult(CommunicationResponse.$Succeed);
            }
            else
            {
                v_Ret.setResult(NetError.$LoginValidateError);
            }
        }
        else
        {
            v_Ret.setResult(NetError.$LoginTypeError);
        }
        
        return v_Ret.setEndTime(new Date());
    }
    
    
    
    /**
     * 业务处理
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-09-25
     * @version     v1.0
     * 
     * @param i_Ctx
     * @param i_ClientUser
     * @param i_Msg
     * @return
     */
    private CommunicationResponse mainActive(final ChannelHandlerContext i_Ctx ,final ClientUserInfo i_ClientUser ,final Data i_Msg)
    {
        Date                  v_BTime       = new Date();
        Request               v_RequestData = i_Msg.getRequest();
        CommunicationRequest  v_CRequest    = null;
        ServerEventListener   v_Listener    = null;
        
        i_ClientUser.setActiveTime(v_BTime);
        
        if ( Help.isNull(i_Msg.getRequest().getEventType()) )
        {
            v_Listener = this.mainServer.getDefaultListener();
        }
        else
        {
            v_Listener = this.mainServer.getListeners(v_RequestData.getEventType());
            
            if ( v_Listener == null )
            {
                v_Listener = this.mainServer.getDefaultListener();
            }
        }
        
        v_CRequest = CommunicationProtoDecoder.toRequest(v_RequestData);
        
        // 同步处理机制（服务端的事件机制为同步时，并且客户端没有主动要求为异步时）
        if ( v_Listener.isSync() && !v_RequestData.getIsNonSync() )
        {
            return execute(v_Listener ,v_CRequest ,i_ClientUser ,v_BTime.getTime());
        }
        // 异步线程处理机制
        else
        {
            final ServerEventListener  v_FListener = v_Listener;
            final CommunicationRequest v_FRequest  = v_CRequest;
            this.mainServer.getExecutorPool().submit(new Callable<Object>()
            {
                @Override
                public Object call() throws Exception
                {
                    sendResponse(i_Ctx ,execute(v_FListener ,v_FRequest ,i_ClientUser ,v_BTime.getTime()));
                    return null;
                }
            });
        }
       
        return null;
    }
    
    
    
    /**
     * 执行。数据通讯的监听事件接口的处理和执行
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-09-27
     * @version     v1.0
     * 
     * @param i_Listener    监听事件接口
     * @param i_Request     客户端的请求数据
     * @param i_ClientUser  客户端的信息
     * @param i_BTime       通讯的开始时间
     * @return              返回给客户端的响应数据。在异常时，也能保证创建出响应对象
     */
    private CommunicationResponse execute(ServerEventListener i_Listener ,CommunicationRequest i_Request ,ClientUserInfo i_ClientUser ,long i_BTime)
    {
        CommunicationResponse v_Reponse = null;
        
        try
        {
            v_Reponse = i_Listener.communication(i_Request).setEndTime(new Date());;
            
            if ( !i_Request.isRetunData() )
            {
                // 客户端要求：不返回执行的结果数据
                v_Reponse.setData(null);
            }
            
            // 是否返回成功，应由终端监听事件决定。本方法不应干涉，所以注释下行代码
            // v_Reponse.setResult(CommunicationResponse.$Succeed);
            
            i_ClientUser.addActiveCount();
            i_ClientUser.addActiveTimeLen(Date.getNowTime().getTime() - i_BTime);
        }
        catch (Exception e)
        {
            $Logger.error(e);
            
            v_Reponse = new CommunicationResponse();
            
            v_Reponse.setVersion(          i_Request.getVersion());
            v_Reponse.setSessionTime(      i_Request.getSessionTime());
            v_Reponse.setTime(             i_Request.getTime());
            v_Reponse.setToken(            i_Request.getToken());
            v_Reponse.setData(             null);
            v_Reponse.setDataXID(          i_Request.getDataXID());
            v_Reponse.setDataXIsNew(       i_Request.getDataXIsNew());
            v_Reponse.setDataExpireTimeLen(i_Request.getDataExpireTimeLen());
            v_Reponse.setResult(           NetError.$ResponseDataError);
            v_Reponse.setEndTime(          new Date());
            
            i_ClientUser.addErrorCount();
        }
        
        return v_Reponse;
    }
    
    
    
    /**
     * 空闲检测的触发事件。有空闲检测事件，即表示通道是活着的
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext i_Ctx ,Object i_Event) throws Exception
    {
        if ( i_Event instanceof IdleStateEvent )
        {
            IdleStateEvent v_Event = (IdleStateEvent)i_Event;
            
            if ( v_Event.state() == IdleState.ALL_IDLE )
            {
                ClientUserInfo v_ClientUser = $Clients.get(i_Ctx);
                if ( v_ClientUser != null )
                {
                    v_ClientUser.setIdleTime(new Date());
                }
            }
        }
        
        super.userEventTriggered(i_Ctx ,i_Event);
    }
    
    
    
    /**
     * 通道非活动的事件
     */
    @Override
    public void channelInactive(ChannelHandlerContext i_Ctx) throws Exception
    {
        super.channelInactive(i_Ctx);
        this.logout(i_Ctx);
    }

    
    
    /**
     * 异常处理，一般是需要关闭通道的
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext i_Ctx ,Throwable i_Cause) throws Exception
    {
        this.logout(i_Ctx);
        i_Ctx.close();
        
        String         v_Error      = "";
        ClientUserInfo v_ClientUser = $Clients.get(i_Ctx);
        if ( v_ClientUser != null )
        {
            v_Error = v_ClientUser.toString();
        }
        $Logger.error(i_Cause.getMessage() + v_Error);
    }
    
}
