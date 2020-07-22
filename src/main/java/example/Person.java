package example;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;

@Entity
public class Person {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  private String name;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "person", insertable = false, updatable = false)
  @OrderColumn(name = "pos")
  private List<Nickname> nicknames = new ArrayList<>();

  protected Person() {
  }

  public Person(String name) {
    this.name = name;
  }

  public void addNickname(Nickname nickname) {
    this.nicknames.add(nickname);
  }

  public List<Nickname> getNicknames() {
    return nicknames;
  }

  public int getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Person)) {
      return false;
    }
    Person person = (Person) o;
    return getName().equals(person.getName());
  }

  @Override
  public int hashCode() {
    return getName().hashCode();
  }
}
