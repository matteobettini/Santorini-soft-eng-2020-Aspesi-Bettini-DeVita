package it.polimi.ingsw.CLI;

import it.polimi.ingsw.ConnectionStatus;

public interface ConnectionStrategy {
    public boolean handleConnection(ConnectionStatus connectionStatus);
}
