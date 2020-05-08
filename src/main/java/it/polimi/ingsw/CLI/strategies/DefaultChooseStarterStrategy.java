package it.polimi.ingsw.CLI.strategies;

import it.polimi.ingsw.CLI.Board;
import it.polimi.ingsw.CLI.InputUtilities;
import it.polimi.ingsw.CLI.MatchData;
import it.polimi.ingsw.packets.PacketStartPlayer;

import java.util.Set;

public class DefaultChooseStarterStrategy implements ChooseStarterStrategy {
    @Override
    public void handleChooseStartPlayer() {
        MatchData matchData = MatchData.getInstance();
        Board board = matchData.getBoard();
        String startPlayer;
        System.out.print("\n" + "Choose the starting player by writing his name ( ");
        Set<String> players = matchData.getIds().keySet();
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
        matchData.getClient().send(packetStartPlayer);
    }
}
