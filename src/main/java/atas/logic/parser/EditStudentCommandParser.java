package atas.logic.parser;

import static atas.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static atas.logic.commands.studentlist.EditStudentListCommand.MESSAGE_NOT_EDITED;
import static atas.logic.commands.studentlist.EditStudentListCommand.MESSAGE_USAGE;
import static atas.logic.parser.CliSyntax.PREFIX_EMAIL;
import static atas.logic.parser.CliSyntax.PREFIX_MATRICULATION;
import static atas.logic.parser.CliSyntax.PREFIX_NAME;
import static atas.logic.parser.CliSyntax.PREFIX_TAG;
import static java.util.Objects.requireNonNull;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import atas.commons.core.index.Index;
import atas.logic.commands.studentlist.EditStudentListCommand;
import atas.logic.commands.studentlist.EditStudentListCommand.EditStudentDescriptor;
import atas.logic.parser.exceptions.ParseException;
import atas.model.tag.Tag;

/**
 * Parses input arguments and creates a new EditStudentCommand object
 */
public class EditStudentCommandParser implements Parser<EditStudentListCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the EditStudentCommand
     * and returns an EditStudentCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public EditStudentListCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_NAME, PREFIX_MATRICULATION, PREFIX_EMAIL, PREFIX_TAG);

        Index index;

        try {
            index = ParserUtil.parseIndex(argMultimap.getPreamble());
        } catch (ParseException pe) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, MESSAGE_USAGE), pe);
        }

        EditStudentListCommand.EditStudentDescriptor editStudentDescriptor = new EditStudentDescriptor();
        if (argMultimap.getValue(PREFIX_NAME).isPresent()) {
            editStudentDescriptor.setName(ParserUtil.parseName(argMultimap.getValue(PREFIX_NAME).get()));
        }
        if (argMultimap.getValue(PREFIX_MATRICULATION).isPresent()) {
            editStudentDescriptor.setMatriculation(ParserUtil.parseMatriculation(
                    argMultimap.getValue(PREFIX_MATRICULATION).get()));
        }
        if (argMultimap.getValue(PREFIX_EMAIL).isPresent()) {
            editStudentDescriptor.setEmail(ParserUtil.parseEmail(argMultimap.getValue(PREFIX_EMAIL).get()));
        }
        parseTagsForEdit(argMultimap.getAllValues(PREFIX_TAG)).ifPresent(editStudentDescriptor::setTags);

        if (!editStudentDescriptor.isAnyFieldEdited()) {
            throw new ParseException(MESSAGE_NOT_EDITED);
        }

        return new EditStudentListCommand(index, editStudentDescriptor);
    }

    /**
     * Parses {@code Collection<String> tags} into a {@code Set<Tag>} if {@code tags} is non-empty.
     * If {@code tags} contain only one element which is an empty string, it will be parsed into a
     * {@code Set<Tag>} containing zero tags.
     */
    private Optional<Set<Tag>> parseTagsForEdit(Collection<String> tags) throws ParseException {
        assert tags != null;

        if (tags.isEmpty()) {
            return Optional.empty();
        }
        Collection<String> tagSet = tags.size() == 1 && tags.contains("") ? Collections.emptySet() : tags;
        return Optional.of(ParserUtil.parseTags(tagSet));
    }

}
