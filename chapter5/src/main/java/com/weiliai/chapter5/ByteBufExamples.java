package com.weiliai.chapter5;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.ByteProcessor;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Random;

/**
 * chapter5 code
 *
 * @author LiWei
 * @date 2021/10/20
 */
public class ByteBufExamples {

    private static final Random RANDOM = new Random();

    private static final ByteBuf HEAP_BUF_FROM_SOMEWHERE = Unpooled.buffer(1024);

    private static final ByteBuf DIRECT_BUF_FROM_SOMEWHERE = Unpooled.directBuffer(1024);

    private static final Channel CHANNEL_FROM_SOMEWHERE = new NioSocketChannel();

    private static void handleArray(byte[] array, int offset, int len) {
    }

    // 5.1 Backing array
    public static void heapBuffer() {
        ByteBuf heapBuf = HEAP_BUF_FROM_SOMEWHERE;
        if (heapBuf.hasArray()) { //检查ByteBuf是否有一个支撑数组
            byte[] array = heapBuf.array(); //获取数组的引用
            int offset = heapBuf.arrayOffset() + heapBuf.readerIndex(); //计算第一个字节的偏移量
            int length = heapBuf.readableBytes(); //获取可读字节数
            handleArray(array, offset, length); // 使用数组,偏移量和长度作为参数调用自定义处理方法
        }
    }

    // 5.2 Direct buffer data access
    public static void directBuffer() {
        ByteBuf directBuf = DIRECT_BUF_FROM_SOMEWHERE;
        if (!directBuf.hasArray()) { //检查ByteBuf是否由数组支撑.如果不是,则这是一个直接缓冲区
            int length = directBuf.readableBytes(); //获取可读字节数
            byte[] array = new byte[length]; // 分配一个新的数组来保存具有该长度的字节数据
            directBuf.getBytes(directBuf.readerIndex(), array); // 将字节复制到该数组
            handleArray(array, 0, length); // 使用数组,偏移量和长度作为参数调用自定义处理方法
        }
    }

    // 5.3 Composite buffer pattern using ByteBuffer 使用ByteBuffer的复合缓冲区模式
    // 创建了一个包含两个ByteBuffer的数组用来保存这些消息组件,同时创建了第三个ByteBuffer用来保存所有这些数据的副本
    public static void byteBufferComposite(ByteBuffer header, ByteBuffer body) {
        // Use an array to hold the message parts
        ByteBuffer[] message = {header, body};
        // Create a new ByteBuffer and use copy to merge the header and body
        ByteBuffer message2 = ByteBuffer.allocate(header.remaining() + body.remaining());
        message2.put(header);
        message2.put(body);
        message2.flip();
    }

    // 5.4 Composite buffer pattern using CompositeByteBuf
    public static void byteBufComposite() {
        CompositeByteBuf messageBuf = Unpooled.compositeBuffer();
        ByteBuf headerBuf = HEAP_BUF_FROM_SOMEWHERE;
        ByteBuf bodyBuf = DIRECT_BUF_FROM_SOMEWHERE;
        messageBuf.addComponents(headerBuf, bodyBuf); // 将ByteBuf 实例追加到CompositeByteBuf
        // ...
        messageBuf.removeComponent(0); //  删除位于索引位置为0(第一个组件)的ByteBuf
        messageBuf.forEach(System.out::println); //循环遍历所有的ByteBuf 实例
    }

    // 5.5 Accessing the data in a CompositeByteBuf
    public static void byteBufCompositeArray() {
        CompositeByteBuf compBuf = Unpooled.compositeBuffer();
        int length = compBuf.readableBytes(); //获得可读字节数
        byte[] array = new byte[length]; // 分配一个具有可读字节数长度的新数组
        compBuf.getBytes(compBuf.readerIndex(), array); // 将字节读到该数组中
        handleArray(array, 0, array.length); // 使用偏移量和长度作为参数使用该数组
    }

    // 5.6 Access data
    public static void byteBufRelativeAccess() {
        ByteBuf buffer = HEAP_BUF_FROM_SOMEWHERE;
        for (int i = 0; i < buffer.capacity(); i++) {
            byte b = buffer.getByte(i);
            System.out.println((char)b);
        }
    }

    // 5.7 Read all data
    public static void readAllData() {
        ByteBuf buffer = HEAP_BUF_FROM_SOMEWHERE;
        while (buffer.isReadable()) {
            System.out.println(buffer.readByte());
        }
    }

    // 5.8 write data
    public static void write() {
        ByteBuf buffer = HEAP_BUF_FROM_SOMEWHERE;
        while (buffer.writableBytes() >= 4) {
            buffer.writeInt(RANDOM.nextInt());
        }
    }

    // 5.9 Using ByteProcessor to find \r
    public static void byteProcessor() {
        ByteBuf byteBuffer = HEAP_BUF_FROM_SOMEWHERE;
        byteBuffer.forEachByte(ByteProcessor.FIND_CR);
    }

    // 5.10 Slice a ByteBuf
    public static void byteBufSlice() {
        Charset utf8 = StandardCharsets.UTF_8;
        ByteBuf buf = Unpooled.copiedBuffer("Netty in Action rocks!", utf8);
        ByteBuf sliced = buf.slice(0, 15);
        System.out.println(sliced.toString(utf8));
        buf.setByte(0, (byte)'J');
        assert buf.getByte(0) == sliced.getByte(0);
    }

    // 5.11 Copying a ByteBuf
    public static void byteBufCopy() {
        Charset utf8 = StandardCharsets.UTF_8;
        ByteBuf buf = Unpooled.copiedBuffer("Netty in Action rocks!", utf8);
        ByteBuf copy = buf.copy(0, 15);
        System.out.println(copy.toString(utf8));
        buf.setByte(0, (byte)'J');
        assert buf.getByte(0) != copy.getByte(0);
    }

    // 5.12 get() and set() usage
    public static void byteBufSetGet() {
        Charset utf8 = StandardCharsets.UTF_8;
        ByteBuf buf = Unpooled.copiedBuffer("Netty in Action rocks!", utf8);
        System.out.println((char)buf.getByte(0));
        int readerIndex = buf.readerIndex();
        int writerIndex = buf.writerIndex();
        buf.setByte(0, (byte)'B');
        System.out.println((char)buf.getByte(0));
        assert readerIndex == buf.readerIndex();
        assert writerIndex == buf.writerIndex();
    }

    // 5.13 read() and write() operations on the ByteBuf
    public static void byteBufWriteRead() {
        Charset utf8 = StandardCharsets.UTF_8;
        ByteBuf buf = Unpooled.copiedBuffer("Netty in Action rocks!", utf8);
        System.out.println((char)buf.readByte());
        int readerIndex = buf.readerIndex();
        int writerIndex = buf.writerIndex();
        buf.writeByte((byte)'?');
        assert readerIndex == buf.readerIndex();
        assert writerIndex != buf.writerIndex();
    }

    // 5.14 Obtaining a ByteBufAllocator reference
    public static void obtainingByteBufAllocatorReference() {
        Channel channel = CHANNEL_FROM_SOMEWHERE;
        ByteBufAllocator alloc = channel.alloc();
        //...
        ChannelHandlerContext ctx = null; //AbstractChannelHandlerContext 实现类
        ctx.alloc();

    }

    // 5.15 Reference counting
    public static void referenceCounting() {
        Channel channel = CHANNEL_FROM_SOMEWHERE;
        ByteBufAllocator alloc = channel.alloc();
        //...
        ByteBuf byteBuf = alloc.directBuffer();
        assert byteBuf.refCnt() == 1;
    }

    // 5.16 Release reference-counted object
    public static void releaseReferenceCountedObject() {
        ByteBuf buffer = HEAP_BUF_FROM_SOMEWHERE; //get reference form somewhere
        boolean released = buffer.release();
        //...
    }

}
