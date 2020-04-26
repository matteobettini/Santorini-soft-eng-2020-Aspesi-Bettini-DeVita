package it.polimi.ingsw;

import it.polimi.ingsw.server.Server;
import it.polimi.ingsw.server.ServerFactory;

import java.io.IOException;

public class GameServerMain {

    public static void main(String[] args) throws IOException {

        Server server = ServerFactory.createServer();
        server.startServer();

    }
}
