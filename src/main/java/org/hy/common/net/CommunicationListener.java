package org.hy.common.net;

import org.hy.common.net.protocol.ServerEventListener;





/**
 * 数据通讯的监听事件接口
 *
 * @author      ZhengWei(HY)
 * @createDate  2017-01-16
 * @version     v1.0
 *              v2.0  2021-09-27  添加：处理数据通讯的过程是否为同步的。当为 false 时，表示服务端开启线程处理
 *              v2.0  2021-09-29  废弃：实现 ServerEventListener 接口。
 *                                     保留本类及本类的包路径是为了：兼容老版本
 *                                     新版本建议直接使用 ServerEventListener 接口。
 */
public interface CommunicationListener extends ServerEventListener
{
    
}
