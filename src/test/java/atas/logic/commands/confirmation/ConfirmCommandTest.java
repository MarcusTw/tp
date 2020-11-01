package atas.logic.commands.confirmation;

import static atas.testutil.TypicalIndexes.INDEX_FIRST_STUDENT;
import static atas.testutil.TypicalIndexes.INDEX_SECOND_STUDENT;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import atas.logic.commands.studentlist.ClearStudentListCommand;
import atas.logic.commands.studentlist.DeleteStudentListCommand;
import atas.logic.commands.studentlist.EditStudentListCommand;
import atas.model.student.Student;
import atas.testutil.EditStudentDescriptorBuilder;
import atas.testutil.StudentBuilder;

public class ConfirmCommandTest {
    @Test
    public void equals() {
        DeleteStudentListCommand firstDeleteStudentCommand = new DeleteStudentListCommand(INDEX_FIRST_STUDENT);
        DeleteStudentListCommand secondDeleteStudentCommand = new DeleteStudentListCommand(INDEX_SECOND_STUDENT);

        Student editedStudent = new StudentBuilder().build();
        EditStudentListCommand.EditStudentDescriptor descriptor = new EditStudentDescriptorBuilder(editedStudent).build();
        EditStudentListCommand editStudentCommand = new EditStudentListCommand(INDEX_FIRST_STUDENT, descriptor);

        ClearStudentListCommand clearStudentListCommand = new ClearStudentListCommand();

        ConfirmCommand confirmationDeleteStudentCommand = new ConfirmationCommand(firstDeleteStudentCommand);
        ConfirmCommand confirmationEditStudentCommand = new ConfirmationCommand(editStudentCommand);
        ConfirmCommand confirmationClearStudentListCommand = new ConfirmationCommand(clearStudentListCommand);

        ConfirmCommand confirmationAcceptDeleteStudentCommand = new ConfirmationAcceptCommand(
                firstDeleteStudentCommand);
        ConfirmCommand confirmationAcceptEditStudentCommand = new ConfirmationAcceptCommand(
                editStudentCommand);
        ConfirmCommand confirmationAcceptClearStudentListCommand = new ConfirmationAcceptCommand(
                clearStudentListCommand);

        ConfirmCommand confirmationRejectDeleteStudentCommand = new ConfirmationRejectCommand(
                firstDeleteStudentCommand);
        ConfirmCommand confirmationRejectEditStudentCommand = new ConfirmationRejectCommand(
                editStudentCommand);
        ConfirmCommand confirmationRejectClearStudentListCommand = new ConfirmationRejectCommand(
                clearStudentListCommand);

        assertTrue(confirmationDeleteStudentCommand.equals(confirmationDeleteStudentCommand));
        assertTrue(confirmationEditStudentCommand.equals(confirmationEditStudentCommand));
        assertTrue(confirmationClearStudentListCommand.equals(confirmationClearStudentListCommand));

        assertFalse(confirmationDeleteStudentCommand.equals(new ConfirmationCommand(
                secondDeleteStudentCommand)));

        assertFalse(confirmationDeleteStudentCommand.equals(confirmationClearStudentListCommand));
        assertFalse(confirmationDeleteStudentCommand.equals(confirmationEditStudentCommand));
        assertFalse(confirmationEditStudentCommand.equals(confirmationClearStudentListCommand));

        // Make sure there is a clear distinction between accept and reject confirmation command.
        assertFalse(confirmationAcceptDeleteStudentCommand.equals(confirmationRejectDeleteStudentCommand));
        assertFalse(confirmationAcceptClearStudentListCommand.equals(confirmationRejectClearStudentListCommand));
        assertFalse(confirmationAcceptEditStudentCommand.equals(confirmationRejectEditStudentCommand));

        // Make sure there is clear distinction between confirmation and accept/reject confirmation command.
        assertFalse(confirmationClearStudentListCommand.equals(confirmationAcceptClearStudentListCommand));
        assertFalse(confirmationClearStudentListCommand.equals(confirmationRejectClearStudentListCommand));
        assertFalse(confirmationDeleteStudentCommand.equals(confirmationAcceptDeleteStudentCommand));
        assertFalse(confirmationDeleteStudentCommand.equals(confirmationRejectDeleteStudentCommand));
        assertFalse(confirmationEditStudentCommand.equals(confirmationAcceptEditStudentCommand));
        assertFalse(confirmationEditStudentCommand.equals(confirmationRejectEditStudentCommand));
    }
}
