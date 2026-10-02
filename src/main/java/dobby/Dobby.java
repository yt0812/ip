package dobby;

import java.io.IOException;

import dobby.command.Command;
import dobby.exception.DobbyException;
import dobby.parser.Parser;
import dobby.storage.Storage;
import dobby.task.TaskList;
import dobby.ui.Ui;

/**
 * A simple command-line chatbot that keeps the user's tasks in memory and on disk.
 */
public class Dobby {

    /** Handles Dobby's interactions with the user. */
    private final Ui ui;

    /** Manages loading and saving Dobby's tasks. */
    private final Storage storage;

    /** Stores the tasks managed during the current session. */
    private final TaskList tasks;

    /** Creates a Dobby chatbot instance using the default data file. */
    public Dobby() {
        this("data/dobby.txt");
    }

    /**
     * Creates a Dobby chatbot instance using the supplied data file.
     *
     * @param filePath the path of the file used to persist tasks.
     */
    public Dobby(String filePath) {
        ui = new Ui();
        storage = new Storage(filePath);
        tasks = loadTasks();
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
     * Starts Dobby and delegates each user command to an executable command object.
     */
    public void run() {
        ui.showWelcome();
        boolean isExit = false;

        while (!isExit && ui.hasNextCommand()) {
            String fullCommand = ui.readCommand();

            try {
                Command command = Parser.parse(fullCommand);
                command.execute(tasks, ui, storage);
                isExit = command.isExit();
            } catch (DobbyException exception) {
                ui.showMessage(exception.getMessage());
            }
        }
    }

    /**
     * Loads saved tasks, starting empty when the file is unavailable.
     *
     * @return the loaded task list.
     */
    private TaskList loadTasks() {
        try {
            return new TaskList(storage.loadTasks(TaskList.MAX_TASKS));
        } catch (IOException exception) {
            return new TaskList();
        }
    }
}
