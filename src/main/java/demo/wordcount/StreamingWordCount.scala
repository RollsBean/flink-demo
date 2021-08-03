package demo.wordcount

import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment, createTypeInformation}
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows
import org.apache.flink.streaming.api.windowing.time.Time

/**
 *
 * @author 景行
 * @author Kevin Fan
 * @date 2021/08/03
 * */
object StreamingWordCount {

  /**
   * 使用方法：
   * <br/>
   * <p>1. 在控制台执行命令 <code>nc -lk 9999</code>
   * <p>2. 然后运行本程序
   * <p>3. 执行完步骤 1 后，在控制台输入文本，Flink 程序将记录每个单词出现的次数并打印在控制台
   * <blockquote> 示例
   * <pre>
   * 输入：java: Compilation failed: internal java compiler error
   * 输出：3> (compilation,1)
   *      1> (datastream,1)
   *      2> (java,2)
   *      7> (error,1)
   *      4> (internal,1)
   *      3> (compiler,1)
   *      1> (failed,1)
   * </pre>
   * 上面的输出中，开头的 "3>" 指的是 task id，接下来是每个单词出现的次数
   * @param args
   */
  def main(args: Array[String]): Unit = {
    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment

    val text = env.socketTextStream("localhost", 9999)
    val counts: DataStream[(String, Int)] = text.flatMap {
      _.toLowerCase.split("\\W+") filter {
        _.nonEmpty
      }
    }
      .map {
        (_, 1)
      }
      .keyBy(_._1)
      .window(TumblingProcessingTimeWindows.of(Time.seconds(5)))
      .sum(1)

    counts.print()

    env.execute("Window Stream WordCount")
  }

}
