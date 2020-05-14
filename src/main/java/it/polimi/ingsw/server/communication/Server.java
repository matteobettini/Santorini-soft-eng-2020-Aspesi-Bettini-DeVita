package it.polimi.ingsw.server.communication;

import java.io.IOException;

public interface Server {

    /**
     * This method creates a server socket
     * and then loops continuing to accept incoming connections
     * and assigning them to a thread in the thread pool
     * @throws IOException when it occurs during server shutdown
     */
    void startServer() throws IOException;

}
