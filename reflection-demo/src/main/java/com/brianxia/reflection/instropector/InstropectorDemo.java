package com.brianxia.reflection.instropector;

import org.springframework.beans.BeanUtils;

import javax.xml.ws.spi.Invoker;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

/**
 * @author brianxia
 * @version 1.0
 * @date 2021/3/17 19:22
 */
public class InstropectorDemo {

    public static Map<String,PropertyDescriptor> sourcePd = new HashMap<>();

    public static void createPd(Object source) throws IntrospectionException {
        sourcePd.clear();
        //获取BeanInfo
        BeanInfo beanInfo = Introspector.getBeanInfo(source.getClass());
        //获取属性描述信息
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();

        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            sourcePd.put(propertyDescriptor.getName(),propertyDescriptor);
        }
    }

    public static PropertyDescriptor getPd(String name) {
        if(!sourcePd.containsKey(name)){
            return null;
        }

        return sourcePd.get(name);
    }

    /**
     *
     * @param source  the source bean
     * @param target  the target bean
     */
    public static void copyProperties(Object source,Object target) throws IntrospectionException, InvocationTargetException, IllegalAccessException {
        //获取BeanInfo
        BeanInfo beanInfo = Introspector.getBeanInfo(target.getClass());
        //获取属性描述信息
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();

        //创建source的描述信息map
        createPd(source);

        //遍历属性描述信息，进行copy
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {

            String name = propertyDescriptor.getName();
            PropertyDescriptor sourcePd = getPd(name);
            //如果source没有对应属性，直接continue
            if(sourcePd == null){
                continue;
            }

            //获取getter和setter方法
            Method writeMethod = propertyDescriptor.getWriteMethod();
            Method readMethod = sourcePd.getReadMethod();

            //授予权限 private也可以访问
            if(writeMethod != null && readMethod != null){
                if(!Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers())){
                    writeMethod.setAccessible(true);
                }

                if(!Modifier.isPublic(readMethod.getDeclaringClass().getModifiers())){
                    readMethod.setAccessible(true);
                }

                //复制属性
                Object invoke = readMethod.invoke(source);
                writeMethod.invoke(target,invoke);
            }
        }
    }

    public static void main(String[] args) throws IntrospectionException, InvocationTargetException, IllegalAccessException {
        Source source = new Source();
        source.setName("张三");
        source.setAge(18);
        Target target = new Target();

        long start = System.currentTimeMillis();
        for (int i = 0; i < 1000000; i++) {
            target.setAge(source.getAge());
            target.setName(source.getName());
            //copyProperties(source,target);
            //BeanUtils.copyProperties(source,target);
            //org.apache.commons.beanutils.BeanUtils.copyProperties(target,source);
        }
        long end = System.currentTimeMillis();
        System.out.println(end - start);
        //System.out.println(target);

    }
}
