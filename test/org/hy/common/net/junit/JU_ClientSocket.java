package org.hy.common.net.junit;

import org.hy.common.net.ClientSocket;
import org.hy.common.net.data.CommunicationResponse;
import org.junit.Assert;
import org.junit.Test;





/**
 * 测试单元
 *
 * @author      ZhengWei(HY)
 * @createDate  2019-03-06
 * @version     v1.0
 */
public class JU_ClientSocket
{
    
    @Test
    public void test_ClientSocket()
    {
        ClientSocket v_Client = new ClientSocket();
        
        v_Client.setHostName("10.1.50.241");
        v_Client.setPort(9021);
        
        CommunicationResponse v_Response = v_Client.sendCommand("JOBS" ,"disasterRecoveryChecks" ,false);
        if ( v_Response.getResult() != 0 )
        {
            Assert.fail("网络通讯异常");
        }
    }
    
    
    
    @Test
    public void test_ClientSocket_Cloud()
    {
        ClientSocket v_Client = new ClientSocket();
        
        v_Client.setHostName("127.0.0.1");
        v_Client.setPort(2021);
        
        CommunicationResponse v_Response = v_Client.sendCommand("AnalyseBase" ,"analyseCluster_Info");
        if ( v_Response.getResult() != 0 )
        {
            Assert.fail("网络通讯异常");
        }
    }
    
}
