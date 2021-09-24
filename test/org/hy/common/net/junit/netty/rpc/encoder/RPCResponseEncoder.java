package org.hy.common.net.junit.netty.rpc.encoder;

import org.hy.common.net.data.CommunicationResponse;
import org.hy.common.xml.log.Logger;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;





/**
 * 自定义的编解码器
 * 
 * @author      ZhengWei(HY)
 * @createDate  2021-09-24
 * @version     v1.0
 */
public class RPCResponseEncoder extends MessageToByteEncoder<CommunicationResponse>
{
    private static final Logger $Logger = new Logger(RPCResponseEncoder.class ,true);
    
    public static final String $Charset  = "UTF-8";
    
    public static final long   $NullLong = Long.MIN_VALUE;
    
    
    
    @Override
    protected void encode(ChannelHandlerContext i_Ctx ,CommunicationResponse i_Msg ,ByteBuf io_Out) throws Exception
    {
        // 会话时间
        if ( i_Msg.getSessionTime() != null )
        {
            io_Out.writeLong(i_Msg.getSessionTime().getTime());
        }
        else
        {
            io_Out.writeLong($NullLong);
        }
        
        // 通讯的时间
        if ( i_Msg.getTime() != null )
        {
            io_Out.writeLong(i_Msg.getTime().getTime());
        }
        else
        {
            io_Out.writeLong($NullLong);
        }
        
        // 通讯票据
        if ( i_Msg.getToken() != null )
        {
            io_Out.writeInt(  i_Msg.getToken().getBytes($Charset).length);
            io_Out.writeBytes(i_Msg.getToken().getBytes($Charset));
        }
        else
        {
            io_Out.writeInt(0);
        }
        
        // 数据的XID
        if ( i_Msg.getDataXID() != null )
        {
            io_Out.writeInt(  i_Msg.getDataXID().getBytes($Charset).length);
            io_Out.writeBytes(i_Msg.getDataXID().getBytes($Charset));
        }
        else
        {
            io_Out.writeInt(0);
        }
        
        // 是否每次通过 XJava.getObject(id) 获取一个全新的对象实例
        io_Out.writeBoolean(i_Msg.getDataXIsNew());
        
        // 数据的过期时长
        io_Out.writeLong(i_Msg.getDataExpireTimeLen());
        
        // 通讯的结果类型
        io_Out.writeInt(i_Msg.getResult());
        
        // 通讯结束时间
        if ( i_Msg.getEndTime() != null )
        {
            io_Out.writeLong(i_Msg.getEndTime().getTime());
        }
        else
        {
            io_Out.writeLong($NullLong);
        }
        
        $Logger.info("响应编码完成");
    }
    
}
