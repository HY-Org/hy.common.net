package org.hy.common.net.data.protobuf;

import org.hy.common.Date;
import org.hy.common.Help;
import org.hy.common.net.data.ClientUserInfo;
import org.hy.common.net.data.Command;
import org.hy.common.net.data.CommunicationRequest;
import org.hy.common.net.data.CommunicationResponse;
import org.hy.common.net.data.LoginRequest;
import org.hy.common.net.data.LoginResponse;
import org.hy.common.net.data.protobuf.CommunicationProto.DataProtocol;
import org.hy.common.net.data.protobuf.CommunicationProto.XCommand;
import org.hy.common.xml.XJSON;
import org.hy.common.xml.log.Logger;

import com.google.protobuf.ByteString;





/**
 * Protobuf对象转换为常规Java对象
 *
 * @author      ZhengWei(HY)
 * @createDate  2021-09-27
 * @version     v1.0
 */
public class CommunicationProtoDecoder
{
    
    private static final Logger $Logger = new Logger(CommunicationProtoDecoder.class ,true);
    
    
    
    private CommunicationProtoDecoder()
    {
        
    }
    
    
    
    /**
     * 将 CommunicationProto.LoginRequest 对象转为 org.hy.common.net.data.LoginRequest 对象
     * 在转换后，就可以支持原接口 ServerSocketValidate 了。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-09-25
     * @version     v1.0
     * 
     * @param i_Request
     * @param i_Host
     * @return
     */
    public static LoginRequest toLoginRequest(CommunicationProto.LoginRequest i_Request ,String i_Host)
    {
        LoginRequest v_Ret = new ClientUserInfo();
        
        v_Ret.setVersion(      i_Request.getVersion());
        v_Ret.setId(           i_Request.getId());
        v_Ret.setSystemName(   i_Request.getSystemName());
        v_Ret.setUserName(     i_Request.getUserName());
        v_Ret.setPassword(     i_Request.getPassword());
        v_Ret.setHost(         i_Request.getHost());
        v_Ret.setPort(         i_Request.getPort());
        v_Ret.setRemoteAddress(i_Host);
        
        return v_Ret;
    }
    
    
    
    /**
     * 将 CommunicationProto.LoginRequest 对象转为 org.hy.common.net.data.LoginRequest 对象
     * 在转换后，就可以支持原接口 ServerSocketValidate 了。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-09-25
     * @version     v1.0
     * 
     * @param i_Request
     * @param i_Host
     * @return
     */
    public static LoginResponse toLoginResponse(CommunicationProto.LoginResponse i_Response)
    {
        LoginResponse v_Ret = new LoginResponse();
        
        v_Ret.setVersion(         i_Response.getVersion());
        v_Ret.setResult(          i_Response.getResult());
        v_Ret.setEndTime(new Date(i_Response.getEndTime()));
        v_Ret.setToken(           i_Response.getToken());
        v_Ret.setHost(            i_Response.getHost());
        v_Ret.setPort(            i_Response.getPort());
        
        return v_Ret;
    }
    
    
    
    /**
     * 将 CommunicationProto.Request 对象转为 org.hy.common.net.data.CommunicationRequest 对象
     * 在转换后，就可以支持原接口 ServerEventListener 了。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-09-27
     * @version     v1.0
     * 
     * @param i_Request
     * @return
     */
    public static CommunicationRequest toRequest(CommunicationProto.Request i_Request)
    {
        CommunicationRequest v_Ret = new CommunicationRequest();
        
        // 会话时间
        if ( i_Request.getSessionTime() > 0 )
        {
            v_Ret.setSessionTime(new Date(i_Request.getSessionTime()));
        }
        
        // 通讯的时间
        if ( i_Request.getTime() > 0 )
        {
            v_Ret.setTime(new Date(i_Request.getTime()));
        }
        
        v_Ret.setVersion(          i_Request.getVersion());             // 通讯的接口版本
        v_Ret.setToken(            i_Request.getToken());               // 通讯票据
        v_Ret.setDataXID(          i_Request.getDataXID());             // 数据的XID，即XJava的对象ID
        v_Ret.setDataXIsNew(       i_Request.getDataXIsNew());          // 是否每次通过 XJava.getObject(id) 获取一个全新的对象实例。默认构造出的对象为"单例"
        v_Ret.setDataExpireTimeLen(i_Request.getDataExpireTimeLen());   // 数据的过期时长(单位：秒)。小于等于0或为空，表示永远有效
        v_Ret.setNonSync(          i_Request.getIsNonSync());           // 通讯处理时是否为异步的。当为 true 时，表示服务端开启线程处理
        v_Ret.setEventType(        i_Request.getEventType());           // 数据通讯的事件类型
        v_Ret.setDataOperation(    i_Request.getDataOperation());       // 数据的操作类型
        v_Ret.setRetunData(        i_Request.getIsRetunData());         // 是否返回数据，即通讯CommunicationResponse.data是否返回。
        
        String     v_DataClass = i_Request.getDataClass();
        ByteString v_DataBytes = i_Request.getData();
        if ( !Help.isNull(v_DataClass) && v_DataBytes != null && v_DataBytes.size() > 0 )
        {
            DataProtocol v_DataProtocol = i_Request.getDataProtocol();
            
            // 转换通用数据
            v_Ret.setData(dataProtocolToObject(v_DataClass ,v_DataProtocol ,v_DataBytes.toByteArray()));
            
            // 转换执行命令的数据
            if ( XCommand.class == v_Ret.getData().getClass() )
            {
                Command  v_NCmd = new Command();
                XCommand v_XCmd = (XCommand)v_Ret.getData();
                if ( !Help.isNull(v_XCmd.getParamsClassList()) )
                {
                    v_NCmd.setParams(new Object[v_XCmd.getParamsClassCount()]);
                    for (int i=0; i<v_XCmd.getParamsClassCount(); i++)
                    {
                        v_DataClass = v_XCmd.getParamsClass(i);
                        v_DataBytes = v_XCmd.getParamsValue(i);
                        
                        if ( !Help.isNull(v_DataClass) && v_DataBytes != null && v_DataBytes.size() > 0 )
                        {
                            v_NCmd.getParams()[i] = dataProtocolToObject(v_DataClass ,v_XCmd.getParamsProtocol(i) ,v_DataBytes.toByteArray());
                        }
                        else
                        {
                            v_NCmd.getParams()[i] = null;
                        }
                    }
                }
                
                v_NCmd.setMethodName(v_XCmd.getMethodName());
                v_Ret.setData(v_NCmd);
            }
        }
        
        return v_Ret;
    }
    
    
    
    /**
     * 将 CommunicationProto.Response 对象转为 org.hy.common.net.data.CommunicationResponse 对象
     * 在转换后，就可以支持原接口 ServerEventListener 了。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-09-29
     * @version     v1.0
     * 
     * @param i_Response
     * @return
     */
    public static CommunicationResponse toResponse(CommunicationProto.Response i_Response)
    {
        CommunicationResponse v_Ret = new CommunicationResponse();
        
        // 会话时间
        if ( i_Response.getSessionTime() > 0 )
        {
            v_Ret.setSessionTime(new Date(i_Response.getSessionTime()));
        }
        
        // 通讯的时间
        if ( i_Response.getTime() > 0 )
        {
            v_Ret.setTime(new Date(i_Response.getTime()));
        }
        
        // 通讯结束时间
        if ( i_Response.getEndTime() > 0 )
        {
            v_Ret.setEndTime(new Date(i_Response.getEndTime()));
        }
        
        v_Ret.setVersion(          i_Response.getVersion());             // 通讯的接口版本
        v_Ret.setToken(            i_Response.getToken());               // 通讯票据
        v_Ret.setDataXID(          i_Response.getDataXID());             // 数据的XID，即XJava的对象ID
        v_Ret.setDataXIsNew(       i_Response.getDataXIsNew());          // 是否每次通过 XJava.getObject(id) 获取一个全新的对象实例。默认构造出的对象为"单例"
        v_Ret.setDataExpireTimeLen(i_Response.getDataExpireTimeLen());   // 数据的过期时长(单位：秒)。小于等于0或为空，表示永远有效
        v_Ret.setNonSync(          i_Response.getIsNonSync());           // 通讯处理时是否为异步的。当为 true 时，表示客户端开启线程处理
        v_Ret.setResult(           i_Response.getResult());              // 通讯的结果类型
        
        String     v_DataClass = i_Response.getDataClass();
        ByteString v_DataBytes = i_Response.getData();
        if ( !Help.isNull(v_DataClass) && v_DataBytes != null && v_DataBytes.size() > 0 )
        {
            DataProtocol v_DataProtocol = i_Response.getDataProtocol();
            
            // 转换通用数据
            v_Ret.setData(dataProtocolToObject(v_DataClass ,v_DataProtocol ,v_DataBytes.toByteArray()));
            
            // 转换执行命令的数据
            if ( v_Ret.getData() != null && XCommand.class == v_Ret.getData().getClass() )
            {
                Command  v_NCmd = new Command();
                XCommand v_XCmd = (XCommand)v_Ret.getData();
                if ( !Help.isNull(v_XCmd.getParamsClassList()) )
                {
                    v_NCmd.setParams(new Object[v_XCmd.getParamsClassCount()]);
                    for (int i=0; i<v_XCmd.getParamsClassCount(); i++)
                    {
                        v_DataClass = v_XCmd.getParamsClass(i);
                        v_DataBytes = v_XCmd.getParamsValue(i);
                        
                        if ( !Help.isNull(v_DataClass) && v_DataBytes != null && v_DataBytes.size() > 0 )
                        {
                            v_NCmd.getParams()[i] = dataProtocolToObject(v_DataClass ,v_XCmd.getParamsProtocol(i) ,v_DataBytes.toByteArray());
                        }
                        else
                        {
                            v_NCmd.getParams()[i] = null;
                        }
                    }
                }
                
                v_NCmd.setMethodName(v_XCmd.getMethodName());
                v_Ret.setData(v_NCmd);
            }
        }
        
        return v_Ret;
    }
    
    
    
    /**
     * 按数据协议，将Bytes转成对象实例
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-09-27
     * @version     v1.0
     * 
     * @param i_ClassName  通讯数据的类型
     * @param i_Protocol   通讯数据的协议（0:JSON   1:Protobuf）
     * @param i_Data       通讯数据的二进制数据（也可以是Protobuf规范的、可反序列的数据）
     * @return
     */
    public static Object dataProtocolToObject(String i_ClassName ,DataProtocol i_Protocol ,byte [] i_Data)
    {
        Object v_Ret = null;
        
        if ( DataProtocol.Json == i_Protocol )
        {
            String v_JsonString = new String(i_Data ,CommunicationProtoEncoder.$Charset);
            XJSON  v_XJson      = new XJSON();
            
            try
            {
                v_Ret = v_XJson.toJava(v_JsonString ,CommunicationProtoEncoder.$JsonRootName ,Help.forName(i_ClassName));
            }
            catch (Exception e)
            {
                $Logger.error(e);
            }
        }
        else if ( DataProtocol.Protobuf == i_Protocol )
        {
            v_Ret = ProtobufHelp.decoder(i_ClassName ,i_Data);
        }
        else if ( DataProtocol.BasicDataType == i_Protocol )
        {
            try
            {
                v_Ret = Help.toObject(Help.forName(i_ClassName) ,new String(i_Data ,CommunicationProtoEncoder.$Charset));
            }
            catch (Exception e)
            {
                $Logger.error(e);
            }
        }
        
        return v_Ret;
    }
    
}
