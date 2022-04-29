package com.weiliai.chapter11;

import io.netty.channel.*;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.File;
import java.io.FileInputStream;

/**
 * <p>
 * 11.11 Transferring file contents with FileRegion
 *
 * @author LiWei
 * @since 2022/4/28
 */
public class FileRegionWriteHandler extends ChannelInboundHandlerAdapter {

    private static final Channel CHANNEL_FROM_SOMEWHERE = new NioSocketChannel();
    private static final File FILE_FROM_SOMEWHERE = new File("");

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        File file = FILE_FROM_SOMEWHERE; //get reference from somewhere
        Channel channel = CHANNEL_FROM_SOMEWHERE; //get reference from somewhere
        //...
        FileInputStream in = new FileInputStream(file);
        DefaultFileRegion region = new DefaultFileRegion(in.getChannel(), 0, file.length());

        channel.writeAndFlush(region)
            .addListener((ChannelFutureListener)future -> {
                if (!future.isSuccess()) {
                    Throwable cause = future.cause();
                    System.err.println(cause);
                }
            });
    }
}
