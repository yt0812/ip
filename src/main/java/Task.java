/**
 * Represents one quest in Dobby's task list.
 */
public class Task {

    /** The task's description. */
    protected String description;

    /** Whether this task has been completed. */
    protected boolean isDone;

    /**
     * Creates a new unfinished task.
     *
     * @param description the task's description
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns the symbol used to display this task's completion status.
     *
     * @return {@code X} for a completed task or a space for an unfinished task
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /** Marks this task as completed. */
    public void markAsDone() {
        isDone = true;
    }

    /** Returns this task to the unfinished state. */
    public void markAsUndone() {
        isDone = false;
    }

    /**
     * Returns the text describing this task.
     *
     * @return the task description
     */
    public String getDescription() {
        return description;
    }
}
