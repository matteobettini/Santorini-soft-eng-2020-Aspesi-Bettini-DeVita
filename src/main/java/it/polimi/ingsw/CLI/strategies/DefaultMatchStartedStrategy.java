package it.polimi.ingsw.CLI.strategies;

import it.polimi.ingsw.CLI.CLI;
import it.polimi.ingsw.CLI.MatchData;
import it.polimi.ingsw.packets.PacketMatchStarted;

public class DefaultMatchStartedStrategy implements MatchStartedStrategy {

    /**
     * This method handles a PacketMatchStarted containing the info about the match such as the player ids or
     * the selected mode (normal or hardcore) and displays everything to the user. It also set the game-mode in the MatchData instance
     * and set the related GameModeStrategy in the cli instance.
     * @param packetMatchStarted is the packet with the players in the match and the selected game-mode.
     * @param cli is the instance of the cli used to set the GameModeStrategy.
     */
    @Override
    public void handleMatchStarted(PacketMatchStarted packetMatchStarted, CLI cli) {
        MatchData matchData = MatchData.getInstance();
        System.out.println("\n" + "The match has started!");
        System.out.println("Players in game: ");
        for(String player : packetMatchStarted.getPlayers()){
            System.out.println("- " + player);
        }
        System.out.print("Selected mode: ");
        if(packetMatchStarted.isHardcore()) System.out.println("Hardcore");
        else System.out.println("Normal");
        matchData.setHardcore(packetMatchStarted.isHardcore());
        cli.setGameModeStrategy(packetMatchStarted.isHardcore());
    }
}
