package it.polimi.ingsw.client.gui.controllers;

import it.polimi.ingsw.client.gui.utils.GUISwitcher;
import it.polimi.ingsw.client.gui.match_data.MatchData;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

/**
 * This is the default controller for this application.
 * It manages connections tries with the game server.
 */
public class SplashController extends GUIController {
    /*
      -----------------------
        Graphical bindings
      -----------------------
    */
    @FXML
    private HBox waitPane;
    @FXML
    private Button btnClose;
    @FXML
    private Button btnStart;
    @FXML
    private ImageView imgWait;
    @FXML
    private Label lblWait;

    /*
      -----------------------
        Private attributes
      -----------------------
     */
    private MatchData data = MatchData.getInstance();

    /*
      -----------------------
        External Handlers
      -----------------------
      Description: invoked from external when a correspondent packet arrives
     */

    /**
     * Called when a connection attempt fails
     */
    public void handleConnectionFailed(){
        showWait("Cannot connect to Game Server at " + data.getIP() + ":" + data.getPort(), true);
        ensureActive(); //Let's activate if i am not
    }

    /**
     * Called when connection is established
     */
    public void handleConnectionSuccessful(){
        closeWait();
    }

    /*
      ---------------------
        Graphical Events
      ---------------------
      Description: invoked from JavaFX runtime when the user interacts with the GUI
     */
    @FXML
    private void onBtnStartClicked(MouseEvent event) {
        showWait("Connecting to the Game Server ...", false);
        data.getClient().asyncStart(data.getIP(),data.getPort(),true); //Try connect
    }
    @FXML
    private void onBtnSettingsClicked(MouseEvent event){
        GUISwitcher.getInstance().activateSettings();
    }

    @FXML
    private void onBtnCloseClicked(MouseEvent event) {
        closeWait();
    }

    /*
      ---------------------------------
         Graphic manipulation
      ---------------------------------
      Description: functions to change interaction mode with user
     */

    /**
     * Shows a full-scene message, that can be closed or not by the user
     * @param message Message to be displayed
     * @param closeable True if the message can be closed by the user
     */
    private void showWait(String message, boolean closeable){
        lblWait.setText(message);
        btnClose.setVisible(closeable);
        imgWait.setVisible(!closeable);
        waitPane.setVisible(true);
    }

    /**
     * Close a full-scene message
     */
    private void closeWait(){
        waitPane.setVisible(false);
    }
}
