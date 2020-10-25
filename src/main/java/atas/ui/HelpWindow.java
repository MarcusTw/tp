package atas.ui;

import java.util.logging.Logger;

import atas.commons.core.LogsCenter;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.stage.Stage;

/**
 * Controller for a help page
 */
public class HelpWindow extends UiPart<Stage> {

    public static final String USERGUIDE_URL = "https://ay2021s1-cs2103t-w16-4.github.io/tp/UserGuide.html";
    public static final String LINK_MESSAGE = "Link to the full user guide: " + USERGUIDE_URL;

    public static final String COMMAND_LIST_GENERAL = "General:\n"
            + "Get help: help\n"
            + "Switch between tabs: switch TAB_NAME\n"
            + "Generate the name of a randomly selected student: rng\n"
            + "Undo a command: undo\n"
            + "Redo a command: redo\n"
            + "Exit the application: exit\n";

    public static final String COMMAND_LIST_STUDENTS = "Students: \n"
            + "Add a student: addstu n/NAME m/MATRICULATION_NUMBER e/NUS_EMAIL_ADDRESS [t/TAG]…\u200B\n"
            + "List all students: liststu\n"
            + "Find student(s): findstu KEYWORD [MORE_KEYWORDS]\n"
            + "Delete a student: deletestu INDEX\n"
            + "Edit a student's particulars: editstu INDEX [n/UPDATED_NAME] [m/UPDATED_MATRICULATION_NUMBER] "
            + "[e/UPDATED_NUS_EMAIL_ADDRESS] [t/UPDATED_TAG]…\u200B\n"
            + "Clear the student list: clearstu\n";

    public static final String COMMAND_LIST_SESSIONS = "Sessions:\n"
            + "Add a session: addses s/SESSION_NAME d/SESSION_DATE\n"
            + "Delete a session: deleteses INDEX\n"
            + "Edit a session: editses INDEX [s/SESSION_NAME] [d/SESSION_DATE]\n"
            + "Clear the session list: clearses\n"
            + "Enter a session: enterses INDEX\n";

    public static final String COMMAND_LIST_CURRENT_SESSION = "Current Session:\n"
            + "Toggle the presence status of student(s): presence INDEX_RANGE\n"
            + "Toggle the participation status of student(s): participate INDEX_RANGE\n";

    public static final String COMMAND_LIST = "Here is the list of commands you can try with ATAS:\n\n"
            + COMMAND_LIST_GENERAL + "\n"
            + COMMAND_LIST_STUDENTS + "\n"
            + COMMAND_LIST_SESSIONS + "\n"
            + COMMAND_LIST_CURRENT_SESSION + "\n";

    private static final Logger logger = LogsCenter.getLogger(HelpWindow.class);
    private static final String FXML = "HelpWindow.fxml";

    @FXML
    private Button copyButton;

    @FXML
    private Label linkMessage;

    @FXML
    private Label commandList;

    /**
     * Creates a new HelpWindow.
     *
     * @param root Stage to use as the root of the HelpWindow.
     */
    public HelpWindow(Stage root) {
        super(FXML, root);
        linkMessage.setText(LINK_MESSAGE);
        commandList.setText(COMMAND_LIST);
    }

    /**
     * Creates a new HelpWindow.
     */
    public HelpWindow() {
        this(new Stage());
    }

    /**
     * Shows the help window.
     * @throws IllegalStateException
     * <ul>
     *     <li>
     *         if this method is called on a thread other than the JavaFX Application Thread.
     *     </li>
     *     <li>
     *         if this method is called during animation or layout processing.
     *     </li>
     *     <li>
     *         if this method is called on the primary stage.
     *     </li>
     *     <li>
     *         if {@code dialogStage} is already showing.
     *     </li>
     * </ul>
     */
    public void show() {
        logger.fine("Showing help page about the application.");
        getRoot().show();
        getRoot().centerOnScreen();
    }

    /**
     * Returns true if the help window is currently being shown.
     */
    public boolean isShowing() {
        return getRoot().isShowing();
    }

    /**
     * Hides the help window.
     */
    public void hide() {
        getRoot().hide();
    }

    /**
     * Focuses on the help window.
     */
    public void focus() {
        getRoot().requestFocus();
    }

    /**
     * Copies the URL to the user guide to the clipboard.
     */
    @FXML
    private void copyUrl() {
        final Clipboard clipboard = Clipboard.getSystemClipboard();
        final ClipboardContent url = new ClipboardContent();
        url.putString(USERGUIDE_URL);
        clipboard.setContent(url);
    }
}
