package dobby.command;

import dobby.exception.DobbyException;
import dobby.ui.Ui;

/**
 * Represents an action that Dobby can execute.
 */
public abstract class Command {

    /**
     * Executes this command using the supplied user interface.
     *
     * @param ui the user interface used to display command responses.
     * @throws DobbyException when the command cannot be executed.
     */
    public abstract void execute(Ui ui) throws DobbyException;

    /**
     * Returns whether this command ends the Dobby session.
     *
     * @return true when this command requests application exit.
     */
    public boolean isExit() {
        return false;
    }
}
