package it.polimi.ingsw.server;

import it.polimi.ingsw.server.cards.CardFactory;
import it.polimi.ingsw.server.cards.exceptions.InvalidCardException;
import it.polimi.ingsw.server.communication.Server;

import it.polimi.ingsw.server.communication.ServerImpl;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GameServer {

    private static final String VERBOSE_ARGUMENT = "-v";
    private static final String LOGFILE_ARGUMENT = "-log";
    private static final String PORT_ARGUMENT = "-port";
    private static final String HELP_ARGUMENT = "-help";
    private static final int DEFAULT_PORT = 4567;
    private static final int MIN_PORT = 1024;
    private static final int MAX_PORT = 65535;

    public static void main(String[] args) {

        int port = DEFAULT_PORT;

        List<String> arguments = Arrays.asList(args);

        if(arguments.size() == 1 && arguments.contains(HELP_ARGUMENT)){

            String s = "This is the server for Santorini table game, with no input the server will start on port " + DEFAULT_PORT + "\n\n" +
                    "Here is a list of all the available commands:\n\n" +
                    "-port: followed by the desired port number between " + MIN_PORT + " and " + MAX_PORT + " as argument\n" +
                    "-v: to activate logging in the console.\n" +
                    "-log: followed by a file name, to activate logging both in the console and in the chosen file\n" +
                    "-help: to get help\n";
            System.out.println(s);
            return;

        } else if(arguments.contains(PORT_ARGUMENT)){
            String proposedPortString = "";
            try{
                proposedPortString = arguments.get(arguments.indexOf(PORT_ARGUMENT)+1);
            }catch (Exception ignored){ }

            boolean error = false;

            try{
                int proposedPort = Integer.parseInt(proposedPortString);
                if(proposedPort >= MIN_PORT && proposedPort <= MAX_PORT)
                    port = proposedPort;
                else
                    error = true;
            }catch (NumberFormatException ignored){
                error = true;
            }

            if(error){
                System.out.println("Invalid port number, insert " + HELP_ARGUMENT + " to see the available port numbers.");
                return;
            }

            arguments.remove(PORT_ARGUMENT);
            arguments.remove(proposedPortString);
        }

        ServerLogger.preliminarySetup();

        if(arguments.size() == 1 && arguments.contains(VERBOSE_ARGUMENT))
            ServerLogger.setupServerLogger();
        else if((arguments.size() == 1 || arguments.size() == 2) && arguments.contains(LOGFILE_ARGUMENT)) {
            String proposedLogFile = null;
            try {
                proposedLogFile = arguments.get(arguments.indexOf(LOGFILE_ARGUMENT) + 1);
            } catch (Exception ignored) { }

            if (proposedLogFile == null){
                System.out.println("Insert e file name after " + LOGFILE_ARGUMENT + ", insert " + HELP_ARGUMENT + " to see the available configuration options.");
                return;
            }
            ServerLogger.setupServerLogger(proposedLogFile);
        } else if(!arguments.isEmpty()){
            System.out.println("Too many arguments, insert " + HELP_ARGUMENT + " to see the available configuration options.");
            return;
        }

        try {
            CardFactory.getInstance();
        } catch (InvalidCardException e) {
            Logger.getLogger(ServerLogger.LOGGER_NAME).log(Level.SEVERE, "Error loading the cards", e.getMessage());
            return; //Do not load the server if there are errors with the cards
        }

        Server server = new ServerImpl(port);

        server.startServer();
    }
}
