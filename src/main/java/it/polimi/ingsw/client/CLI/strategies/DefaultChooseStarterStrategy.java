package it.polimi.ingsw.client.CLI.strategies;

import it.polimi.ingsw.client.CLI.InputUtilities;
import it.polimi.ingsw.client.CLI.MatchData;
import it.polimi.ingsw.common.packets.PacketStartPlayer;

import java.util.Set;

public class DefaultChooseStarterStrategy implements ChooseStarterStrategy {

    /**
     * This handler asks the queried player (also called challenger) the starting player. If the active player is not the user
     * this method will display who is choosing the starting player.
     * The server will eventually check the chosen starting player and if it is necessary ask it another time.
     * @param activePlayer is the player asked to do this action.
     * @param isRetry true if the action is requested another time, false otherwise.
     */
    @Override
    public void handleChooseStartPlayer(String activePlayer, boolean isRetry) {
        MatchData matchData = MatchData.getInstance();

        if(!activePlayer.equals(matchData.getPlayerName())){
            System.out.println("\n" + activePlayer + " is choosing the starting player...");
            return;
        }

        String startPlayer;
        if(!isRetry){
            System.out.print("\n" + "Choose the starting player by writing his name (");
            Set<String> players = matchData.getIds().keySet();
            int size = players.size();
            int count = 1;
            for(String player : players){
                if(count != size) System.out.print(player + ", ");
                else System.out.print(player);
                ++count;
            }
            System.out.print("): ");
        }
        else System.out.print("\n" + "Choose a valid nickname: ");
        startPlayer = InputUtilities.getLine();
        if(startPlayer == null) return;

        PacketStartPlayer packetStartPlayer = new PacketStartPlayer(startPlayer);
        matchData.getClient().send(packetStartPlayer);
    }
}
