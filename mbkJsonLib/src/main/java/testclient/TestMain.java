package testclient;

import com.mobikwik.testcode.Mobikwik;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by chiragjain on 8/6/20.
 */
public class TestMain {

  public static void main(String[] args) {
    Mobikwik g = new Mobikwik();
    Address address=new Address("Gurgaon",123456,"golf course road");
    List<Project> projects=new ArrayList();
    projects.add(new Project("Insurance",123));
    projects.add(new Project("Recharge",456));
    Employee employee=new Employee("chirag",30,address,projects);
    System.out.println(g.toJson(employee));
  }

}