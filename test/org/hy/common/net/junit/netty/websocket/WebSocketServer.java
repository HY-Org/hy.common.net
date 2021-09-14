package org.hy.common.net.junit.netty.websocket;

import org.hy.common.net.netty.Server;

import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;





/**
 * WebSocket服务
 * 
 * @author      ZhengWei(HY)
 * @createDate  2021-09-14
 * @version     v1.0
 */
public class WebSocketServer extends Server
{

    @Override
    public void initChannel(SocketChannel i_Channel)
    {
        // 因为是基于Http协议的，所以要使用Http的编码和解码器
        
        ChannelPipeline v_Pipeline = i_Channel.pipeline();
        
        v_Pipeline.addLast(new HttpServerCodec());                   // HttpServerCodec 是Netty提供的Http协议的编码解码器
        v_Pipeline.addLast(new ChunkedWriteHandler());               // 是以块方式写
        v_Pipeline.addLast(new HttpObjectAggregator(1028 * 8));      // Http数据在传输过程中是分段的，此步就是将多个段聚合
        
        // WebSocket数据是以帧的方式传递的。
        // 浏览器的访问方式为 ws://127.0.0.1:2021/xxx
        // 此步的核心功能是将 Http 协议升级为 WebSocket 协议，保持长连接
        v_Pipeline.addLast(new WebSocketServerProtocolHandler("/hello"));
        v_Pipeline.addLast(new WebSocketInboundHandler());
    }
    
    
    
    public static void main(String [] args)
    {
        new WebSocketServer().setPort(2021).start();
    }
    
}
