package com.will.springioc.sample;

public class DemoBeanFactory {
    public static DemoBean getDemoBeanStatic() {
        return new DemoBean();
    }

    public DemoBean getDemoBean() {
        return new DemoBean();
    }
}
