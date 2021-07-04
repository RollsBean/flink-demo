package demo.fraud

import org.apache.flink.api.common.state.{ValueState, ValueStateDescriptor}
import org.apache.flink.api.scala.typeutils.Types
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.KeyedProcessFunction
import org.apache.flink.util.Collector
import org.apache.flink.walkthrough.common.entity.{Alert, Transaction}

object FraudDetector {
  val SMALL_AMOUNT: Double = 1.00
  val LARGE_AMOUNT: Double = 500.00
  val ONE_MINUTE: Long     = 60 * 1000L
}
/*
 *
 * @author: 景行
 * @author: Kevin Fan
 * @date: 2021/7/1
 */
class FraudDetector extends KeyedProcessFunction[Long, Transaction, Alert]{

  /**
   * <code>ValueState</code> 提供了三个方法，update 更新状态；value 获取值；clear 清空状态
   */
  @transient private var flagState: ValueState[java.lang.Boolean] = _

  /**
   * 定义 timerState 来保存时间状态
   */
  @transient private var timerState: ValueState[java.lang.Long] = _

  @throws[Exception]
  override def open(parameters: Configuration): Unit = {
    val flagDescriptor = new ValueStateDescriptor("flag", Types.BOOLEAN)
    flagState = getRuntimeContext.getState(flagDescriptor)

    val timerDescriptor = new ValueStateDescriptor("timer-state", Types.LONG)
    timerState = getRuntimeContext.getState(timerDescriptor)
  }

  /**
   * 逻辑：
   * <br>
   * <p>如果交易小于SMALL_AMOUNT，则将状态置为true；
   * 如果状态不为null，也就是状态是true，并且交易大于LARGE_AMOUNT，则发送一条告警日志
   * <p>无论交易是否大于LARGE_AMOUNT，都需要重制状态为null，因为只检测两个相邻的交易
   * <br>
   * 更新v2：
   * <p>
   *   添加上时间限制，只有在一分钟内发生上面这种情况才判断为欺诈交易
   * @param transaction
   * @param context
   * @param collector
   * @throws
   */
  @throws[Exception]
  override def processElement(transaction: Transaction,
                              context: KeyedProcessFunction[Long, Transaction, Alert]#Context,
                              collector: Collector[Alert]): Unit = {
    // Get the current state for the current key
    val lastTransactionWasSmall = flagState.value

    // Check if the flag is set
    if (lastTransactionWasSmall != null) {
      if (transaction.getAmount > FraudDetector.LARGE_AMOUNT) {
        // Output an alert downstream
        val alert = new Alert
        alert.setId(transaction.getAccountId)

        collector.collect(alert)
      }
      // Clean up our state
      cleanUp(context)
    }

    if (transaction.getAmount < FraudDetector.SMALL_AMOUNT) {
      // set the flag to true
      flagState.update(true)
      val timer = context.timerService.currentProcessingTime + FraudDetector.ONE_MINUTE

      context.timerService.registerProcessingTimeTimer(timer)
      timerState.update(timer)
    }
  }

  @throws[Exception]
  private def cleanUp(ctx: KeyedProcessFunction[Long, Transaction, Alert]#Context): Unit = {
    // delete timer
    val timer = timerState.value
    ctx.timerService.deleteProcessingTimeTimer(timer)

    // clean up all states
    timerState.clear()
    flagState.clear()
  }

  /**
   * 定时器到时之后的处理
   * <br>
   * 这里需要在定时器到时之后，清除它的状态信息（标识状态和时间状态）
   * @param timestamp
   * @param ctx
   * @param out
   */
  override def onTimer(timestamp: Long, ctx: KeyedProcessFunction[Long, Transaction, Alert]#OnTimerContext, out: Collector[Alert]): Unit = {
    // remove key state
    flagState.clear()
    timerState.clear()
  }
}
