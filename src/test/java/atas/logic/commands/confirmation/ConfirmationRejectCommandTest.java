package atas.logic.commands.confirmation;

import static atas.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static atas.logic.commands.CommandTestUtil.assertCommandSuccess;
import static atas.testutil.TypicalIndexes.INDEX_FIRST_STUDENT;
import static atas.testutil.TypicalIndexes.INDEX_SECOND_STUDENT;
import static atas.testutil.TypicalMemoContents.EMPTY_MEMO_CONTENT;
import static atas.testutil.TypicalSessions.getTypicalSessionList;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import atas.logic.commands.studentlist.ClearStudentListCommand;
import atas.logic.commands.studentlist.DeleteStudentCommand;
import atas.logic.commands.studentlist.EditStudentCommand;
import atas.model.Model;
import atas.model.ModelManager;
import atas.model.UserPrefs;
import atas.model.student.Student;
import atas.testutil.EditStudentDescriptorBuilder;
import atas.testutil.ModelManagerBuilder;
import atas.testutil.StudentBuilder;

public class ConfirmationRejectCommandTest {
    private Model model = ModelManagerBuilder.buildTypicalModelManager();

    @Test
    public void execute_rejectDeleteStudentCommand_success() {
        DeleteStudentCommand deleteStudentCommand = new DeleteStudentCommand(INDEX_FIRST_STUDENT);
        String expectedMessage = String.format(ConfirmationRejectCommand.MESSAGE_REJECT_COMMAND, deleteStudentCommand);

        ModelManager expectedModel = new ModelManager(getTypicalSessionList(model.getStudentList().getStudentList()),
                model.getStudentList(), new UserPrefs(), EMPTY_MEMO_CONTENT);

        ConfirmationRejectCommand confirmationRejectCommand = new ConfirmationRejectCommand(deleteStudentCommand);
        assertCommandSuccess(confirmationRejectCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_rejectEditStudentCommand_success() {
        EditStudentCommand editStudentCommand = new EditStudentCommand(INDEX_FIRST_STUDENT,
            new EditStudentDescriptorBuilder().withName(VALID_NAME_BOB).build());

        String expectedMessage = String.format(ConfirmationRejectCommand.MESSAGE_REJECT_COMMAND, editStudentCommand);

        ModelManager expectedModel = new ModelManager(getTypicalSessionList(model.getStudentList().getStudentList()),
                model.getStudentList(), new UserPrefs(), EMPTY_MEMO_CONTENT);

        ConfirmationRejectCommand confirmationRejectCommand = new ConfirmationRejectCommand(editStudentCommand);
        assertCommandSuccess(confirmationRejectCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_rejectClearStudentListCommand_success() {
        ClearStudentListCommand clearStudentListCommand = new ClearStudentListCommand();
        Model expectedModel = new ModelManager(getTypicalSessionList(model.getStudentList().getStudentList()),
                model.getStudentList(), new UserPrefs(), EMPTY_MEMO_CONTENT);
        String expectedMessage = String.format(ConfirmationRejectCommand.MESSAGE_REJECT_COMMAND,
                clearStudentListCommand);

        ConfirmationRejectCommand confirmationRejectCommand =
                new ConfirmationRejectCommand(clearStudentListCommand);
        assertCommandSuccess(confirmationRejectCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void equals() {
        DeleteStudentCommand firstDeleteStudentCommand = new DeleteStudentCommand(INDEX_FIRST_STUDENT);
        DeleteStudentCommand secondDeleteStudentCommand = new DeleteStudentCommand(INDEX_SECOND_STUDENT);

        Student editedStudent = new StudentBuilder().build();
        EditStudentCommand.EditStudentDescriptor descriptor =
                new EditStudentDescriptorBuilder(editedStudent).build();
        EditStudentCommand editStudentCommand = new EditStudentCommand(INDEX_FIRST_STUDENT, descriptor);

        ClearStudentListCommand clearStudentListCommand = new ClearStudentListCommand();

        ConfirmationRejectCommand confirmationRejectDeleteStudentCommand =
                new ConfirmationRejectCommand(firstDeleteStudentCommand);
        ConfirmationRejectCommand confirmationRejectEditStudentCommand =
                new ConfirmationRejectCommand(editStudentCommand);
        ConfirmationRejectCommand confirmationRejectClearStudentListCommand =
                new ConfirmationRejectCommand(clearStudentListCommand);

        // Check if confirmationRejectDangerousCommand equals confirmationRejectSameDangerousCommand
        assertTrue(confirmationRejectDeleteStudentCommand.equals(confirmationRejectDeleteStudentCommand));
        assertTrue(confirmationRejectEditStudentCommand.equals(confirmationRejectEditStudentCommand));
        assertTrue(confirmationRejectClearStudentListCommand.equals(confirmationRejectClearStudentListCommand));

        // Check if confirmationRejectDangerousCommand equals confirmationRejectAnotherDangerousCommand
        assertFalse(confirmationRejectDeleteStudentCommand.equals(confirmationRejectClearStudentListCommand));
        assertFalse(confirmationRejectDeleteStudentCommand.equals(confirmationRejectEditStudentCommand));
        assertFalse(confirmationRejectEditStudentCommand.equals(confirmationRejectClearStudentListCommand));

        // Check if confirmationRejectDeleteStudentCommand equals confirmationRejectAnotherDeleteStudentCommand
        assertFalse(confirmationRejectDeleteStudentCommand.equals(new ConfirmationRejectCommand(
                secondDeleteStudentCommand)));

        // Check if ConfirmationRejectCommand equals another subclass of ConfirmCommand
        assertFalse(confirmationRejectDeleteStudentCommand.equals(new ConfirmationAcceptCommand(
                firstDeleteStudentCommand)));
    }
}
