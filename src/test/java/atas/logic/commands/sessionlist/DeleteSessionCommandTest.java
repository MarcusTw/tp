package atas.logic.commands.sessionlist;

import static atas.logic.commands.CommandTestUtil.assertCommandSuccess;
import static atas.logic.commands.CommandTestUtil.showSessionAtIndex;
import static atas.testutil.Assert.assertThrows;
import static atas.testutil.TypicalIndexes.INDEX_FIRST_SESSION;
import static atas.testutil.TypicalIndexes.INDEX_SECOND_SESSION;
import static atas.testutil.TypicalMemoContents.EMPTY_MEMO_CONTENT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import atas.model.Model;
import atas.model.ModelManager;
import atas.model.UserPrefs;
import atas.model.session.Session;
import atas.model.session.SessionList;
import atas.model.session.exceptions.SessionNotFoundException;
import atas.testutil.ModelManagerBuilder;
import atas.testutil.SessionBuilder;
import atas.testutil.TypicalSessions;


public class DeleteSessionCommandTest {

    private Model model = ModelManagerBuilder.buildTypicalModelManager();

    @Test
    public void execute_validIndex_success() {
        showSessionAtIndex(model, INDEX_FIRST_SESSION);
        Session sessionToDelete = model.getFilteredSessionList().get(INDEX_FIRST_SESSION.getZeroBased());
        DeleteSessionCommand deleteSessionCommand = new DeleteSessionCommand(INDEX_FIRST_SESSION);

        String expectedMessage = String.format(DeleteSessionCommand.MESSAGE_DELETE_SESSION_SUCCESS, sessionToDelete);

        ModelManager expectedModel = new ModelManager(TypicalSessions.getTypicalSessionList(
                model.getStudentList().getStudentList()), model.getStudentList(), new UserPrefs(), EMPTY_MEMO_CONTENT);

        expectedModel.deleteSession(sessionToDelete, INDEX_FIRST_SESSION);
        showNoSession(expectedModel);
        assertCommandSuccess(deleteSessionCommand, model, expectedMessage, expectedModel);
    }

    /**
     * Updates {@code model}'s filtered list to show no one.
     */
    private void showNoSession (Model model) {
        model.updateFilteredSessionList(s -> false);

        assertTrue(model.getFilteredSessionList().isEmpty());
    }

    @Test
    public void delete_sessionNotFound_throwsSessionNotFoundException() {
        SessionList emptySessionList = new SessionList();
        SessionBuilder sessionBuilder = new SessionBuilder();
        assertThrows(SessionNotFoundException.class, () -> emptySessionList.deleteSession(sessionBuilder.build()));
    }

    @Test
    public void equals() {
        DeleteSessionCommand deleteFirstSessionCommand = new DeleteSessionCommand(INDEX_FIRST_SESSION);
        DeleteSessionCommand deleteSecondSessionCommand = new DeleteSessionCommand(INDEX_SECOND_SESSION);

        // same object -> returns true
        assertTrue(deleteFirstSessionCommand.equals(deleteFirstSessionCommand));

        // same sessionName -> returns true
        DeleteSessionCommand deleteTutCommandCopy =
                new DeleteSessionCommand(deleteFirstSessionCommand.getTargetIndex());
        assertTrue(deleteFirstSessionCommand.equals(deleteTutCommandCopy));

        // different types -> returns false
        assertFalse(deleteFirstSessionCommand.equals(1));

        // null -> returns false
        assertFalse(deleteFirstSessionCommand.equals(null));

        // different sessionName -> returns false
        assertFalse(deleteFirstSessionCommand.equals(deleteSecondSessionCommand));
    }

    @Test
    public void testToString() {
        DeleteSessionCommand deleteSessionCommand = new DeleteSessionCommand(INDEX_FIRST_SESSION);
        String expectedString = "Delete 1";
        assertEquals(expectedString, deleteSessionCommand.toString());
    }
}
