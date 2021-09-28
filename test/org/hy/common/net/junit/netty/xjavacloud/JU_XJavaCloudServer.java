package org.hy.common.net.junit.netty.xjavacloud;

import org.hy.common.net.netty.rpc.ServerRPC;

public class JU_XJavaCloudServer
{
    
    public static void main(String [] args)
    {
        ServerRPC v_Server = new ServerRPC().setPort(3021);
        v_Server.start();
    }
    
}
