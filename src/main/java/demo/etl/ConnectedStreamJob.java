package demo.etl;

import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * 一个控制流是用来指定哪些词需要从 streamOfWords 里过滤掉的（在control中存在，但是streamOfWords不存在的值）。
 * <br/>
 * 一个称为 ControlFunction 的 RichCoFlatMapFunction 作用于连接的流来实现这个功能。
 * <br/>
 * 期望输出：APACHE，FLINK
 * <br/>但是我们没法控制 flatMap1 和 flatMap2 的调用顺序，这两个输入流是相互竞争的关系，
 * 可以使用自定义的算子实现 InputSelectable 接口，在两输入算子消费它的输入流时增加一些顺序上的限制。
 * @see <a href="https://ci.apache.org/projects/flink/flink-docs-release-1.13/zh/docs/learn-flink/etl/">
 *     https://ci.apache.org/projects/flink/flink-docs-release-1.13/zh/docs/learn-flink/etl/
 *     </a>
 * @author 景行
 * @author Kevin Fan
 * @date 2021/07/04
 **/
public class ConnectedStreamJob {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        KeyedStream<String, String> control = env
                .fromElements("DROP", "IGNORE")
                .keyBy(x -> x);

        KeyedStream<String, String> streamOfWords = env
                .fromElements("APACHE", "FLINK","DROP", "IGNORE")
                .keyBy(x -> x);

        control
                .connect(streamOfWords)
                .flatMap(new ControlFunction())
                .print();

        // 真正触发程序执行的方法
        env.execute();
    }
}
