package com.weiliai.chapter14.nio.c4;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import static com.weiliai.chapter14.nio.c2.ByteBufferUtil.debugAll;

public class MultiThreadServer {

    public static void main(String[] args) throws IOException {

        Thread.currentThread().setName("boss");
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);

        Selector boss = Selector.open();
        SelectionKey bossKey = ssc.register(boss, 0, null);
        bossKey.interestOps(SelectionKey.OP_ACCEPT);
        ssc.bind(new InetSocketAddress(8080));
        int availableCount = Runtime.getRuntime().availableProcessors();
        Worker[] workers = new Worker[availableCount];
        for (int i = 0; i < availableCount; i++) {
            workers[i] = new Worker("worker-" + i);
        }
        AtomicInteger atoInt = new AtomicInteger();

        for (; ; ) {
            boss.select();
            Iterator<SelectionKey> iter = boss.selectedKeys().iterator();
            while (iter.hasNext()) {
                SelectionKey key = iter.next();
                iter.remove();
                if (key.isAcceptable()) {
                    SocketChannel sc = ssc.accept();
                    sc.configureBlocking(false);
                    workers[atoInt.getAndIncrement() % availableCount].register(sc);
                }
            }
        }
    }

    static class Worker implements Runnable {

        private Thread thread;

        private Selector selector;

        private final String name;

        private volatile boolean start;

        private final ConcurrentLinkedQueue<Runnable> queue = new ConcurrentLinkedQueue<>();

        public Worker(String name) {
            this.name = name;
        }

        public void register(SocketChannel sc) throws IOException {
            if (!start) {
                selector = Selector.open();
                thread = new Thread(this, name);
                thread.start();
                start = true;
            }
            queue.add(() -> {
                try {
                    sc.register(selector, SelectionKey.OP_READ, null);
                } catch (ClosedChannelException e) {
                    e.printStackTrace();
                }
            });
            selector.wakeup(); // 唤醒select方法
        }

        @Override
        public void run() {
            for (; ; ) {
                try {
                    selector.select();

                    Runnable task = queue.poll();
                    if (null != task) task.run();

                    Iterator<SelectionKey> iter = selector.selectedKeys().iterator();
                    while (iter.hasNext()) {
                        SelectionKey key = iter.next();
                        iter.remove();
                        if (key.isReadable()) {
                            ByteBuffer buffer = ByteBuffer.allocate(16);
                            SocketChannel channel = (SocketChannel) key.channel();
                            channel.read(buffer);
                            buffer.flip();
                            debugAll(buffer);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }

}
