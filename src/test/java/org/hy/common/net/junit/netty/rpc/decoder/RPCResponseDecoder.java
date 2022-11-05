package org.hy.common.net.junit.netty.rpc.decoder;

import java.util.List;

import org.hy.common.Date;
import org.hy.common.net.data.CommunicationResponse;
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
public class RPCResponseDecoder extends ByteToMessageDecoder
{
    private static final Logger $Logger = new Logger(RPCResponseDecoder.class ,true);
    
    

    @Override
    protected void decode(ChannelHandlerContext i_Ctx ,ByteBuf i_In ,List<Object> io_Out) throws Exception
    {
        CommunicationResponse v_Response = new CommunicationResponse();
        long                  v_Long    = 0L;
        int                   v_Int     = 0;
        byte []               v_Bytes   = null;
        
        // 会话时间
        v_Long = i_In.readLong();
        if ( v_Long != RPCRequestEncoder.$NullLong )
        {
            v_Response.setSessionTime(new Date(v_Long));
        }
        
        // 通讯的时间
        v_Long = i_In.readLong();
        if ( v_Long != RPCRequestEncoder.$NullLong )
        {
            v_Response.setTime(new Date(v_Long));
        }
        
        // 通讯票据
        v_Int = i_In.readInt();
        if ( v_Int > 0 )
        {
            v_Bytes = new byte[v_Int];
            i_In.readBytes(v_Bytes);
            v_Response.setToken(new String(v_Bytes ,RPCRequestEncoder.$Charset));
        }
        
        // 数据的XID
        v_Int = i_In.readInt();
        if ( v_Int > 0 )
        {
            v_Bytes = new byte[v_Int];
            i_In.readBytes(v_Bytes);
            v_Response.setDataXID(new String(v_Bytes ,RPCRequestEncoder.$Charset));
        }
        
        // 是否每次通过 XJava.getObject(id) 获取一个全新的对象实例
        v_Response.setDataXIsNew(i_In.readBoolean());
        
        // 数据的过期时长
        v_Response.setDataExpireTimeLen(i_In.readLong());
        
        // 通讯的结果类型
        v_Response.setResult(i_In.readInt());
        
        // 通讯结束时间
        v_Long = i_In.readLong();
        if ( v_Long != RPCRequestEncoder.$NullLong )
        {
            v_Response.setEndTime(new Date(v_Long));
        }
        
        io_Out.add(v_Response);
        
        $Logger.info("响应解码完成");
    }
    
}
