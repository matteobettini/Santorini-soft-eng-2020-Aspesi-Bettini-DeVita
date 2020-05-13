package it.polimi.ingsw.CLI.strategies;

import it.polimi.ingsw.CLI.InputUtilities;
import it.polimi.ingsw.CLI.MatchData;
import it.polimi.ingsw.packets.PacketNumOfPlayersAndGamemode;

public class DefaultRequestNumberOfPlayersGameModeStrategy implements RequestNumberOfPlayersGameModeStrategy{

    /**
     * This method handles the request of the game-mode (normal or hardcore) and the number of players.
     * @param message is the message of request sent from the server.
     */
    @Override
    public void handleRequestNumberOfPlayerGameMode(String message, boolean isRetry) {
        MatchData matchData = MatchData.getInstance();
        Integer number;

        String retryPrefix = "The chosen number of players is incorrect, ";

        System.out.println("\n" + (isRetry ? retryPrefix : "") + message);
        System.out.print("Number of Players: ");
        number = InputUtilities.getInt("Not a number,select a valid number of players: ");
        if(number == null) return;

        String choice;
        do{
            System.out.print("Do you want to play in hardcore? (y | n) ");
            choice = InputUtilities.getLine();
            if(choice == null) return;
        }while(!(choice.toLowerCase().equals("y") || choice.toLowerCase().equals("n")));

        System.out.println("\nMatchmaking...");
        matchData.getClient().send(new PacketNumOfPlayersAndGamemode(number, choice.toLowerCase().equals("y")));
    }
}
