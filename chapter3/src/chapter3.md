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

- Netty中所有的IO操作都是异步的,因为一个操作可能不会立即返回,所以我们需要一种用于在之后的某个时间点确定其结果的方法.
- Netty提供了ChannelFuture接口来完成此功能,其中addListener()方法注册一个ChannelFutureListener,以便于某个操作完成时(无论是否成功)得到通知.
- ChannelFuture看作是将来要执行的操作的结果占位符,它究竟什么时候被执行可能取决于若干因素,但是肯定的是它将会被执行.
- 所有属于同一个Channel的操作都保证其将以它们被调用的顺序被执行

## ChannelHandler和ChannelPipeline

### ChannelHandler接口

- ChannelHandler充当了所有处理入站和出站数据的应用程序逻辑的容器
- ChannelHandler是由网络事件触发的,但ChannelHandler可专门用于几乎任何类型的动作
    - 将数据格式转换为另一种格式
    - 处理转换过程中所抛出的异常等

- ChannelInboundHandler这种类型的ChannelHandler接收入站事件和数据,这些数据随后将被你的应用程序的业务逻辑所处理,当要给连接的客户端发送响应时,也可以从ChannelInboundHandler冲刷数据.

### ChannelPipeline接口

- ChannelPipeline为ChannelHandler链提供了容器,并定义了用于在该链上传播入站和出战事件流的API
- Channel被创建时,它会自动地分配到它专属的ChannelPipeline

- ChannelHandler安装到ChannelPipeline中的过程
    - 一个ChannelInitializer的实现被注册到ServerBootstrap中
    - 当ChannelInitializer.initChannel()方法被调用时,ChannelInitializer将在ChannelPipeline中安装一组自定义的ChannelHandler
    - ChannelInitializer将它自己从ChannelPipeline中移除

- ChannelPipeline和ChannelHandler之间的共生关系
    - ChannelHandler是专为支持广泛的用途而设计的,可以看作是处理往来Channel-Pipeline事件(包括数据)的任何代码的通用容器
    - ChannelHandler的两个子接口ChannelInboundHandler和ChannelOutboundHandler
    - 使事件流经ChannelPipeline是ChannelHandler的工作,它们是在应用程序的初始化或者引导阶段被安装的
    - 这些对象接收事件,执行它们所实现的处理逻辑,并将数据传递给链中的下一个ChannelHandler,执行顺序是由被添加的顺序决定的,ChannelPipeline是ChannelHandler的编排顺序
    - 当一个ChannelHandler添加到ChannelPipeline中时获得一个ChannelHandlerContext,其代表了ChannelHandler和ChannelPipeline之间的绑定

- Netty中有两种发送消息的方式
    - 直接写到Channel中,将会导致消息从ChannelPipeline的尾端开始流动
    - 写道和ChannelHandler相关联的ChannelHandlerContext对象中,将会导致消息从ChannelPipeline中的下一个ChannelHandler开始流动

### 更加深入了解ChannelHandler

- Netty以适配器类的形式提供了大量默认的ChannelHandler实现,旨在简化应用程序处理逻辑的开发过程
- ChannelPipeline中的每个ChannelHandler将负责把事件转发到链中的下一个ChannelHandler,这些适配器(及子类)将自动执行这个操作,所以只需要重写想要处理的特殊方法和事件

- 为什么要适配器类
    - 有一些适配器类可以将编写自定义的ChannelHandler所需要的努力降到最低限度,因为它们提供了定义在对应接口中的所有方法的默认实现.
    - 编写自定义ChannelHandler常用的适配器类
        - ChannelHandlerAdapter
        - ChannelInboundHandlerAdapter
        - ChannelOutboundHandlerAdapter
        - ChannelDuplexHandler

### 编码器和解码器

- 两种方向的转换原因是网络数据总是一系列的字节
    - 当通过Netty发送或者接收一个消息时,就会发生一次数据转换,入站消息会被解码,即从字节转换为另一种格式,通常为一个Java对象
    - 如果是出站消息,则会发生相反方向转换,将会从它当前的格式被编码成字节

- Netty为编码器和解码器提供了不同类型的抽象类,所有netty提供的编码/解码器类都实现了ChannelOutboundHandler或者ChannelInboundHandler接口

### 抽象类SimpleChannelInboundHandler

- 常见的情况是,你的应用程序会利用一个ChannelHandler来接收编码消息,并对该数据应用业务逻辑
    - 要创建一个这样的ChannelHandler,你只需要扩展基类SimpleChannelInboundHandler<T>,其中T是要处理消息的Java类型.
    - 在这个ChannelHandler中,需要重写基类的一个或者多个方法,并且获取到一个获取到一个ChannelHandlerContext的引用,这个引用将作为输入参数传递给ChannelHandler的所有方法
    - 在这种类型的ChannelHandler中,最重要的方法是channelRead0(ChannelHandlerContext,T),除了要求不阻塞当前IO线程之外,具体实现完全取决于业务

## 引导

- Netty的引导类为应用程序的网络层配置提供了容器
    - 涉及将一个进程绑定到某个指定端口(服务端)
    - 或者将一个进程连接到另一个运行在某个指定主机的指定端口上的进程(客户端)


- 服务器引导ServerBootstrap
    - ServerBootstrap将绑定一个端口,因为服务器必须要监听连接,
    - 需要两个EventLoopGroup(也可以是同一个实例),因为服务端需要两组不同的Channel
        - 第一组将只包含一个ServerChannel,代表服务器自身的已绑定到某个本地端口的正在监听的套接字
        - 第二组将包含所有已创建的用来处理传入客户端的连接(对于每个服务器已经接收的连接都有一个)的Channel
    - 与ServerChannel相关联的EventLoopGroup将分配一个负责为传入连接请求创建Channel的EventLoop,一旦被接受,第二个EventLoopGroup就会给它的Channel分配一个EventLoop


- 客户端引导Bootstrap
    - Bootstrap是由想要连接到远程节点的客户端应用程序所使用
    - 只需要一个EventLoopGroup