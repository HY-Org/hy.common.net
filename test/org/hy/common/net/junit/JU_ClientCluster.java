package org.hy.common.net.junit;

import org.hy.common.net.ClientSocket;
import org.hy.common.net.common.ClientCluster;
import org.hy.common.net.data.LoginRequest;
import org.hy.common.net.netty.rpc.ClientRPC;
import org.hy.common.xml.log.Logger;
import org.junit.Test;

public class JU_ClientCluster
{
    private static final Logger $Logger = new Logger(JU_ClientCluster.class ,true);
    
    
    
    @Test
    public void test_V2老版本()
    {
        ClientCluster v_Client = new ClientSocket("10.1.50.76" ,1721);
        this.send(v_Client);
    }
    
    
    
    @Test
    public void test_V3新版本()
    {
        ClientCluster v_Client = new ClientRPC().setHost("10.1.90.23").setPort(3021);
        this.send(v_Client);
    }
    
    
    
    private void send(ClientCluster i_Client)
    {
        i_Client.operation().startServer();
        i_Client.operation().login(new LoginRequest().setUserName("测试").setSystemName("测试"));
        
        $Logger.info("开始执行");
        i_Client.operation().sendCommand(-1 ,"QiTaoService" ,"calcQiTao2021");
        $Logger.info("执行完成");
        
        try
        {
            Thread.sleep(10 * 60 * 1000);
        }
        catch (Exception exce)
        {
            // Nothing.
        }
    }
    
}
