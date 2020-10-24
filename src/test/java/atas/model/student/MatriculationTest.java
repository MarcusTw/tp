package atas.model.student;

import static atas.testutil.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class MatriculationTest {

    public static final Matriculation VALID_MATRICULATION = new Matriculation("A0159980Z");
    public static final Matriculation VALID_MATRICULATION_COPY = new Matriculation(VALID_MATRICULATION.value);
    public static final Matriculation DIFFERENT_VALID_MATRICULATION = new Matriculation("A0169980Z");

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Matriculation(null));
    }

    @Test
    public void constructor_invalidMatriculation_throwsIllegalArgumentException() {
        String invalidMatriculation = "";
        assertThrows(IllegalArgumentException.class, () -> new Matriculation(invalidMatriculation));
    }

    @Test
    public void isValidMatriculation() {
        // null Matriculation number
        assertThrows(NullPointerException.class, () -> Matriculation.isValidMatriculation(null));

        // invalid Matriculation numbers
        assertFalse(Matriculation.isValidMatriculation("")); // empty string
        assertFalse(Matriculation.isValidMatriculation(" ")); // spaces only
        assertFalse(Matriculation.isValidMatriculation("1234567")); // only numbers
        assertFalse(Matriculation.isValidMatriculation("Matriculation")); // non-numeric
        assertFalse(Matriculation.isValidMatriculation("1234567X")); // missing prefix
        assertFalse(Matriculation.isValidMatriculation("A123456X")); // missing number
        assertFalse(Matriculation.isValidMatriculation("A1234567")); // missing suffix

        // valid Matriculation numbers
        assertTrue(Matriculation.isValidMatriculation("A1234567X")); // standard
        assertTrue(Matriculation.isValidMatriculation("A2534564F")); // random
        assertTrue(Matriculation.isValidMatriculation("A0000000X")); // full of zeros
    }

    @Test
    public void equals() {
        // this == other
        assertEquals(VALID_MATRICULATION, VALID_MATRICULATION);
        assertEquals(VALID_MATRICULATION_COPY, VALID_MATRICULATION_COPY);
        assertEquals(DIFFERENT_VALID_MATRICULATION, DIFFERENT_VALID_MATRICULATION);

        // same matriculation value
        assertEquals(VALID_MATRICULATION_COPY, VALID_MATRICULATION);

        // different matriculation value
        assertNotEquals(DIFFERENT_VALID_MATRICULATION, VALID_MATRICULATION);
        assertNotEquals(DIFFERENT_VALID_MATRICULATION, VALID_MATRICULATION_COPY);
    }
}
