package dobby;

import java.util.Scanner;

/**
 * A simple command-line chatbot that keeps the user's tasks in memory.
 *
 * <p>The tasks are intentionally not written to disk. They last only for the
 * current run of Dobby, just like notes written on a magical temporary scroll.
 */
public class Dobby {

    /** The maximum number of tasks that Dobby can remember in one session. */
    private static final int MAX_TASKS = 100;

    /** The line printed between Dobby's messages. */
    private static final String SEPARATOR = "____________________________________________________________";

    /** Creates a Dobby chatbot instance. */
    public Dobby() {
    }

    /**
     * Displays the greeting, handles commands, and exits when the user enters {@code bye}.
     *
     * @param args command-line arguments, which are not used.
     */
    public static void main(String[] args) {
        printWelcome();
        runCommandLoop();
    }

    /**
     * Prints Dobby's welcome banner and opening message.
     */
    private static void printWelcome() {
        String banner = "     *        .        *        .        *\n"
                + "      ____          _      _\n"
                + "     |  _ \\   ___  | |__  | |__   _   _\n"
                + "     | | | | / _ \\ | '_ \\ | '_ \\ | | | |\n"
                + "     | |_| || (_) || |_) || |_) || |_| |\n"
                + "     |____/  \\___/ |_.__/ |_.__/  \\__, |\n"
                + "                                  |___/\n"
                + "     .        *        .        *        .";

        System.out.println(SEPARATOR);
        System.out.println(banner);
        System.out.println("Hello! I'm Dobby, your mildly magical command goblin.");
        System.out.println("What adventure shall we get into today?");
    }

    /**
     * Reads commands and maintains the task list for the current session.
     */
    private static void runCommandLoop() {
        Task[] tasks = new Task[MAX_TASKS];
        int taskCount = 0;

        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String command = scanner.nextLine();

            if (command.equals("bye")) {
                printMessage("Bye for now! Dobby is off to polish the quest scrolls. Stay mighty!");
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
    private static int processCommand(String command, Task[] tasks, int taskCount) {
        if (command.equals("list")) {
            printTaskList(tasks, taskCount);
            return taskCount;
        }

        if (command.startsWith("mark ")) {
            markTask(command, tasks, taskCount);
            return taskCount;
        }

        if (command.startsWith("unmark ")) {
            unmarkTask(command, tasks, taskCount);
            return taskCount;
        }

        Task task = parseTask(command);

        if (task == null) {
            return taskCount;
        }

        return addTask(task, command, tasks, taskCount);
    }

    /**
     * Adds a parsed task when the task pouch still has room.
     *
     * @param task the parsed task to add.
     * @param command the original command used to create the task.
     * @param tasks the in-memory task array.
     * @param taskCount the number of occupied positions in {@code tasks}.
     * @return the updated number of stored tasks
     */
    private static int addTask(Task task, String command, Task[] tasks, int taskCount) {
        if (taskCount >= MAX_TASKS) {
            printMessage("Your task pouch is bursting at the seams! Dobby can only carry 100 quests.");
            return taskCount;
        }

        tasks[taskCount] = task;
        taskCount++;

        if (isTypedTaskCommand(command)) {
            printTaskAdded(task, taskCount);
        } else {
            printMessage("Quest accepted! Added to your magical task scroll: " + command);
        }

        return taskCount;
    }

    /**
     * Prints one response surrounded by the standard separator.
     *
     * @param message the response to print.
     */
    private static void printMessage(String message) {
        System.out.println("    " + SEPARATOR);
        System.out.println("     " + message);
        System.out.println("    " + SEPARATOR);
    }

    /**
     * Prints all stored tasks in the order in which they were entered.
     *
     * @param tasks the in-memory task array.
     * @param taskCount the number of occupied positions in {@code tasks}.
     */
    private static void printTaskList(Task[] tasks, int taskCount) {
        System.out.println("    " + SEPARATOR);
        System.out.println("     Behold, brave adventurer! Here are your mighty quests:");

        if (taskCount == 0) {
            System.out.println("     Your task pouch is empty - add a quest and let the adventure begin!");
        } else {
            for (int i = 0; i < taskCount; i++) {
                System.out.println("     " + (i + 1) + "." + tasks[i]);
            }
        }

        System.out.println("    " + SEPARATOR);
    }

    /**
     * Marks the task selected by a {@code mark <number>} command as done.
     *
     * @param command the complete command entered by the user.
     * @param tasks the in-memory task array.
     * @param taskCount the number of occupied positions in {@code tasks}.
     */
    private static void markTask(String command, Task[] tasks, int taskCount) {
        String taskNumberText = command.substring("mark ".length()).trim();
        int taskNumber;

        try {
            taskNumber = Integer.parseInt(taskNumberText);
        } catch (NumberFormatException exception) {
            printMessage("Please tell me which task number to mark - Dobby cannot read that quest rune!");
            return;
        }

        if (taskNumber < 1 || taskNumber > taskCount) {
            printMessage("That quest number is hiding in another dimension! Try a number from 1 to "
                    + taskCount + ".");
            return;
        }

        int taskIndex = taskNumber - 1;
        tasks[taskIndex].markAsDone();

        System.out.println("    " + SEPARATOR);
        System.out.println("     Nice! Quest progress unlocked - I've marked this task as done:");
        System.out.println("       " + tasks[taskIndex]);
        System.out.println("    " + SEPARATOR);
    }

    /**
     * Reverses the done status of the task selected by an {@code unmark <number>} command.
     *
     * @param command the complete command entered by the user.
     * @param tasks the in-memory task array.
     * @param taskCount the number of occupied positions in {@code tasks}.
     */
    private static void unmarkTask(String command, Task[] tasks, int taskCount) {
        String taskNumberText = command.substring("unmark ".length()).trim();
        int taskNumber;

        try {
            taskNumber = Integer.parseInt(taskNumberText);
        } catch (NumberFormatException exception) {
            printMessage("Please tell me which quest number to unmark - Dobby cannot read that rune!");
            return;
        }

        if (taskNumber < 1 || taskNumber > taskCount) {
            printMessage("That quest number is hiding in another dimension! Try a number from 1 to "
                    + taskCount + ".");
            return;
        }

        int taskIndex = taskNumber - 1;
        tasks[taskIndex].markAsUndone();

        System.out.println("    " + SEPARATOR);
        System.out.println("     Plot twist! This quest is back on the adventure board -");
        System.out.println("     I've marked this task as not done yet:");
        System.out.println("       " + tasks[taskIndex]);
        System.out.println("    " + SEPARATOR);
    }

    /**
     * Creates a task from a user command.
     *
     * <p>Explicit commands use {@code todo}, {@code deadline}, and
     * {@code event}. A bare line is still accepted as a ToDo so existing users
     * can continue entering tasks in the original style.
     *
     * @param command the complete command entered by the user.
     * @return the parsed task, or {@code null} when the command is invalid
     */
    private static Task parseTask(String command) {
        if (command.equals("todo") || command.startsWith("todo ")) {
            return parseToDo(command);
        }

        if (command.equals("deadline") || command.startsWith("deadline ")) {
            return parseDeadline(command);
        }

        if (command.equals("event") || command.startsWith("event ")) {
            return parseEvent(command);
        }

        return new Task(command);
    }

    /**
     * Parses an explicit ToDo command.
     *
     * @param command the complete ToDo command.
     * @return the parsed ToDo, or {@code null} when its description is empty
     */
    private static Task parseToDo(String command) {
        String description = command.substring("todo".length()).trim();

        if (description.isEmpty()) {
            printMessage("A ToDo needs a description before Dobby can add it to the quest scroll!");
            return null;
        }

        return new Todo(description);
    }

    /**
     * Parses a deadline command using the {@code /by} separator.
     *
     * @param command the complete deadline command.
     * @return the parsed deadline, or {@code null} when a description or due time is missing
     */
    private static Task parseDeadline(String command) {
        String taskDetails = command.substring("deadline".length()).trim();
        int byIndex = taskDetails.lastIndexOf(" /by ");

        if (byIndex <= 0) {
            printMessage("A deadline needs a description and a due time, such as /by Sunday!");
            return null;
        }

        String description = taskDetails.substring(0, byIndex).trim();
        String deadline = taskDetails.substring(byIndex + " /by ".length()).trim();

        if (deadline.isEmpty()) {
            printMessage("Dobby needs to know when this deadline is due - try adding a date after /by!");
            return null;
        }

        return new Deadline(description, deadline);
    }

    /**
     * Parses an event command using the {@code /from} and {@code /to} separators.
     *
     * @param command the complete event command.
     * @return the parsed event, or {@code null} when an event detail is missing
     */
    private static Task parseEvent(String command) {
        String taskDetails = command.substring("event".length()).trim();
        int fromIndex = taskDetails.indexOf(" /from ");
        int toIndex = fromIndex < 0 ? -1 : taskDetails.indexOf(" /to ", fromIndex + " /from ".length());

        if (fromIndex <= 0 || toIndex <= fromIndex + " /from ".length()) {
            printMessage("An event needs a description, a start time after /from, and an end time after /to!");
            return null;
        }

        String description = taskDetails.substring(0, fromIndex).trim();
        String start = taskDetails.substring(fromIndex + " /from ".length(), toIndex).trim();
        String end = taskDetails.substring(toIndex + " /to ".length()).trim();

        if (end.isEmpty()) {
            printMessage("Dobby needs to know when this event ends - try adding a time after /to!");
            return null;
        }

        return new Event(description, start, end);
    }

    /**
     * Prints the playful quest confirmation used by explicit typed-task commands.
     *
     * @param task the task that was added.
     * @param taskCount the number of tasks now stored.
     */
    private static void printTaskAdded(Task task, int taskCount) {
        String questLabel = taskCount == 1 ? "quest" : "quests";

        System.out.println("    " + SEPARATOR);
        System.out.println("     Huzzah! A new quest has joined your magical task scroll:");
        System.out.println("       " + task);
        System.out.println("     The quest scroll now holds " + taskCount + " " + questLabel
                + ". Keep adventuring!");
        System.out.println("    " + SEPARATOR);
    }

    /**
     * Checks whether a command explicitly names one of the supported task types.
     *
     * @param command the complete command entered by the user.
     * @return {@code true} when the command starts with a typed-task keyword
     */
    private static boolean isTypedTaskCommand(String command) {
        return command.startsWith("todo ") || command.startsWith("deadline ") || command.startsWith("event ");
    }
}
