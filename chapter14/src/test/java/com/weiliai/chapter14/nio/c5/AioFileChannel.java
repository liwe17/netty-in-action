package com.weiliai.chapter14.nio.c5;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.TimeUnit;

import static com.weiliai.chapter14.nio.c2.ByteBufferUtil.debugAll;

public class AioFileChannel {

    public static void main(String[] args) throws IOException {
        try  {
            AsynchronousFileChannel channel = AsynchronousFileChannel.open(Paths.get("chapter14/data.txt"), StandardOpenOption.READ);
            // 参数1 ByteBuffer
            // 参数2 读取的位置
            // 参数3 附件
            // 参数4 回调对象
            ByteBuffer buffer = ByteBuffer.allocate(16);

            System.err.println("read begin...");
            channel.read(buffer, 0, buffer, new CompletionHandler<Integer, ByteBuffer>() {
                @Override
                public void completed(Integer result, ByteBuffer attachment) {
                    System.err.println("read completed...");
                    attachment.flip();
                    debugAll(attachment);
                    try {
                        TimeUnit.SECONDS.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void failed(Throwable exc, ByteBuffer attachment) {
                    exc.printStackTrace();
                }
            });
            System.err.println("read end...");
        } catch (IOException e) {
            //e.printStackTrace();
        }
        System.in.read();
    }
}
