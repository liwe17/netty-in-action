package com.weiliai.chapter14.netty.c3;

import io.netty.channel.EventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.Future;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Slf4j
public class TestNettyFuture {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        NioEventLoopGroup group = new NioEventLoopGroup(2);
        EventLoop next = group.next();

        Future<Integer> future = next.submit(() -> {
            log.debug("execute compute");
            TimeUnit.SECONDS.sleep(1);
            return 70;
        });

        future.addListener(future1 -> log.debug("receive result = {}", future1.get()));

    }

}
