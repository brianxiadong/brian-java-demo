package com.brianxia.reflection.instropector;

/**
 * @author brianxia
 * @version 1.0
 * @date 2021/3/17 19:28
 */
public class Target {

    private String name;

    private long age;

    public long getAge() {
        return age;
    }

    public void setAge(long age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Target{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}
