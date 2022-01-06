package org.hy.common.net.common;

import java.util.List;

import org.hy.common.net.data.ClientUserInfo;





/**
 * 服务端的公开方法接口
 * 
 * @author      ZhengWei(HY)
 * @createDate  2022-01-04
 * @version     v1.0
 */
public interface ServerOperation
{
    
    /**
     * 服务端口
     * 
     * @author      ZhengWei(HY)
     * @createDate  2022-01-05
     * @version     v1.0
     * 
     * @return
     */
    public int getPort();
    
    
    
    /**
     * 获取：登录的客户信息
     * 
     * @author      ZhengWei(HY)
     * @createDate  2022-01-04
     * @version     v1.0
     * 
     * @return
     */
    public List<ClientUserInfo> getClientUsers();
    
    
    
    /**
     * 获取：会话时间（单位：秒）。空闲多少时间后，移除登录会话
     * 
     * @author      ZhengWei(HY)
     * @createDate  2022-01-05
     * @version     v1.0
     * 
     * @return
     */
    public long getSessionTime();
    
    
    
    /**
     * 获取：同一账户多次登录的最大上限，当超过时自动退出之前的任一登录账户。
     * 
     * 默认为：0，表示允许同一账户无限制登录
     * 设置成：1，表示当前最新的登录，将踢出之前的登录
     * 
     * @author      ZhengWei(HY)
     * @createDate  2022-01-05
     * @version     v1.0
     * 
     * @return
     */
    public int getSameUserOnlineMaxCount();
    
    
    
    /**
     * 重置统计数据
     * 
     * @author      ZhengWei(HY)
     * @createDate  2022-01-06
     * @version     v1.0
     *
     */
    public void reset();
    
}
