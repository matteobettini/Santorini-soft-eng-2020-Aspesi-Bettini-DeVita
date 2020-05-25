package it.polimi.ingsw.client.gui.controllers;

import it.polimi.ingsw.client.gui.match_data.MatchData;
import it.polimi.ingsw.common.packets.PacketCardsFromServer;
import it.polimi.ingsw.common.packets.PacketMatchStarted;
import it.polimi.ingsw.common.packets.PacketSetup;
import it.polimi.ingsw.common.packets.PacketStartPlayer;
import it.polimi.ingsw.common.utils.ResourceScanner;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.net.URL;
import java.util.*;

public class MatchStartController extends GUIController {
    @FXML
    private HBox waitPane;
    @FXML
    private Button btnClose;
    @FXML
    private HBox playersPane;
    @FXML
    private Label lblMsg;
    @FXML
    private ImageView imgWait;
    @FXML
    private HBox msgPane;
    @FXML
    private Label lblWait;

    private static final String avatarPath = "/client/textures/icon_player.png";

    private MatchData matchData = MatchData.getInstance();
    private ResourceScanner scanner = ResourceScanner.getInstance();

    private boolean isSelectActive = false;

    /*
        External Handlers
     */
    public void handleMatchStart(PacketMatchStarted packet){
        assert (packet.getPlayers() != null);
        closeWait();
        matchData.setMatchPlayers(new ArrayList<>(packet.getPlayers()));
        matchData.setMatchHardcore(packet.isHardcore());
        populateWithPlayers(); //Complete graphics
        showMessage("The match was started with " + matchData.getMatchPlayers().size() + " players in " + (matchData.isMatchHardcore() ? "hardcore" : "normal") + " mode");
        //ensureActive();
    }
    public void handleOthersCardChoice(PacketCardsFromServer packet){
        closeWait();
        showMessage(packet.getTo() + " is choosing the card" + (packet.getNumberToChoose() > 1 ? "s" : ""));
        ensureActive();
    }
    public void handleSetupInfo(PacketSetup packet){
        matchData.setIds(packet.getIds());
        //Convert Colors
        Map<String, Color> colorData = new HashMap<>();
        for(String playerID : packet.getColors().keySet()){
            colorData.put(playerID, convertColor(packet.getColors().get(playerID)));
        }
        matchData.setPlayersColor(colorData);
        matchData.setPlayersCards(packet.getCards());
        matchData.setBuildings(packet.getBuildingsCounter());
    }
    public void handleStartPlayer(String to, boolean isRetry){
        assert !isRetry;
        closeWait();
        if (matchData.getUsername().equals(to)){
            //Enable select
            isSelectActive = true;
            showMessage("Select the start player, clicking on it");
        }else{
            showMessage(to + " is selecting the start player");
        }
        ensureActive();
    }

    /*
        Auxiliary Graphic functions
     */
    DropShadow dropShadow = new DropShadow(BlurType.GAUSSIAN, Color.web("#ff00b5"), 100.0, 0.5,0,0);
    private void populateWithPlayers(){
        playersPane.getChildren().clear();
        for(String nick : matchData.getMatchPlayers()){
            addGraphicalPlayer(nick);
        }
    }
    private void addGraphicalPlayer(String nick){
        //Load graphic resource
        ImageView avatar = null;
        try {
            URL imagePath = scanner.getResourcePath(avatarPath);
            avatar = new ImageView(imagePath.toString());
            avatar.setPickOnBounds(true);
            avatar.setPreserveRatio(true);
            avatar.setFitWidth(150);
            avatar.setFitHeight(200);
        }catch (Exception ex){
            assert false;
        }
        //Create label for name
        Label lblName = new Label();
        lblName.setFont(new Font("Calibri",30));
        lblName.setText(nick);
        lblName.setTextFill(Color.WHITE);
        //Create Avatar container
        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        vBox.getChildren().add(avatar); //Add image avatar
        vBox.getChildren().add(lblName); //Add name label
        //Register mouse event handlers
        vBox.setOnMouseEntered(event -> {
            if (isSelectActive)
                vBox.setEffect(dropShadow); //Color player
        });
        vBox.setOnMouseExited(event -> {
            if (isSelectActive)
                vBox.setEffect(null); //Uncolor player
        });
        vBox.setOnMouseClicked(event -> {
            if (isSelectActive){
                isSelectActive = false;
                showWait("Sending player to Server ...",false);
                PacketStartPlayer packet = new PacketStartPlayer(nick);
                matchData.getClient().send(packet);
            }
        });
        playersPane.getChildren().add(vBox);
    }
    private Color convertColor(java.awt.Color color){
        return Color.color((double)color.getRed()/255,(double)color.getBlue()/255,(double)color.getGreen()/255);
    }

    /*
        Graphic Events
     */
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
    private void showMessage(String message){
        lblMsg.setText(message);
    }
}
