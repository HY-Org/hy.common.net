package org.hy.common.net.junit.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Arrays;

import org.hy.common.xml.log.Logger;
import org.junit.Test;





/**
 * 测试单元：测试NIO的Buffer
 * 
 * @author      ZhengWei(HY)
 * @createDate  2021-08-30
 * @version     v1.0
 */
public class JU_NIOBuffer
{
    private static final Logger $Logger = new Logger(JU_NIOBuffer.class ,true);
    
    
    
    @Test
    public void test_NioIntBuffer()
    {
        IntBuffer v_NIOBuffer = IntBuffer.allocate(10);
        
        // 向Buffer中写入数据
        for (int i=0; i<v_NIOBuffer.capacity(); i++)
        {
            v_NIOBuffer.put(i * 2);
        }
        
        // Buffer的读写切换开关。由读转向写、由写转向读
        v_NIOBuffer.flip();
        
        // 读取Buffer中的数据
        while ( v_NIOBuffer.hasRemaining() )
        {
            $Logger.info(v_NIOBuffer.get());  // 每get一次，读指针后移一次
        }
    }
    
    
    
    /**
     * 测试：运用多个Buffer共同操作读写，
     *    a. Scattering 分离
     *    b. Gathering  聚合
     * 
     * @throws IOException
     */
    @Test
    public void test_ScatteringGathering() throws IOException
    {
        ServerSocketChannel v_SSChannel = ServerSocketChannel.open();
        
        // 绑定端口，并启动
        v_SSChannel.socket().bind(new InetSocketAddress(2021));
        
        // 非阻塞模式（可选的）
        // v_SSChannel.configureBlocking(false);
        
        ByteBuffer [] v_Buffers = new ByteBuffer[3];
        v_Buffers[0] = ByteBuffer.allocate(2);
        v_Buffers[1] = ByteBuffer.allocate(4);
        v_Buffers[2] = ByteBuffer.allocate(8);
        
        // 等待客服端的连接
        SocketChannel v_Channel   = v_SSChannel.accept();
        long          v_MaxSize   = 2 + 4 + 8;
        long          v_ReadSize  = 0;
        long          v_WirteSize = 0;
        long          v_SumSize   = 0;
        
        while ( true )
        {
            // 1. 读取客服端的数据
            while ( v_SumSize < v_MaxSize )
            {
                v_ReadSize = v_Channel.read(v_Buffers);
                v_SumSize += v_ReadSize;
                    
                Arrays.asList(v_Buffers).stream().map(i_Buffer -> "position = " + i_Buffer.position() + " ,limit = " + i_Buffer.limit()).forEach(System.out::println);
            }
            
            // 反转所有Buffer，为响应客户端作准备
            Arrays.asList(v_Buffers).forEach(i_Buffer -> i_Buffer.flip());
            
            // 2. 响应客户端的消息
            while ( v_WirteSize < v_SumSize )
            {
                v_WirteSize += v_Channel.write(v_Buffers);
            }
            
            // 重置所有Buffer
            Arrays.asList(v_Buffers).forEach(i_Buffer -> i_Buffer.clear());
            
            v_ReadSize  = 0;
            v_WirteSize = 0;
            v_SumSize   = 0;
        }
    }
    
}
