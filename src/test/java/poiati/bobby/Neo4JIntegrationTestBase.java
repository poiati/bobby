package poiati.bobby;


import org.junit.After;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.test.TestGraphDatabaseFactory;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.index.Index;


public class Neo4JIntegrationTestBase {
    public static final Person NED = new Person("Ned", 999);
    public static final Person ROB = new Person("Rob", 111);
    public static final Person ROBERT = new Person("Robert", 222);
    public static final Person ARYA = new Person("Arya", 333);
    public static final Person JAMES = new Person("James", 444);
    public static final Person TYRION = new Person("Tyrion", 555);

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
