package it.polimi.ingsw.client.cli.strategies;

import it.polimi.ingsw.client.cli.CLI;
import it.polimi.ingsw.client.cli.utilities.colors.ForeColor;
import it.polimi.ingsw.client.cli.match_data.MatchData;
import it.polimi.ingsw.client.cli.utilities.OutputUtilities;
import it.polimi.ingsw.common.packets.PacketMatchStarted;

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
        for(String player : packetMatchStarted.getPlayers()) System.out.println("- " + player);
        System.out.print("Selected mode: ");
        if(packetMatchStarted.isHardcore()) System.out.println(ForeColor.ANSI_BRIGHT_RED.getCode() + "Hardcore" + OutputUtilities.ANSI_RESET);
        else System.out.println(ForeColor.ANSI_BRIGHT_GREEN.getCode() + "Normal"+ OutputUtilities.ANSI_RESET);
        matchData.setHardcore(packetMatchStarted.isHardcore());
        cli.setGameModeStrategy(packetMatchStarted.isHardcore());
    }
}
