package com.weiliai.chapter7;

import java.util.Collections;
import java.util.List;

/**
 * <p>
 * 7.1 Executing tasks in an event loop
 *
 * @author LiWei
 * @since 2022/4/19
 */
@SuppressWarnings("all")
public class EventLoopExamples {

    public static void executeTaskInEventLoop() {
        boolean terminated = true;
        //...
        while (!terminated) {
            List<Runnable> readyEvents = blockUntilEventsReady();
            for (Runnable ev : readyEvents) {
                ev.run();
            }
        }
    }

    private static final List<Runnable> blockUntilEventsReady() {
        return Collections.singletonList(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

}
