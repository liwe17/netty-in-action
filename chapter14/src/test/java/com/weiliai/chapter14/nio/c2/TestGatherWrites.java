package com.weiliai.chapter14.nio.c2;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;

public class TestGatherWrites {

    public static void main(String[] args) {

        ByteBuffer b1 = StandardCharsets.UTF_8.encode("hello");
        ByteBuffer b2 = StandardCharsets.UTF_8.encode("word");
        ByteBuffer b3 = StandardCharsets.UTF_8.encode("你好");

        try (RandomAccessFile file = new RandomAccessFile("chapter14/words2.txt", "rw");
             FileChannel channel = file.getChannel()) {

            long write = channel.write(new ByteBuffer[]{b1, b2, b3});
            System.err.println(write);

        } catch (IOException ignored) {

        }
    }

}
