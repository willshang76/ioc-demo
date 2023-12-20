package beanfactory;

import com.will.springioc.sample.DemoBeanFactory;
import org.junit.AfterClass;
import org.junit.Test;

import com.will.springioc.bean.BeanDefinition;
import com.will.springioc.bean.MyBeanFactory;
import com.will.springioc.bean.MyGenericBeanDefinition;
import com.will.springioc.sample.DemoBean;

public class DemoBeanFactoryTest {
    static MyBeanFactory myBeanFactory = new MyBeanFactory();
    
    @Test
    public void beanRegistry_createViaConstructor_succeed() throws Exception {

        MyGenericBeanDefinition beanDefinition = new MyGenericBeanDefinition();

        beanDefinition.setBeanClass(DemoBean.class);
        beanDefinition.setScope(BeanDefinition.Scope.SINGLETON);

        beanDefinition.setInitMethodName("initialize");
        beanDefinition.setDestroyMethodName("destroy");

        myBeanFactory.registerBeanDefinition("MyBeanViaConstructor", beanDefinition);

    }

    @Test
    public void beanRegistry_createBeanViaStaticFactoryMethod_succeed() throws Exception {
        MyGenericBeanDefinition beanDefinition = new MyGenericBeanDefinition();
        beanDefinition.setBeanClass(DemoBeanFactory.class);
        beanDefinition.setFactoryMethodName("getDemoBeanStatic");
        myBeanFactory.registerBeanDefinition("MyBeanViaStaticFactoryMethod", beanDefinition);
    }

    @Test
    public void beanRegistry_createBeanViaFactoryMethod_succeed() throws Exception {
        // Register bean factory.
        String factoryName = "MyBeanFactory";
        MyGenericBeanDefinition beanFactoryDefinition = new MyGenericBeanDefinition();
        beanFactoryDefinition.setBeanClass(DemoBeanFactory.class);
        myBeanFactory.registerBeanDefinition(factoryName, beanFactoryDefinition);

        // Register the bean
        MyGenericBeanDefinition beanDefinition = new MyGenericBeanDefinition();
        beanDefinition.setFactoryBeanName(factoryName);
        beanDefinition.setFactoryMethodName("getDemoBean");
        myBeanFactory.registerBeanDefinition("MyBeanViaFactoryMethod", beanDefinition);
    }

    @AfterClass
    public static void verifyGetBean() throws Exception {

        DemoBean demoBeanViaConstructor = (DemoBean) myBeanFactory.getBean("MyBeanViaConstructor");
        demoBeanViaConstructor.peformAction();

        DemoBean demoBeanViaConstructor2 = (DemoBean) myBeanFactory.getBean("MyBeanViaConstructor");
        demoBeanViaConstructor2.peformAction();

        System.out.println("demoBeanViaConstructor.equals(demoBeanViaConstructor2) = " + demoBeanViaConstructor.equals(demoBeanViaConstructor2));

        DemoBean demoBeanViaFactoryStatic = (DemoBean) myBeanFactory.getBean("MyBeanViaStaticFactoryMethod");
        demoBeanViaFactoryStatic.peformAction();

        DemoBean demoBeanViaFactoryStatic2 = (DemoBean) myBeanFactory.getBean("MyBeanViaStaticFactoryMethod");
        demoBeanViaFactoryStatic2.peformAction();

        DemoBean demoBeanViaFactory = (DemoBean) myBeanFactory.getBean("MyBeanViaFactoryMethod");
        demoBeanViaFactory.peformAction();

        DemoBean demoBeanViaFactory2 = (DemoBean) myBeanFactory.getBean("MyBeanViaFactoryMethod");
        demoBeanViaFactory2.peformAction();

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("Runtime shut down hook is triggered.");
                try {
                    myBeanFactory.close();
                } catch (Exception exception) {
                    System.out.println(exception);
                }
            }
        }));

    }


}
