package com.weiliai.chapter14.netty.c2;

import io.netty.channel.DefaultEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

public class TestEventLoop {

    public static void main(String[] args) {
        NioEventLoopGroup group = new NioEventLoopGroup(2); // io任务,定时任务,普通任务
        DefaultEventLoopGroup group1 = new DefaultEventLoopGroup(); // 定时任务,普通任务

        System.out.println(group.next());
        System.out.println(group.next());
        System.out.println(group.next());
        System.out.println(group.next());

        // 普通任务
        group.execute(() -> System.out.printf("ThreadName = %s, hello word %n", Thread.currentThread().getName()));

        //定时任务
        group.scheduleAtFixedRate(() -> System.out.printf("[%s] %s, ok %n", LocalDateTime.now(), Thread.currentThread().getName()),
                0,
                1,
                TimeUnit.SECONDS);

        System.out.printf("ThreadName = %s, main %n", Thread.currentThread().getName());
    }

}
