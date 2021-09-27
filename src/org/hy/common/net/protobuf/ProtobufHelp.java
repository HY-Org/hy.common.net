package org.hy.common.net.protobuf;

import java.lang.reflect.Method;

import org.hy.common.Help;
import org.hy.common.StaticReflect;
import org.hy.common.xml.log.Logger;

import com.google.protobuf.GeneratedMessageV3;





/**
 * Protobuf的辅助类
 *
 * @author      ZhengWei(HY)
 * @createDate  2021-09-25
 * @version     v1.0
 */
public class ProtobufHelp
{
    
    private static final Logger $Logger = new Logger(ProtobufHelp.class ,true);
    
    
    
    private ProtobufHelp()
    {
        
    }
    
    
    
    /**
     * 编码：对象转字节（序列化）
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-09-25
     * @version     v1.0
     * 
     * @param i_ProtobufObject  Protobuf的对象实例
     * @return
     */
    public static byte [] encoder(GeneratedMessageV3 i_ProtobufObject)
    {
        return i_ProtobufObject.toByteArray();
    }
    
    
    
    /**
     * 解码：字节转对象（反序列化）
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-09-25
     * @version     v1.0
     * 
     * @param i_ProtobufClass  Protobuf对象类型
     * @param i_ProtobufBytes  Protobuf字节数据，即encoder方法的返回值
     * @return
     */
    public static Object decoder(Class<? extends GeneratedMessageV3> i_ProtobufClass ,byte [] i_ProtobufBytes)
    {
        try
        {
            Method v_ParseFromMethod = i_ProtobufClass.getMethod("parseFrom" ,new Class<?>[] {byte[].class});
            if ( v_ParseFromMethod != null )
            {
                return StaticReflect.invoke(v_ParseFromMethod ,i_ProtobufBytes);
            }
        }
        catch (Exception e)
        {
            $Logger.error(e);
        }
        
        return null;
    }
    
    
    
    /**
     * 解码：字节转对象（反序列化）
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-09-27
     * @version     v1.0
     * 
     * @param i_ProtobufClass  Protobuf对象类型的全路径名称的字符串
     * @param i_ProtobufBytes  Protobuf字节数据，即encoder方法的返回值
     * @return
     */
    @SuppressWarnings("unchecked")
    public static Object decoder(String i_ProtobufClass ,byte [] i_ProtobufBytes)
    {
        try
        {
            return decoder((Class<? extends GeneratedMessageV3>) (Help.forName(i_ProtobufClass)) ,i_ProtobufBytes);
        }
        catch (ClassNotFoundException e)
        {
            $Logger.error(e);
        }
        
        return null;
    }
    
}
