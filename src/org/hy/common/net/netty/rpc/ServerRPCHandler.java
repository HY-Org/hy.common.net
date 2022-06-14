package org.hy.common.net.netty.rpc;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;

import org.hy.common.Date;
import org.hy.common.ExpireMap;
import org.hy.common.Help;
import org.hy.common.net.common.NetError;
import org.hy.common.net.data.CommunicationRequest;
import org.hy.common.net.data.CommunicationResponse;
import org.hy.common.net.data.LoginRequest;
import org.hy.common.net.data.LoginResponse;
import org.hy.common.net.data.NetException;
import org.hy.common.net.data.SessionInfo;
import org.hy.common.net.data.protobuf.CommunicationProto.Data;
import org.hy.common.net.data.protobuf.CommunicationProto.Request;
import org.hy.common.net.data.protobuf.CommunicationProtoDecoder;
import org.hy.common.net.data.protobuf.DataType;
import org.hy.common.net.protocol.ServerEventListener;
import org.hy.common.thread.Task;
import org.hy.common.thread.TaskPool;
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
    
    private static final Logger                                        $Logger   = new Logger(ServerRPCHandler.class ,true);
    
    /** 登录成功的客户端Channel及客户信息 */
    private static final ExpireMap<ChannelHandlerContext ,SessionInfo> $Sessions = new ExpireMap<ChannelHandlerContext ,SessionInfo>();
    
    
    
    /** 所属服务 */
    private ServerRPC mainServer;
    
    
    
    public ServerRPCHandler(ServerRPC i_ServerRPC)
    {
        super();
        this.mainServer = i_ServerRPC;
    }
    
    
    
    /**
     * 获取：会话信息
     * 
     * @author      ZhengWei(HY)
     * @createDate  2022-01-04
     * @version     v1.0
     * 
     * @return
     */
    protected List<SessionInfo> getSessions()
    {
        return Help.toList($Sessions);
    }
    
    
    
    /**
     * 重置统计数据
     * 
     * @author      ZhengWei(HY)
     * @createDate  2022-01-06
     * @version     v1.0
     *
     */
    public void reset()
    {
        for (SessionInfo v_Session : $Sessions.values())
        {
            v_Session.setRequestCount(0);
            v_Session.setActiveCount(0);
            v_Session.setActiveTimeLen(0);
            v_Session.setActiveTime(null);
            
            if ( v_Session.getNetExceptions() != null )
            {
                v_Session.getNetExceptions().clear();
            }
        }
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
        SessionInfo v_Session = $Sessions.get(i_Ctx);
        
        // 未登录
        if ( v_Session == null )
        {
            i_Ctx.writeAndFlush(this.login(i_Ctx ,i_Msg));
        }
        // 已登录
        else
        {
            v_Session.addRequestCount();
            v_Session.setActiveTime(new Date());
            $Sessions.put(i_Ctx ,v_Session ,this.mainServer.getSessionTime());  // 保持会话的有效性
            sendResponse(i_Ctx ,true ,this.mainActive(i_Ctx ,v_Session ,i_Msg));
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
    private void sendResponse(ChannelHandlerContext i_Ctx ,boolean i_IsRetunData ,CommunicationResponse i_Response)
    {
        if ( i_Response != null )
        {
            $Logger.debug("返回结果：" + i_Response.getResult());
            
            if ( i_IsRetunData )  // 客户端要求返回执行结果（注：仅用于异步操作的情况）
            {
                i_Ctx.writeAndFlush(i_Response);
            }
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
        SessionInfo v_Session = $Sessions.get(i_Ctx);
        if ( v_Session != null )
        {
            v_Session.setOnline(false);
            v_Session.setLogoutTime(new Date());
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
     * @param i_NewSession
     */
    private void removeExpireSameClientUser(SessionInfo i_NewSession)
    {
        if ( this.mainServer.getSameUserOnlineMaxCount() > 0 )
        {
            int v_OnlineCount = 0;
            for (Map.Entry<ChannelHandlerContext ,SessionInfo> v_Item : $Sessions.entrySet())
            {
                SessionInfo v_OldSession = v_Item.getValue();
                
                if ( v_OldSession.getUserName()  .equals(i_NewSession.getUserName())
                  && v_OldSession.getSystemName().equals(i_NewSession.getSystemName()) )
                {
                    if ( v_OnlineCount < this.mainServer.getSameUserOnlineMaxCount() )
                    {
                        v_OnlineCount++;
                    }
                    else
                    {
                        $Sessions.remove(v_Item.getKey());
                        v_Item.getKey().close();
                        break;
                    }
                }
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
        StringBuffer  v_Buf = new StringBuffer();
        LoginResponse v_Ret = new LoginResponse();
        
        v_Ret.setVersion(1);
        
        if ( Data.DataType.LoginRequest == i_Msg.getDataType() )
        {
            boolean      v_LoginRet      = true;
            LoginRequest v_CLoginRequest = CommunicationProtoDecoder.toLoginRequest(i_Msg.getLoginRequest() ,i_Ctx.channel().remoteAddress().toString());
            
            v_Ret.setSerialNo(v_CLoginRequest.getSerialNo());
            v_Buf.append(v_Ret.getSerialNo()).append("：接受类型：").append(DataType.getDataTypeName(i_Msg.getDataTypeValue()));
            
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
            
            v_Buf
            .append("; user=")  .append(i_Msg.getLoginRequest().getUserName())
            .append("; system=").append(i_Msg.getLoginRequest().getSystemName());
            
            if ( v_LoginRet )
            {
                InetSocketAddress v_ClientAddress = (InetSocketAddress)i_Ctx.channel().remoteAddress();
                SessionInfo       v_Session       = (SessionInfo)v_CLoginRequest;
                v_Session.setHost(v_ClientAddress.getAddress().getHostAddress());
                v_Session.setPort(v_ClientAddress.getPort());
                v_Session.setLoginTime(new Date());
                v_Session.setOnline(true);
                this.removeExpireSameClientUser(v_Session);
                $Sessions.put(i_Ctx ,v_Session ,this.mainServer.getSessionTime());
                
                v_Ret.setPort(this.mainServer.getPort());
                v_Ret.setResult(CommunicationResponse.$Succeed);
            }
            else
            {
                v_Ret.setResult(NetError.$Server_LoginValidateError);
            }
        }
        else
        {
            v_Buf.append("-：接受类型：").append(DataType.getDataTypeName(i_Msg.getDataTypeValue()));
            v_Ret.setSerialNo("-");  // 因无法确定消息流水号是什么，又不能为空，所以取横杠
            v_Ret.setResult(NetError.$Client_LoginTypeError);
        }
        
        v_Buf.append(" -> ").append(v_Ret.getResult());
        $Logger.debug(v_Buf.toString());
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
     * @param i_Session
     * @param i_Msg
     * @return
     */
    private CommunicationResponse mainActive(final ChannelHandlerContext i_Ctx ,final SessionInfo i_Session ,final Data i_Msg)
    {
        Date                  v_BTime       = new Date();
        Request               v_RequestData = i_Msg.getRequest();
        CommunicationRequest  v_CRequest    = null;
        ServerEventListener   v_Listener    = null;
        StringBuffer          v_Buf         = new StringBuffer();
        
        v_Buf.append(v_RequestData.getSerialNo());
        v_Buf.append("：接受类型：") .append(DataType.getDataTypeName(i_Msg.getDataTypeValue()));
        v_Buf.append("; user=")     .append(i_Session.getUserName());
        v_Buf.append("; system=")   .append(i_Session.getSystemName());
        v_Buf.append("; loginTime=").append(i_Session.getLoginTime().getFull());
        
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
        v_Buf.append("; ").append(v_CRequest.toString());
        
        // 同步处理机制（服务端的事件机制为同步时，并且客户端没有主动要求为异步时）
        if ( v_Listener.isSync() && !v_RequestData.getIsNonSync() )
        {
            v_Buf.append("; sync=1");
            $Logger.debug(v_Buf.toString());
            return execute(v_Listener ,v_CRequest ,i_Session ,v_BTime.getTime());
        }
        // 异步线程处理机制
        else
        {
            v_Buf.append("; sync=0");
            $Logger.debug(v_Buf.toString());
            TaskPool.putTask(new ServerRPCTask(this ,v_Listener ,v_CRequest ,i_Session ,v_BTime ,i_Ctx));
            
            // 客户端不要求返回执行结果，同时还希望服务是异步执行的情况下：返回成功即可
            if ( !v_CRequest.isRetunData() )
            {
                CommunicationResponse v_Reponse = new CommunicationResponse();
                
                v_Reponse.setSerialNo(         v_CRequest.getSerialNo());
                v_Reponse.setVersion(          v_CRequest.getVersion());
                v_Reponse.setSessionTime(      v_CRequest.getSessionTime());
                v_Reponse.setTime(             v_CRequest.getTime());
                v_Reponse.setToken(            v_CRequest.getToken());
                v_Reponse.setData(             null);
                v_Reponse.setDataXID(          v_CRequest.getDataXID());
                v_Reponse.setDataXIsNew(       v_CRequest.getDataXIsNew());
                v_Reponse.setDataExpireTimeLen(v_CRequest.getDataExpireTimeLen());
                v_Reponse.setResult(           CommunicationResponse.$Succeed);
                v_Reponse.setEndTime(          new Date());
                
                return v_Reponse;
            }
        }
       
        return null;
    }
    
    
    
    /**
     * 异步执行。数据通讯的监听事件接口的处理和执行
     * 
     * @author      ZhengWei(HY)
     * @createDate  2022-06-14
     * @version     v1.0
     * 
     * @param i_Listener
     * @param i_Request
     * @param i_Session
     * @param i_BTime
     * @param i_Ctx
     */
    public void executeNoSync(final ServerEventListener   i_Listener
                             ,final CommunicationRequest  i_Request
                             ,final SessionInfo           i_Session
                             ,final Date                  i_BTime
                             ,final ChannelHandlerContext i_Ctx)
    {
        sendResponse(i_Ctx ,i_Request.isRetunData() ,execute(i_Listener ,i_Request ,i_Session ,i_BTime.getTime()));
        
        /*
            final ServerEventListener  v_FListener = v_Listener;
            final CommunicationRequest v_FRequest  = v_CRequest;
            this.mainServer.getExecutorPool().submit(new Callable<Object>()
            {
                @Override
                public Object call() throws Exception
                {
                    sendResponse(i_Ctx ,v_FRequest.isRetunData() ,execute(v_FListener ,v_FRequest ,i_Session ,v_BTime.getTime()));
                    return null;
                }
            });
         */
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
     * @param i_Session     客户端的信息
     * @param i_BTime       通讯的开始时间
     * @return              返回给客户端的响应数据。在异常时，也能保证创建出响应对象
     */
    private CommunicationResponse execute(ServerEventListener i_Listener ,CommunicationRequest i_Request ,SessionInfo i_Session ,long i_BTime)
    {
        CommunicationResponse v_Reponse = null;
        
        try
        {
            $Logger.debug(i_Request.getSerialNo() + "：" + i_Listener.getEventType() + " start...");
            v_Reponse = i_Listener.communication(i_Request).setEndTime(new Date());
            $Logger.debug(i_Request.getSerialNo() + "：" + i_Listener.getEventType() + " finish.");
            
            if ( !i_Request.isRetunData() )
            {
                // 客户端要求：不返回执行的结果数据
                v_Reponse.setData(null);
            }
            
            // 是否返回成功，应由终端监听事件决定。本方法不应干涉，所以注释下行代码
            // v_Reponse.setResult(CommunicationResponse.$Succeed);
            
            Date v_ETime = new Date();
            i_Session.setActiveTime(v_ETime);
            i_Session.addActiveCount();
            i_Session.addActiveTimeLen(v_ETime.getTime() - i_BTime);
        }
        catch (Exception exce)
        {
            $Logger.error(exce);
            
            v_Reponse = new CommunicationResponse();
            
            v_Reponse.setSerialNo(         i_Request.getSerialNo());
            v_Reponse.setVersion(          i_Request.getVersion());
            v_Reponse.setSessionTime(      i_Request.getSessionTime());
            v_Reponse.setTime(             i_Request.getTime());
            v_Reponse.setToken(            i_Request.getToken());
            v_Reponse.setData(             null);
            v_Reponse.setDataXID(          i_Request.getDataXID());
            v_Reponse.setDataXIsNew(       i_Request.getDataXIsNew());
            v_Reponse.setDataExpireTimeLen(i_Request.getDataExpireTimeLen());
            v_Reponse.setResult(           NetError.$Server_ResponseDataError);
            v_Reponse.setEndTime(          new Date());
            
            i_Session.addException(new NetException(i_Request ,NetError.$Server_ResponseDataError ,"未知异常" ,exce));
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
                SessionInfo v_Session = $Sessions.get(i_Ctx);
                if ( v_Session != null )
                {
                    v_Session.setIdleTime(new Date());
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
        
        String      v_Error   = "";
        SessionInfo v_Session = $Sessions.get(i_Ctx);
        if ( v_Session != null )
        {
            v_Error = v_Session.toString();
        }
        $Logger.error(i_Cause.getMessage() + v_Error);
    }
    
    
    
    
    
    private static final String $TaskName = "ServerRPCTask";
    private static       long   $SerialNo = 0;
    /**
     * 注意：本方法可能在多个实例、多个线程中执行，所以要用 static synchronized
     * 
     * @return
     */
    private static synchronized long GetSerialNo()
    {
        return ++$SerialNo;
    }
    
    
    
    class ServerRPCTask extends Task<Object>
    {
        private ServerRPCHandler      rpcHandler;
        
        private ServerEventListener   listener;
        
        private CommunicationRequest  request;
        
        private SessionInfo           session;
        
        private Date                  btime;
        
        private ChannelHandlerContext ctx;
        
        
        
        public ServerRPCTask(ServerRPCHandler      i_RPCHandler
                            ,ServerEventListener   i_Listener
                            ,CommunicationRequest  i_Request
                            ,SessionInfo           i_Session
                            ,Date                  i_BTime
                            ,ChannelHandlerContext i_Ctx)
        {
            super($TaskName);
            
            this.rpcHandler = i_RPCHandler;
            this.listener   = i_Listener;
            this.request    = i_Request;
            this.session    = i_Session;
            this.btime      = i_BTime;
            this.ctx        = i_Ctx;
        }

        
        public ServerRPCTask(String i_TaskType)
        {
            super(i_TaskType);
        }
        
        

        @Override
        public String getTaskDesc()
        {
            return "SRPC:" + this.request.toString();
        }

        
        
        @Override
        public void execute()
        {
            try
            {
                this.rpcHandler.sendResponse(this.ctx ,this.request.isRetunData() ,this.rpcHandler.execute(this.listener ,this.request ,this.session ,this.btime.getTime()));
            }
            catch (Exception exce)
            {
                $Logger.error(exce);
            }
            this.finishTask();
        }

        
        
        @Override
        public long getSerialNo()
        {
            return GetSerialNo();
        }
        
    }
    
}
