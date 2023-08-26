package com.weiliai.chapter14.netty.c3;

import io.netty.channel.EventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.DefaultPromise;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Slf4j
public class TestNettyPromise {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // 1. 准备EventLoop对象
        EventLoop eventLoop = new NioEventLoopGroup().next();

        // 2. 可以主动创建promise,结果容器
        DefaultPromise<Integer> promise = new DefaultPromise<>(eventLoop);

        // 3. 任意一个线程执行计算,计算完毕后向promise填充结果
        new Thread(() -> {
            System.out.println("start compute");
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            promise.setSuccess(80);
        }).start();

        // 4. 接收结果的线程
        log.debug("waiting...");
        log.debug("receive result = {}", promise.get());

    }
}
