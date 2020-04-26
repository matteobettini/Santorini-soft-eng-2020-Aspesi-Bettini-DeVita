package it.polimi.ingsw.server;

public class ServerFactory {

    /**
     * This method builds a server and returns it as a Server interface
     * @return the created server
     */
    public static Server createServer(){
        return new ServerImpl();
    }
}
