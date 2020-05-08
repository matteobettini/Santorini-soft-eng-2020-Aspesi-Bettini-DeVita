package it.polimi.ingsw.CLI.strategies;

import it.polimi.ingsw.CLI.CLI;
import it.polimi.ingsw.ConnectionStatus;

public interface ConnectionStrategy {
    void handleConnection(ConnectionStatus connectionStatus, CLI cli);
}
