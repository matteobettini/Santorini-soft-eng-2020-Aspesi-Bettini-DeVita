package it.polimi.ingsw.CLI.Strategies;

import it.polimi.ingsw.CLI.CLI;
import it.polimi.ingsw.CLI.InputUtilities;

public class DefaultNumberOfPlayers implements NumberOfPlayersStrategy {
    @Override
    public void handNumberOfPlayers(String message, CLI cli) {
        Integer number;
        System.out.print("\n" +message + ": ");
        number = InputUtilities.getInt();
        if(number == null) return;
        cli.getClient().sendInt(number);
    }
}
