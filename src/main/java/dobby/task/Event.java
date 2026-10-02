package dobby.task;

/**
 * Represents a task with a specified start and end date or time.
 */
public class Event extends Task {

    /** The date or time when this event starts. */
    private final String start;

    /** The date or time when this event ends. */
    private final String end;

    /**
     * Creates an unfinished event.
     *
     * @param description the event's description.
     * @param start the date or time when the event starts.
     * @param end the date or time when the event ends.
     */
    public Event(String description, String start, String end) {
        super(description);
        this.start = start;
        this.end = end;
    }

    /**
     * Returns the date or time when this event starts.
     *
     * @return the event start text.
     */
    public String getStart() {
        return start;
    }

    /**
     * Returns the date or time when this event ends.
     *
     * @return the event end text.
     */
    public String getEnd() {
        return end;
    }

    @Override
    public String toString() {
        return "[E][" + getStatusIcon() + "] " + description
                + " (from: " + start + " to: " + end + ")";
    }
}
