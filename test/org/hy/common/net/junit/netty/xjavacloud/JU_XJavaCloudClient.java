package org.hy.common.net.junit.netty.xjavacloud;

import org.hy.common.net.data.LoginRequest;
import org.hy.common.net.netty.rpc.ClientRPC;

public class JU_XJavaCloudClient
{
    
    public static void main(String [] args)
    {
        ClientRPC v_Client = new ClientRPC().setPort(3021).setHost("127.0.0.1");
        v_Client.start();
        
        v_Client.getOperation().login(new LoginRequest());
    }
    
}
