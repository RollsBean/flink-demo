package demo

import org.apache.flink.streaming.api.scala._
import org.apache.flink.walkthrough.common.entity.{Alert, Transaction}
import org.apache.flink.walkthrough.common.sink.AlertSink
import org.apache.flink.walkthrough.common.source.TransactionSource

/*
 * 如果出现小于1美元然后紧跟着一个大于500美元的交易，则输出报警信息
 * @author: 景行
 * @date: 2021/7/1
 */
object FraudDetectionJob {

  /**
   * 参考 Flink 官方文档
   * @see
   * @param args 默认
   */
  def main(args: Array[String]): Unit = {
    // StreamExecutionEnvironment 用于设置你的执行环境
    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment
    // 创建数据源，正常是从 Kafka Rabbit MQ 接受数据，此 demo 使用本地能生成模拟数据的数据源
    val transactions: DataStream[Transaction] = env
      .addSource(new TransactionSource)
      .name("transactions")
    // 使用 accountid 分组并使用 FraudDetector 函数处理数据
    val alerts: DataStream[Alert] = transactions
      .keyBy(transaction => transaction.getAccountId)
      .process(new FraudDetector)
      .name("fraud-detector")
    // 定义输出流，AlertSink 只输出 info 日志
    alerts
      .addSink(new AlertSink)
      .name("send-alerts")

    env.execute("Fraud Detection")
  }

}
