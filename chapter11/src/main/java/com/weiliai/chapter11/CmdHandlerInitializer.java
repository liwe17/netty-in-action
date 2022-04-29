package com.weiliai.chapter11;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.LineBasedFrameDecoder;

/**
 * <p>
 * 11.9 Using a ChannelInitializer as a decoder installer
 *
 * @author LiWei
 * @since 2022/4/27
 */
public class CmdHandlerInitializer extends ChannelInitializer<Channel> {

    private static final byte SPACE = ' ';

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ch.pipeline()
            .addLast(new CmdDecoder(64 * 1024))
            .addLast(new CmdHandler());
    }

    public static final class Cmd {

        public Cmd(ByteBuf name, ByteBuf args) {
            this.name = name;
            this.args = args;
        }

        private final ByteBuf name;

        private final ByteBuf args;

    }

    public static final class CmdHandler extends SimpleChannelInboundHandler<Cmd> {

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, Cmd msg) throws Exception {

        }
    }

    public static final class CmdDecoder extends LineBasedFrameDecoder {

        public CmdDecoder(int maxLength) {
            super(maxLength);
        }

        @Override
        protected Object decode(ChannelHandlerContext ctx, ByteBuf buffer) throws Exception {
            ByteBuf frame = (ByteBuf)super.decode(ctx, buffer);
            if (null == frame)
                return null;
            int index = frame.indexOf(frame.readerIndex(), frame.writerIndex(), SPACE);

            return new Cmd(frame.slice(frame.readerIndex(), index), frame.slice(index + 1, frame.writerIndex()));
        }
    }

}
