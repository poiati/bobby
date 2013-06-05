package poiati.bobby;


import org.junit.Before;
import org.junit.After;
import org.junit.Test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.test.TestGraphDatabaseFactory;
import org.neo4j.graphdb.index.Index;


public class Neo4JPersonRepositoryTest {
    GraphDatabaseService graphDb;
    PersonRepository personRepository;
    String name = "Ned";
    Integer facebookId = 999;

    @Before
    public void setUp() {
        this.graphDb = new TestGraphDatabaseFactory().newImpermanentDatabase();
        this.personRepository = new Neo4JPersonRepository(graphDb);
    }

    @After
    public void tearDown() {
        this.graphDb.shutdown();
    }

    @Test
    public void testCreate() {
        this.personRepository.create(new Person(name, facebookId));

        Index<Node> index = this.graphDb.index().forNodes(Neo4JPersonRepository.INDEX_NAME);
        Node userNode = index.get(Neo4JPersonRepository.PROPERTY_FACEBOOKID, this.facebookId).getSingle();
        assertThat((String) userNode.getProperty(Neo4JPersonRepository.PROPERTY_NAME), is(this.name));
    }
}
