package it.polimi.ingsw;

public class ServerFactory {

    public static Server createServer(){
        return new ServerImpl();
    }
}
