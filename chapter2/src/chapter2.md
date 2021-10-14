# 我的第一款Netty应用程序

## 主要内容

- 设置开发环境
- 编写Echo服务器和客户端
- 构建并测试应用程序

## 编写Echo服务器

- 至少一个ChannelHandler-该组件实现了服务器对从客户端接收的数据的处理,即它的业务逻辑
- 引导-配置服务器的启动代码.至少它会将服务器绑定到它要监听连接请求的端口

### ChannelHandler和业务逻辑

- ChannelHandler是一个接口,它的实现负责接收并响应事件通知.在Netty应用程序中,所有的数据处理逻辑都包含在这些核心抽象实现中
    - ChannelInboundHandler实现这个接口
    - ChannelInboundHandlerAdapter继承这个类,提供ChannelInboundHandler默认实现

- Echo服务器用到的方法
    - channelRead()-对于每个传入的消息都要调用.
    - channelReadComplete()—通知ChannelInboundHandler最后一次对channelRead()的调用是当前批量读取中的最后一条消息.
    - exceptionCaught()——在读取操作期间,有异常抛出时会调用.

- 业务代码
    - com.weiliai.chapter2.server.EchoServerHandler

- 关键点
    - 针对不同类型的事件来调用ChannelHandler.
    - 应用程序通过实现或者扩展ChannelHandler来挂钩到事件的生命周期,并且提供自定义的应用程序逻辑.
    - 在架构上,ChannelHandler有助于保持业务逻辑与网络处理代码的分离.这简化了开发过程,因为代码必须不断地演化以响应不断变化的需求

### 引导服务器

- 引导服务器本身的过程
    - 绑定到服务器将在其上监听并接受传入连接请求的端口
    - 配置Channel,将有关的入站消息通知给EchoServerHandler实例

- 业务代码
    - com.weiliai.chapter2.server.EchoServer

- 关键点
    - EchoServerHandler实现了业务逻辑
    - main()方法引导服务器过程
        - 创建一个实例ServerBootstrap的实例以引导和绑定服务器
        - 创建并分配一个NioEventLoopGroup实例以进行事件的处理,如连接以及读写数据
        - 指定服务器绑定的本地的InetSocketAddress
        - 使用一个EchoServerHandler的实例化每一个新的Channel
        - 调用ServerBootstrap.bind()方法绑定服务器

## 编写Echo客户端

- Echo客户端
    - 连接到服务器
    - 发送一个或者多个消息
    - 对于每个消息,等待并接收从服务器发回的相同的消息
    - 关闭连接

### 通过ChannelHandler实现客户端逻辑

- 客户端将拥有一个用户处理数据的ChannelInboundHandler,在这个场景下,将扩展SimpleChannelInboundHandler类来处理所有必须任务
- 要求重写的方法
    - channelActive()-在到服务器的连接已经并建立之后被调用
    - channelRead0()-当从服务器接收到一条消息时被调用
    - exceptionCaught()-在处理过程中发生异常时被调用

- 业务代码
    - com.weiliai.chapter2.client.EchoClientHandler

- SimpleChannelInboundHandler与ChannelInboundHandler
    - 客户端使用SimpleChannelInboundHandler而服务端使用ChannelInboundHandler与两个因素相关
        - 业务逻辑如果处理消息
        - Netty如何管理资源
    - 在客户端,当channelRead0()方法完成时,你已经有了传入消息,并且已经处理完它了,当该方法返回时,SimpleChannelInboundHandler负责释放指向保存该消息的ByteBuf的内存引用
    - 在EchoServerHandler中,你仍然需要将传入消息会送给发送者,而write()操作是异步的,直到channelRead()
      方法返回后可能仍然没有完成,因此EchoServerHandler扩展了ChannelInboundHandlerAdapter,其在这个时间的不会释放消息,消息在EchoServerHandler的channelReadComplete()
      方法中,当writeAndFlush()方法被调用时被释放.

### 引导客户端

- 引导客户端类似于引导服务器
    - 客户端是使用主机和端口参数来连接远程地址,即Echo服务器的地址.
    - 服务器是绑定到一个一直被监听的端口

- 业务代码
    - com.weiliai.chapter2.client.EchoClient

- 关键点
    - 为初始化客户端,创建一个Bootstrap实例
    - 为进行事件处理分配了一个NioEventLoopGroup实例,其中事件处理包括创建新的连接以及处理入站和出站数据
    - 为服务器连接创建了一个InetSocketAddress实例
    - 当连接被建立时,一个EchoClientHandler实例会被安装到该Channel的ChannelPipeline中
    - 在一切都设置完成后,调用Bootstrap.connect()方法连接到远程节点

- 程序功能说明
    - 一旦客户端建立连接,它就发送它的消息—Netty rocks!
    - 服务器报告接收到的消息,并将其回送给客户端
    - 客户端报告返回的消息推出