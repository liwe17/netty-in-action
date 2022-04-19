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






