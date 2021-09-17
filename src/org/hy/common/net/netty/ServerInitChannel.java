package org.hy.common.net.netty;

import org.hy.common.xml.log.Logger;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;





/**
 * 通道初始化类
 * 
 * @author      ZhengWei(HY)
 * @createDate  2021-09-14
 * @version     v1.0
 */
public class ServerInitChannel<T extends Server<T>> extends ChannelInitializer<SocketChannel>
{

    private static final Logger $Logger = new Logger(ServerInitChannel.class ,true);
    
    private Server<T> myServer;
    
    
    
    public ServerInitChannel(Server<T> i_MyServer)
    {
        this.myServer = i_MyServer;
    }
    
    
    
    @Override
    protected void initChannel(SocketChannel i_Channel) throws Exception
    {
        $Logger.info("客户标示：" + i_Channel.hashCode());
        
        this.myServer.initChannel(i_Channel);
    }
    
}
