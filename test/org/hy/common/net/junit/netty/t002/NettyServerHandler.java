package org.hy.common.net.junit.netty.t002;

import java.util.concurrent.TimeUnit;

import org.hy.common.xml.log.Logger;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;





/**
 * 自定义一个处理器：Handler
 * 
 * @author      ZhengWei(HY)
 * @createDate  2021-09-09
 * @version     v1.0
 */
public class NettyServerHandler extends ChannelInboundHandlerAdapter
{
    private static final Logger $Logger = new Logger(NettyServerHandler.class ,true);
    
    

    /**
     * 有读取数据时触发
     * 
     * 注意：费时操作请误在这里直接处理，而是使用Netty任务对列处理（提交到Channel对应的NioEventLoop的TaskQueue/ScheduleQueue中即可）
     * 
     * i_Ctx：是上下文对象，包含管道pipline、通道channel、地址
     * i_Msg：客户端发送的数据
     */
    @Override
    public void channelRead(ChannelHandlerContext i_Ctx ,Object i_Msg) throws Exception
    {
        // 处理费时操作的方案1：自定义普通任务
        i_Ctx.channel().eventLoop().execute(new Runnable()
        {
            @Override
            public void run()
            {
                // 在这里处理
                try
                {
                    Thread.sleep(10 * 1000);
                    $Logger.info("任务一：执行完成");
                }
                catch (Exception e)
                {
                    $Logger.error(e);
                }
            }
        });
        
        // 再添加一个任务二
        i_Ctx.channel().eventLoop().execute(new Runnable()
        {
            @Override
            public void run()
            {
                // 在这里处理
                try
                {
                    Thread.sleep(20 * 1000);  // 注意：任务二是等30秒后，才执行的，需要等任务一执行完，才执行任务二的。
                    $Logger.info("任务二：执行完成");
                }
                catch (Exception e)
                {
                    $Logger.error(e);
                }
            }
        });
        
        
        
        // 处理费时操作的方案2：自定义定时任务
        i_Ctx.channel().eventLoop().schedule(new Runnable()
        {
            @Override
            public void run()
            {
                // 在这里处理
                try
                {
                    Thread.sleep(1 * 1000);
                    $Logger.info("任务三：执行完成");
                }
                catch (Exception e)
                {
                    $Logger.error(e);
                }
            }
        } ,10 ,TimeUnit.SECONDS); // 延时10秒后执行
        
        
        
        ByteBuf v_Buffer = (ByteBuf) i_Msg;
        $Logger.info(Thread.currentThread().getName() + "：接收" + i_Ctx.channel().remoteAddress() + "发送的数据：" + v_Buffer.toString(CharsetUtil.UTF_8));
    }



    /**
     * 读取数据完毕。可以回复客户端了
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext i_Ctx) throws Exception
    {
        // 将数据写入到缓存，并刷新
        i_Ctx.writeAndFlush(Unpooled.copiedBuffer("你好，客户端" ,CharsetUtil.UTF_8));
    }



    /**
     * 异常处理，一般是需要关闭通道的
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext i_Ctx ,Throwable i_Cause) throws Exception
    {
        $Logger.error(i_Cause);
        i_Ctx.close();
    }
    
}
