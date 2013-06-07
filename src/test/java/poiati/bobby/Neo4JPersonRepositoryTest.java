package poiati.bobby;


import org.junit.Before;
import org.junit.After;
import org.junit.Test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.neo4j.graphdb.Node;


public class Neo4JPersonRepositoryTest extends Neo4JIntegrationTestBase {
    Neo4JPersonRepository personRepository;
    String name = "Ned";
    Integer facebookId = 999;

    @Before
    public void setUp() {
        super.setUp();
        this.personRepository = new Neo4JPersonRepository(graphDb);
    }

    @Test
    public void testCreate() {
        this.personRepository.create(new Person(name, facebookId));

        Node userNode = this.getIndexedNode(facebookId);
        assertThat((String) userNode.getProperty(Neo4JPersonRepository.PROPERTY_NAME), is(this.name));
    }
}
