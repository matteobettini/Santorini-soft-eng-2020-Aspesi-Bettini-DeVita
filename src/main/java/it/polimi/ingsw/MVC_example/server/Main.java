package it.polimi.ingsw.MVC_example.server;

import java.io.IOException;

public class Main {

    public static void main(String[] args){

        Model myModel = new Model();
        ModelView myModelView = new ModelView();
        Controller myController = new Controller(myModelView, myModel);
        Server myServer = new Server(4567);

        myServer.addListener(myModelView);
        myModelView.setServer(myServer);
        myModel.addListener(myModelView);
        myModelView.addListener(myController);

        try {
            myServer.startServer();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
