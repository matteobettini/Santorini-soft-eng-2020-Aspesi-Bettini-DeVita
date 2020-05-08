package it.polimi.ingsw.CLI.strategies;

import it.polimi.ingsw.CLI.InputUtilities;
import it.polimi.ingsw.CLI.MatchData;
import it.polimi.ingsw.packets.PacketNickname;

public class DefaultNicknameStrategy implements NicknameStrategy {
    @Override
    public void handleNickname(String message) {
        MatchData matchData = MatchData.getInstance();
        String nickname;
        System.out.print("\n" + message + ": ");
        nickname = InputUtilities.getLine();
        if(nickname == null) return;
        matchData.setPlayerName(nickname);
        matchData.getClient().send(new PacketNickname(nickname));
    }
}
