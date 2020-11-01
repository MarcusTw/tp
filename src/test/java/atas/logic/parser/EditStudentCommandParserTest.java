package atas.logic.parser;

import static atas.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static atas.logic.commands.CommandTestUtil.EMAIL_DESC_AMY;
import static atas.logic.commands.CommandTestUtil.EMAIL_DESC_BOB;
import static atas.logic.commands.CommandTestUtil.INVALID_EMAIL_DESC;
import static atas.logic.commands.CommandTestUtil.INVALID_MATRICULATION_DESC;
import static atas.logic.commands.CommandTestUtil.INVALID_NAME_DESC;
import static atas.logic.commands.CommandTestUtil.INVALID_TAG_DESC;
import static atas.logic.commands.CommandTestUtil.MATRICULATION_DESC_AMY;
import static atas.logic.commands.CommandTestUtil.MATRICULATION_DESC_BOB;
import static atas.logic.commands.CommandTestUtil.NAME_DESC_AMY;
import static atas.logic.commands.CommandTestUtil.TAG_DESC_FRIEND;
import static atas.logic.commands.CommandTestUtil.TAG_DESC_HUSBAND;
import static atas.logic.commands.CommandTestUtil.VALID_EMAIL_AMY;
import static atas.logic.commands.CommandTestUtil.VALID_EMAIL_BOB;
import static atas.logic.commands.CommandTestUtil.VALID_MATRICULATION_AMY;
import static atas.logic.commands.CommandTestUtil.VALID_MATRICULATION_BOB;
import static atas.logic.commands.CommandTestUtil.VALID_NAME_AMY;
import static atas.logic.commands.CommandTestUtil.VALID_TAG_FRIEND;
import static atas.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;
import static atas.testutil.TypicalIndexes.INDEX_FIRST_STUDENT;
import static atas.testutil.TypicalIndexes.INDEX_SECOND_STUDENT;
import static atas.testutil.TypicalIndexes.INDEX_THIRD_STUDENT;

import org.junit.jupiter.api.Test;

import atas.commons.core.index.Index;
import atas.logic.commands.studentlist.EditStudentCommand;
import atas.logic.commands.studentlist.EditStudentCommand.EditStudentDescriptor;
import atas.model.student.Email;
import atas.model.student.Matriculation;
import atas.model.student.Name;
import atas.model.tag.Tag;
import atas.testutil.EditStudentDescriptorBuilder;

public class EditStudentCommandParserTest {

    private static final String TAG_EMPTY = " " + CliSyntax.PREFIX_TAG;

    private static final String MESSAGE_INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditStudentCommand.MESSAGE_USAGE);

    private EditStudentCommandParser parser = new EditStudentCommandParser();

    @Test
    public void parse_missingParts_failure() {
        // no index specified
        CommandParserTestUtil.assertParseFailure(parser, VALID_NAME_AMY, MESSAGE_INVALID_FORMAT);

        // no field specified
        CommandParserTestUtil.assertParseFailure(parser, "1", EditStudentCommand.MESSAGE_NOT_EDITED);

        // no index and no field specified
        CommandParserTestUtil.assertParseFailure(parser, "", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_invalidPreamble_failure() {
        // negative index
        CommandParserTestUtil.assertParseFailure(parser, "-5" + NAME_DESC_AMY, MESSAGE_INVALID_FORMAT);

        // zero index
        CommandParserTestUtil.assertParseFailure(parser, "0" + NAME_DESC_AMY, MESSAGE_INVALID_FORMAT);

        // invalid arguments being parsed as preamble
        CommandParserTestUtil.assertParseFailure(parser, "1 some random string", MESSAGE_INVALID_FORMAT);

        // invalid prefix being parsed as preamble
        CommandParserTestUtil.assertParseFailure(parser, "1 i/ string", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_invalidValue_failure() {
        // invalid name
        CommandParserTestUtil.assertParseFailure(parser, "1" + INVALID_NAME_DESC, Name.MESSAGE_CONSTRAINTS);
        // invalid matriculation
        CommandParserTestUtil.assertParseFailure(parser, "1" + INVALID_MATRICULATION_DESC,
                Matriculation.MESSAGE_CONSTRAINTS);
        // invalid email
        CommandParserTestUtil.assertParseFailure(parser, "1" + INVALID_EMAIL_DESC, Email.MESSAGE_CONSTRAINTS);
        // invalid tag
        CommandParserTestUtil.assertParseFailure(parser, "1" + INVALID_TAG_DESC, Tag.MESSAGE_CONSTRAINTS);

        // invalid phone followed by valid email
        CommandParserTestUtil.assertParseFailure(parser, "1" + INVALID_MATRICULATION_DESC + EMAIL_DESC_AMY,
                Matriculation.MESSAGE_CONSTRAINTS);

        // valid phone followed by invalid matriculation. The test case for invalid phone followed by valid phone
        // is tested at {@code parse_invalidValueFollowedByValidValue_success()}
        CommandParserTestUtil.assertParseFailure(parser, "1" + MATRICULATION_DESC_BOB
                        + INVALID_MATRICULATION_DESC, Matriculation.MESSAGE_CONSTRAINTS);

        // while parsing {@code PREFIX_TAG} alone will reset the tags of the {@code Student} being edited,
        // parsing it together with a valid tag results in error
        CommandParserTestUtil.assertParseFailure(parser, "1" + TAG_DESC_FRIEND + TAG_DESC_HUSBAND + TAG_EMPTY,
                Tag.MESSAGE_CONSTRAINTS);
        CommandParserTestUtil.assertParseFailure(parser, "1" + TAG_DESC_FRIEND + TAG_EMPTY + TAG_DESC_HUSBAND,
                Tag.MESSAGE_CONSTRAINTS);
        CommandParserTestUtil.assertParseFailure(parser, "1" + TAG_EMPTY + TAG_DESC_FRIEND + TAG_DESC_HUSBAND,
                Tag.MESSAGE_CONSTRAINTS);

        // multiple invalid values, but only the first invalid value is captured
        CommandParserTestUtil.assertParseFailure(parser, "1" + INVALID_NAME_DESC + INVALID_EMAIL_DESC
                        + VALID_MATRICULATION_AMY, Name.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_allFieldsSpecified_success() {
        Index targetIndex = INDEX_SECOND_STUDENT;
        String userInput = targetIndex.getOneBased() + MATRICULATION_DESC_BOB + TAG_DESC_HUSBAND
                + EMAIL_DESC_AMY + NAME_DESC_AMY + TAG_DESC_FRIEND;

        EditStudentDescriptor descriptor = new EditStudentDescriptorBuilder().withName(VALID_NAME_AMY)
                .withMatriculation(VALID_MATRICULATION_BOB).withEmail(VALID_EMAIL_AMY)
                .withTags(VALID_TAG_HUSBAND, VALID_TAG_FRIEND).build();
        EditStudentCommand expectedCommand = new EditStudentCommand(targetIndex, descriptor);

        CommandParserTestUtil.assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_someFieldsSpecified_success() {
        Index targetIndex = INDEX_FIRST_STUDENT;
        String userInput = targetIndex.getOneBased() + MATRICULATION_DESC_BOB + EMAIL_DESC_AMY;

        EditStudentDescriptor descriptor = new EditStudentDescriptorBuilder().withMatriculation(VALID_MATRICULATION_BOB)
                .withEmail(VALID_EMAIL_AMY).build();
        EditStudentCommand expectedCommand = new EditStudentCommand(targetIndex, descriptor);

        CommandParserTestUtil.assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_oneFieldSpecified_success() {
        // name
        Index targetIndex = INDEX_THIRD_STUDENT;
        String userInput = targetIndex.getOneBased() + NAME_DESC_AMY;
        EditStudentCommand.EditStudentDescriptor
            descriptor = new EditStudentDescriptorBuilder().withName(VALID_NAME_AMY).build();
        EditStudentCommand expectedCommand = new EditStudentCommand(targetIndex, descriptor);
        CommandParserTestUtil.assertParseSuccess(parser, userInput, expectedCommand);

        // matriculation
        userInput = targetIndex.getOneBased() + MATRICULATION_DESC_AMY;
        descriptor = new EditStudentDescriptorBuilder().withMatriculation(VALID_MATRICULATION_AMY).build();
        expectedCommand = new EditStudentCommand(targetIndex, descriptor);
        CommandParserTestUtil.assertParseSuccess(parser, userInput, expectedCommand);

        // email
        userInput = targetIndex.getOneBased() + EMAIL_DESC_AMY;
        descriptor = new EditStudentDescriptorBuilder().withEmail(VALID_EMAIL_AMY).build();
        expectedCommand = new EditStudentCommand(targetIndex, descriptor);
        CommandParserTestUtil.assertParseSuccess(parser, userInput, expectedCommand);

        // tags
        userInput = targetIndex.getOneBased() + TAG_DESC_FRIEND;
        descriptor = new EditStudentDescriptorBuilder().withTags(VALID_TAG_FRIEND).build();
        expectedCommand = new EditStudentCommand(targetIndex, descriptor);
        CommandParserTestUtil.assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_multipleRepeatedFields_acceptsLast() {
        Index targetIndex = INDEX_FIRST_STUDENT;
        String userInput = targetIndex.getOneBased() + MATRICULATION_DESC_AMY + EMAIL_DESC_AMY
                + TAG_DESC_FRIEND + MATRICULATION_DESC_AMY + EMAIL_DESC_AMY + TAG_DESC_FRIEND
                + MATRICULATION_DESC_BOB + EMAIL_DESC_BOB + TAG_DESC_HUSBAND;

        EditStudentDescriptor descriptor = new EditStudentDescriptorBuilder().withMatriculation(VALID_MATRICULATION_BOB)
                .withEmail(VALID_EMAIL_BOB).withTags(VALID_TAG_FRIEND, VALID_TAG_HUSBAND).build();
        EditStudentCommand expectedCommand = new EditStudentCommand(targetIndex, descriptor);

        CommandParserTestUtil.assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_invalidValueFollowedByValidValue_success() {
        // no other valid values specified
        Index targetIndex = INDEX_FIRST_STUDENT;
        String userInput = targetIndex.getOneBased() + INVALID_MATRICULATION_DESC + MATRICULATION_DESC_BOB;
        EditStudentDescriptor descriptor = new EditStudentDescriptorBuilder()
                .withMatriculation(VALID_MATRICULATION_BOB).build();
        EditStudentCommand expectedCommand = new EditStudentCommand(targetIndex, descriptor);
        CommandParserTestUtil.assertParseSuccess(parser, userInput, expectedCommand);

        // other valid values specified
        userInput = targetIndex.getOneBased() + EMAIL_DESC_BOB + INVALID_MATRICULATION_DESC + MATRICULATION_DESC_BOB;
        descriptor = new EditStudentDescriptorBuilder().withMatriculation(VALID_MATRICULATION_BOB)
                .withEmail(VALID_EMAIL_BOB).build();
        expectedCommand = new EditStudentCommand(targetIndex, descriptor);
        CommandParserTestUtil.assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_resetTags_success() {
        Index targetIndex = INDEX_THIRD_STUDENT;
        String userInput = targetIndex.getOneBased() + TAG_EMPTY;

        EditStudentDescriptor descriptor = new EditStudentDescriptorBuilder().withTags().build();
        EditStudentCommand expectedCommand = new EditStudentCommand(targetIndex, descriptor);

        CommandParserTestUtil.assertParseSuccess(parser, userInput, expectedCommand);
    }
}
