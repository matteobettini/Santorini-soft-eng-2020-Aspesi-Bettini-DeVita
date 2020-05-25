package it.polimi.ingsw.client.gui.controllers;

import it.polimi.ingsw.client.gui.match_data.MatchData;
import it.polimi.ingsw.common.packets.PacketNickname;
import it.polimi.ingsw.common.packets.PacketNumOfPlayersAndGamemode;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class SetupController extends GUIController {
    @FXML
    private TextField txtUsername;
    @FXML
    private VBox chooseModePane;
    @FXML
    private ComboBox<String> cboMode;
    @FXML
    private Button btnNext;
    @FXML
    private VBox chooseNumPane;
    @FXML
    private Label lblMsg;
    @FXML
    private Label lblWait;
    @FXML
    private ImageView imgWait;
    @FXML
    private Button btnClose;
    @FXML
    private HBox waitPane;
    @FXML
    private HBox msgPane;
    @FXML
    private ComboBox<Integer> cboPlayerNumber;

    ObservableList<String> gameModes = FXCollections.observableArrayList("Normal", "Hardcore");
    ObservableList<Integer> playersNums = FXCollections.observableArrayList(2, 3);

    private MatchData data = MatchData.getInstance();

    private boolean nickAsked = false;

    @FXML
    private void initialize(){
        cboPlayerNumber.setValue(playersNums.get(0));
        cboPlayerNumber.setItems(playersNums);
        cboMode.setValue(gameModes.get(0));
        cboMode.setItems(gameModes);
    }


    /*
        External Handlers
     */
    public void handleUsernameRequested(boolean isRetry){
        if (!isRetry)
            showMessage("Select a username, then click Next");
        else
            showMessage("Invalid username, choose another");
        txtUsername.setDisable(false);
        displaySettings(false);
        closeWait();
        nickAsked = false;
        ensureActive();
    }
    public void handleGameSettings(boolean isRetry){
        assert (!isRetry); //Settings are fixed
        assert (nickAsked);
        showMessage("Select game settings, then click Next");
        txtUsername.setDisable(true);
        displaySettings(true);
        closeWait();
        ensureActive();
    }


    /*
        Graphic Events
     */
    @FXML
    private void onBtnNextClicked(MouseEvent event) {
        if (!nickAsked)
            answerNickname();
        else
            answerGameSettings();
    }
    @FXML
    private void onBtnCloseClicked(MouseEvent event) {
        closeWait();
    }

    private void answerNickname(){
        String username = txtUsername.getText();
        //Check username validity
        data.setUsername(username);
        //Send username
        nickAsked = true;
        showWait("Matchmaking ...", false);
        PacketNickname packet = new PacketNickname(username);
        data.getClient().send(packet);
    }
    private void answerGameSettings(){
        //Send game settings
        showWait("Matchmaking ...", false);
        PacketNumOfPlayersAndGamemode packet = new PacketNumOfPlayersAndGamemode(cboPlayerNumber.getValue(),gameModes.indexOf(cboMode.getValue()) == 1);
        data.getClient().send(packet);
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
    private void showMessage(String message){
        lblMsg.setText(message);
    }
    private void displaySettings(boolean show){
        chooseModePane.setVisible(show);
        chooseNumPane.setVisible(show);
    }
}
