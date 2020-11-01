package atas.logic.commands.sessionlist;

import static atas.commons.core.Messages.MESSAGE_INVALID_SESSION_DISPLAYED_INDEX;
import static java.util.Objects.requireNonNull;

import java.util.List;

import atas.commons.core.index.Index;
import atas.logic.commands.CommandResult;
import atas.logic.commands.confirmation.DangerousCommand;
import atas.logic.commands.exceptions.CommandException;
import atas.model.Model;
import atas.model.session.Session;

public class DeleteSessionCommand extends DangerousCommand implements IndexedSessionListCommand {

    public static final String COMMAND_WORD = "deleteses";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Deletes the session identified by the index number used in the displayed session list.\n"
            + "Parameters: INDEX (must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + " 1";


    public static final String MESSAGE_DELETE_SESSION_SUCCESS = "Deleted session: %1$s";
    public static final String MESSAGE_SESSION_NOT_FOUND = "Session not found.";

    private final Index targetIndex;

    /**
     * Creates a DeleteSessionCommand to delete the specified {@code Session}
     */
    public DeleteSessionCommand(Index targetIndex) {
        this.targetIndex = targetIndex;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        List<Session> lastShownList = model.getFilteredSessionList();

        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(MESSAGE_INVALID_SESSION_DISPLAYED_INDEX);
        }

        Session sessionToDelete = lastShownList.get(targetIndex.getZeroBased());

        if (!model.hasSession(sessionToDelete)) {
            throw new CommandException(MESSAGE_SESSION_NOT_FOUND);
        }

        model.deleteSession(sessionToDelete, targetIndex);
        model.commit();
        return new CommandResult(String.format(MESSAGE_DELETE_SESSION_SUCCESS, sessionToDelete));
    }

    @Override
    public Index getTargetIndex() {
        return targetIndex;
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof DeleteSessionCommand // instanceof handles nulls
                && targetIndex.equals(((DeleteSessionCommand) other).targetIndex));
    }

    @Override
    public String toString() {
        String oneBasedIndex = String.valueOf(targetIndex.getOneBased());
        return "Delete " + oneBasedIndex;
    }
}
