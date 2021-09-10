package org.hy.common.net.junit.nio.copyfile;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;

import org.hy.common.Date;
import org.hy.common.xml.log.Logger;





/**
 * 零拷贝文件（不使用CPU的拷贝）的客户端
 * 
 * @author      ZhengWei(HY)
 * @createDate  2021-09-06
 * @version     v1.0
 */
public class ZeroCopyFileClient
{
    private static final Logger $Logger = new Logger(ZeroCopyFileClient.class ,true);
    
    
    
    public static void main(String [] args) throws IOException
    {
        SocketChannel v_Channel = SocketChannel.open();
        v_Channel.connect(new InetSocketAddress("127.0.0.1" ,2021));
        
        String          v_CopyFileName = "C:\\Users\\hyzhe\\Downloads\\002.Count And Move.mp4";
        FileInputStream v_FileIn       = new FileInputStream(v_CopyFileName);
        FileChannel     v_FileChannel  = v_FileIn.getChannel();
        
        long        v_BTime        = Date.getNowTime().getTime();
        
        // 在Linux 2.4下，transoferTo就可以完成文件的传输
        // 在Windows下，transferTo只能发送8M大小，大文件就要分段输文件
        v_FileChannel.transferTo(0 ,v_FileChannel.size() ,v_Channel);
        
        v_FileChannel.close();
        
        long v_ETime = Date.getNowTime().getTime();
        
        
        $Logger.info("拷贝总用时长：" + Date.toTimeLen(v_ETime - v_BTime));
    }
    
}
