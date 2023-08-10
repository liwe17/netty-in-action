package com.weiliai.chapter14.nio.c2;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class TestByteBuffer {

    public static void main(String[] args) {
        // FileChannel
        // 1.输入流 2.RandomAccessFile

        try (FileInputStream fileStream = new FileInputStream("chapter14/data.txt");
             FileChannel channel = fileStream.getChannel()) {
            // 准备缓冲区
            ByteBuffer buffer = ByteBuffer.allocate(10);
            for (; ; ) {
                // 从channel读取数据,向buffer写入
                int read = channel.read(buffer);
                if (-1 == read) {
                    break;
                }
                // 打印buffer内容
                buffer.flip(); // 切换至读模式
                while (buffer.hasRemaining()) { //是否还有剩余未读数据
                    byte b = buffer.get();
                    System.err.println((char) b);
                }
                buffer.clear(); // 切换到写模式
            }
        } catch (IOException ignored) {

        }

    }

}
