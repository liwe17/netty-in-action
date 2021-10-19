# 传输

- OIO-阻塞传输
- NIO-异步传输
- Local-JVM内部的异步通信
- Embedded-测试你的ChannelHandler

流经网络的数据总是具有相同的类型:字节.字节是如果流动的主要取决于我们所说的网络传输-一个帮助我们抽象底层数据传输机制的概念.

## 案例研究:传输迁移

一个简单的应用程序只简单地接收连接,向客户端写"Hi!",然后关闭连接

### 不通过Netty使用OIO和NIO

- OIO业务代码
    - com.weiliai.chapter4.PlainOioServer

- NIO业务代码
    - com.weiliai.chapter4.PlainNioServer

### 通过Netty使用OIO和NIO

- Netty OIO业务代码
    - com.weiliai.chapter4.NettyOioServer

- Netty NIO业务代码
    - com.weiliai.chapter4.NettyNioServer

## 传输API

- 传输API的核心是接口Channel,它用于所有的IO操作

```java
public interface Channel extends AttributeMap, ChannelOutboundInvoker, Comparable<Channel> {

    /**
     * Returns the configuration of this channel.
     */
    ChannelConfig config();

    /**
     * Return the assigned ChannelPipeline
     */
    ChannelPipeline pipeline();

}
```

- Channel的重要方法
    - eventLoop():返回分配给Channel的EventLoop.
    - pipeline():返回分配给Channel的ChannelPipeline.
    - isActive():返回Channel是否激活,已激活说明与远程连接对等
    - localAddress():返回已绑定的本地SocketAddress.
    - remoteAddress():返回已绑定的远程SocketAddress
    - write():写数据到远程客户端,数据通过ChannelPipeline传输过去,并且排队直到它被冲刷
    - flush():将之前已写的数据冲刷到底层传输,如一个Socket
    - writeAndFlush():一个简便的方法,等同于调用write()并接着调用flush()
- 每个Channel都会特定分配一个ChannelPipeline和ChannelConfig
    - ChannelConfig包含了该Channel的所有配置设置,并且支持热更新.由于特定的传输可能具有独特设置,所以它可能会实现一个ChannelConfig的子类型.
    - ChannelPipeline持有所有将应用于入站和出站数据以及事件的ChannelHandler实例,ChannelPipeline实现了拦截过滤器模式,帮助我们构建高度灵活的Netty程序.
- 由于Channel是独一无二的,为了保证顺序将其声明为java.lang.Comparable的一个子接口,如果不同的Channel实例返回相同的散列码,那么AbstractChannel中的compareTo()
  方法的实现将抛出一个Error

- ChannelHandler实现了应用程序用于处理状态变化以及数据处理的逻辑
    - 将数据从一种格式转换为另一种格式
    - 提供异常的通知
    - 提供Channel变为活动的或者非活动的通知
    - 提供当Channel注册到EventLoop或者从EventLoop注销时的通知
    - 提供有关用户自定义事件的通知

- 业务代码
    - com.weiliai.chapter4.ChannelOperationExamples

## 内置的传输

- Netty内置了一些开箱即用的传输
    - NIO: io.netty.channel.socket.nio,使用java.nio.channels包作为基础-基于选择器的方式
    - Epoll:io.netty.channel.epoll,JNI驱动的epoll()和非阻塞IO,支持只有在Linux上可用的多种特性,如SO_REUSEPORT,比NIO更快,完全非阻塞的
    - OIO:io.netty.channel.socket.oio,基于java.net的工具包,使用阻塞流
    - Local:io.netty.channel.local,用来在虚拟机之间本地通信
    - Embedded:io.netty.channel.embedded,嵌入传输,它允许在没有真正网络的运输中使用ChannelHandler,可以非常有用的来测试ChannelHandler的实现

### NIO-非阻塞IO

- NIO传输是目前最常用的方式,通过使用选择器提供了完全异步的方式操作所有的IO,NIO从Java 1.4才被提供.

- NIO中,我们可以注册一个通道或获得某个通道改变的状态,可能的状态改变
    - 新的Channel已被接受并且就绪
    - Channel连接已经完成
    - Channel有已经就绪的可供读取的数据
    - Channel可用于写数据

- 选择器运行在一个检查状态变化并对其做出相应响应的线程上,在应用程序对状态的改变做出响应之后,选择器将会被重置并重复这个过程
- 选择器所支持的操作在SelectionKey中定义
    - OP_ACCEPT:请求在接收新连接并创建Channel时获得通知
    - OP_CONNECT:请求在建立一个连接时获得通知
    - OP_READ:请求当数据已经就绪,可以从Channel中读取时获得通知
    - ON_WRITE:请求当可以向Channel中写更多的数据时获得通知

- 对于所有Netty的传输实现都共有的用户级别API完全地隐藏了这些NIO的内部细节,该处理流程如下
    - 新的Channel注册到选择器
    - 选择器处理状态变化的通知(之前已经注册的)
    - Selector.select()将会阻塞,直到接收到新的状态变化或者配置的超时时间已过时
    - 检查是否由状态变化,处理所有的状态变化

- 零拷贝
    - zero-file-copy是一种目前只有在使用NIO和Epoll传输时才可使用的特性
    - 可以快速高效地将数据从文件系统移动到网络接口,而不需要将其从内核空间复制到用户空间

### Epoll-用于Linux的本地非阻塞传输

- Netty的NIO传输基于Java提供的异步/非阻塞网络编程的通用抽象,但也包含相应的限制,因JDK在素有系统提供了相同的功能
- Linux作为高性能网络编程的平台,自Linux内核版本2.5.44(2002)引入epoll,提供了比旧的POSIX select和poll系统调用更好的性能.

- 业务代码
    - com.weiliai.chapter4.ChannelOperationExamples

### OIO-旧的阻塞IO

- Netty的OIO传输实现代表了一种折中,它可以通过常规的传输API使用,但是由于它是建立在java.net包的阻塞实现之上的,所以它不是异步的.但是它仍然非常适合于某些用途

### 用于JVM内部通信的Local传输

- Netty提供了一个Local传输,用于在同一个JVM中运行的客户端和服务器程序之间的异步通信
