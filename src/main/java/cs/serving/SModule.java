package cs.serving;

import com.google.auto.service.AutoService;
import java.util.Map;
import org.apache.flink.statefun.sdk.spi.StatefulFunctionModule;

@AutoService(StatefulFunctionModule.class)
public class SModule implements StatefulFunctionModule {

    @Override
    public void configure(Map<String, String> globalConfiguration, Binder binder) {
        binder.bindIngressRouter(Conventions.REQUEST_INGRESS, new SRouter());
        binder.bindFunctionProvider(Conventions.MY_FUNCTION_TYPE, unused -> new SFunction());
    }
}