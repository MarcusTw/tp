package atas.model.session;

import static atas.commons.util.AppUtil.checkArgument;
import static java.util.Objects.requireNonNull;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Represents a date of a session in the session list.
 * Guarantees: immutable; is valid as declared in {@link #isValidSessionDate(String)}
 */
public class SessionDate implements Comparable<SessionDate> {

    public static final String MESSAGE_CONSTRAINTS = "The date of a session should be in the form of dd/MM/yyyy "
            + "corresponding to a valid date.\n"
            + "Example : 10/2/2019";
    public static final String VALIDATION_REGEX = "[0-9]{1,2}/[0-9]{1,2}/[0-9]{4,4}";
    public static final String PLACEHOLDER_DATE = "1/1/2020";

    public final LocalDate value;

    /**
     * Constructs a {@code SessionDate}.
     *
     * @param date A valid date.
     */
    public SessionDate(String date) {
        requireNonNull(date);
        checkArgument(isValidSessionDate(date), MESSAGE_CONSTRAINTS);
        value = LocalDate.parse(standardizeDateString(date), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    /**
     * Constructs a {@code SessionDate}.
     *
     * @param date A valid date.
     */
    public SessionDate(LocalDate date) {
        requireNonNull(date);
        value = date;
    }

    public SessionDate getCopy() {
        return new SessionDate(this.value);
    }

    /**
     * Returns if a given string is a valid date.
     */
    public static boolean isValidSessionDate(String date) {
        return date.matches(VALIDATION_REGEX);
    }

    /**
     * Standardize date string into the format of dd/MM/yyyy.
     *
     * This method transforms date strings such as "1/2/1900" into
     * "01/02/1900" for parsing purposes.
     * @param date date string to be processed
     * @return standardized date string
     */
    public String standardizeDateString(String date) {
        String[] dateSegments = date.split("/");
        for (int i = 0; i < 2; i++) {
            if (dateSegments[i].length() == 1) {
                dateSegments[i] = "0" + dateSegments[i];
            }
        }
        return dateSegments[0] + "/" + dateSegments[1] + "/" + dateSegments[2];
    }

    /**
     * Returns a valid SessionDate that can act as a placeholder.
     */
    public static SessionDate getPlaceholderSessionDate() {
        return new SessionDate(PLACEHOLDER_DATE);
    }

    @Override
    public String toString() {
        return value.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof SessionDate // instanceof handles nulls
                && value.equals(((SessionDate) other).value)); // state check
    }

    @Override
    public int compareTo(SessionDate other) {
        return other.value.compareTo(this.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
