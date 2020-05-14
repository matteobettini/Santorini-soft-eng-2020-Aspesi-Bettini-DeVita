package it.polimi.ingsw.client.cli.strategies;

import it.polimi.ingsw.client.cli.CLI;
import it.polimi.ingsw.client.communication.ConnectionStatus;

public interface ConnectionStrategy {
    /**
     * This handler processes the messages about the connection sent from the server.
     * @param connectionStatus contains the info about the connection status.
     * @param cli is the instance of the cli that can be used to set the connection strategy or call the method run that
     * will execute entire process again.
     */
    void handleConnection(ConnectionStatus connectionStatus, CLI cli);
}
