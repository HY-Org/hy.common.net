package org.hy.common.net.junit.netty.protobuf2;

import java.util.Random;

import org.hy.common.xml.log.Logger;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;





/**
 * 业务的处理器：Handler
 * 
 * <String> 表示客户端与服务端相互通讯的数据封装类。
 * 
 * @author      ZhengWei(HY)
 * @createDate  2021-09-18
 * @version     v1.0
 */
public class ProtobufMoreObjectClientInboundHandler extends SimpleChannelInboundHandler<String>
{
    private static final Logger $Logger = new Logger(ProtobufMoreObjectClientInboundHandler.class ,true);
    
    

    @Override
    public void channelActive(ChannelHandlerContext i_Ctx) throws Exception
    {
        PeoplePOJO.Student.Builder v_StudentBuilder = PeoplePOJO.Student.newBuilder();
        v_StudentBuilder.setId(0);
        v_StudentBuilder.setName("郑同学");
        v_StudentBuilder.setClassName("四年级三班");
        
        PeoplePOJO.Teacher.Builder v_TeacherBuilder = PeoplePOJO.Teacher.newBuilder();
        v_TeacherBuilder.setId(1);
        v_TeacherBuilder.setName("张老师");
        v_TeacherBuilder.setSchoolName("铁小");
        
        PeoplePOJO.Worker.Builder v_WorkerBuilder = PeoplePOJO.Worker.newBuilder();
        v_WorkerBuilder.setId(2);
        v_WorkerBuilder.setName("郑家长");
        v_WorkerBuilder.setFactoryName("艾云");
        
        int v_Random = new Random().nextInt(3);
        if ( v_Random == 0 )
        {
            i_Ctx.channel().writeAndFlush(PeoplePOJO.People.newBuilder().setDataType(PeoplePOJO.People.DataType.StudentType).setStudent(v_StudentBuilder.build()).build());
        }
        else if ( v_Random == 1 )
        {
            i_Ctx.channel().writeAndFlush(PeoplePOJO.People.newBuilder().setDataType(PeoplePOJO.People.DataType.TeacherType).setTeacher(v_TeacherBuilder.build()).build());
        }
        else
        {
            i_Ctx.channel().writeAndFlush(PeoplePOJO.People.newBuilder().setDataType(PeoplePOJO.People.DataType.WorkerType) .setWorkder(v_WorkerBuilder .build()).build());
        }
        
        super.channelActive(i_Ctx);
    }
    
    

    @Override
    protected void channelRead0(ChannelHandlerContext i_Ctx ,String i_Msg) throws Exception
    {
        $Logger.info(i_Msg);
    }
    
}
