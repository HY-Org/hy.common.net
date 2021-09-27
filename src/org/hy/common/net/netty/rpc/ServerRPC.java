package org.hy.common.net.netty.rpc;

import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.hy.common.Help;
import org.hy.common.net.CommunicationListener;
import org.hy.common.net.XJavaCommunicationListener;
import org.hy.common.net.data.protobuf.CommunicationProto;
import org.hy.common.net.netty.Server;

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
 */
public class ServerRPC extends Server<ServerRPC>
{
    
    /** 全局共享型的线程池 */
    protected EventExecutorGroup                 executorPool;
    
    /** 全局共享型的线程池的大小（默认：CPU核心数） */
    protected int                                executorPoolSize;
    
    /** 多长时间没有读 */
    protected long                               readerIdleTime;
    
    /** 多长时间没有写 */
    protected long                               writerIdleTime;
    
    /** 多长时间没有读写 */
    protected long                               allIdleTime;
    
    /** 登陆验证接口。当为 null 时不验证，直接登陆成功 */
    protected ServerRPCValidate                  validate;
    
    /**
     * 数据通讯事件的监听者集合。
     * 
     * 同样的事件类型，只能有一个监听者。
     */
    protected Map<String ,CommunicationListener> listeners;
    
    /** 默认的数据通讯监听者 */
    protected CommunicationListener              defaultListener;
    
    
    
    public ServerRPC()
    {
        super();
        
        this.readerIdleTime   = 15;
        this.writerIdleTime   = 15;
        this.allIdleTime      = 30;
        this.executorPoolSize = Runtime.getRuntime().availableProcessors();
        
        this.listeners        = new Hashtable<String ,CommunicationListener>();
        this.defaultListener  = new XJavaCommunicationListener();
    }
    
    
    
    @Override
    public void initChannel(SocketChannel i_Channel ,ChannelPipeline i_Pipeline)
    {
        i_Pipeline.addLast("编码器" ,new ProtobufEncoder());
        i_Pipeline.addLast("解码器" ,new ProtobufDecoder(CommunicationProto.Data.getDefaultInstance()));  // 指定对哪种类型解码
        // 将会触发一个IdleStateEvent的事件，并且事件会传递到下一个处理器来处理，
        // 通过触发下一个Handler的userEventTrigged方法
        i_Pipeline.addLast("心跳器" ,new IdleStateHandler(this.readerIdleTime ,this.writerIdleTime ,this.allIdleTime ,TimeUnit.SECONDS));
        i_Pipeline.addLast("业务器" ,new ServerRPCHandler(this));
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
    public CommunicationListener getDefaultListener()
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
    public void setListener(CommunicationListener i_Listener)
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
    public void addListener(CommunicationListener i_Listener)
    {
        if ( i_Listener == null )
        {
            throw new NullPointerException("CommunicationListener is null.");
        }
        
        if ( Help.isNull(i_Listener.getEventType()) )
        {
            throw new NullPointerException("CommunicationListener.getEventType() is null.");
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
    public CommunicationListener getListeners(String i_EventType)
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
    public void removeListener(CommunicationListener i_Listener)
    {
        if ( i_Listener == null )
        {
            throw new NullPointerException("CommunicationListener is null.");
        }
        
        if ( Help.isNull(i_Listener.getEventType()) )
        {
            throw new NullPointerException("CommunicationListener.getEventType() is null.");
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
     * 获取：多长时间没有读
     * 
     * @return
     */
    public long getReaderIdleTime()
    {
        return readerIdleTime;
    }


    /**
     * 设置：多长时间没有读
     * 
     * @param readerIdleTime
     */
    public ServerRPC setReaderIdleTime(long readerIdleTime)
    {
        this.readerIdleTime = readerIdleTime;
        return this;
    }


    /**
     * 获取：多长时间没有写
     * 
     * @return
     */
    public long getWriterIdleTime()
    {
        return writerIdleTime;
    }


    /**
     * 设置：多长时间没有写
     * 
     * @param writerIdleTime
     */
    public ServerRPC setWriterIdleTime(long writerIdleTime)
    {
        this.writerIdleTime = writerIdleTime;
        return this;
    }


    /**
     * 获取：多长时间没有读写
     * 
     * @return
     */
    public long getAllIdleTime()
    {
        return allIdleTime;
    }


    /**
     * 设置：多长时间没有读写
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
    public ServerRPCValidate getValidate()
    {
        return validate;
    }


    
    /**
     * 设置：登陆验证接口。当为 null 时不验证，直接登陆成功
     * 
     * @param validate
     */
    public ServerRPC setValidate(ServerRPCValidate validate)
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
    
}
