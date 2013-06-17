package poiati.bobby;


import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.index.Index;


public abstract class Neo4JService {
    protected final GraphDatabaseService graphDb;

    protected Neo4JService(final GraphDatabaseService graphDb) {
        this.graphDb = graphDb;
    }

    protected Node getIndexedNode(Integer facebookId) {
        final Index<Node> index = this.graphDb.index().forNodes(Neo4JPersonRepository.INDEX_NAME);
        return index.get(Neo4JPersonRepository.PROPERTY_FACEBOOKID, facebookId).getSingle();
    }

}
