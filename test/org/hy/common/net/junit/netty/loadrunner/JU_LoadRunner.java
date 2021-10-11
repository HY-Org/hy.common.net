package org.hy.common.net.junit.netty.loadrunner;

import org.hy.common.Date;
import org.hy.common.Help;
import org.hy.common.app.AppParameter;
import org.hy.common.net.data.LoginRequest;
import org.hy.common.net.netty.rpc.ClientRPC;
import org.hy.common.thread.TaskGroup;
import org.hy.common.thread.TaskPool;
import org.hy.common.thread.ThreadPool;
import org.junit.Test;





/**
 * 测试单元：XJavaCloud的压力测试
 * 
 * @author      ZhengWei(HY)
 * @createDate  2021-10-11
 * @version     v1.0
 */
public class JU_LoadRunner
{
    
    public static String $ID          = "0175C6BE1B7BFCF2390793C91642AEC0";
    
    public static int    $ForCount    = 100000;
    
    public static int    $ThreadCount = 100;

    public static int    $Timeout     = 60 * 1000;
    
    private ClientRPC client;
    
    
    
    public ClientRPC getClient()
    {
        return client;
    }



    @Test
    public void loadRunner() throws InterruptedException
    {
        ThreadPool.setMaxThread($ThreadCount);
        ThreadPool.setMinThread(1);
        ThreadPool.setMinIdleThread(2);
        ThreadPool.setIntervalTime(10);
        ThreadPool.setIdleTimeKill(600);
        ThreadPool.setWatch(true);
        
        
        // 步骤1：连接
        this.client = new ClientRPC().setHost("10.1.50.242").setPort(3021).start();
        
        // 步骤2：登录
        LoginRequest v_LoginRequest = new LoginRequest();
        v_LoginRequest.setUserName  ("用户1");
        v_LoginRequest.setSystemName("系统1");
        this.client.operation().login(v_LoginRequest);
        
        
        TaskGroup v_TaskGroup = new TaskGroup("XSSO");
        for (int v_Index=0; v_Index<$ForCount; v_Index++)
        {
            v_TaskGroup.addTask(new JU_LoadRunnerThreadTask(this));
        }
        
        Date v_BTime = Date.getNowTime();
        System.out.println(v_BTime.getFull() + "  开始");
        v_TaskGroup.startupAllTask();
        
        
        while ( TaskPool.size() >= 1 || ThreadPool.getActiveThreadCount() >= 2 )
        {
            ThreadPool.sleep(1000);
        }
        Date v_ETime = Date.getNowTime();
        System.out.println(v_ETime.getFull() + "  完成");
        System.out.println("计划次数：" + $ForCount);
        System.out.println("请求次数：" + JU_LoadRunnerThreadTask.$RequestCount);
        System.out.println("完成次数：" + JU_LoadRunnerThreadTask.$FinishCount);
        System.out.println("成功比率：" + Help.division(4 ,(JU_LoadRunnerThreadTask.$FinishCount * 100) + "" ,JU_LoadRunnerThreadTask.$RequestCount + "") + "%");
        System.out.println("共用时长：" + Date.toTimeLen(v_ETime.getTime() - v_BTime.getTime()));
        System.out.println("访问均时：" + (v_ETime.getTime() - v_BTime.getTime()) * 1D / $ForCount                            + " 毫秒处理一次");
        System.out.println("成功均时：" + (v_ETime.getTime() - v_BTime.getTime()) * 1D / JU_LoadRunnerThreadTask.$FinishCount + " 毫秒处理一次");
        
        
        Thread.sleep(1000 * 60 * 1);
        ThreadPool.shutdownAllThread();
    }
    
    
    
    public static void main(String [] args) throws InterruptedException
    {
        AppParameter v_AppParams   = new AppParameter(args);
        String       v_ID          = v_AppParams.getParamValue("id");       // 数据ID
        String       v_ThreadCount = v_AppParams.getParamValue("tc");       // 线程数据
        String       v_ForCount    = v_AppParams.getParamValue("fc");       // 循环数量
        String       v_Timeout     = v_AppParams.getParamValue("timeout");  // 每个连接的超时时长
        
        if ( v_AppParams.isShowHelp() )
        {
            System.out.println(showHelpInfo());
            return;
        }
        
        if ( Help.isNull(v_ID) )
        {
            System.out.println("入参id为空，如，id=xxx");
            return;
        }
        
        $ID = v_ID;
        
        if ( Help.isNumber(v_ThreadCount) )
        {
            $ThreadCount = Integer.parseInt(v_ThreadCount);
            
            if ( $ThreadCount <= 0 )
            {
                $ThreadCount = 100;
            }
        }
        
        if ( Help.isNumber(v_ForCount) )
        {
            $ForCount = Integer.parseInt(v_ForCount);
            
            if ( $ForCount <= 0 )
            {
                $ForCount = 100;
            }
        }
        
        if ( Help.isNumber(v_Timeout) )
        {
            $Timeout = Integer.parseInt(v_Timeout);
            
            if ( $Timeout <= 0 )
            {
                $Timeout = $Timeout * 1000;
            }
        }
        
        (new JU_LoadRunner()).loadRunner();
    }
    
    
    
    private static String showHelpInfo()
    {
        StringBuilder v_Buffer = new StringBuilder();
        
        v_Buffer.append("\n");
        v_Buffer.append("Net单点登录压测工具");
        v_Buffer.append("\n");
        v_Buffer.append("\n命令格式：");
        v_Buffer.append("\n\tjava org.hy.common.net.junit.JU_LoadRunner <id=xxx> [tc=100] [fc=1000000] [timeout=60]");
        v_Buffer.append("\n");
        v_Buffer.append("\n命令参数说明：");
        v_Buffer.append("\n\t").append("id       ").append("数据ID");
        v_Buffer.append("\n\t").append("tc       ").append("线程最大数据");
        v_Buffer.append("\n\t").append("fc       ").append("压测数量");
        v_Buffer.append("\n\t").append("timeout  ").append("连接超时时长（单位：秒）");
        v_Buffer.append("\n\t").append("/?       ").append("显示帮助信息");
        v_Buffer.append("\n");
        
        return v_Buffer.toString();
    }
    
}
