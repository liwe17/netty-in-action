# ByteBuf

- ByteBuf-Netty的数据容器
- API的详细信息
- 用例
- 内存分配


- ByteBuf 简介
    - 网络数据的传输单位总是字节,Java NIO提供了ByteBuffer作为它的字节容器,但是使用起来过于复杂,而且也很繁琐
    - Netty的ByteBuff替代品是ByteBuf,一个强大的实现,即解决了JDK API的局限性,又为网络应用程序的开发者提供了更好的API

## ByteBuf的API

- Netty的数据处理API通过两个组件暴露
    - abstract class ByteBuf
    - interface ByteBufHolder


- ByteBuf API的优点
    - 它可以被用户自定义的缓冲区类型扩展
    - 通过内置的复合缓冲区类型实现了透明的零拷贝
    - 容量可以按需增长(类似于JDK的StringBuilder)
    - 在读和写这两种模式之间切换不需要调用ByteBuffer的flip()方法
    - 读和写使用了不同的索引
    - 支持方法的链式调用
    - 支持引用计数
    - 支持池化

## ByteBuf类-Netty的数据容器

因为所有的网络通信都涉及字节序列的移动,所以高效易用的数据结构明显是必不可少的,Netty的ByteBuf实现满足并超越了这些需求.

### 它是如何工作的

- ByteBuf维护了两个不同的索引,一个由不同的索引分别控制读访问和写访问的字节数组
    - 一个用于读取,当从ByteBuf读取时,它的readerIndex将会被递增已经被读取的字节数
    - 一个用于写入,当写入ByteBuf时,它的writerIndex也会被递增
    - readerIndex和writerIndex的起始位置都是索引位置0

### ByteBuf的使用模式

### 堆缓冲区(Heap Buffer)

- 支撑数组(backing array)模式
    - 将数据存储在JVM的堆空间中
    - 可以在没有使用池化的情况下提供快速的分配和释放
    - 适合于有遗留的数据需要处理的情况

- 业务代码
    - com.weiliai.chapter5.ByteBufExamples.heapBuffer

### 直接缓冲区(Direct Buffer)

- 直接缓冲区模式
    - 直接缓冲区,在堆之外直接分配内存
    - 直接缓冲区不会占用堆空间容量,使用时需考虑到应用程序要使用的最大内存容量以及如何限制它
    - 在分配内存空间和释放内存时比堆缓冲区更复杂

- 业务代码
    - com.weiliai.chapter5.ByteBufExamples.directBuffer

### 复合缓冲区

- 复合缓冲区模式
    - 它为多个ByteBuf提供一个聚合视图
    - 通过ByteBuf的子类CompositeByteBuf实现这个模式,它提供了一个将多个缓冲区表示为单个合并缓冲区的虚拟表示
    - CompositeByteBuf中的ByteBuf实例可能同时包含直接内存分配和非直接内存分配
    - 如果其中只有一个实例,那么对CompositeByteBuf上的hasArray()方法的调用将返回该组件上的hasArray()方法的值,否则它将返回false

- 业务代码
    - com.weiliai.chapter5.ByteBufExamples.byteBufferComposite
    - com.weiliai.chapter5.ByteBufExamples.byteBufCompositeArray

## 字节级操作

ByteBuf提供了许多超出基本读,写操作的方法用于修改它的数据

### 随机访问索引

- ByteBuf于Java数组一样
    - 索引从0开始,第一个字节的索引是0,最后一个字节的索引总是capacity()-1
    - 注意通过索引访问时不会推进读索引和写索引,我们可以通过ByteBuf的readerIndex()或writerIndex()来分别推进读索引或写索引

- 业务代码
    - com.weiliai.chapter5.ByteBufExamples.byteBufRelativeAccess

### 顺序访问索引

- ByteBuf提供两个指针变量支付读和写操作,读操作是使用readerIndex(),写操作使用writeIndex().
    - ByteBuf一定符合: 0<=readIndex<=writeIndex<=capacity
- JDK种的ByteBuffer只有一个方法来设置索引,所以需要使用flip()方法来切换读和写模式

### 可丢弃字节

- ByteBuf.discardReadBytes()用来回收已经读取的字节
    - discardReadBytes()会丢弃从索引0到readerIndex之间的字节
    - 但可能涉及内存复制,因为它需要移动ByteBuf中可读的字节到开始位置,这样的操作很影响性能,只有需要马上释放内存的时候使用收益大

### 可读字节

- ByteBuf的可读字节分段存储了实际数据
    - 新分配,包装的或复制的缓冲区的默认readerIndex的值为0
    - 任何read或skip开头的操作都将检索或者跳过位于当前readerIndex的数据,并且将它增加已读字节数
    - 如果被调用的方法需要一个ByteBuf参数作为写入目标,并且没有指定目标参数,那么该目标缓冲区的writerIndex也增加
    - 若没有足够的可写字节会抛出IndexOutOfBoundException

- 业务代码
    - com.weiliai.chapter5.ByteBufExamples.readAllData

### 可写字节

- ByteBuf的可写字节分段是指拥有一个未定义内容的,写入就绪的内存区域
    - 新分配的writerIndex的默认值为0
    - 任何write开头的操作都将从当前writeIndex处开始写数据,并增加已经写入的字节数
    - 写操作的参数也是一个ByteBuf并且没有指定数据源索引,那么指定缓冲区的readerIndex也会一起增加
    - 若没有足够的可写字节会抛出IndexOutOfBoundException

- 业务代码
    - com.weiliai.chapter5.ByteBufExamples.write

### 索引管理

- JDK的InputStream定义了mark(int readLimit)和reset()方法,这些方法分别被用来将流中的当前位置标记为指定的值,以及将流重置到该位置
- ByteBuf
    - arkReaderIndex(),markWriterIndex(),resetWriterIndex()和resetReaderIndex()来标记和重置ByteBuf的readerIndex和writerIndex
    - 调用readerIndex(int)或者writerIndex(int)来将索引移动到指定位置
    - clear()方法来将readerIndex和writerIndex都设置为0,注:不会清楚内存的数据
        - clear()比discardReadBytes()轻量的多,只重置索引不会复制任何内存

### 查找操作

- 在ByteBuf中有多种可以用来确定指定值的索引的方法
    - 最简单的是使用indexOf()方法
    - 较复杂的查找通过那些需要一个ByteBufProcessor作为参数的方法达成
    - ByteBufProcessor针对一些常见的值定义了许多便利的方法

- 业务代码
    - com.weiliai.chapter5.ByteBufExamples.byteProcessor

### 派生缓冲区

- 派生缓冲区为ByteBuf提供了以专门的方式来呈现其内容的视图
    - duplicate()
    - slice()
    - slice(int,int)
    - Unpooled.unmodifiableBuffer(...)
    - order(ByteOrder)

- readSlice(int)
    - 每个这些方法都将返回一个新的ByteBuf实例.它具有自己的读索引,写索引和标记索引
    - 如果你修改了它的内容,也同时修改了其对应的源实例,所以要小心
- ByteBuf copy
    - 如果需要一个现有缓冲区的真实副本,请使用copy()或者copy(int,int)方法
    - 返回的ByteBuf拥有独立的数据副本

- 业务代码
    - com.weiliai.chapter5.ByteBufExamples.byteBufCopy
    - com.weiliai.chapter5.ByteBufExamples.byteBufSlice

### 读/写操作

- 两种类别读写
    - get()和set()操作,从给定索引开始,并且保持不变
    - read()和write()操作,从给定的索引开始,并且会根据已经访问过的字节数对索引进行调整

- 业务代码
    - com.weiliai.chapter5.ByteBufExamples.byteBufSetGet
    - com.weiliai.chapter5.ByteBufExamples.byteBufWriteRead

### 更多的操作

- ByteBuf提供的其他有用操作

## ByteBufHolder接口

- ByteBufHolder是一个接口,其实现类是DefaultByteBufHolder,还有一些实现了ByteBufHolder接口的其他接口类
- ByteBufHolder的作用就是帮助更方便的访问ByteBuf中的数据,当缓冲区没用了后,可以使用这个辅助类释放资源

### 按需分配:ByteBufAllocator接口

- Netty通过接口ByteBufAllocator实现了ByteBuf的池化,它可以用来分配我们锁描述过的任意类型的ByteBuf实例
    - 一个堆缓冲区可以使用ByteBufAllocator.heapBuffer()
    - 一个直接缓冲区可以使用ByteBufAllocator.directBuffer()
    - 一个复合缓冲区可以使用ByteBufAllocator.compositeBuffer()

- Netty提供了两种ByteBufAllocator的实现
    - PooledByteBufAllocator:池化ByteBuf的实例以提高性能并最大限度减少内存碎片(默认)
    - UnpooledByteBufAllocator:不池化ByteBuf实例,并且每次被调用都会返回一个新的实例

### Unpooled缓冲区

- Unpooled也是用来创建缓冲区的工具类,Unpooled的使用也很容易
    - 它提供了静态的辅助方法来创建未池化的ByteBuf实例

### ByteBufUtil类

- ByteBufUtil提供了用于操作ByteBuf的静态的辅助方法
    - ByteBufUtil提供了Unpooled之外的一些方法
    - 最有价值的是hexDump(ByteBuf buffer)方法,返回指定ByteBuf中可读字节的十六进制字符串,用于调试程序时打印ByteBuf的内容,相对于字节更友好
    - 另一个有用的方法是boolean equals(ByteBuf,ByteBuf),它被用来判断两个ByteBuf实例的相等性

### 引用计数

- 引用计数是一种通过在某个对象所持有的资源不再被其他对象引用时释放该对象所持有的资源来优化内存使用和性能的技术
    - Netty在第4版中为ByteBuf和ByteBufHolder引入了引用计数技术,它们都实现了接口 ReferenceCounted
    - 引用计数对于池化实现(如PooledByteBufAllocator)来说是至关重要的,它降低了内存分配的开销

- 业务代码
    - com.weiliai.chapter5.ByteBufExamples.referenceCounting
    - com.weiliai.chapter5.ByteBufExamples.releaseReferenceCountedObject

## 总结

- 本章专门探究了Netty的基于ByteBuf的数据容器
    - 使用不同的读索引和写索引来控制数据访问
    - 使用内存的不同方式——基于字节数组和直接缓冲区
    - 通过CompositeByteBuf生成多个ByteBuf的聚合视图
    - 数据访问方法——搜索,切片以及复制
    - 读,写,获取和设置API
    - ByteBufAllocator池化和引用计数







    