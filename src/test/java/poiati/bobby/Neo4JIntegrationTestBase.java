package poiati.bobby;


import org.junit.After;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.test.TestGraphDatabaseFactory;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.index.Index;


public class Neo4JIntegrationTestBase {
    GraphDatabaseService graphDb;

    public void setUp() {
        this.graphDb = new TestGraphDatabaseFactory().newImpermanentDatabase();
    }

    @After
    public void tearDown() {
        this.graphDb.shutdown();
    }

    protected Node getIndexedNode(Integer facebookId) {
        Index<Node> index = this.graphDb.index().forNodes(Neo4JPersonRepository.INDEX_NAME);
        return index.get(Neo4JPersonRepository.PROPERTY_FACEBOOKID, facebookId).getSingle();
    }
}
