package org.hy.common.net.junit.netty.encoder;

import org.hy.common.xml.log.Logger;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;





/**
 * 自定义的编解码器
 * 
 * @author      ZhengWei(HY)
 * @createDate  2021-09-22
 * @version     v1.0
 */
public class MyEncoder extends MessageToByteEncoder<MessageInfo>
{

    private static final Logger $Logger = new Logger(MyEncoder.class ,true);
    
    
    
    @Override
    protected void encode(ChannelHandlerContext i_Ctx ,MessageInfo i_Msg ,ByteBuf io_Out) throws Exception
    {
        $Logger.info("编码器被调用");
        
        io_Out.writeInt(  i_Msg.getDatasLength());
        io_Out.writeBytes(i_Msg.getDatas());
    }
    
}
