package io.vantiq.ext;

import java.util.Arrays;

public class Employee {

    public String name;
    public int age;
    public String[] emails;
    public Employee boss;

    @Override
    public String toString() {
        return "Employee{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", emails=" + Arrays.toString(emails) +
                ", boss=" + boss +
                '}';
    }
}
