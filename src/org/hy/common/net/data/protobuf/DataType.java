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
    
    public static final int $LoginRequest  = 0;
    
    public static final int $LoginResponse = 1;
    
    public static final int $Request       = 2;
    
    public static final int $Response      = 3;
    
    
    
    private DataType()
    {
        
    }
    
    
    
    public static String getDataTypeName(int i_DataType)
    {
        switch ( i_DataType )
        {
            case $LoginRequest:
                return "LoginRequest";
                
            case $LoginResponse:
                return "LoginResponse";
                
            case $Request:
                return "Request";
                
            default:
                return "Response";
        }
    }
    
}
