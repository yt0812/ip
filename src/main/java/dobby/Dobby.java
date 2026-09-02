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

        Task[] tasks = new Task[MAX_TASKS];
        int taskCount = 0;

        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String command = scanner.nextLine();

            if (command.equals("bye")) {
                printMessage("Bye for now! Dobby is off to polish the quest scrolls. Stay mighty!");
                break;
            }

            if (command.equals("list")) {
                printTaskList(tasks, taskCount);
                continue;
            }

            if (command.startsWith("mark ")) {
                markTask(command, tasks, taskCount);
                continue;
            }

            if (command.startsWith("unmark ")) {
                unmarkTask(command, tasks, taskCount);
                continue;
            }

            if (taskCount < MAX_TASKS) {
                tasks[taskCount] = new Task(command);
                taskCount++;
                printMessage("Quest accepted! Added to your magical task scroll: " + command);
            } else {
                printMessage("Your task pouch is bursting at the seams! Dobby can only carry 100 quests.");
            }
        }
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
                System.out.println("     " + (i + 1) + ".[" + tasks[i].getStatusIcon() + "] "
                        + tasks[i].getDescription());
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
        System.out.println("       [" + tasks[taskIndex].getStatusIcon() + "] "
                + tasks[taskIndex].getDescription());
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
        System.out.println("       [" + tasks[taskIndex].getStatusIcon() + "] "
                + tasks[taskIndex].getDescription());
        System.out.println("    " + SEPARATOR);
    }
}
