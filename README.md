# java-interview

Senior Java engineer interview exams in 2019

高级Java工程师面试题2019

## 面试题目

### 面试题来自本人的程序员朋友面试经历，经本人和开源爱好者汇总

* 呆萝卜

    * [1万并发量的秒杀系统怎么实现](readme/seckill-architect.md)
    
    * [mq集群怎么保证保数据的串行处理](readme/mq-sequence-handle.md)
    
    * [zookeeper有哪些角色](readme/zk-role.md)
    
    * [zookeeper选举机制](https://www.linuxidc.com/Linux/2018-09/154117.htm)

    * [Redis集群方式有哪几种](readme/redis-cluster-strategies.md)
    
    * [如何排查Java的CPU性能问题-CPU100%占用](readme/cpu100percent.md)


<hr/>

* 杭州盈火网络科技有限公司

    * [Consul集群原理](readme/consul-cluster-principle.md)
    
    * [多线程环境提高并发性能的手段](readme/improve-concurrency.md)
    
    * [JVM垃圾收集器算法](readme/gc.md)
    
    * 分布式系统有哪些常用的算法？(至少说出2种)
    
    答案: 一致性hash,Raft算法

<hr/>

* 杭州连连支付

    两个经理没有问太多细节，可能他们是主管，都不是写代码的。 只问了下项目宏观的问题。感觉并不是真的招人。

<hr/>

* 杭银消费金融

    * [SQL全表扫描有什么坏处，什么情况会导致全表扫描](readme/full-table-scan.md)

<hr/>

* 税友

    * [Dubbo服务间调用（A -> B -> C, A，B和C都是多实例集群）,如何跟踪调用过程,并确定调用的是哪个实例](readme/call-chain.md)
        
    * 在读redis的并发量特别大的时候（比如达到10万qps），应该怎么做？
    答案：做读写分离。master-slave.

    * spring+mybatis注解方式为什么mybatis的dao接口不需要实现类?
    答案：用用了jdk的动态代理机制，比如InvocationHandler+Proxy
    
    * Nginx怎么做限流配置
    可以网上查答案，是常规配置
    
<hr/>

* 浙江大华

    * sql查询如何避免全表扫描
    
    * Feign负载均衡策略是什么？
    
    默认是轮询
    
    * mysql的master与keepalived怎么做HA方案

<hr/>

* 钱兔网络

    面试官没有问技术问题。都是写宏观的问题。

<hr/>

* 海康威视-仓储管理部

    * 索引的分类有哪几种？
    
    innerdb只有两种索引：btree和fulltext)
    
    * sql语句的优化策略
    
<hr/>

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
    
    
 <hr/>
    
* 挖财

    * redis跳表 skiplist
    
    * cms垃圾收集器
    
    * G1垃圾收集器
    
    * 如何编写一个spring-boot-starter组件, （类似于mybatis-spring-boot-starter）?
    
    答案: resources下面增加一个文件夹META-INF，里面增加一个文件spring.factories 
    然后在自己的组件包中定义一个用@Configuration来定义的类，还有Configure对应的bean
    想情见[https://github.com/liushaoming/jframe](https://github.com/liushaoming/jframe) 里
    的<code>jframe-spring-boot-starter</code>

<hr/>

* 自己查漏补缺

    * sql遗漏知识点
    
    答案: exists与in的区别和各自的应用场景是什么？    having的作用是什么？
    truncate与delete的区别.
    Oracle的over(), partition by的使用。partition by与group by的区别。
    


<hr/>

* 京东金融

    * Java基础
     - 泛型，用过泛型吗， 泛型的原理
     - 封装继承多态你是怎么理解的
     - hashCode()和equals()方法可以只写一个吗？只写一个有什么问题
    * 多线程
     future在什么场景下使用？
    * 数据库
      数据库用的什么,mysql， 联合索引的有效性，创建一个含有3个字段的索引(A,B,C), where A 有效吗？ where A and B 有效吗？。。。
    * 框架
      Spring-cloud如何保证幂等性？
    * 设计模式
      写一下几种单例模式， 饿汉式，懒汉式，有没有线程安全的问题；
    * 算法
      写一个熟悉的排序算法 
    
* 某知名互联网公司
```text
一、选择题

1. 以下算法中能恢复明文的加密算法有（）
A. BASE64
B. RSA
C. AES
D. SHA256
E. DES
F. Blowfish

答案(ABCEF)
解析 
A. BASE64属于一种编码方式，可以逆向编码，也就可以恢复明文   (可逆)
B. RSA 由 RSA 公司发明，是一个支持变长密钥的公共密钥算法，需要加密的文件块的长度也是可变的（非对称加密）
可以恢复明文  (可逆)
C. AES（Advanced Encryption Standard）：高级加密标准，是下一代的加密算法标准，速度快，安全级别高； (可逆)
D. SHA256 是一种hash加密（  不可逆）
E. DES （Data Encryption Standard） 对称加密  (可逆) 
F. Blowfish算法是一个64位分组及可变密钥长度的对称密钥分组密码算法 (可逆)

结论：除了Hash加密算法不可逆，其它的对称加密和不对称加密还有字符编码算法都是可逆的算法。
Hash算法特别的地方在于它是一种单向算法，用户可以通过Hash算法对目标信息生成一段特定长度的唯一的Hash值，却不能通过这个Hash值重新
获得目标信息。因此Hash算法常用在不可还原的密码存储、信息完整性校验等。
常见的Hash算法：MD2、MD4、MD5、HAVAL、SHA、SHA-1、HMAC、HMAC-MD5、HMAC-SHA1、SHA-256

二、判断题
1. 如果两台主机的MAC地址相同，则无法互相通信()
答案: 未知
2. 在设计网站的登录功能的时候，为防止密码在传输过程中被非法窃取，可以通过MD5算法在浏览器端加密后传到服务器端。
服务器端通过把密码进行解密后与数据库中存储的密码进行比对一致则登录成功()
答案：错误
3. 一个进程中有多个线程，每个线程没有独立的地址空间（）
答案：错误
4. MySQL的InnerDB和MyISAM引擎都支持事务（）
答案：错误
5. 数据库索引可以提高增删改查等操作的速度（）
答案：错误
6. 快速排序在平均情况下比冒泡排序快，不过在某些情况下冒泡排序可能比快速排序快（）
答案：正确

三、问答题
1、生成10个[100,200]的随机数(优先使用JDK8新特性)


2、将以下字符串按逗号分割成List,去除每个成员项首位空格，过滤空白项，合并重复项
，逆序排列。（优先使用JDK8新特性）
String s = " z111,c888,n222,,,g000, t333,a999,c888 ,p000 ,z111 ";


3、一天有一个年轻人来到王老板的店里买了一件礼物这件礼物的成本是18元， 标价是21元。结果这个年轻人
掏出100元要买这件礼物。 王老板当时没有零钱，用100元向街坊换了100元的零钱，找给年轻人79元。但是街坊
发现那个100元是假钞，老板无奈还了街坊100元。现在问题是：王老板在这次交易中国呢到底损失了多少钱？
答案：答案不难，让读者自己体会😄

```


## 更多Java面试题

部分题目的答案和实例代码在陆续整理中，你们也可以贡献你们的答案。

更多面试题，广大程序员们，欢迎你们star此项目。

欢迎你们提交Pull Request以提交你们的题目
