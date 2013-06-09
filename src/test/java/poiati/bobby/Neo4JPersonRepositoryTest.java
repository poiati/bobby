package poiati.bobby;


import org.junit.Before;
import org.junit.After;
import org.junit.Test;

import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.neo4j.graphdb.Node;


public class Neo4JPersonRepositoryTest extends Neo4JIntegrationTestBase {
    public static final Person NED = new Person("Ned", 999);
    public static final Person ROB = new Person("Rob", 111);
    public static final Person ROBERT = new Person("Robert", 222);
    public static final Person ARYA = new Person("Arya", 333);
    public static final Person JAMES = new Person("James", 444);

    Neo4JPersonRepository personRepository;
    String name = "Ned";
    Integer facebookId = 999;

    @Before
    public void setUp() {
        super.setUp();
        this.personRepository = new Neo4JPersonRepository(graphDb);
    }

    @Test
    public void testCreate() {
        this.personRepository.create(new Person(this.name, this.facebookId));

        Node userNode = this.getIndexedNode(facebookId);
        assertThat((String) userNode.getProperty(Neo4JPersonRepository.PROPERTY_NAME), is(this.name));
    }

    @Test
    public void testFriendsFor() {
        this.prepareDatabase();

        Set<Person> friendsOfNed = this.personRepository.friendsFor(this.facebookId);

        assertThat(friendsOfNed.size(), is(3));
        assertThat(friendsOfNed, hasItems(ROB, ROBERT, ARYA));
    }

    @Test
    public void testFriendsInverseRelation() {
        this.prepareDatabase();

        Set<Person> friendsOfArya = this.personRepository.friendsFor(ARYA.getFacebookId());

        assertThat(friendsOfArya.size(), is(1));
        assertThat(friendsOfArya, hasItems(NED));
    }

    @Test
    public void testSuggestionFor() {
        this.prepareDatabase();

        Set<Person> suggestionsForNed = this.personRepository.suggestionsFor(this.facebookId);

        assertThat(suggestionsForNed.size(), is(1));
        assertThat(suggestionsForNed, hasItems(JAMES));
    }

    @Test
    public void testSuggestionForInverse() {
        this.prepareDatabase();

        Set<Person> suggestionsForJames = this.personRepository.suggestionsFor(JAMES.getFacebookId());

        assertThat(suggestionsForJames.size(), is(1));
        assertThat(suggestionsForJames, hasItems(NED));
    }

    // TODO This test is not isolated because of the usage of Neo4JPersonRepository.
    private void prepareDatabase() {
        PersonRepository personRepository = new Neo4JPersonRepository(this.graphDb);
        personRepository.create(NED);
        personRepository.create(ROB);
        personRepository.create(ROBERT);
        personRepository.create(ARYA);
        personRepository.create(JAMES);

        ConnectionManager connectionManager = new Neo4JConnectionManager(this.graphDb);
        connectionManager.connectFriend(this.facebookId, ROB.getFacebookId());
        connectionManager.connectFriend(this.facebookId, ROBERT.getFacebookId());
        connectionManager.connectFriend(this.facebookId, ARYA.getFacebookId());
        connectionManager.connectFriendSuggestion(this.facebookId, JAMES.getFacebookId());
    }
}
