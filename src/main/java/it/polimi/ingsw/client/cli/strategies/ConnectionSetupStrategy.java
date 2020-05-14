package it.polimi.ingsw.client.cli.strategies;

import it.polimi.ingsw.client.cli.CLI;
import it.polimi.ingsw.client.communication.enums.ConnectionState;
import it.polimi.ingsw.client.communication.ConnectionStatus;

public class ConnectionSetupStrategy implements ConnectionStrategy {

    /**
     * This handler displays to the user the reason of closure of the connection during the setup.
     * Only two possible connection states are possible during the setup:
     * - Unable to connect if the chosen server can't be reached.
     * - Connected if the connection is correctly established.
     * @param connectionStatus is the connection's status communicated by the server.
     * @param cli is the instance of the cli that has the method run which will execute the entire process again, it can also be used
     * to set the connection strategy to the ConnectionInGameStrategy if the connection is established.
     */
    @Override
    public void handleConnection(ConnectionStatus connectionStatus, CLI cli) {

        ConnectionState connectionState = connectionStatus.getState();

        assert connectionState != ConnectionState.MATCH_ENDED && connectionState !=  ConnectionState.CLOSURE_UNEXPECTED;

        if(connectionState == ConnectionState.CONNECTED){
            System.out.println("Connection established!");
            cli.setConnectionInGameStrategy();
        }
        else{
            System.out.println("\n" + connectionStatus.getReasonOfClosure());
            cli.setAskConnectionParameters(true);
            cli.run();
        }

    }
}
