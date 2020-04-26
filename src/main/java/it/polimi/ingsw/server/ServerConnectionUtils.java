package it.polimi.ingsw.server;

import it.polimi.ingsw.view.ConnectionToClient;

public interface ServerConnectionUtils {

    void lobby(ConnectionToClient connectionToClient);
    void deregister(ConnectionToClient connectionToClient);

}
