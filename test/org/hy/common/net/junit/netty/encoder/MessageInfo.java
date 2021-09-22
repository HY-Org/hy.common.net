package org.hy.common.net.junit.netty.encoder;

import java.nio.charset.Charset;

import org.hy.common.Help;

/**
 * 消息类
 * 
 * 注意：此类经可以不用接口序列化接口
 * 
 * @author      ZhengWei(HY)
 * @createDate  2021-09-22
 * @version     v1.0
 */
public class MessageInfo
{
    
    /** 数据长度 */
    private int     datasLength;
    
    /** 数据的二进制形式 */
    private byte [] datas;
    
    
    
    public MessageInfo()
    {
        this(null);
    }
    
    
    public MessageInfo(String i_Message)
    {
        if ( !Help.isNull(i_Message) )
        {
            this.datas       = i_Message.getBytes(Charset.forName("UTF-8"));
            this.datasLength = this.datas.length;
        }
    }
    
    
    public int getDatasLength()
    {
        return datasLength;
    }

    
    public void setDatasLength(int datasLength)
    {
        this.datasLength = datasLength;
    }

    
    public byte [] getDatas()
    {
        return datas;
    }

    
    public void setDatas(byte [] datas)
    {
        this.datas = datas;
    }
    
}
