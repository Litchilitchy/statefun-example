package cs.serving;

import org.apache.flink.kinesis.shaded.com.amazonaws.services.dynamodbv2.xspec.M;
import org.apache.flink.statefun.sdk.Context;
import org.apache.flink.statefun.sdk.StatefulFunction;

public class SFunction implements StatefulFunction {
    Model model;
    @Override
    public void invoke(Context context, Object input) {
        String in = (String) input;
        if (model == null) {
            model = new Model(in);
        }
        if (!(input instanceof String)) {
            throw new IllegalArgumentException("Unknown message received " + input);
        }

        String out = in + "function processed";
        System.out.println(context.toString());
        System.out.println("Function received ->" + in + "||");
        System.out.println("Function processed to ->" + out + "||");
        context.send(Conventions.RESULT_EGRESS, out);
    }
}
class Model {
    Model(String input) {
        System.out.println("New Model loaded with input " + input);
    }
}