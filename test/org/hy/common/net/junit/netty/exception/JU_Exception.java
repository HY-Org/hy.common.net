package org.hy.common.net.junit.netty.exception;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.hy.common.Date;
import org.hy.common.Help;
import org.hy.common.net.ClientSocketCluster;
import org.hy.common.net.common.ClientCluster;
import org.hy.common.net.data.CommunicationResponse;
import org.hy.common.net.data.LoginRequest;
import org.hy.common.net.netty.rpc.ClientRPC;
import org.hy.common.net.netty.rpc.ServerRPC;
import org.hy.common.xml.XJava;
import org.hy.common.xml.log.Logger;
import org.junit.Test;





/**
 * 测试单元：专测试各种异常
 * 
 * @author      ZhengWei(HY)
 * @createDate  2021-12-14
 * @version     v1.0
 */
public class JU_Exception
{
    private static final Logger $Logger = new Logger(JU_Exception.class ,true);
    
    
    
    @Test
    public void test_连接端口不存()
    {
        List<ClientCluster> v_Clients    = new ArrayList<ClientCluster>();
        List<ClientCluster> v_Exceptions = null;
        
        v_Clients.add(new ClientRPC().setHost("10.1.90.89").setPort(9021));
        
        v_Exceptions = ClientSocketCluster.startServer(v_Clients);
        $Logger.info("启动异常：" + v_Exceptions.size() + " 个");
        
        v_Exceptions = ClientSocketCluster.login(v_Clients ,new LoginRequest().setUserName("测试").setSystemName("测试"));
        $Logger.info("登录异常：" + v_Exceptions.size() + " 个");
    }
    
    
    
    @Test
    public void test_连接非Netty端口()
    {
        List<ClientCluster> v_Clients    = new ArrayList<ClientCluster>();
        List<ClientCluster> v_Exceptions = null;
        
        v_Clients.add(new ClientRPC().setHost("10.1.90.89").setPort(22));
        
        v_Exceptions = ClientSocketCluster.startServer(v_Clients);
        $Logger.info("启动异常：" + Help.NVL(v_Exceptions).size() + " 个");
        
        v_Exceptions = ClientSocketCluster.login(v_Clients ,new LoginRequest().setUserName("测试").setSystemName("测试"));
        $Logger.info("登录异常：" + Help.NVL(v_Exceptions).size() + " 个");
    }
    
    
    
    @Test
    public void test_集群访问()
    {
        List<ClientCluster> v_Clients    = new ArrayList<ClientCluster>();
        List<ClientCluster> v_Exceptions = null;
        
        v_Clients.add(new ClientRPC().setHost("10.1.90.29")  .setPort(3021));
        v_Clients.add(new ClientRPC().setHost("42.63.227.65").setPort(9021));
        
        v_Exceptions = ClientSocketCluster.startServer(v_Clients);
        $Logger.info("启动异常：" + Help.NVL(v_Exceptions).size() + " 个");
        
        v_Exceptions = ClientSocketCluster.login(v_Clients ,new LoginRequest().setUserName("测试").setSystemName("测试"));
        $Logger.info("登录异常：" + Help.NVL(v_Exceptions).size() + " 个");
        
        Date                                      v_BTime         = new Date();
        boolean                                   v_IsSync        = true;
        Map<ClientCluster ,CommunicationResponse> v_ResponseDatas = ClientSocketCluster.getObjectss(v_Clients ,v_IsSync ,0 ,"MS_Timing_");
        for (Map.Entry<? extends ClientCluster ,CommunicationResponse> v_Item : v_ResponseDatas.entrySet())
        {
            CommunicationResponse v_ResponseData = v_Item.getValue();
            
            if ( v_ResponseData.getResult() == 0 )
            {
                if ( v_ResponseData.getData() != null && v_ResponseData.getData() instanceof List )
                {
                    $Logger.info(((List<?>)v_ResponseData.getData()).size());
                }
            }
        }
        
        Date v_ETime = new Date();
        $Logger.info((v_IsSync ? "同步" : "并行") + "用时为：" + Date.toTimeLen(v_ETime.getTime() - v_BTime.getTime()));
        ClientSocketCluster.shutdownServer(v_Clients);
    }
    
    
    
    @Test
    public void test_服务端关闭_客户端未连接()
    {
        // 准备服务端的测试数据
        XJava.putObject("TEST_Date" ,new Date());
        
        $Server = new ServerRPC().setPort(3021);
        $Server.start();
        
        try
        {
            Thread.sleep(10 * 1000);
        }
        catch (Exception exce)
        {
            $Logger.error(exce);
        }
        
        $Server.shutdown();
        $Logger.info("服务关闭完成");
    }
    
    
    
    @Test
    public void test_服务端关闭_客户端有连接()
    {
        // 准备服务端的测试数据
        XJava.putObject("TEST_Date" ,new Date());
        
        $Server = new ServerRPC().setPort(3021);
        $Server.start();
        
        try
        {
            Thread.sleep(5 * 1000);
        }
        catch (Exception exce)
        {
            $Logger.error(exce);
        }
        
        $Clients = new ArrayList<ClientCluster>();
        $Clients.add(new ClientRPC().setHost("127.0.0.1").setPort(3021));
        List<ClientCluster> v_Exceptions = null;
        
        v_Exceptions = ClientSocketCluster.startServer($Clients);
        $Logger.info("启动异常：" + Help.NVL(v_Exceptions).size() + " 个");
        
        v_Exceptions = ClientSocketCluster.login($Clients ,new LoginRequest().setUserName("服务端关闭_客户端有连接").setSystemName("测试"));
        $Logger.info("登录异常：" + Help.NVL(v_Exceptions).size() + " 个");
        
        $Server.shutdown();
        $Logger.info("服务关闭完成");
    }
    
    
    
    private static ServerRPC           $Server;
    private static List<ClientCluster> $Clients;
    
    // 建议服务端与客户端在两台不同的电脑上测试
    @Test
    public void test_断开重新连接_服务端()
    {
        // 准备服务端的测试数据
        XJava.putObject("TEST_Date" ,new Date());
        
        $Server = new ServerRPC().setPort(3021);
        $Server.start();
        
        try
        {
            Thread.sleep(10 * 60 * 1000);
        }
        catch (Exception exce)
        {
            $Logger.error(exce);
        }
    }
    
    
    
    @Test
    public void test_断开重新连接_客户端()
    {
        $Clients = new ArrayList<ClientCluster>();
        $Clients.add(new ClientRPC().setHost("127.0.0.1").setPort(3021));
        
        
        $Logger.info("正常情况下的客户端通讯");
        this.test_断开重新连接_客户端($Clients);
        
        $Logger.info("模拟服务端宕机");
        // 因在不同的进程中，请手动关闭服务。
        
        $Logger.info("服务端宕机情况下的客户端通讯");
        this.test_断开重新连接_客户端($Clients);
    }
    
    
    
    // 建议服务端与客户端在两台不同的电脑上测试
    private void test_断开重新连接_客户端(List<ClientCluster> i_Clients)
    {
        List<ClientCluster> v_Exceptions = null;
        
        v_Exceptions = ClientSocketCluster.startServer(i_Clients);
        $Logger.info("启动异常：" + Help.NVL(v_Exceptions).size() + " 个");
        
        v_Exceptions = ClientSocketCluster.login(i_Clients ,new LoginRequest().setUserName("断开重新连接").setSystemName("测试"));
        $Logger.info("登录异常：" + Help.NVL(v_Exceptions).size() + " 个");
        
        Date                                      v_BTime         = new Date();
        boolean                                   v_IsSync        = true;
        Map<ClientCluster ,CommunicationResponse> v_ResponseDatas = ClientSocketCluster.getObjects(i_Clients ,v_IsSync ,0 ,"TEST_Date");
        for (Map.Entry<? extends ClientCluster ,CommunicationResponse> v_Item : v_ResponseDatas.entrySet())
        {
            CommunicationResponse v_ResponseData = v_Item.getValue();
            
            if ( v_ResponseData.getResult() == 0 )
            {
                if ( v_ResponseData.getData() != null && v_ResponseData.getData() instanceof Date )
                {
                    $Logger.info(v_ResponseData.getData().toString());
                }
            }
        }
        
        Date v_ETime = new Date();
        $Logger.info((v_IsSync ? "同步" : "并行") + "用时为：" + Date.toTimeLen(v_ETime.getTime() - v_BTime.getTime()));
    }
    
}
