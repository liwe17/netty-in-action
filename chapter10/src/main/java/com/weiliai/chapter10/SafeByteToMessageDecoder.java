package com.weiliai.chapter10;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.TooLongFrameException;

import java.util.List;

/**
 * <p>
 * 10.4 TooLongFrameException
 *
 * @author LiWei
 * @since 2022/4/26
 */
public class SafeByteToMessageDecoder extends ByteToMessageDecoder {

    private static final int MAX_FRAME_SIZE = 1024;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int size = in.readableBytes();
        if (MAX_FRAME_SIZE < size) {
            in.skipBytes(size);
            throw new TooLongFrameException("Frame too big!");
        }
        // do something
    }
}
