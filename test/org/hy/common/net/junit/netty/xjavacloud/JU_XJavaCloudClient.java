package org.hy.common.net.junit.netty.xjavacloud;

import java.util.List;

import org.hy.common.net.data.CommunicationResponse;
import org.hy.common.net.data.LoginRequest;
import org.hy.common.net.netty.rpc.ClientRPC;
import org.hy.common.xml.log.Logger;





/**
 * 测试单元：XJavaCloud的客户端
 * 
 * @author      ZhengWei(HY)
 * @createDate  2021-09-29
 * @version     v1.0
 */
public class JU_XJavaCloudClient
{
    private static final Logger $Logger = new Logger(JU_XJavaCloudClient.class ,true);
    
    
    
    public static void main(String [] args)
    {
        // 步骤1：连接
        ClientRPC v_Client = new ClientRPC().setPort(3021).setHost("127.0.0.1");
        v_Client.start();
        
        
        
        // 步骤2：登录
        LoginRequest v_LoginRequest = new LoginRequest();
        v_LoginRequest.setUserName  ("用户1");
        v_LoginRequest.setSystemName("系统1");
        v_Client.operation().login(v_LoginRequest);
        
        
        
        // 步骤3：通讯
        CommunicationResponse v_Response = null;
        List<?>               v_Datas    = null;
        
        
        v_Response = v_Client.operation().sendCommand(0, "TEST-Date-1" ,"getFull");
        
        $Logger.info("获取服务端的对象：时间1：" + v_Client.operation().getObject(0, "TEST-Date-1"));
        $Logger.info("获取服务端的对象：时间2：" + v_Client.operation().getObject(0, "TEST-Date-2"));

        $Logger.info("获取服务端的对象：多个对象：");
        v_Response = v_Client.operation().getObjects(0, "TEST-Date");
        v_Datas = (List<?>)v_Response.getData();
        v_Datas.forEach((item) ->
        {
            $Logger.info("->" + item);
        });

        $Logger.info("获取服务端的对象：自定义对象：" + v_Client.operation().getObject(0, "TEST-DataInfo-1"));
        $Logger.info("获取服务端的对象：多个自定义对象：");
        for (int i=1; i<=10; i++)
        {
            v_Response = v_Client.operation().getObjects(0, "TEST-DataInfo");
            v_Datas = (List<?>)v_Response.getData();
            v_Datas.forEach((item) ->
            {
                $Logger.info("->" + item);
            });
        }
        
        
        int v_Count = 0;
        while ( ++v_Count < 100 )
        {
            if ( !v_Client.isStart() )
            {
                v_Client.start(v_Client.newBootstrap());
            }
            
            if ( !v_Client.operation().isLogin() )
            {
                v_Client.operation().login(v_LoginRequest);
            }
            
            CommunicationResponse v_Ret = v_Client.operation().getObject(0, "TEST-Date-1");
            $Logger.info("获取服务端的对象：时间1：" + v_Ret.getData());
        }
        
        v_Client.shutdown();
    }
    
}
