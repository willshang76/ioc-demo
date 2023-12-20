package com.will.springioc.bean;

public interface BeanRegistry {
    void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) throws Exception;

    BeanDefinition getBeanDefinition(String beanName);

    boolean containsBeanDefinition(String beanName);
}