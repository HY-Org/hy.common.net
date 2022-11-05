package org.hy.common.net.netty.rpc.encoder;

import org.hy.common.xml.log.Logger;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;





/**
 * 数据通讯类的编码器：粘包和拆包自定义处理
 * 
 * 【数据头(占4个字节)】 + 【数据体】
 * 
 * @author      ZhengWei(HY)
 * @createDate  2021-10-11
 * @version     v1.0
 */
public class ProtobufLengthHeadEncoder extends MessageToByteEncoder<ByteBuf>
{
    private static final Logger $Logger     = new Logger(ProtobufLengthHeadEncoder.class ,true);
    
    public static final int     $HeadLength = 4;
    
    

    public ProtobufLengthHeadEncoder()
    {
        super();
    }



    @Override
    protected void encode(ChannelHandlerContext i_Ctx ,ByteBuf i_Msg ,ByteBuf io_Out) throws Exception
    {
        try
        {
            int v_DataLength = i_Msg.readableBytes();
            
            io_Out.ensureWritable($HeadLength + v_DataLength);             // 前4个字节+数据长度
            io_Out.writeBytes(intToBytes(v_DataLength));                   // 把body的长度写到前四个字节，int转为网络需
            io_Out.writeBytes(i_Msg ,i_Msg.readerIndex() ,v_DataLength);
        }
        catch (Exception exce)
        {
            $Logger.error(exce);
            throw exce;
        }
    }

    

    /**
     * 写入的时候，把 int 转化为网络序
     * 
     * @param i_Value
     * @return
     */
    public static byte [] intToBytes(int i_Value)
    {
        byte [] v_Bytes = new byte[$HeadLength];
        
        v_Bytes[3] = (byte) (i_Value       & 0xff);
        v_Bytes[2] = (byte) (i_Value >> 8  & 0xff);
        v_Bytes[1] = (byte) (i_Value >> 16 & 0xff);
        v_Bytes[0] = (byte) (i_Value >> 24 & 0xff);
        
        return v_Bytes;
    }
    
}
