package atas.logic.commands.atas;

import static atas.model.Model.PREDICATE_SHOW_ALL_SESSIONS;
import static atas.model.Model.PREDICATE_SHOW_ALL_STUDENTS;

import atas.logic.commands.Command;
import atas.logic.commands.CommandResult;
import atas.logic.commands.exceptions.CommandException;
import atas.model.Model;
import atas.model.exceptions.UnableToRedoException;

/**
 * Undoes a the effects of the previous command, if possible.
 */
public class RedoCommand implements Command {
    public static final String COMMAND_WORD = "redo";
    public static final String MESSAGE_SUCCESS = "Previous command successfully redone!";

    @Override
    public CommandResult execute(Model model) throws UnableToRedoException, CommandException {
        try {
            model.redo();
            model.updateFilteredStudentList(PREDICATE_SHOW_ALL_STUDENTS);
            model.updateFilteredSessionList(PREDICATE_SHOW_ALL_SESSIONS);
        } catch (UnableToRedoException e) {
            throw new CommandException(e.getMessage());
        }
        return new CommandResult(MESSAGE_SUCCESS);
    }
}
