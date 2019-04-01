# java-interview

Senior Java engineer interview exams in 2019

高级Java工程师面试题2019

## 面试题目

### 面试时间2019-03

* 呆萝卜
    * mq集群怎么保证保数据的串行处理
    
    答案:mq本身的集群可以保证串行的
    或者镜像模式，更好
    
    * zookeeper有哪些角色?
    
    答案: 三种(leader, follower, observer)
    
    * zookeeper选举机制是怎么样的?

    * Redis集群方式有哪几种?
    
    答案: 1官方集群，2主从模式，3，用zookeeper保存redis实例的ip+端口;

<hr/>

* 杭州盈火网络科技有限公司

    年终奖2到3个月

    * Consul集群原理（同步元数据）
    
    * 多线程提高并发的手段
    
    除了mq. 线程池，还有什么办法？
    
    * 分布式系统有哪些常用的算法？(至少说出2种)
    
    答案: 一致性hash,Raft算法


* 杭州连连支付

    两个经理没有问太多细节，可能他们是主管，都不是写代码的。 只问了下项目宏观的问题。感觉并不是真的招人。


* 杭银消费金融

    * 全表扫描的坏处。除了这个查询本身速度慢，还会产生哪些别的坏处？


* 税友

    * Dubbo多个服务实例（比如3个），如果出错了。怎么知道是哪一个实例出的错误.也就是需要跟踪服务的调用过程。
    
    答案：面试官说他们采用的是goole的什么框架。我觉得这个不是答案，需要答出实现原理，才有意义。
    
    * 在读redis的并发量特别大的时候（比如达到10万qps），应该怎么做？
    答案：做读写分离。master-slave.

    * spring+mybatis注解方式为什么mybatis的dao接口不需要实现类?
    答案：用用了jdk的动态代理机制，比如InvocationHandler+Proxy
    
    * Nginx怎么做限流配置
    可以网上查答案，是常规配置
    

* 浙江大华

    * sql查询如何避免全表扫描
    
    * Feign负载均衡策略是什么？
    
    默认是轮询
    
    * mysql的master与keepalived怎么做HA方案


* 钱兔网络

    面试官没有问技术问题。都是写宏观的问题。


* 海康威视-仓储管理部

    *索引的分类有哪几种？
    
    innerdb只有两种索引：btree和fulltext)
    
    * sql语句的优化策略
    

* 传化智联

    * 索引的三种
    
    BTREE        (B+TREE结构，  适合于数字和varchar)
    FULLTEXT    （全文检索，适合text类型）
    HASH	    只存在于memory引擎和nda引擎）
    
    * sql语句的优化策略
    
    * 如何避免全表查询

    * 集群的时候如果处理"脑裂"现象？
    
    zk脑裂， mysql集群的脑裂

    * 面试被问到“classLoader双亲委托与类加载隔离”
    
    * spring+mybatis注解方式为什么mybatis的dao接口不需要实现类?
    
    答案：用用了jdk的动态代理机制，比如InvocationHandler+Proxy
    
    
* 挖财

    * redis跳表 skiplist
    
    * cms垃圾收集器
    
    * G1垃圾收集器
    
    * 如何编写一个spring-boot-starter组件, （类似于mybatis-spring-boot-starter）?
    
    答案: resources下面增加一个文件夹META-INF，里面增加一个文件spring.factories 
    然后在自己的组件包中定义一个用@Configuration来定义的类，还有Configure对应的bean
    想情见[https://github.com/liushaoming/jframe](https://github.com/liushaoming/jframe) 里
    的<code>jframe-spring-boot-starter</code>



## 更多Java面试题

部分题目的答案和实例代码在陆续整理中，你们也可以贡献你们的答案。

更多面试题，广大程序员们，欢迎你们star此项目。

欢迎你们提交Pull Request以提交你们的题目
