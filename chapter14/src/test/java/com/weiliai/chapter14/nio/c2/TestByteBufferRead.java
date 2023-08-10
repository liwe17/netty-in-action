package com.weiliai.chapter14.nio.c2;

import java.nio.ByteBuffer;

public class TestByteBufferRead {

    public static void main(String[] args) {
        ByteBuffer buffer = ByteBuffer.allocate(10);
        buffer.put(new byte[]{'a', 'b', 'c', 'd'});
        buffer.flip();

        // 从头开始读
//        buffer.get(new byte[4]);
//        debugAll(buffer);
//        buffer.rewind();
//        System.out.println((char) buffer.get());

        // mark&reset
        // mark做标记,记录position位置,reset是将position重置到mark位置
        System.out.println((char) buffer.get()); // 0->1
        System.out.println((char) buffer.get()); // 1->2
        buffer.mark(); // 标记为2
        System.out.println((char) buffer.get()); // 2->3
        System.out.println((char) buffer.get()); // 3->4
        buffer.reset(); // 重置为2
        System.out.println((char) buffer.get());
        System.out.println((char) buffer.get());

        // get(i)
        System.out.println(buffer.get(3));

    }

}
