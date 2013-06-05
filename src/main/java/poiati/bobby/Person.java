package poiati.bobby;


public class Person {
    private final String name;
    private final Integer facebookId;

    public Person(final String name, final Integer facebookId) {
        this.name = name;
        this.facebookId = facebookId;
    }

    public String getName() {
        return this.name;
    }

    public Integer getFacebookId() {
        return this.facebookId;
    }
}
