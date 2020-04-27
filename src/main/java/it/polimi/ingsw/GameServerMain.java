package it.polimi.ingsw;

import it.polimi.ingsw.cards.CardFactory;
import it.polimi.ingsw.cards.exceptions.CardLoadingException;
import it.polimi.ingsw.cards.exceptions.InvalidCardException;
import it.polimi.ingsw.server.Server;
import it.polimi.ingsw.server.ServerFactory;

import java.io.IOException;

public class GameServerMain {

    public static void main(String[] args) throws IOException {
        try {
            CardFactory.getInstance();
        } catch (CardLoadingException | InvalidCardException e) {
            System.err.println("[" + e.getClass().toString() + "]" + e.getMessage());
            return; //Do not load the server if there are errors with the cards
        }
        Server server = ServerFactory.createServer();
        server.startServer();
    }
}
