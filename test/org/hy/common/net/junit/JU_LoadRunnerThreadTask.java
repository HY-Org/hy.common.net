package org.hy.common.net.junit;

import java.util.List;

import org.hy.common.Help;
import org.hy.common.net.ClientSocket;
import org.hy.common.net.data.CommunicationResponse;
import org.hy.common.thread.Task;
import org.hy.common.xml.log.Logger;





/**
 * 测试线程任务
 *
 * @author   ZhengWei(HY)
 * @version  V1.0  2019-09-26
 */
public class JU_LoadRunnerThreadTask extends Task<Object>
{
    private static final Logger $Logger = new Logger(JU_LoadRunnerThreadTask.class ,true);
    
    private static int $SerialNo = 0;
    
    private static int $Count    = 0;
    
    public final  static String $USID = "USID";
    
    
    
    private synchronized int GetSerialNo()
    {
        return ++$SerialNo;
    }
    
    
    private synchronized int GetCount()
    {
        return ++$Count;
    }
    

    public JU_LoadRunnerThreadTask()
    {
        super("JU_LoadRunnerThreadTask");
    }

    
    @Override
    @SuppressWarnings("unchecked")
    public void execute()
    {
        try
        {
            ClientSocket                v_Server       = new ClientSocket("10.1.50.242" ,2021);
            CommunicationResponse       v_ResponseData = null;
            List<CommunicationResponse> v_Datas        = null;
            
            v_ResponseData = v_Server.getObjects($USID + "00DC3B6A1A92E063122403BDF7B63789");
            
            if ( v_ResponseData != null && v_ResponseData.getResult() == 0 )
            {
                v_Datas = (List<CommunicationResponse>)v_ResponseData.getData();
                
                if ( !Help.isNull(v_Datas) )
                {
                    // $Logger.info("共同步 " + v_Datas.size() + " 份。" + GetCount());
                }
                else
                {
                    $Logger.info("未获取到数据");
                }
            }
        }
        catch (Throwable exce)
        {
            exce.printStackTrace();
        }
        
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
