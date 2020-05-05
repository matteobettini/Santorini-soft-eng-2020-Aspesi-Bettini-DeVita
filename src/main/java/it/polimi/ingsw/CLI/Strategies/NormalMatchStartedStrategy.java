package it.polimi.ingsw.CLI.Strategies;

import it.polimi.ingsw.CLI.CLI;
import it.polimi.ingsw.CLI.Strategies.MatchStartedStrategy;
import it.polimi.ingsw.packets.PacketMatchStarted;

public class NormalMatchStartedStrategy implements MatchStartedStrategy {
    @Override
    public void handleMatchStarted(PacketMatchStarted packetMatchStarted, CLI cli) {
        System.out.println("\n" + "The match has started!");
        System.out.println("Players in game: ");
        for(String player : packetMatchStarted.getPlayers()){
            System.out.println("- " + player);
        }
        System.out.print("Selected mode: ");
        if(packetMatchStarted.isHardcore()) System.out.println("Hardcore");
        else System.out.println("Normal");
        cli.setActionStrategy(packetMatchStarted.isHardcore());
        cli.getBoard().setHardcore(packetMatchStarted.isHardcore());
    }
}
