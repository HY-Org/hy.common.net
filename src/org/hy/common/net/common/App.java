package org.hy.common.net.common;





/**
 * Netty应用程序。顶级类
 * 
 * @author      ZhengWei(HY)
 * @createDate  2021-09-16
 * @version     v1.0
 */
public class App<T extends App<T>>
{
    
    /** 是否启动 */
    protected boolean isStart;
    
    /** 服务的IP */
    protected String  host;
    
    /** 服务的端口 */
    protected int     port;
    
    /** 注释说明 */
    protected String  comment;
    
    
    
    /**
     * 启动程序
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-09-15
     * @version     v1.0
     */
    @SuppressWarnings("unchecked")
    public T start()
    {
        this.isStart = true;
        
        return (T) this;
    }
    
    
    
    /**
     * 关闭程序
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-09-15
     * @version     v1.0
     */
    public void shutdown()
    {
        this.isStart = false;
    }
    
    
    
    public App()
    {
        this.isStart = false;
    }
    
    
    
    /**
     * 判定服务启动状态
     * 
     * @return
     */
    public boolean isStart()
    {
        return isStart;
    }
    
    
    
    /**
     * 获取：服务的端口
     * 
     * @return
     */
    public int getPort()
    {
        return port;
    }


    
    /**
     * 设置：服务的端口
     * 
     * @param port
     */
    @SuppressWarnings("unchecked")
    public T setPort(int port)
    {
        this.port = port;
        return (T) this;
    }
    
    
    
    /**
     * 获取：服务的端口
     * 
     * @return
     */
    public String getHost()
    {
        return host;
    }


    
    /**
     * 获取：服务的端口
     * 
     * @param host
     */
    @SuppressWarnings("unchecked")
    public T setHost(String host)
    {
        this.host = host;
        return (T) this;
    }
    
    
    
    /**
     * 获取：注释说明
     * 
     * @return
     */
    public String getComment()
    {
        return comment;
    }
    
    
    
    /**
     * 设置：注释说明
     * 
     * @param comment
     */
    @SuppressWarnings("unchecked")
    public T setComment(String comment)
    {
        this.comment = comment;
        return (T) this;
    }
    
}
