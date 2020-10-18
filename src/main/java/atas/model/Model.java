package atas.model;

import java.nio.file.Path;
import java.util.function.Predicate;

import atas.commons.core.GuiSettings;
import atas.commons.core.index.Index;
import atas.model.attendance.Attributes;
import atas.model.attendance.IndexRange;
import atas.model.attendance.Session;
import atas.model.attendance.SessionList;
import atas.model.attendance.SessionName;
import atas.model.memo.Memo;
import atas.model.student.Student;
import javafx.collections.ObservableList;

/**
 * The API of the Model component.
 */
public interface Model {
    /** {@code Predicate} that always evaluate to true */
    Predicate<Student> PREDICATE_SHOW_ALL_STUDENTS = unused -> true;

    Predicate<Session> PREDICATE_SHOW_ALL_SESSIONS = unused -> true;

    /**
     * Replaces user prefs data with the data in {@code userPrefs}.
     */
    void setUserPrefs(ReadOnlyUserPrefs userPrefs);

    /**
     * Returns the user prefs.
     */
    ReadOnlyUserPrefs getUserPrefs();

    /**
     * Returns the user prefs' GUI settings.
     */
    GuiSettings getGuiSettings();

    /**
     * Sets the user prefs' GUI settings.
     */
    void setGuiSettings(GuiSettings guiSettings);

    /**
     * Returns the user prefs' student list file path.
     */
    Path getStudentListFilePath();

    /**
     * Sets the user prefs' student list file path.
     */
    void setStudentListFilePath(Path studentListFilePath);

    /**
     * Returns the user prefs' session list file path.
     */
    Path getSessionListFilePath();

    /**
     * Sets the user prefs' session list file path.
     */
    void setSessionListFilePath(Path sessionListFilePath);

    /**
     * Returns the user prefs' memo file path.
     */
    Path getMemoFilePath();

    /**
     * Sets the user prefs' memo file path.
     */
    void setMemoFilePath(Path memoPadFilePath);

    /**
     * Replaces student list data with the data in {@code studentList}.
     */
    void setStudentList(ReadOnlyStudentList studentList);

    /** Returns the StudentList */
    ReadOnlyStudentList getStudentList();

    /**
     * Returns true if a student with the same identity as {@code student} exists in the student list.
     */
    boolean hasStudent(Student student);

    /**
     * Deletes the given student.
     * The student must exist in the student list.
     */
    void deleteStudent(Student target, Index id);

    /**
     * Adds the given student.
     * {@code student} must not already exist in the student list.
     */
    void addStudent(Student student);

    /**
     * Replaces the given student {@code target} with {@code editedStudent}.
     * {@code target} must exist in the student list.
     * The identity of {@code editedStudent} must not be the same as another existing student in the student list.
     */
    void setStudent(Student target, Student editedStudent);


    /**
     * CLear the sessions existing in the current session list.
     * The link between the students and the session list will not be disrupted.
     */
    void resetSessionList();

    /** Returns the SessionList */
    SessionList getSessionList();

    /**
     * Returns true if a session with the same identity as {@code session} exists in the session list.
     */
    boolean hasSession(Session session);

    /**
     * Deletes the given session.
     * The session must exist in the session list.
     */
    void deleteSession(Session target, Index id);

    /**
     * Adds the given session.
     * {@code session} must not already exist in the session list.
     */
    void addSession(Session session);

    /**
     * Adds the given session Index.
     * {@code session index} must not already exist in the session list.
     */
    void enterSession(Index sessionIndex);

    /**
     * Returns attributes of the students given session.
     * The session must exist in the session list.
     * @return Attributes of the students in the session.
     */
    ObservableList<Attributes> getFilteredAttributesList();

    /**
     * Replaces the given session {@code target} with {@code editedSession}.
     * {@code target} must exist in the session list.
     * The session identity of {@code editedSession} must not be the same as another
     * existing session in the session list.
     */
    void setSession(Session target, Session editedSession);

    /**
     * Updates participation status of the given session.
     */
    void updateParticipationBySessionName(SessionName sessionName, IndexRange indexRange);

    /**
     * Updates presence status of the given session.
     */
    void updatePresenceBySessionName(SessionName sessionName, IndexRange indexRange);


    /** Returns an unmodifiable view of the filtered student list */
    ObservableList<Student> getFilteredStudentList();

    /** Returns an unmodifiable view of the filtered session list */
    ObservableList<Session> getFilteredSessionList();

    /**
     * Updates the filter of the filtered student list to filter by the given {@code predicate}.
     * @throws NullPointerException if {@code predicate} is null.
     */
    void updateFilteredStudentList(Predicate<Student> predicate);

    /**
     * Updates the filter of the filtered session list to filter by the given {@code predicate}.
     * @throws NullPointerException if {@code predicate} is null.
     */
    void updateFilteredSessionList(Predicate<Session> predicate);

    /**
     * Sets currentSession to be false.
     */
    void setCurrentSessionFalse();

    /**
     * Sets currentSession to be true.
     */
    void setCurrentSessionTrue();

    /**
     * Returns currentSession status.
     */
    boolean returnCurrentSessionEnabledStatus();

    /**
     * Returns the Memo.
     */
    Memo getMemo();

    /**
     * Returns the index of a randomly selected student in the student list.
     */
    Index generateRandomStudentIndex();
}
