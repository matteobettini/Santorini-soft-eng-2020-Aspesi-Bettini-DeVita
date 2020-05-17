package it.polimi.ingsw.client.gui.controllers;

import it.polimi.ingsw.client.gui.enums.ControllerType;
import it.polimi.ingsw.client.gui.match_data.MatchData;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

public class DisconnectController extends GUIController {
    @FXML
    private Label lblMessage;
    @FXML
    private Button btnReturn;

    private MatchData data = MatchData.getInstance();

    /*
        External Handlers
     */
    public void handleDisconnection(String reason){
        lblMessage.setText(reason);
        ensureActive();
    }

    /*
       Graphic Events
    */
    @FXML
    void onBtnReturnClicked(MouseEvent event) {
        data.changeController(ControllerType.SPLASH);
    }
}
