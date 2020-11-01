package atas.logic.commands.sessionlist;

import static atas.commons.core.Messages.MESSAGE_INVALID_SESSION_DISPLAYED_INDEX;
import static atas.logic.commands.CommandTestUtil.DESC_CON;
import static atas.logic.commands.CommandTestUtil.DESC_REC;
import static atas.logic.commands.CommandTestUtil.assertCommandFailure;
import static atas.logic.commands.CommandTestUtil.assertCommandSuccess;
import static atas.logic.commands.CommandTestUtil.showSessionAtIndex;
import static atas.testutil.TypicalIndexes.INDEX_FIRST_SESSION;
import static atas.testutil.TypicalIndexes.INDEX_SECOND_SESSION;
import static atas.testutil.TypicalMemoContents.EMPTY_MEMO_CONTENT;
import static atas.testutil.TypicalSessions.SESSIONDATE_WEEK_TWO_STRING;
import static atas.testutil.TypicalSessions.SESSIONNAME_WEEK_TWO_STRING;
import static atas.testutil.TypicalSessions.getTypicalSessionList;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import atas.commons.core.index.Index;
import atas.logic.commands.studentlist.ClearStudentListCommand;
import atas.model.Model;
import atas.model.ModelManager;
import atas.model.UserPrefs;
import atas.model.session.Session;
import atas.model.student.StudentList;
import atas.testutil.EditSessionDescriptorBuilder;
import atas.testutil.ModelManagerBuilder;
import atas.testutil.SessionBuilder;


public class EditSessionCommandTest {

    private Model model = ModelManagerBuilder.buildTypicalModelManager();

    @Test
    public void execute_allFieldsSpecifiedUnfilteredList_success() {
        Session editedSession = new SessionBuilder().build();
        EditSessionCommand.EditSessionDescriptor descriptor = new EditSessionDescriptorBuilder(editedSession).build();
        EditSessionCommand editSessionCommand = new EditSessionCommand(INDEX_FIRST_SESSION, descriptor);

        String expectedMessage = String.format(EditSessionCommand.MESSAGE_EDIT_SESSION_SUCCESS, editedSession);

        Model expectedModel = new ModelManager(getTypicalSessionList(
                model.getStudentList().getStudentList()),
                new StudentList(model.getStudentList()), new UserPrefs(), EMPTY_MEMO_CONTENT);
        expectedModel.setSession(model.getFilteredSessionList().get(0), editedSession);

        assertCommandSuccess(editSessionCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_editOnlySessionName_success() {

        Index indexLastSession = Index.fromOneBased(model.getNumberOfSessions());
        Session lastSession = model.getFilteredSessionList().get(indexLastSession.getZeroBased());

        SessionBuilder sessionInList = new SessionBuilder(lastSession);
        Session editedSession = sessionInList.withSessionName(SESSIONNAME_WEEK_TWO_STRING).build();

        EditSessionCommand.EditSessionDescriptor descriptor = new EditSessionDescriptorBuilder()
                .withSessionName(SESSIONNAME_WEEK_TWO_STRING).build();
        EditSessionCommand editSessionCommand = new EditSessionCommand(indexLastSession, descriptor);

        String expectedMessage = String.format(EditSessionCommand.MESSAGE_EDIT_SESSION_SUCCESS, editedSession);

        Model expectedModel = new ModelManager(getTypicalSessionList(
                model.getStudentList().getStudentList()),
                new StudentList(model.getStudentList()), new UserPrefs(), EMPTY_MEMO_CONTENT);
        expectedModel.setSession(lastSession, editedSession);

        assertCommandSuccess(editSessionCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_editOnlySessionDate_success() {
        Index indexLastSession = Index.fromOneBased(model.getNumberOfSessions());
        Session lastSession = model.getFilteredSessionList().get(indexLastSession.getZeroBased());

        SessionBuilder sessionInList = new SessionBuilder(lastSession);
        Session editedSession = sessionInList.withSessionDate(SESSIONDATE_WEEK_TWO_STRING).build();

        EditSessionCommand.EditSessionDescriptor descriptor = new EditSessionDescriptorBuilder()
                .withSessionDate(SESSIONDATE_WEEK_TWO_STRING).build();
        EditSessionCommand editSessionCommand = new EditSessionCommand(indexLastSession, descriptor);

        String expectedMessage = String.format(EditSessionCommand.MESSAGE_EDIT_SESSION_SUCCESS, editedSession);

        Model expectedModel = new ModelManager(getTypicalSessionList(model.getStudentList().getStudentList()),
                new StudentList(model.getStudentList()), new UserPrefs(), EMPTY_MEMO_CONTENT);
        expectedModel.setSession(lastSession, editedSession);

        assertCommandSuccess(editSessionCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_noFieldSpecifiedUnfilteredList_success() {
        EditSessionCommand editSessionCommand = new EditSessionCommand(INDEX_FIRST_SESSION,
                new EditSessionCommand.EditSessionDescriptor());
        Session editedSession = model.getFilteredSessionList().get(INDEX_FIRST_SESSION.getZeroBased());

        String expectedMessage = String.format(EditSessionCommand.MESSAGE_EDIT_SESSION_SUCCESS, editedSession);

        Model expectedModel = new ModelManager(getTypicalSessionList(
                model.getStudentList().getStudentList()),
            new StudentList(model.getStudentList()), new UserPrefs(), EMPTY_MEMO_CONTENT);

        // TODO assertCommandSuccess(editSessionCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_duplicateSessionAfterEdit_failure() {
        Session firstSession = model.getFilteredSessionList().get(INDEX_FIRST_SESSION.getZeroBased());
        EditSessionCommand.EditSessionDescriptor descriptor = new EditSessionDescriptorBuilder(firstSession).build();
        EditSessionCommand editSessionCommand = new EditSessionCommand(INDEX_SECOND_SESSION, descriptor);

        assertCommandFailure(editSessionCommand, model, EditSessionCommand.MESSAGE_DUPLICATE_SESSION);
    }

    @Test
    public void execute_sessionNameDoesNotExist_failure() {
        showSessionAtIndex(model, INDEX_FIRST_SESSION);
        Index outOfBoundIndex = INDEX_SECOND_SESSION;
        // ensures that outOfBoundIndex is still in bounds of ATAS
        assertTrue(outOfBoundIndex.getZeroBased() < model.getSessionList().returnSize());

        EditSessionCommand editSessionCommand = new EditSessionCommand(outOfBoundIndex,
                new EditSessionDescriptorBuilder().withSessionName(SESSIONNAME_WEEK_TWO_STRING).build());

        assertCommandFailure(editSessionCommand, model, MESSAGE_INVALID_SESSION_DISPLAYED_INDEX);
    }

    @Test
    public void equals() {
        final EditSessionCommand standardCommand = new EditSessionCommand(INDEX_FIRST_SESSION, DESC_REC);

        // same values -> returns true
        EditSessionCommand.EditSessionDescriptor copyDescriptor =
                new EditSessionCommand.EditSessionDescriptor(DESC_REC);
        EditSessionCommand commandWithSameValues = new EditSessionCommand(INDEX_FIRST_SESSION, copyDescriptor);

        assertTrue(standardCommand.equals(commandWithSameValues));

        // same object -> returns true
        assertTrue(standardCommand.equals(standardCommand));

        // null -> returns false
        assertFalse(standardCommand.equals(null));

        // different types -> returns false
        assertFalse(standardCommand.equals(new ClearStudentListCommand()));


        // different index -> returns false
        assertFalse(standardCommand.equals(new EditSessionCommand(INDEX_SECOND_SESSION, DESC_REC)));

        // same descriptor object -> returns true
        assertTrue(copyDescriptor.equals(copyDescriptor));

        // different type -> returns false
        assertFalse(copyDescriptor.equals(standardCommand));

        // different descriptor -> returns false
        assertFalse(standardCommand.equals(new EditSessionCommand(INDEX_FIRST_SESSION, DESC_CON)));
    }
}
