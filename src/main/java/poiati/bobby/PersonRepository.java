package poiati.bobby;


import java.util.Set;


public interface PersonRepository {

    void create(Person person);

    Set<Person> friendsFor(Integer facebookId); 

    Set<Person> suggestionsFor(Integer facebookId); 

}
