package com.will.springioc.bean;


import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MyBeanFactory implements BeanFactory, BeanRegistry, Closeable {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(MyBeanFactory.class);

    private Map<String, BeanDefinition> beanDefintionMap = new ConcurrentHashMap<>(256);

    private final Map<String, Object> singletonBeanMap = new ConcurrentHashMap<>(256);

    // Register bean and store the definition information.
    @Override
    public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) throws Exception {
        Preconditions.checkNotNull(beanName, "Bean name shouldn't be null.");
        Preconditions.checkNotNull(beanDefinition, "Bean definition shouldn't be null.");

        // Check if the bean definition exists
        Preconditions.checkArgument(!this.containsBeanDefinition(beanName), "Duplicate bean name.");

        this.beanDefintionMap.put(beanName, beanDefinition);

    }

    @Override
    public BeanDefinition getBeanDefinition(String beanName) {
        return this.beanDefintionMap.get(beanName);
    }

    @Override
    public boolean containsBeanDefinition(String beanName) {
        return this.beanDefintionMap.containsKey(beanName);
    }

    @Override
    public Object getBean(String beanName) throws Exception {
        Preconditions.checkNotNull(beanName, "Bean name shouldn't be null");
        BeanDefinition beanDefinition = this.beanDefintionMap.get(beanName);

        Object instance = null;

        if (beanDefinition.getScope().equals(BeanDefinition.Scope.SINGLETON)) {
            synchronized (this.singletonBeanMap) {
                instance = this.singletonBeanMap.get(beanName);
                if (instance == null) {
                    instance = createBeanInstance(beanDefinition);
                    this.singletonBeanMap.put(beanName, instance);
                }
            }
        } else {
            instance = createBeanInstance(beanDefinition);
        }

        return instance;

    }

    @Override
    public <T> T getBean(Class<T> type) throws Exception {
        return null;
    }

    @Override
    public Class<?> getType(String name) throws Exception {
        return null;
    }

    @Override
    public void close() throws IOException {

        for (Map.Entry<String, BeanDefinition> beanDefinitionEntry : this.beanDefintionMap.entrySet()) {
            String beanName = beanDefinitionEntry.getKey();
            BeanDefinition beanDefinition = beanDefinitionEntry.getValue();

            if (beanDefinition.getScope().equals(BeanDefinition.Scope.SINGLETON) && !Strings.isNullOrEmpty(beanDefinition.getDestroyMethodName())) {
                Object instance = this.singletonBeanMap.get(beanName);
                try {
                    Method destoryMethod = instance.getClass().getMethod(beanDefinition.getDestroyMethodName(), null);
                    destoryMethod.invoke(instance, null);
                } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
                         | InvocationTargetException e) {
                    log.error("Exception happens when the Bean(" + beanName + ") definition is: " + beanDefinition + "！", e);
                }
            }
        }
    }

    private Object createBeanInstance(BeanDefinition beanDefinition) throws Exception {
        Class<?> type = beanDefinition.getBeanClass();

        Object beanInstance = null;
        if (type != null) {
            if (Strings.isNullOrEmpty(beanDefinition.getFactoryMethodName())) {
                beanInstance = this.createInstanceConstructor(beanDefinition);
            } else {
                beanInstance = this.createInstanceStaticFactoryMethod(beanDefinition);
            }
        } else {
            beanInstance = this.createInstanceFactory(beanDefinition);
        }

        this.initialize(beanDefinition, beanInstance);

        return beanInstance;
    }

    // Use constructor to create the instance.
    private Object createInstanceConstructor(BeanDefinition beanDefinition)
            throws InstantiationException, IllegalAccessException {
        try {
            return beanDefinition.getBeanClass().newInstance(); //Class
        } catch (SecurityException e) {
            log.error("Couldn't initialize bean via constructor,beanDefinition：" + beanDefinition, e);
            throw e;
        }
    }

    // StaticFactoryMethod to create the instance
    private Object createInstanceStaticFactoryMethod(BeanDefinition beanDefinition) throws Exception {
        Class<?> classType = beanDefinition.getBeanClass(); //User.class ==> constroctor; static method return instance.
        Method m = classType.getMethod(beanDefinition.getFactoryMethodName(), null);
        return m.invoke(classType, null);
    }

    // FactoryBean to create the instance.
    private Object createInstanceFactory(BeanDefinition beanDefinition) throws Exception {
        Object beanFactory = this.getBean(beanDefinition.getFactoryBeanName());
        Method m = beanFactory.getClass().getMethod(beanDefinition.getFactoryMethodName(), null);
        return m.invoke(beanFactory, null);
    }

    private void initialize(BeanDefinition beanDefinition, Object instance) throws Exception {

        if (Strings.isNullOrEmpty(beanDefinition.getInitMethodName()))
            return;
        // user object ==> User.class
        Method m = instance.getClass().getMethod(beanDefinition.getInitMethodName(), null);
        m.invoke(instance, null);
    }

}

