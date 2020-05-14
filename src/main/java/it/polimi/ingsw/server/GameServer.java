package it.polimi.ingsw.server;

import it.polimi.ingsw.server.cards.CardFactory;
import it.polimi.ingsw.server.cards.exceptions.InvalidCardException;
import it.polimi.ingsw.server.communication.Server;

import it.polimi.ingsw.server.communication.ServerImpl;

import java.io.IOException;

public class GameServer {

    public static void main(String[] args) throws IOException {

        try {
            CardFactory.getInstance();
        } catch (InvalidCardException e) {
            System.err.println("[" + e.getClass().toString() + "]" + e.getMessage());
            return; //Do not load the server if there are errors with the cards
        }

        int port = 4567;

        if(args.length > 0){
            try{
                int proposedPort = Integer.parseInt(args[0]);
                if(proposedPort >= 1024 && proposedPort <= 65535)
                    port = proposedPort;
            }catch (NumberFormatException ignored){ }
        }

        Server server = new ServerImpl(port);

        server.startServer();
    }
}
