package it.polimi.ingsw;

import it.polimi.ingsw.view.ConnectionToClient;

public interface ServerConnectionUtils {

    void lobby(ConnectionToClient connection);
    void deregister(ConnectionToClient connectionToClient);

}
