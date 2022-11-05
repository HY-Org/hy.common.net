package org.hy.common.net.junit.netty.encoder;

import java.util.List;

import org.hy.common.xml.log.Logger;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;





/**
 * 自定义的解解码器
 * 
 * @author      ZhengWei(HY)
 * @createDate  2021-09-22
 * @version     v1.0
 */
public class MyDecoder extends ByteToMessageDecoder
{
    private static final Logger $Logger = new Logger(MyDecoder.class ,true);
    
    
    
    @Override
    protected void decode(ChannelHandlerContext i_Ctx ,ByteBuf i_In ,List<Object> io_Out) throws Exception
    {
        $Logger.info("解码器被调用");
        
        MessageInfo v_Messge = new MessageInfo();
        v_Messge.setDatasLength(i_In.readInt());
        v_Messge.setDatas(new byte[v_Messge.getDatasLength()]);
        
        i_In.readBytes(v_Messge.getDatas());
        
        io_Out.add(v_Messge);
    }
    
}
