package it.polimi.ingsw.client;

import it.polimi.ingsw.client.cli.CLI;
import it.polimi.ingsw.client.gui.GUI;

public class GameClient {

    private static final String CLI_ARGUMENT = "--cli";
    private static final String HELP_ARGUMENT = "--help";

    public static void main(String[] args){
        if (args.length == 0) GUI.main(args);
        else if(args.length > 1) System.out.println("Too many arguments, insert " + HELP_ARGUMENT + " to see the available graphical interface options.");
        else{
            if(CLI_ARGUMENT.equals(args[0])) CLI.main(args);
            else if(HELP_ARGUMENT.equals(args[0])) System.out.println("Insert " + CLI_ARGUMENT + " to start the game in command line interface mode, otherwise don't insert anything to start the GUI.");
            else System.out.println("Command not found, insert " + HELP_ARGUMENT + " to see the available graphical interface options.");
        }

    }
}
