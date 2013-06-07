package poiati.bobby;


import org.neo4j.graphdb.RelationshipType;


public enum ConnectionType implements RelationshipType {
    KNOWS,
    SUGGESTED
}
