package org.hy.common.net.netty.rpc.decoder;

import java.util.List;

import org.hy.common.net.netty.rpc.encoder.ProtobufLengthHeadEncoder;
import org.hy.common.xml.log.Logger;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;





/**
 * 数据通讯类的解码器：粘包和拆包自定义处理
 * 
 * 【数据头(占4个字节)】 + 【数据体】
 * 
 * @author      ZhengWei(HY)
 * @createDate  2021-10-11
 * @version     v1.0
 */
public class ProtobufLengthHeadDecoder extends ByteToMessageDecoder
{
    
    private static final Logger $Logger = new Logger(ProtobufLengthHeadDecoder.class ,true);
    
    

    public ProtobufLengthHeadDecoder()
    {
        super();
    }



    @Override
    protected void decode(ChannelHandlerContext i_Ctx ,ByteBuf i_In ,List<Object> io_Out) throws Exception
    {
        try
        {
            i_In.markReaderIndex();
            int v_ReaderIndex = i_In.readerIndex();
            i_In.markReaderIndex();
            
            byte [] v_DataLengthBytes = new byte[ProtobufLengthHeadEncoder.$HeadLength];
            if ( i_In.readableBytes() < ProtobufLengthHeadEncoder.$HeadLength )
            {
                // 有可能是端口探测工具发出的消息
                $Logger.debug("Data length less min length[" + ProtobufLengthHeadEncoder.$HeadLength + "]: " + i_In.readableBytes());
                return;
            }
            
            i_In.readBytes(v_DataLengthBytes);                             // 读取前4个字节
            int v_DataLength = bytesToInt(v_DataLengthBytes);              // 自定义字节序获取前四个字节表示的长度
            if ( v_ReaderIndex != i_In.readerIndex() )
            {
                if ( v_DataLength < 0 )
                {
                    $Logger.debug("Invalid data length: " + v_DataLength);
                    // 是要返回，但则此没有必要 return;
                }
                else
                {
                    if ( i_In.readableBytes() < v_DataLength )
                    {
                        i_In.resetReaderIndex();
                    }
                    else
                    {
                        io_Out.add(i_In.readRetainedSlice(v_DataLength));  // 读取相应长度的数据
                    }
                }
            }
        }
        catch (Exception exce)
        {
            $Logger.error(exce);
            throw exce;
        }
    }
    
    
    
    /**
     * 把 网络序 转化为 int
     * 
     * @param i_Bytes
     * @return
     */
    public static int bytesToInt(byte [] i_Bytes)
    {
        return i_Bytes[3] & 0xff | (i_Bytes[2] & 0xff) << 8 | (i_Bytes[1] & 0xff) << 16 | (i_Bytes[0] & 0xff) << 24;
    }
    
}
