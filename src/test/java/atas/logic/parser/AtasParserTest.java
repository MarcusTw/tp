package atas.logic.parser;

import static atas.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static atas.commons.core.Messages.MESSAGE_UNKNOWN_COMMAND;
import static atas.logic.commands.atas.HelpCommand.MESSAGE_USAGE;
import static atas.testutil.Assert.assertThrows;
import static atas.testutil.TypicalIndexes.INDEX_FIRST_SESSION;
import static atas.testutil.TypicalIndexes.INDEX_FIRST_STUDENT;
import static atas.testutil.TypicalIndexes.INDEX_SECOND_SESSION;
import static atas.testutil.TypicalTabNames.STUDENTS_TAB_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import atas.commons.core.index.Index;
import atas.logic.commands.atas.ExitCommand;
import atas.logic.commands.atas.HelpCommand;
import atas.logic.commands.atas.SwitchCommand;
import atas.logic.commands.confirmation.ConfirmationCommand;
import atas.logic.commands.confirmation.ConfirmationRejectCommand;
import atas.logic.commands.memo.AddNoteCommand;
import atas.logic.commands.sessionlist.AddSessionCommand;
import atas.logic.commands.sessionlist.ClearSessionsCommand;
import atas.logic.commands.sessionlist.DeleteSessionCommand;
import atas.logic.commands.sessionlist.EditSessionCommand;
import atas.logic.commands.sessionlist.EnterSessionCommand;
import atas.logic.commands.sessionlist.session.ParticipateCommand;
import atas.logic.commands.sessionlist.session.PresenceCommand;
import atas.logic.commands.studentlist.AddStudentCommand;
import atas.logic.commands.studentlist.ClearStudentListCommand;
import atas.logic.commands.studentlist.DeleteStudentListCommand;
import atas.logic.commands.studentlist.EditStudentListCommand;
import atas.logic.commands.studentlist.FindStudentsCommand;
import atas.logic.commands.studentlist.ListStudentsCommand;
import atas.logic.parser.exceptions.ParseException;
import atas.model.session.IndexRange;
import atas.model.session.Session;
import atas.model.student.NameContainsKeywordsPredicate;
import atas.model.student.Student;
import atas.testutil.EditSessionDescriptorBuilder;
import atas.testutil.EditStudentDescriptorBuilder;
import atas.testutil.SessionUtil;
import atas.testutil.StudentBuilder;
import atas.testutil.StudentUtil;
import atas.testutil.TypicalSessions;


public class AtasParserTest {

    private final AtasParser parser = new AtasParser();

    @Test
    public void parseCommand_add() throws Exception {
        Student student = new StudentBuilder().build();
        AddStudentCommand command = (AddStudentCommand) parser.parseCommand(StudentUtil.getAddStudentCommand(student));
        assertEquals(new AddStudentCommand(student), command);
    }

    @Test
    public void parseCommand_clear() throws Exception {
        assertTrue(parser.parseCommand(ClearStudentListCommand.COMMAND_WORD) instanceof ConfirmationCommand);
        assertTrue(parser.parseCommand(ConfirmationCommand.REJECT_COMMAND_FULL) instanceof ConfirmationRejectCommand);
        assertTrue(parser.parseCommand(ClearStudentListCommand.COMMAND_WORD + " 3") instanceof ConfirmationCommand);
    }

    @Test
    public void parseCommand_delete() throws Exception {
        ConfirmationCommand command = (ConfirmationCommand) parser.parseCommand(
                DeleteStudentListCommand.COMMAND_WORD + " " + INDEX_FIRST_STUDENT.getOneBased());
        assertEquals(new ConfirmationCommand(new DeleteStudentListCommand(INDEX_FIRST_STUDENT)), command);
    }

    @Test
    public void parseCommand_edit() throws Exception {
        Student student = new StudentBuilder().build();
        EditStudentListCommand.EditStudentDescriptor descriptor = new EditStudentDescriptorBuilder(student).build();
        ConfirmationCommand command = (ConfirmationCommand) parser.parseCommand(
            EditStudentListCommand.COMMAND_WORD + " "
                + INDEX_FIRST_STUDENT.getOneBased() + " " + StudentUtil.getEditStudentDescriptorDetails(descriptor));
        assertEquals(new ConfirmationCommand(new EditStudentListCommand(INDEX_FIRST_STUDENT, descriptor)), command);
    }

    @Test
    public void parseCommand_exit() throws Exception {
        assertTrue(parser.parseCommand(ExitCommand.COMMAND_WORD) instanceof ExitCommand);
        assertTrue(parser.parseCommand(ExitCommand.COMMAND_WORD + " 3") instanceof ExitCommand);
    }

    @Test
    public void parseCommand_find() throws Exception {
        List<String> keywords = Arrays.asList("foo", "bar", "baz");
        FindStudentsCommand command = (FindStudentsCommand) parser.parseCommand(
                FindStudentsCommand.COMMAND_WORD + " " + keywords.stream().collect(Collectors.joining(" ")));
        assertEquals(new FindStudentsCommand(new NameContainsKeywordsPredicate(keywords)), command);
    }

    @Test
    public void parseCommand_help() throws Exception {
        assertTrue(parser.parseCommand(HelpCommand.COMMAND_WORD) instanceof HelpCommand);
        assertTrue(parser.parseCommand(HelpCommand.COMMAND_WORD + " 3") instanceof HelpCommand);
    }

    @Test
    public void parseCommand_list() throws Exception {
        assertTrue(parser.parseCommand(ListStudentsCommand.COMMAND_WORD) instanceof ListStudentsCommand);
        assertTrue(parser.parseCommand(ListStudentsCommand.COMMAND_WORD + " 3") instanceof ListStudentsCommand);
    }

    @Test
    public void parseCommand_switch() throws Exception {
        SwitchCommand command = (SwitchCommand) parser.parseCommand(
                SwitchCommand.COMMAND_WORD + " " + STUDENTS_TAB_NAME);
        assertEquals(new SwitchCommand(STUDENTS_TAB_NAME), command);
    }

    @Test
    public void parseCommand_addSession() throws Exception {
        Session session = TypicalSessions.TUT1;
        AddSessionCommand command = (AddSessionCommand) parser.parseCommand(
                SessionUtil.getAddSessionCommand(session));
        assertEquals(new AddSessionCommand(session), command);
    }

    @Test
    public void parseCommand_deleteSession() throws Exception {
        ConfirmationCommand command = (ConfirmationCommand) parser.parseCommand(
                DeleteSessionCommand.COMMAND_WORD + " " + "1");
        assertEquals(new ConfirmationCommand(new DeleteSessionCommand(INDEX_FIRST_SESSION)), command);
    }

    @Test
    public void parseCommand_editSession() throws Exception {
        Session session = TypicalSessions.SESSION_WEEK_ONE;
        EditSessionCommand.EditSessionDescriptor descriptor = new EditSessionDescriptorBuilder(session).build();
        ConfirmationCommand confirmationEditSessionCommand =
                (ConfirmationCommand) parser.parseCommand(EditSessionCommand.COMMAND_WORD + " "
                + "2" + " " + SessionUtil.getSessionDetails(session));
        EditSessionCommand command = (EditSessionCommand) confirmationEditSessionCommand.getDangerousCommand();
        assertEquals(new EditSessionCommand(INDEX_SECOND_SESSION, descriptor), command);
    }

    @Test
    public void parseCommand_clearSession() throws Exception {
        ConfirmationCommand confirmationClearCommand =
                (ConfirmationCommand) parser.parseCommand(ClearSessionsCommand.COMMAND_WORD);
        ClearSessionsCommand command = (ClearSessionsCommand) confirmationClearCommand.getDangerousCommand();
        assertEquals(new ClearSessionsCommand(), command);
    }

    @Test
    public void parseCommand_enterSession() throws Exception {
        EnterSessionCommand command = (EnterSessionCommand) parser.parseCommand(
                EnterSessionCommand.COMMAND_WORD + " " + "1");
        assertEquals(new EnterSessionCommand(Index.fromOneBased(1)), command);
    }

    @Test
    public void parseCommand_participate() throws Exception {
        ParticipateCommand command = (ParticipateCommand) parser.parseCommand(
                ParticipateCommand.COMMAND_WORD + " 1-3");
        assertEquals(new ParticipateCommand(new IndexRange("1-3")), command);
    }

    @Test
    public void parseCommand_presence() throws Exception {
        PresenceCommand command = (PresenceCommand) parser.parseCommand(
                PresenceCommand.COMMAND_WORD + " 1-3");
        assertEquals(new PresenceCommand(new IndexRange("1-3")), command);
    }

    @Test
    public void parseCommand_addNote() throws Exception {
        AddNoteCommand command = (AddNoteCommand) parser.parseCommand(
                AddNoteCommand.COMMAND_WORD + " note");
        assertEquals(new AddNoteCommand("note"), command);
    }

    @Test
    public void parseCommand_addNote_onlyWhiteSpace() throws Exception {
        AddNoteCommand command = (AddNoteCommand) parser.parseCommand(
                AddNoteCommand.COMMAND_WORD + " \n \t \r ");
        assertEquals(new AddNoteCommand("\n \t \r "), command);
    }

    @Test
    public void parseCommand_unrecognisedInput_throwsParseException() {
        assertThrows(ParseException.class, String.format(MESSAGE_INVALID_COMMAND_FORMAT, MESSAGE_USAGE), ()
            -> parser.parseCommand(""));
    }

    @Test
    public void parseCommand_unknownCommand_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_UNKNOWN_COMMAND, () -> parser.parseCommand("unknownCommand"));
    }
}
