package com.weiliai.chapter8;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

/**
 * <p>
 * 8.1 Bootstrapping a client
 *
 * @author LiWei
 * @since 2022/4/20
 */
public class BootstrapClient {

    public void bootstrap() {
        NioEventLoopGroup group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap(); //创建一个Bootstrap类的实例以创建和连接新的客户端Channel
        bootstrap.group(group) //设置EventLoopGroup,提供用于处理Channel事件的EventLoop
            .channel(NioSocketChannel.class) //指定Channel实现
            .handler(new SimpleChannelInboundHandler<ByteBuf>() {
                @Override
                protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
                    System.out.println("Received data");
                }
            });
        ChannelFuture future = bootstrap.connect(new InetSocketAddress("www.baidu.com", 80)); //连接到远程主机
        future.addListener((ChannelFutureListener)future1 -> {
            if (future1.isSuccess()) {
                System.out.println("Connection established");
            } else {
                System.err.println("Connection attempt failed");
                future1.cause()
                    .printStackTrace();
            }
        });
    }

    public static void main(String[] args) {
        new BootstrapClient().bootstrap();
    }

}
