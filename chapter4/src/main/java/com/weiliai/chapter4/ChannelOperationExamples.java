package com.weiliai.chapter4;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Writing to a Channel
 *
 * @author LiWei
 * @date 2021/10/19
 */
public class ChannelOperationExamples {

    private static final Channel CHANNEL_FROM_SOMEWHERE = new NioSocketChannel();

    // writing to channel
    public static void writingToChannel() {
        ByteBuf buf = Unpooled.copiedBuffer("your data".getBytes(StandardCharsets.UTF_8));
        ChannelFuture cf = CHANNEL_FROM_SOMEWHERE.writeAndFlush(buf);
        cf.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    System.out.println("Write successful");
                } else {
                    System.err.println("Write error");
                    future.cause().printStackTrace();
                }
            }
        });
    }

    //Using a Channel from many threads
    public static void writingToChannelFromManyThreads() {
        ByteBuf buf = Unpooled.copiedBuffer("your data".getBytes(StandardCharsets.UTF_8)).retain();
        Runnable writer = new Runnable() {//创建将数据写到Channel 的Runnable
            @Override
            public void run() {
                CHANNEL_FROM_SOMEWHERE.writeAndFlush(buf.duplicate());
            }
        };

        ExecutorService executor = Executors.newCachedThreadPool();
        for (int i = 0; i < 2; i++) {
            executor.execute(writer); //递交写任务给线程池以便在某个线程中执行
        }
    }

}
