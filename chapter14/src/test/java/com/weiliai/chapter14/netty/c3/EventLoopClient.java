package com.weiliai.chapter14.netty.c3;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;

import java.net.InetSocketAddress;
import java.time.LocalDateTime;

public class EventLoopClient {

    public static void main(String[] args) throws InterruptedException {

        ChannelFuture future = new Bootstrap()
                .group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new StringEncoder());
                    }
                })
                .connect(new InetSocketAddress(8080));

        // 方式1 使用sync同步处理结果
        future.sync();
//        Channel channel = future.channel();
//        channel.writeAndFlush("hello word");

        // 方式2 使用addListener回调方法异步处理结果
        future.addListener((ChannelFutureListener) future1 -> {
            Channel channel = future1.channel();
            channel.writeAndFlush("hello word");

            System.out.printf("%s [%s] %s %n",
                    LocalDateTime.now(),
                    Thread.currentThread().getName(),
                    channel);
        });


        System.out.println();

    }
}
