package com.weiliai.chapter9;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

/**
 * <p>
 * 9.3 AbsIntegerEncoder
 *
 * @author LiWei
 * @since 2022/4/22
 */
public class AbsIntegerEncode extends MessageToMessageEncoder<ByteBuf> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> out) throws Exception {
        while (in.readableBytes() >= 4) {
            int value = Math.abs(in.readInt());
            out.add(value);
        }
    }
}
