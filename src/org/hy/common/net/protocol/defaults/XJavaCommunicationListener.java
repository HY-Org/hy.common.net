package org.hy.common.net.protocol.defaults;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.hy.common.Help;
import org.hy.common.MethodReflect;
import org.hy.common.net.common.NetError;
import org.hy.common.net.data.Command;
import org.hy.common.net.data.CommunicationRequest;
import org.hy.common.net.data.CommunicationResponse;
import org.hy.common.net.protocol.ServerEventListener;
import org.hy.common.xml.XJava;





/**
 * XJava数据通讯的监听事件
 *
 * @author      ZhengWei(HY)
 * @createDate  2017-01-16
 * @version     v1.0
 *              v2.0  2017-02-07  添加：查询多个本地XJava中的对象
 *              v3.0  2017-02-28  添加：获取服务端的Java.getSessionMap()数据
 */
public class XJavaCommunicationListener implements ServerEventListener
{
    
    /**
     *  数据通讯的事件类型。即通知哪一个事件监听者来处理数据通讯（对应 ServerSocket.listeners 的分区标识）
     * 
     *  事件类型区分大小写
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-01-16
     * @version     v1.0
     *
     * @return
     */
    @Override
    public String getEventType()
    {
        return CommunicationRequest.$DefaultEventType;
    }
    
    
    
    /**
     * 数据通讯事件的执行动作
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-01-16
     * @version     v1.0
     *
     * @param i_RequestData
     * @return
     */
    @Override
    public CommunicationResponse communication(CommunicationRequest i_RequestData)
    {
        CommunicationResponse v_ResponseData = new CommunicationResponse().setSerialNo(i_RequestData.getSerialNo());
        
        if ( Help.isNull(i_RequestData.getDataXID()) )
        {
            // 获取Java.getSessionMap()数据
            if ( CommunicationRequest.$Operation_SessionMap.equals(i_RequestData.getDataOperation()) )
            {
                v_ResponseData.setData(XJava.getSessionMap());
            }
            else
            {
                v_ResponseData.setResult(NetError.$XJava_DataXIDError);
            }
            
            return v_ResponseData;
        }
        
        v_ResponseData.setDataXID(i_RequestData.getDataXID());
        
        // 查询本地XJava中的对象
        if ( CommunicationRequest.$Operation_Select.equals(i_RequestData.getDataOperation()) )
        {
            v_ResponseData.setData(XJava.getObject(i_RequestData.getDataXID()));
            if ( v_ResponseData.getData() == null )
            {
                v_ResponseData.setResult(NetError.$XJava_SelectError);
            }
        }
        // 查询多个本地XJava中的对象
        else if ( CommunicationRequest.$Operation_Selects.equals(i_RequestData.getDataOperation()) )
        {
            Map<String ,Object> v_Objects = XJava.getObjects(i_RequestData.getDataXID());
            if ( Help.isNull(v_Objects) )
            {
                v_ResponseData.setResult(NetError.$XJava_SelectError);
            }
            else
            {
                List<CommunicationResponse> v_RetDatas      = new ArrayList<CommunicationResponse>();
                long                        v_ExpireTimeLen = 0L;
                
                for (Entry<String ,Object> v_Item : v_Objects.entrySet())
                {
                    CommunicationResponse v_ItemData = new CommunicationResponse();
                    
                    v_ExpireTimeLen = XJava.getSessionMap().getExpireTimeLen(v_Item.getKey());
                    if ( v_ExpireTimeLen <= 0L )
                    {
                        v_ExpireTimeLen = 0L;
                    }
                    else
                    {
                        v_ExpireTimeLen = v_ExpireTimeLen / 1000;
                    }
                    
                    v_ItemData.setDataXID(          v_Item.getKey());
                    v_ItemData.setData(             v_Item.getValue());
                    v_ItemData.setDataExpireTimeLen(v_ExpireTimeLen);
                    
                    v_RetDatas.add(v_ItemData);
                }
                
                v_ResponseData.setData(v_RetDatas);
            }
        }
        // 添加及更新
        else if ( CommunicationRequest.$Operation_Insert.equals(i_RequestData.getDataOperation())
               || CommunicationRequest.$Operation_Update.equals(i_RequestData.getDataOperation()) )
        {
            if ( i_RequestData.getData() == null )
            {
                v_ResponseData.setResult(NetError.$XJava_RequestDataError);
            }
            else
            {
                if ( i_RequestData.getDataExpireTimeLen() <= 0 )
                {
                    XJava.putObject(i_RequestData.getDataXID() ,i_RequestData.getData() ,i_RequestData.getDataXIsNew());
                }
                else
                {
                    XJava.putObject(i_RequestData.getDataXID() ,i_RequestData.getData() ,i_RequestData.getDataExpireTimeLen());
                }
            }
        }
        // 删除
        else if ( CommunicationRequest.$Operation_Delete.equals(i_RequestData.getDataOperation()) )
        {
            XJava.remove(i_RequestData.getDataXID());
        }
        // 执行命令
        else if ( CommunicationRequest.$Operation_Command.equals(i_RequestData.getDataOperation()) )
        {
            if ( i_RequestData.getData() == null || !(i_RequestData.getData() instanceof Command) )
            {
                v_ResponseData.setResult(NetError.$XJava_CommandValidateError);
            }
            else
            {
                Command v_Command  = (Command)i_RequestData.getData();
                Object  v_Instance = XJava.getObject(i_RequestData.getDataXID());
                
                if ( v_Instance == null )
                {
                    v_ResponseData.setResult(NetError.$XJava_CommandDataXIDError);
                }
                else
                {
                    try
                    {
                        Method v_Method = MethodReflect.getMethod(v_Instance ,v_Command.getMethodName() ,v_Command.getParams());
                        
                        if ( v_Method == null )
                        {
                            v_ResponseData.setResult(NetError.$XJava_CommandMethodError);
                        }
                        else
                        {
                            if ( v_Method.getReturnType() == java.lang.Void.TYPE )
                            {
                                v_Method.invoke(v_Instance ,v_Command.getParams());
                            }
                            else
                            {
                                Object v_MethodRet = v_Method.invoke(v_Instance ,v_Command.getParams());
                                v_ResponseData.setData(v_MethodRet);
                            }
                        }
                    }
                    catch (Throwable exce)
                    {
                        v_ResponseData.setResult(NetError.$XJava_CommandError);
                        exce.printStackTrace();
                    }
                }
            }
        }
        else
        {
            v_ResponseData.setResult(NetError.$XJava_NothingError);
        }
        
        return v_ResponseData;
    }
    
}
