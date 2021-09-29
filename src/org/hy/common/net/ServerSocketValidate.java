package org.hy.common.net;

import org.hy.common.net.protocol.ServerValidate;





/**
 * 服务端的登陆验证接口
 *
 * @author      ZhengWei(HY)
 * @createDate  2017-01-15
 * @version     v1.0
 *              v2.0  2021-09-29  废弃：实现 ServerValidate 接口。
 *                                     保留本类及本类的包路径是为了：兼容老版本
 *                                     新版本建议直接使用 ServerValidate 接口。
 */
public interface ServerSocketValidate extends ServerValidate
{
    
}
