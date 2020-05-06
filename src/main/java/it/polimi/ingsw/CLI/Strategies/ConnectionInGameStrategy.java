package it.polimi.ingsw.CLI.Strategies;

import it.polimi.ingsw.CLI.CLI;
import it.polimi.ingsw.CLI.InputUtilities;
import it.polimi.ingsw.ConnectionStatus;

public class ConnectionInGameStrategy implements ConnectionStrategy {
    @Override
    public void handleConnection(ConnectionStatus connectionStatus, CLI cli) {
        if(connectionStatus.isClosed()){
            System.out.println("\n" + connectionStatus.getReasonOfClosure());
            System.out.print("Do you want to reconnect? (y | n) ");
            String choice;
            do{
                choice = InputUtilities.getLine();
                if(choice == null) choice = "";
            }while(!(choice.toLowerCase().equals("y") || choice.toLowerCase().equals("n")));

            if(choice.toLowerCase().equals("y")){
                cli.setAskConnectionParameters(false);
                cli.run();
            }
            else{
                System.out.println("Thank you for playing Santorini, see you next time!");
            }
        }

    }
}
