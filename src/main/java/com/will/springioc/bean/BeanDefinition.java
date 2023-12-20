package com.will.springioc.bean;

/**
 * Bean interface
 */
public interface BeanDefinition {
    enum Scope {
        SCOPE_UNSPECIFIED,
        SINGLETON,
        PROTOTYPE,
    }

    Scope scope = Scope.SCOPE_UNSPECIFIED;

    Class<?> getBeanClass();

    Scope getScope();

    String getFactoryMethodName();

    String getFactoryBeanName();

    String getInitMethodName();

    String getDestroyMethodName();

}
