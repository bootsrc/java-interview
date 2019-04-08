redis包含三种集群策略

* 主从复制
* 哨兵
* 集群

## 主从复制
在主从复制中，数据库分为俩类，主数据库(master)和从数据库(slave)。其中主从复制有如下特点：

主数据库可以进行读写操作，当读写操作导致数据变化时会自动将数据同步给从数据库
从数据库一般都是只读的，并且接收主数据库同步过来的数据
一个master可以拥有多个slave，但是一个slave只能对应一个master
主从复制工作机制
当slave启动后，主动向master发送SYNC命令。master接收到SYNC命令后在后台保存快照（RDB持久化）和缓存保存快照这段时间的命令，然后将保存的快照文件和缓存的命令发送给slave。slave接收到快照文件和命令后加载快照文件和缓存的执行命令。

复制初始化后，master每次接收到的写命令都会同步发送给slave，保证主从数据一致性。

主从配置
redis默认是主数据，所以master无需配置，我们只需要修改slave的配置即可。

设置需要连接的master的ip端口:

slaveof 192.168.0.107 6379

如果master设置了密码。需要配置：

masterauth

连接成功进入命令行后，可以通过以下命令行查看连接该数据库的其他库信息:

info replication

## 哨兵
哨兵的作用是监控 redis系统的运行状况，他的功能如下：

监控主从数据库是否正常运行
master出现故障时，自动将slave转化为master
多哨兵配置的时候，哨兵之间也会自动监控
多个哨兵可以监控同一个redis
哨兵工作机制
哨兵进程启动时会读取配置文件的内容，通过sentinel monitor master-name ip port quorum查找到master的ip端口。一个哨兵可以监控多个master数据库，只需要提供多个该配置项即可。

同事配置文件还定义了与监控相关的参数，比如master多长时间无响应即即判定位为下线。

哨兵启动后，会与要监控的master建立俩条连接：

一条连接用来订阅master的_sentinel_:hello频道与获取其他监控该master的哨兵节点信息
另一条连接定期向master发送INFO等命令获取master本身的信息
与master建立连接后，哨兵会执行三个操作，这三个操作的发送频率都可以在配置文件中配置：

定期向master和slave发送INFO命令
定期向master个slave的_sentinel_:hello频道发送自己的信息
定期向master、slave和其他哨兵发送PING命令
这三个操作的意义非常重大，发送INFO命令可以获取当前数据库的相关信息从而实现新节点的自动发现。所以说哨兵只需要配置master数据库信息就可以自动发现其slave信息。获取到slave信息后，哨兵也会与slave建立俩条连接执行监控。通过INFO命令，哨兵可以获取主从数据库的最新信息，并进行相应的操作，比如角色变更等。

接下来哨兵向主从数据库的_sentinel_:hello频道发送信息与同样监控这些数据库的哨兵共享自己的信息，发送内容为哨兵的ip端口、运行id、配置版本、master名字、master的ip端口还有master的配置版本。这些信息有以下用处：

其他哨兵可以通过该信息判断发送者是否是新发现的哨兵，如果是的话会创建一个到该哨兵的连接用于发送PIN命令。
其他哨兵通过该信息可以判断master的版本，如果该版本高于直接记录的版本，将会更新
当实现了自动发现slave和其他哨兵节点后，哨兵就可以通过定期发送PING命令定时监控这些数据库和节点有没有停止服务。发送频率可以配置，但是最长间隔时间为1s，可以通过sentinel down-after-milliseconds mymaster 600设置。

如果被ping的数据库或者节点超时未回复，哨兵任务其主观下线。如果下线的是master，哨兵会向其他哨兵点发送命令询问他们是否也认为该master主观下线，如果达到一定数目（即配置文件中的quorum）投票，哨兵会认为该master已经客观下线，并选举领头的哨兵节点对主从系统发起故障恢复。

如上文所说，哨兵认为master客观下线后，故障恢复的操作需要由选举的领头哨兵执行，选举采用Raft算法：

发现master下线的哨兵节点（我们称他为A）向每个哨兵发送命令，要求对方选自己为领头哨兵
如果目标哨兵节点没有选过其他人，则会同意选举A为领头哨兵
如果有超过一半的哨兵同意选举A为领头，则A当选
如果有多个哨兵节点同时参选领头，此时有可能存在一轮投票无竞选者胜出，此时每个参选的节点等待一个随机时间后再次发起参选请求，进行下一轮投票精选，直至选举出领头哨兵
选出领头哨兵后，领头者开始对进行故障恢复，从出现故障的master的从数据库中挑选一个来当选新的master,选择规则如下：

所有在线的slave中选择优先级最高的，优先级可以通过slave-priority配置
如果有多个最高优先级的slave，则选取复制偏移量最大（即复制越完整）的当选
如果以上条件都一样，选取id最小的slave
挑选出需要继任的slaver后，领头哨兵向该数据库发送命令使其升格为master，然后再向其他slave发送命令接受新的master，最后更新数据。将已经停止的旧的master更新为新的master的从数据库，使其恢复服务后以slave的身份继续运行。

哨兵配置
哨兵配置的配置文件为sentinel.conf，设置主机名称，地址，端口，以及选举票数即恢复时最少需要几个哨兵节点同意。

sentinel monitor mymaster 192.168.0.107 6379 1

只要配置需要监控的master就可以了，哨兵会监控连接该master的slave。

启动哨兵节点：

redis-server sentinel.conf --sentinel &

出现如下内容表示启动成功

```text
[root@buke110 redis]# bin/redis-server etc/sentinel.conf --sentinel &
[1] 3072
[root@buke110 redis]# 3072:X 12 Apr 22:40:02.503 * Increased maximum number of open files to 10032 (it was originally set to 1024).
                _._                                                  
           _.-``__ ''-._                                             
      _.-``    `.  `_.  ''-._           Redis 2.9.102 (00000000/0) 64 bit
  .-`` .-```.  ```\/    _.,_ ''-._                                   
 (    '      ,       .-`  | `,    )     Running in sentinel mode
 |`-._`-...-` __...-.``-._|'` _.-'|     Port: 26379
 |    `-._   `._    /     _.-'    |     PID: 3072
  `-._    `-._  `-./  _.-'    _.-'                                   
 |`-._`-._    `-.__.-'    _.-'_.-'|                                  
 |    `-._`-._        _.-'_.-'    |           http://redis.io        
  `-._    `-._`-.__.-'_.-'    _.-'                                   
 |`-._`-._    `-.__.-'    _.-'_.-'|                                  
 |    `-._`-._        _.-'_.-'    |                                  
  `-._    `-._`-.__.-'_.-'    _.-'                                   
      `-._    `-.__.-'    _.-'                                       
          `-._        _.-'                                           
              `-.__.-'                                               

3072:X 12 Apr 22:40:02.554 # Sentinel runid is e510bd95d4deba3261de72272130322b2ba650e7
3072:X 12 Apr 22:40:02.554 # +monitor master mymaster 192.168.0.107 6379 quorum 1
3072:X 12 Apr 22:40:03.516 * +slave slave 192.168.0.108:6379 192.168.0.108 6379 @ mymaster 192.168.0.107 6379
3072:X 12 Apr 22:40:03.516 * +slave slave 192.168.0.109:6379 192.168.0.109 6379 @ mymaster 192.168.0.107 6379


```

可以在任何一台服务器上查看指定哨兵节点信息：

bin/redis-cli -h 192.168.0.110 -p 26379 info Sentinel

控制台输出哨兵信息：

```text
[root@buke107 redis]# bin/redis-cli -h 192.168.0.110  -p 26379  info Sentinel
# Sentinel
sentinel_masters:1
sentinel_tilt:0
sentinel_running_scripts:0
sentinel_scripts_queue_length:0
master0:name=mymaster,status=ok,address=192.168.0.107:6379,slaves=2,sentinels=1
```

## 集群
使用集群，只需要将每个数据库节点的cluster-enable配置打开即可。每个集群中至少需要三个主数据库才能正常运行。

集群配置
安装依赖环境ruby，注意ruby版本必须高于2.2

```shell 
yum install ruby
yum install rubygems
gem install redis
```

修改配置文件:

```text 
bind 192.168.0.107
```

配置端口
```text
port 6380
```

配置快照保存路径
```text
dir /usr/local/redis-cluster/6380/
```

开启集群
```text
cluster-enabled yes
```

为节点设置不同的工作目录
```text
cluster-config-file nodes-6380.conf
```

集群失效时间
```text
cluster-node-timeout 15000
```

开启集群中的节点:
```text
reids-service …/6380/redis.conf
```

将节点加入集群中
```text
redis-trib.rb create --replicas 1 192.168.0.107:6380 192.168.0.107:6381 192.168.0.107:6382 192.168.0.107:6383 192.168.0.107:6384 192.168.0.107:6385
```


中途需要输入yes确定创建集群:
```text
[root@buke107 src]# redis-trib.rb create --replicas 1 192.168.0.107:6380 192.168.0.107:6381 192.168.0.107:6382 192.168.0.107:6383 192.168.0.107:6384 192.168.0.107:6385 
>>> Creating cluster
Connecting to node 192.168.0.107:6380: OK
Connecting to node 192.168.0.107:6381: OK
Connecting to node 192.168.0.107:6382: OK
Connecting to node 192.168.0.107:6383: OK
Connecting to node 192.168.0.107:6384: OK
Connecting to node 192.168.0.107:6385: OK
>>> Performing hash slots allocation on 6 nodes...
Using 3 masters:
192.168.0.107:6380
192.168.0.107:6381
192.168.0.107:6382
Adding replica 192.168.0.107:6383 to 192.168.0.107:6380
Adding replica 192.168.0.107:6384 to 192.168.0.107:6381
Adding replica 192.168.0.107:6385 to 192.168.0.107:6382
M: 5cd3ed3a84ead41a765abd3781b98950d452c958 192.168.0.107:6380
   slots:0-5460 (5461 slots) master
M: 90b4b326d579f9b5e181e3df95578bceba29b204 192.168.0.107:6381
   slots:5461-10922 (5462 slots) master
M: 868456121fa4e6c8e7abe235a88b51d354a944b5 192.168.0.107:6382
   slots:10923-16383 (5461 slots) master
S: b8e047aeacb9398c3f58f96d0602efbbea2078e2 192.168.0.107:6383
   replicates 5cd3ed3a84ead41a765abd3781b98950d452c958
S: 68cf66359318b26df16ebf95ba0c00d9f6b2c63e 192.168.0.107:6384
   replicates 90b4b326d579f9b5e181e3df95578bceba29b204
S: d6d01fd8f1e5b9f8fc0c748e08248a358da3638d 192.168.0.107:6385
   replicates 868456121fa4e6c8e7abe235a88b51d354a944b5
Can I set the above configuration? (type 'yes' to accept): yes
>>> Nodes configuration updated
>>> Assign a different config epoch to each node
>>> Sending CLUSTER MEET messages to join the cluster
Waiting for the cluster to join....
>>> Performing Cluster Check (using node 192.168.0.107:6380)
M: 5cd3ed3a84ead41a765abd3781b98950d452c958 192.168.0.107:6380
   slots:0-5460 (5461 slots) master
M: 90b4b326d579f9b5e181e3df95578bceba29b204 192.168.0.107:6381
   slots:5461-10922 (5462 slots) master
M: 868456121fa4e6c8e7abe235a88b51d354a944b5 192.168.0.107:6382
   slots:10923-16383 (5461 slots) master
M: b8e047aeacb9398c3f58f96d0602efbbea2078e2 192.168.0.107:6383
   slots: (0 slots) master
   replicates 5cd3ed3a84ead41a765abd3781b98950d452c958
M: 68cf66359318b26df16ebf95ba0c00d9f6b2c63e 192.168.0.107:6384
   slots: (0 slots) master
   replicates 90b4b326d579f9b5e181e3df95578bceba29b204
M: d6d01fd8f1e5b9f8fc0c748e08248a358da3638d 192.168.0.107:6385
   slots: (0 slots) master
   replicates 868456121fa4e6c8e7abe235a88b51d354a944b5
[OK] All nodes agree about slots configuration.
>>> Check for open slots...
>>> Check slots coverage...
[OK] All 16384 slots covered.
```

进入任何一个集群中的节点:
```text
redis-cli -c -h 192.168.0.107 -p 6381
```

查看集群中的节点:
```text
[root@buke107 src]# redis-cli -c -h 192.168.0.107 -p 6381
192.168.0.107:6381> cluster nodes
868456121fa4e6c8e7abe235a88b51d354a944b5 192.168.0.107:6382 master - 0 1523609792598 3 connected 10923-16383
d6d01fd8f1e5b9f8fc0c748e08248a358da3638d 192.168.0.107:6385 slave 868456121fa4e6c8e7abe235a88b51d354a944b5 0 1523609795616 6 connected
5cd3ed3a84ead41a765abd3781b98950d452c958 192.168.0.107:6380 master - 0 1523609794610 1 connected 0-5460
b8e047aeacb9398c3f58f96d0602efbbea2078e2 192.168.0.107:6383 slave 5cd3ed3a84ead41a765abd3781b98950d452c958 0 1523609797629 1 connected
68cf66359318b26df16ebf95ba0c00d9f6b2c63e 192.168.0.107:6384 slave 90b4b326d579f9b5e181e3df95578bceba29b204 0 1523609796622 5 connected
90b4b326d579f9b5e181e3df95578bceba29b204 192.168.0.107:6381 myself,master - 0 0 2 connected 5461-10922

```

如上图所示，已经建立起三主三从的集群。

增加集群节点


转自原文：
[https://blog.csdn.net/q649381130/article/details/79931791](https://blog.csdn.net/q649381130/article/details/79931791)
