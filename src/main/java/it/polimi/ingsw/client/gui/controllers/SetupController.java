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

/**
 *  This controller is responsible for game-starting setup.
 *  This includes choosing nickname and eventually game settings
 */
public class SetupController extends GUIController {
    /*
      -----------------------
        Graphical bindings
      -----------------------
    */
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

    /*
      -----------------------
        Private attributes
      -----------------------
     */
    ObservableList<String> gameModes = FXCollections.observableArrayList("Normal", "Hardcore"); //Game modes
    ObservableList<Integer> playersNums = FXCollections.observableArrayList(2, 3); //Players numbers

    private MatchData data = MatchData.getInstance();
    private boolean nickAsked = false; //Save if nick was already asked

    /**
     * This method is fired from FXML loader after controller is loaded
     */
    @FXML
    private void initialize(){
        //Setup game modes
        cboMode.setItems(gameModes);
        cboMode.setValue(gameModes.get(0));
        //Setup players numbers
        cboPlayerNumber.setItems(playersNums);
        cboPlayerNumber.setValue(playersNums.get(0));
    }


    /*
      -----------------------
        External Handlers
      -----------------------
      Description: invoked from external when a correspondent packet arrives
     */

    /**
     * Called when packet choose nickname arrives
     * @param isRetry True if first nickname was not available
     */
    public void handleUsernameRequested(boolean isRetry){
        if (!isRetry)
            showMessage("Select a username, then click Next");
        else
            showWait("Invalid username, choose another", true);
        //Init graphics
        txtUsername.setDisable(false);
        displaySettings(false); //Hide game settings
        closeWait();
        nickAsked = false;
        ensureActive();
    }

    /**
     * Called when the user is required to supply game settings to the server
     * @param isRetry True if previous game settings where not available
     */
    public void handleGameSettings(boolean isRetry){
        assert (!isRetry); //Should not be possible
        assert (nickAsked); //Server always asks username first
        //Init graphics
        showMessage("Select game settings, then click Next");
        txtUsername.setDisable(true);
        displaySettings(true);
        closeWait();
        ensureActive();
    }


    /*
      ---------------------
        Graphical Events
      ---------------------
      Description: invoked from JavaFX runtime when the user interacts with the GUI
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

    /**
     * Answers to server with username data
     */
    private void answerNickname(){
        String username = txtUsername.getText();
        //Check username validity
        if (username.trim().length() == 0){
            showWait("You should provide a valid non-empty nickname",true);
            return;
        }
        data.setUsername(username); //Save it
        //Send username
        nickAsked = true;
        showWait("Matchmaking ...", false);
        PacketNickname packet = new PacketNickname(username); //Send data
        data.getClient().send(packet);
    }

    /**
     * Answer server with game settings data
     */
    private void answerGameSettings(){
        //Send game settings
        showWait("Matchmaking ...", false);
        PacketNumOfPlayersAndGamemode packet = new PacketNumOfPlayersAndGamemode(cboPlayerNumber.getValue(),gameModes.indexOf(cboMode.getValue()) == 1);
        data.getClient().send(packet);
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

    /**
     * Show a top-center permanent message on the scene
     * @param message Message string
     */
    private void showMessage(String message){
        lblMsg.setText(message);
    }

    /**
     * Shows settings elements
     * @param show True if the elements must be visible, false otherwise
     */
    private void displaySettings(boolean show){
        chooseModePane.setVisible(show);
        chooseNumPane.setVisible(show);
    }
}
