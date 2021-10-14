package com.weiliai.chapter1;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * ChannelHandler triggered by a callback
 *
 * @author LiWei
 * @date 2021/10/13
 */
public class ConnectHandler extends ChannelInboundHandlerAdapter {


    //当一个新的连接已经被建立时,channelActive(ChannelHandlerContext)将会被调用
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.printf("Client %s connected", ctx.channel().remoteAddress());
    }
}
