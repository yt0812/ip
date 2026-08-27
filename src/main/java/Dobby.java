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

    /**
     * Displays the greeting, handles commands, and exits when the user enters {@code bye}.
     *
     * @param args command-line arguments, which are not used
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

        String[] tasks = new String[MAX_TASKS];
        int taskCount = 0;

        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String command = scanner.nextLine();

            if (command.equals("bye")) {
                printMessage("Bye! May your day be merry and your adventures be mighty!");
                break;
            }

            if (command.equals("list")) {
                printTaskList(tasks, taskCount);
                continue;
            }

            if (taskCount < MAX_TASKS) {
                tasks[taskCount] = command;
                taskCount++;
                printMessage("added: " + command);
            } else {
                printMessage("Your task pouch is full! Dobby can only remember 100 tasks.");
            }
        }
    }

    /**
     * Prints one response surrounded by the standard separator.
     *
     * @param message the response to print
     */
    private static void printMessage(String message) {
        System.out.println("    " + SEPARATOR);
        System.out.println("     " + message);
        System.out.println("    " + SEPARATOR);
    }

    /**
     * Prints all stored tasks in the order in which they were entered.
     *
     * @param tasks the in-memory task array
     * @param taskCount the number of occupied positions in {@code tasks}
     */
    private static void printTaskList(String[] tasks, int taskCount) {
        System.out.println("    " + SEPARATOR);

        if (taskCount == 0) {
            System.out.println("     Your task pouch is empty - add a task to begin your quest!");
        } else {
            for (int i = 0; i < taskCount; i++) {
                System.out.println("     " + (i + 1) + ". " + tasks[i]);
            }
        }

        System.out.println("    " + SEPARATOR);
    }
}
