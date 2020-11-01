package atas.logic.commands.confirmation;

import static atas.commons.core.Messages.MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX;
import static atas.logic.commands.CommandTestUtil.VALID_NAME_BOB;
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
import atas.logic.commands.studentlist.ClearStudentListCommand;
import atas.logic.commands.studentlist.DeleteStudentListCommand;
import atas.logic.commands.studentlist.EditStudentListCommand;
import atas.model.Model;
import atas.model.ModelManager;
import atas.model.UserPrefs;
import atas.model.student.Student;
import atas.model.student.StudentList;
import atas.testutil.EditStudentDescriptorBuilder;
import atas.testutil.ModelManagerBuilder;
import atas.testutil.StudentBuilder;

public class ConfirmationAcceptCommandTest {
    private Model model = ModelManagerBuilder.buildTypicalModelManager();

    @Test

    public void execute_acceptValidDeleteCommand_success() {
        Student studentToDelete = model.getFilteredStudentList().get(INDEX_FIRST_STUDENT.getZeroBased());
        DeleteStudentListCommand deleteStudentCommand = new DeleteStudentListCommand(INDEX_FIRST_STUDENT);

        String expectedMessage = String.format(DeleteStudentListCommand.MESSAGE_DELETE_STUDENT_SUCCESS, studentToDelete);

        ModelManager expectedModel = new ModelManager(getTypicalSessionList(model.getStudentList().getStudentList()),
                model.getStudentList(), new UserPrefs(), EMPTY_MEMO_CONTENT);
        expectedModel.deleteStudent(studentToDelete, INDEX_FIRST_STUDENT);

        ConfirmationAcceptCommand confirmationAcceptCommand = new ConfirmationAcceptCommand(deleteStudentCommand);
        assertCommandSuccess(confirmationAcceptCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidDeleteStudentCommand_throwsCommandException() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredStudentList().size() + 1);
        DeleteStudentListCommand deleteStudentCommand = new DeleteStudentListCommand(outOfBoundIndex);

        ConfirmationAcceptCommand confirmationAcceptCommand = new ConfirmationAcceptCommand(deleteStudentCommand);
        assertCommandFailure(confirmationAcceptCommand, model, MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX);
    }

    @Test
    public void execute_acceptValidEditStudentCommand_success() {
        showStudentAtIndex(model, INDEX_FIRST_STUDENT);

        Student studentInFilteredList = model.getFilteredStudentList().get(INDEX_FIRST_STUDENT.getZeroBased());
        Student editedStudent = new StudentBuilder(studentInFilteredList).withName(VALID_NAME_BOB).build();
        EditStudentListCommand editStudentCommand = new EditStudentListCommand(INDEX_FIRST_STUDENT,
            new EditStudentDescriptorBuilder().withName(VALID_NAME_BOB).build());

        String expectedMessage = String.format(EditStudentListCommand.MESSAGE_EDIT_STUDENT_SUCCESS, editedStudent);

        Model expectedModel = new ModelManager(getTypicalSessionList(model.getStudentList().getStudentList()),
                new StudentList(model.getStudentList()), new UserPrefs(), EMPTY_MEMO_CONTENT);
        expectedModel.setStudent(model.getFilteredStudentList().get(0), editedStudent);

        ConfirmationAcceptCommand confirmationAcceptCommand = new ConfirmationAcceptCommand(editStudentCommand);
        assertCommandSuccess(confirmationAcceptCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidEditStudentCommand_failure() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredStudentList().size() + 1);
        EditStudentListCommand.EditStudentDescriptor descriptor = new EditStudentDescriptorBuilder()

                .withName(VALID_NAME_BOB).build();
        EditStudentListCommand editStudentCommand = new EditStudentListCommand(outOfBoundIndex, descriptor);

        ConfirmationAcceptCommand confirmationAcceptCommand = new ConfirmationAcceptCommand(editStudentCommand);

        assertCommandFailure(confirmationAcceptCommand, model, MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX);
    }

    @Test
    public void execute_acceptClearStudentsCommand_success() {
        ClearStudentListCommand clearStudentListCommand = new ClearStudentListCommand();
        ModelManager expectedModel = new ModelManager(getTypicalSessionList(new ModelManager()
                .getStudentList().getStudentList()),
                new ModelManager().getStudentList(), new UserPrefs(), EMPTY_MEMO_CONTENT);
        ConfirmationAcceptCommand confirmationAcceptCommand = new ConfirmationAcceptCommand(clearStudentListCommand);
        assertCommandSuccess(confirmationAcceptCommand, model, ClearStudentListCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void equals() {

        DeleteStudentListCommand firstDeleteStudentCommand = new DeleteStudentListCommand(INDEX_FIRST_STUDENT);
        DeleteStudentListCommand secondDeleteStudentCommand = new DeleteStudentListCommand(INDEX_SECOND_STUDENT);

        Student editedStudent = new StudentBuilder().build();
        EditStudentListCommand.EditStudentDescriptor descriptor = new EditStudentDescriptorBuilder(editedStudent).build();
        EditStudentListCommand editStudentCommand = new EditStudentListCommand(INDEX_FIRST_STUDENT, descriptor);

        ClearStudentListCommand clearStudentsCommand = new ClearStudentListCommand();

        ConfirmationAcceptCommand confirmationAcceptDeleteStudentCommand = new ConfirmationAcceptCommand(
                firstDeleteStudentCommand);
        ConfirmationAcceptCommand confirmationAcceptEditStudentCommand = new ConfirmationAcceptCommand(
                editStudentCommand);
        ConfirmationAcceptCommand confirmationAcceptClearStudentsCommand = new ConfirmationAcceptCommand(
                clearStudentsCommand);

        // Check if confirmationAcceptDangerousCommand equals confirmationAcceptSameDangerousCommand
        assertTrue(confirmationAcceptDeleteStudentCommand.equals(confirmationAcceptDeleteStudentCommand));
        assertTrue(confirmationAcceptEditStudentCommand.equals(confirmationAcceptEditStudentCommand));
        assertTrue(confirmationAcceptClearStudentsCommand.equals(confirmationAcceptClearStudentsCommand));

        // Check if confirmationAcceptDangerousCommand equals confirmationAcceptAnotherDangerousCommand
        assertFalse(confirmationAcceptDeleteStudentCommand.equals(confirmationAcceptClearStudentsCommand));
        assertFalse(confirmationAcceptDeleteStudentCommand.equals(confirmationAcceptEditStudentCommand));
        assertFalse(confirmationAcceptEditStudentCommand.equals(confirmationAcceptClearStudentsCommand));

        // Check if confirmationAcceptDeleteStudentCommand equals confirmationAcceptAnotherDeleteCommand
        assertFalse(confirmationAcceptDeleteStudentCommand.equals(new ConfirmationAcceptCommand(
                secondDeleteStudentCommand)));

        // Check if confirmationAcceptCommand equals another subclass of ConfirmCommand
        assertFalse(confirmationAcceptDeleteStudentCommand.equals(new ConfirmationRejectCommand(
                firstDeleteStudentCommand)));
    }
}
