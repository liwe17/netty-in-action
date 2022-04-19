# ChannelHandler和ChannelPipeline

- ChannelHandler API和ChannelPipeline API
- 检测资源泄露
- 异常处理

## ChannelHandler家族

### Channel的生命周期

- Channel定义了一组和ChannelInboundHandler API密切相关的简单但功能强大的状态模型

<table>
  <tr>
    <td>状态</td>
    <td>描述</td>
  </tr>
  <tr>
    <td>ChannelUnregistered</td>
    <td>Channel已经被创建,但还未注册到EventLoop</td>
  </tr>
  <tr>
    <td>ChannelRegistered</td>
    <td>Channel已经被注册到EventLoop</td>
  </tr>
  <tr>
    <td>ChannelActive</td>
    <td>channel处于活动状态(已经连接到它的远程节点),它可以接收和发送数据了</td>
  </tr>
  <tr>
    <td>ChannelInactive</td>
    <td>channel没有连接到远程节点</td>
  </tr>
</table>

- 当状态发送改变,会生成对应的事件,这些事件将会被发送给ChannelPipeline中的ChannelHandler,其可以随后作出响应
    - ChannelRegistered -> ChannelActive -> ChannelInactive -> ChannelUnregistered

### ChannelHandler的生命周期

- ChannelHandler定义的生命周期操作
    - 在ChannelHandler被添加到ChannelPipeline中或者被从ChannelPipeline中移除时会调用这些操作.
    - 方法中每一个都接收一个ChannelHandlerContext参数

<table>
  <tr>
    <td>类型</td>
    <td>描述</td>
  </tr>
  <tr>
    <td>handlerAdded</td>
    <td>当ChannelHandler添加到ChannelPipeline时被调用</td>
  </tr>
  <tr>
    <td>handlerRemoved</td>
    <td>当从ChannelPipeline中移除ChannelHandler时被调用</td>
  </tr>
  <tr>
    <td>exceptionCaught</td>
    <td>当处理过程中在ChannelPipeline中有错误产生时被调用</td>
  </tr>
</table>

- Netty定义了下面两个重要的ChannelHandler子接口
    - ChannelInboundHandler-处理入站数据以及各种状态变化
    - ChannelOutboundHandler-处理出站数据并且允许拦截所有的操作

### ChannelInboundHandler接口

- ChannelInboundHandler的生命周期操作
    - 在数据被接收时或其对应的Channel状态发生变化时被调用,这些方法和Channel的生命周期密切相关

<table>
  <tr>
    <td>类型</td>
    <td>描述</td>
  </tr>
  <tr>
    <td>channelRegistered</td>
    <td>当Channel已经注册到它的EventLoop并且能够处理I/O时被调用</td>
  </tr>
  <tr>
    <td>ChannelUnregistered</td>
    <td>当Channel从它的EventLoop注销并且无法处理任何I/O时被调用</td>
  </tr>
  <tr>
    <td>channelActive</td>
    <td>当Channel处于活跃状态时被调用;Channel已经连接绑定并就绪</td>
  </tr>
  <tr>
    <td>channelInactive</td>
    <td>当Channel离开活动状态并且不再连接它的远程节点时被调用</td>
  </tr>
  <tr>
    <td>channelReadCompleted</td>
    <td>当Channel上的一个读操作完成时被调用</td>
  </tr>
  <tr>
    <td>channelRead</td>
    <td>当从Channel读取数据时被调用</td>
  </tr>
  <tr>
    <td>channelWritabilityChanged</td>
    <td>当Channel可写状态发生改变时调用</td>
  </tr>
  <tr>
    <td>userEventTriggered</td>
    <td>当ChannelInboundHandler.fireUserEventTriggered()方法被调用时被调用</td>
  </tr>
</table>

- 当某个ChannelInboundHandler的实现重写channelRead()方法时,它将负责显示地释放与池化的ByteBuf实例相关的内存,Netty提供了实用方法ReferenceCountUtil.release();

- 业务代码
    - com.weiliai.chapter6.SimpleDiscardHandler
    - com.weiliai.chapter6.DiscardHandler

### ChannelOutboundHandler接口

- 出站操作和数据将由ChannelOutboundHandler处理,它的方法被Channel,ChannelPipeline以及ChannelHandlerContext调用
- ChannelOutboundHandler的一个强大功能是可以按需推迟操作或事件,使得可以通过一些复杂的方法处理请求
    - 例如到远程节点的写入被暂停了,那么可以推迟冲刷操作并在稍后继续

<table>
  <tr>
    <td>类型</td>
    <td>描述</td>
  </tr>
  <tr>
    <td>bind(ChannelHandlerContext,SocketAddress,ChannelPromise)</td>
    <td>当请求将Channel绑定到本地地址时被调用</td>
  </tr>
  <tr>
    <td>connect(ChannelHandlerContext,SocketAddress,SocketAddress,ChannelPromise)</td>
    <td>当请求将Channel连接到远程节点时被调用</td>
  </tr>
  <tr>
    <td>disconnect(ChannelHandlerContext,ChannelPromise)</td>
    <td>当请求将Channel从远程节点断开时被调用</td>
  </tr>
  <tr>
    <td>close(ChannelHandlerContext,ChannelPromise)</td>
    <td>当请求关闭Channel时调用</td>
  </tr>
  <tr>
    <td>deregister(ChannelHandlerContext,ChannelPromise)</td>
    <td>当请求将Channel从它的EventLoop注销时调用</td>
  </tr>
  <tr>
    <td>read(ChannelHandlerContext)</td>
    <td>当请求从Channel读取更多数据时调用</td>
  </tr>
  <tr>
    <td>flush(ChannelHandlerContext)</td>
    <td>当请求通过Channel将入队数据冲刷到远程节点被调用</td>
  </tr>
  <tr>
    <td>write(ChannelHandlerContext,Object,ChannelPromise)</td>
    <td>当请求通过Channel将数据写入到远程节点被调用</td>
  </tr>
</table>

- ChannelPromise与ChannelFuture
    - ChannelOutboundHandler中的大部分方法都需要一个ChannelPromise参数,以便在操作完成时得到通知
    - ChannelPromise是ChannelFuture的一个子类,其定义了一些可写的方法,如setSuccess()和setFailure(),从而是ChannelFuture不可变

### ChannelHandler适配器

- 使用ChannelInboundHandlerAdapter和ChannelOutboundHandlerAdapter类作为自己的ChannelHandler的起始点
    - 两个适配器分别提供了ChannelInboundHandler和ChannelOutboundHandler的基本实现
    - 通过扩展抽象类ChannelHandlerAdapter,它们获得了它们共同的超接口ChannelHandler的方法
    - ChannelHandlerAdapter还提供了实用方法isSharable()
        - 如果其对应的实现被标注为Sharable,那么这个方法将返回true,表示它可以被添加到多个ChannelPipeline中
    - 在Adapter中所提供的方法体调用了其相关联的ChannelHandlerContext上的等效方法,从而将事件转发到了ChannelPipeline中的下一个ChannelHandler中

### 资源管理

- Netty提供了类ResourceLeakDetector,它将对你应用程序的缓冲区分配做大约1%的采样来检测内存泄露
- Netty泄露检测级别主要4种
    - DISABLE: 禁用泄露.只在进行过详尽的测试之后
    - SIMPLE: 使用1%的默认采样率检测并报告任何发现的泄露,默认级别,适合大部分情况
    - ADVANCED: 使用默认采样率,报告所发现的任何的泄露以及对应的消息被访问的位置
    - PARANOID: 类似ADVANCED,但是其会对每次(对消息)访问进行采样,对性能有很大影响,只在调试阶段使用

```shell
java -Dio.netty.leakDetectionLevel=ADVANCED
```

- 消费入站消息的简单方式
    - 由于消费入站数据是一项常规任务,所以netty提供了一个特殊的被称为SimpleChannelInboundHandler的ChannelInboundHandler实现
    - SimpleChannelInboundHandler会在消息被channelRead0()方法消费之后自动释放消息

- 在出站方向这边,如果处理write()操作并丢弃了一个消息,那么也应该负责释放它
    - 如果一个消息被消费或丢弃了,并且没有传递给ChannelPipeline中的下一个ChannelOutboundHandler,那么用户就有责任调用ReferenceCountUtil.release()
    - 如果消息到达了实际的传输层,那么当它被写入时或者Channel关闭时,都将被自动释放

- 业务代码
    - com.weiliai.chapter6.DiscardInboundHandler
    - com.weiliai.chapter6.DiscardOutboundHandler

## ChannelPipeline接口

- ChannelPipeline是一个拦截流经Channel的入站和出站事件的ChannelHandler实例链
    - ChannelHandler之间的交互是组成一个应用程序数据和事件处理逻辑的核心
    - 每一个新创建的Channel都将会分配一个新的ChannelPipeline,这项关联是永久性的
        - Channel既不能附加另外一个ChannelPipeline,也不能分离当前的
        - 在Netty生命周期中,这是一项固定的操作,不需要开发人员的任何干预

- 根据事件起源,将会被ChannelInboundHandler或ChannelOutboundHandler处理,随后调用ChannelHandlerContext实现,它将会被转发给同一超类的下一个ChannelHandler

- ChannelHandlerContext
    - ChannelHandlerContext使ChannelHandler能够和它的ChannelPipeline以及其他的ChannelHandler交互
    - ChannelHandlerContext可以通知其所属ChannelPipeline中的下一个ChannelHandler,甚至可以动态修改它所属的ChannelPipeline
    - ChannelHandlerContext具有丰富的用于处理事件和执行I/O操作的API

- ChannelPipeline相对论,Netty总是将ChannelPipeline的入站口作为头部,出站口作为尾部
- ChannelPipeline传播事件时,它会测试ChannelPipeline中的下一个ChannelHandler的类型是否和事件的运动方向相匹配,不匹配则会跳过,并将事件传递到下一个ChannelHandler

### 修改ChannelPipeline

- ChannelHandler可以通过添加,删除或替换其他的ChannelHandler来实时地修改ChannelPipeline的布局,也可以将自己从ChannelPipeline中移除,这也是最重要的能力之一

- ChannelPipeline上的相关方法,由ChannelHandler用来修改ChannelPipeline的布局

<table>
  <tr>
    <td>名称</td>
    <td>描述</td>
  </tr>
  <tr>
    <td>add*</td>
    <td>将一个ChannelHandler添加到ChannelPipeline中</td>
  </tr>
  <tr>
    <td>remove</td>
    <td>将一个ChannelHandler从ChannelPipeline中移除</td>
  </tr>
  <tr>
    <td>replace</td>
    <td>将ChannelPipeline中一个ChannelHandler替换为另一个ChannelHandler</td>
  </tr>
</table>

- 业务代码
    - com.weiliai.chapter6.ModifyChannelPipeline

- ChannelHandler的执行和阻塞
    - ChannelPipeline中的每一个ChannelHandler都是通过它的EventLoop(I/O线程)来处理传递给它的事件.所以不要阻塞这个线程,因为会对整体的I/O产生负面影响
    - 有时可能需要与那些使用阻塞API的遗留代码进行交互
        - ChannelPipeline有接受一个EventExecutorGroup的add()方法
        - 如果一个事件被传递给自定义的EventExecutorGroup,它将被包含在这个EventExecutorGroup中的某个EventExecutor所处理,从而被从该Channel本身的EventLoop中移除
        - 针对这种用例,Netty提供了一个叫DefaultEventExecutorGroup的默认实现

- ChannelPipeline的用于访问ChannelHandler的操作

<table>
  <tr>
    <td>名称</td>
    <td>描述</td>
  </tr>
  <tr>
    <td>get</td>
    <td>通过类型或者名称返回ChannelHandler</td>
  </tr>
  <tr>
    <td>context</td>
    <td>返回和ChannelHandler绑定的ChannelHandlerContext</td>
  </tr>
  <tr>
    <td>names</td>
    <td>返回ChannelPipeline中所有ChannelHandler的名称</td>
  </tr>
</table>

### 触发事件

- ChannelPipeline的API公开了用于调用入站和出站操作的附加方法

- ChannelPipeline的入站方法

<table>
  <tr>
    <td>方法名称</td>
    <td>描述</td>
  </tr>
  <tr>
    <td>fireChannelRegistered</td>
    <td>调用ChannelPipeline中下一个ChannelInboundHandler的channelRegistered(ChannelHandlerContext)方法</td>
  </tr>
  <tr>
    <td>fireChannelUnregistered</td>
    <td>调用ChannelPipeline中下一个ChannelInboundHandler的fireChannelUnregistered(ChannelHandlerContext)方法</td>
  </tr>
  <tr>
    <td>fireChannelActive</td>
    <td>调用ChannelPipeline中下一个ChannelInboundHandler的fireChannelActive(ChannelHandlerContext)方法</td>
  </tr>
  <tr>
    <td>fireChannelInactive</td>
    <td>调用ChannelPipeline中下一个ChannelInboundHandler的fireChannelInactive(ChannelHandlerContext)方法</td>
  </tr>
  <tr>
    <td>fireExceptionCaught</td>
    <td>调用ChannelPipeline中下一个ChannelInboundHandler的fireExceptionCaught(ChannelHandlerContext,Throwable)方法</td>
  </tr>
  <tr>
    <td>fireUserEventTriggered</td>
    <td>调用ChannelPipeline中下一个ChannelInboundHandler的fireUserEventTriggered(ChannelHandlerContext,Object)方法</td>
  </tr>
  <tr>
    <td>fireChannelRead</td>
    <td>调用ChannelPipeline中下一个ChannelInboundHandler的fireChannelRead(ChannelHandlerContext,Object)方法</td>
  </tr>
  <tr>
    <td>fireChannelReadComplete</td>
    <td>调用ChannelPipeline中下一个ChannelInboundHandler的fireChannelReadComplete(ChannelHandlerContext,Object)方法</td>
  </tr>
  <tr>
    <td>fireChannelWritabilityChanged</td>
    <td>调用ChannelPipeline中下一个ChannelInboundHandler的fireChannelWritabilityChanged(ChannelHandlerContext)方法</td>
  </tr>
</table>

- 出站,处理事件将会导致底层的套接字上发生一系列的动作

- ChannelPipeline的出站操作

<table>
  <tr>
    <td>bind</td>
    <td>将Channel绑定到一个本地地址,这将调用ChannelPipeline中的下一个ChannelOutboundHandler的bind(ChannelHandlerContext,SocketAddress,ChannelPromise)方法</td>
  </tr>
  <tr>
    <td>connect</td>
    <td>将Channel连接到远程地址,这将调用ChannelPipeline中的下一个ChannelOutboundHandler的connect(ChannelHandlerContext,SocketAddress,SocketAddress,ChannelPromise)方法</td>
  </tr>
  <tr>
    <td>disconnect</td>
    <td>将Channel断开连接,这将调用ChannelPipeline中的下一个ChannelOutboundHandler的disconnect(ChannelHandlerContext,ChannelPromise)方法</td>
  </tr>
  <tr>
    <td>close</td>
    <td>将Channel关闭,这将调用ChannelPipeline中的下一个ChannelOutboundHandler的close(ChannelHandlerContext,ChannelPromise)方法</td>
  </tr>
  <tr>
    <td>deregister</td>
    <td>将Channel从它的EventLoop注销,这将调用ChannelPipeline中的下一个ChannelOutboundHandler的deregister(ChannelHandlerContext,ChannelPromise)方法</td>
  </tr>
  <tr>
    <td>flush</td>
    <td>冲刷Channel所有挂起的写入,这将调用ChannelPipeline中的下一个ChannelOutboundHandler的flush(ChannelHandlerContext)方法</td>
  </tr>
  <tr>
    <td>write</td>
    <td>将消息写入Channel,这将调用ChannelPipeline中的下一个ChannelOutboundHandler的write(ChannelHandlerContext,Object,ChannelPromise)方法,注意:这并不会将消息写入底层Socket,只会放入队列,要写入Socket,需要调用flush或者writeAndFlush</td>
  </tr>
  <tr>
    <td>writeAndFlush</td>
    <td>这其实是一个先调用write在调用flush方法的便利方法</td>
  </tr>
  <tr>
    <td>read</td>
    <td>从Channel读取更多数据,这将调用ChannelPipeline中的下一个ChannelOutboundHandler的read(ChannelHandlerContext)方法</td>
  </tr>
</table>

- ChannelPipeline总结
    - ChannelPipeline保存了与Channel相关联的ChannelHandler
    - ChannelPipeline可以根据需要,添加或删除ChannelHandler来动态修改
    - ChannelPipeline有丰富的API用来被调用,以响应入站和出站事件

## ChannelHandlerContext接口

- ChannelHandlerContext接口
    - ChannelHandlerContext代表了ChannelHandler和ChannelPipeline之间的关联
    - 每当ChannelHandler添加到ChannelPipeline中时,都会创建ChannelHandlerContext

- ChannelHandlerContext主要功能是管理它所关联的ChannelHandler和同一个ChannelPipeline中的其他ChannelHandler之间的交互
    - ChannelHandlerContext有很多的方法,其中一些方法也存在于Channel和ChannelPipeline本身上
    - 如果调用Channel或者ChannelPipeline上的方法,它们将沿着整个ChannelPipeline进行传播
    - 调用位于ChannelHandlerContext上的相同方法,则将从当前所关联的ChannelHandler开始,并且只会传播给位于该ChannelPipeline中的下一个能够处理该事件的ChannelHandler

- ChannelHandlerContext的API(详见接口描述)

- 使用ChannelHandlerContext的API注意
    - ChannelHandlerContext和ChannelHandler之间的关联(绑定)是永远不会改变的,所以缓存对它的引用是安全的
    - ChannelHandlerContext的方法将产生更短的事件流,应该尽可能地利用这个特性来获得最大的性能

### 使用ChannelHandlerContext

- 如果你想有一些事件流全部通过ChannelPipeline,有两个方法可以做到
    - 调用Channel的方法
    - 调用ChannelPipeline的方法

- 如果想从ChannelPipeline的指定位置开始,不想流经整个ChannelPipeline
    - 为了节省开销,不感兴趣的ChannelHandler不让通过
    - 排除一些ChannelHandler

- 代码清单
    - com.weiliai.chapter6.WriteHandlers
    - com.weiliai.chapter6.WriteHandler

- ChannelHandler实例如果带有@Sharable注解则可以被添加到多个ChannelPipeline
    - 就是说单个ChannelHandler实例可以有多个ChannelHandlerContext,因此可以调用不同ChannelHandlerContext获取同一个ChannelHandler
    - 如果添加不带@Sharable注解的ChannelHandler实例到多个ChannelPipeline则会抛出异常
    - 使用@Sharable注解后的ChannelHandler必须在不同的线程和不同的通道上安全使用

- 为什么要共享ChannelHandler
    - 使用@Sharable注解共享一个ChannelHandler在一些需求中还是有很好的作用的
    - 使用一个ChannelHandler来统计连接数或来处理一些全局数据等

- 代码清单
    - com.weiliai.chapter6.UnsharableHandler
    - com.weiliai.chapter6.SharableHandler

## 异常处理

- 异常处理是任何真实应用程序的重要组成部分,Netty提供了几种方式用于处理入站或者出站处理过程中所抛出的异常

### 处理入站异常

- 如果在处理入站事件的过程中有异常被抛出,那么它将从它在ChannelInboundHandler里被触发的那一点开始流经ChannelPipeline.
    - 要想处理这种类型的入站异常,需要在ChannelInboundHandler实现中重写exceptionCaught的方法
    - 重写exceptionCaught方法的ChannelInboundHandler通常位于ChannelPipeline最后
    - 如何响应异常,可能很大程度上取决于我们的应用程序
        - 可能关闭Channel和连接也可能尝试进行恢复
        - 如果不实现任何处理入站的异常逻辑或者没有消费异常,那么Netty将会记录该异常为没有被处理的事实


- 简单的说
    - ChannelHandler.exceptionCaught()的默认实现是简单地将当前异常转发给ChannelPipeline中的下一个ChannelHandler
    - 如果异常到达了ChannelPipeline的尾端,它将会被记录为未被处理
    - 要想定义自定义的处理逻辑,需要重写exceptionCaught()方法,然后决定是否需要将该异常传播出去

- 业务代码
    - com.weiliai.chapter6.InboundExceptionHandler

### 处理出站异常

- 用于处理出站操作中的正常完成以及异常的选项,基于以下通知机制
    - 每个出站操作都将返回一个ChannelFuture
        - 注册到ChannelFuture的ChannelFutureListener将在操作完成时被通知该操作是成功还是出错
    - 几乎所有的ChannelOutboundHandler上的方法都会传入一个ChannelPromise的实例
        - ChannelPromise也可以被分配用于异步通知的监听器,但提供了立即通知的方法
            - ChannelPromise setSuccess()
            - ChannelPromise setFailure(Throwable cause)

- ChannelFutureListener添加只需要调用ChannelFuture实例上的addListener(ChannelFutureListener)方法,有两种方式可以做到
    - 最常用的方式是,调用出站操作(如write()方法)所返回的ChannelFuture上的addListener()方法
    - 将ChannelFutureListener添加到即将作为参数传递给ChannelOutboundHandler的方法的ChannelPromise

- 代码清单
    - com.weiliai.chapter6.OutboundExceptionHandler
    - com.weiliai.chapter6.ChannelFutures








































