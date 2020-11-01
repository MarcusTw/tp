package atas.logic.commands.sessionlist;

import static java.util.Objects.requireNonNull;

import atas.logic.commands.Command;
import atas.logic.commands.CommandResult;
import atas.logic.commands.exceptions.CommandException;
import atas.logic.parser.CliSyntax;
import atas.model.Model;
import atas.model.session.Session;

/**
 * Creates a new class session.
 */
public class AddSessionCommand implements Command {

    public static final String COMMAND_WORD = "addses";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Adds a session to the class. "
            + "Parameters: "
            + CliSyntax.PREFIX_SESSION_NAME + "SESSION_NAME "
            + CliSyntax.PREFIX_SESSION_DATE + "SESSION_DATE "
            + "\n"
            + "Example: " + COMMAND_WORD + " "
            + CliSyntax.PREFIX_SESSION_NAME + "Tutorial 1 "
            + CliSyntax.PREFIX_SESSION_DATE + "12/7/2020 ";


    public static final String MESSAGE_SUCCESS = "New session added: %1$s";
    public static final String MESSAGE_DUPLICATE_SESSION = "This session already exists in this class.";

    private final Session toAdd;

    /**
     * Creates an AddSessionCommand to add the specified {@code Session}
     */
    public AddSessionCommand(Session session) {
        requireNonNull(session);
        toAdd = session;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        if (model.hasSession(toAdd)) {
            throw new CommandException(MESSAGE_DUPLICATE_SESSION);
        }

        model.addSession(toAdd);
        model.commit();
        return new CommandResult(String.format(MESSAGE_SUCCESS, toAdd));
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof AddSessionCommand // instanceof handles nulls
                && toAdd.equals(((AddSessionCommand) other).toAdd));
    }

}
