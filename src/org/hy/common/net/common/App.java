package org.hy.common.net.common;





/**
 * Netty应用程序。顶级类
 * 
 *   超时时长有三个级别，优先级从高到低依次为
 * 
 *     最高级（通讯级）：通讯数据的超时时长，取 Timeout 类的 xxxTimeout 属性
 * 
 *     中等级（应用级）：客户端上配置的超时时长，取 App 类的 timeout 属性
 *                     当最高级为配置时，本级生效。
 * 
 *     最低级（默认级）：当上两级均为配置时，本级生效，取 Timeout 类的可变常量值 $Default_xxx
 * 
 * 
 *   超时时长的取值规则：
 *     0表示永不超时，一直等待
 *     负数或NULL：表示取默认超时时长
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
    
    /** 本应用的默认超时时长。每次通讯的数据超时时长级别高于本属性，只有当没有设置通讯数据超时时长时，本属性才生效。（单位：毫秒） */
    protected Long    timeout;
    
    /** 服务IP及端口（主要是缓存起来，方便每次的日志显示，节约一点点性能） */
    private   String  hostPort;
    
    
    
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
        this.port     = port;
        this.hostPort = this.host + ":" + this.port;
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
        this.host     = host;
        this.hostPort = this.host + ":" + this.port;
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



    /**
     * 获取：本应用的默认超时时长。每次通讯的数据超时时长级别高于本属性，只有当没有设置通讯数据超时时长时，本属性才生效。（单位：毫秒）
     * 
     * @return
     */
    public Long getTimeout()
    {
        return timeout;
    }

    

    /**
     * 设置：本应用的默认超时时长。每次通讯的数据超时时长级别高于本属性，只有当没有设置通讯数据超时时长时，本属性才生效。（单位：毫秒）
     * 
     * @param i_Timeout
     */
    @SuppressWarnings("unchecked")
    public T setTimeout(Long i_Timeout)
    {
        this.timeout = i_Timeout;
        return (T) this;
    }

    

    /**
     * 服务IP及端口（主要是缓存起来，方便每次的日志显示，节约一点点性能）
     * 
     * @return
     */
    public String getHostPort()
    {
        return hostPort;
    }
    
}
