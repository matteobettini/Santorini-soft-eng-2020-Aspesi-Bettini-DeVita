package it.polimi.ingsw.CLI.strategies;

import it.polimi.ingsw.CLI.MatchData;
import it.polimi.ingsw.packets.PacketMatchStarted;

public class DefaultMatchStartedStrategy implements MatchStartedStrategy {
    @Override
    public void handleMatchStarted(PacketMatchStarted packetMatchStarted) {
        MatchData matchData = MatchData.getInstance();
        System.out.println("\n" + "The match has started!");
        System.out.println("Players in game: ");
        for(String player : packetMatchStarted.getPlayers()){
            System.out.println("- " + player);
        }
        System.out.print("Selected mode: ");
        if(packetMatchStarted.isHardcore()) System.out.println("Hardcore");
        else System.out.println("Normal");
    }
}
