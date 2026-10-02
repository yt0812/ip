package dobby.task;

/**
 * Represents a task that must be completed by a specified date or time.
 */
public class Deadline extends Task {

    /** The date or time by which this task should be completed. */
    private final String by;

    /**
     * Creates an unfinished deadline.
     *
     * @param description the task's description.
     * @param deadline the date or time by which the task should be completed.
     */
    public Deadline(String description, String deadline) {
        super(description);
        this.by = deadline;
    }

    /**
     * Returns the date or time by which this task should be completed.
     *
     * @return the deadline text.
     */
    public String getBy() {
        return by;
    }

    @Override
    public String toString() {
        return "[D][" + getStatusIcon() + "] " + description + " (by: " + by + ")";
    }
}
