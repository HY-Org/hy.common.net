package org.hy.common.net.junit.nio.copyfile;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;





/**
 * 零拷贝文件（不使用CPU的拷贝）的服务端
 * 
 * @author      ZhengWei(HY)
 * @createDate  2021-09-06
 * @version     v1.0
 */
public class ZeroCopyFileServer
{
    
    public static void main(String [] args) throws IOException
    {
        ServerSocketChannel v_Channel = ServerSocketChannel.open();
        ServerSocket        v_Socket  = v_Channel.socket();
        
        v_Channel.bind(new InetSocketAddress(2021));
        
        ByteBuffer v_Buffer = ByteBuffer.allocate(1024 * 4);
        
        while ( true )
        {
            SocketChannel v_RequestChannel = v_Channel.accept();
            
            int v_ReadCount = 0;
            while ( -1 != v_ReadCount )
            {
                v_RequestChannel.read(v_Buffer);
                
                v_Buffer.rewind(); // 倒带
            }
        }
    }
    
}
