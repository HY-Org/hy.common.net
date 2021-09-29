package org.hy.common.net.socket;

import java.net.Socket;





/**
 * Socket请求接口（通用的）
 *
 * @author      ZhengWei(HY)
 * @createDate  2017-01-13
 * @version     v1.0
 */
public interface SocketRepuest
{
    
    /**
     * 当服务端接收到请求时执行的动作。
     * 
     * 此接口将被服务端以多线程的方式触发执行。
     * 
     * 注意：具体的实现类要负责关闭Socket。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-01-13
     * @version     v1.0
     *
     * @param i_ServerBase  接收请求的服务端Socket服务
     * @param i_Socket      接收到的Socket请求
     */
    public void request(ServerBase i_ServerBase ,Socket i_Socket);
    
}
