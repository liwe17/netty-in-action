package io.netty.channel;

import io.netty.util.concurrent.EventExecutor;

/**
 * <p>
 *
 * @author LiWei
 * @since 2022/4/19
 */
public class DummyChannelHandlerContext extends AbstractChannelHandlerContext {

    public static ChannelHandlerContext DUMMY_INSTANCE = new DummyChannelHandlerContext(null, null, null, null);

    public DummyChannelHandlerContext(DefaultChannelPipeline pipeline, EventExecutor executor, String name,
        Class<? extends ChannelHandler> handlerClass) {
        super(pipeline, executor, name, handlerClass);
    }

    @Override
    public ChannelHandler handler() {
        return null;
    }
}
