---
layout: page
title: Developer Guide
---
* [**Setting up, getting started**](#su_gs)
* [**Design**](#design)
   * [Architecture](#architecture)
   * [UI component](#ui_component)
   * [Logic component](#logic_component)
   * [Model component](#model_component)
   * [Storage component](#storage_component)
   * [Common classes](#common_classes)
* [**Implementation**](#implementation)
   * [Switching between tabs](#switching)
     * [Design consideration:](#switch_design_consideration)
   * [User confirmation](#ucp)
   * [Adding a session](#adding_a_session)
   * [Entering a session](#enter_session)
   * [Generating the name of a randomly-selected student](#rng)
   * [[Proposed] Undo/redo feature](#undo_redo)
     * [Proposed Implementation](#proposed_implementation)
     * [Design consideration:](#design_consideration)
       * [Aspect: How undo & redo executes](#aspect_undo_redo)
  * [[Proposed] Data archiving](#data_archiving)
* [**Documentation, logging, testing, configuration, dev-ops**](#documentation_etc)
* [**Appendix: Requirements**](#appendix_requirements)
  * [Product scope](#product_scope)
  * [User stories](#user_stories)
  * [Use cases](#use_cases)
  * [Non-Functional Requirements](#nfr)
  * [Glossary](#glossary)
* [**Appendix: Instructions for manual testing**](#appendix_manual_testing)
  * [Launch and shutdown](#launch_shutdown)
  * [Deleting a student](#deleting_a_student)
  * [Saving data](#saving_data)

--------------------------------------------------------------------------------------------------------------------

## <a name="su_gs"></a>**Setting up, getting started**

Refer to the guide [_Setting up and getting started_](SettingUp.md).

--------------------------------------------------------------------------------------------------------------------

## <a name="design"></a>**Design**

### <a name="architecture"></a>Architecture

<img src="images/ArchitectureDiagram.png" width="450" />

The ***Architecture Diagram*** given above explains the high-level design of the App. Given below is a quick overview of each component.

<div markdown="span" class="alert alert-primary">

:bulb: **Tip:** The `.puml` files used to create diagrams in this document can be found in the [diagrams](https://github.com/se-edu/addressbook-level3/tree/master/docs/diagrams/) folder. Refer to the [_PlantUML Tutorial_ at se-edu/guides](https://se-education.org/guides/tutorials/plantUml.html) to learn how to create and edit diagrams.

</div>

**`Main`** has two classes called [`Main`](https://github.com/AY2021S1-CS2103T-W16-4/tp/blob/master/src/main/java/atas/Main.java) and [`MainApp`](https://github.com/AY2021S1-CS2103T-W16-4/tp/blob/master/src/main/java/atas/MainApp.java). It is responsible for,
* At app launch: Initializes the components in the correct sequence, and connects them up with each other.
* At shut down: Shuts down the components and invokes cleanup methods where necessary.

[**`Commons`**](#common-classes) represents a collection of classes used by multiple other components.

The rest of the App consists of four components.

* [**`UI`**](#ui-component): The UI of the App.
* [**`Logic`**](#logic-component): The command executor.
* [**`Model`**](#model-component): Holds the data of the App in memory.
* [**`Storage`**](#storage-component): Reads data from, and writes data to, the hard disk.

Each of the four components,

* defines its *API* in an `interface` with the same name as the Component.
* exposes its functionality using a concrete `{Component Name}Manager` class (which implements the corresponding API `interface` mentioned in the previous point.

For example, the `Logic` component (see the class diagram given below) defines its API in the `Logic.java` interface and exposes its functionality using the `LogicManager.java` class which implements the `Logic` interface.

![Class Diagram of the Logic Component](images/LogicClassDiagram.png)

**How the architecture components interact with each other**

The *Sequence Diagram* below shows how the components interact with each other for the scenario where the user issues the command `addses s/Tutorial 1 d/10/10/2020`.

<img src="images/ArchitectureSequenceDiagram.png" width="574" />

The sections below give more details of each component.

### <a name="ui_component"></a>UI component

![Structure of the UI Component](images/UiClassDiagram.png)

**API** :
[`Ui.java`](https://github.com/AY2021S1-CS2103T-W16-4/tp/blob/master/src/main/java/atas/ui/Ui.java)

The UI consists of a `MainWindow` that is made up of parts e.g.`CommandBox`, `ResultDisplay`, `StudentListPanel`, `StatusBarFooterLeft` etc. All these, including the `MainWindow`, inherit from the abstract `UiPart` class except `Tab`.

The `UI` component uses JavaFx UI framework. The layout of these UI parts are defined in matching `.fxml` files that are in the `src/main/resources/view` folder. For example, the layout of the [`MainWindow`](https://github.com/AY2021S1-CS2103T-W16-4/tp/blob/master/src/main/java/atas/ui/MainWindow.java) is specified in [`MainWindow.fxml`](https://github.com/AY2021S1-CS2103T-W16-4/tp/blob/master/src/main/resources/view/MainWindow.fxml).

The `UI` component,

* Executes user commands using the `Logic` component.
* Listens for changes to `Model` data so that the UI can be updated with the modified data.
* Displays results to user if any.

### <a name="logic_component"></a>Logic component

![Structure of the Logic Component](images/LogicClassDiagram.png)

**API** :
[`Logic.java`](https://github.com/AY2021S1-CS2103T-W16-4/tp/blob/master/src/main/java/atas/logic/Logic.java)

1. `Logic` uses the `AtasParser` class to parse the user command.
1. This results in a `Command` object which is executed by the `LogicManager`.
1. The command execution can affect the `Model` (e.g. adding a student).
1. The result of the command execution is encapsulated as a `CommandResult` object which is passed back to the `Ui`.
1. In addition, the `CommandResult` object can also instruct the `Ui` to perform certain actions, such as displaying help to the user.

Given below is the Sequence Diagram for interactions within the `Logic` component for the `execute("enterses 1")` API call.

![Interactions Inside the Logic Component for the `enterses 1` Command](images/EnterSessionSequenceDiagram.png)

### <a name="model_component"></a>Model component

![Structure of the Model Component](images/ModelClassDiagram.png)

**API** : [`Model.java`](https://github.com/AY2021S1-CS2103T-W16-4/tp/blob/master/src/main/java/atas/model/Model.java)

The `Model`,

* stores a `UserPref` object that represents the user’s preferences.
* stores the data of the following: 
   * student list in a `StudentList` object 
   * session list in a `SessionList` object 
   * memo in a `Memo` object.
* exposes an unmodifiable `ObservableList<Student>` and `ObservableList<Session>` that can be 'observed'. e.g. For each list, the UI can be bound to the list so that the UI automatically updates when the data in the list change.
* does not depend on any of the other three components.

### <a name="storage_component"></a>Storage component

![Structure of the Storage Component](images/StorageClassDiagram.png)

**API** : [`Storage.java`](https://github.com/AY2021S1-CS2103T-W16-4/tp/blob/master/src/main/java/atas/storage/Storage.java)

The `Storage` component,
* can save `UserPref` objects in json format and read it back.
* can save the student list data in json format and read it back.
* can save the session list data in json format and read it back.
* can save the memo data in txt file and read it back.

### <a name="common_classes"></a>Common classes

Classes used by multiple components are in the `seedu.atas.commons` package.

--------------------------------------------------------------------------------------------------------------------

## <a name="implementation"></a>**Implementation**

This section describes some noteworthy details on how certain features are implemented.

### <a name="switching"></a>Switching between tabs

The switching of tabs is facilitated by `SwitchCommand`, `LogicManager`, `MainWindow` and `Tab`. `Tab` is an enum class that represents the various tabs that exist in ATAS.

Given below is an example usage scenario and how the switch mechanism behaves at each step.

Step 1. The user launches the application for the first time. The default "Students" tab is displayed.

Step 2. The user executes `switch sessions` command to view the sessions tab. `MainWindow#executeCommand()` is called and `LogicManager` calls `AtasParser#parseCommand()`. This results in the creation of a `SwitchCommandParser` to parse the user input.

Step 3. `SwitchCommandParser#parse()` checks if there is an argument being passed by the user. If an argument is passed, a `SwitchCommand` will be created and `SwitchCommand#execute()` will be called by the `LogicManager`.

<div markdown="span" class="alert alert-info">:information_source: **Note:** If no argument is found, a `ParseException` will be thrown and the execution terminates.

</div><br>

Step 4. `SwitchCommand#execute()` will check if the argument passed in is an existing tab name. If the argument is valid, a `CommandResult` containing a variable `switchTab` with the value of the target `Tab` will be return to `MainWindow`.

<div markdown="span" class="alert alert-info">:information_source: **Note:** If the argument is not a valid tab name, a `CommandException` will be thrown and the execution terminates.

</div><br>

Step 5. `MainWindow#handleSwitchTab()` will then be called and will check if the current `Tab` is the same as the target `Tab` to switched to. If it is not the same, `MainWindow` will utilise `tabPane` to execute the switch, thus updating the screen for the user.
<div markdown="span" class="alert alert-info">:information_source: **Note:** If the current `Tab` is the same as the target `Tab`, a `CommandException` will be thrown and the execution terminates.

</div><br>

The following sequence diagram shows how the switch operation works:

![SwitchTabsSequenceDiagram](images/SwitchTabsSequenceDiagram.png)

The following activity diagram summarizes what happens when a user executes a switch command:

![SwitchTabsActivityDiagram](images/SwitchTabsActivityDiagram.png)

#### <a name="switch_design_consideration"></a>Design consideration:

* `Tab` is being implemented as an enum class because there is a fixed set of tabs that are available to be switched to. This prevents invalid values to be assigned to `Tab`.

### <a name="ucp"></a>Getting user confirmation for commands that changes the local data.

This feature (henceforth referred to as 'user confirmation') is facilitated by 'ConfirmCommand', 'DangerousCommand', 'Logic', and 'Model'.

There are 6 DangerousCommand(s) that will change the existing local data upon execution, namely:
1. deletestu (deleting a student)
2. editstu (editing the particulars of a student)
3. clearstu (clearing the student list)
4. deleteses (deleting a sesion)
5. editses (editing the information of a session)
6. clearses (clearing the session list)

`DangerousCommand` implements the method:

* `DangerousCommand#execute(Model)` - Returns a `CommandResult` of the `DangerousCommand`.

`ConfirmationCommand` implements the method:

* `ConfirmationCommand#execute(Model)` - Returns a `CommandResult` containing a user confirmation prompt to confirm the execution of the `DangerousCommand`.

'ConfirmationAcceptCommand' implements the method:

* `ConfirmationAcceptCommand#execute(Model)` - Executes the `DangerousCommand` and returns a `CommandResult` from `DangerousCommand#execute(Model)`. 

`ConfirmationRejectCommand` implements the method:

* `ConfirmationRejectCommand#execute(Model)` - Returns a `CommandResult` indicating the `DangerousCommand` is not executed.

Given below is an example usage scenario and how the user confirmation prompt feature behaves at each step.


Step 1. The user launches the application with all his/her students already added to the student list. The `ModelManager` should already contain the list of students assigned to the user.

Step 2. The user executes a dangerous command, for example `deletestu 1`, to delete the first student in the student list. A `DeleteStudentCommandParser` is created by `LogicManager`.
If the input delete command is valid, a `DeleteStudentCommand` will be created by `DeleteStudentCommandParser#parse()` and passed into a `ConfirmationCommand` as a `DangerousCommand`.

Step 3. The `ConfirmationCommand` is executed, and the `ResultDisplay` window shows a confirmation prompt `Delete 1? (yes/no)`, where the user needs to input a `yes` or `no` to confirm or reject the execution of the `DangerousCommand` respectively.

Step 4. If the confirmation input is valid, a `ConfirmationCommandParser` is created by the `LogicManager` and either a `ConfirmationAcceptCommand` or `ConfirmationRejectCommand` is returned by `ConfirmationCommandParser#parser()`.

Step 5. The `ConfirmationAcceptCommand` or `ConfirmationRejectCommand` is then executed in `LogicManager#execute(String)` and a `CommandResult` is returned, which is displayed on `ResultDisplay`.

The following sequence diagram shows how the user confirmation feature works:

![UserConfirmationSequenceDiagram](images/UserConfirmationSequenceDiagram.png)

The following activity diagram summarizes what happens when a user executes a dangerous command (for eg. `DeleteStudentCommand`).

![DeleteStudentActivityDiagram](images/DeleteStudentActivityDiagram.png)

### <a name="adding_a_session"></a>Adding a session

Adding a session to a class requires user input from the CLI. Adding a session to a class requires user input from 
the CLI. The parser will then parse the user input to obtain the name and the date of the session. The newly added 
session will also be filled with students' default presence and participation.

The below example given assumes that the user inputs `addses s/Tutorial 1 d/1/1/2020`, which 'Tutorial 1' and
'1/1/2020' are the name and the date of the session to be added, and `addses` command has already been parsed.

Step 1: Parse input and create session

An `AddSessionCommandParser` is created by `LogicManager` and calls `AddSessionCommandParser#parse()` with the given
user input. Input is parsed according to the prefix `s/` and `d/` to identify the parts of the 
user input and split it into `String` slices. If both the name and date are present in the input, 
those `String` will be used to create a `SessionName` and a `SessionDate` object, which will be used to 
create and initialize a `Session` object. The `Session` object created will be the one being added to 
the current session list later.

`AddSessionCommandParser#parse()` returns a `AddSessionCommand` object that contains the session object
 back to the `LogicManager`.

![AddSessionSequenceDiagram1](images/AddSessionSequenceDiagram1.png)

Step 2: Add session to the model/session list

`LogicManager` calls `AddSessionCommand#execute()` to proceed on to adding the session to the model.
The `AddSessionCommand` will first check if the same session is already in the session list, if the 
session exists in the current session list, then the session will not be added to the session list.

Assuming that the session to add is not a session already in the list, `ModelManager` will update
the internal student list of the session list, then it uses addSession method to add the session to the 
list.

![AddSessionSequenceDiagram2](images/AddSessionSequenceDiagram2.png)


The following activity diagram summarizes what happens when a user executes an `addses` command:

![AddSessionActivityDiagram](images/AddSessionActivityDiagram.png)

### <a name="enter_session"></a>Entering a session

This feature (henceforth referred to as 'enter session') is facilitated by `EnterSessionCommand`, `LogicManager` and `Model`.

`LogicManager` implements the method:

* `LogicManager#execute(Model)` — Returns a `CommandResult` containing the session index of the session.

`EnterSessionCommand` implements the method from `LogicManager`:

* `EnterSessionCommand#execute(Model)` — Returns a `CommandResult` containing the session index of the session.

`Model` implements the method:

* `Model#enterSession(Index)` — Enters a session based on session index.

Given below is an example usage scenario and how the enter session mechanism behaves at each step.

Step 1. The user launches the application with all his/her students already added to the student list. The `ModelManager` should already contain the list of students assigned to the user.

Step 2. The user executes `enterses 1` to enter session 1. The `enterses 1` command calls `LogicManager#execute()`.

Step 3. `EnterSessionCommandParser#parse()` checks if there argument passed is valid. If the argument is valid, a `EnterSessionCommand` will be created and `EnterSessionCommand#execute()` will be called by the `LogicManager`.

<div markdown="span" class="alert alert-info">:information_source: **Note:** If an invalid argument is found, a `ParseException` will be thrown and the execution terminates.

</div><br>

Step 4. `Model#enterSession()` will be called by `EnterSession#execute()` and the displayed tab will be switched to Current Session. Details of session 2 will be displayed to the user.

The following sequence diagram shows how the enter session operation works:

![EnterSessionSequenceDiagram](images/EnterSessionSequenceDiagram.png)

The following activity diagram summarizes what happens when a user executes an enter session command:

![EnterSessionActivityDiagram](images/EnterSessionActivityDiagram.png)

### <a name="rng"></a>Generating the name of a randomly-selected student

This feature (henceforth referred to as 'RNG') is facilitated by `RngCommand` and `RandomGenerator`. 

`RngCommand` implements the method:

* `RngCommand#execute(Model)` — Returns a `CommandResult` containing the name of a randomly-selected student.

`RandomGenerator` implements the method:

* `RandomGenerator#getNextInt(int)` — Returns the index of the next randomly-generated student.

The `RandomGenerator` is contained in `Model`. It implements the method:

* `ModelManager#generateRandomStudentIndex()` — Returns the index of a randomly-generated student in the student list.

The operation above is exposed in the `Model` interface as `Model#generateRandomStudentIndex()`.

Given below is an example usage scenario and how the RNG mechanism behaves at each step.

Step 1. The user launches the application with all his/her students already added to the student list. The `ModelManager` should already contain the list of students assigned to the user.

Step 2. The user executes `rng` to pick a random student in his/her student list. The `rng` command calls `Model#generateRandomStudentIndex()`, which in turn calls `RandomGenerator#getNextInt(int)`.

Step 3. The `Index` returned during the execution of `RngCommand#execute(Model)` is used to fetch the name of the corresponding student (in the student list) to be selected. The student's name is then displayed to the user.

The following sequence diagram shows how the RNG operation works:

![RngSequenceDiagram](images/RngSequenceDiagram.png)

The following activity diagram summarizes what happens when a user executes an RNG command:

![RngActivityDiagram](images/RngActivityDiagram.png)


### <a name="undo_redo"></a>\[Proposed\] Undo/redo feature

#### <a name="proposed_implementation"></a>Proposed Implementation

The proposed undo/redo mechanism is facilitated by `VersionedAddressBook`. It extends `AddressBook` with an undo/redo history, stored internally as an `addressBookStateList` and `currentStatePointer`. Additionally, it implements the following operations:

* `VersionedAddressBook#commit()` — Saves the current address book state in its history.
* `VersionedAddressBook#undo()` — Restores the previous address book state from its history.
* `VersionedAddressBook#redo()` — Restores a previously undone address book state from its history.

These operations are exposed in the `Model` interface as `Model#commitAddressBook()`, `Model#undoAddressBook()` and `Model#redoAddressBook()` respectively.

Given below is an example usage scenario and how the undo/redo mechanism behaves at each step.

Step 1. The user launches the application for the first time. The `VersionedAddressBook` will be initialized with the initial address book state, and the `currentStatePointer` pointing to that single address book state.

![UndoRedoState0](images/UndoRedoState0.png)

Step 2. The user executes `delete 5` command to delete the 5th student in the address book. The `delete` command calls `Model#commitAddressBook()`, causing the modified state of the address book after the `delete 5` command executes to be saved in the `addressBookStateList`, and the `currentStatePointer` is shifted to the newly inserted address book state.

![UndoRedoState1](images/UndoRedoState1.png)

Step 3. The user executes `add n/David …​` to add a new student. The `add` command also calls `Model#commitAddressBook()`, causing another modified address book state to be saved into the `addressBookStateList`.

![UndoRedoState2](images/UndoRedoState2.png)

<div markdown="span" class="alert alert-info">:information_source: **Note:** If a command fails its execution, it will not call `Model#commitAddressBook()`, so the address book state will not be saved into the `addressBookStateList`.

</div>

Step 4. The user now decides that adding the student was a mistake, and decides to undo that action by executing the `undo` command. The `undo` command will call `Model#undoAddressBook()`, which will shift the `currentStatePointer` once to the left, pointing it to the previous address book state, and restores the address book to that state.

![UndoRedoState3](images/UndoRedoState3.png)

<div markdown="span" class="alert alert-info">:information_source: **Note:** If the `currentStatePointer` is at index 0, pointing to the initial AddressBook state, then there are no previous AddressBook states to restore. The `undo` command uses `Model#canUndoAddressBook()` to check if this is the case. If so, it will return an error to the user rather
than attempting to perform the undo.

</div>

The following sequence diagram shows how the undo operation works:

![UndoSequenceDiagram](images/UndoSequenceDiagram.png)

<div markdown="span" class="alert alert-info">:information_source: **Note:** The lifeline for `UndoCommand` should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline reaches the end of diagram.

</div>

The `redo` command does the opposite — it calls `Model#redoAddressBook()`, which shifts the `currentStatePointer` once to the right, pointing to the previously undone state, and restores the address book to that state.

<div markdown="span" class="alert alert-info">:information_source: **Note:** If the `currentStatePointer` is at index `addressBookStateList.size() - 1`, pointing to the latest address book state, then there are no undone AddressBook states to restore. The `redo` command uses `Model#canRedoAddressBook()` to check if this is the case. If so, it will return an error to the user rather than attempting to perform the redo.

</div>

Step 5. The user then decides to execute the command `list`. Commands that do not modify the address book, such as `list`, will usually not call `Model#commitAddressBook()`, `Model#undoAddressBook()` or `Model#redoAddressBook()`. Thus, the `addressBookStateList` remains unchanged.

![UndoRedoState4](images/UndoRedoState4.png)

Step 6. The user executes `clear`, which calls `Model#commitAddressBook()`. Since the `currentStatePointer` is not pointing at the end of the `addressBookStateList`, all address book states after the `currentStatePointer` will be purged. Reason: It no longer makes sense to redo the `add n/David …​` command. This is the behavior that most modern desktop applications follow.

![UndoRedoState5](images/UndoRedoState5.png)

The following activity diagram summarizes what happens when a user executes a new command:

![CommitActivityDiagram](images/CommitActivityDiagram.png)

#### <a name="design_consideration"></a>Design consideration:

##### <a name="aspect_undo_redo"></a>Aspect: How undo & redo executes

* **Alternative 1 (current choice):** Saves the entire address book.
 * Pros: Easy to implement.
 * Cons: May have performance issues in terms of memory usage.

* **Alternative 2:** Individual command knows how to undo/redo by
 itself.
 * Pros: Will use less memory (e.g. for `delete`, just save the student being deleted).
 * Cons: We must ensure that the implementation of each individual command are correct.

_{more aspects and alternatives to be added}_

### <a name="data_archiving"></a>\[Proposed\] Data archiving

_{Explain here how the data archiving feature will be implemented}_
 

--------------------------------------------------------------------------------------------------------------------

## <a name="documentation_etc"></a>**Documentation, logging, testing, configuration, dev-ops**

* [Documentation guide](Documentation.md)
* [Testing guide](Testing.md)
* [Logging guide](Logging.md)
* [Configuration guide](Configuration.md)
* [DevOps guide](DevOps.md)

--------------------------------------------------------------------------------------------------------------------

## <a name="appendix_requirements"></a>**Appendix: Requirements**

### <a name="product_scope"></a>Product scope

**Target user profile**:

* Bob is a passionate CS1010 TA and he likes to track his students’ progress.
* Bob currently uses Excel to keep track of his student’s particulars.
* Bob finds the features on Excel clunky at times and he finds them difficult to learn.
* Bob is looking for an application with a sleeker and cleaner UI so he doesn’t have to remember where the different features in Excel are.
* Bob has limited memory in his computer and wants to install smaller softwares.
* Bob is a fast typist.
* Bob hates to move his mouse at all (to save file, to categorise data fields and student data)
* Bob uses a laptop and doesn’t have a mouse with him all the time(or he doesn’t have a place to use his mouse when on the bus or mrt).


**Value proposition**:

* Manage students faster than a typical mouse/GUI driven app
* Sleeker and cleaner UI, less clunky
* Less memory
* Intuitive command-line interface - easy to learn and pick up, especially for technologically savvy individuals
* CS student-friendly
* Potential Features:
 * Mass sending of emails/files
 * Import/export data files from Excel, csv files
 * Track student’s progress (assignments, tests, class participation)
 * Record student’s attendance
 * Profile pictures for recognisability
 * Schedule consultations/Zoom meeting
 * Automate formation of Telegram groups



### <a name="user_stories"></a>User stories

Priorities: High (must have) - `* * *`, Medium (nice to have) - `* *`, Low (unlikely to have) - `*`

| Priority | As a …​                                    | I want to …​                     | So that I can…​                                                        |
| -------- | ------------------------------------------ | ------------------------------ | ----------------------------------------------------------------------- |
| `* * *`  | user                                       | see usage instructions         | refer to instructions when I forget how to use the App                  |
| `* * *`  | user                                       | add a new student              |                                                                         |
| `* * *`  | user                                       | delete a student               | remove students no longer taking the module                             |
| `* * *`  | user                                       | track all students in my class | so that I will know who is missing from my class                        |
| `* * *`  | user                                       | find a student by name         | locate details of students without having to go through the entire list |
| `* * *`  | user                                       | hide private contact details   | minimize chance of someone else seeing them by accident                 |
| `* * *`  | user with many students in my class        | sort students by name          | locate a student easily                                                 |
| `* *  `  | user                                       | track my students' attendance  | take note of the tutorial participation for each student                |
| `* *  `  | user who has trouble putting face to name  | easily identify each student with a profile photo | recognise students through their faces more easily   |
| `* *  `  | user who yearns for efficiency due to limited class time | have a random number generator | get a random student to answer my question instead of having to rely on online generators |
| `* *  `  | user                                       | add non-registered Christian names or nicknames to my students | remember them more easily               |
| `* *  `  | user                                       | send important emails to my students | not have to rely on Microsoft Outlook/LumiNUS                     |
| `* *  `  | user                                       | have diagrams tracking students' progress | tell from one look that a student is not catching up with lecture material |
| `* *  `  | tutor who can be quite forgetful           | create a list of announcements | remember to convey them to my students                                  |
| `* *  `  | tutor who wants to protect the particulars of my students | encrypt files with passwords | keep the module and students' data safe                    |
| `* *  `  | tutor who often overshoots the time allocated for my lesson | be reminded at certain intervals | be on track throughout the lesson                    |
| `* *  `  | tutor who wants the class to participate actively in class discussions | quickly record who has/has not spoken yet | call on those who haven't spoken for subsequent questions |
| `*    `  | user who likes customization               | customize my own keyboard shortcuts |                                                                    |
| `*    `  | user who likes colors                      | customize the UI to whatever color scheme I want | enjoy using the App more                              |

*{More to be added}*


### <a name="use_cases"></a>Use cases

(For all use cases below, the **System** is the `ATAS` and the **Actor** is the `user`, unless specified otherwise)

**Use case: UC01 - Add a student**

**MSS**

1. User requests to add a student with specified Matriculation number and NUS email, as well as any additional tags (if any)
1. ATAS adds the student

Use case ends.

**Use case: UC02 - Delete a student**

**MSS**

1.  User requests to list students
1.  ATAS shows a list of students
1.  User requests to delete a specific student in the list
1.  ATAS deletes the student

Use case ends.

**Extensions**

* 2a. The list is empty.

   Use case ends.

* 3a. The given index is invalid.

  * 3a1. ATAS shows an error message.

      Use case resumes at step 2.

**Use case: UC03 - Clear the class list**

**MSS**

1.  User requests to clear the class list
1.  ATAS clears the class list

Use case ends.

**Extensions**

* 2a. The list is empty.

   Use case ends.

**Use case: UC04 - List the registered students**

**MSS**

1. User requests to see the list of students that have been added to ATAS
1. ATAS shows the list of students

Use case ends.

**Use case: UC05 - Find a student**

**MSS**

1.  User requests to find a student with one or more keywords
1.  ATAS searches for students’ names that contain any of the keywords
1.  ATAS shows the filtered list of students to User

   Use case ends.

**Extensions**

* 2a. The list is empty.

   Use case ends.

**Use case: UC06 - Exit ATAS**

**MSS**

1. User requests to exit ATAS
1. ATAS closes

Use case ends.

*{More to be added}*

### <a name="nfr"></a>Non-Functional Requirements

1. Should work on any _mainstream OS_ as long as it has Java `11` or above installed.
1. Should be able to hold up to 1000 students without a noticeable sluggishness in performance for typical usage.
1. A user with above average typing speed for regular English text (i.e. not code, not system admin commands) should be able to accomplish most of the tasks faster using commands than using the mouse.
1. Should be for a single user and not a multi-user product.
1. Should not depend on remote servers.
1. Should be able to function without having to rely on being connected to a network.
1. Should be able to work without requiring an installer.
1. Should not contain very large file sizes (JAR files - 100Mb, PDF files - 15Mb/file).

*{More to be added}*

### <a name="glossary"></a>Glossary

* **Mainstream OS**: Windows, Linux, Unix, OS-X
* **Matriculation number**: A unique alphanumeric number attributed to each NUS student. Follows the format A0123456X, where each digit can be from 0-9 and the last letter can be any alphabet A-Z
* **Private contact detail**: A contact detail that is not meant to be shared with others

--------------------------------------------------------------------------------------------------------------------

## <a name="appendix_manual_testing"></a>**Appendix: Instructions for manual testing**

Given below are instructions to test the app manually.

<div markdown="span" class="alert alert-info">:information_source: **Note:** These instructions only provide a starting point for testers to work on;
testers are expected to do more *exploratory* testing.

</div>

### <a name="launch_shutdown"></a>Launch and shutdown

1. Initial launch

  1. Download the jar file and copy into an empty folder

  1. Double-click the jar file Expected: Shows the GUI with a set of sample contacts. The window size may not be optimum.

1. Saving window preferences

  1. Resize the window to an optimum size. Move the window to a different location. Close the window.

  1. Re-launch the app by double-clicking the jar file.<br>
      Expected: The most recent window size and location is retained.

1. _{ more test cases …​ }_

### <a name="deleting_a_student"></a>Deleting a student

1. Deleting a student while all students are being shown

  1. Prerequisites: List all students using the `list` command. Multiple students in the list.

  1. Test case: `delete 1`<br>
     Expected: First contact is deleted from the list. Details of the deleted contact shown in the status message. Timestamp in the status bar is updated.

  1. Test case: `delete 0`<br>
     Expected: No student is deleted. Error details shown in the status message. Status bar remains the same.

  1. Other incorrect delete commands to try: `delete`, `delete x`, `...` (where x is larger than the list size)<br>
     Expected: Similar to previous.

1. _{ more test cases …​ }_

### <a name="saving_data"></a>Saving data

1. Dealing with missing/corrupted data files

  1. _{explain how to simulate a missing/corrupted file, and the expected behavior}_

1. _{ more test cases …​ }_

