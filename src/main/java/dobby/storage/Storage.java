package dobby.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import dobby.task.Deadline;
import dobby.task.Event;
import dobby.task.Task;
import dobby.task.Todo;

/**
 * Reads and writes Dobby's tasks in a small, human-readable text file.
 */
public final class Storage {

    /** The relative path of the file used to persist tasks. */
    private static final Path DATA_FILE = Path.of("data", "dobby.txt");

    /** The separator used between fields in a stored task record. */
    private static final String FIELD_SEPARATOR = " | ";

    private Storage() {
    }

    /**
     * Loads up to the requested number of valid tasks from the data file.
     *
     * <p>A missing file represents a new installation. Malformed records are
     * ignored so that one corrupted line does not prevent the chatbot from
     * starting with the remaining valid tasks.
     *
     * @param maximumTasks the maximum number of tasks to return.
     * @return the valid tasks read from the data file.
     * @throws IOException when the data file cannot be read.
     */
    public static List<Task> loadTasks(int maximumTasks) throws IOException {
        List<Task> tasks = new ArrayList<>();

        if (!Files.exists(DATA_FILE)) {
            return tasks;
        }

        for (String line : Files.readAllLines(DATA_FILE, StandardCharsets.UTF_8)) {
            if (tasks.size() >= maximumTasks) {
                break;
            }

            Task task = parseTask(line);
            if (task != null) {
                tasks.add(task);
            }
        }

        return tasks;
    }

    /**
     * Saves the occupied portion of the task array to the data file.
     *
     * @param tasks the array containing the tasks to save.
     * @param taskCount the number of occupied positions in {@code tasks}.
     * @throws IOException when the data directory or file cannot be written.
     */
    public static void saveTasks(Task[] tasks, int taskCount) throws IOException {
        Path parent = DATA_FILE.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }

        List<String> records = new ArrayList<>();
        for (int i = 0; i < taskCount; i++) {
            records.add(formatTask(tasks[i]));
        }

        Files.write(DATA_FILE, records, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
    }

    /**
     * Converts one task into its text representation.
     *
     * @param task the task to format.
     * @return the stored representation of the task.
     */
    private static String formatTask(Task task) {
        String type;
        List<String> fields = new ArrayList<>();

        if (task instanceof Deadline deadline) {
            type = "D";
            fields.add(deadline.getBy());
        } else if (task instanceof Event event) {
            type = "E";
            fields.add(event.getStart());
            fields.add(event.getEnd());
        } else {
            type = "T";
        }

        StringBuilder record = new StringBuilder();
        record.append(type).append(FIELD_SEPARATOR)
                .append(task.isDone() ? "1" : "0").append(FIELD_SEPARATOR)
                .append(escape(task.getDescription()));

        for (String field : fields) {
            record.append(FIELD_SEPARATOR).append(escape(field));
        }

        return record.toString();
    }

    /**
     * Parses one stored task record, returning {@code null} for malformed data.
     *
     * @param line the record to parse.
     * @return the parsed task, or {@code null} when the record is invalid.
     */
    private static Task parseTask(String line) {
        if (line == null || line.isBlank()) {
            return null;
        }

        List<String> fields = splitFields(line);
        String type = fields.get(0).trim();

        if (fields.size() < 3 || !isValidStatus(fields.get(1).trim())) {
            return null;
        }

        String description = fields.get(2).trim();
        if (description.isEmpty()) {
            return null;
        }

        Task task;
        if (type.equals("T") && fields.size() == 3) {
            task = new Todo(description);
        } else if (type.equals("D") && fields.size() == 4 && !fields.get(3).trim().isEmpty()) {
            task = new Deadline(description, fields.get(3).trim());
        } else if (type.equals("E") && fields.size() == 5
                && !fields.get(3).trim().isEmpty() && !fields.get(4).trim().isEmpty()) {
            task = new Event(description, fields.get(3).trim(), fields.get(4).trim());
        } else {
            return null;
        }

        if (fields.get(1).trim().equals("1")) {
            task.markAsDone();
        }

        return task;
    }

    /**
     * Splits a record while preserving escaped separators in user-entered text.
     *
     * @param line the record to split.
     * @return the decoded fields in the record.
     */
    private static List<String> splitFields(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder field = new StringBuilder();
        boolean isEscaped = false;

        for (int i = 0; i < line.length(); i++) {
            char character = line.charAt(i);

            if (isEscaped) {
                if (character == '|' || character == '\\') {
                    field.append(character);
                } else {
                    field.append('\\').append(character);
                }
                isEscaped = false;
            } else if (character == '\\') {
                isEscaped = true;
            } else if (character == '|') {
                fields.add(field.toString().trim());
                field.setLength(0);
            } else {
                field.append(character);
            }
        }

        if (isEscaped) {
            field.append('\\');
        }
        fields.add(field.toString().trim());
        return fields;
    }

    /**
     * Escapes characters that have a structural meaning in a record.
     *
     * @param value the text to escape.
     * @return the escaped text.
     */
    private static String escape(String value) {
        return value.replace("\\", "\\\\").replace("|", "\\|");
    }

    /**
     * Checks whether a stored status is one of the supported values.
     *
     * @param status the status field to validate.
     * @return whether the status is {@code 0} or {@code 1}.
     */
    private static boolean isValidStatus(String status) {
        return status.equals("0") || status.equals("1");
    }
}
