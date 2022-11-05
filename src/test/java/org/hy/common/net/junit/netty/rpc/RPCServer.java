package org.hy.common.net.junit.netty.rpc;

import org.hy.common.net.junit.netty.rpc.decoder.RPCRequestDecoder;
import org.hy.common.net.junit.netty.rpc.encoder.RPCResponseEncoder;
import org.hy.common.net.netty.Server;

import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;





/**
 * RPC的服务端
 * 
 * @author      ZhengWei(HY)
 * @createDate  2021-09-24
 * @version     v1.0
 */
public class RPCServer extends Server<RPCServer>
{

    @Override
    public void initChannel(SocketChannel i_Channel ,ChannelPipeline i_Pipeline)
    {
        i_Pipeline.addLast(new RPCRequestDecoder());
        i_Pipeline.addLast(new RPCResponseEncoder());
        i_Pipeline.addLast(new RPCServerHandler());
    }
    
    
    
    public static void main(String [] args)
    {
        new RPCServer().setPort(2021).start();
    }
    
}
