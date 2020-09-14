package cs.serving;

import org.apache.flink.statefun.sdk.FunctionType;
import org.apache.flink.statefun.sdk.io.Router;

public class SRouter implements Router<String> {
    @Override
    public void route(String message, Downstream<String> downstream) {

        FunctionType type = new FunctionType("", "my-function");
        System.out.println("Downstreaming <" + message + "> to " + type.toString());
        downstream.forward(type, message, message + "<router processed>");
    }
}
