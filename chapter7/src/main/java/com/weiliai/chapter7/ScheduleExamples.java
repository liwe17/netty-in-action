package com.weiliai.chapter7;

import io.netty.channel.Channel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.ScheduledFuture;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 *
 * @author LiWei
 * @since 2022/4/19
 */
public class ScheduleExamples {

    private static final Channel CHANNEL_FROM_SOMEWHERE = new NioSocketChannel();

    // 7.2 Scheduling a task with a ScheduledExecutorService
    public static void schedule() {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(10);
        executor.schedule(() -> System.out.println("Now it is 60 seconds later"), 1, TimeUnit.MINUTES);
        executor.shutdown();
    }

    // 7.3 Scheduling a task with EventLoop
    public static void scheduleViaEventLoop() {
        Channel channel = CHANNEL_FROM_SOMEWHERE;
        channel.eventLoop()
            .schedule(() -> System.out.println("Now it is 60 seconds later"), 1, TimeUnit.MINUTES);
    }

    // 7.4 Scheduling a recurring task with EventLoop
    public static void scheduleFixedViaEventLoop() {
        Channel ch = CHANNEL_FROM_SOMEWHERE; // get reference from somewhere
        ch.eventLoop()
            .scheduleAtFixedRate(() -> System.out.println("Now it is 60 seconds later"), 1, 1, TimeUnit.MINUTES);
    }

    // 7.5 Canceling a task using ScheduledFuture
    public static void cancelingTaskUsingScheduledFuture() {
        Channel ch = CHANNEL_FROM_SOMEWHERE; // get reference from somewhere
        ScheduledFuture<?> future = ch.eventLoop()
            .scheduleAtFixedRate(() -> System.out.println("Now it is 60 seconds later"), 1, 1, TimeUnit.MINUTES);
        // Some other code that runs...
        future.cancel(false);
    }
}
