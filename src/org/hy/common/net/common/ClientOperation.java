package org.hy.common.net.common;

import org.hy.common.net.data.CommunicationRequest;
import org.hy.common.net.data.CommunicationResponse;
import org.hy.common.net.data.LoginRequest;
import org.hy.common.net.data.LoginResponse;





/**
 * 客户端的公开方法接口
 * 
 * @author      ZhengWei(HY)
 * @createDate  2021-09-28
 * @version     v1.0
 */
public interface ClientOperation
{
    
    /**
     * 启动服务
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-12-06
     * @version     v1.0
     */
    public boolean startServer();
    
    
    
    /**
     * 关闭服务
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-12-06
     * @version     v1.0
     */
    public boolean shutdownServer();
    
    
    
    /**
     * 是否登录成功
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-09-28
     * @version     v1.0
     * 
     * @return
     */
    public boolean isLogin();
    
    
    
    /**
     * 登录操作
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-09-28
     * @version     v1.0
     * 
     * @param i_Request  登录信息。必填参数[userName ,systemName]
     * @return
     */
    public LoginResponse login(LoginRequest i_Request);
    
    
    
    /**
     * 登出操作。一般为异常时，内部自动调用使用
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-10-19
     * @version     v1.0
     */
    public boolean logout();
    
    
    
    /**
     * 发送通讯数据
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-09-28
     * @version     v1.0
     * 
     * @param i_Request  通讯请求
     * @return
     */
    public CommunicationResponse send(CommunicationRequest i_Request);
    
    
    
    /**
     * 向服务端发送执行命令
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-01-17
     * @version     v1.0
     *
     * @param i_XID            XJava对象池的ID
     * @param i_Command        执行命令名称（即方法名称）
     * @return
     */
    public CommunicationResponse sendCommand(String i_XID ,String i_Command);
    
    
    
    /**
     * 向服务端发送执行命令
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-01-17
     * @version     v1.0
     *
     * @param i_XID            XJava对象池的ID
     * @param i_Command        执行命令名称（即方法名称）
     * @param i_ServerIsReturn 服务端是否返回执行结果的数据
     * @return
     */
    public CommunicationResponse sendCommand(String i_XID ,String i_Command ,boolean i_ServerIsReturn);
    
    
    
    /**
     * 向服务端发送执行命令
     * 
     * @author      ZhengWei(HY)
     * @createDate  2019-02-27
     * @version     v1.0
     *
     * @param i_XID            XJava对象池的ID
     * @param i_Command        执行命令名称（即方法名称）
     * @param i_CommandParams  执行命令参数（即方法参数）
     * @return
     */
    public CommunicationResponse sendCommand(String i_XID ,String i_Command ,Object [] i_CommandParams);
    
    
    
    /**
     * 向服务端发送执行命令
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-01-17
     * @version     v1.0
     *              v2.0  2019-02-27  添加：服务端是否返回执行结果的数据
     *
     * @param i_XID            XJava对象池的ID
     * @param i_Command        执行命令名称（即方法名称）
     * @param i_CommandParams  执行命令参数（即方法参数）
     * @param i_ServerIsReturn 服务端是否返回执行结果的数据
     * @return
     */
    public CommunicationResponse sendCommand(String i_XID ,String i_Command ,Object [] i_CommandParams ,boolean i_ServerIsReturn);
    
    
    
    /**
     * 移除服务端的对象（默认从XJava对象池中移除）。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2019-02-27
     * @version     v1.0
     *
     * @param i_XID            XJava对象池的ID
     * @return
     */
    public CommunicationResponse removeObject(String i_XID);
    
    
    
    /**
     * 移除服务端的对象（默认从XJava对象池中移除）。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-01-23
     * @version     v1.0
     *              v2.0  2019-02-27  添加：服务端是否返回执行结果的数据
     *
     * @param i_XID            XJava对象池的ID
     * @param i_ServerIsReturn 服务端是否返回执行结果的数据
     * @return
     */
    public CommunicationResponse removeObject(String i_XID ,boolean i_ServerIsReturn);
    
    
    
    /**
     * 获取服务端的对象（默认从XJava对象池中获取）。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-01-23
     * @version     v1.0
     *
     * @param i_XID            XJava对象池的ID
     * @return
     */
    public CommunicationResponse getObject(String i_XID);
    
    
    
    /**
     * 获取服务端的多个对象（默认从XJava对象池中获取）。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-02-07
     * @version     v1.0
     *
     * @param i_XID  XJava对象池的ID标识符的前缀(区分大小写)
     * @return       CommunicationResponse.getData() 是一个List<CommunicationResponse>()结构的实例。
     */
    public CommunicationResponse getObjects(String i_XID);
    
    
    
    /**
     * 获取服务端的Java.getSessionMap()数据。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-02-28
     * @version     v1.0
     *
     * @return       CommunicationResponse.getData() 是一个ExpireMap<String ,Object>()结构的实例。
     */
    public CommunicationResponse getSessionMap();
    
    
    
    /**
     * 向服务端中写入或更新对象（默认将写入XJava对象池）。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-01-23
     * @version     v1.0
     *
     * @param i_XID            XJava对象池的ID
     * @param i_Data           XJava对象
     * @return
     */
    public CommunicationResponse sendObject(String i_XID ,Object i_Data);
    
    
    
    /**
     * 向服务端中写入或更新对象（默认将写入XJava对象池）。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2019-02-27
     * @version     v1.0
     *
     * @param i_XID            XJava对象池的ID
     * @param i_Data           XJava对象
     * @param i_ServerIsReturn 服务端是否返回执行结果的数据
     * @return
     */
    public CommunicationResponse sendObject(String i_XID ,Object i_Data ,boolean i_ServerIsReturn);
    
    
    
    /**
     * 向服务端中写入或更新对象（默认将写入XJava对象池）。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2019-02-27
     * @version     v1.0
     *
     * @param i_XID            XJava对象池的ID
     * @param i_Data           XJava对象
     * @param i_ExpireTimeLen  数据的过期时长(单位：秒)
     * @return
     */
    public CommunicationResponse sendObject(String i_XID ,Object i_Data ,long i_ExpireTimeLen);
    
    
    
    /**
     * 向服务端中写入或更新对象（默认将写入XJava对象池）。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-01-23
     * @version     v1.0
     *              v2.0  2019-02-27  添加：服务端是否返回执行结果的数据
     *
     * @param i_XID            XJava对象池的ID
     * @param i_Data           XJava对象
     * @param i_ExpireTimeLen  数据的过期时长(单位：秒)
     * @param i_ServerIsReturn 服务端是否返回执行结果的数据
     * @return
     */
    public CommunicationResponse sendObject(String i_XID ,Object i_Data ,long i_ExpireTimeLen ,boolean i_ServerIsReturn);
    
}
