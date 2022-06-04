package org.hy.common.net.junit.netty.loadrunner;

import java.util.List;

import org.hy.common.Help;
import org.hy.common.net.data.CommunicationResponse;
import org.hy.common.thread.Task;
import org.hy.common.xml.log.Logger;





/**
 * 测试单元：XJavaCloud的压力测试的线程任务
 *
 * @author   ZhengWei(HY)
 * @version  V1.0  2021-10-11
 */
public class JU_LoadRunnerThreadTask extends Task<Object>
{
    private static final Logger $Logger = new Logger(JU_LoadRunnerThreadTask.class ,true);
    
    private static int          $SerialNo     = 0;
    
    public  static int          $RequestCount = 0;
    
    public  static int          $ErrorCount   = 0;
    
    public  static int          $FinishCount  = 0;
    
    public final  static String $USID = "USID";
    
    private JU_LoadRunner father;
    
    
    
    /**
     * 注意：本方法可能在多个实例、多个线程中执行，所以要用 static synchronized
     * 
     * @return
     */
    private static synchronized int GetSerialNo()
    {
        return ++$SerialNo;
    }
    
    
    
    /**
     * 注意：本方法可能在多个实例、多个线程中执行，所以要用 static synchronized
     * 
     * @return
     */
    public static synchronized int GetRequestCount()
    {
        return ++$RequestCount;
    }
    
    
    
    /**
     * 注意：本方法可能在多个实例、多个线程中执行，所以要用 static synchronized
     * 
     * @return
     */
    public static synchronized int GetErrorCount()
    {
        return ++$ErrorCount;
    }
    
    
    
    /**
     * 注意：本方法可能在多个实例、多个线程中执行，所以要用 static synchronized
     * 
     * @return
     */
    public static synchronized int GetFinishCount()
    {
        return ++$FinishCount;
    }
    
    
    
    public JU_LoadRunnerThreadTask(JU_LoadRunner i_Father)
    {
        super("JU_LoadRunnerThreadTask");
        this.father = i_Father;
    }

    
    
    @Override
    @SuppressWarnings("unchecked")
    public void execute()
    {
        int v_RCount = GetRequestCount();
        
        try
        {
            CommunicationResponse v_ResponseData = null;
            
            v_ResponseData = this.father.getClient().operation().getObjects(0 ,JU_LoadRunner.$ID); // $USID +
            
            if ( v_ResponseData != null && v_ResponseData.getResult() == 0 )
            {
                List<CommunicationResponse> v_Datas = (List<CommunicationResponse>)v_ResponseData.getData();
                
                if ( !Help.isNull(v_Datas) )
                {
                    GetFinishCount();
                    $Logger.info("共同步 " + v_Datas.size() + " 份。第 " + v_RCount + " 个任务");
                }
                else
                {
                    GetErrorCount();
                    $Logger.info("未获取到数据");
                }
            }
            else
            {
                GetErrorCount();
                $Logger.info("异常：" + v_ResponseData.getResult());
            }
        }
        catch (Throwable exce)
        {
            GetErrorCount();
            exce.printStackTrace();
        }
        
        this.finishTask();
    }
    
    
    
    public void execute_stop()
    {
        GetRequestCount();
        GetFinishCount();
        this.finishTask();
    }
    
    
    
    /**
     * 获取任务描述
     * 
     * @return
     */
    @Override
    public String getTaskDesc()
    {
        return "测试" + this.getTaskName();
    }
    
    
    @Override
    protected void finalize() throws Throwable
    {
        super.finalize();
    }


    @Override
    public long getSerialNo()
    {
        return GetSerialNo();
    }

}
