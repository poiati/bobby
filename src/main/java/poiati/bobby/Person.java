package poiati.bobby;


public class Person {
    private final String name;
    private final Integer facebookId;

    public Person(final String name, final Integer facebookId) {
        if (name == null || facebookId == null)
            throw new IllegalArgumentException();
        this.name = name;
        this.facebookId = facebookId;
    }

    public String getName() {
        return this.name;
    }

    public Integer getFacebookId() {
        return this.facebookId;
    }

    public boolean equals(final Object other) {
        if (other == this) {
            return true;
        }
        if (other == null || !(other instanceof Person) ) {
            return false;
        }
        final Person otherPerson = (Person) other;
        return this.name.equals(otherPerson.name) && this.facebookId.equals(otherPerson.facebookId);
    }

    public int hashCode() {
        return 17 + this.name.hashCode() + this.facebookId.hashCode();
    }

    public String toString() {
        return String.format("%s < %d >", this.name, this.facebookId);
    }
}
