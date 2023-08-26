package com.weiliai.chapter14.netty.c3;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

@Slf4j
public class TestJdkFuture {

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        // 1. 创建线程池
        ExecutorService executor = Executors.newFixedThreadPool(2);

        // 2. 提交任务
        Future<Integer> future = executor.submit(() -> {
            log.debug("execute compute");
            TimeUnit.SECONDS.sleep(1);
            return 50;
        });

        // 3. 主线程通过future来获取结果
        log.debug("wait for result");
        log.debug("result = {}", future.get());

    }

}
