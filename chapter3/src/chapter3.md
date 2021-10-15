# Netty的组件和设计

- Netty的技术和体系结构方面的内容
- Channel,EventLoop和ChannelFuture
- ChannelHandler和ChannelPipeline

## Channel,EventLoop和ChannelFuture

- Netty网络抽象的代表
    - Channel-Socket
    - EventLoop-控制流,多线程处理,并发
    - ChannelFuture-异步通知

### Channel接口

- 基本的IO操作(bind(),connect(),read()和write())依赖于底层网络传输的所提供的原语.
- 在基于Java网络编程中,基本构造是Class Socket,Netty的Channel接口所提供的API,大大降低了Socket类的复杂性
- Channel拥有许多预定义的,专门化实现的广泛类层次结构的根
    - EmbeddedChannel
    - LocalServerChannel
    - NioDatagramChannel
    - NioSctpChannel
    - NioSocketChannel

### EventLoop接口

- EventLoop定义了Netty的核心抽象,用于处理连接的生命周期中所产生的事件

- Channel,EventLoop,Thread以及EventLoopGroup之间的关系,创建Channel->将Channel注册到EventLoop->整个生命周期内部使用EventLoop处理IO事件
    - 一个EventLoopGroup包含一个或者多个EventLoop
    - 一个EventLoop在它的生命周期内只能和一个Thread绑定
    - 所有由EventLoop处理的IO事件都将在它专用的Thread上被处理
    - 一个Channel在它的生命周期内只注册一个EventLoop

这中设计,一个给定的Channel的IO操作都是有相同的Thread执行的,实际上消除了对于同步的需要

### ChannelFuture接口


