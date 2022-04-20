package com.weiliai.chapter8;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

/**
 * <p>
 * 8.4 Bootstrapping a server
 *
 * @author LiWei
 * @since 2022/4/20
 */
public class BootstrapServer {

    public void bootstrap() {
        NioEventLoopGroup group = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap(); // 创建ServerBootstrap
        bootstrap.group(group) //设置EventLoopGroup，其提供了用于处理Channel 事件的EventLoop
            .channel(NioServerSocketChannel.class)
            .childHandler(
                new SimpleChannelInboundHandler<ByteBuf>() { //设置用于处理已被接受的子Channel的I/O及数据的ChannelInboundHandler
                    @Override
                    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
                        System.out.println("Received data");
                    }
                });
        ChannelFuture future = bootstrap.bind(new InetSocketAddress(8080));
        future.addListener((ChannelFutureListener)channelFuture -> {
            if (channelFuture.isSuccess()) {
                System.out.println("Server bound");
            } else {
                System.err.println("Bind attempt failed");
                channelFuture.cause()
                    .printStackTrace();
            }
        });
    }

    public static void main(String[] args) {
        new BootstrapServer().bootstrap();
    }
}
