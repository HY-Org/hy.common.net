package org.hy.common.net.common;





/**
 * 通讯异常的常量类
 *
 * @author      ZhengWei(HY)
 * @createDate  2018-02-22
 * @version     v1.0
 */
public interface NetError
{
    
    /** 服务端未知异常 */
    public static final int $Server_UnknownError           = 500;
    
    /** 通讯超时 */
    public static final int $Server_TimeoutError           = 504;
    
    /** 登陆验证异常 */
    public static final int $Server_LoginValidateError     = 551;
    
    /** 登陆异常。可能是网络 或 通讯的服务器错误 */
    public static final int $Server_LoginError             = 552;
    
    /** 响应返回错误 */
    public static final int $Server_ResponseDataError      = 555;
    
    
    
    /** 请启动服务后再通讯 */
    public static final int $Client_StartNotError          = 451;
    
    /** 请登录后再通讯 */
    public static final int $Client_LoginNotError          = 452;
    
    /** 登陆数据类型异常（一般指未按协议传送数据） */
    public static final int $Client_LoginTypeError         = 453;
    
    /** 请求错误。可能是请求数据为空 或 通讯的数据端口被占用  */
    public static final int $Client_RequestDataError       = 455;
    
    
    
    /** XJava操作，未知命令类型错误。如客户端传参超范围，见CommunicationRequest类中的常量 */
    public static final int $XJava_NothingError            = 600;
    
    /** XJava操作，XID异常。如对象空 */
    public static final int $XJava_DataXIDError            = 601;
    
    /** XJava操作，查询本地XJava中的对象不存在 */
    public static final int $XJava_SelectError             = 602;
    
    /** XJava操作，添加及更新的对象时请求对象异常。如请求对象为空 */
    public static final int $XJava_RequestDataError        = 603;
    
    /** XJava操作，执行命令时验证异常。如请求对象为空 或 请求参数错误 */
    public static final int $XJava_CommandValidateError    = 604;
    
    /** XJava操作，执行命令时XID异常。如对象空 */
    public static final int $XJava_CommandDataXIDError     = 605;
    
    /** XJava操作，执行命令的方法异常。如执行方法不存在 */
    public static final int $XJava_CommandMethodError      = 606;
    
    /** XJava操作，执行命令异常。如执行方法时的各种错误 */
    public static final int $XJava_CommandError            = 607;
    
}
