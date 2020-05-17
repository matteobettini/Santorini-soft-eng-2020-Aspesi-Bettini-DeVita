package it.polimi.ingsw.client.gui.match_data;

import it.polimi.ingsw.client.communication.Client;
import it.polimi.ingsw.client.communication.ClientImpl;
import it.polimi.ingsw.client.gui.controllers.DisconnectController;
import it.polimi.ingsw.client.gui.controllers.GUIController;
import it.polimi.ingsw.client.gui.controllers.SetupController;
import it.polimi.ingsw.client.gui.controllers.SplashController;
import it.polimi.ingsw.client.gui.enums.ControllerType;
import javafx.scene.Scene;

public class MatchData {
    private static MatchData instance = null;


    private String IP;
    private Integer Port;

    private Client client;

    private String username;


    private SplashController splashController;
    private DisconnectController disconnectController;
    private SetupController setupController;

    private GUIController lastController;

    private MatchData(){
        IP = "127.0.0.1";
        Port = 4567;
    }

    public static MatchData getInstance() {
        if (instance == null) instance = new MatchData();
        return instance;
    }
    /*
        Client
     */
    public void setNewClient(){
        this.client = new ClientImpl();
    }
    public Client getClient() {
        assert (client != null);
        return client;
    }

    public String getIP() {
        return IP;
    }
    public void setIP(String IP) {
        this.IP = IP;
    }
    public Integer getPort() {
        return Port;
    }
    public void setPort(Integer port) {
        Port = port;
    }

    /*
        User info
     */

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    /*
            Controllers
         */
    public void setSplashController(SplashController splashController) {
        this.splashController = splashController;
    }
    public void setDisconnectController(DisconnectController disconnectController) {
        this.disconnectController = disconnectController;
    }
    public void setSetupController(SetupController setupController) {
        this.setupController = setupController;
    }

    public SplashController getSplashController() {
        assert splashController != null;
        return splashController;
    }
    public DisconnectController getDisconnectController() {
        assert disconnectController != null;
        return disconnectController;
    }
    public SetupController getSetupController() {
        return setupController;
    }

    public void changeController(ControllerType next){
        if (lastController != null)
            lastController.deactive();
        lastController = controllerTypeToController(next);
        lastController.activate();
    }
    private GUIController controllerTypeToController(ControllerType type){
        GUIController controller = null;
        switch (type){
            case SPLASH:
                controller = splashController;
                break;
            case DISCONNECTED:
                controller = disconnectController;
                break;
            case SETUP:
                controller = setupController;
                break;
        }
        assert controller != null;
        return controller;
    }
}
