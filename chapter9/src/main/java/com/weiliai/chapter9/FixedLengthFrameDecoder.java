package com.weiliai.chapter9;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * <p>
 * 9.1 FixedLengthFrameDecoder
 * <p>
 * 扩展ByteToMessageDecoder 以处理入站字节,并将它们解码为消息
 *
 * @author LiWei
 * @since 2022/4/21
 */
public class FixedLengthFrameDecoder extends ByteToMessageDecoder {

    private final int frameLength; //指定要生成的帧的长度

    public FixedLengthFrameDecoder(int frameLength) {
        if (frameLength <= 0)
            throw new IllegalArgumentException("frameLength must be a positive integer: " + frameLength);

        this.frameLength = frameLength;
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> out) throws Exception {
        // 检查是否有足够的字节可以被读取,以生成下一个帧
        while (in.readableBytes() >= frameLength) {
            // 从ByteBuf 中读取一个新帧,将该帧添加到已被解码的消息列表中
            ByteBuf buf = in.readBytes(frameLength);
            out.add(buf);
        }
    }
}
