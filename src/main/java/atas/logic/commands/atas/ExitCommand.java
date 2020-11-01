package atas.logic.commands.atas;

import atas.logic.commands.Command;
import atas.logic.commands.CommandResult;
import atas.model.Model;

/**
 * Terminates the program.
 */
public class ExitCommand implements Command {

    public static final String COMMAND_WORD = "exit";

    public static final String MESSAGE_EXIT_ACKNOWLEDGEMENT = "Bye beautiful :(";

    @Override
    public CommandResult execute(Model model) {
        return new CommandResult(String.format(MESSAGE_EXIT_ACKNOWLEDGEMENT), false, null, false, true, false);
    }

}
