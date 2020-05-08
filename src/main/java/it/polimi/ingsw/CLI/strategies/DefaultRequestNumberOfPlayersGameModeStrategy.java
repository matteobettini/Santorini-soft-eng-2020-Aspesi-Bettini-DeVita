package it.polimi.ingsw.CLI.strategies;

import it.polimi.ingsw.CLI.InputUtilities;
import it.polimi.ingsw.CLI.MatchData;
import it.polimi.ingsw.packets.PacketNumOfPlayersAndGamemode;

public class DefaultRequestNumberOfPlayersGameModeStrategy implements RequestNumberOfPlayersGameModeStrategy{
        @Override
        public void handleRequestNumberOfPlayerGameMode(String message) {
            MatchData matchData = MatchData.getInstance();
            Integer number;
            System.out.println("\n" +message);
            System.out.print("Number of Players: ");
            number = InputUtilities.getInt("Not a number, retry\nNumber of Players: ");
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
