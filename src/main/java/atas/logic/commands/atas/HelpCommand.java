package atas.logic.commands.atas;

import atas.logic.commands.Command;
import atas.logic.commands.CommandResult;
import atas.model.Model;

/**
 * Format full help instructions for every command for display.
 */
public class HelpCommand implements Command {

    public static final String COMMAND_WORD = "help";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Shows program usage instructions.\n"
            + "Example: " + COMMAND_WORD;

    public static final String SHOWING_HELP_MESSAGE = "Opened help window.";

    @Override
    public CommandResult execute(Model model) {
        return new CommandResult(SHOWING_HELP_MESSAGE, true, null, false, false, false);
    }
}
