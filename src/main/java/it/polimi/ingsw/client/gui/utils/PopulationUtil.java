package it.polimi.ingsw.client.gui.utils;

import it.polimi.ingsw.common.utils.ResourceScanner;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.net.URL;

public class PopulationUtil {
    private static final String cardsPath = "/client/textures/gods";
    private static ResourceScanner scanner = ResourceScanner.getInstance();
    public static ImageView loadCardImage(String cardName, int width, int height){
        ImageView card = new ImageView();
        card.setFitWidth(width);
        card.setFitHeight(height);
        card.setPreserveRatio(true);
        card.setSmooth(true);
        URL cardPath = scanner.getResourcePath(cardsPath + "/" + cardName.toLowerCase() + ".png");
        assert cardPath != null;
        card.setImage(new Image(cardPath.toString()));
        return card;
    }
}
