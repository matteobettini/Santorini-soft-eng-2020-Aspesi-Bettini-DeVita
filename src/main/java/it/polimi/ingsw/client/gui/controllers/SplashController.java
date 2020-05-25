package it.polimi.ingsw.client.gui.controllers;

import it.polimi.ingsw.client.gui.enums.ControllerType;
import it.polimi.ingsw.client.gui.match_data.MatchData;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

public class SplashController extends GUIController {
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

    private MatchData data = MatchData.getInstance();

    /*
        External Handlers
     */
    public void handleConnectionFailed(){
        showWait("Cannot connect to Game Server at " + data.getIP() + ":" + data.getPort(), true);
        ensureActive(); //Let's activate if i am not
    }

    public void handleConnectionSuccessful(){
        closeWait();
    }

    /*
        Graphic Events
     */
    @FXML
    private void onBtnStartClicked(MouseEvent event) {
        showWait("Connecting to the Game Server ...", false);
        data.getClient().asyncStart(data.getIP(),data.getPort()); //Try connect
    }
    @FXML
    private void onBtnSettingsClicked(MouseEvent event){
        data.changeController(ControllerType.SETTINGS);
    }

    @FXML
    private void onBtnCloseClicked(MouseEvent event) {
        closeWait();
    }

    /*
        Graphic manipulation
     */
    private void showWait(String message, boolean closeable){
        lblWait.setText(message);
        btnClose.setVisible(closeable);
        imgWait.setVisible(!closeable);
        waitPane.setVisible(true);
    }
    private void closeWait(){
        waitPane.setVisible(false);
    }
}
