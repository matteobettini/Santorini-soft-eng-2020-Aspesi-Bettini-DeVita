package it.polimi.ingsw.CLI.Strategies;

import it.polimi.ingsw.CLI.CLI;
import it.polimi.ingsw.ConnectionStatus;

public interface ConnectionStrategy {
    public void handleConnection(ConnectionStatus connectionStatus, CLI cli);
}
