package it.polimi.ingsw.client.cli.strategies;

public interface NicknameStrategy {
    /**
     * This handler makes the user choose his nickname or displays who is choosing his own one.
     * @param message is the message of request sent from the server.
     */
    void handleNickname(String message);
}
