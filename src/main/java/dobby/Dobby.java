package dobby;

import java.io.IOException;

import dobby.command.ExitCommand;
import dobby.exception.DobbyException;
import dobby.storage.Storage;
import dobby.task.Deadline;
import dobby.task.Event;
import dobby.task.Task;
import dobby.task.TaskList;
import dobby.task.Todo;
import dobby.ui.Ui;

/**
 * A simple command-line chatbot that keeps the user's tasks in memory and on disk.
 */
public class Dobby {

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
        TaskList tasks = loadTasks();

        ui.showWelcome();
        runCommandLoop(tasks);
    }

    /**
     * Reads commands and maintains the task list for the current session.
     */
    private void runCommandLoop(TaskList tasks) {
        while (ui.hasNextCommand()) {
            String command = ui.readCommand();

            if (runExitCommand(command)) {
                return;
            }

            processCommand(command, tasks);
        }
    }

    /**
     * Runs the exit command when the user enters {@code bye}.
     *
     * @param command the complete command entered by the user.
     * @return true when the command ends the session.
     */
    private boolean runExitCommand(String command) {
        if (!command.equals("bye")) {
            return false;
        }

        ExitCommand exitCommand = new ExitCommand();
        exitCommand.execute(ui);
        return exitCommand.isExit();
    }

    /**
     * Processes one command and updates the supplied task list when needed.
     *
     * @param command the complete command entered by the user.
     * @param tasks the task list currently managed by Dobby.
     */
    private void processCommand(String command, TaskList tasks) {
        try {
            if (command.equals("list")) {
                ui.showTaskList(tasks);
                return;
            }

            if (command.equals("mark") || command.startsWith("mark ")) {
                markTask(command, tasks);
                saveTasks(tasks);
                return;
            }

            if (command.equals("unmark") || command.startsWith("unmark ")) {
                unmarkTask(command, tasks);
                saveTasks(tasks);
                return;
            }

            Task task = parseTask(command);
            addTask(task, tasks);
            saveTasks(tasks);
        } catch (DobbyException exception) {
            ui.showMessage(exception.getMessage());
        }
    }

    /**
     * Loads saved tasks, starting empty when the file is unavailable.
     *
     * @return the loaded task list.
     */
    private static TaskList loadTasks() {
        try {
            return new TaskList(Storage.loadTasks(TaskList.MAX_TASKS));
        } catch (IOException exception) {
            return new TaskList();
        }
    }

    /**
     * Saves the current tasks and keeps the session running if the file cannot be written.
     *
     * @param tasks the task list to save.
     */
    private void saveTasks(TaskList tasks) {
        try {
            Storage.saveTasks(tasks);
        } catch (IOException exception) {
            ui.showSaveError();
        }
    }

    /**
     * Adds a parsed task when the task pouch still has room.
     *
     * @param task the parsed task to add.
     * @param tasks the task list currently managed by Dobby.
     */
    private void addTask(Task task, TaskList tasks) throws DobbyException {
        tasks.add(task);
        ui.showTaskAdded(task, tasks.size());
    }

    /**
     * Marks the task selected by a {@code mark <number>} command as done.
     *
     * @param command the complete command entered by the user.
     * @param tasks the task list currently managed by Dobby.
     */
    private void markTask(String command, TaskList tasks) throws DobbyException {
        int taskIndex = parseTaskIndex(command, "mark", tasks.size());
        tasks.get(taskIndex).markAsDone();

        ui.showTaskMarked(tasks.get(taskIndex));
    }

    /**
     * Reverses the done status of the task selected by an {@code unmark <number>} command.
     *
     * @param command the complete command entered by the user.
     * @param tasks the task list currently managed by Dobby.
     */
    private void unmarkTask(String command, TaskList tasks) throws DobbyException {
        int taskIndex = parseTaskIndex(command, "unmark", tasks.size());
        tasks.get(taskIndex).markAsUndone();

        ui.showTaskUnmarked(tasks.get(taskIndex));
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
