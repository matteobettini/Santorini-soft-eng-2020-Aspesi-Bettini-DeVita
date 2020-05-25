package it.polimi.ingsw.client.gui.controllers;

import it.polimi.ingsw.client.gui.enums.ControllerType;
import it.polimi.ingsw.client.gui.match_data.MatchData;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

import java.util.regex.Pattern;

public class SettingsController extends GUIController {
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

    private MatchData data = MatchData.getInstance();

    private static final String zeroTo255 = "([01]?[0-9]{1,2}|2[0-4][0-9]|25[0-5])";
    private static final String IP_REGEXP = "^(" + zeroTo255 + "\\." + zeroTo255 + "\\." + zeroTo255 + "\\." + zeroTo255 + ")$";
    private static final Pattern IP_PATTERN = Pattern.compile(IP_REGEXP);

    @FXML
    private void initialize(){
        showMessage("Insert new settings, and apply them clicking on Apply");
    }

    @Override
    public void activate() {
        txtIP.setText(data.getIP());
        txtPort.setText(data.getPort().toString());
        super.activate();
    }

    /*
        Graphic Events
     */
    @FXML
    private void onBtnCancelClicked(MouseEvent event) {
        data.changeController(ControllerType.SPLASH);
    }
    @FXML
    private void onBtnCloseClicked(MouseEvent event){
        closeWait();
    }
    @FXML
    private void onBtnApplyClicked(MouseEvent event) {
        String address = txtIP.getText();
        Integer port;
        if (!IP_PATTERN.matcher(address).matches()){
            showWait("IP address not valid", true);
            return;
        }
        try{
            port = Integer.parseInt(txtPort.getText());
        }catch (NumberFormatException ex){
            showWait("Port not valid", true);
            return;
        }
        data.setIP(address);
        data.setPort(port);
        data.changeController(ControllerType.SPLASH);
    }

    /*
        Graphic manipulation
     */
    private void showMessage(String message){
        lblMsg.setText(message);
    }
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
