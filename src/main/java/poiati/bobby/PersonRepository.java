package poiati.bobby;


import java.util.Set;


interface PersonRepository {

    void create(Person person);

    Set<Person> friendsFor(Integer facebookId); 

}
