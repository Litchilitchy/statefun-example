package cs.serving;

import org.apache.flink.streaming.api.functions.source.RichParallelSourceFunction;

public class Source extends RichParallelSourceFunction {
    int a = 0;
    private volatile boolean isRunning = true;
    @Override
    public void run(SourceContext sourceContext) throws Exception {
        while (isRunning) {
            a = (a + 1) % 4;
            Thread.sleep(1000);
            System.out.println("Collecting " + String.valueOf(a) + "from source");
            sourceContext.collect(String.valueOf(a));
        }

    }

    @Override
    public void cancel() {
        System.out.println("Source Cancelling...");
    }
}
