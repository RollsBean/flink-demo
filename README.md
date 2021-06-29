# Flink demo

Flink 入门学习，代码参考官方教程：[https://ci.apache.org/projects/flink/flink-docs-release-1.13/zh/](https://ci.apache.org/projects/flink/flink-docs-release-1.13/zh/)

## 本地环境搭建 Flink 

### 下载安装

Flink JAR 包下载地址：[https://flink.apache.org/downloads.html#apache-flink-1131](https://flink.apache.org/downloads.html#apache-flink-1131)

[flink download](./static/image/flink%20download.jpg)

作为参考，我下载的版本是 `Apache Flink 1.13.1 for Scala 2.12`，Flink 版本是 `1.13.1`，对应的 Scala 版本是 `2.1.2`

### 本地运行 Flink

解压之后进入 bin 目录执行如下命令启动 Flink：

```shell
kevin@medeMacBook-Pro bin % ./start-cluster.sh
Starting cluster.
Starting standalonesession daemon on host medeMacBook-Pro.local.
Starting taskexecutor daemon on host medeMacBook-Pro.local.
```

## 本地安装 Scala 

需要安装对应的 Scala 版本，我这里用的是 `2.1.2`

## 运行 demo

准备工作完成后，就可以运行本项目了。

### 运行官方的反欺诈小 demo

运行 demo.FraudDetectionJob.main 方法，出现如下日志表明运行成功。

```text
[main] INFO org.apache.flink.runtime.minicluster.MiniCluster - Starting Flink Mini Cluster
[main] INFO org.apache.flink.runtime.minicluster.MiniCluster - Starting Metrics Registry
[main] INFO org.apache.flink.runtime.metrics.MetricRegistryImpl - No metrics reporter configured, no metrics will be exposed/reported.
[main] INFO org.apache.flink.runtime.minicluster.MiniCluster - Starting RPC Service(s)
[main] INFO org.apache.flink.runtime.rpc.akka.AkkaRpcServiceUtils - Trying to start local actor system
[flink-akka.actor.default-dispatcher-3] INFO akka.event.slf4j.Slf4jLogger - Slf4jLogger started
[main] INFO org.apache.flink.runtime.rpc.akka.AkkaRpcServiceUtils - Actor system started at akka://flink
[main] INFO org.apache.flink.runtime.rpc.akka.AkkaRpcServiceUtils - Trying to start local actor system
[flink-metrics-2] INFO akka.event.slf4j.Slf4jLogger - Slf4jLogger started
[main] INFO org.apache.flink.runtime.rpc.akka.AkkaRpcServiceUtils - Actor system started at akka://flink-metrics
[main] INFO org.apache.flink.runtime.rpc.akka.AkkaRpcService - Starting RPC endpoint for org.apache.flink.runtime.metrics.dump.MetricQueryService at akka://flink-metrics/user/rpc/MetricQueryService .
[main] INFO org.apache.flink.runtime.minicluster.MiniCluster - Starting high-availability services
...
[flink-akka.actor.default-dispatcher-2] INFO org.apache.flink.runtime.executiongraph.ExecutionGraph - fraud-detector -> Sink: send-alerts (2/8) (9ebfa880a558aba0aad5f9d11ff06d4d) switched from INITIALIZING to RUNNING.
[fraud-detector -> Sink: send-alerts (4/8)#0] INFO org.apache.flink.runtime.taskmanager.Task - fraud-detector -> Sink: send-alerts (4/8)#0 (f7f9f0b4239acbd9dc475a2326c1e908) switched from INITIALIZING to RUNNING.
[fraud-detector -> Sink: send-alerts (1/8)#0] INFO org.apache.flink.runtime.taskmanager.Task - fraud-detector -> Sink: send-alerts (1/8)#0 (b76531e05c365ebf2f9e65ce4428136f) switched from INITIALIZING to RUNNING.
[flink-akka.actor.default-dispatcher-2] INFO org.apache.flink.runtime.executiongraph.ExecutionGraph - fraud-detector -> Sink: send-alerts (3/8) (1ec51b49624f93b66c24cff2e11e045f) switched from INITIALIZING to RUNNING.
[flink-akka.actor.default-dispatcher-2] INFO org.apache.flink.runtime.executiongraph.ExecutionGraph - fraud-detector -> Sink: send-alerts (5/8) (a605c6f873497ef98ef1029090d9988a) switched from INITIALIZING to RUNNING.
[flink-akka.actor.default-dispatcher-2] INFO org.apache.flink.runtime.executiongraph.ExecutionGraph - fraud-detector -> Sink: send-alerts (7/8) (d5d5e27c4e1103a6e6fbcf06e2ff1f57) switched from INITIALIZING to RUNNING.
[flink-akka.actor.default-dispatcher-2] INFO org.apache.flink.runtime.executiongraph.ExecutionGraph - fraud-detector -> Sink: send-alerts (6/8) (2ea6912173dc0e73e1268773ebea2773) switched from INITIALIZING to RUNNING.
[flink-akka.actor.default-dispatcher-2] INFO org.apache.flink.runtime.executiongraph.ExecutionGraph - fraud-detector -> Sink: send-alerts (8/8) (5c6d0c38da9706c6f38c77d32ccd03be) switched from INITIALIZING to RUNNING.
[flink-akka.actor.default-dispatcher-2] INFO org.apache.flink.runtime.executiongraph.ExecutionGraph - fraud-detector -> Sink: send-alerts (4/8) (f7f9f0b4239acbd9dc475a2326c1e908) switched from INITIALIZING to RUNNING.
[flink-akka.actor.default-dispatcher-2] INFO org.apache.flink.runtime.executiongraph.ExecutionGraph - fraud-detector -> Sink: send-alerts (1/8) (b76531e05c365ebf2f9e65ce4428136f) switched from INITIALIZING to RUNNING.
[fraud-detector -> Sink: send-alerts (8/8)#0] INFO org.apache.flink.walkthrough.common.sink.AlertSink - Alert{id=3}
[fraud-detector -> Sink: send-alerts (8/8)#0] INFO org.apache.flink.walkthrough.common.sink.AlertSink - Alert{id=3}
```

### 注意：Maven 依赖的问题

首先定义了 Flink 和 Scala 的版本
```xml
<properties>
    <flink.version>1.13.1</flink.version>
    <scala.version>2.12</scala.version>
</properties>
```

接着，引入 flink 相关的依赖包，开头引入 slf4j 是为了解决 **Failed to load class "org.slf4j.impl.StaticLoggerBinder".** 问题。引入
**flink-clients** 是为了解决**No ExecutorFactory found to execute the application.** 异常。
```xml
<dependencies>
    <!-- https://mvnrepository.com/artifact/org.slf4j/slf4j-simple -->
    <!--    SLF4J: Failed to load class "org.slf4j.impl.StaticLoggerBinder".    -->
    <dependency>
        <groupId>org.slf4j</groupId>
        <artifactId>slf4j-simple</artifactId>
        <version>1.7.30</version>
    </dependency>
    <dependency>
        <groupId>org.apache.flink</groupId>
        <artifactId>flink-scala_${scala.version}</artifactId>
        <version>${flink.version}</version>
        <scope>compile</scope>
    </dependency>
    <dependency>
        <groupId>org.apache.flink</groupId>
        <artifactId>flink-streaming-scala_${scala.version}</artifactId>
        <version>${flink.version}</version>
        <scope>compile</scope>
    </dependency>
    <dependency>
        <groupId>org.apache.flink</groupId>
        <artifactId>flink-streaming-java_${scala.version}</artifactId>
        <version>${flink.version}</version>
        <scope>compile</scope>
    </dependency>
    <dependency>
        <groupId>org.apache.flink</groupId>
        <artifactId>flink-walkthrough-common_${scala.version}</artifactId>
        <version>${flink.version}</version>
    </dependency>
    <!--    Fix: No ExecutorFactory found to execute the application.    -->
    <dependency>
        <groupId>org.apache.flink</groupId>
        <artifactId>flink-clients_${scala.version}</artifactId>
        <version>${flink.version}</version>
    </dependency>
</dependencies>
```

