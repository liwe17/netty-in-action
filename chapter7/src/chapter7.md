# EventLoop和线程模型

- 线程模型概述
- 事件循环的概念和实现
- 任务调度
- 实现细节

## 线程模型概述

- 基本的线程池化模式
    - 从池的空闲线程列表中选择一个Thread并且指派它去运行一个已提交的任务(一个Runnable的实现)
    - 当任务完成时,将该Thread返回给该列表,使其可被重用

- 虽然池化和重用线程相对于简单地为每个任务都创建和销毁线程是一种进步,但存在不可避免的问题,不能消除由上下文切换所带来的开销,其将随着线程数量的增加很快变得明显,并且在高负载下愈演愈烈

## EventLoop接口

- 运行任务来处理在连接的生命周期内发生的事件是任何网络框架的基本功能
- 与之相应的编程上的构造通常被称为事件循环——一个Netty使用了接口EventLoop来适配的术语

- Netty的EventLoop是协同设计的一部分,采用两个基本的API
    - 并发
        - io.netty.util.concurrent包构建在JDK的java.util.concurrent包上,用来提供线程执行器
    - 网络编程
        - io.netty.channel包中的累,为了与Channel的事件进行交互,扩展了这些接口/类

- 一个EventLoop将由一个永远不会改变的Thread驱动,同时任务(Runnable或Callable)可以直接提交给EventLoop实现,以立即执行或者调度执行
- 按照配置和可用核心的不同,可能会创建多个EventLoop实例用以优化资源的使用,并且单个EventLoop可能会被指派于服务多个Channel

- Netty的EventLoop在继承了ScheduledExecutorService的同时,只定义了一个方法parent(),用于返回当前EventLoop实现的实例所属的EventLoopGroup的引用

- 事件/任务的执行顺序:事件和任务总是以先进先出FIFO的顺序执行,以保证字节内容总是按正确的顺序被处理,消除潜在的数据损坏的可能性

```java
public interface EventLoop extends OrderedEventExecutor, EventLoopGroup {
    @Override
    EventLoopGroup parent();
}
```

### Netty 4中的I/O和事件处理

- 由I/O操作触发的事件将流经安装了一个或多个ChannelHandler的ChannelPipeline.传播这些事件的方法调用可以随后被ChannelHandler所拦截,并且可以按需处理事件
- Netty4中,所有I/O操作和事件都已经分配给了EventLoop的那个线程来处理

## 任务调度

- 假设需要调度一个任务以便稍后(延迟)执行或者周期性地执行

### JDK的任务调度API

- 虽然ScheduledExecutorServiceAPI是直截了当的,但是在高负载下它将带来性能上的负担
- 事实上作为线程池管理的一部分,将会有额外的线程创建.如果有大量任务被紧凑地调度,那么这将成为一个瓶颈

- 业务代码
    - com.weiliai.chapter7.ScheduleExamples.schedule

### 使用EventLoop调度任务

- Netty的EventLoop扩展了ScheduledExecutorService,所以它提供了使用JDK实现可用的所有方法,利用Netty的任务调度功能来获得性能上的提升

- 业务代码
    - com.weiliai.chapter7.ScheduleExamples.scheduleViaEventLoop
    - com.weiliai.chapter7.ScheduleExamples.scheduleFixedViaEventLoop
    - com.weiliai.chapter7.ScheduleExamples.cancelingTaskUsingScheduledFuture

## 实现细节

### 线程管理

- Netty线程模型的卓越性能取决于对于当前执行的Thread的身份的确定,也就是说,确定它是否是分配给当前Channel以及它的EventLoop的那一个线程(EventLoop将负责处理一个Channel的整个生命周期内的所有事件)
    - 如果当前调用线程正是支撑EventLoop的线程,那么所提交的代码块将会被直接执行
    - 否则,EventLoop将调度该任务以便稍后执行,并将它放入到内部队列中,当EventLoop下次处理它的事件时,它会执行队列中的那些任务/事件
    - 每个EventLoop都有它自已的任务队列,独立于任何其他的EventLoop

- 永远不要将一个长时间运行的任务放入到执行队列中,因为它将阻塞需要在同一线程上执行的任何其他任务

### EventLoop线程的分配

- 服务于Channel的I/O和事件的EventLoop包含在EventLoopGroup中.根据不同的传输实现,EventLoop的创建和分配方式也不同

#### 异步传输

- 异步传输实现只使用了少量的EventLoop(以及和它们相关联的Thread)
    - 在当前的线程模型中,它们可能会被多个Channel所共享.这使得可以通过尽可能少量的Thread来支撑大量的Channel,而不是每个Channel分配一个Thread

- 实现细节
    - 所有的EventLoop都由EventLoopGroup分配
    - 每个EventLoop将处理分配给它的所有Channel的所有事件和任务,每个EventLoop都和一个Thread相关联
    - EventLoopGroup将为每个新创建的Channel分配一个EventLoop.在每个Channel的整个生命周期内,所有的操作都将由相同的Thread执行
        - 当前实现中,使用顺序循环(round-robin)的方式进行分配以获取一个均衡的分布,并且相同的EventLoop可能会被分配给多个Channel(未来版本可能会变)
        - 一旦一个Channel被分配给一个EventLoop,它将在它的整个生命周期中都使用整个EventLoop(及相关联Thread)

- EventLoop的分配方式对ThreadLocal的使用的影响
    - 一个EventLoop通常会被用于支撑多个Channel,所以对于所有相关联的Channel来说,ThreadLocal都将是一样的

#### 阻塞传输

- 每一个Channel都将被分配给一个EventLoop(及相关联Thread)
    - 所有的EventLoop都由这个EventLoopGroup分配,每个新的Channel都将被分配一个新的EventLoop
    - 分配给Channel的EventLoop将用于执行它所有的事件和任务
    - 每个Channel的I/O事件都将只会被一个Thread(用于支撑该Channel的EventLoop的Thread)处理
























