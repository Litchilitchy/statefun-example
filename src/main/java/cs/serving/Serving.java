package cs.serving;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.CoreOptions;
import org.apache.flink.statefun.flink.core.*;
import org.apache.flink.statefun.flink.core.message.MessageFactoryType;
import org.apache.flink.statefun.flink.core.spi.Modules;
import org.apache.flink.statefun.flink.io.datastream.SinkFunctionSpec;
import org.apache.flink.statefun.flink.io.datastream.SourceFunctionSpec;
import org.apache.flink.statefun.sdk.io.EgressIdentifier;
import org.apache.flink.statefun.sdk.io.EgressSpec;
import org.apache.flink.statefun.sdk.io.IngressIdentifier;
import org.apache.flink.statefun.sdk.io.IngressSpec;
import org.apache.flink.streaming.api.environment.ExecutionCheckpointingOptions;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;


import java.util.HashMap;
import java.util.Map;

public class Serving {
    private final Configuration flinkConfig;
    private final Map<IngressIdentifier<?>, IngressSpec<?>> ingress = new HashMap<>();
    private final Map<EgressIdentifier<?>, EgressSpec<?>> egress = new HashMap<>();

    public Serving() {flinkConfig = new Configuration();}

    public void start() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        configureStrictlyRequiredFlinkConfigs(flinkConfig);

        env.configure(flinkConfig, Thread.currentThread().getContextClassLoader());

        StatefulFunctionsConfig stateFunConfig = new StatefulFunctionsConfig(flinkConfig);
//        stateFunConfig.addAllGlobalConfigurations(globalConfigurations);

        stateFunConfig.setProvider(new ServingProvider(ingress, egress));
        env.setParallelism(1);
        StatefulFunctionsJob.main(env, stateFunConfig);
    }
    public static final class ServingProvider implements StatefulFunctionsUniverseProvider {
        private static final long serialVersionUID = 1;
        private final Map<IngressIdentifier<?>, IngressSpec<?>> ingressToReplace;
        private final Map<EgressIdentifier<?>, EgressSpec<?>> egressToReplace;

        ServingProvider(
                Map<IngressIdentifier<?>, IngressSpec<?>> dummyIngress,
                Map<EgressIdentifier<?>, EgressSpec<?>> dummyEgress) {
            this.ingressToReplace = dummyIngress;
            this.egressToReplace = dummyEgress;
        }

        @Override
        public StatefulFunctionsUniverse get(
                ClassLoader classLoader, StatefulFunctionsConfig configuration) {
            Modules modules = Modules.loadFromClassPath();
            StatefulFunctionsUniverse universe = modules.createStatefulFunctionsUniverse(configuration);
            ingressToReplace.forEach((id, spec) -> universe.ingress().put(id, spec));
            egressToReplace.forEach((id, spec) -> universe.egress().put(id, spec));
            return universe;
        }
    }
    private static void configureStrictlyRequiredFlinkConfigs(Configuration flinkConfig) {
        flinkConfig.set(
                CoreOptions.ALWAYS_PARENT_FIRST_LOADER_PATTERNS_ADDITIONAL,
                String.join(";", StatefulFunctionsConfigValidator.PARENT_FIRST_CLASSLOADER_PATTERNS));
        flinkConfig.set(
                ExecutionCheckpointingOptions.MAX_CONCURRENT_CHECKPOINTS,
                StatefulFunctionsConfigValidator.MAX_CONCURRENT_CHECKPOINTS);
        flinkConfig.set(
                StatefulFunctionsConfig.USER_MESSAGE_SERIALIZER, MessageFactoryType.WITH_KRYO_PAYLOADS);
    }

    public static void main(String[] args) throws Exception{
        Logger.getLogger("org").setLevel(Level.INFO);
        Serving serving = new Serving();
        IngressIdentifier ingressId = new IngressIdentifier<>(String.class, "", "in");
        EgressIdentifier egressId = new EgressIdentifier<>("", "out", String.class);
        serving.ingress.put(ingressId, new SourceFunctionSpec<>(ingressId, new Source()));
        serving.egress.put(egressId, new SinkFunctionSpec<>(egressId, new Sink()));
        serving.start();
    }
}
