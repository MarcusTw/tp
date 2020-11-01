package atas.logic.commands.atas;

import static java.util.Objects.requireNonNull;

import atas.logic.commands.Command;
import atas.logic.commands.CommandResult;
import atas.logic.commands.exceptions.CommandException;
import atas.model.Model;
import atas.ui.Tab;

/**
 * Switches tab using the name of the destination tab.
 * Name of destination tab is case insensitive.
 */
public class SwitchCommand implements Command {

    public static final String COMMAND_WORD = "switch";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Switches to the specified tab (case-insensitive).\n"
            + "Parameters: TAB_NAME (must be an existing tab)\n"
            + "Examples: "
            + COMMAND_WORD + " students, "
            + COMMAND_WORD + " sessions, "
            + COMMAND_WORD + " current, "
            + COMMAND_WORD + " memo";

    public static final String MESSAGE_SWITCH_TAB_SUCCESS = "Switched to %1$s tab";

    public static final String MESSAGE_INVALID_TAB = "Tab does not exist!";

    public static final String MESSAGE_ALREADY_ON_TAB = "Already at %1$s tab!";

    private final String tabName;

    public SwitchCommand(String tabName) {
        this.tabName = tabName;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        Tab tab;
        requireNonNull(tabName);
        String trimmedTab = tabName.toLowerCase();
        switch(trimmedTab) {
        case "students":
            tab = Tab.STUDENTS;
            break;
        case "sessions":
            tab = Tab.SESSIONS;
            break;
        case "current":
            tab = Tab.CURRENT;
            break;
        case "memo":
            tab = Tab.MEMO;
            break;
        default:
            throw new CommandException(MESSAGE_INVALID_TAB);
        }
        return new CommandResult(String.format(MESSAGE_SWITCH_TAB_SUCCESS, tab.toDisplayName()),
                false, tab, false, false, false);
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof SwitchCommand // instanceof handles nulls
                && tabName.equals(((SwitchCommand) other).tabName)); // state check
    }
}
