package it.polimi.ingsw.MVC_example.client;

public class Main {

    public static void main(String[] args){
        View myView = new View();
        Client client = new Client();

        client.addListener(myView);
        myView.setClient(client);

        client.startCLient("127.0.0.1", 4567);
        myView.dispalyView();

    }

}
