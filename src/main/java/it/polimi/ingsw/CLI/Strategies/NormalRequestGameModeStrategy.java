package it.polimi.ingsw.CLI.Strategies;

import it.polimi.ingsw.CLI.CLI;
import it.polimi.ingsw.CLI.InputUtilities;

public class NormalRequestGameModeStrategy implements RequestGameModeStrategy {
    @Override
    public void handleRequestGameMode(String message, CLI cli) {
        String choice;
        System.out.println("\n" +message);
        choice = InputUtilities.getLine();
        if(choice == null) return;
        cli.getClient().sendBoolean(choice.equals("y") || choice.equals("Y"));
    }
}
