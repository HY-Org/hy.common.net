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
        
        v_Client.setHostName("10.1.50.1");
        v_Client.setPort(1021);
        
        CommunicationResponse v_Response = v_Client.sendCommand("JOBS" ,"disasterRecoveryChecks" ,false);
        if ( v_Response.getResult() == 0 )
        {
            Assert.fail("网络通讯成功");
        }
        else
        {
            Assert.fail("网络通讯异常");
        }
    }
    
}
