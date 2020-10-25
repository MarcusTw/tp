package atas.model.session;

import static atas.commons.util.CollectionUtil.requireAllNonNull;
import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import atas.commons.core.index.Index;
import atas.model.session.exceptions.DuplicateSessionException;
import atas.model.session.exceptions.SessionNotFoundException;
import atas.model.student.Student;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Represents a collection of all the sessions in the semester.
 */
public class SessionList implements Iterable<Session>, ReadOnlySessionList {

    private final ObservableList<Session> sessions;
    private final ObservableList<Student> internalStudentList;

    /**
     * Creates an SessionList using the sessions in the {@code list}
     */
    public SessionList() {
        sessions = FXCollections.observableArrayList();
        internalStudentList = FXCollections.observableArrayList();
    }

    /**
     * Creates an SessionList using the sessions in the {@code list}
     */
    public SessionList(List<Student> list) {
        sessions = FXCollections.observableArrayList();
        internalStudentList = FXCollections.observableArrayList(list);
    }

    /**
     * Copies the session in {@code toBeCopied} in to a new list
     */
    public SessionList(ReadOnlySessionList toBeCopied) {
        this();
        resetData(toBeCopied);
    }

    /**
     * Replaces the contents of the session list with {@code sessions}.
     * {@code sessions} must not contain duplicate students.
     */
    public void resetData(ReadOnlySessionList newData) {
        requireNonNull(newData);

        setSessions(newData.getCopy().getSessions());
        setStudents(newData.getInternalStudentList());
    }

    /**
     * Replaces the contents of this list with {@code sessions}.
     * {@code sessions} must not contain duplicate sessions.
     */
    private void setSessions(List<Session> sessions) {
        requireAllNonNull(sessions);
        if (!sessionsAreUnique(sessions)) {
            throw new DuplicateSessionException();
        }
        this.sessions.setAll(sessions);
        FXCollections.sort(this.sessions);
    }

    /**
     * Returns true if {@code sessions} contains only unique sessions.
     */
    private boolean sessionsAreUnique(List<Session> sessions) {
        for (int i = 0; i < sessions.size() - 1; i++) {
            for (int j = i + 1; j < sessions.size(); j++) {
                if (sessions.get(i).isSameSession(sessions.get(j))) {
                    return false;
                }
            }
        }
        return true;
    }

    private void setStudents(List<Student> students) {
        requireAllNonNull(students);
        this.internalStudentList.setAll(students);
    }

    @Override
    public SessionList getCopy() {
        SessionList copy = new SessionList();
        List<Session> sessionListCopy = new ArrayList<>();
        for (Session s : sessions) {
            sessionListCopy.add(s.getCopy());
        }
        copy.sessions.addAll(sessionListCopy);
        copy.internalStudentList.addAll(internalStudentList);
        return copy;
    }

    /**
     * Updates the student list of the session.
     */
    @Override
    public void updateStudentList(List<Student> list) {
        internalStudentList.clear();
        internalStudentList.addAll(list);
    }

    /**
     * Adds a session to the list.
     * The session must not already exist in the list.
     */
    public void addSession(Session session) {
        requireNonNull(session);
        if (contains(session)) {
            throw new DuplicateSessionException();
        }
        session.initializeSession(internalStudentList);
        sessions.add(session);
        FXCollections.sort(this.sessions);
    }

    /**
     * Deletes a session from the list.
     * The session must be already in the list.
     */
    public void deleteSession(Session target) {
        requireNonNull(target);
        if (!sessions.remove(target)) {
            throw new SessionNotFoundException();
        }
        FXCollections.sort(this.sessions);
    }

    public void setSession(Session oldSession, Session newSession) {
        requireNonNull(oldSession);
        requireNonNull(newSession);

        if (!contains(oldSession)) {
            throw new SessionNotFoundException();
        }

        sessions.removeIf(oldSession::isSameSession);
        sessions.add(newSession);
        FXCollections.sort(this.sessions);
    }

    public Session getSessionBasedOnId(Index index) {
        return sessions.get(index.getZeroBased());
    }

    /**
     * Returns true if the collection contains an equivalent session as the given argument.
     */
    public boolean contains(Session toCheck) {
        requireNonNull(toCheck);
        return sessions.stream().anyMatch(toCheck::isSameSession);
    }

    /**
     * Updates all sessions after the deletion of a student (with a given student ID)
     */
    public void updateAllSessionsAfterDelete(Index studentId) {
        requireNonNull(studentId);
        for (Session s : sessions) {
            s.updateSessionAfterDelete(studentId, internalStudentList);
        }
        FXCollections.sort(this.sessions);
    }

    /**
     * Updates all sessions after adding a student
     */
    public void updateAllSessionsAfterAdd() {
        for (Session s : sessions) {
            s.updateSessionAfterAdd(internalStudentList);
        }
        FXCollections.sort(this.sessions);
    }

    /**
     * Updates all sessions after the deletion of a session (with a given session ID)
     */
    public void updateAllSessionsAfterDeleteSession(Index sessionId) {
        requireNonNull(sessionId);
        for (int i = sessionId.getZeroBased(); i < sessions.size(); i++) {
            sessions.get(i).getSessionIndex().decreaseZeroBasedIndexByOne();
        }
    }

    /**
     * Finds the session using the given {@code sessionName} and students' participation status
     * according to the {@code indexRange}
     */
    public void updateStudentParticipation(SessionName sessionName, IndexRange indexRange) {
        requireNonNull(sessionName);
        requireNonNull(indexRange);

        for (Session session: sessions) {
            if (session.getSessionName().equals(sessionName)) {
                session.updateParticipation(indexRange);
            }
        }
    }

    /**
     * Finds the session using the given {@code sessionName} and students' presence status
     * according to the {@code indexRange}
     */
    public void updateStudentPresence(SessionName sessionName, IndexRange indexRange) {
        requireNonNull(sessionName);
        requireNonNull(indexRange);

        for (Session session: sessions) {
            if (session.getSessionName().equals(sessionName)) {
                session.updatePresence(indexRange);
            }
        }
    }

    /**
     * Clears all the existing sessions in the session list.
     */
    public void clearSessions() {
        sessions.clear();
    }

    public int returnSize() {
        return sessions.size();
    }

    @Override
    public ObservableList<Session> getSessions() {
        return sessions;
    }

    @Override
    public ObservableList<Student> getInternalStudentList() {
        return internalStudentList;
    }

    @Override
    public Iterator<Session> iterator() {
        return sessions.iterator();
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof SessionList) {

            if (sessions.size() != ((SessionList) other).getSessions().size()) {
                return false;
            }

            Session[] testArray1 = new Session[sessions.size()];
            Session[] testArray2 = new Session[sessions.size()];

            int i = 0;
            int j = 0;
            for (Session s: sessions) {
                testArray1[i] = s;
                i++;
            }
            for (Session s: ((SessionList) other).getSessions()) {
                testArray2[j] = s;
                j++;
            }

            for (int k = 0; k < sessions.size(); k++) {
                if (!testArray1[k].isSameSession(testArray2[k])) {
                    return false;
                }
            }

            return true;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "SessionList{" + "sessions=" + sessions + ", internalStudentList=" + internalStudentList + '}';
    }
}
