package org.hy.common.net.junit.netty.xjavacloud;

import org.hy.common.net.data.CommunicationResponse;
import org.hy.common.net.data.LoginRequest;
import org.hy.common.net.netty.rpc.ClientRPC;
import org.hy.common.xml.log.Logger;
import org.junit.Test;





public class JU_XJavaExec
{
    
    private static final Logger $Logger = new Logger(JU_XJavaExec.class);
    
    
    @Test
    public void test_XJavaExce()
    {
     // 步骤1：连接
        ClientRPC v_Client = new ClientRPC().setPort(3021).setHost("10.1.50.76");
        v_Client.setTimeout(30 * 1000L);
        v_Client.start();
        
        
        
        // 步骤2：登录
        LoginRequest v_LoginRequest = new LoginRequest();
        v_LoginRequest.setUserName  ("用户1");
        v_LoginRequest.setSystemName("系统1");
        v_Client.operation().login(v_LoginRequest);
        
        
        
        // 步骤3：通讯
        CommunicationResponse v_Response = null;
        
        
        v_Response = v_Client.operation().sendCommand(-1, "SyncDatasJob" ,"syncDatas");
        
        $Logger.info(v_Response);
    }
    
}
