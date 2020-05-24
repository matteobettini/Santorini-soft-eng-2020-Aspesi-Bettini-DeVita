package it.polimi.ingsw.server;

import it.polimi.ingsw.server.cards.CardFactory;
import it.polimi.ingsw.server.cards.exceptions.InvalidCardException;
import it.polimi.ingsw.server.communication.Server;

import it.polimi.ingsw.server.communication.ServerImpl;

import java.io.IOException;

public class GameServer {

    private static final String HELP_ARGUMENT = "--help";
    private static final int DEFAULT_PORT = 4567;
    private static final int MIN_PORT = 1024;
    private static final int MAX_PORT = 65535;

    public static void main(String[] args) throws IOException {

        int port = DEFAULT_PORT;

        if(args.length == 1 && HELP_ARGUMENT.equals(args[0])){
           System.out.println("Insert a port number between " + MIN_PORT + " and " + MAX_PORT + " as argument or don't insert anything to start the server on port " + DEFAULT_PORT + ".");
           return;
        }
        else if(args.length == 1){
            boolean error = false;

            try{
                int proposedPort = Integer.parseInt(args[0]);
                if(proposedPort >= MIN_PORT && proposedPort <= MAX_PORT)
                    port = proposedPort;
                else error = true;
            }catch (NumberFormatException ignored){
                error = true;
            }

            if(error){
                System.out.println("Invalid port number, insert " + HELP_ARGUMENT + " to see the available port numbers.");
                return;
            }
        }
        else if(args.length > 1) {
            System.out.println("Too many arguments, insert " + HELP_ARGUMENT + " to see the available configuration options.");
            return;
        }

        try {
            CardFactory.getInstance();
        } catch (InvalidCardException e) {
            System.err.println("[" + e.getClass().toString() + "]" + e.getMessage());
            return; //Do not load the server if there are errors with the cards
        }

        Server server = new ServerImpl(port);

        server.startServer();
    }
}
