package it.polimi.ingsw.client.gui.controllers;

import it.polimi.ingsw.client.gui.match_data.MatchData;
import it.polimi.ingsw.client.gui.utils.PopulationUtil;
import it.polimi.ingsw.common.packets.PacketCardsFromClient;
import it.polimi.ingsw.common.packets.PacketCardsFromServer;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * This controller is responsible for cards choices when a match starts.
 * It's used both for the challenger's choice than for other players' choice.
 */
public class CardsController extends GUIController {
    /*
      -----------------------
        Graphical bindings
      -----------------------
    */
    @FXML
    private HBox waitPane;
    @FXML
    private Label lblDescr;
    @FXML
    private Button btnClose;
    @FXML
    private VBox descriptionPane;
    @FXML
    private TilePane cardsPane;
    @FXML
    private Button btnNext;
    @FXML
    private Label lblMsg;
    @FXML
    private ImageView descrImage;
    @FXML
    private ImageView imgWait;
    @FXML
    private HBox msgPane;
    @FXML
    private Label lblWait;

    /*
      ------------
        Constants
      ------------
     */
    private static final int CARD_WIDTH = 400;
    private static final int CARD_HEIGHT = 280;

    /*
      -----------------------
        Private attributes
      -----------------------
     */
    private MatchData matchData = MatchData.getInstance();
    private List<String> selectedCards = new LinkedList<>(); //Current selected cards
    private int cardsToChoose = 1; //Cards to be selected

    /*
      -----------------------
        External Handlers
      -----------------------
      Description: invoked from external when a correspondent packet arrives
     */

    /**
     * Handler for Card Choice Packet
     * @param packet Packet containing info to be processed
     * @param isRetry True if the previous response to this packet was invalid
     */
    public void handleCardsChoice(PacketCardsFromServer packet, boolean isRetry){
        assert !isRetry; //Should not happen, for the way cards are chosen
        //Clear previous data
        cardsToChoose = packet.getNumberToChoose();
        selectedCards.clear();
        //Display cards
        populateCards(packet.getAllCards(), packet.getAvailableCards());
        //Ensure everything is active and ready for the choice
        showMessage("Select " + packet.getNumberToChoose() + " card" + (packet.getNumberToChoose() > 1 ? "s" : "") + " using the right click.\nUse the left click to show card's description");
        closeWait();
        ensureActive(); //Show linked scene if not already active
    }

    /*
      ---------------------
        Graphical Events
      ---------------------
      Description: invoked from JavaFX runtime when the user interacts with the GUI
     */
    @FXML
    private void onBtnNextClicked(MouseEvent event) {
        //Send the response to the server. It's not enabled if the choice is invalid
        showWait("Sending cards to Game Server ...");
        PacketCardsFromClient packet = new PacketCardsFromClient(selectedCards); //Generate packet with player's choice
        matchData.getClient().send(packet); //Actually send data
    }
    @FXML
    private void onBtnHideClicked(MouseEvent event) {
        hideDescription();
    }
    @FXML
    private void onBtnCloseClicked(MouseEvent event) {
        closeWait();
    }

    /*
      ---------------------------------
        Auxiliary Graphical functions
      ---------------------------------
      Description: used to populate GUI with graphical elements from data
     */

    DropShadow dropShadow = new DropShadow(BlurType.GAUSSIAN, Color.web("#ff00b5"), 100.0, 0.5,0,0); //Card selected effect

    /**
     * Adds cards to the view, as the user can select them
     * @param allCards All cards to show
     * @param available Cards that can be chosen
     */
    private void populateCards(Map<String, String> allCards, List<String> available){
        //Clear cards container
        cardsPane.getChildren().clear();
        //Sort card names
        List<String> cardNames = allCards.keySet().stream().sorted().collect(Collectors.toList());
        for(String cardName : cardNames){
            ImageView card = loadCard(cardName, allCards.get(cardName), available.contains(cardName));
            cardsPane.getChildren().add(card); //Add to card cards container
        }
    }

    /**
     * Loads card texture and generate Graphical Node to be added to view.
     * Also adds proper listeners
     * @param name Card's name
     * @param description Card's description
     * @param isAvailable True if card can be chosen
     * @return ImageView with card's data
     */
    private ImageView loadCard(String name, String description, boolean isAvailable){
        ImageView card = PopulationUtil.loadCardImage(name,CARD_WIDTH,CARD_HEIGHT);
        //Fade card if not available
        if (!isAvailable){
            card.setOpacity(0.5);
        }
        //Set mouse handlers
        card.setOnMouseClicked((e)->{
            if (e.getButton() == MouseButton.PRIMARY && isAvailable){ //Left button to select card, if is available
                if (isCardSelected(name)) { //If already selected
                    //Cancel select
                    card.setEffect(null);
                    deselectCard(name);
                }else{
                    //Select card
                    card.setEffect(dropShadow);
                    selectCard(name);
                }
            }else if (e.getButton() == MouseButton.SECONDARY) { //Right button to show info
                showDescription(card.getImage(), description);
            }
        });
        return card;
    }

    /**
     * Mark a card as selected, compiling data that will then be sent.
     * Also make sure button confirm is active
     * @param cardName Card name
     */
    private void selectCard(String cardName){
        selectedCards.add(cardName);
        btnNext.setVisible(selectedCards.size() == cardsToChoose);
    }

    /**
     * Deselect a selected card. Adjust confirm button
     * @param cardName Card name
     */
    private void deselectCard(String cardName){
        selectedCards.remove(cardName);
        btnNext.setVisible(selectedCards.size() == cardsToChoose);
    }

    /**
     * Helper method to get if a card is selected
     * @param cardName Card name
     * @return True if is selected, false otherwise
     */
    private boolean isCardSelected(String cardName){
        return selectedCards.contains(cardName);
    }

    /*
      ---------------------------------
         Graphic manipulation
      ---------------------------------
      Description: functions to change interaction mode with user
     */

    /**
     * Shows a full-scene waiting message with the given message.
     * @param message Message string
     */
    private void showWait(String message){
        lblWait.setText(message);
        btnClose.setVisible(false);
        imgWait.setVisible(true);
        waitPane.setVisible(true);
    }

    /**
     * Hides a opened full-scene message
     */
    private void closeWait(){
        waitPane.setVisible(false);
    }

    /**
     * Shows cards description
     * @param card Card's image (parsed texture)
     * @param description Card's description
     */
    private void showDescription(Image card, String description){
        descrImage.setImage(card);
        lblDescr.setText(description);
        descriptionPane.setVisible(true);
    }

    /**
     * Hides card's description
     */
    private void hideDescription(){
        descriptionPane.setVisible(false);
    }

    /**
     * Show a top-center permanent message on the scene
     * @param message Message string
     */
    private void showMessage(String message){
        lblMsg.setText(message);
    }
}
