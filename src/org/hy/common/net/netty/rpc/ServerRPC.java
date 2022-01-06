package org.hy.common.net.netty.rpc;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.hy.common.Help;
import org.hy.common.net.common.ServerOperation;
import org.hy.common.net.data.ClientUserInfo;
import org.hy.common.net.data.protobuf.CommunicationProto;
import org.hy.common.net.netty.Server;
import org.hy.common.net.netty.rpc.decoder.ProtobufLengthHeadDecoder;
import org.hy.common.net.netty.rpc.encoder.CommunicationResponseEncoder;
import org.hy.common.net.netty.rpc.encoder.LoginResponseEncoder;
import org.hy.common.net.netty.rpc.encoder.ProtobufLengthHeadEncoder;
import org.hy.common.net.protocol.ServerEventListener;
import org.hy.common.net.protocol.ServerValidate;
import org.hy.common.net.protocol.defaults.XJavaCommunicationListener;

import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;





/**
 * RPC + Protobuf的服务端。
 * 
 * 同时允许用户自定义部分的数据使用不同的数据格式协议（如 Json、Protobuf、编程语言的基本数据类型）
 * 
 * @author      ZhengWei(HY)
 * @createDate  2021-09-25
 * @version     v1.0
 *              v1.1  2022-01-04  添加：会话时间（单位：秒）。空闲多少时间后，移除登录会话
 */
public class ServerRPC extends Server<ServerRPC> implements ServerOperation
{
    
    /** 全局共享型的线程池 */
    protected EventExecutorGroup                 executorPool;
    
    /** 全局共享型的线程池的大小（默认：CPU核心数） */
    protected int                                executorPoolSize;
    
    /** 多长时间没有读（单位：秒） */
    protected long                               readerIdleTime;
    
    /** 多长时间没有写（单位：秒） */
    protected long                               writerIdleTime;
    
    /** 多长时间没有读写（单位：秒） */
    protected long                               allIdleTime;
    
    /** 会话时间（单位：秒）。空闲多少时间后，移除登录会话 */
    protected long                               sessionTime;
    
    /** 登陆验证接口。当为 null 时不验证，直接登陆成功 */
    protected ServerValidate                     validate;
    
    /**
     * 数据通讯事件的监听者集合。
     * 
     * 同样的事件类型，只能有一个监听者。
     */
    protected Map<String ,ServerEventListener>   listeners;
    
    /** 默认的数据通讯监听者 */
    protected ServerEventListener                defaultListener;
    
    /** 业务的处理器：Handler */
    protected ServerRPCHandler                   serverRPCHandler;
    
    
    
    public ServerRPC()
    {
        super();
        
        this.readerIdleTime   = 15;
        this.writerIdleTime   = 15;
        this.allIdleTime      = 30;
        this.sessionTime      = 60 * 60;
        this.executorPoolSize = Runtime.getRuntime().availableProcessors();
        
        this.listeners        = new Hashtable<String ,ServerEventListener>();
        this.defaultListener  = new XJavaCommunicationListener();
    }
    
    
    
    @Override
    public void initChannel(SocketChannel i_Channel ,ChannelPipeline i_Pipeline)
    {
        // 编码器采用：先进后出原则。即最后的编码器，优先编码
        i_Pipeline.addLast("编码器4" ,new ProtobufLengthHeadEncoder());
        i_Pipeline.addLast("编码器3" ,new ProtobufEncoder());
        i_Pipeline.addLast("编码器2" ,new CommunicationResponseEncoder());
        i_Pipeline.addLast("编码器1" ,new LoginResponseEncoder());
        
        // 解码器采用：先进先出原则。即最后的解码器，最后解码
        i_Pipeline.addLast("解码器1" ,new ProtobufLengthHeadDecoder());
        i_Pipeline.addLast("解码器2" ,new ProtobufDecoder(CommunicationProto.Data.getDefaultInstance()));  // 指定对哪种类型解码
        // 将会触发一个IdleStateEvent的事件，并且事件会传递到下一个处理器来处理，
        // 通过触发下一个Handler的userEventTrigged方法
        i_Pipeline.addLast("心跳器"  ,new IdleStateHandler(this.readerIdleTime ,this.writerIdleTime ,this.allIdleTime ,TimeUnit.SECONDS));
        i_Pipeline.addLast("业务器"  ,this.serverRPCHandler = new ServerRPCHandler(this));
    }
    
    
    
    /**
     * 获取：全局共享型的线程池
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-09-27
     * @version     v1.0
     * 
     * @return
     */
    public synchronized EventExecutorGroup getExecutorPool()
    {
        if ( this.executorPool == null )
        {
            this.executorPool = new DefaultEventExecutorGroup(this.executorPoolSize);
        }
        
        return this.executorPool;
    }
    
    
    
    /**
     * 默认的数据通讯监听者。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-01-16
     * @version     v1.0
     *
     * @return
     */
    public ServerEventListener getDefaultListener()
    {
        return this.defaultListener;
    }
    
    
    
    /**
     * 添加数据通讯的事件监听者
     * 
     * 配合XJava的XML文件配置
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-01-16
     * @version     v1.0
     *
     * @param i_Listener
     */
    public void setListener(ServerEventListener i_Listener)
    {
        this.addListener(i_Listener);
    }
    
    
    
    /**
     * 添加数据通讯的事件监听者
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-01-16
     * @version     v1.0
     *
     * @param i_Listener
     */
    public void addListener(ServerEventListener i_Listener)
    {
        if ( i_Listener == null )
        {
            throw new NullPointerException("ServerEventListener is null.");
        }
        
        if ( Help.isNull(i_Listener.getEventType()) )
        {
            throw new NullPointerException("ServerEventListener.getEventType() is null.");
        }
        
        this.listeners.put(i_Listener.getEventType() ,i_Listener);
    }
    
    
    
    /**
     * 获取事件类型的数据通讯的事件监听者
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-01-16
     * @version     v1.0
     *
     * @param i_EventType  事件类型（区分大小写）
     * @return
     */
    public ServerEventListener getListeners(String i_EventType)
    {
        return this.listeners.get(i_EventType);
    }
    
    
    
    /**
     * 移除数据通讯的事件监听者
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-01-16
     * @version     v1.0
     *
     * @param i_Listener
     */
    public void removeListener(ServerEventListener i_Listener)
    {
        if ( i_Listener == null )
        {
            throw new NullPointerException("ServerEventListener is null.");
        }
        
        if ( Help.isNull(i_Listener.getEventType()) )
        {
            throw new NullPointerException("ServerEventListener.getEventType() is null.");
        }
        
        this.listeners.remove(i_Listener.getEventType());
    }
    
    
    
    /**
     * 移除数据通讯的事件监听者
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-01-16
     * @version     v1.0
     *
     * @param i_Listener
     */
    public void removeListener(String i_EventType)
    {
        if ( Help.isNull(i_EventType) )
        {
            throw new NullPointerException("EventType is null.");
        }
        
        this.listeners.remove(i_EventType);
    }
    
    
    
    /**
     * 清空所有数据通讯的监听者
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-01-16
     * @version     v1.0
     *
     */
    public void clearListeners()
    {
        this.listeners.clear();
    }



    /**
     * 获取：多长时间没有读（单位：秒）
     * 
     * @return
     */
    public long getReaderIdleTime()
    {
        return readerIdleTime;
    }


    /**
     * 设置：多长时间没有读（单位：秒）
     * 
     * @param readerIdleTime
     */
    public ServerRPC setReaderIdleTime(long readerIdleTime)
    {
        this.readerIdleTime = readerIdleTime;
        return this;
    }


    /**
     * 获取：多长时间没有写（单位：秒）
     * 
     * @return
     */
    public long getWriterIdleTime()
    {
        return writerIdleTime;
    }


    /**
     * 设置：多长时间没有写（单位：秒）
     * 
     * @param writerIdleTime
     */
    public ServerRPC setWriterIdleTime(long writerIdleTime)
    {
        this.writerIdleTime = writerIdleTime;
        return this;
    }


    /**
     * 获取：多长时间没有读写（单位：秒）
     * 
     * @return
     */
    public long getAllIdleTime()
    {
        return allIdleTime;
    }


    /**
     * 设置：多长时间没有读写（单位：秒）
     * 
     * @param allIdleTime
     */
    public ServerRPC setAllIdleTime(long allIdleTime)
    {
        this.allIdleTime = allIdleTime;
        return this;
    }
    
    
    
    /**
     * 获取：登陆验证接口。当为 null 时不验证，直接登陆成功
     */
    public ServerValidate getValidate()
    {
        return validate;
    }


    
    /**
     * 设置：登陆验证接口。当为 null 时不验证，直接登陆成功
     * 
     * @param validate
     */
    public ServerRPC setValidate(ServerValidate validate)
    {
        this.validate = validate;
        return this;
    }



    /**
     * 获取：全局共享型的线程池的大小（默认：CPU核心数）
     */
    public int getExecutorPoolSize()
    {
        return executorPoolSize;
    }



    /**
     * 设置：全局共享型的线程池的大小（默认：CPU核心数）
     * 
     * @param validate
     */
    public ServerRPC setExecutorPoolSize(int executorPoolSize)
    {
        this.executorPoolSize = executorPoolSize;
        return this;
    }



    /**
     * 获取：会话时间（单位：秒）。空闲多少时间后，移除登录会话
     * 
     * @return
     */
    @Override
    public long getSessionTime()
    {
        return sessionTime;
    }



    /**
     * 设置：会话时间（单位：秒）。空闲多少时间后，移除登录会话
     * 
     * @param readerIdleTime
     */
    public void setSessionTime(long sessionTime)
    {
        this.sessionTime = sessionTime;
    }
    
    
    
    /**
     * 获取：登录的客户信息
     * 
     * @author      ZhengWei(HY)
     * @createDate  2022-01-04
     * @version     v1.0
     * 
     * @return
     */
    @Override
    public List<ClientUserInfo> getClientUsers()
    {
        if ( this.serverRPCHandler == null )
        {
            return new ArrayList<ClientUserInfo>();
        }
        
        return this.serverRPCHandler.getClientUsers();
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
        if ( this.serverRPCHandler == null )
        {
            return;
        }
        
        this.serverRPCHandler.reset();
    }
    
}
