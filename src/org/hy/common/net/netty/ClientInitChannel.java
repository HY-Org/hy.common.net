package org.hy.common.net.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;





/**
 * 通道初始化类
 * 
 * @author      ZhengWei(HY)
 * @createDate  2021-09-17
 * @version     v1.0
 */
public class ClientInitChannel<T extends Client<T>> extends ChannelInitializer<SocketChannel>
{
    
    private Client<T> myClient;
    
    
    
    public ClientInitChannel(Client<T> i_MyClient)
    {
        this.myClient = i_MyClient;
    }
    
    
    
    @Override
    protected void initChannel(SocketChannel i_Channel) throws Exception
    {
        this.myClient.initChannel(i_Channel ,i_Channel.pipeline());
    }
    
}
