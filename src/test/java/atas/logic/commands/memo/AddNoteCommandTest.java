package atas.logic.commands.memo;

import static atas.testutil.Assert.assertThrows;
import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.function.Predicate;

import org.junit.jupiter.api.Test;

import atas.commons.core.GuiSettings;
import atas.commons.core.index.Index;
import atas.logic.commands.CommandResult;
import atas.model.Model;
import atas.model.ReadOnlyUserPrefs;
import atas.model.memo.Memo;
import atas.model.session.Attributes;
import atas.model.session.IndexRange;
import atas.model.session.Session;
import atas.model.session.SessionList;
import atas.model.session.SessionName;
import atas.model.student.ReadOnlyStudentList;
import atas.model.student.Student;
import javafx.collections.ObservableList;

public class AddNoteCommandTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new AddNoteCommand(null));
    }

    @Test
    public void execute_noteAcceptedByModel_addSuccessful() throws Exception {
        String content = "content";
        String note = "note";
        AddNoteCommandTest.ModelStubWithNewMemoContent modelStub = new AddNoteCommandTest
                .ModelStubWithNewMemoContent(content);

        CommandResult commandResult = new AddNoteCommand(note).execute(modelStub);

        assertEquals(AddNoteCommand.MESSAGE_SUCCESS, commandResult.getFeedbackToUser());
        assertEquals(content.concat("\n").concat(note), modelStub.getMemoContent());
    }

    @Test
    public void equals() {
        String note1 = "note1";
        String note2 = "note2";
        AddNoteCommand addNote1Command = new AddNoteCommand(note1);
        AddNoteCommand addNote2Command = new AddNoteCommand(note2);

        // same object -> returns true
        assertTrue(addNote1Command.equals(addNote1Command));

        // same values -> returns true
        AddNoteCommand addNote1CommandCopy = new AddNoteCommand(note1);
        assertTrue(addNote1Command.equals(addNote1CommandCopy));

        // different types -> returns false
        assertFalse(addNote1Command.equals(1));

        // null -> returns false
        assertFalse(addNote1Command.equals(null));

        // different String -> returns false
        assertFalse(addNote1Command.equals(addNote2Command));
    }

    /**
     * A default model stub that have all of the methods failing.
     */
    private class ModelStub implements Model {
        @Override
        public void setUserPrefs(ReadOnlyUserPrefs userPrefs) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ReadOnlyUserPrefs getUserPrefs() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public GuiSettings getGuiSettings() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setGuiSettings(GuiSettings guiSettings) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public Path getStudentListFilePath() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setStudentListFilePath(Path studentListFilePath) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void addStudent(Student student) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void clearStudentList() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ReadOnlyStudentList getStudentList() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public boolean hasStudent(Student student) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void deleteStudent(Student target, Index id) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setStudent(Student target, Student editedStudent) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public Path getSessionListFilePath() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setSessionListFilePath(Path sessionListFilePath) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void resetSessionList() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public SessionList getSessionList() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public boolean hasSession(Session session) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void deleteSession(Session target, Index id) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void addSession(Session session) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void enterSession(Index sessionIndex) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ObservableList<Attributes> getCurrentAttributesList() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public Session getCurrentSession() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void updateCurrentAttributesList() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setSession(Session target, Session editedSession) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void updateParticipationBySessionName(SessionName sessionName, IndexRange indexRange) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void updatePresenceBySessionName(SessionName sessionName, IndexRange indexRange) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ObservableList<Student> getFilteredStudentList() {
            throw new AssertionError("This method should not be called.");
        }

        /**
         * Returns the number of students in the student list.
         */
        @Override
        public int getNumberOfStudents() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ObservableList<Session> getFilteredSessionList() {
            throw new AssertionError("This method should not be called.");
        }

        /**
         * Returns the number of sessions in the session list.
         */
        @Override
        public int getNumberOfSessions() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void updateFilteredStudentList(Predicate<Student> predicate) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void updateFilteredSessionList(Predicate<Session> predicate) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public Index getSessionId() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setCurrentSessionFalse() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setCurrentSessionTrue() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public boolean returnCurrentSessionEnabledStatus() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public Path getMemoFilePath() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setMemoFilePath(Path memoFilePath) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public Memo getMemo() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public String getMemoContent() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void saveMemoContent(String content) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void addNoteToMemo(String note) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public Index generateRandomStudentIndex() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void refreshStatistics() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void refreshStudentStatistics() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void refreshSessionStatistics() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public String getLeftSessionDetails() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public String getRightSessionDetails() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void commit() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public boolean canUndo() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void undo() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public boolean canRedo() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void redo() {
            throw new AssertionError("This method should not be called.");
        }

    }

    /**
     * A Model stub that contains a single .
     */
    private class ModelStubWithNewMemoContent extends AddNoteCommandTest.ModelStub {
        private String content = "original content";

        ModelStubWithNewMemoContent(String content) {
            requireNonNull(content);
            this.content = content;
        }

        @Override
        public void addNoteToMemo(String note) {
            this.content = content.concat("\n").concat(note);
        }

        @Override
        public String getMemoContent() {
            return this.content;
        }
    }
}
