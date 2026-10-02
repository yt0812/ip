package dobby.command;

import dobby.ui.Ui;

/**
 * Represents the command that ends the Dobby session.
 */
public final class ExitCommand extends Command {

    /**
     * Executes the exit response.
     *
     * @param ui the user interface used to display the exit response.
     */
    @Override
    public void execute(Ui ui) {
        ui.showMessage("Bye for now! Dobby is off to polish the quest scrolls. Stay mighty!");
    }

    /**
     * Returns that this command ends the Dobby session.
     *
     * @return true because this is the exit command.
     */
    @Override
    public boolean isExit() {
        return true;
    }
}
