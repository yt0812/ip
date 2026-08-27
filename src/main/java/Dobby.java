import java.util.Scanner;

/**
 * A simple command-line chatbot that echoes commands entered by the user.
 */
public class Dobby {

    /**
     * Displays the greeting, echoes commands, and exits when the user enters {@code bye}.
     *
     * @param args command-line arguments, which are not used
     */
    public static void main(String[] args) {
        String separator = "____________________________________________________________";
        String banner = "     *        .        *        .        *\n"
                + "      ____          _      _\n"
                + "     |  _ \\   ___  | |__  | |__   _   _\n"
                + "     | | | | / _ \\ | '_ \\ | '_ \\ | | | |\n"
                + "     | |_| || (_) || |_) || |_) || |_| |\n"
                + "     |____/  \\___/ |_.__/ |_.__/  \\__, |\n"
                + "                                  |___/\n"
                + "     .        *        .        *        .";

        System.out.println(separator);
        System.out.println(banner);
        System.out.println("Hello! I'm Dobby, your mildly magical command goblin.");
        System.out.println("What quest shall we tackle today?");

        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String command = scanner.nextLine();

            if (command.equals("bye")) {
                System.out.println("    " + separator);
                System.out.println("     Bye! May your bugs be tiny and your code be mighty!");
                System.out.println("    " + separator);
                break;
            }

            System.out.println("    " + separator);
            System.out.println("     " + command);
            System.out.println("    " + separator);
        }
    }
}
