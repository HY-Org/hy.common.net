package org.hy.common.net.data.protobuf;





/**
 * Communication.proto 中定义的 DataType的说明（程序化可以输出的说明）
 *
 * @author      ZhengWei(HY)
 * @createDate  2021-12-08
 * @version     v1.0
 */
public class DataType
{
    
    private DataType()
    {
        
    }
    
    
    
    public static String getDataTypeName(int i_DataType)
    {
        switch ( i_DataType )
        {
            case 0:
                return "LoginRequest";
            case 1:
                return "LoginResponse";
            case 2:
                return "Request";
            default:
                return "Response";
        }
    }
    
}
