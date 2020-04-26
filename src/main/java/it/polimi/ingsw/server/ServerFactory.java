package it.polimi.ingsw.server;

public class ServerFactory {

    public static Server createServer(){
        return new ServerImpl();
    }
}
