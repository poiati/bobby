package poiati.bobby.rest;


import java.util.List;
import java.util.Set;
import java.util.ArrayList;

import poiati.bobby.Person;


public class PersonsJsonMapper {
    private final List<Person> objects = new ArrayList();;

    public PersonsJsonMapper(final Set<Person> persons) {
        if (persons == null)
            throw new IllegalArgumentException("persons can't be null");
        for (final Person person : persons) {
            this.objects.add(person);
        }
    }

    public List<Person> getObjects() {
        return this.objects;
    }
}
