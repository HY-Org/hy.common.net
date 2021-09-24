package org.hy.common.net.junit.netty.rpc;

import java.lang.reflect.Proxy;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.hy.common.net.data.CommunicationResponse;
import org.hy.common.net.junit.netty.rpc.decoder.RPCResponseDecoder;
import org.hy.common.net.junit.netty.rpc.encoder.RPCRequestEncoder;
import org.hy.common.net.netty.Client;
import org.hy.common.xml.log.Logger;

import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;





/**
 * RPC的客户端
 * 
 * @author      ZhengWei(HY)
 * @createDate  2021-09-24
 * @version     v1.0
 */
public class RPCClient extends Client<RPCClient>
{

    private static final Logger $Logger = new Logger(RPCClient.class ,true);
    
    // 按CPU的核数创建线程池
    private static ExecutorService $ThreadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    
    private RPCClientHandler clientHandler;
    
    
    
    @Override
    public void initChannel(SocketChannel i_Channel ,ChannelPipeline i_Pipeline)
    {
        i_Pipeline.addLast(new RPCRequestEncoder());
        i_Pipeline.addLast(new RPCResponseDecoder());
        i_Pipeline.addLast(this.clientHandler = new RPCClientHandler());
    }
    
    
    
    public Object getBean(final Class<?> i_Class)
    {
        return Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader() ,new Class<?>[] {i_Class} ,(proxy ,method ,args) ->
        {
            clientHandler.setDataExpireTimeLen(1024);
            return $ThreadPool.submit(clientHandler).get();
        });
    }
    
    
    
    public static void main(String [] args)
    {
        RPCClient v_Client = new RPCClient().setHost("127.0.0.1").setPort(2021);
        v_Client.start();
        
        RPCInterface          v_RPC      = (RPCInterface)v_Client.getBean(RPCInterface.class);
        CommunicationResponse v_Response = v_RPC.hello();
        
        $Logger.info(v_Response);
    }
    
}
