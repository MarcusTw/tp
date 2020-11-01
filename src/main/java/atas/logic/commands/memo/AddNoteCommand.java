package atas.logic.commands.memo;

import static java.util.Objects.requireNonNull;

import atas.logic.commands.Command;
import atas.logic.commands.CommandResult;
import atas.logic.commands.exceptions.CommandException;
import atas.model.Model;

/**
 * Appends a note on a new line to the end of {@code Memo}.
 */
public class AddNoteCommand implements Command {

    public static final String COMMAND_WORD = "addnote";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Appends a note on a new line to the end of the memo.\n"
            + "Parameters: "
            + "NOTE\n"
            + "Example: " + COMMAND_WORD + " "
            + "clarify qn 4 with prof";

    public static final String MESSAGE_SUCCESS = "Note added to memo!";
    public static final String MESSAGE_EMPTY_NOTE = "Note is empty!";

    private final String toAdd;

    /**
     * Creates an AddNoteCommand to add the specified String of text.
     */
    public AddNoteCommand(String note) {
        requireNonNull(note);
        toAdd = note;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        if (toAdd.isEmpty()) {
            throw new CommandException(MESSAGE_EMPTY_NOTE);
        }

        model.addNoteToMemo(toAdd);
        return new CommandResult(MESSAGE_SUCCESS, false, null, true, false, false);
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof AddNoteCommand // instanceof handles nulls
                && toAdd.equals(((AddNoteCommand) other).toAdd));
    }
}
