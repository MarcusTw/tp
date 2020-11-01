package atas.logic.commands.studentlist;

import static java.util.Objects.requireNonNull;

import atas.logic.commands.Command;
import atas.logic.commands.CommandResult;
import atas.logic.commands.exceptions.CommandException;
import atas.logic.parser.CliSyntax;
import atas.model.Model;
import atas.model.student.Student;

/**
 * Adds a student to the class.
 */
public class AddStudentCommand implements Command {

    public static final String COMMAND_WORD = "addstu";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Adds a student to the class. "
            + "Parameters: "
            + CliSyntax.PREFIX_NAME + "NAME "
            + CliSyntax.PREFIX_MATRICULATION + "MATRICULATION "
            + CliSyntax.PREFIX_EMAIL + "EMAIL "
            + "[" + CliSyntax.PREFIX_TAG + "TAG]...\n"
            + "Example: " + COMMAND_WORD + " "
            + CliSyntax.PREFIX_NAME + "John Doe "
            + CliSyntax.PREFIX_MATRICULATION + "A1234567X "
            + CliSyntax.PREFIX_EMAIL + "johnd@u.nus.edu "
            + CliSyntax.PREFIX_TAG + "outspoken "
            + CliSyntax.PREFIX_TAG + "foreigner";

    public static final String MESSAGE_SUCCESS = "New student added: %1$s";
    public static final String MESSAGE_DUPLICATE_STUDENT = "This student already exists in the class";

    private final Student toAdd;

    /**
     * Creates an AddCommand to add the specified {@code Student}
     */
    public AddStudentCommand(Student student) {
        requireNonNull(student);
        toAdd = student;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        if (model.hasStudent(toAdd)) {
            throw new CommandException(MESSAGE_DUPLICATE_STUDENT);
        }

        model.addStudent(toAdd);
        model.commit();
        return new CommandResult(String.format(MESSAGE_SUCCESS, toAdd));
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof AddStudentCommand // instanceof handles nulls
                && toAdd.equals(((AddStudentCommand) other).toAdd));
    }
}
