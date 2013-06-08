package poiati.bobby;


import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;
import org.neo4j.kernel.Traversal;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.graphdb.traversal.Traverser;
import org.neo4j.graphdb.traversal.Evaluators;

import java.util.Set;
import java.util.HashSet;


class Neo4JPersonRepository extends Neo4JService implements PersonRepository {
    public static final String PROPERTY_NAME = "name";
    public static final String PROPERTY_FACEBOOKID = "facebookId";
    public static final String INDEX_NAME = "persons";

    public Neo4JPersonRepository(final GraphDatabaseService graphDb) {
        super(graphDb);
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

    // TODO Handle node not found (change exception type) and write test
    public Set<Person> friendsFor(final Integer facebookId) {
        final Node personNode = this.getIndexedNode(facebookId);
        System.out.println(facebookId);
        if (personNode == null) {
            throw new IllegalArgumentException();
        }
        final HashSet persons = new HashSet<Person>();
        final Traverser traverser = friendsTraversalDescription().traverse(personNode);
        for (final Node node : traverser.nodes()) {
            System.out.println(node.getProperty("name"));
            persons.add(unmarshal(node));
        }
        return persons;
    }

    private TraversalDescription friendsTraversalDescription() {
        return Traversal.description().
            breadthFirst().
            relationships(ConnectionType.KNOWS).
            evaluator(Evaluators.excludeStartPosition()).
            evaluator(Evaluators.toDepth(1));
    }

    private Person unmarshal(final Node node) {
        return new Person((String) node.getProperty(PROPERTY_NAME), (Integer) node.getProperty(PROPERTY_FACEBOOKID));
    }

    private void indexNode(final Node personNode) {
        final Index<Node> index = graphDb.index().forNodes(INDEX_NAME);
        index.add(personNode, PROPERTY_FACEBOOKID, personNode.getProperty(PROPERTY_FACEBOOKID));
    }
}
