package poiati.bobby;


import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;


// TODO Exception if node not found

public class Neo4JConnectionManager extends Neo4JService implements ConnectionManager {
    public Neo4JConnectionManager(final GraphDatabaseService graphDb) {
        super(graphDb);
    }

    public void connectFriend(final Integer fromFacebookId, final Integer toFacebookId) {
        this.connect(fromFacebookId, toFacebookId, ConnectionType.KNOWS);
    }

    public void connectFriendSuggestion(final Integer fromFacebookId, final Integer toFacebookId) {
        this.connect(fromFacebookId, toFacebookId, ConnectionType.SUGGESTED);
    }

    private void connect(final Integer fromFacebookId, final Integer toFacebookId, final ConnectionType connectionType) {
        final Transaction transaction = graphDb.beginTx();

        try {
            final Node fromNode = this.getIndexedNode(fromFacebookId);
            final Node toNode = this.getIndexedNode(toFacebookId);

            fromNode.createRelationshipTo(toNode, connectionType);

            transaction.success();
        } catch(final Exception e) {
            transaction.failure();
        } finally {
            transaction.finish();
        }
    }
}
