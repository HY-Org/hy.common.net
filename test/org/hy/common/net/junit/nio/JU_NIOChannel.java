package org.hy.common.net.junit.nio;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import org.hy.common.Help;
import org.hy.common.StringHelp;
import org.hy.common.xml.log.Logger;
import org.junit.Test;





/**
 * 测试单元：测试NIO的Channel
 * 
 * @author      ZhengWei(HY)
 * @createDate  2021-08-31
 * @version     v1.0
 */
public class JU_NIOChannel
{
    
    private static final Logger $Logger = new Logger(JU_NIOChannel.class ,true);
    
    
    
    @Test
    public void test_FileChannel() throws IOException
    {
        String           v_FileName = Help.getSysTempPath() + StringHelp.getUUID() + ".txt";
        FileOutputStream v_FileOut  = new FileOutputStream(v_FileName);
        FileChannel      v_Channel  = v_FileOut.getChannel();
        ByteBuffer       v_Buffer   = ByteBuffer.allocate(1024);
        String           v_Datas    = "Hello World";
        
        v_Buffer.put(v_Datas.getBytes());
        v_Buffer.flip();
        
        v_Channel.write(v_Buffer);
        v_Channel.close();
        v_FileOut.close();
        
        $Logger.info("Create file is " + v_FileName);
        
        
        
        File            v_File      = new File(v_FileName);
        FileInputStream v_FileInput = new FileInputStream(v_File);
        
        v_Channel = v_FileInput.getChannel();
        v_Buffer  = ByteBuffer.allocate((int)v_File.length());
        
        v_Channel.read(v_Buffer);
        
        v_Buffer.flip();
        v_Datas = new String(v_Buffer.array());
        v_Channel.close();
        v_FileInput.close();
        
        $Logger.info("Open file content is " + v_Datas);
    }
    
    
    
    @Test
    public void copyFile() throws IOException
    {
        String v_FileA = "C:\\Users\\hyzhe\\Downloads\\002.Count And Move.mp4";
        String v_FileB = Help.getSysTempPath() + StringHelp.getUUID() + ".mp4";
        
        FileInputStream  v_FileAIn  = new FileInputStream (v_FileA);
        FileOutputStream v_FileBOut = new FileOutputStream(v_FileB);
        FileChannel      v_ChannelA = v_FileAIn .getChannel();
        FileChannel      v_ChannelB = v_FileBOut.getChannel();
        ByteBuffer       v_Buffer   = ByteBuffer.allocate(1024);
        
        while ( true )
        {
            int v_Read = v_ChannelA.read(v_Buffer);
            if ( v_Read == -1 )
            {
                break;
            }
            
            v_Buffer.flip();
            v_ChannelB.write(v_Buffer);
            v_Buffer.clear();  // 必须标记复位
        }
        
        v_ChannelA.close();
        v_ChannelB.close();
        v_FileAIn .close();
        v_FileBOut.close();
        
        $Logger.info("copy file finish " + v_FileB);
    }
    
    
    
    @Test
    public void copyFile_V2() throws IOException
    {
        String v_FileA = "C:\\Users\\hyzhe\\Downloads\\002.Count And Move.mp4";
        String v_FileB = Help.getSysTempPath() + StringHelp.getUUID() + ".mp4";
        
        FileInputStream  v_FileAIn  = new FileInputStream (v_FileA);
        FileOutputStream v_FileBOut = new FileOutputStream(v_FileB);
        FileChannel      v_ChannelA = v_FileAIn .getChannel();
        FileChannel      v_ChannelB = v_FileBOut.getChannel();

        v_ChannelB.transferFrom(v_ChannelA ,0 ,v_ChannelA.size());
        
        v_ChannelA.close();
        v_ChannelB.close();
        v_FileAIn .close();
        v_FileBOut.close();
        
        $Logger.info("copy file finish " + v_FileB);
    }
    
    
    
    @Test
    public void test_MappedByteBuffer() throws IOException
    {
        RandomAccessFile v_RandomAccessFile = new RandomAccessFile("1.txt" ,"rw");
        FileChannel      v_Channel          = v_RandomAccessFile.getChannel();
        
        /**
         * 参数1： 使用的读写模式
         * 参数2： 可以直接修改的起始位置
         * 参数3： 是映射到内存的大小。即可以直接在内存中修改的范围
         */
        MappedByteBuffer v_MappedBuffer = v_Channel.map(FileChannel.MapMode.READ_WRITE ,0 ,1024);
        
        // 第1个位置修改为9
        v_MappedBuffer.put(1 ,(byte)9);
        v_MappedBuffer.put(5 ,(byte)1);
        
        v_Channel.close();
        v_RandomAccessFile.close();
    }
    
}
