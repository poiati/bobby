package poiati.bobby;


import org.junit.Test;
import org.junit.Before;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Direction;


public class Neo4JConnectionManagerTest extends Neo4JIntegrationTestBase {
    Neo4JConnectionManager connectionManager;
    Integer fromFacebookId = 123;
    Integer toFacebookId = 456;

    @Before
    public void setUp() {
        super.setUp();
        this.connectionManager = new Neo4JConnectionManager(this.graphDb);
        this.prepareDatabase();
    }

    @Test
    public void testConnectFriend() {
        this.connectionManager.connectFriend(fromFacebookId, toFacebookId);

        Node nedNode = this.getIndexedNode(fromFacebookId);
        Node robNode = this.getIndexedNode(toFacebookId);

        this.assertConnected(nedNode, robNode, ConnectionType.KNOWS);
        this.assertConnected(robNode, nedNode, ConnectionType.KNOWS);
    }

    @Test
    public void testConnectFriendSuggestion() {
        this.connectionManager.connectFriendSuggestion(fromFacebookId, toFacebookId);

        Node nedNode = this.getIndexedNode(fromFacebookId);
        Node robNode = this.getIndexedNode(toFacebookId);

        this.assertConnected(nedNode, robNode, ConnectionType.SUGGESTED);
        this.assertConnected(robNode, nedNode, ConnectionType.SUGGESTED);
    }

    // TODO This test is not isolated because of the usage of Neo4JPersonRepository.
    private void prepareDatabase() {
        PersonRepository personRepository = new Neo4JPersonRepository(this.graphDb);
        personRepository.create(new Person("Ned", this.fromFacebookId));
        personRepository.create(new Person("Rob", this.toFacebookId));
    }

    private void assertConnected(Node nodeOne, Node nodeTwo, ConnectionType connectionType) {
        Node otherNode = nodeOne.getSingleRelationship(connectionType, Direction.BOTH).getOtherNode(nodeOne);

        assertThat(otherNode, is(nodeTwo));
    }
}
