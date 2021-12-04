package org.hy.common.net.netty.rpc;

import java.lang.reflect.Proxy;

import org.hy.common.net.common.ClientCluster;
import org.hy.common.net.common.ClientOperation;
import org.hy.common.net.data.protobuf.CommunicationProto;
import org.hy.common.net.netty.Client;
import org.hy.common.net.netty.rpc.callable.ClientRPCOperationProxy;
import org.hy.common.net.netty.rpc.decoder.ProtobufLengthHeadDecoder;
import org.hy.common.net.netty.rpc.encoder.CommunicationRequestEncoder;
import org.hy.common.net.netty.rpc.encoder.LoginRequestEncoder;
import org.hy.common.net.netty.rpc.encoder.ProtobufLengthHeadEncoder;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;





/**
 * RPC + Protobuf的客户端。
 * 
 * 同时允许用户自定义部分的数据使用不同的数据格式协议（如 Json、Protobuf、编程语言的基本数据类型）
 * 
 * @author      ZhengWei(HY)
 * @createDate  2021-09-25
 * @version     v1.0
 */
public class ClientRPC extends Client<ClientRPC> implements ClientCluster
{
    
    /** 业务处理器 */
    private ClientRPCHandler clientHandler;
    
    /** 业务接口 */
    private ClientOperation  clientOperation;
    
    
    
    public ClientRPC()
    {
        super();
        this.newOperation();
    }
    
    
    
    @Override
    public void initChannel(SocketChannel i_Channel ,ChannelPipeline i_Pipeline)
    {
        // 编码器采用：先进后出原则。即最后的编码器，优先编码
        i_Pipeline.addLast("编码器4" ,new ProtobufLengthHeadEncoder());
        i_Pipeline.addLast("编码器3" ,new ProtobufEncoder());
        i_Pipeline.addLast("编码器2" ,new CommunicationRequestEncoder());
        i_Pipeline.addLast("编码器1" ,new LoginRequestEncoder());
        
        // 解码器采用：先进先出原则。即最后的解码器，最后解码
        i_Pipeline.addLast("解码器1" ,new ProtobufLengthHeadDecoder());  // 指定对哪种类型解码
        i_Pipeline.addLast("解码器2" ,new ProtobufDecoder(CommunicationProto.Data.getDefaultInstance()));  // 指定对哪种类型解码
        i_Pipeline.addLast("解码器3" ,this.clientHandler = new ClientRPCHandler(this));
    }
    
    
    
    /**
     * 启动服务，配置参数外界传入。为了重新登录而重写start方法
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-10-19
     * @version     v1.0
     * 
     * @param io_Bootstrap
     */
    @Override
    public synchronized ClientRPC start()
    {
        this.operation().logout();
        return super.start();
    }



    /**
     * 启动服务，配置参数外界传入。为了重新登录而重写start方法
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-10-19
     * @version     v1.0
     * 
     * @param io_Bootstrap
     */
    @Override
    public synchronized ClientRPC start(Bootstrap io_Bootstrap)
    {
        this.operation().logout();
        return super.start(io_Bootstrap);
    }


    
    /**
     * 关闭程序。退出而重写shutdown方法
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-09-16
     * @version     v1.0
     */
    @Override
    public synchronized void shutdown()
    {
        this.operation().logout();
        super.shutdown();
    }



    /**
     * 获取业务接口
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-09-28
     * @version     v1.0
     * 
     * @return
     */
    @Override
    public ClientOperation operation()
    {
        return this.clientOperation;
    }
    
    
    
    /**
     * 获取客户端的主机地址
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-10-12
     * @version     v1.0
     * 
     * @return
     */
    @Override
    public String getHost()
    {
        return super.getHost();
    }
    
    
    
    /**
     * 获取客户端的主机端口
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-10-12
     * @version     v1.0
     * 
     * @return
     */
    @Override
    public int getPort()
    {
        return super.getPort();
    }
    
    
    
    /**
     * 包内使用：构建一个新的业务接口
     * 
     * @return
     */
    private ClientOperation newOperation()
    {
        return this.clientOperation = (ClientOperation)Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader() ,new Class<?>[] {ClientOperation.class} ,new ClientRPCOperationProxy(this));
    }
    
    
    
    /**
     * 获取业务处理器
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-09-29
     * @version     v1.0
     * 
     * @return
     */
    public ClientRPCHandler clientHandler()
    {
        return this.clientHandler;
    }
    
}
