package utils;

import lombok.Data;
import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.InvocationTargetException;

public class Set{
    public static void main(String[] args) throws InvocationTargetException, IllegalAccessException
    {
        Employee employee = new Employee();
        employee.setName("A");
        employee.setAge(10);

        Employee employee2 = new Employee();
         BeanUtils.copyProperties(employee,employee2);

        System.out.println(employee);
        System.out.println(employee2);

        employee2.setAge(18);//浅拷贝
        System.out.println(employee);
        System.out.println(employee2);


        EmployeeF employeef = new EmployeeF();
         BeanUtils.copyProperties(employee,employeef);

        System.out.println(employeef);
    }
}
@Data
class Employee
{
    private String name;

    private Integer age;
}
@Data
class EmployeeF extends Employee
{
    private String name;

    private Integer age;

    private boolean sex;

    private F father;
}
@Data
class F
{
    private String F1;

    private Integer F2;
}