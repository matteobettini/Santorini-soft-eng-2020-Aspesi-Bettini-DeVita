package it.polimi.ingsw;

import it.polimi.ingsw.cards.CardFactory;
import it.polimi.ingsw.cards.exceptions.CardLoadingException;
import it.polimi.ingsw.cards.exceptions.InvalidCardException;
import it.polimi.ingsw.server.Server;

import it.polimi.ingsw.server.ServerImpl;

import java.io.IOException;

public class GameServerMain {

    public static void main(String[] args) throws IOException {

        try {
            CardFactory.getInstance();
        } catch (CardLoadingException | InvalidCardException e) {
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
