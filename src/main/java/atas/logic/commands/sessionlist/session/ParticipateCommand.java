package atas.logic.commands.sessionlist.session;

import static atas.commons.core.Messages.MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX_RANGE;
import static java.util.Objects.requireNonNull;

import java.util.List;

import atas.commons.core.Messages;
import atas.logic.commands.Command;
import atas.logic.commands.CommandResult;
import atas.logic.commands.exceptions.CommandException;
import atas.model.Model;
import atas.model.session.Attributes;
import atas.model.session.IndexRange;
import atas.model.session.SessionName;

/**
 * Toggles the participation status of certain students in particular sessions.
 */
public class ParticipateCommand implements Command {

    public static final String COMMAND_WORD = "participate";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Toggles the participation status of students in a "
            + "session.\n"
            + "Parameters: INDEX_RANGE\n"
            + IndexRange.MESSAGE_CONSTRAINTS + "\n"
            + "Examples: " + COMMAND_WORD + " 3, " + COMMAND_WORD + " 1-4";


    public static final String MESSAGE_SUCCESS = "Participation status changed";

    private final IndexRange range;
    private final SessionName sessionName;

    /**
     * Creates an ParticipateCommand to add the specified {@code IndexRange}
     */
    public ParticipateCommand(SessionName sessionName, IndexRange range) {
        requireNonNull(range);
        this.range = range;
        this.sessionName = sessionName;
    }

    /**
     * Creates an ParticipateCommand to add the specified {@code IndexRange}
     */
    public ParticipateCommand(IndexRange range) {
        this.range = range;
        this.sessionName = new SessionName("DEFAULT");
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        if (!model.returnCurrentSessionEnabledStatus()) {
            throw new CommandException(Messages.MESSAGE_NOT_IN_SESSION_TAB);
        }

        List<Attributes> lastShownList = model.getCurrentAttributesList();

        if (range.getZeroBasedUpper() >= lastShownList.size()) {
            throw new CommandException(MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX_RANGE);
        }

        model.updateParticipationBySessionName(sessionName, range);
        model.commit();
        return new CommandResult(MESSAGE_SUCCESS);
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof ParticipateCommand // instanceof handles nulls
                && range.equals(((ParticipateCommand) other).range)
                && sessionName.equals(((ParticipateCommand) other).sessionName));
    }
}
