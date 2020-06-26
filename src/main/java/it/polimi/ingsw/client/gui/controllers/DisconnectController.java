package it.polimi.ingsw.client.gui.controllers;

import it.polimi.ingsw.client.gui.utils.GUISwitcher;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
/**
 * This controller is responsible for in-game connection lost.
 * It shows the reason of the connection problem.
 */
public class DisconnectController extends GUIController {
    /*
      -----------------------
        Graphical bindings
      -----------------------
    */
    @FXML
    private Label lblMessage;
    @FXML
    private Button btnReturn;

    /*
      -----------------------
        External Handlers
      -----------------------
      Description: invoked from external when a connection lost in game is fired
     */
    public void handleDisconnection(String reason){
        lblMessage.setText(reason);
        ensureActive();
    }

    /*
     ---------------------
       Graphical Events
     ---------------------
     Description: invoked from JavaFX runtime when the user interacts with the GUI
    */
    @FXML
    void onBtnReturnClicked(MouseEvent event) {
        //Returns to home
        GUISwitcher.getInstance().activateDefault();
    }
}
