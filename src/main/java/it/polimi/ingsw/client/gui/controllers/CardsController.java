package it.polimi.ingsw.client.gui.controllers;

import it.polimi.ingsw.client.gui.match_data.MatchData;
import it.polimi.ingsw.client.gui.utils.PopulationUtil;
import it.polimi.ingsw.common.packets.PacketCardsFromClient;
import it.polimi.ingsw.common.packets.PacketCardsFromServer;
import it.polimi.ingsw.common.utils.ResourceScanner;
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

import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CardsController extends GUIController {
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

    private MatchData matchData = MatchData.getInstance();

    private List<String> selectedCards = new LinkedList<>();
    private int cardsToChoose = 1;

    /*
        External Handlers
     */
    public void handleCardsChoice(PacketCardsFromServer packet, boolean isRetry){
        assert !isRetry; //Should not happen, for the way cards are chosen
        cardsToChoose = packet.getNumberToChoose();
        selectedCards.clear();
        populateCards(packet.getAllCards(),packet.getAvailableCards());
        showMessage("Select " + packet.getNumberToChoose() + " card" + (packet.getNumberToChoose() > 1 ? "s" : "") + " using the right click.\nUse the left click to show card's description");
        closeWait();
        ensureActive();
    }

    /*
        Graphic Events
     */
    @FXML
    private void onBtnNextClicked(MouseEvent event) {
        showWait("Sending cards to Game Server ...", false);
        PacketCardsFromClient packet = new PacketCardsFromClient(selectedCards);
        matchData.getClient().send(packet);
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
        Auxiliary Graphic functions
     */
    DropShadow dropShadow = new DropShadow(BlurType.GAUSSIAN, Color.web("#ff00b5"), 100.0, 0.5,0,0);
    private void populateCards(Map<String, String> allCards, List<String> available){
        //Clear card container
        cardsPane.getChildren().clear();
        //Sort card names
        List<String> cardNames = allCards.keySet().stream().sorted().collect(Collectors.toList());
        for(String cardName : cardNames){
            if (available.contains(cardName)){
                ImageView card = loadCard(cardName, allCards.get(cardName));
                cardsPane.getChildren().add(card); //Add to card list
            }
        }
    }

    private ImageView loadCard(String name, String description){
        ImageView card = PopulationUtil.loadCardImage(name,400,280);
        //Disable card if not available
        /*if (!isAvailable){
            card.setDisable(true);
            card.setOpacity(0.5);
        }*/
        //Set mouse handlers
        card.setOnMouseClicked((e)->{
            if (e.getButton() == MouseButton.PRIMARY){
                if (isCardSelected(name)) {
                    card.setEffect(null);
                    deselectCard(name);
                }else{
                    card.setEffect(dropShadow);
                    selectCard(name);
                }
            }else{
                showDescription(card.getImage(), description);
            }
        });
        return card;
    }
    private void selectCard(String cardName){
        selectedCards.add(cardName);
        btnNext.setVisible(selectedCards.size() == cardsToChoose);
    }
    private void deselectCard(String cardName){
        selectedCards.remove(cardName);
        btnNext.setVisible(selectedCards.size() == cardsToChoose);
    }
    private boolean isCardSelected(String cardName){
        return selectedCards.contains(cardName);
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

    private void showDescription(Image card, String description){
        descrImage.setImage(card);
        lblDescr.setText(description);
        descriptionPane.setVisible(true);
    }
    private void hideDescription(){
        descriptionPane.setVisible(false);
    }

    private void showMessage(String message){
        lblMsg.setText(message);
    }
}
