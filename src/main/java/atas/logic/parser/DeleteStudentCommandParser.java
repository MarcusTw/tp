package atas.logic.parser;

import static atas.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static atas.logic.commands.studentlist.DeleteStudentListCommand.MESSAGE_USAGE;

import atas.commons.core.index.Index;
import atas.logic.commands.studentlist.DeleteStudentListCommand;
import atas.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new DeleteStudentCommand object
 */
public class DeleteStudentCommandParser implements Parser<DeleteStudentListCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the DeleteStudentCommand
     * and returns a DeleteStudentCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public DeleteStudentListCommand parse(String args) throws ParseException {
        try {
            Index index = ParserUtil.parseIndex(args);
            return new DeleteStudentListCommand(index);
        } catch (ParseException pe) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, MESSAGE_USAGE), pe);
        }
    }

}
