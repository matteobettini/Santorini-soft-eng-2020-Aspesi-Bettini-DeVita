package it.polimi.ingsw.server;

import it.polimi.ingsw.view.ConnectionToClient;

public interface ServerConnectionUtils {

    /**
     * This method puts the selected client into the lobby
     * If the lobby is empty it asks the desired num of players and the gamemode
     * If the lobby is not empty it keeps asking a new username till a valid one is obtained
     * When the lobby size reaches the desired match size, a new match is created and started
     * If anything fails de-registration is handled
     * @param connectionToClient the connection to put in the lobby
     */
    void lobby(ConnectionToClient connectionToClient);


    /**
     * This methods de-registers the selected client from the server
     * First it looks in the existing matches if the client is present and if it is it sends the termination
     * signal to that match and removes it from the active matches
     * Then it looks if the client is in the lobby and, if it is,
     * the client is removed from the lobby
     * @param connectionToClient the client to be de-registered
     */
    void deregister(ConnectionToClient connectionToClient);

}
