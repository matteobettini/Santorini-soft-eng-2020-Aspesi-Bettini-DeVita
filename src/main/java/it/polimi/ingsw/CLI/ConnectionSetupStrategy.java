package it.polimi.ingsw.CLI;

import it.polimi.ingsw.ConnectionStatus;

public class ConnectionSetupStrategy implements ConnectionStrategy{
    @Override
    public boolean handleConnection(ConnectionStatus connectionStatus) {
        if(connectionStatus.isClosed()){
            System.out.println(connectionStatus.getReasonOfClosure());
            return true;
        }
        else{
            System.out.println("Connection established!");
            return false;
        }

    }
}
