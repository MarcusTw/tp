package atas.logic.commands.atas;

import static atas.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static atas.logic.commands.CommandTestUtil.assertCommandSuccess;
import static atas.logic.commands.atas.RedoCommand.MESSAGE_SUCCESS;
import static atas.testutil.Assert.assertThrows;
import static atas.testutil.TypicalIndexRanges.INDEX_RANGE_ONE;
import static atas.testutil.TypicalIndexes.INDEX_FIRST_SESSION;
import static atas.testutil.TypicalIndexes.INDEX_FIRST_STUDENT;
import static atas.testutil.TypicalSessions.REMEDIAL;
import static atas.testutil.TypicalSessions.SESSIONNAME_WEEK_ONE_STRING;
import static atas.testutil.TypicalStudents.HOON;

import org.junit.jupiter.api.Test;

import atas.commons.core.index.Index;
import atas.logic.commands.Command;
import atas.logic.commands.exceptions.CommandException;
import atas.logic.commands.sessionlist.AddSessionCommand;
import atas.logic.commands.sessionlist.ClearSessionsCommand;
import atas.logic.commands.sessionlist.DeleteSessionCommand;
import atas.logic.commands.sessionlist.EditSessionCommand;
import atas.logic.commands.sessionlist.session.ParticipateCommand;
import atas.logic.commands.sessionlist.session.PresenceCommand;
import atas.logic.commands.studentlist.AddStudentCommand;
import atas.logic.commands.studentlist.ClearStudentListCommand;
import atas.logic.commands.studentlist.DeleteStudentListCommand;
import atas.logic.commands.studentlist.EditStudentListCommand;
import atas.model.Model;
import atas.testutil.EditSessionDescriptorBuilder;
import atas.testutil.EditStudentDescriptorBuilder;
import atas.testutil.ModelManagerBuilder;

/**
 * Unit testing for {@code RedoCommand}.
 * Dependent on the all {@code Command} objects used, including {@code UndoCommand}.
 */
public class RedoCommandTest {

    private final Model expectedModel = ModelManagerBuilder.buildTypicalModelManager();
    private final Model modelCopy = ModelManagerBuilder.buildTypicalModelManager();
    private final RedoCommand redoCommand = new RedoCommand();

    @Test
    public void execute_invalidRedoCommand_throwsCommandException() {
        assertThrows(CommandException.class, () -> redoCommand.execute(modelCopy));
    }

    @Test
    public void execute_redoAddStudentCommand_success() throws CommandException {
        AddStudentCommand command = new AddStudentCommand(HOON);
        executeCommandAndUndo(command, modelCopy);
        assertRedoCommandSuccess(command, modelCopy, expectedModel);
    }

    @Test
    public void execute_redoDeleteStudentCommand_success() throws CommandException {
        DeleteStudentListCommand command = new DeleteStudentListCommand(INDEX_FIRST_STUDENT);
        executeCommandAndUndo(command, modelCopy);
        assertRedoCommandSuccess(command, modelCopy, expectedModel);
    }

    @Test
    public void execute_redoClearStudentCommand_success() throws CommandException {
        ClearStudentListCommand command = new ClearStudentListCommand();
        executeCommandAndUndo(command, modelCopy);
        assertRedoCommandSuccess(command, modelCopy, expectedModel);

    }

    @Test
    public void execute_redoEditStudentCommand_success() throws CommandException {
        EditStudentListCommand command = new EditStudentListCommand(INDEX_FIRST_STUDENT,
                new EditStudentDescriptorBuilder().withName(VALID_NAME_BOB).build());
        executeCommandAndUndo(command, modelCopy);
        assertRedoCommandSuccess(command, modelCopy, expectedModel);

    }

    @Test
    public void execute_redoAddSessionCommand_success() throws CommandException {
        AddSessionCommand command = new AddSessionCommand(REMEDIAL);
        executeCommandAndUndo(command, modelCopy);
        assertRedoCommandSuccess(command, modelCopy, expectedModel);
    }

    @Test
    public void execute_redoDeleteSessionCommand_success() throws CommandException {
        DeleteSessionCommand command = new DeleteSessionCommand(INDEX_FIRST_SESSION);
        executeCommandAndUndo(command, modelCopy);
        assertRedoCommandSuccess(command, modelCopy, expectedModel);
    }

    @Test
    public void execute_redoClearSessionCommand_success() throws CommandException {
        ClearSessionsCommand command = new ClearSessionsCommand();
        executeCommandAndUndo(command, modelCopy);
        assertRedoCommandSuccess(command, modelCopy, expectedModel);
    }

    @Test
    public void execute_redoEditSessionCommand_success() throws CommandException {
        EditSessionCommand command = new EditSessionCommand(INDEX_FIRST_SESSION,
                new EditSessionDescriptorBuilder().withSessionName(SESSIONNAME_WEEK_ONE_STRING).build());
        executeCommandAndUndo(command, modelCopy);
        assertRedoCommandSuccess(command, modelCopy, expectedModel);
    }

    @Test
    public void execute_redoParticipateCommand_success() throws CommandException {
        expectedModel.setCurrentSessionTrue();
        modelCopy.enterSession(Index.fromZeroBased(1));
        expectedModel.enterSession(Index.fromZeroBased(1));
        ParticipateCommand command =
                new ParticipateCommand(modelCopy.getSessionList().getSessions().get(0).getSessionName(),
                        INDEX_RANGE_ONE);
        executeCommandAndUndo(command, modelCopy);
        assertRedoCommandSuccess(command, modelCopy, expectedModel);
    }

    @Test
    public void execute_redoPresenceCommand_success() throws CommandException {
        expectedModel.setCurrentSessionTrue();
        modelCopy.enterSession(Index.fromZeroBased(1));
        expectedModel.enterSession(Index.fromZeroBased(1));
        PresenceCommand command =
                new PresenceCommand(modelCopy.getSessionList().getSessions().get(0).getSessionName(),
                        INDEX_RANGE_ONE);
        executeCommandAndUndo(command, modelCopy);
        assertRedoCommandSuccess(command, modelCopy, expectedModel);
    }

    private void executeCommandAndUndo(Command command, Model model) throws CommandException {
        command.execute(model);
        new UndoCommand().execute(model);
    }

    private void assertRedoCommandSuccess(Command command, Model actualModel, Model expectedModel)
            throws CommandException {
        command.execute(expectedModel);
        assertCommandSuccess(redoCommand, actualModel, MESSAGE_SUCCESS, expectedModel);
    }

}
