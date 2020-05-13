package it.polimi.ingsw.CLI.strategies;

import it.polimi.ingsw.CLI.CLI;
import it.polimi.ingsw.CLI.InputUtilities;
import it.polimi.ingsw.ConnectionState;
import it.polimi.ingsw.ConnectionStatus;

public class ConnectionInGameStrategy implements ConnectionStrategy {

    /**
     * This handler displays to the user the reason of closure of the connection during the game.
     * Only two possible connection states are possible during the game:
     * - Match ended because there is a winner.
     * - Closure Unexpected if the connection is terminated for unexpected reasons.
     * @param connectionStatus is the connection's status communicated by the server.
     * @param cli is the instance of the cli that has the method run which will execute the entire process again.
     */
    @Override
    public void handleConnection(ConnectionStatus connectionStatus, CLI cli) {
        ConnectionState connectionState = connectionStatus.getState();

        assert connectionState != ConnectionState.CONNECTED && connectionState !=  ConnectionState.UNABLE_TO_CONNECT;

        System.out.println("\n" + connectionStatus.getReasonOfClosure());
        String choice;
        do{
            System.out.print("Do you want to reconnect? (y | n) ");
            choice = InputUtilities.getLine();
            if(choice == null) return;
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
