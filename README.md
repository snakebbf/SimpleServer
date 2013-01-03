SimpleServer
============

基于NIO的一个简单基础SimpleServer。
设计思想：
核心使用BlockingQueue封装读写事件。
Reader和Writer由线程运行。
Reader处理封装出Request交由Handler接口处理，用户自定义Handler实现类即可。
使用HandlerAdapter实现默认操作，用户只需实现关心的读写操作。
Reader和Writer将读写关闭等操作封装成对象交付给Selector的操作注册表，Selector轮询操作表依次处理，
统一由Selector处理所有事件相关的处理，提高响应处理效率。

