package seedu.address.model.person;

import static java.util.Objects.requireNonNull;

public class MatriculationNumber {
    public final String value;
    private static final String DOMAIN_FIRST_CHARACTER_REGEX = "A";
    private static final String DOMAIN_MIDDLE_REGEX = "[0-9]{7,8}";
    private static final String DOMAIN_LAST_CHARACTER_REGEX = "[A-Z]{1,2}";
    public static final String VALIDATION_REGEX = DOMAIN_FIRST_CHARACTER_REGEX
            + DOMAIN_MIDDLE_REGEX + DOMAIN_LAST_CHARACTER_REGEX;

    public static final String MESSAGE_CONSTRAINTS = "Matriculation Number should be in the format A1234567Z "
            + "and adhere to the following constraints:\n"
            + "1. The prefix should be A.\n"
            + "2. The domain middle portion should contain 7 numerical digits.\n"
            + "3. The last character is an alphabet character between A-Z.";

    /**
     * Constructs a {@code MatriculationNumber}
     *
     * @param matriculationNumber A valid Matriculation Number.
     */
    public MatriculationNumber(String matriculationNumber) {
        requireNonNull(matriculationNumber);
        isValidMatriculationNumber(matriculationNumber);
        value = matriculationNumber;
    }

    /**
     * Returns if a given string is a valid Matriculation Number.
     */
    public static boolean isValidMatriculationNumber(String test) {
        return test.matches(VALIDATION_REGEX);
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        return other == this
                || (other instanceof MatriculationNumber
                && value.equals(((MatriculationNumber) other).value));
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}

