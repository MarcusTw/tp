package atas.logic;

import static atas.commons.core.Messages.MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX;
import static atas.commons.core.Messages.MESSAGE_UNKNOWN_COMMAND;
import static atas.logic.commands.CommandTestUtil.EMAIL_DESC_AMY;
import static atas.logic.commands.CommandTestUtil.MATRICULATION_DESC_AMY;
import static atas.logic.commands.CommandTestUtil.NAME_DESC_AMY;
import static atas.logic.commands.confirmation.ConfirmationCommand.ACCEPT_COMMAND_FULL;
import static atas.logic.parser.CliSyntax.PREFIX_TAG;
import static atas.testutil.Assert.assertThrows;
import static atas.testutil.TypicalMemoContents.EMPTY_MEMO_CONTENT;
import static atas.testutil.TypicalMemoContents.SAMPLE_MEMO_CONTENT_ONE;
import static atas.testutil.TypicalStudents.AMY;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import atas.commons.core.index.Index;
import atas.logic.commands.CommandResult;
import atas.logic.commands.exceptions.CommandException;
import atas.logic.commands.studentlist.AddStudentCommand;
import atas.logic.commands.studentlist.DeleteStudentCommand;
import atas.logic.commands.studentlist.EditStudentCommand;
import atas.logic.commands.studentlist.ListStudentsCommand;
import atas.logic.parser.exceptions.ParseException;
import atas.model.Model;
import atas.model.ModelManager;
import atas.model.UserPrefs;
import atas.model.memo.Memo;
import atas.model.session.ReadOnlySessionList;
import atas.model.student.ReadOnlyStudentList;
import atas.model.student.Student;
import atas.storage.JsonAtasStorage;
import atas.storage.JsonSessionListStorage;
import atas.storage.JsonUserPrefsStorage;
import atas.storage.StorageManager;
import atas.storage.TxtMemoStorage;
import atas.testutil.StudentBuilder;


public class LogicManagerTest {
    private static final IOException DUMMY_IO_EXCEPTION = new IOException("dummy exception");

    @TempDir
    public Path temporaryFolder;

    private Model model = new ModelManager();
    private Logic logic;

    @BeforeEach
    public void setUp() {
        JsonSessionListStorage jsonSessionListStorage =
                new JsonSessionListStorage(temporaryFolder.resolve("typicalSessionSessionList.json"));
        JsonAtasStorage studentListStorage =
                new JsonAtasStorage(temporaryFolder.resolve("studentList.json"));
        JsonUserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(temporaryFolder.resolve("userPrefs.json"));
        TxtMemoStorage memoStorage = new TxtMemoStorage(temporaryFolder.resolve("memo.txt"));
        StorageManager storage = new StorageManager(
                jsonSessionListStorage, studentListStorage, userPrefsStorage, memoStorage);
        logic = new LogicManager(model, storage);
    }

    @Test
    public void execute_invalidCommandFormat_throwsParseException() {
        String invalidCommand = "uicfhmowqewca";
        assertParseException(invalidCommand, MESSAGE_UNKNOWN_COMMAND);
    }

    @Test
    public void execute_commandExecutionError_throwsCommandException() throws CommandException, ParseException {
        Index outOfBoundIndex = Index.fromOneBased(model.getNumberOfStudents() + 1);
        int outOfBoundIndexOneBased = outOfBoundIndex.getOneBased();

        String deleteStudentCommand = DeleteStudentCommand.COMMAND_WORD + " " + outOfBoundIndexOneBased;
        assertCommandException(deleteStudentCommand, MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX);
        //assertCommandException(ACCEPT_COMMAND_FULL, MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX);

        String editStudentCommand = EditStudentCommand.COMMAND_WORD + " " + outOfBoundIndexOneBased + " "
                + PREFIX_TAG + "newTag";
        assertCommandException(editStudentCommand, MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX);
        //assertCommandException(ACCEPT_COMMAND_FULL, MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX);
    }

    @Test
    public void execute_validCommand_success() throws Exception {
        String listStudentsCommand = ListStudentsCommand.COMMAND_WORD;
        assertCommandSuccess(listStudentsCommand, ListStudentsCommand.MESSAGE_SUCCESS, model);
    }

    @Test
    public void execute_storageThrowsIoException_throwsCommandException() {
        // Setup LogicManager with JsonStudentListIoExceptionThrowingStub
        JsonSessionListStorage jsonSessionListStorage =
                new JsonSessionListIoExceptionThrowingStub(temporaryFolder.resolve("ioExceptionSessionList.json"));
        JsonAtasStorage studentListStorage =
                new JsonAtasIoExceptionThrowingStub(temporaryFolder.resolve("ioExceptionStudentList.json"));
        JsonUserPrefsStorage userPrefsStorage =
                new JsonUserPrefsStorage(temporaryFolder.resolve("ioExceptionUserPrefs.json"));
        TxtMemoStorage memoStorage = new TxtMemoIoExceptionThrowingStub(temporaryFolder.resolve("ioExceptionMemo.txt"));
        StorageManager storage = new StorageManager(
                jsonSessionListStorage, studentListStorage, userPrefsStorage, memoStorage);
        logic = new LogicManager(model, storage);

        // Execute add command
        String addStudentCommand = AddStudentCommand.COMMAND_WORD + NAME_DESC_AMY
                + MATRICULATION_DESC_AMY + EMAIL_DESC_AMY;
        Student expectedStudent = new StudentBuilder(AMY).withTags().build();

        ModelManager expectedModel = new ModelManager();
        expectedModel.addStudent(expectedStudent);
        String expectedMessage = LogicManager.FILE_OPS_ERROR_MESSAGE + DUMMY_IO_EXCEPTION;
        assertCommandFailure(addStudentCommand, CommandException.class, expectedMessage, expectedModel);
    }

    @Test
    public void getFilteredStudentList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> logic.getFilteredStudentList().remove(0));
    }

    @Test
    public void getters() {
        JsonSessionListStorage jsonSessionListStorage =
                new JsonSessionListStorage(temporaryFolder.resolve("typicalSessionSessionList.json"));
        JsonAtasStorage atasStorage =
                new JsonAtasStorage(temporaryFolder.resolve("atas.json"));
        JsonUserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(temporaryFolder.resolve("userPrefs.json"));
        TxtMemoStorage memoStorage = new TxtMemoStorage(temporaryFolder.resolve("memo.txt"));
        StorageManager storage = new StorageManager(
                jsonSessionListStorage, atasStorage, userPrefsStorage, memoStorage);
        LogicManager logicManager = new LogicManager(model, storage);

        logic.enableCurrentSession();
        logic.disableCurrentSession();

        assertEquals(logicManager.getStudentList(), logic.getStudentList());
        assertEquals(logicManager.getFilteredStudentList(), logic.getFilteredStudentList());
        assertEquals(logicManager.getFilteredSessionList(), logic.getFilteredSessionList());
        assertEquals(logicManager.getStudentListFilePath(), logic.getStudentListFilePath());
        assertEquals(logicManager.getGuiSettings(), logic.getGuiSettings());
        assertEquals(logicManager.getMemoContent(), logic.getMemoContent());
    }

    @Test
    public void testSaveMemoContent() throws CommandException {
        Memo memo = model.getMemo();
        logic.saveMemoContent(SAMPLE_MEMO_CONTENT_ONE);
        assertEquals(SAMPLE_MEMO_CONTENT_ONE, memo.getContent());
    }

    /**
     * Executes the command and confirms that
     * - no exceptions are thrown <br>
     * - the feedback message is equal to {@code expectedMessage} <br>
     * - the internal model manager state is the same as that in {@code expectedModel} <br>
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandSuccess(String inputCommand, String expectedMessage,
            Model expectedModel) throws CommandException, ParseException {
        CommandResult result = logic.execute(inputCommand);
        assertEquals(expectedMessage, result.getFeedbackToUser());
        assertEquals(expectedModel, model);
    }

    /**
     * Executes the command, confirms that a ParseException is thrown and that the result message is correct.
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertParseException(String inputCommand, String expectedMessage) {
        assertCommandFailure(inputCommand, ParseException.class, expectedMessage);
    }

    /**
     * Executes the command, confirms that a CommandException is thrown and that the result message is correct.
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandException(String inputCommand, String expectedMessage) {
        assertCommandFailure(inputCommand, CommandException.class, expectedMessage);
    }

    /**
     * Executes the command, confirms that the exception is thrown and that the result message is correct.
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandFailure(String inputCommand, Class<? extends Throwable> expectedException,
            String expectedMessage) {
        Model expectedModel = new ModelManager(model.getSessionList(),
                model.getStudentList(), new UserPrefs(), EMPTY_MEMO_CONTENT);
        assertCommandFailure(inputCommand, expectedException, expectedMessage, expectedModel);
    }

    /**
     * Executes the command and confirms that
     * - the {@code expectedException} is thrown <br>
     * - the resulting error message is equal to {@code expectedMessage} <br>
     * - the internal model manager state is the same as that in {@code expectedModel} <br>
     * @see #assertCommandSuccess(String, String, Model)
     */
    private void assertCommandFailure(String inputCommand, Class<? extends Throwable> expectedException,
            String expectedMessage, Model expectedModel) {
        assertThrows(expectedException, expectedMessage, () -> logic.execute(inputCommand));
        assertEquals(expectedModel, model);
    }

    /**
     * A stub class to throw an {@code IOException} when the save method is called.
     */
    private static class JsonAtasIoExceptionThrowingStub extends JsonAtasStorage {
        private JsonAtasIoExceptionThrowingStub(Path filePath) {
            super(filePath);
        }

        @Override
        public void saveStudentList(ReadOnlyStudentList studentList, Path filePath) throws IOException {
            throw DUMMY_IO_EXCEPTION;
        }
    }

    /**
     * A stub class to throw an {@code IOException} when the save method is called.
     */
    private static class JsonSessionListIoExceptionThrowingStub extends JsonSessionListStorage {
        private JsonSessionListIoExceptionThrowingStub(Path filePath) {
            super(filePath);
        }

        @Override
        public void saveSessionList(ReadOnlySessionList sessionList, Path filePath) throws IOException {
            throw DUMMY_IO_EXCEPTION;
        }
    }

    /**
     * A stub class to throw an {@code IOException} when the save method is called.
     */
    private static class TxtMemoIoExceptionThrowingStub extends TxtMemoStorage {
        private TxtMemoIoExceptionThrowingStub(Path filePath) {
            super(filePath);
        }

        @Override
        public void saveMemo(Memo memo, Path filePath) throws IOException {
            throw DUMMY_IO_EXCEPTION;
        }
    }
}
