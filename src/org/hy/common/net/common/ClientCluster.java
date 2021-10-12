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
    
}
