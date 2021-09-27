package org.hy.common.net.junit.protobuf;

import org.hy.common.net.junit.netty.protobuf.DataPOJO;
import org.hy.common.net.protobuf.ProtobufHelp;
import org.hy.common.xml.log.Logger;





/**
 * 测试单元：Protobuf的序列化及反序列化
 *
 * @author      ZhengWei(HY)
 * @createDate  2021-09-25
 * @version     v1.0
 */
public class JU_Protobuf
{
    private static final Logger $Logger = new Logger(JU_Protobuf.class ,true);
    
    
    
    
    public static void main(String [] args)
    {
        byte [] v_DataByte = ProtobufHelp.encoder(DataPOJO.Data.newBuilder().setId(20210925).setName("爱学习").build());
        Object  v_Data     = ProtobufHelp.decoder(DataPOJO.Data.class ,v_DataByte);
        
        $Logger.info("反序列化后的：名称=" + ((DataPOJO.Data)v_Data).getName());
        $Logger.info("反序列化后的：数据=" + ((DataPOJO.Data)v_Data).getId());
    }
    
}
