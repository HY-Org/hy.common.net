package org.hy.common.net.junit.netty.xjavacloud;

import org.hy.common.net.data.LoginRequest;
import org.hy.common.net.netty.rpc.ClientRPC;

public class JU_XJavaCloudClient
{
    
    public static void main(String [] args)
    {
        ClientRPC v_Client = new ClientRPC().setPort(3021).setHost("127.0.0.1");
        v_Client.start();
        
        LoginRequest v_LoginRequest = new LoginRequest();
        v_LoginRequest.setUserName  ("用户1");
        v_LoginRequest.setSystemName("系统1");
        v_Client.getOperation().login(v_LoginRequest);
    }
    
}
