package cs.test;


import com.google.auto.service.AutoService;

@AutoService(Module.class)
public class MyModule implements Module {
    @Override
    public void get() {
        System.out.println("get!");
    }
}
