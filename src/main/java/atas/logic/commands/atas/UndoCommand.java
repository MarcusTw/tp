package atas.logic.commands.atas;

import static atas.model.Model.PREDICATE_SHOW_ALL_SESSIONS;
import static atas.model.Model.PREDICATE_SHOW_ALL_STUDENTS;

import atas.logic.commands.Command;
import atas.logic.commands.CommandResult;
import atas.logic.commands.exceptions.CommandException;
import atas.model.Model;
import atas.model.exceptions.UnableToUndoException;

/**
 * Undoes a the effects of the previous command, if possible.
 */
public class UndoCommand implements Command {
    public static final String COMMAND_WORD = "undo";
    public static final String MESSAGE_SUCCESS = "Previous command successfully undone!";

    @Override
    public CommandResult execute(Model model) throws UnableToUndoException, CommandException {
        try {
            model.undo();
            model.updateFilteredStudentList(PREDICATE_SHOW_ALL_STUDENTS);
            model.updateFilteredSessionList(PREDICATE_SHOW_ALL_SESSIONS);
        } catch (UnableToUndoException e) {
            throw new CommandException(e.getMessage());
        }
        return new CommandResult(MESSAGE_SUCCESS);
    }
}
