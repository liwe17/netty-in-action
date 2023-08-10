package com.weiliai.chapter14.nio.c2;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import static com.weiliai.chapter14.nio.c2.ByteBufferUtil.debugAll;

public class TestScatteringReads {

    public static void main(String[] args) {
        try (RandomAccessFile file = new RandomAccessFile("chapter14/words.txt", "rw");
             FileChannel channel = file.getChannel()) {

            ByteBuffer b1 = ByteBuffer.allocate(3);
            ByteBuffer b2 = ByteBuffer.allocate(3);
            ByteBuffer b3 = ByteBuffer.allocate(5);

            long read = channel.read(new ByteBuffer[]{b1, b2, b3});
            System.err.println(read);

            b1.flip();
            b2.flip();
            b3.flip();

            debugAll(b1);
            debugAll(b2);
            debugAll(b3);

        } catch (IOException ignored) {

        }
    }

}
