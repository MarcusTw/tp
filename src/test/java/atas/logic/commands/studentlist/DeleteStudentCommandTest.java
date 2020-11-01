package atas.logic.commands.studentlist;


import static atas.commons.core.Messages.MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX;
import static atas.logic.commands.CommandTestUtil.assertCommandFailure;
import static atas.logic.commands.CommandTestUtil.assertCommandSuccess;
import static atas.logic.commands.CommandTestUtil.showStudentAtIndex;
import static atas.testutil.TypicalIndexes.INDEX_FIRST_STUDENT;
import static atas.testutil.TypicalIndexes.INDEX_SECOND_STUDENT;
import static atas.testutil.TypicalMemoContents.EMPTY_MEMO_CONTENT;
import static atas.testutil.TypicalSessions.getTypicalSessionList;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import atas.commons.core.index.Index;
import atas.model.Model;
import atas.model.ModelManager;
import atas.model.UserPrefs;
import atas.model.student.Student;
import atas.testutil.ModelManagerBuilder;

/**
 * Contains integration tests (interaction with the Model, UndoCommand and RedoCommand) and unit tests for
 * {@code DeleteStudentCommand}.
 */
public class DeleteStudentCommandTest {

    private Model model = ModelManagerBuilder.buildTypicalModelManager();

    @Test
    public void execute_validIndexUnfilteredList_success() {
        Student studentToDelete = model.getFilteredStudentList().get(INDEX_FIRST_STUDENT.getZeroBased());
        DeleteStudentCommand deleteStudentCommand = new DeleteStudentCommand(INDEX_FIRST_STUDENT);

        String expectedMessage = String.format(DeleteStudentCommand.MESSAGE_DELETE_STUDENT_SUCCESS,
                studentToDelete);

        ModelManager expectedModel = new ModelManager(getTypicalSessionList(model.getStudentList().getStudentList()),
                model.getStudentList(), new UserPrefs(), EMPTY_MEMO_CONTENT);
        expectedModel.deleteStudent(studentToDelete, INDEX_FIRST_STUDENT);

        assertCommandSuccess(deleteStudentCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidIndexUnfilteredList_throwsCommandException() {
        Index outOfBoundIndex = Index.fromOneBased(model.getNumberOfStudents() + 1);
        DeleteStudentCommand deleteStudentCommand = new DeleteStudentCommand(outOfBoundIndex);

        assertCommandFailure(deleteStudentCommand, model, MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX);
    }

    @Test
    public void execute_validIndexFilteredList_success() {
        showStudentAtIndex(model, INDEX_FIRST_STUDENT);

        Student studentToDelete = model.getFilteredStudentList().get(INDEX_FIRST_STUDENT.getZeroBased());
        DeleteStudentCommand deleteStudentCommand = new DeleteStudentCommand(INDEX_FIRST_STUDENT);

        String expectedMessage = String.format(DeleteStudentCommand.MESSAGE_DELETE_STUDENT_SUCCESS,
                studentToDelete);

        Model expectedModel = new ModelManager(getTypicalSessionList(model.getStudentList().getStudentList()),
                model.getStudentList(), new UserPrefs(), EMPTY_MEMO_CONTENT);
        expectedModel.deleteStudent(studentToDelete, INDEX_FIRST_STUDENT);
        showNoPerson(expectedModel);

        assertCommandSuccess(deleteStudentCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidIndexFilteredList_throwsCommandException() {
        showStudentAtIndex(model, INDEX_FIRST_STUDENT);

        Index outOfBoundIndex = INDEX_SECOND_STUDENT;
        // ensures that outOfBoundIndex is still in bounds of address book list
        assertTrue(outOfBoundIndex.getZeroBased() < model.getStudentList().getStudentList().size());

        DeleteStudentCommand deleteStudentCommand = new DeleteStudentCommand(outOfBoundIndex);

        assertCommandFailure(deleteStudentCommand, model, MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX);
    }

    @Test
    public void equals() {
        DeleteStudentCommand deleteFirstStudentCommand = new DeleteStudentCommand(INDEX_FIRST_STUDENT);
        DeleteStudentCommand deleteSecondStudentCommand = new DeleteStudentCommand(INDEX_SECOND_STUDENT);

        // same object -> returns true
        assertTrue(deleteFirstStudentCommand.equals(deleteFirstStudentCommand));

        // same values -> returns true
        DeleteStudentCommand deleteFirstStudentCommandCopy = new DeleteStudentCommand(INDEX_FIRST_STUDENT);
        assertTrue(deleteFirstStudentCommand.equals(deleteFirstStudentCommandCopy));

        // different types -> returns false
        assertFalse(deleteFirstStudentCommand.equals(1));

        // null -> returns false
        assertFalse(deleteFirstStudentCommand.equals(null));

        // different person -> returns false
        assertFalse(deleteFirstStudentCommand.equals(deleteSecondStudentCommand));
    }

    /**
     * Updates {@code model}'s filtered list to show no one.
     */
    private void showNoPerson(Model model) {
        model.updateFilteredStudentList(p -> false);

        assertTrue(model.getFilteredStudentList().isEmpty());
    }
}
