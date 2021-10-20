package org.hy.common.net.junit.netty.xjavacloud;

import org.hy.common.Date;
import org.hy.common.net.netty.rpc.ServerRPC;
import org.hy.common.xml.XJava;





/**
 * 测试单元：XJavaCloud的服务端
 * 
 * @author      ZhengWei(HY)
 * @createDate  2021-09-29
 * @version     v1.0
 */
public class JU_XJavaCloudServer
{
    
    public static void main(String [] args)
    {
        // 准备服务端的测试数据
        XJava.putObject("TEST-Date-0" ,new Date());
        XJava.putObject("TEST-Date-1" ,new Date());
        XJava.putObject("TEST-Date-2" ,new java.util.Date());
        XJava.putObject("TEST-Date-3" ,new Date());
        XJava.putObject("TEST-Date-4" ,new Date());
        XJava.putObject("TEST-Date-5" ,new Date());
        XJava.putObject("TEST-Date-6" ,new Date());
        XJava.putObject("TEST-Date-7" ,new Date());
        XJava.putObject("TEST-Date-8" ,new Date());
        
        XJava.putObject("TEST-DataInfo-1" ,new DataInfo().setValueString("数据1").setValueDate(new Date("2021-10-11 13:00:00")).setValueObject(1L));
        XJava.putObject("TEST-DataInfo-2" ,new DataInfo().setValueString("数据2").setValueDate(new Date("2021-10-11 14:00:00")).setValueObject("2"));
        
        
        ServerRPC v_Server = new ServerRPC().setPort(3021);
        v_Server.start();
    }
    
}
