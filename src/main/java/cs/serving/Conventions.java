package cs.serving;

import org.apache.flink.statefun.sdk.FunctionType;
import org.apache.flink.statefun.sdk.io.EgressIdentifier;
import org.apache.flink.statefun.sdk.io.IngressIdentifier;

public class Conventions {
    static final IngressIdentifier<String> REQUEST_INGRESS =
            new IngressIdentifier<>(
                    String.class, "", "in");

    static final EgressIdentifier<String> RESULT_EGRESS =
            new EgressIdentifier<>(
                    "", "out", String.class);

    static final FunctionType MY_FUNCTION_TYPE =
            new FunctionType("", "my-function");
}
