package testclient;

import java.util.List;

/**
 * Created by chiragjain on 8/6/20.
 */
public class Employee {

  String name;
  int age;
  Address address;
  List<Project> projects;

  public Employee(String name, int age, Address address,List<Project> projects) {
    this.name = name;
    this.age = age;
    this.address = address;
    this.projects=projects;
  }
}
