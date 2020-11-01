package atas.logic.commands.studentlist;

import static java.util.Objects.requireNonNull;

import atas.commons.core.Messages;
import atas.logic.commands.Command;
import atas.logic.commands.CommandResult;
import atas.model.Model;
import atas.model.student.NameContainsKeywordsPredicate;

/**
 * Finds and lists all students in student list whose name contains any of the argument keywords.
 * Keyword matching is case insensitive.
 */
public class FindStudentsCommand implements Command {
    public static final String COMMAND_WORD = "findstu";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Finds all students whose names contain any of "
            + "the specified keywords (case-insensitive) and displays them as a list with index numbers.\n"
            + "Parameters: KEYWORD [MORE_KEYWORDS]...\n"
            + "Example: " + COMMAND_WORD + " alice bob charlie";

    private final NameContainsKeywordsPredicate predicate;

    public FindStudentsCommand(NameContainsKeywordsPredicate predicate) {
        this.predicate = predicate;
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredStudentList(predicate);
        int numberOfStudents = model.getNumberOfStudents();
        return new CommandResult(
                String.format(Messages.getStudentListedMessage(numberOfStudents), numberOfStudents));
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof FindStudentsCommand // instanceof handles nulls
                && predicate.equals(((FindStudentsCommand) other).predicate)); // state check
    }
}
