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

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;
import java.util.HashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Component
class Neo4JPersonRepository extends Neo4JService implements PersonRepository {
    final transient Logger logger = LoggerFactory.getLogger(Neo4JConnectionManager.class);

    public static final String PROPERTY_NAME = "name";
    public static final String PROPERTY_FACEBOOKID = "facebookId";
    public static final String INDEX_NAME = "persons";

    @Autowired
    public Neo4JPersonRepository(final GraphDatabaseService graphDb) {
        super(graphDb);
    }

    public void create(final Person person) {
        if (person == null)
            throw new IllegalArgumentException();
        final Transaction transaction = graphDb.beginTx();
        try {
            this.checkIfFacebookIdAlreadyExists(person.getFacebookId());

            final Node personNode = this.graphDb.createNode();
            personNode.setProperty(PROPERTY_NAME, person.getName());
            personNode.setProperty(PROPERTY_FACEBOOKID, person.getFacebookId());

            this.indexNode(personNode);

            transaction.success();

            if (logger.isInfoEnabled()) {
                logger.info("New Person Created: {}", person);
            }
        } catch(final Exception e) {
            transaction.failure();
            logger.error("Can't create person " + person, e);
            throw e;
        } finally {
            transaction.finish();
        }
    }

    public Set<Person> friendsFor(final Integer facebookId) {
        return this.connectionsFor(facebookId, ConnectionType.KNOWS);
    }

    public Set<Person> suggestionsFor(final Integer facebookId) {
        return this.connectionsFor(facebookId, ConnectionType.SUGGESTED);
    }

    private void checkIfFacebookIdAlreadyExists(final Integer facebookId) {
        if (this.getIndexedNode(facebookId) != null) {
            throw new FacebookIdAlreadyExistsException(facebookId);
        }
    }

    private Set<Person> connectionsFor(final Integer facebookId, final ConnectionType connectionType) {
        final Node personNode = this.getIndexedNode(facebookId);
        if (personNode == null) {
            if (logger.isWarnEnabled()) {
                logger.warn("Can't list friends of {} because it does not exists.", facebookId);
            }
            throw new ResourceNotFoundException();
        }
        final HashSet persons = new HashSet<Person>();
        final Traverser traverser = friendsTraversalDescription(connectionType).traverse(personNode);
        for (final Node node : traverser.nodes()) {
            persons.add(unmarshal(node));
        }

        if (logger.isInfoEnabled()) {
            logger.info("Listing connections of type {} for facebook id {}. Result: {}",
                    connectionType,
                    facebookId,
                    persons);
        }

        return persons;
    }

    private TraversalDescription friendsTraversalDescription(final ConnectionType connectionType) {
        return Traversal.description().
            breadthFirst().
            relationships(connectionType).
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
