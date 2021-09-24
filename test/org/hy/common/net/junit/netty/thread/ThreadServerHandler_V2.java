package org.hy.common.net.junit.netty.thread;

import java.util.concurrent.Callable;

import org.hy.common.xml.log.Logger;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;





/**
 * 业务的处理器：Handler
 * 
 * <String> 表示客户端与服务端相互通讯的数据封装类。
 * 
 * @author      ZhengWei(HY)
 * @createDate  2021-09-24
 * @version     v1.0
 */
public class ThreadServerHandler_V2 extends SimpleChannelInboundHandler<String>
{

    private static final Logger             $Logger            = new Logger(ThreadServerHandler_V2.class ,true);
    
    // Handler共享型的线程池：创建业务线程池（16个线程）
    private static final EventExecutorGroup $ServiceThreadPool = new DefaultEventExecutorGroup(16);
    
    
    
    /**
     * 有读取数据时触发
     * 
     * 注意：费时操作请误在这里直接处理，而是使用Netty任务对列处理（提交到Channel对应的NioEventLoop的TaskQueue/ScheduleQueue中即可）
     * 
     * i_Ctx：是上下文对象，包含管道pipline、通道channel、地址
     * i_Msg：客户端发送的数据
     */
    @Override
    protected void channelRead0(ChannelHandlerContext i_Ctx ,String i_Msg) throws Exception
    {
        $Logger.info("业务处理的多线程执行(业务线程与Handler是二个不同的线程)：接收数据");
        
        $ServiceThreadPool.submit(new Callable<Object>()
        {
            @Override
            public Object call() throws Exception
            {
                $Logger.info("业务处理的多线程执行(业务线程与Handler是二个不同的线程)：开始");
                
                try
                {
                    Thread.sleep(5 * 1000);
                }
                catch (InterruptedException e)
                {
                    $Logger.error(e);
                }
                
                $Logger.info("业务处理的多线程执行(业务线程与Handler是二个不同的线程)：完成");
                i_Ctx.writeAndFlush("你好客户端：业务处理完成");
                return null;
            }
        });
        
        $Logger.info("业务处理的多线程执行(业务线程与Handler是二个不同的线程)：准备");
    }



    @Override
    public void exceptionCaught(ChannelHandlerContext i_Ctx ,Throwable i_Cause) throws Exception
    {
        $Logger.error(i_Cause);
        super.exceptionCaught(i_Ctx ,i_Cause);
        i_Ctx.close();
    }
    
}
