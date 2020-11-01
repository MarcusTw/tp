package atas.logic.commands.studentlist;

import static atas.commons.core.Messages.MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX;
import static atas.logic.commands.CommandTestUtil.DESC_AMY;
import static atas.logic.commands.CommandTestUtil.DESC_BOB;
import static atas.logic.commands.CommandTestUtil.VALID_MATRICULATION_BOB;
import static atas.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static atas.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;
import static atas.logic.commands.CommandTestUtil.assertCommandFailure;
import static atas.logic.commands.CommandTestUtil.assertCommandSuccess;
import static atas.logic.commands.CommandTestUtil.showStudentAtIndex;
import static atas.testutil.TypicalIndexes.INDEX_FIRST_STUDENT;
import static atas.testutil.TypicalIndexes.INDEX_SECOND_STUDENT;
import static atas.testutil.TypicalMemoContents.EMPTY_MEMO_CONTENT;
import static atas.testutil.TypicalSessions.getTypicalSessionList;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import atas.commons.core.index.Index;
import atas.logic.commands.studentlist.EditStudentListCommand.EditStudentDescriptor;
import atas.model.Model;
import atas.model.ModelManager;
import atas.model.UserPrefs;
import atas.model.student.Student;
import atas.model.student.StudentList;
import atas.testutil.EditStudentDescriptorBuilder;
import atas.testutil.ModelManagerBuilder;
import atas.testutil.StudentBuilder;

/**
 * Contains integration tests (interaction with the Model, UndoCommand and RedoCommand) and unit tests for EditCommand.
 */
public class EditStudentCommandTest {

    private Model model = ModelManagerBuilder.buildTypicalModelManager();

    @Test
    public void execute_allFieldsSpecifiedUnfilteredList_success() {
        Student editedStudent = new StudentBuilder().build();
        EditStudentDescriptor descriptor = new EditStudentDescriptorBuilder(editedStudent).build();
        EditStudentListCommand editStudentCommand = new EditStudentListCommand(INDEX_FIRST_STUDENT, descriptor);

        String expectedMessage = String.format(EditStudentListCommand.MESSAGE_EDIT_STUDENT_SUCCESS, editedStudent);

        Model expectedModel = new ModelManager(getTypicalSessionList(model.getStudentList().getStudentList()),
                new StudentList(model.getStudentList()), new UserPrefs(), EMPTY_MEMO_CONTENT);
        expectedModel.setStudent(model.getFilteredStudentList().get(0), editedStudent);

        assertCommandSuccess(editStudentCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_someFieldsSpecifiedUnfilteredList_success() {
        Index indexLastStudent = Index.fromOneBased(model.getFilteredStudentList().size());
        Student lastStudent = model.getFilteredStudentList().get(indexLastStudent.getZeroBased());

        StudentBuilder studentInList = new StudentBuilder(lastStudent);
        Student editedStudent = studentInList.withName(VALID_NAME_BOB).withMatriculation(VALID_MATRICULATION_BOB)
                .withTags(VALID_TAG_HUSBAND).build();

        EditStudentListCommand.EditStudentDescriptor descriptor = new EditStudentDescriptorBuilder()
                .withName(VALID_NAME_BOB).withMatriculation(VALID_MATRICULATION_BOB)
                .withTags(VALID_TAG_HUSBAND).build();
        EditStudentListCommand editStudentCommand = new EditStudentListCommand(indexLastStudent, descriptor);

        String expectedMessage = String.format(EditStudentListCommand.MESSAGE_EDIT_STUDENT_SUCCESS, editedStudent);

        Model expectedModel = new ModelManager(getTypicalSessionList(model.getStudentList().getStudentList()),
                new StudentList(model.getStudentList()), new UserPrefs(), EMPTY_MEMO_CONTENT);
        expectedModel.setStudent(lastStudent, editedStudent);

        assertCommandSuccess(editStudentCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_noFieldSpecifiedUnfilteredList_success() {
        EditStudentListCommand editStudentCommand = new EditStudentListCommand(INDEX_FIRST_STUDENT,
                new EditStudentListCommand.EditStudentDescriptor());
        Student editedStudent = model.getFilteredStudentList().get(INDEX_FIRST_STUDENT.getZeroBased());

        String expectedMessage = String.format(EditStudentListCommand.MESSAGE_EDIT_STUDENT_SUCCESS, editedStudent);

        Model expectedModel = new ModelManager(getTypicalSessionList(model.getStudentList().getStudentList()),
                new StudentList(model.getStudentList()), new UserPrefs(), EMPTY_MEMO_CONTENT);

        assertCommandSuccess(editStudentCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_filteredList_success() {
        showStudentAtIndex(model, INDEX_FIRST_STUDENT);

        Student studentInFilteredList = model.getFilteredStudentList().get(INDEX_FIRST_STUDENT.getZeroBased());
        Student editedStudent = new StudentBuilder(studentInFilteredList).withName(VALID_NAME_BOB).build();
        EditStudentListCommand editStudentCommand = new EditStudentListCommand(INDEX_FIRST_STUDENT,
                new EditStudentDescriptorBuilder().withName(VALID_NAME_BOB).build());

        String expectedMessage = String.format(EditStudentListCommand.MESSAGE_EDIT_STUDENT_SUCCESS, editedStudent);

        Model expectedModel = new ModelManager(getTypicalSessionList(model.getStudentList().getStudentList()),
                new StudentList(model.getStudentList()), new UserPrefs(), EMPTY_MEMO_CONTENT);
        expectedModel.setStudent(model.getFilteredStudentList().get(0), editedStudent);

        assertCommandSuccess(editStudentCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_duplicateStudentUnfilteredList_failure() {
        Student firstStudent = model.getFilteredStudentList().get(INDEX_FIRST_STUDENT.getZeroBased());
        EditStudentDescriptor descriptor = new EditStudentDescriptorBuilder(firstStudent).build();
        EditStudentListCommand editStudentCommand = new EditStudentListCommand(INDEX_SECOND_STUDENT, descriptor);

        assertCommandFailure(editStudentCommand, model, EditStudentListCommand.MESSAGE_DUPLICATE_STUDENT);
    }

    @Test
    public void execute_duplicateStudentFilteredList_failure() {
        showStudentAtIndex(model, INDEX_FIRST_STUDENT);

        // edit student in filtered list into a duplicate in student list
        Student studentInList = model.getStudentList().getStudentList().get(INDEX_SECOND_STUDENT.getZeroBased());
        EditStudentListCommand editStudentCommand = new EditStudentListCommand(INDEX_FIRST_STUDENT,
                new EditStudentDescriptorBuilder(studentInList).build());

        assertCommandFailure(editStudentCommand, model, EditStudentListCommand.MESSAGE_DUPLICATE_STUDENT);
    }

    @Test
    public void execute_invalidStudentIndexUnfilteredList_failure() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredStudentList().size() + 1);
        EditStudentListCommand.EditStudentDescriptor
                descriptor = new EditStudentDescriptorBuilder().withName(VALID_NAME_BOB).build();
        EditStudentListCommand editStudentCommand = new EditStudentListCommand(outOfBoundIndex, descriptor);

        assertCommandFailure(editStudentCommand, model, MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX);
    }

    /**
     * Edit filtered list where index is larger than size of filtered list,
     * but smaller than size of student list
     */
    @Test
    public void execute_invalidStudentIndexFilteredList_failure() {
        showStudentAtIndex(model, INDEX_FIRST_STUDENT);
        Index outOfBoundIndex = INDEX_SECOND_STUDENT;
        // ensures that outOfBoundIndex is still in bounds of student list list
        assertTrue(outOfBoundIndex.getZeroBased() < model.getStudentList().getStudentList().size());

        EditStudentListCommand editStudentCommand = new EditStudentListCommand(outOfBoundIndex,
                new EditStudentDescriptorBuilder().withName(VALID_NAME_BOB).build());

        assertCommandFailure(editStudentCommand, model, MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX);
    }

    @Test
    public void equals() {
        final EditStudentListCommand standardCommand = new EditStudentListCommand(INDEX_FIRST_STUDENT, DESC_AMY);

        // same values -> returns true
        EditStudentListCommand.EditStudentDescriptor copyDescriptor =
                new EditStudentListCommand.EditStudentDescriptor(DESC_AMY);
        EditStudentListCommand commandWithSameValues = new EditStudentListCommand(INDEX_FIRST_STUDENT, copyDescriptor);
        assertTrue(standardCommand.equals(commandWithSameValues));

        // same object -> returns true
        assertTrue(standardCommand.equals(standardCommand));

        // null -> returns false
        assertFalse(standardCommand.equals(null));

        // different types -> returns false
        assertFalse(standardCommand.equals(new ClearStudentListCommand()));

        // different index -> returns false
        assertFalse(standardCommand.equals(new EditStudentListCommand(INDEX_SECOND_STUDENT, DESC_AMY)));

        // different descriptor -> returns false
        assertFalse(standardCommand.equals(new EditStudentListCommand(INDEX_FIRST_STUDENT, DESC_BOB)));
    }

}
