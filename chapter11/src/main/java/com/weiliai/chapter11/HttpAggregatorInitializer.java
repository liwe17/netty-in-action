package com.weiliai.chapter11;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;

/**
 * <p>
 * 11.3 Automatically aggregating HTTP message fragments
 *
 * @author LiWei
 * @since 2022/4/27
 */
public class HttpAggregatorInitializer extends ChannelInitializer<Channel> {

    private final boolean isClient;

    public HttpAggregatorInitializer(boolean isClient) {
        this.isClient = isClient;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        if (isClient)
            pipeline.addLast("codec", new HttpClientCodec());
        else
            pipeline.addLast("codec", new HttpServerCodec());
        pipeline.addLast("aggregator",
            new HttpObjectAggregator(512 * 1024)); //将最大的消息大小为512kb的HttpObjectAggregator添加到ChannelPipeline
    }
}
