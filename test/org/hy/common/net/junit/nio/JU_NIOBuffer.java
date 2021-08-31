package org.hy.common.net.junit.nio;

import java.nio.IntBuffer;

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
    
}
