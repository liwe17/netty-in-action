package com.weiliai.chapter2.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

/**
 * EchoServerHandler
 *
 * @author LiWei
 * @date 2021/10/13
 */
@Sharable //标识一个ChannelHandler可以被多个Channel安全的共享
public class EchoServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf in = (ByteBuf) msg;
        System.out.printf("Server received %s", in.toString(CharsetUtil.UTF_8)); //打印消息记录到控制台
        ctx.write(in); //将接收到的消息写给发送者,而不冲刷出站消息
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER)
                .addListener(ChannelFutureListener.CLOSE); //将末端消息冲刷到远程节点,并且关闭该Channel

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace(); //打印异常栈跟踪
        ctx.close(); //关闭该Channel
    }
}
