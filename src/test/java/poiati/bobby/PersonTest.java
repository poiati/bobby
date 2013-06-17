package poiati.bobby;


import org.junit.Test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class PersonTest {

    @Test
    public void testEquality() {
        Person ned = new Person("Ned", 123);
        assertThat(ned, is(new Person("Ned", 123)));
    }

    public void testToString() {
        Person ned = new Person("Ned", 123);
        assertThat(ned.toString(), is("Ned < 123 >"));
    }

}
