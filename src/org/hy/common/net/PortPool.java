package org.hy.common.net;

import org.hy.common.Queue;
import org.hy.common.xml.log.Logger;





/**
 * 简单的端口池
 *
 * @author      ZhengWei(HY)
 * @createDate  2021-08-26
 * @version     v1.0
 */
public class PortPool
{
    private static final Logger $Logger = new Logger(PortPool.class);
    
    
    /** 服务端随机创建端口范围的最小端口号（可等于此属性值）。默认值：17000 */
    private int            minPort;
    
    /** 服务端随机创建端口范围的最大端口号（可等于此属性值）。默认值：17999 */
    private int            maxPort;
    
    /** 当前分配到哪一个端口 */
    private int            indexPort;
    
    /** 已打开的并且空闲的端口排列 */
    private Queue<Integer> portQueue;
    
    
    
    public PortPool()
    {
        this.indexPort = 0;
        this.portQueue = new Queue<Integer>();
    }
    
    
    
    /**
     * 获取空闲的端口。内部会阻塞等待
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-08-26
     * @version     v1.0
     *
     * @return
     */
    public synchronized Integer getIdle()
    {
        $Logger.debug("getIdle port is ready.");
        
        Integer v_IdlePort = 0;
        if ( this.maxPort > this.indexPort )
        {
            if ( this.minPort > this.indexPort )
            {
                this.indexPort = this.minPort;
                v_IdlePort = this.indexPort;
            }
            else
            {
                v_IdlePort = this.portQueue.get();
                if ( v_IdlePort == null )
                {
                    ++this.indexPort;
                    v_IdlePort = this.indexPort;
                }
            }
        }
        else
        {
            v_IdlePort = this.portQueue.get();
            if ( v_IdlePort == null )
            {
                do
                {
                    try
                    {
                        Thread.sleep(10);
                    }
                    catch (Exception exce)
                    {
                        // Nothing.
                    }
                    v_IdlePort = this.portQueue.get();
                }
                while ( v_IdlePort == null );
            }
        }
        
        $Logger.debug("getIdle port is " + v_IdlePort);
        return v_IdlePort;
    }
    
    
    
    /**
     * 添加空间的端口。如，端口释放后，也应调用此方法
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-08-26
     * @version     v1.0
     * 
     * @param i_Port
     * @return
     */
    public void putIdle(Integer i_Port)
    {
        this.portQueue.put(i_Port);
    }

    
    
    /**
     * 获取：服务端随机创建端口范围的最小端口号（可等于此属性值）。默认值：17000
     */
    public int getMinPort()
    {
        return minPort;
    }

    
    /**
     * 设置：服务端随机创建端口范围的最小端口号（可等于此属性值）。默认值：17000
     * 
     * @param minPort
     */
    public void setMinPort(int minPort)
    {
        this.minPort = minPort;
    }

    
    /**
     * 获取：服务端随机创建端口范围的最大端口号（可等于此属性值）。默认值：17999
     */
    public int getMaxPort()
    {
        return maxPort;
    }

    
    /**
     * 设置：服务端随机创建端口范围的最大端口号（可等于此属性值）。默认值：17999
     * 
     * @param maxPort
     */
    public void setMaxPort(int maxPort)
    {
        this.maxPort = maxPort;
    }
    
}
