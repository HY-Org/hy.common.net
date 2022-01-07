package org.hy.common.net.common;





/**
 * 集群操作的接口
 * 
 * @author      ZhengWei(HY)
 * @createDate  2021-10-12
 * @version     v1.0
 */
public interface ClientCluster
{
    
    /**
     * 获取客户端的业务操作接口
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-10-12
     * @version     v1.0
     * 
     * @return
     */
    public ClientOperation operation();
    
    
    
    /**
     * 获取客户端的主机地址
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-10-12
     * @version     v1.0
     * 
     * @return
     */
    public String getHost();
    
    
    
    /**
     * 获取客户端的主机端口
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-10-12
     * @version     v1.0
     * 
     * @return
     */
    public int getPort();
    
    
    
    /**
     * 获取：会话时间（单位：秒）。空闲多少时间后，移除统计功能
     * 
     * 客户端在使用时，主要用于“统计”功能
     * 
     * @author      ZhengWei(HY)
     * @createDate  2022-01-07
     * @version     v1.0
     * 
     * @return
     */
    public long getSessionTime();
    
}
