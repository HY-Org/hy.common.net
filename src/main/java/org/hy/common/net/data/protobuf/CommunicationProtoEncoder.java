package org.hy.common.net.data.protobuf;

import java.nio.charset.Charset;
import java.util.List;

import org.hy.common.Help;
import org.hy.common.StringHelp;
import org.hy.common.net.data.CommunicationRequest;
import org.hy.common.net.data.CommunicationResponse;
import org.hy.common.net.data.LoginRequest;
import org.hy.common.net.data.LoginResponse;
import org.hy.common.net.data.protobuf.CommunicationProto.DataProtocol;
import org.hy.common.xml.XJSON;
import org.hy.common.xml.log.Logger;

import com.google.protobuf.ByteString;
import com.google.protobuf.GeneratedMessageV3;

import io.netty.util.CharsetUtil;





/**
 * 常规Java对象转换为Protobuf对象
 *
 * @author      ZhengWei(HY)
 * @createDate  2021-09-27
 * @version     v1.0
 */
public class CommunicationProtoEncoder
{
    
    private static final Logger  $Logger       = new Logger(CommunicationProtoEncoder.class ,true);
    
    public static  final String  $JsonRootName = "XJavaCloudDatas";
    
    public static  final Charset $Charset      = CharsetUtil.UTF_8;
    
    
    
    private CommunicationProtoEncoder()
    {
        
    }
    
    
    
    /**
     * 将 LoginRequest 对象转为 Protobuf 的 LoginRequest 对象
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-09-28
     * @version     v1.0
     * 
     * @param i_LoginRequest
     * @return
     */
    public static CommunicationProto.LoginRequest.Builder toLoginRequest(LoginRequest i_LoginRequest)
    {
        CommunicationProto.LoginRequest.Builder v_Ret = CommunicationProto.LoginRequest.newBuilder();
        
        v_Ret.setSerialNo(           i_LoginRequest.getSerialNo());
        v_Ret.setVersion(            i_LoginRequest.getVersion());
        v_Ret.setId(        Help.NVL(i_LoginRequest.getId()));
        v_Ret.setSystemName(Help.NVL(i_LoginRequest.getSystemName()));
        v_Ret.setHost(      Help.NVL(i_LoginRequest.getHost()));
        v_Ret.setPort(               i_LoginRequest.getPort());
        v_Ret.setUserName(  Help.NVL(i_LoginRequest.getUserName()));
        v_Ret.setPassword(  Help.NVL(i_LoginRequest.getPassword()));
        
        return v_Ret;
    }
    
    
    
    /**
     * 将 LoginResponse 对象转为 Protobuf 的 LoginResponse 对象
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-09-29
     * @version     v1.0
     * 
     * @param i_LoginResponse
     * @return
     */
    public static CommunicationProto.LoginResponse.Builder toLoginResponse(LoginResponse i_LoginResponse)
    {
        CommunicationProto.LoginResponse.Builder v_Ret = CommunicationProto.LoginResponse.newBuilder();
        
        // 通讯结束时间
        if ( i_LoginResponse.getEndTime() != null )
        {
            v_Ret.setEndTime(i_LoginResponse.getEndTime().getTime());
        }
        
        v_Ret.setSerialNo(        i_LoginResponse.getSerialNo());
        v_Ret.setVersion(         i_LoginResponse.getVersion());
        v_Ret.setResult(          i_LoginResponse.getResult());
        v_Ret.setToken(  Help.NVL(i_LoginResponse.getToken()));
        v_Ret.setHost(   Help.NVL(i_LoginResponse.getHost()));
        v_Ret.setPort(            i_LoginResponse.getPort());
        
        return v_Ret;
    }
    
    
    
    /**
     * 将 CommunicationRequest 对象转为 Protobuf 的 Request 对象
     * 
     * 自动按 i_Request.data 的类型识别是 Json协议，还是Protobuf协议，还是Java基本数据类型
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-09-29
     * @version     v1.0
     * 
     * @param i_Request
     * @return
     */
    public static CommunicationProto.Request.Builder toRequest(CommunicationRequest i_Request)
    {
        CommunicationProto.Request.Builder v_Ret = CommunicationProto.Request.newBuilder();
        
        // 会话时间
        if ( i_Request.getSessionTime() != null )
        {
            v_Ret.setSessionTime(i_Request.getSessionTime().getTime());
        }
        
        // 通讯的时间
        if ( i_Request.getTime() != null )
        {
            v_Ret.setTime(i_Request.getTime().getTime());
        }
        
        v_Ret.setSerialNo(                  i_Request.getSerialNo());           // 消息流水号
        v_Ret.setVersion(                   i_Request.getVersion());            // 通讯的接口版本
        v_Ret.setToken(            Help.NVL(i_Request.getToken()));             // 通讯的接口版本
        v_Ret.setDataXID(          Help.NVL(i_Request.getDataXID()));           // 数据的XID，即XJava的对象ID
        v_Ret.setDataXIsNew(                i_Request.getDataXIsNew());         // 是否每次通过 XJava.getObject(id) 获取一个全新的对象实例。默认构造出的对象为"单例"
        v_Ret.setDataExpireTimeLen(         i_Request.getDataExpireTimeLen());  // 数据的过期时长(单位：秒)。小于等于0或为空，表示永远有效
        v_Ret.setIsNonSync(                 i_Request.isNonSync());             // 通讯处理时是否为异步的。当为 true 时，表示服务端开启线程处理
        v_Ret.setEventType(        Help.NVL(i_Request.getEventType()));         // 数据通讯的事件类型
        v_Ret.setDataOperation(    Help.NVL(i_Request.getDataOperation()));     // 数据的操作类型
        v_Ret.setIsRetunData(               i_Request.isRetunData());           // 是否返回数据，即通讯CommunicationResponse.data是否返回。
        
        if ( i_Request.getData() != null )
        {
            v_Ret.setDataClass(i_Request.getData().getClass().getName());
            
            // Protobuf协议
            if ( i_Request.getData() instanceof GeneratedMessageV3 )
            {
                GeneratedMessageV3 v_ProtoObj = (GeneratedMessageV3)i_Request.getData();
                
                v_Ret.setDataProtocol(DataProtocol.Protobuf);
                v_Ret.setData(v_ProtoObj.toByteString());
            }
            // 编程语言的基本数据类型
            else if ( Help.isBasicDataType(i_Request.getData().getClass()) )
            {
                v_Ret.setDataProtocol(DataProtocol.BasicDataType);
                v_Ret.setData(ByteString.copyFrom(StringHelp.toString(i_Request.getData()).getBytes($Charset)));
            }
            // Json协议
            else
            {
                String v_JsonString = "";
                XJSON  v_XJson      = new XJSON();
                v_XJson.setReturnNVL(false);
                v_XJson.setSerializable(true);    // 实现Json的序列化
                
                try
                {
                    v_JsonString = v_XJson.toJson(i_Request.getData() ,$JsonRootName).toJSONString();
                }
                catch (Exception e)
                {
                    $Logger.error(e);
                }
                
                v_Ret.setDataProtocol(DataProtocol.Json);
                v_Ret.setData(ByteString.copyFrom(v_JsonString.getBytes($Charset)));
            }
        }
        
        return v_Ret;
    }
    
    
    
    /**
     * 将 CommunicationResponse 对象转为 Protobuf 的 Response 对象
     * 
     * 自动按 i_Response.data 的类型识别是 Json协议，还是Protobuf协议，还是Java基本数据类型
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-09-27
     * @version     v1.0
     * 
     * @param i_Response
     * @return
     */
    public static CommunicationProto.Response.Builder toResponse(CommunicationResponse i_Response)
    {
        CommunicationProto.Response.Builder v_Ret = CommunicationProto.Response.newBuilder();
        
        // 会话时间
        if ( i_Response.getSessionTime() != null )
        {
            v_Ret.setSessionTime(i_Response.getSessionTime().getTime());
        }
        
        // 通讯的时间
        if ( i_Response.getTime() != null )
        {
            v_Ret.setTime(i_Response.getTime().getTime());
        }
        
        // 通讯结束时间
        if ( i_Response.getEndTime() != null )
        {
            v_Ret.setEndTime(i_Response.getEndTime().getTime());
        }
        
        v_Ret.setSerialNo(                  i_Response.getSerialNo());           // 消息流水号
        v_Ret.setVersion(                   i_Response.getVersion());            // 通讯的接口版本
        v_Ret.setToken(            Help.NVL(i_Response.getToken()));             // 通讯的接口版本
        v_Ret.setDataXID(          Help.NVL(i_Response.getDataXID()));           // 数据的XID，即XJava的对象ID
        v_Ret.setDataXIsNew(                i_Response.getDataXIsNew());         // 是否每次通过 XJava.getObject(id) 获取一个全新的对象实例。默认构造出的对象为"单例"
        v_Ret.setDataExpireTimeLen(         i_Response.getDataExpireTimeLen());  // 数据的过期时长(单位：秒)。小于等于0或为空，表示永远有效
        v_Ret.setIsNonSync(                 i_Response.isNonSync());             // 通讯处理时是否为异步的。当为 true 时，表示客户端开启线程处理
        v_Ret.setResult(                    i_Response.getResult());             // 通讯的结果类型
        
        if ( i_Response.getData() != null )
        {
            v_Ret.setDataClass(i_Response.getData().getClass().getName());
            
            // Protobuf协议
            if ( i_Response.getData() instanceof GeneratedMessageV3 )
            {
                GeneratedMessageV3 v_ProtoObj = (GeneratedMessageV3)i_Response.getData();
                
                v_Ret.setDataProtocol(DataProtocol.Protobuf);
                v_Ret.setData(v_ProtoObj.toByteString());
            }
            // 编程语言的基本数据类型
            else if ( Help.isBasicDataType(i_Response.getData().getClass()) )
            {
                v_Ret.setDataProtocol(DataProtocol.BasicDataType);
                v_Ret.setData(ByteString.copyFrom(StringHelp.toString(i_Response.getData()).getBytes($Charset)));
            }
            // Json协议
            else
            {
                if ( i_Response.getData() instanceof List )
                {
                    if ( !Help.isNull(i_Response.getData()) )
                    {
                        v_Ret.setDataClass(((List<?>)i_Response.getData()).get(0).getClass().getName());
                    }
                }
                
                String v_JsonString = "";
                XJSON  v_XJson      = new XJSON();
                v_XJson.setReturnNVL(false);
                v_XJson.setSerializable(true);    // 实现Json的序列化
                
                try
                {
                    v_JsonString = v_XJson.toJson(i_Response.getData() ,$JsonRootName).toJSONString();
                }
                catch (Exception e)
                {
                    $Logger.error(e);
                }
                
                v_Ret.setDataProtocol(DataProtocol.Json);
                v_Ret.setData(ByteString.copyFrom(v_JsonString.getBytes($Charset)));
            }
        }
        
        return v_Ret;
    }
    
}
