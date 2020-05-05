package it.polimi.ingsw.CLI.Strategies;

import it.polimi.ingsw.CLI.CLI;
import it.polimi.ingsw.CLI.InputUtilities;
import it.polimi.ingsw.CLI.Strategies.NicknameStrategy;

public class NormalNicknameStrategy implements NicknameStrategy {
    @Override
    public void handleNickname(String message, CLI cli) {
        String nickname;
        System.out.print("\n" + message);
        nickname = InputUtilities.getLine();
        if(nickname == null) return;
        cli.getBoard().setPlayerName(nickname);
        cli.getClient().sendString(nickname);
    }
}
