package org.hy.common.net.junit.netty.xjavacloud;

import org.hy.common.net.data.CommunicationResponse;
import org.hy.common.net.data.LoginRequest;
import org.hy.common.net.netty.rpc.ClientRPC;
import org.hy.common.xml.log.Logger;
import org.junit.Test;





public class JU_XJavaExec
{
    
    private static final Logger $Logger = new Logger(JU_XJavaExec.class ,true);
    
    
    @Test
    public void test_XJavaExce()
    {
        // 步骤1：连接
        ClientRPC v_Client = new ClientRPC().setPort(3021).setHost("127.0.0.1");
        v_Client.setTimeout(30 * 1000L);
        v_Client.start();
        
        
        
        // 步骤2：登录
        LoginRequest v_LoginRequest = new LoginRequest();
        v_LoginRequest.setUserName  ("用户1");
        v_LoginRequest.setSystemName("系统1");
        v_Client.operation().login(v_LoginRequest);
        
        
        
        int v_Size         = 10000 * 10;
        int v_Count        = 0;
        int v_SucceedCount = 0;
        int v_FailCount    = 0;
        for (int x=1; x<=v_Size; x++)
        {
            // 步骤3：通讯
            CommunicationResponse v_Response = null;
            v_Response = v_Client.operation().sendCommand(-1
                                                         ,"currentMakingProcessService"
                                                         ,"timingMonitorInAndOutWarehouseData"
                                                         ,false
                                                         ,true);
            if ( v_Response.getResult() == 0 )
            {
                v_SucceedCount++;
            }
            else
            {
                v_FailCount++;
            }
            
            $Logger.info("执行第 " + (++v_Count) + " 次");
        }
        
        $Logger.info("成功：" + v_SucceedCount + " 次；失败：" + v_FailCount + " 次。");
        
        try
        {
            Thread.sleep(10 * 1000 * 60);
        }
        catch (Exception exce)
        {
            
        }
    }
    
    
    
    @Test
    public void test_XJavaFileContent()
    {
        // 步骤1：连接
        ClientRPC v_Client = new ClientRPC().setPort(9021).setHost("10.1.90.89");
        v_Client.setTimeout(30 * 1000L);
        v_Client.start();
        
        
        
        // 步骤2：登录
        LoginRequest v_LoginRequest = new LoginRequest();
        v_LoginRequest.setUserName  ("用户1");
        v_LoginRequest.setSystemName("系统1");
        v_Client.operation().login(v_LoginRequest);
        
        
        
        // 步骤3：通讯
        CommunicationResponse v_Response = null;
        v_Response = v_Client.operation().sendCommand(-1, "AnalyseFS" ,"getFileContent" ,new Object[]{"$WebHome/META-INF" ,"MANIFEST.MF"});
        
        $Logger.info(v_Response);
        
        try
        {
            Thread.sleep(10 * 1000 * 60);
        }
        catch (Exception exce)
        {
            
        }
    }
    
}
