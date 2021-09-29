package org.hy.common.net.junit;

import java.util.ArrayList;
import java.util.List;

import org.hy.common.Date;
import org.hy.common.net.ClientSocket;
import org.hy.common.net.ServerSocket;
import org.hy.common.net.data.CommunicationRequest;
import org.hy.common.net.data.CommunicationResponse;
import org.hy.common.net.data.LoginRequest;
import org.hy.common.net.protocol.ServerValidate;
import org.hy.common.net.socket.ClientSocketValidate;
import org.hy.common.xml.SerializableDef;
import org.hy.common.xml.XJava;
import org.junit.Test;





public class JU_ServerSocket extends SerializableDef implements ServerValidate ,ClientSocketValidate
{
    
    private static final long serialVersionUID = -1693715094193260126L;
    
    

    private String       stringValue;
    
    private Integer      integerValue;
    
    private int          intValue;
    
    private Date         dateValue;
    
    private List<String> stringListValue;
    
    
    
    public JU_ServerSocket()
    {
        this.stringValue     = "abcdefg";
        this.integerValue    = 1234567890;
        this.intValue        = 987654321;
        this.dateValue       = new Date("2017-01-14");
        this.stringListValue = new ArrayList<String>();
        
        this.stringListValue.add("qaz");
        this.stringListValue.add("wsx");
        this.stringListValue.add("edc");
    }
    
    
    
    /**
     * 客户端的获取登陆信息
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-01-15
     * @version     v1.0
     *
     * @return
     */
    @Override
    public LoginRequest getLoginRequest()
    {
        return new LoginRequest("ZhengWei(HY)" ,"2017-01-15");
    }
    
    
    
    /**
     * 服务端的登陆验证方法
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-01-15
     * @version     v1.0
     *
     * @param i_LoginRequest  登陆信息
     * @return                验证成功时，返回true
     */
    @Override
    public boolean validate(LoginRequest i_LoginRequest)
    {
        if ( "ZhengWei(HY)".equals(i_LoginRequest.getUserName())
          && "2017-01-15"  .equals(i_LoginRequest.getPassword()) )
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
    
    
    @Test
    public void test_001() throws InterruptedException
    {
        ServerSocket v_Server = new ServerSocket().setValidate(this);
        
        v_Server.open();
        
        
        
        ClientSocket          v_Client       = new ClientSocket().setValidate(this);
        CommunicationRequest  v_RequestData  = null;
        CommunicationResponse v_ResponseData = null;
        
        
        // 远程添加操作
        v_RequestData = new CommunicationRequest();
        v_RequestData.setDataXID("JU_ServerSocket");
        v_RequestData.setData(new JU_ServerSocket());
        v_RequestData.setDataOperation(CommunicationRequest.$Operation_Insert);
        
        v_ResponseData = v_Client.send(v_RequestData);
        
        if ( v_ResponseData.getResult() == 0 )
        {
            System.out.println("通讯成功：远程添加。");
            System.out.println(XJava.getObject("JU_ServerSocket"));
        }
        else
        {
            System.out.println("通讯异常：错误编码为 " + v_ResponseData.getResult());
        }
        
        
        // 获取远程对象
        v_RequestData = new CommunicationRequest();
        v_RequestData.setDataXID("JU_ServerSocket");
        v_RequestData.setDataOperation(CommunicationRequest.$Operation_Select);
        
        v_ResponseData = v_Client.send(v_RequestData);
        
        if ( v_ResponseData.getResult() == 0 )
        {
            System.out.println("通讯成功：获取远程对象。");
            System.out.println(v_ResponseData.getData());
        }
        else
        {
            System.out.println("通讯异常：错误编码为 " + v_ResponseData.getResult());
        }
        
        // 等待临时通讯端口被超时后关闭
        Thread.sleep(5 * 60 * 1000);
    }


    
    public String getStringValue()
    {
        return stringValue;
    }

    public void setStringValue(String stringValue)
    {
        this.stringValue = stringValue;
    }

    public Integer getIntegerValue()
    {
        return integerValue;
    }
    
    public void setIntegerValue(Integer integerValue)
    {
        this.integerValue = integerValue;
    }

    public int getIntValue()
    {
        return intValue;
    }

    public void setIntValue(int intValue)
    {
        this.intValue = intValue;
    }

    public Date getDateValue()
    {
        return dateValue;
    }

    public void setDateValue(Date dateValue)
    {
        this.dateValue = dateValue;
    }
    
    public List<String> getStringListValue()
    {
        return stringListValue;
    }
    
    public void setStringListValue(List<String> stringListValue)
    {
        this.stringListValue = stringListValue;
    }

}
