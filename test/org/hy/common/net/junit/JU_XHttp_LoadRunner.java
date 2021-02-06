package org.hy.common.net.junit;

import org.hy.common.Date;
import org.hy.common.Help;
import org.hy.common.Return;
import org.hy.common.xml.XHttp;
import org.hy.common.xml.annotation.Xjava;
import org.junit.Test;





@Xjava
public class JU_XHttp_LoadRunner
{
    
    private XHttp xhttp;
    
    
    public JU_XHttp_LoadRunner()
    {
        this.xhttp = new XHttp();
        
        this.xhttp.setProtocol("http");
        this.xhttp.setIp("10.1.50.102");
        this.xhttp.setUrl("/wx/wechat/template/sendWxMpTemplateMessage");
    }
    
    
    
    @Test
    public void test_LoadRunner()
    {
        long v_Begin = Date.getNowTime().getTime();
        
        String [] v_OpenIDs = {"ohwW61ZJLmy0DrySmE-6IyikJpE0" ,"ohwW61TIn8Fa7eBTjKdkmCJET9Rc"};
        
        for (int i=1; i<=2000; i++)
        {
            int    v_OpenIndex = Help.random(0 ,1);
            String v_OpenID    = v_OpenIDs[v_OpenIndex];
            Return<?> v_Ret    = this.xhttp.request("openid=" + v_OpenID + "&first=" + i + "&keyword1=" + i+ "&keyword2=" + i+ "&keyword3=" + i+ "&remark=" + i+ "&templateId=_XSLvrgGNhT_QoQO92163z2Upx-SPZPk7t6StgL4FjQ&t=" + Date.getNowTime().getTime());
            
            System.out.println(v_OpenIndex + "wx - " + i + " - " +(Date.getNowTime().getTime() - v_Begin) / 1000 + "s - " + v_Ret.getParamStr());
        }
    }
    
}
