package com.predictor.counter;


public class Person {
    public final long id;
    public final Sex sex;
    public final String name;
    public final int age;

    public Person(long id, Sex sex, String name,  int age) {
        this.id = id;
        this.sex = sex;
        this.name = name;
        this.age = age;
    }

    @Override
    public String toString(){
        return String.format("Person:{id:%d, sex:\"%s\", name:\"%s\", age:%d}",id, (sex.equals(Sex.MALE))?"MALE":"FEMALE", name, age );
    }

    enum Sex{
        MALE,
        FEMALE
    }
}
