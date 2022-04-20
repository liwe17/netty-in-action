# 引导

- 主要内容
    - 引导客户端和服务器
    - 从Channel内引导客户端
    - 添加ChannelHandler
    - 使用ChannelOption和属性

- 简单来说引导一个应用程序是指对它进行配置,并使它运行起来的过程

## 引导类

- 引导类的层次结构包括一个抽象的父类和两个具体的引导类
    - 一个抽象的父类:AbstractBootstrap
    - 两个具体的引导类:Bootstrap和ServerBootStrap

- 两种应用程序类型之间通用的引导步骤由AbstractBootstrap处理,而特定于客户端或者服务器的引导步骤则分别由Bootstrap或ServerBootstrap处理

- 具体的引导类用来支撑不同应用程序的功能
    - 服务器致力于使用一个父Channel来接受来自客户端的连接,并创建子Channel以用于它们之间的通信
    - 客户端将最可能只需要一个单独的,没有父Channel的Channel来用于所有的网络交互

- Netty的克隆操作只能浅拷贝引导的EventLoopGroup,也就是说EventLoopGroup在所有的克隆的Channel实例之间共享
    - 因为通常这些克隆的Channel的生命周期都很短暂,一个典型的场景是——创建一个Channel以进行一次HTTP请求

- AbstractBootstrap类的完整声明

```java
public abstract class AbstractBootstrap<B extends AbstractBootstrap<B, C>, C extends Channel> implements Cloneable {
    // B是其父类型的子类型
    @Override
    public abstract B clone();
}
```

- 子类的声明

```java
public class Bootstrap extends AbstractBootstrap<Bootstrap, Channel> {

}

public class ServerBootstrap extends AbstractBootstrap<ServerBootstrap, ServerChannel> {

}
```

## 引导客户端和无连接协议

- Bootstrap类被用于客户端或者使用了无连接协议的应用程序

- Bootstrap类的API

<table>
  <tr>
    <td>名称</td>
    <td>描述</td>
  </tr>
  <tr>
    <td>B group(EventLoopGroup)</td>
    <td>设置用于处理Channel所有事件的EventLoopGroup</td>
  </tr>
  <tr>
    <td>B channel(Class<? extends C>) | B channelFactory(ChannelFactory<? extends C>)</td>
    <td>channel()方法指定了Channel的实现类.如果该类没有默认的构造函数,可以通过调用channelFactory()方法指定也给工厂类,它将会被bind()方法调用</td>
  </tr>
  <tr>
    <td>B localAddress(SocketAddress)</td>
    <td>指定Channel应该绑定到的本地地址,如果没有指定,则由操作系统创建一个随机地址,也可以通过bind()或connect()方法指定localAddress</td>
  </tr>
  <tr>
    <td> &lt;T&gt; B option(ChannelOption &lt;T&gt, T) </td>
    <td>设置ChannelOption,其将被应用到每个新创建的Channel的ChannelConfig.这些选项将会通过bind()或者connect()方法设置到Channel,不管谁先调用.这个方法在Channel已经被创建后在调用将不会有效果.支持的ChannelOption类型取决于Channel的类型</td>
  </tr>
  <tr>
    <td>&lt;T&gt;  B attr(AttributeKey&lt;T&gt , T)</td>
    <td>指定新创建Channel的属性值,这些属性值将会通过bind()或者connect()方法设置到Channel,具体取决于谁先调用,这个方法在Channel已经被创建后在调用将不会有效果</td>
  </tr>
  <tr>
    <td>B handler(ChannelHandler)</td>
    <td>设置将被添加到ChannelPipeline以接收事件通知的ChannelHandler</td>
  </tr>
  <tr>
    <td>Bootstrap clone()</td>
    <td>创建一个当前Bootstrap的克隆,其具有和原始的Bootstrap相同的设置信息</td>
  </tr>
  <tr>
    <td>Bootstrap remoteAddress(SocketAddress</td>
    <td>设置远程地址,也可以通过connect()方法指定</td>
  </tr>
  <tr>
    <td>ChannelFuture connect()</td>
    <td>连接到远程节点并返回ChannelFuture,其将在连接操作完成的时候收到通知</td>
  </tr>
  <tr>
    <td>ChannelFuture bind()</td>
    <td>绑定Channel并返回一个ChannelFuture,其将在绑定操作完成的时候收到通知,在那之后必须调用Channel.connect()方法建立连接</td>
  </tr>
</table>

### 引导客户端

- Bootstrap类负责为客户端和使用无连接协议的应用程序创建Channel
    - Bootstrap类将会在bind()方法被调用后创建一个新的Channel,在这之后将会调用connect()方法以建立连接
    - 在connect()方法被调用后,Bootstrap类将会创建一个新的Channel


- 业务代码
    - com.weiliai.chapter8.BootstrapClient

### Channel和EventLoopGroup的兼容性

- 相互兼容的EventLoopGroup和Channel,必须保持这种兼容性,不能混用具有不同前缀的组件,否则将会导致IllegalStateException
    - channel
        - nio
            - NioEventLoopGroup
        - oio
            - OioEventLoopGroup
    - socket
        - nio
            - NioDatagramChannel
            - NioServerSocketChannel
            - NioSocketChannel
        - oio
            - OioDatagramChannel
            - OioServerSocketChannel
            - OioSocketChannel

- 业务代码
    - com.weiliai.chapter8.InvalidBootstrapClient

## 引导服务器

### ServerBootstrap类

- ServerBootstrap类的方法

<table>
    <tr>
        <td>名称</td>
        <td>描述</td>
    </tr>
    <tr>
        <td>group</td>
        <td>设置ServerBootstrap要用的EventLoopGroup.这个EventLoopGroup将用于ServerChannel和被接收的子Channel的I/O处理</td>
    </tr>
    <tr>
        <td>channel</td>
        <td>设置将要被实例化的ServerChannel</td>
    </tr>
    <tr>
        <td>channelFactory</td>
        <td>如果不能通过默认构造函数创建Channel,那么可以提供一个ChannelFactory</td>
    </tr>
    <tr>
        <td>localAddress</td>
        <td>指定ServerChannel应该绑定到的本地地址,如果没有指定,则由操作系统创建一个随机地址,也可以通过bind()指定localAddress</td>
    </tr>
    <tr>
        <td>option</td>
        <td>指定要应用到新创建ServerChannel的ChannelConfig的ChannelOption.这些选项将会通过bind()设置到Channel,在bind()调用之后,设置或改变ChannelOption都不会有效果.支持的ChannelOption类型取决于Channel的类型</td>
    </tr>
    <tr>
        <td>childOption</td>
        <td>指定子Channel被接受时,应用到子Channel的ChannelConfig的ChannelOption.支持的ChannelOption类型取决于Channel的类型</td>
    </tr>
    <tr>
        <td>attr</td>
        <td>指定ServerChannel上的属性,属性将会通过bind()方法设置给Channel.在调用bind()方法之后改变它们将不会有任何效果</td>
    </tr>
    <tr>
        <td>childAttr</td>
        <td>将属性设置给已经被接受的子Channel.接下来的调用将不会有任何的效果</td>
    </tr>
    <tr>
        <td>handler</td>
        <td>设置被添加到ServerChannel的ChannelPipeline中的ChannelHandler</td>
    </tr>
    <tr>
        <td>childHandler</td>
        <td>设置将被添加到已被接受的子Channel的ChannelPipeline中的ChannelHandler.handler和childHandler方法区别:handler添加的ChannelHandler由接受子Channel的ServerChannel处理,childHandler所添加的ChannelHandler将由已被接受的子Channel处理,其代表一个绑定到远程节点的套接字</td>
    </tr>
    <tr>
        <td>clone</td>
        <td>克隆一个设置和原始的ServerBootstrap相同的ServerBootstrap</td>
    </tr>
    <tr>
        <td>bind</td>
        <td>绑定ServerChannel并返回一个ChannelFuture,其将会在绑定操作完成后收到通知</td>
    </tr>
</table>

### 引导服务器

- ServerChannel的实现负责创建子Channel,这些子Channel代表已被接受的连接

- 服务器的引导过程
    - 当bind()方法被调用时,将会创建一个ServerChannel
    - 当连接被接受时,ServerChannel将会创建一个新的子Channel

- 业务代码
    - com.weiliai.chapter8.BootstrapServer

## 从Channel引导客户端

- 假设你的服务器正在处理一个客户端的请求,这个请求需要它充当第三方系统的客户端.
    - 当一个应用程序(如一个代理服务器)必须要和现有的系统(如web服务或数据库)集成,就可能发生这种情况
    - 这种情况,将需要从已经被接受的子Channel中引导一个客户端Channel

- 两种解决方案
    - 创建一个新的引导,效率低,涉及线程创建和上下文切换
    - 好的解决方案,实现EventLoop共享
        - 通过将已被接受的子Channel的EventLoop传递给Bootstrap的group()方法来共享该EventLoop
        - 由于分配给EventLoop的所有Channel都使用同一个线程,所以这避免了额外的线程创建以及上下文切换

- 两个Channel之间共享EventLoop
    - 在bind()方法被调用时,ServerBootstrap将创建一个新的ServerChannel
    - ServerChannel接受新的连接,并创建子Channel来处理它们
    - 为已被接受的连接创建子Channel
    - 由子Channel创建的Bootstrap类的实例将在connect()方法调用时创建新的Channel
    - EventLoop在由ServerChannel所创建子Channel以及由connect()方法所创建Channel之间共享

- 编写Netty应用程序的一个一般准则:尽可能地重用EventLoop,以减少线程创建所带来的开销

- 业务代码
    - com.weiliai.chapter8.BootstrapSharingEventLoopGroup

## 在引导过程中添加多个ChannelHandler

- 一个必须要支持多种协议的应用程序将会有很多的ChannelHandler,而不会是一个庞大而又笨重的类
    - 针对于这个用例,Netty提供了一个特殊的ChannelInboundHandlerAdapter子类

```java
public abstract class ChannelInitializer<C extends Channel> extends ChannelInboundHandlerAdapter {

    // 这个方法提供了一种将多个ChannelHandler添加到一个ChannelPipeline中的简便方法
    protected abstract void initChannel(C ch) throws Exception;
}
```

- 业务代码
    - com.weiliai.chapter8.BootstrapWithInitializer

## 使用Netty的ChannelOption和属性

- 在每个Channel创建时都手动配置它可能会变得相当乏味.幸运的是,你不必这样做.相反,你可以使用option()方法来将ChannelOption应用到引导.
    - 你所提供的值将会被自动应用到引导所创建的所有Channel
    - 可用的ChannelOption包括了底层连接的详细信息
        - keep-alive或者超时属性以及缓冲区设置

- Netty应用程序通常与组织的专有软件集成在一起,而像Channel这样的组件可能甚至会在正常的Netty生命周期之外被使用.在某些常用的属性和数据不可用时,Netty提供了如下两个工具
    - AttributeMap抽象(一个由Channel和引导类提供的集合)
    - AttributeKey<T>(一个用于插入和获取属性值的泛型类)

- 考虑一个用于跟踪用户和Channel之间的关系的服务器应用程序.这可以通过将用户的ID存储为Channel的一个属性来完成

- 业务代码
    - com.weiliai.chapter8.BootstrapClientWithOptionsAndAttrs

## 引导DatagramChannel

- 引导代码示例使用的都是基于TCP协议的SocketChannel,但是Bootstrap类也可以被用于无连接的协议
    - Netty提供了各种DatagramChannel的实现
    - 不再调用connect()方法,而是只调用bind()方法

- 业务代码
    - com.weiliai.chapter8.BootstrapDatagramChannel

## 关闭

- 引导使你的应用程序启动并且运行起来,但是迟早你都需要优雅地将它关闭

- 调用EventLoopGroup.shutdownGracefully()的作用
    - 关闭EventLoopGroup,它将处理任何挂起的事件和任务,并且随后释放所有活动的线程
    - 调用将会返回一个Future,这个Future将在关闭完成时接收到通知
- 也可以在调用EventLoopGroup.shutdownGracefully()方法之前,显式地在所有活动的Channel上调用Channel.close()

- 业务代码
    - com.weiliai.chapter8.GracefulShutdown

