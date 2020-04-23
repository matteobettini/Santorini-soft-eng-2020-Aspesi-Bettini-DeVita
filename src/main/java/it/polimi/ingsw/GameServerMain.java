package it.polimi.ingsw;

import java.io.IOException;

public class GameServerMain {

    public static void main(String[] args) throws IOException {

        Server server = new Server();
        server.startServer();

    }
}
