package atas.logic.commands.sessionlist;

import static atas.commons.core.Messages.MESSAGE_INVALID_SESSION_DISPLAYED_INDEX;
import static java.util.Objects.requireNonNull;

import atas.commons.core.index.Index;
import atas.logic.commands.CommandResult;
import atas.logic.commands.exceptions.CommandException;
import atas.model.Model;
import atas.ui.Tab;

public class EnterSessionCommand implements IndexedSessionListCommand {

    public static final String COMMAND_WORD = "enterses";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Enters a specified session. \n"
            + "Parameters: INDEX (must be a positive integer) \n"
            + "Example: " + COMMAND_WORD + " 1 ";

    public static final String MESSAGE_SUCCESS = "Session entered: %1$s";

    public static final String MESSAGE_ALREADY_IN_SESSION = "Already in session %d!";

    //sessionIndex should be zero-based here due to parser
    private final Index sessionIndex;

    /**
     * Creates an AddSessionCommand to add the specified {@code Session}
     */
    public EnterSessionCommand(Index sessionIndex) {
        requireNonNull(sessionIndex);
        this.sessionIndex = sessionIndex;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        if (sessionIndex.getZeroBased() >= model.getFilteredSessionList().size()) {
            throw new CommandException(MESSAGE_INVALID_SESSION_DISPLAYED_INDEX);
        }

        model.enterSession(sessionIndex);
        return new CommandResult(String.format(MESSAGE_SUCCESS, sessionIndex.getOneBased()),
            false, Tab.CURRENT, false, false, true);
    }

    @Override
    public Index getTargetIndex() {
        return sessionIndex;
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof EnterSessionCommand // instanceof handles nulls
                && sessionIndex.equals(((EnterSessionCommand) other).sessionIndex));
    }

    @Override
    public String toString() {
        String oneBasedIndex = String.valueOf(sessionIndex.getOneBased());
        return "Enter " + oneBasedIndex;
    }

}
