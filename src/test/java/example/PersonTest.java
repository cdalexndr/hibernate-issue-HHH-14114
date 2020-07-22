package example;

import static org.testng.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.CacheType;
import org.springframework.boot.test.autoconfigure.core.AutoConfigureCache;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@SpringBootTest
@TestPropertySource(properties = {
    "logging.level.org.hibernate.SQL=DEBUG",
    "logging.level.org.hibernate.type=TRACE"
})
public class PersonTest extends AbstractTransactionalTestNGSpringContextTests {

  private static int idGen = 0;
  @Autowired
  PersonRepository personRepository;
  @Autowired
  EntityManager entityManager;

  private void insertNickName(int personId, String nickname, int order) {
    entityManager.createNativeQuery(
        "insert into Nickname(id, person, value, pos)"
            + " values(:id, :person, :nickname, :order)")
        .setParameter("id", ++idGen)
        .setParameter("person", personId)
        .setParameter("nickname", nickname)
        .setParameter("order", order)
        .executeUpdate();
  }

  private int insertPerson(String name) {
    int id = ++idGen;
    entityManager.createNativeQuery(
        "insert into Person(id, name)"
            + " values(:id,:name)")
        .setParameter("id", id)
        .setParameter("name", name)
        .executeUpdate();
    return id;
  }

  @Test
  public void testValidPos() {
    int personId = insertPerson("person");
    insertNickName(personId, "nick1", 0);
    insertNickName(personId, "nick2", 1);

    Person person = personRepository.findById(personId).orElseThrow();
    assertEquals(person.getNicknames().stream()
            .map(Nickname::getValue)
            .collect(Collectors.toSet()),
        Set.of("nick1", "nick2"));
  }

  @Test
  public void testOverlapPos() {
    int personId = insertPerson("person");
    insertNickName(personId, "nick1", 0);
    insertNickName(personId, "nick2", 0);

    Person person = personRepository.findById(personId).orElseThrow();
    assertEquals(person.getNicknames().stream()
            .map(Nickname::getValue)
            .collect(Collectors.toSet()),
        Set.of("nick1", "nick2"));
  }
}
