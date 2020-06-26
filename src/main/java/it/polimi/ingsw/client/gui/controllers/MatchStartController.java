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

/**
 * This controller is responsible for showing initial info about the match,
 * and eventually making challenger choose start player.
 * It also shows status of match setup, with other player's actions
 */
public class MatchStartController extends GUIController {
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
    private HBox playersPane;
    @FXML
    private Label lblMsg;
    @FXML
    private ImageView imgWait;
    @FXML
    private HBox msgPane;
    @FXML
    private Label lblWait;
    @FXML
    private Label lblGameMode;

    /*
      ------------
        Constants
      ------------
     */
    private static final String AVATAR_PATH = "/client/textures/icon_player.png";
    private static final Font AVATAR_NAME_FONT = new Font("Calibri",30);


    /*
      -----------------------
        Private attributes
      -----------------------
     */
    private MatchData matchData = MatchData.getInstance();
    private ResourceScanner scanner = ResourceScanner.getInstance();
    private boolean isSelectActive = false;

    /*
      -----------------------
        External Handlers
      -----------------------
      Description: invoked from external when a correspondent packet arrives
     */

    /**
     * Called when a match is created
     * @param packet Data from server
     */
    public void handleMatchStart(PacketMatchStarted packet){
        assert (packet.getPlayers() != null);
        //Init graphics
        closeWait();
        //Save data
        matchData.setMatchPlayers(new ArrayList<>(packet.getPlayers()));
        matchData.setMatchHardcore(packet.isHardcore());
        populateWithPlayers(); //Populate avatars
        lblGameMode.setText("Game Mode: " + (packet.isHardcore() ? "Hardcore" : "Normal")); //Display game mode
        //ensureActive();
    }

    /**
     * Called when another player is choosing cards
     * @param packet Other player cards data
     */
    public void handleOthersCardChoice(PacketCardsFromServer packet){
        closeWait();
        showMessage(packet.getTo() + " is choosing the card" + (packet.getNumberToChoose() > 1 ? "s" : ""));
        ensureActive();
    }

    /**
     * Called when a start player is needed
     * @param to User who must select a start player
     * @param isRetry True if first selection was invalid
     */
    public void handleStartPlayer(String to, boolean isRetry){
        assert !isRetry; //Should not happen
        closeWait();
        if (matchData.getUsername().equals(to)){ //If i am the addressee
            //Enable select
            isSelectActive = true;
            showMessage("Select a start player, by clicking on his avatar");
        }else{
            isSelectActive = false;
            showMessage(to + " is selecting the start player");
        }
        ensureActive();
    }

    /**
     * Called when the match is fully setup
     * @param packet Data from server
     */
    public void handleSetupInfo(PacketSetup packet){
        matchData.setIds(packet.getIds());
        //Convert Colors
        Map<String, Color> colorData = new HashMap<>();
        for(String playerID : packet.getColors().keySet()){
            colorData.put(playerID, convertColor(packet.getColors().get(playerID)));
        }
        //Save data
        matchData.setPlayersColor(colorData);
        matchData.setPlayersCards(packet.getCards());
        matchData.setBuildings(packet.getBuildingsCounter());
    }

    /*
      ---------------------------------
        Auxiliary Graphical functions
      ---------------------------------
      Description: used to populate GUI with graphical elements from data
     */

    DropShadow dropShadow = new DropShadow(BlurType.GAUSSIAN, Color.web("#ff00b5"), 100.0, 0.5,0,0); //Selected effect

    /**
     * Populate players avatars in view
     */
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
            URL imagePath = scanner.getResourcePath(AVATAR_PATH);
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
        lblName.setFont(AVATAR_NAME_FONT);
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
                PacketStartPlayer packet = new PacketStartPlayer(nick); //Send choice
                matchData.getClient().send(packet);
            }
        });
        playersPane.getChildren().add(vBox); //Add avatar to pane
    }

    /**
     * Convert AWT color to JavaFX color
     * @param color AWT color
     * @return JavaFX color
     */
    private Color convertColor(java.awt.Color color){
        return Color.color((double)color.getRed()/255,(double)color.getBlue()/255,(double)color.getGreen()/255);
    }

    /*
      ---------------------
        Graphical Events
      ---------------------
      Description: invoked from JavaFX runtime when the user interacts with the GUI
     */
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

    /**
     * Show a top-center permanent message on the scene
     * @param message Message string
     */
    private void showMessage(String message){
        lblMsg.setText(message);
    }
}
