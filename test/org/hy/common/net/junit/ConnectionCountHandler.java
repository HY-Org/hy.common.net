package org.hy.common.net.junit;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.hy.common.Date;
import org.hy.common.xml.log.Logger;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;





@Sharable
public class ConnectionCountHandler extends ChannelInboundHandlerAdapter implements Runnable
{
    private static final Logger $Logger = new Logger(ConnectionCountHandler.class);
    
    
    //jdk1.5 并发包中的用于计数的类
    private AtomicInteger nConnection = new AtomicInteger();

      public ConnectionCountHandler()
      {
          // 每两秒统计一下连接数
          Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(this , 0, 2, TimeUnit.SECONDS);
      }
      
     /**
       *  每次过来一个新连接就对连接数加一
       * @param ctx
       */
      @Override
      public void channelActive(ChannelHandlerContext ctx) {
          nConnection.incrementAndGet();
      }
     /**
       *  端口的时候减一
       * @param ctx
       */
      @Override
      public void channelInactive(ChannelHandlerContext ctx) {
          nConnection.decrementAndGet();
      }

      @Override
      public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
          
          try
          {
              super.exceptionCaught(ctx, cause);
              Channel channel = ctx.channel();
              if(channel.isActive()){
                  ctx.close();
              }
          }
          catch (Exception exce)
          {
              exce.printStackTrace();
          }
      }

    @Override
    public void run()
    {
        $Logger.info(Date.getNowTime().getFullMilli() + "\t同时在线连接数: " + nConnection.get());
    }
      
}
