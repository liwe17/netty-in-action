# Netty-异步和事件驱动

## 主要内容

- Java网络编程
    - com.weiliai.chapter1.BlockingIOExample
- Netty简介
- Netty的核心组件

## Netty的核心组件

- Channel
- 回调
- Future
- 事件和ChannelHandler

这些构建块代表了不同类型的构造:资源,逻辑以及通知.你的应用程序将使用它们来访问网络以及流经网络的数据.

### Channel

- Channel是Java NIO的一个基本构造.
- Channel代表一个到实体(例如一个文件,一个网络套接字或者一个硬件设备)的开放连接,如读操作和写操作.
- Channel可以被看作是传入或传出数据的载体,因此可以被打开/关闭,连接/断开.

### 回调

- 一个回调其实就是一个方法,一个指向已经被提供给另外一个方法的方法的引用,以便后者可以在适当的时候调用.
- netty内部使用回调来处理事件,当一个回调被触发时,相关的事件被一个interface-ChannelHandler的实现处理.
    - com.weiliai.chapter1.ConnectHandler

### Future

- Future提供了另一种在操作完成时通知应用程序的方式.可以看作是一个异步操作结果的占位符;它在将来某个时刻完成,并提供对其结果的访问.
- JDK预置了interface java.util.concurrent.Future,但其只允许手动检查对应操作是否已经完成,或者一直阻塞直到它完成,非常繁琐.
- netty提供了自己的实现ChannelFuture,用于在异步操作的时候使用,提供了额外的方法,这些方法使我们能够注册一个或多个ChannelFutureListener实例.
    - ChannelFutureListener监听器的回调方法operationComplete(),会在对应操作完成时被调用,并判断是否完成还是出错.
    - ChannelFutureListener提供的通知机制消除了手动检查对应的操作是否完成的必要.

### 事件和ChannelHandler

- Netty使用不同的事件来通知我们状态的改变或者是操作的状态,这使得我们能够基于已经发生的事件来触发适当的动作.
    - 入站事件
        - 连接已被激活或失活
        - 数据读取
        - 用户事件
        - 错误事件
    - 出站事件
        - 打开或者关闭到远程节点的连接
        - 将数据写入或者冲刷到套接字
- Netty的ChannelHandler为处理器提供了基本的抽象,可以认为每个ChannelHandler的实例都类似与一种为了响应特定事件而被执行的回调.

### 整体看

- Future,回调和ChannelHandler
    - Netty的异步编程模型是建立在Future和回调的概念之上的.
    - 将事件派发到ChannelHandler的方法则发生在更深的层次上.
    - 结合在一起,这些元素就提供了一个处理环境,使你的应用程序逻辑可以独立于任何网络操作相关的顾虑而独立地演变.

- 选择器,事件和EventLoop
    - Netty通过触发事件将Selector从应用程序中抽象出来,消除了所有本来将需要手动编写的派发代码,在内部,将会为每个Channel分配一个EventLoop,用以处理所有事件.
    - EventLoop本身只由一个线程驱动,其处理了一个Channel的所有I/O事件,并且在该EventLoop的整个生命周期内都不会改变.
