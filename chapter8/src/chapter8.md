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