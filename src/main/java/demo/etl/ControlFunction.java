package demo.etl;

import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.co.RichCoFlatMapFunction;
import org.apache.flink.util.Collector;

/**
 * <code>RichCoFlatMapFunction</code> 是一个双流 flatmap 的函数
 * @author 景行
 * @author Kevin Fan
 * @date 2021/07/04
 **/
public class ControlFunction extends RichCoFlatMapFunction<String,String,String> {

    private ValueState<Boolean> blocked;

    @Override
    public void open(Configuration parameters) throws Exception {
        blocked = getRuntimeContext()
                .getState(new ValueStateDescriptor<>("blocked", Boolean.class));
    }

    /**
     * 第一个流的元素处理
     * @param value
     * @param out
     * @throws Exception
     */
    @Override
    public void flatMap1(String value, Collector<String> out) throws Exception {
        blocked.update(true);
    }

    /**
     * 第二个流的元素处理
     * @param value
     * @param out
     * @throws Exception
     */
    @Override
    public void flatMap2(String value, Collector<String> out) throws Exception {
        if (blocked.value() == null) {
            out.collect(value);
        }
    }
}
