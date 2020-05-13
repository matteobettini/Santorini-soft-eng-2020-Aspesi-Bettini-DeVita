package it.polimi.ingsw.CLI.strategies;

import it.polimi.ingsw.CLI.InputUtilities;
import it.polimi.ingsw.CLI.MatchData;
import it.polimi.ingsw.packets.PacketNickname;

public class DefaultNicknameStrategy implements NicknameStrategy {

    /**
     * This method handles the request of the nickname to the user. It also sets the user nickname in the MatchData instance.
     * @param message contains the message of request sent from the server.
     */
    @Override
    public void handleNickname(String message) {
        MatchData matchData = MatchData.getInstance();
        String nickname;
        System.out.print("\n" + message + ": ");
        nickname = InputUtilities.getLine();
        if(nickname == null) return;
        matchData.setPlayerName(nickname);
        System.out.println("\n" + "Waiting in lobby...");
        matchData.getClient().send(new PacketNickname(nickname));
    }
}
