package org.hy.common.net.junit;

import org.hy.common.Date;
import org.hy.common.thread.TaskGroup;
import org.hy.common.thread.TaskPool;
import org.hy.common.thread.ThreadBase;
import org.hy.common.thread.ThreadPool;
import org.junit.Test;





public class JU_LoadRunner
{
    
    @Test
    public void loadRunner()
    {
        ThreadPool.setMaxThread(100);
        ThreadPool.setMinThread(1);
        ThreadPool.setMinIdleThread(2);
        ThreadPool.setIntervalTime(100);
        ThreadPool.setIdleTimeKill(600);
        ThreadPool.setWatch(true);
        

        
        for (int i=1; i<=10000; i++)
        {
            ThreadBase v_ThreadBase = ThreadPool.getThreadInstance(new JU_LoadRunnerThreadTask());
            v_ThreadBase.startupAndExecuteTask();
            
            System.out.println(i);
        }
        
        
        
        while ( ThreadPool.getActiveThreadCount() >= 1 )
        {
            ThreadPool.sleep(1000);
        }
        System.out.println("-- " + ThreadPool.showInfo());
        
        ThreadPool.shutdownAllThread();
    }
    
    
    
    @Test
    public void loadRunnerV2() throws InterruptedException
    {
        ThreadPool.setMaxThread(100);
        ThreadPool.setMinThread(1);
        ThreadPool.setMinIdleThread(2);
        ThreadPool.setIntervalTime(100);
        ThreadPool.setIdleTimeKill(600);
        ThreadPool.setWatch(true);
        
        
        
        int       v_Size      = 1000000;
        TaskGroup v_TaskGroup = new TaskGroup("XSSO");
        for (int v_Index=0; v_Index<v_Size; v_Index++)
        {
            v_TaskGroup.addTask(new JU_LoadRunnerThreadTask());
        }
        
        Date v_BTime = Date.getNowTime();
        System.out.println(v_BTime.getFull() + "  开始");
        v_TaskGroup.startupAllTask();
        
        
        while ( TaskPool.size() >= 1 )
        {
            ThreadPool.sleep(1000);
        }
        Date v_ETime = Date.getNowTime();
        System.out.println(v_ETime.getFull() + "  完成");
        System.out.println("共用时长：" + Date.toTimeLen(v_ETime.getTime() - v_BTime.getTime()));
        System.out.println("平均时长：" + (v_ETime.getTime() - v_BTime.getTime()) * 1D / v_Size + " 毫秒处理一次");
        
        
        Thread.sleep(1000 * 60 * 10);
        ThreadPool.shutdownAllThread();
    }
    
}
