package poiati.bobby;


import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;


class Neo4JPersonRepository implements PersonRepository {
    public static final String PROPERTY_NAME = "name";
    public static final String PROPERTY_FACEBOOKID = "facebookId";
    public static final String INDEX_NAME = "persons";

    private final GraphDatabaseService graphDb;

    public Neo4JPersonRepository(final GraphDatabaseService graphDb) {
        this.graphDb = graphDb;
    }

    public void create(final Person person) {
        final Transaction transaction = graphDb.beginTx();
        try {
            final Node personNode = this.graphDb.createNode();
            personNode.setProperty(PROPERTY_NAME, person.getName());
            personNode.setProperty(PROPERTY_FACEBOOKID, person.getFacebookId());

            this.indexNode(personNode);

            transaction.success();
        } catch(final Exception e) {
            transaction.failure();
        } finally {
            transaction.finish();
        }
    }

    private void indexNode(final Node personNode) {
        final Index<Node> index = graphDb.index().forNodes(INDEX_NAME);
        index.add(personNode, PROPERTY_FACEBOOKID, personNode.getProperty(PROPERTY_FACEBOOKID));
    }
}
