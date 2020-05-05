package it.polimi.ingsw.CLI.Strategies;

import it.polimi.ingsw.CLI.Board;
import it.polimi.ingsw.CLI.CLI;
import it.polimi.ingsw.CLI.InputUtilities;
import it.polimi.ingsw.packets.PacketStartPlayer;

import java.util.Set;

public class NormalChooseStarterStrategy implements ChooseStarterStrategy {
    @Override
    public void handleChooseStartPlayer(CLI cli) {
        Board board = cli.getBoard();
        String startPlayer;
        System.out.print("\n" + "Choose the starting player by writing his name ( ");
        Set<String> players = board.getIds().keySet();
        int size = players.size();
        int count = 1;
        for(String player : players){
            if(count != size) System.out.print(player + ", ");
            else System.out.print(player + " ");
            ++count;
        }
        System.out.print("): ");
        startPlayer = InputUtilities.getLine();
        if(startPlayer == null) return;

        PacketStartPlayer packetStartPlayer = new PacketStartPlayer(startPlayer);
        cli.getClient().send(packetStartPlayer);
    }
}
