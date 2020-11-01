package atas.logic.commands.studentlist;

import static atas.commons.core.Messages.MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX;
import static java.util.Objects.requireNonNull;

import java.util.List;

import atas.commons.core.index.Index;
import atas.logic.commands.CommandResult;
import atas.logic.commands.confirmation.DangerousCommand;
import atas.logic.commands.exceptions.CommandException;
import atas.model.Model;
import atas.model.student.Student;

/**
 * Deletes a student identified using it's displayed index from the student list.
 */
public class DeleteStudentListCommand extends DangerousCommand implements IndexedStudentListCommand {

    public static final String COMMAND_WORD = "deletestu";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Deletes the student identified by the index number used in the displayed student list.\n"
            + "Parameters: INDEX (must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + " 1";

    public static final String MESSAGE_DELETE_STUDENT_SUCCESS = "Deleted student: %1$s";

    private final Index targetIndex;

    public DeleteStudentListCommand(Index targetIndex) {
        this.targetIndex = targetIndex;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Student> lastShownList = model.getFilteredStudentList();

        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX);
        }

        Student studentToDelete = lastShownList.get(targetIndex.getZeroBased());
        model.deleteStudent(studentToDelete, targetIndex);
        model.commit();
        return new CommandResult(String.format(MESSAGE_DELETE_STUDENT_SUCCESS, studentToDelete));
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof DeleteStudentListCommand // instanceof handles nulls
                && targetIndex.equals(((DeleteStudentListCommand) other).targetIndex)); // state check
    }

    @Override
    public String toString() {
        String oneBasedIndex = String.valueOf(targetIndex.getOneBased());
        return "Delete " + oneBasedIndex;
    }

    @Override
    public Index getTargetIndex() {
        return targetIndex;
    }
}
