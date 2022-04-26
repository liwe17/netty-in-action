package com.weiliai.chapter10;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * <p>
 *
 * @author LiWei
 * @since 2022/4/24
 */
public class ToIntegerDecoder extends ByteToMessageDecoder {

    //扩展 ByteToMessageDecoder类,以字节解码为特定格式
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> out) throws Exception {
        while (in.readableBytes() >= 4) { //检查是否至少有4字节可读(一个int的字节长度)
            out.add(in.readInt()); //从入站ByteBuf中读取一个int,并将其添加到解码消息的List中
        }
    }
}
