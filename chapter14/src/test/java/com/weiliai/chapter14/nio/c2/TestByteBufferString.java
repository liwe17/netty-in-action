package com.weiliai.chapter14.nio.c2;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import static com.weiliai.chapter14.nio.c2.ByteBufferUtil.debugAll;

public class TestByteBufferString {

    public static void main(String[] args) {
        // 1. 字符串转byteBuffer
        ByteBuffer buffer = ByteBuffer.allocate(10);
        buffer.put("hello".getBytes());
        debugAll(buffer);

        // 2. Charset 自动切换到读模式
        ByteBuffer buffer2 = StandardCharsets.UTF_8.encode("hello");
        debugAll(buffer2);

        // 3. wrap 自动切换到读模式
        ByteBuffer buffer3 = ByteBuffer.wrap("hello".getBytes());
        debugAll(buffer3);

        System.out.println(StandardCharsets.UTF_8.decode(buffer2));

        // buffer.flip();
        System.out.println(StandardCharsets.UTF_8.decode(buffer));
    }
}
