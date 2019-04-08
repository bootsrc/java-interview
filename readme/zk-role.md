# zookeeper有哪些角色

## zookeeper角色：

* leader：为客户端提供写服务，负责进行投票的发起和决议，更新系统状态，事务请求的唯一调度和处理者

* follower：为客户端提供读服务，参与投票，包括事务请求proposal投票和leader选举投票，接收客户端请求，为客户端返回结果

* observer：为客户端提供读服务，不参与任何投票，包括事务请求proposal投票和leader选举投票。 它会同步leader的状态，加快读写速度