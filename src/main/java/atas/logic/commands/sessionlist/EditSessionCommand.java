package atas.logic.commands.sessionlist;

import static atas.commons.core.Messages.MESSAGE_INVALID_SESSION_DISPLAYED_INDEX;
import static atas.commons.util.CollectionUtil.isAnyNonNull;
import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Optional;

import atas.commons.core.index.Index;
import atas.logic.commands.CommandResult;
import atas.logic.commands.confirmation.DangerousCommand;
import atas.logic.commands.exceptions.CommandException;
import atas.logic.parser.CliSyntax;
import atas.model.Model;
import atas.model.session.Session;
import atas.model.session.SessionDate;
import atas.model.session.SessionName;

public class EditSessionCommand extends DangerousCommand implements IndexedSessionListCommand {

    public static final String COMMAND_WORD = "editses";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Edits the details of the session identified "
            + "by the index number used in the displayed session list. "
            + "Existing values will be overwritten by the input values.\n"
            + "Parameters: INDEX (must be a positive integer) "
            + "[" + CliSyntax.PREFIX_SESSION_NAME + "SESSION_NAME] "
            + "[" + CliSyntax.PREFIX_SESSION_DATE + "DATE] \n"
            + "Example: " + COMMAND_WORD + " 1 "
            + CliSyntax.PREFIX_SESSION_NAME + "Tutorial 1 "
            + CliSyntax.PREFIX_SESSION_DATE + "10/10/2020";

    public static final String MESSAGE_EDIT_SESSION_SUCCESS = "Edited session: %1$s";

    public static final String MESSAGE_NOT_EDITED = "At least one field to edit must be provided.";
    public static final String MESSAGE_DUPLICATE_SESSION = "This session already exists in the session list.";

    private final Index targetIndex;
    private final EditSessionCommand.EditSessionDescriptor editSessionDescriptor;

    /**
     * Parameterized Constructor.
     * @param targetIndex The Index of the session in the filtered session list to edit.
     * @param editSessionDescriptor details to edit the session with
     */
    public EditSessionCommand(Index targetIndex,
                              EditSessionCommand.EditSessionDescriptor editSessionDescriptor) {
        requireNonNull(targetIndex);
        requireNonNull(editSessionDescriptor);

        this.targetIndex = targetIndex;
        this.editSessionDescriptor = new EditSessionCommand.EditSessionDescriptor(editSessionDescriptor);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Session> lastShownList = model.getFilteredSessionList();

        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(MESSAGE_INVALID_SESSION_DISPLAYED_INDEX);
        }

        Session sessionToEdit = lastShownList.get(targetIndex.getZeroBased());
        Session editedSession = createEditedSession(sessionToEdit, editSessionDescriptor);

        if (!sessionToEdit.isSameSession(editedSession) && model.hasSession(editedSession)) {
            throw new CommandException(MESSAGE_DUPLICATE_SESSION);
        }

        model.setSession(sessionToEdit, editedSession);
        model.commit();
        model.updateFilteredStudentList(Model.PREDICATE_SHOW_ALL_STUDENTS);
        return new CommandResult(String.format(MESSAGE_EDIT_SESSION_SUCCESS, editedSession));
    }

    @Override
    public Index getTargetIndex() {
        return targetIndex;
    }

    @Override
    public String toString() {
        String oneBasedIndex = String.valueOf(targetIndex.getOneBased());
        return "Edit " + oneBasedIndex;
    }

    /**
     * Creates and returns a {@code Session} with the details of {@code sessionToEdit}
     * edited with {@code editSessionDescriptor}.
     */
    private static Session createEditedSession(Session sessionToEdit,
                                              EditSessionCommand.EditSessionDescriptor editSessionDescriptor) {
        assert sessionToEdit != null;

        SessionName updatedSessionName = editSessionDescriptor.getSessionName().orElse(sessionToEdit.getSessionName());
        SessionDate updatedSessionDate = editSessionDescriptor.getSessionDate().orElse(sessionToEdit.getSessionDate());

        Session editedSession = new Session(updatedSessionName, updatedSessionDate);
        editedSession.getAttributeList().setAll(sessionToEdit.getAttributeList());
        return editedSession;
    }

    @Override
    public boolean equals(Object other) {
        // short circuit if same object
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof EditSessionCommand)) {
            return false;
        }

        // state check
        EditSessionCommand e = (EditSessionCommand) other;
        return targetIndex.equals(e.targetIndex)
                && editSessionDescriptor.equals(e.editSessionDescriptor);
    }

    /**
     * Stores the details to edit the session with. Each non-empty field value will replace the
     * corresponding field value of the student.
     */
    public static class EditSessionDescriptor {
        private SessionName sessionName;
        private SessionDate sessionDate;

        public EditSessionDescriptor() {}

        /**
         * Copy constructor.
         */
        public EditSessionDescriptor(EditSessionCommand.EditSessionDescriptor toCopy) {
            setSessionName(toCopy.sessionName);
            setSessionDate(toCopy.sessionDate);
        }

        /**
         * Returns true if at least one field is edited.
         */
        public boolean isAnyFieldEdited() {
            return isAnyNonNull(sessionName, sessionDate);
        }

        public Optional<SessionName> getSessionName() {
            return Optional.ofNullable(sessionName);
        }

        public void setSessionName(SessionName sessionName) {
            this.sessionName = sessionName;
        }

        public Optional<SessionDate> getSessionDate() {
            return Optional.ofNullable(sessionDate);
        }

        public void setSessionDate(SessionDate sessionDate) {
            this.sessionDate = sessionDate;
        }

        @Override
        public boolean equals(Object other) {
            // short circuit if same object
            if (other == this) {
                return true;
            }

            // instanceof handles different types
            if (!(other instanceof EditSessionCommand.EditSessionDescriptor)) {
                return false;
            }

            // state check
            EditSessionCommand.EditSessionDescriptor e = (EditSessionCommand.EditSessionDescriptor) other;

            return getSessionName().equals(e.getSessionName())
                    && getSessionDate().equals(e.getSessionDate());
        }
    }
}
