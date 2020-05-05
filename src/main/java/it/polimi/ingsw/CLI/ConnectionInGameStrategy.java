package it.polimi.ingsw.CLI;

import it.polimi.ingsw.ConnectionStatus;

public class ConnectionInGameStrategy implements ConnectionStrategy{
    @Override
    public boolean handleConnection(ConnectionStatus connectionStatus) {
        if(connectionStatus.isClosed()){
            System.out.println(connectionStatus.getReasonOfClosure());
            System.out.print("Do you want to reconnect? (y | n)");
            String choice;
            do{
                choice = InputUtilities.getLine();
                if(choice == null) choice = "";
            }while(!(choice.equals("y") || choice.equals("n") || choice.equals("Y") || choice.equals("N")));

            if(choice.equals("y") || choice.equals("Y")){
                System.out.println("Insert address and port again: ");
                return true;
            }
            else System.out.println("Thank you for playing Santorini, see you next time!");
        }
        return false;
    }
}
