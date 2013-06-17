package poiati.bobby;


import org.junit.Test;
import org.junit.Before;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Direction;


public class Neo4JConnectionManagerTest extends Neo4JIntegrationTestBase {
    Neo4JConnectionManager connectionManager;
    Integer fromFacebookId = 123;
    Integer toFacebookId = 456;

    @Before
    public void setUp() {
        super.setUp();
        this.connectionManager = new Neo4JConnectionManager(this.graphDb);
    }

    @Test
    public void testConnectFriend() {
        this.prepareDatabase();

        this.connectionManager.connectFriend(fromFacebookId, toFacebookId);

        Node nedNode = this.getIndexedNode(fromFacebookId);
        Node robNode = this.getIndexedNode(toFacebookId);

        this.assertConnected(nedNode, robNode, ConnectionType.KNOWS);
        this.assertConnected(robNode, nedNode, ConnectionType.KNOWS);
    }

    @Test
    public void testConnectFriendSuggestion() {
        this.prepareDatabase();

        this.connectionManager.connectFriendSuggestion(fromFacebookId, toFacebookId);

        Node nedNode = this.getIndexedNode(fromFacebookId);
        Node robNode = this.getIndexedNode(toFacebookId);

        this.assertConnected(nedNode, robNode, ConnectionType.SUGGESTED);
        this.assertConnected(robNode, nedNode, ConnectionType.SUGGESTED);
    }

    // TODO This test is not isolated because of the usage of Neo4JPersonRepository and the connectFriend method.
    @Test
    public void testUpdateSuggestions() {
        PersonRepository personRepository = new Neo4JPersonRepository(this.graphDb);
        personRepository.create(NED);
        personRepository.create(ROBERT);
        personRepository.create(JAMES);
        personRepository.create(TYRION);
        this.connectionManager.connectFriend(NED.getFacebookId(), ROBERT.getFacebookId());
        this.connectionManager.connectFriend(ROBERT.getFacebookId(), JAMES.getFacebookId());
        this.connectionManager.connectFriend(ROBERT.getFacebookId(), TYRION.getFacebookId());

        this.connectionManager.updateSuggestions();

        Node nedNode = this.getIndexedNode(NED.getFacebookId());
        Node jamesNode = this.getIndexedNode(JAMES.getFacebookId());
        Node tyrionNode = this.getIndexedNode(TYRION.getFacebookId());

        this.assertConnected(nedNode, jamesNode, ConnectionType.SUGGESTED);
        this.assertConnected(nedNode, tyrionNode, ConnectionType.SUGGESTED);
        this.assertConnected(jamesNode, nedNode, ConnectionType.SUGGESTED);
        this.assertConnected(tyrionNode, nedNode, ConnectionType.SUGGESTED);
        this.assertConnected(jamesNode, tyrionNode, ConnectionType.SUGGESTED);
        this.assertConnected(tyrionNode, jamesNode, ConnectionType.SUGGESTED);
    }

    // TODO This test is not isolated because of the usage of Neo4JPersonRepository.
    private void prepareDatabase() {
        PersonRepository personRepository = new Neo4JPersonRepository(this.graphDb);
        personRepository.create(new Person("Ned", this.fromFacebookId));
        personRepository.create(new Person("Rob", this.toFacebookId));
    }

    private void assertConnected(Node nodeOne, Node nodeTwo, ConnectionType connectionType) {
        for (Relationship relationship : nodeOne.getRelationships(connectionType, Direction.BOTH)) {
            if (relationship.getOtherNode(nodeOne).equals(nodeTwo)) {
                return;
            }
        }
        assert false;
    }
}
