package dobby;

import java.io.IOException;
import java.util.List;

import dobby.exception.DobbyException;
import dobby.storage.Storage;
import dobby.task.Deadline;
import dobby.task.Event;
import dobby.task.Task;
import dobby.task.Todo;
import dobby.ui.Ui;

/**
 * A simple command-line chatbot that keeps the user's tasks in memory and on disk.
 */
public class Dobby {

    /** The maximum number of tasks that Dobby can remember in one session. */
    private static final int MAX_TASKS = 100;

    /** Handles Dobby's interactions with the user. */
    private final Ui ui;

    /** Creates a Dobby chatbot instance. */
    public Dobby() {
        ui = new Ui();
    }

    /**
     * Displays the greeting, handles commands, and exits when the user enters {@code bye}.
     *
     * @param args command-line arguments, which are not used.
     */
    public static void main(String[] args) {
        new Dobby().run();
    }

    /**
     * Starts Dobby, handles commands, and exits when the user enters {@code bye}.
     */
    public void run() {
        Task[] tasks = new Task[MAX_TASKS];
        int taskCount = loadTasks(tasks);

        ui.showWelcome();
        runCommandLoop(tasks, taskCount);
    }

    /**
     * Reads commands and maintains the task list for the current session.
     */
    private void runCommandLoop(Task[] tasks, int taskCount) {
        while (ui.hasNextCommand()) {
            String command = ui.readCommand();

            if (command.equals("bye")) {
                ui.showMessage("Bye for now! Dobby is off to polish the quest scrolls. Stay mighty!");
                return;
            }

            taskCount = processCommand(command, tasks, taskCount);
        }
    }

    /**
     * Processes one command and returns the updated number of stored tasks.
     *
     * @param command the complete command entered by the user.
     * @param tasks the in-memory task array.
     * @param taskCount the number of occupied positions in {@code tasks}.
     * @return the updated number of stored tasks
     */
    private int processCommand(String command, Task[] tasks, int taskCount) {
        try {
            if (command.equals("list")) {
                ui.showTaskList(tasks, taskCount);
                return taskCount;
            }

            if (command.equals("mark") || command.startsWith("mark ")) {
                markTask(command, tasks, taskCount);
                saveTasks(tasks, taskCount);
                return taskCount;
            }

            if (command.equals("unmark") || command.startsWith("unmark ")) {
                unmarkTask(command, tasks, taskCount);
                saveTasks(tasks, taskCount);
                return taskCount;
            }

            Task task = parseTask(command);
            int updatedTaskCount = addTask(task, tasks, taskCount);
            saveTasks(tasks, updatedTaskCount);
            return updatedTaskCount;
        } catch (DobbyException exception) {
            ui.showMessage(exception.getMessage());
            return taskCount;
        }
    }

    /**
     * Loads saved tasks into the supplied task array, starting empty when the file is unavailable.
     *
     * @param tasks the array into which loaded tasks are copied.
     * @return the number of tasks loaded.
     */
    private static int loadTasks(Task[] tasks) {
        try {
            List<Task> loadedTasks = Storage.loadTasks(MAX_TASKS);
            for (int i = 0; i < loadedTasks.size(); i++) {
                tasks[i] = loadedTasks.get(i);
            }
            return loadedTasks.size();
        } catch (IOException exception) {
            return 0;
        }
    }

    /**
     * Saves the current tasks and keeps the session running if the file cannot be written.
     *
     * @param tasks the task array to save.
     * @param taskCount the number of occupied positions in {@code tasks}.
     */
    private void saveTasks(Task[] tasks, int taskCount) {
        try {
            Storage.saveTasks(tasks, taskCount);
        } catch (IOException exception) {
            ui.showSaveError();
        }
    }

    /**
     * Adds a parsed task when the task pouch still has room.
     *
     * @param task the parsed task to add.
     * @param tasks the in-memory task array.
     * @param taskCount the number of occupied positions in {@code tasks}.
     * @return the updated number of stored tasks
     */
    private int addTask(Task task, Task[] tasks, int taskCount) throws DobbyException {
        if (taskCount >= MAX_TASKS) {
            throw new DobbyException("Your task pouch is full at 100 quests. Start a new Dobby session before "
                    + "adding another quest.");
        }

        tasks[taskCount] = task;
        taskCount++;

        ui.showTaskAdded(task, taskCount);
        return taskCount;
    }

    /**
     * Marks the task selected by a {@code mark <number>} command as done.
     *
     * @param command the complete command entered by the user.
     * @param tasks the in-memory task array.
     * @param taskCount the number of occupied positions in {@code tasks}.
     */
    private void markTask(String command, Task[] tasks, int taskCount) throws DobbyException {
        int taskIndex = parseTaskIndex(command, "mark", taskCount);
        tasks[taskIndex].markAsDone();

        ui.showTaskMarked(tasks[taskIndex]);
    }

    /**
     * Reverses the done status of the task selected by an {@code unmark <number>} command.
     *
     * @param command the complete command entered by the user.
     * @param tasks the in-memory task array.
     * @param taskCount the number of occupied positions in {@code tasks}.
     */
    private void unmarkTask(String command, Task[] tasks, int taskCount) throws DobbyException {
        int taskIndex = parseTaskIndex(command, "unmark", taskCount);
        tasks[taskIndex].markAsUndone();

        ui.showTaskUnmarked(tasks[taskIndex]);
    }

    /**
     * Parses and validates the task number in a mark or unmark command.
     *
     * @param command the complete command entered by the user.
     * @param action the command name being validated.
     * @param taskCount the number of tasks currently stored.
     * @return the zero-based index selected by the command.
     * @throws DobbyException when the number is missing, malformed, or out of range.
     */
    private static int parseTaskIndex(String command, String action, int taskCount) throws DobbyException {
        String taskNumberText = command.substring(action.length()).trim();
        int taskNumber;

        try {
            taskNumber = Integer.parseInt(taskNumberText);
        } catch (NumberFormatException exception) {
            throw new DobbyException("The " + action + " command needs a task number. Use " + action
                    + " <number>, for example " + action + " 1.");
        }

        if (taskCount == 0) {
            throw new DobbyException("There are no tasks to " + action + ". Add one with todo <description> first.");
        }

        if (taskNumber < 1 || taskNumber > taskCount) {
            throw new DobbyException("Task " + taskNumber + " does not exist. Choose a number from 1 to "
                    + taskCount + ".");
        }

        return taskNumber - 1;
    }

    /**
     * Creates a task from a user command.
     *
     * <p>Explicit commands use {@code todo}, {@code deadline}, and
     * {@code event}. Other input is rejected so Dobby can explain the supported
     * command formats instead of silently treating a typo as a task.
     *
     * @param command the complete command entered by the user.
     * @return the parsed task.
     * @throws DobbyException when the command is unknown or malformed.
     */
    private static Task parseTask(String command) throws DobbyException {
        if (command.equals("todo") || command.startsWith("todo ")) {
            return parseToDo(command);
        }

        if (command.equals("deadline") || command.startsWith("deadline ")) {
            return parseDeadline(command);
        }

        if (command.equals("event") || command.startsWith("event ")) {
            return parseEvent(command);
        }

        throw new DobbyException(
                "I don't know that command. Try list, todo <description>, deadline <description> /by <date/time>, "
                        + "event <description> /from <start> /to <end>, mark <number>, unmark <number>, or bye.");
    }

    /**
     * Parses an explicit ToDo command.
     *
     * @param command the complete ToDo command.
     * @return the parsed ToDo.
     * @throws DobbyException when the ToDo description is empty.
     */
    private static Task parseToDo(String command) throws DobbyException {
        String description = command.substring("todo".length()).trim();

        if (description.isEmpty()) {
            throw new DobbyException("A ToDo needs a description. Use todo <description>, for example todo read book.");
        }

        return new Todo(description);
    }

    /**
     * Parses a deadline command using the {@code /by} separator.
     *
     * @param command the complete deadline command.
     * @return the parsed deadline.
     * @throws DobbyException when the description or due time is missing.
     */
    private static Task parseDeadline(String command) throws DobbyException {
        String taskDetails = command.substring("deadline".length()).trim();
        int byIndex = taskDetails.lastIndexOf(" /by ");

        if (byIndex < 0) {
            throw new DobbyException("A deadline needs a description and a due time. Use deadline <description> "
                    + "/by <date/time>.");
        }

        if (byIndex == 0) {
            throw new DobbyException("A deadline needs a description before /by. Add text before the /by marker.");
        }

        String description = taskDetails.substring(0, byIndex).trim();
        String deadline = taskDetails.substring(byIndex + " /by ".length()).trim();

        if (deadline.isEmpty()) {
            throw new DobbyException("A deadline needs a due time after /by. For example: deadline report /by Friday.");
        }

        return new Deadline(description, deadline);
    }

    /**
     * Parses an event command using the {@code /from} and {@code /to} separators.
     *
     * @param command the complete event command.
     * @return the parsed event.
     * @throws DobbyException when an event detail is missing.
     */
    private static Task parseEvent(String command) throws DobbyException {
        String taskDetails = command.substring("event".length()).trim();
        int fromIndex = taskDetails.indexOf(" /from ");
        int toIndex = fromIndex < 0 ? -1 : taskDetails.indexOf(" /to ", fromIndex + " /from ".length());

        if (taskDetails.isEmpty()) {
            throw new DobbyException("An event needs a description, start time, and end time. Use event <description> "
                    + "/from <start> /to <end>.");
        }

        if (fromIndex < 0) {
            throw new DobbyException("An event needs a start time after /from and an end time after /to. Use event "
                    + "<description> /from <start> /to <end>.");
        }

        if (fromIndex == 0) {
            throw new DobbyException("An event needs a description before /from. Add text before the /from marker.");
        }

        if (toIndex < 0) {
            throw new DobbyException("An event needs an end time after /to. Add /to <end> after the start time.");
        }

        String description = taskDetails.substring(0, fromIndex).trim();
        String start = taskDetails.substring(fromIndex + " /from ".length(), toIndex).trim();
        String end = taskDetails.substring(toIndex + " /to ".length()).trim();

        if (start.isEmpty()) {
            throw new DobbyException("An event needs a start time after /from. Add a start time before /to.");
        }

        if (end.isEmpty()) {
            throw new DobbyException("An event needs an end time after /to. For example: event meeting /from 2pm "
                    + "/to 4pm.");
        }

        return new Event(description, start, end);
    }

}
