package it.polimi.ingsw.CLI.Strategies;

import it.polimi.ingsw.CLI.CLI;
import it.polimi.ingsw.CLI.InputUtilities;
import it.polimi.ingsw.packets.PacketNumOfPlayersAndGamemode;

public class DefaultRequestNumberOfplayersGameModeStrategy implements RequestNumberOfPlayersGameModeStrategy {
    @Override
    public void handleRequestNumberOfPlayerGameMode(String message, CLI cli) {

        Integer number;
        System.out.print("\n" +message + ": ");
        number = InputUtilities.getInt();
        if(number == null) return;

        String choice;
        choice = InputUtilities.getLine();
        if(choice == null) return;

        cli.getClient().send(new PacketNumOfPlayersAndGamemode(number, choice.toLowerCase().equals("normal")));
    }
}
