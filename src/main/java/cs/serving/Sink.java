package cs.serving;

import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.apache.kafka.common.protocol.types.Field;

public class Sink extends RichSinkFunction<String> {
    @Override
    public void invoke(String value, Context context) throws Exception {
        System.out.println("Sink received " + value);
    }
}
