package com.will.springioc.sample;

public class DemoBean {
    public void peformAction() {
        System.out.println(this + " did something");
    }

    public void initialize() {
        System.out.println(this.hashCode() + " bean init() is called.");
    }

    public void destroy() {
        System.out.println(this.hashCode() + " bean destroy() is called.");
    }
}
