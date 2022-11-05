package org.hy.common.net.junit.netty.rpc.decoder;

import java.util.List;

import org.hy.common.Date;
import org.hy.common.net.data.CommunicationRequest;
import org.hy.common.net.junit.netty.rpc.encoder.RPCRequestEncoder;
import org.hy.common.xml.log.Logger;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;





/**
 * 自定义的解解码器
 * 
 * @author      ZhengWei(HY)
 * @createDate  2021-09-24
 * @version     v1.0
 */
public class RPCRequestDecoder extends ByteToMessageDecoder
{
    private static final Logger $Logger = new Logger(RPCRequestDecoder.class ,true);
    
    

    @Override
    protected void decode(ChannelHandlerContext i_Ctx ,ByteBuf i_In ,List<Object> io_Out) throws Exception
    {
        CommunicationRequest v_Request = new CommunicationRequest();
        long                 v_Long    = 0L;
        int                  v_Int     = 0;
        byte []              v_Bytes   = null;
        
        // 会话时间
        v_Long = i_In.readLong();
        if ( v_Long != RPCRequestEncoder.$NullLong )
        {
            v_Request.setSessionTime(new Date(v_Long));
        }
        
        // 通讯的时间
        v_Long = i_In.readLong();
        if ( v_Long != RPCRequestEncoder.$NullLong )
        {
            v_Request.setTime(new Date(v_Long));
        }
        
        // 通讯票据
        v_Int = i_In.readInt();
        if ( v_Int > 0 )
        {
            v_Bytes = new byte[v_Int];
            i_In.readBytes(v_Bytes);
            v_Request.setToken(new String(v_Bytes ,RPCRequestEncoder.$Charset));
        }
        
        // 数据的XID
        v_Int = i_In.readInt();
        if ( v_Int > 0 )
        {
            v_Bytes = new byte[v_Int];
            i_In.readBytes(v_Bytes);
            v_Request.setDataXID(new String(v_Bytes ,RPCRequestEncoder.$Charset));
        }
        
        // 是否每次通过 XJava.getObject(id) 获取一个全新的对象实例
        v_Request.setDataXIsNew(i_In.readBoolean());
        
        // 数据的过期时长
        v_Request.setDataExpireTimeLen(i_In.readLong());
        
        // 数据通讯的事件类型
        v_Int = i_In.readInt();
        if ( v_Int > 0 )
        {
            v_Bytes = new byte[v_Int];
            i_In.readBytes(v_Bytes);
            v_Request.setEventType(new String(v_Bytes ,RPCRequestEncoder.$Charset));
        }
        
        // 数据的操作类型
        v_Int = i_In.readInt();
        if ( v_Int > 0 )
        {
            v_Bytes = new byte[v_Int];
            i_In.readBytes(v_Bytes);
            v_Request.setDataOperation(new String(v_Bytes ,RPCRequestEncoder.$Charset));
        }
        
        // 是否返回数据
        v_Request.setRetunData(i_In.readBoolean());
        
        io_Out.add(v_Request);
        
        $Logger.info("请求解码完成");
    }
    
}
