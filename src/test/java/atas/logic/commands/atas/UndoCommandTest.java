package atas.logic.commands.atas;

import static atas.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static atas.logic.commands.CommandTestUtil.assertCommandSuccess;
import static atas.logic.commands.atas.UndoCommand.MESSAGE_SUCCESS;
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
import atas.logic.commands.studentlist.DeleteStudentCommand;
import atas.logic.commands.studentlist.EditStudentCommand;
import atas.model.Model;
import atas.testutil.EditSessionDescriptorBuilder;
import atas.testutil.EditStudentDescriptorBuilder;
import atas.testutil.ModelManagerBuilder;

/**
 * Unit testing for {@code RedoCommand}.
 * Dependent on the all {@code Command} objects used.
 */
public class UndoCommandTest {

    private final Model expectedModel = ModelManagerBuilder.buildTypicalModelManager();
    private final Model modelCopy = ModelManagerBuilder.buildTypicalModelManager();
    private final UndoCommand undoCommand = new UndoCommand();

    @Test
    public void execute_invalidUndoCommand_throwsCommandException() {
        assertThrows(CommandException.class, () -> undoCommand.execute(modelCopy));
    }

    @Test
    public void execute_undoAddStudentCommand_success() throws CommandException {
        createCommandAndExecute(new AddStudentCommand(HOON), modelCopy);
        assertUndoCommandSuccess(modelCopy);
    }

    @Test
    public void execute_undoDeleteStudentCommand_success() throws CommandException {
        createCommandAndExecute(new DeleteStudentCommand(INDEX_FIRST_STUDENT), modelCopy);
        assertUndoCommandSuccess(modelCopy);
    }

    @Test
    public void execute_undoClearStudentCommand_success() throws CommandException {
        createCommandAndExecute(new ClearStudentListCommand(), modelCopy);
        assertUndoCommandSuccess(modelCopy);
    }

    @Test
    public void execute_undoEditStudentCommand_success() throws CommandException {
        createCommandAndExecute(new EditStudentCommand(INDEX_FIRST_STUDENT,
                new EditStudentDescriptorBuilder().withName(VALID_NAME_BOB).build()), modelCopy);
        assertUndoCommandSuccess(modelCopy);
    }

    @Test
    public void execute_undoAddSessionCommand_success() throws CommandException {
        createCommandAndExecute(new AddSessionCommand(REMEDIAL), modelCopy);
        assertUndoCommandSuccess(modelCopy);
    }

    @Test
    public void execute_undoDeleteSessionCommand_success() throws CommandException {
        createCommandAndExecute(new DeleteSessionCommand(INDEX_FIRST_SESSION), modelCopy);
        assertUndoCommandSuccess(modelCopy);
    }

    @Test
    public void execute_undoClearSessionCommand_success() throws CommandException {
        createCommandAndExecute(new ClearSessionsCommand(), modelCopy);
        assertUndoCommandSuccess(modelCopy);
    }

    @Test
    public void execute_undoEditSessionCommand_success() throws CommandException {
        createCommandAndExecute(new EditSessionCommand(INDEX_FIRST_SESSION,
                new EditSessionDescriptorBuilder().withSessionName(SESSIONNAME_WEEK_ONE_STRING).build()), modelCopy);
        assertUndoCommandSuccess(modelCopy);
    }

    @Test
    public void execute_undoParticipateCommand_success() throws CommandException {
        modelCopy.enterSession(Index.fromZeroBased(1));
        expectedModel.enterSession(Index.fromZeroBased(1));
        createCommandAndExecute(
                new ParticipateCommand(modelCopy.getSessionList().getSessions().get(0).getSessionName(),
                INDEX_RANGE_ONE), modelCopy);
        assertUndoCommandSuccess(modelCopy);
    }

    @Test
    public void execute_undoPresenceCommand_success() throws CommandException {
        modelCopy.enterSession(Index.fromZeroBased(1));
        expectedModel.enterSession(Index.fromZeroBased(1));
        createCommandAndExecute(
                new PresenceCommand(modelCopy.getSessionList().getSessions().get(0).getSessionName(),
                INDEX_RANGE_ONE), modelCopy);
        assertUndoCommandSuccess(modelCopy);
    }

    private void createCommandAndExecute(Command command, Model model) throws CommandException {
        command.execute(model);
    }

    private void assertUndoCommandSuccess(Model model) {
        assertCommandSuccess(undoCommand, model, MESSAGE_SUCCESS, expectedModel);
    }

}
