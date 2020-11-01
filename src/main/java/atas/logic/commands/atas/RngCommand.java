package atas.logic.commands.atas;

import java.util.List;

import atas.commons.core.index.Index;
import atas.logic.commands.Command;
import atas.logic.commands.CommandResult;
import atas.logic.commands.exceptions.CommandException;
import atas.model.Model;
import atas.model.student.Student;

/**
 * Generates the name of a random student.
 */
public class RngCommand implements Command {

    public static final String COMMAND_WORD = "rng";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Generates the name of a random student.\n"
            + "Example: " + COMMAND_WORD;

    public static final String MESSAGE_SUCCESS = "Student selected: %1$s";

    public static final String MESSAGE_NO_STUDENTS = "There are no students to choose from!";

    @Override
    public CommandResult execute(Model model) throws CommandException {
        List<Student> studentList = model.getFilteredStudentList();
        Index studentIndex;

        try {
            studentIndex = model.generateRandomStudentIndex();
        } catch (IllegalArgumentException e) {
            throw new CommandException(MESSAGE_NO_STUDENTS);
        }

        assert studentIndex != null;
        Student student = studentList.get(studentIndex.getZeroBased());
        return new CommandResult(String.format(MESSAGE_SUCCESS, student.getName()));
    }


}
