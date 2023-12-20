package com.will.springioc.bean;

import lombok.Data;

@Data
public class MyGenericBeanDefinition implements BeanDefinition {
    private Class<?> beanClass; //User.class

    private Scope scope = Scope.SCOPE_UNSPECIFIED;

    private String factoryBeanName;

    private String factoryMethodName;

    private String initMethodName;

    private String destroyMethodName;
}
