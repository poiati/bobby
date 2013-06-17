package poiati.bobby;


import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.kernel.Traversal;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.graphdb.traversal.Traverser;
import org.neo4j.graphdb.traversal.Evaluators;
import org.neo4j.graphdb.Direction;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Component
public class Neo4JConnectionManager extends Neo4JService implements ConnectionManager {

    final transient Logger logger = LoggerFactory.getLogger(Neo4JConnectionManager.class);

    @Autowired
    public Neo4JConnectionManager(final GraphDatabaseService graphDb) {
        super(graphDb);
    }

    public void connectFriend(final Integer fromFacebookId, final Integer toFacebookId) {
        this.connect(fromFacebookId, toFacebookId, ConnectionType.KNOWS);
    }

    public void connectFriendSuggestion(final Integer fromFacebookId, final Integer toFacebookId) {
        this.connect(fromFacebookId, toFacebookId, ConnectionType.SUGGESTED);
    }

    public void updateSuggestions() {
        final Transaction transaction = graphDb.beginTx();
        try {
            this.traverseAllNodesAndUpdateSuggestions();
            transaction.success();
        } catch(final Exception e) {
            transaction.failure();
            logger.error("Error while updating suggestions.");
            throw e;
        } finally {
            transaction.finish();
        }
    }

    private void traverseAllNodesAndUpdateSuggestions() {
        for (final Node node : graphDb.getAllNodes()) {
            this.findAndCreateSuggestions(node);
        }
    }

    private void findAndCreateSuggestions(final Node node) {
        if (!node.hasRelationship(ConnectionType.KNOWS)) {
            return;
        }

        final Traverser traverser = friendToSuggestionTraversalDescription().traverse(node);
        for (final Node suggestionNode : traverser.nodes()) {
            if (!this.isSuggestionRelationshipPresent(node, suggestionNode)) {
                node.createRelationshipTo(suggestionNode, ConnectionType.SUGGESTED);
                if (logger.isInfoEnabled()) {
                    logger.info("New Suggestion Between {} and {}", node.getPropertyValues(), suggestionNode.getPropertyValues());
                }
            }
        }
    }

    private boolean isSuggestionRelationshipPresent(final Node node, final Node otherNode) {
        for (final Relationship relationship : node.getRelationships(ConnectionType.SUGGESTED, Direction.BOTH)) {
            if (relationship.getOtherNode(node).equals(otherNode)) {
                return true;
            }
        }
        return false;
    }

    private void connect(final Integer fromFacebookId, final Integer toFacebookId, final ConnectionType connectionType) {
        final Transaction transaction = graphDb.beginTx();
        try {
            final Node fromNode = this.getIndexedNode(fromFacebookId);
            final Node toNode = this.getIndexedNode(toFacebookId);

            if (fromNode == null || toNode == null) {
                throw new IllegalStateException("One or both sides of the connection does not exists.");
            }

            fromNode.createRelationshipTo(toNode, connectionType);

            if (logger.isInfoEnabled()) {
                logger.info("{} is now Connected to {} with the {} relation.", 
                        fromNode.getProperty(Neo4JPersonRepository.PROPERTY_NAME),
                        toNode.getProperty(Neo4JPersonRepository.PROPERTY_NAME),
                        connectionType);
            }

            transaction.success();
        } catch(final Exception e) {
            transaction.failure();
            logger.error("Error connecting " + fromFacebookId + " to " + toFacebookId + ".", e);
            throw e;
        } finally {
            transaction.finish();
        }
    }

    private TraversalDescription friendToSuggestionTraversalDescription() {
        return Traversal.description().
            breadthFirst().
            relationships(ConnectionType.KNOWS).
            evaluator(Evaluators.excludeStartPosition()).
            evaluator(Evaluators.atDepth(2));
    }
}
