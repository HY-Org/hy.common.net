package org.hy.common.net.junit;

import org.hy.common.thread.ThreadBase;
import org.hy.common.thread.ThreadPool;
import org.junit.Test;





public class JU_LoadRunner
{
    
    @Test
    public void loadRunner()
    {
        ThreadPool.setMaxThread(100);
        ThreadPool.setMinThread(10);
        ThreadPool.setMinIdleThread(2);
        ThreadPool.setIntervalTime(100);
        ThreadPool.setIdleTimeKill(600);
        ThreadPool.setWatch(true);
        
        
        
        for (int i=1; i<=100000; i++)
        {
            ThreadBase v_ThreadBase = ThreadPool.getThreadInstance(new JU_LoadRunnerThreadTask());
            v_ThreadBase.startupAndExecuteTask();
        }
        
        
        
        while ( ThreadPool.getActiveThreadCount() >= 1 )
        {
            ThreadPool.sleep(1000);
        }
        System.out.println("-- " + ThreadPool.showInfo());
        
        ThreadPool.shutdownAllThread();
    }
    
}
