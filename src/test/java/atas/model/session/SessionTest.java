package atas.model.session;

import static atas.testutil.Assert.assertThrows;
import static atas.testutil.TypicalAttributes.ABSENT_BUT_HAS_PARTICIPATED;
import static atas.testutil.TypicalAttributes.PRESENT_AND_HAS_PARTICIPATED;
import static atas.testutil.TypicalAttributes.PRESENT_BUT_HAS_NOT_PARTICIPATED;
import static atas.testutil.TypicalSessions.SESSIONDATE_WEEK_ONE;
import static atas.testutil.TypicalSessions.SESSIONDATE_WEEK_THREE;
import static atas.testutil.TypicalSessions.SESSIONDATE_WEEK_TWO;
import static atas.testutil.TypicalSessions.SESSIONNAME_WEEK_ONE;
import static atas.testutil.TypicalSessions.SESSIONNAME_WEEK_THREE;
import static atas.testutil.TypicalSessions.SESSIONNAME_WEEK_TWO;
import static atas.testutil.TypicalStudents.getTypicalStudents;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import atas.commons.core.index.Index;
import atas.testutil.TypicalSessions;

class SessionTest {

    private final SessionList sessionList = new SessionList(getTypicalStudents());

    private final Session sessionWeekOne = new Session(SESSIONNAME_WEEK_ONE, SESSIONDATE_WEEK_ONE);
    private final Session sessionWeekTwo = new Session(SESSIONNAME_WEEK_TWO, SESSIONDATE_WEEK_TWO);
    private final Session sessionWeekThree = new Session(SESSIONNAME_WEEK_THREE, SESSIONDATE_WEEK_THREE);

    @BeforeEach
    public void setup() {
        sessionList.addSession(sessionWeekOne);
        sessionList.addSession(sessionWeekTwo);
        sessionList.addSession(sessionWeekThree);
    }

    @Test
    public void isSameSession() {
        // same object -> returns true
        assertTrue(sessionWeekOne.isSameSession(sessionWeekOne));

        // null -> returns false
        assertFalse(sessionWeekOne.isSameSession(null));

        // different session name -> returns false
        assertFalse(sessionWeekOne.isSameSession(sessionWeekTwo));
        assertFalse(sessionWeekTwo.isSameSession(sessionWeekThree));
    }

    @Test
    public void equals() {
        // same object -> returns true
        assertTrue(sessionWeekOne.equals(sessionWeekOne));

        // null -> returns false
        assertFalse(sessionWeekOne.equals(null));

        // same master list but different date -> returns false
        assertFalse(sessionWeekOne.equals(sessionWeekTwo));
        assertFalse(sessionWeekTwo.equals(sessionWeekThree));
    }

    @Test
    public void getEmptyAttributeListFromSession_validEmptySessions_success() {
        Session actual = TypicalSessions.EMPTY_SESSION_1;
        Session expected = TypicalSessions.EMPTY_SESSION_2;
        assertEquals(expected.getAttributeList(), actual.getAttributeList());
    }

    @Test
    public void studentBecomesPresent_validId_success() {
        Index index = Index.fromOneBased(1);
        sessionWeekOne.setStudentAsPresent(index);
        assertEquals(PRESENT_BUT_HAS_NOT_PARTICIPATED, sessionWeekOne.getAttributeList().get(index.getZeroBased()));
    }

    @Test
    public void studentBecomesPresent_nullId_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> sessionWeekOne.setStudentAsPresent(null));
    }

    @Test
    public void studentParticipates_validId_success() {
        Index index = Index.fromOneBased(1);
        sessionWeekTwo.setStudentAsParticipated(index);
        assertEquals(ABSENT_BUT_HAS_PARTICIPATED, sessionWeekTwo.getAttributeList().get(index.getZeroBased()));
    }

    @Test
    public void studentParticipates_nullId_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> sessionWeekOne.setStudentAsParticipated(null));
    }

    @Test
    public void studentBecomesPresentAndParticipates_validId_success() {
        Index index = Index.fromOneBased(1);
        sessionWeekThree.setStudentAsParticipated(index);
        sessionWeekThree.setStudentAsPresent(index);
        assertEquals(PRESENT_AND_HAS_PARTICIPATED, sessionWeekThree.getAttributeList().get(index.getZeroBased()));
    }

    @Test
    public void compareTo() {
        Session actual = TypicalSessions.TUT1;
        Session expected = TypicalSessions.TUT2;
        assertTrue(expected.compareTo(actual) < 0);
    }

}
