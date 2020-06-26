package it.polimi.ingsw.client.gui.utils;

import it.polimi.ingsw.common.utils.ResourceScanner;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.net.URL;

/**
 * Helper class to read resources
 */
public class PopulationUtil {
    private static final String cardsPath = "/client/textures/gods";
    private static ResourceScanner scanner = ResourceScanner.getInstance();

    /**
     * Loads a card image from resources
     * @param cardName Card name
     * @param width Image width
     * @param height Image height
     * @return ImageView containing required image
     */
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
