package it.polimi.ingsw.client.gui.controllers;

import it.polimi.ingsw.client.gui.utils.GUISwitcher;
import it.polimi.ingsw.client.gui.match_data.MatchData;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import java.util.regex.Pattern;

/**
 * This controller is responsible for taking user settings for the match
 */
public class SettingsController extends GUIController {
    /*
      -----------------------
        Graphical bindings
      -----------------------
    */
    @FXML
    private TextField txtPort;
    @FXML
    private Button btnCancel;
    @FXML
    private Button btnApply;
    @FXML
    private Label lblMsg;
    @FXML
    private HBox msgPane;
    @FXML
    private TextField txtIP;
    @FXML
    private Label lblWait;
    @FXML
    private ImageView imgWait;
    @FXML
    private Button btnClose;
    @FXML
    private HBox waitPane;

    /*
      -----------------------
        Private attributes
      -----------------------
     */
    private MatchData data = MatchData.getInstance();
    private static final String zeroTo255 = "([01]?[0-9]{1,2}|2[0-4][0-9]|25[0-5])";
    private static final String IP_REGEXP = "^(" + zeroTo255 + "\\." + zeroTo255 + "\\." + zeroTo255 + "\\." + zeroTo255 + ")$"; //Valid IP regex
    private static final Pattern IP_PATTERN = Pattern.compile(IP_REGEXP);

    /**
     * This method is fired from FXML loader after controller is loaded
     */
    @FXML
    private void initialize(){
        showMessage("Insert new settings, and apply them clicking on Apply");
    }

    /**
     * Override superclass activate to show last IP, Port choice
     */
    @Override
    public void activate() {
        txtIP.setText(data.getIP());
        txtPort.setText(data.getPort().toString());
        super.activate(); //Then continue with default behaviour
    }

    /*
      ---------------------
        Graphical Events
      ---------------------
      Description: invoked from JavaFX runtime when the user interacts with the GUI
     */
    @FXML
    private void onBtnCancelClicked(MouseEvent event) {
        GUISwitcher.getInstance().activateDefault();
    }
    @FXML
    private void onBtnCloseClicked(MouseEvent event){
        closeWait();
    }
    @FXML
    private void onBtnApplyClicked(MouseEvent event) {
        String address = txtIP.getText();
        int port;
        //Check if IP is valid
        if (!IP_PATTERN.matcher(address).matches()){
            showAlert("Please provide a valid IP Address");
            return;
        }
        try{
            port = Integer.parseInt(txtPort.getText());
        }catch (NumberFormatException ex){
            showAlert("Please provide a valid Port");
            return;
        }
        //Save data
        data.setIP(address);
        data.setPort(port);
        GUISwitcher.getInstance().activateDefault(); //Return to splash
    }

    /*
      ---------------------------------
         Graphic manipulation
      ---------------------------------
      Description: functions to change interaction mode with user
     */

    /**
     * Shows a full-scene closeable alert
     * @param message Alert message
     */
    private void showAlert(String message){
        lblWait.setText(message);
        btnClose.setVisible(true);
        imgWait.setVisible(false);
        waitPane.setVisible(true);
    }

    /**
     * Close a full-scene alert
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
}
