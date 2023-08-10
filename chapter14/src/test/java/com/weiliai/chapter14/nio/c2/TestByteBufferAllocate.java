package com.weiliai.chapter14.nio.c2;

import java.nio.ByteBuffer;

public class TestByteBufferAllocate {

    public static void main(String[] args) {
        // class java.nio.HeapByteBuffer -java堆内存,读写效率较低,受到GC影响
        System.out.println(ByteBuffer.allocate(16).getClass());

        // class java.nio.DirectByteBuffer -直接内存,读写效率告(少一次拷贝),不会受GC影响,分配效率低
        System.out.println(ByteBuffer.allocateDirect(16).getClass());
    }

}
