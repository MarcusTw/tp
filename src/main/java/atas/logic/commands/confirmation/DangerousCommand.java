package atas.logic.commands.confirmation;

import atas.logic.commands.Command;
import atas.logic.commands.CommandResult;
import atas.logic.commands.exceptions.CommandException;
import atas.model.Model;

public abstract class DangerousCommand implements Command {
    /**
     * Executes the command and returns the result message.
     *
     * @param model {@code Model} which the command should operate on.
     * @return feedback message of the operation result for display
     * @throws CommandException If an error occurs during command execution.
     */
    @Override
    public abstract CommandResult execute(Model model) throws CommandException;
}
