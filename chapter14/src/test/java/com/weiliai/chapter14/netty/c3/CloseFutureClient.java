package com.weiliai.chapter14.netty.c3;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Scanner;

@Slf4j
public class CloseFutureClient {

    public static void main(String[] args) throws InterruptedException {
        NioEventLoopGroup group = new NioEventLoopGroup();

        Channel channel = new Bootstrap()
                .group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new LoggingHandler());
                        ch.pipeline().addLast(new StringEncoder());
                    }
                }).connect(new InetSocketAddress(8080))
                .sync()
                .channel();

        new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            for (; ; ) {
                String line = scanner.next();
                if ("q".equals(line)) {
                    channel.close();
                    break;
                }
                channel.writeAndFlush(line);
            }
        }, "input").start();

        ChannelFuture closeFuture = channel.closeFuture();
        // 方式一: 同步关闭
//        log.info("waiting ...");
//        closeFuture.sync();
//        log.info("after closeFuture operation...");

        // 方式二: 回调中关闭
        closeFuture.addListener((ChannelFutureListener) future1 -> {
            log.info("after closeFuture operation...");
            group.shutdownGracefully();
        });

    }

}
