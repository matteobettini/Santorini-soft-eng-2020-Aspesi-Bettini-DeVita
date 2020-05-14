package it.polimi.ingsw.client.CLI;

import it.polimi.ingsw.client.CLI.colors.BackColor;
import it.polimi.ingsw.client.CLI.colors.ForeColor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GraphicalCardsMenu implements CharFigure {

    private CharStream stream;
    private Map<String, String> godCards;
    private List<String> chosenCards;
    private int cardsPerRow;
    private List<String> availableCards;
    private final int defaultX = 0;
    private final int defaultY = 0;

    /**
     * This constructor initializes all the godCards received from the server. GodCards are all the received god cards,
     * chosenCards are the cards of the players in the match and the availableCards are the possible cards to choose at
     * the beginning of the match.
     */
    public GraphicalCardsMenu(){
        this.godCards = new HashMap<>();
        this.chosenCards = new ArrayList<>();
        this.availableCards = new ArrayList<>();
        this.cardsPerRow = 4;
    }

    /**
     * This method sets the stream used by the GraphicalCardsMenu to print itself.
     * @param stream is the CharsStream instance.
     */
    public void setStream(CharStream stream) {
        this.stream = stream;
    }

    /**
     * This method returns the width required by the GraphicalCardsMenu to print itself on the stream.
     * @return an integer corresponding to the required width.
     */
    public int getRequiredWidth(){
        if(!chosenCards.isEmpty() || availableCards.size() != godCards.size()) return 159;
        return GraphicalCard.getWidth() * cardsPerRow + 20 - 1;
    }

    /**
     * This method returns the height required by the GraphicalCardsMenu to print itself on the stream.
     * @return an integer corresponding to the required height.
     */
    public int getRequiredHeight(){
        if(!chosenCards.isEmpty() || availableCards.size() != godCards.size()) return GraphicalCard.getHeight() + 20;
        int count = godCards.size();
        while(count % cardsPerRow != 0){
            count ++;
        }
        return (count / cardsPerRow) * GraphicalCard.getHeight() + 2 * (count / cardsPerRow + 1) + 7;
    }

    /**
     * This method sets the god cards received from the server. All of them are only displayed to the challenger.
     * Otherwise they are used to get the description for the ChosenCards or the Available ones.
     * @param godCards is the Map that associates god cards' names to their descriptions.
     */
    public void setGodCards(Map<String, String> godCards) {
        this.godCards = godCards;
        if(godCards.size() % 3 == 0) cardsPerRow = 3;
        else cardsPerRow = 4;
    }

    /**
     * This method sets the available god cards that the user can choose.
     * @param availableCards is a list containing the available cards' names.
     */
    public void setAvailableCards(List<String> availableCards) {
        this.availableCards = availableCards;
    }

    /**
     * This method sets the chose god cards in the current match.
     * @param chosenCards  is a list containing the chosen cards' names.
     */
    public void setChosenCards(List<String> chosenCards) {
        this.chosenCards = chosenCards;
    }

    /**
     * This method is used to display the GraphicalCardsMenu on the stream. It's default position is 0, 0.
     */
    @Override
    public void draw() {
        draw(defaultX,defaultY);
    }

    /**
     * This method is used to display the GraphicalCardsMenu on the stream.
     * If the cards have to be displayed to the challenger printAllCards is called.
     * If the chosen cards are set, the user can view them while choosing his worker.
     * If the available cards to choose are set they are displayed to players that are not the challenger.
     */
    @Override
    public void draw(int relX, int relY) {
        if (stream == null) return;

        if (!chosenCards.isEmpty()) {
            printAvailableOrChosen("CARDS IN GAME", relX, relY, chosenCards, BackColor.ANSI_BG_RED, 40);
            return;
        }

        if(availableCards.size() != godCards.size()){
            printAvailableOrChosen("CHOOSE A CARD", relX, relY, availableCards, BackColor.ANSI_BRIGHT_BG_GREEN, 33);
            return;
        }

        if(!godCards.isEmpty()) printAllCards(relX, relY);
    }

    /**
     * This method prints all the god cards received from th server.
     * @param relX is the X coordinate relative to the menu.
     * @param relY is the Y coordinate relative to the menu.
     */
    private void printAllCards(int relX, int relY){
        int marginForTitle = 7;
        int marginForHeading = (cardsPerRow * 10) - (cardsPerRow <= 2 ? 20 : 0);
        stream.setMessage("GOD CARDS", relX + marginForHeading + 5, relY + 2, ForeColor.ANSI_BLACK, BackColor.ANSI_BG_YELLOW);
        int countY = 0;
        int countX = 0;
        for (String godCard : godCards.keySet()) {
            GraphicalCard graphicalCard = new GraphicalCard(stream, godCard, godCards.get(godCard));
            graphicalCard.draw(relX + GraphicalCard.getWidth() * countY + cardsPerRow * (countY + 1), relY + marginForTitle + GraphicalCard.getHeight() * countX + 2 * (countX + 1));
            countY++;
            if (countY == cardsPerRow) {
                countY = 0;
                countX++;
            }
        }
    }

    /**
     * This method prints the available/chosen cards.
     * @param relX is the X coordinate relative to the menu.
     * @param relY is the Y coordinate relative to the menu.
     */
    private void printAvailableOrChosen(String message, int relX, int relY, List<String> cardsToPrint, BackColor titleColor, int startTitle){
        stream.setMessage(message, relX + startTitle, relY + 2, ForeColor.ANSI_BLACK, titleColor);
        int marginY = 10;
        if (cardsToPrint.size() == 2) {
            int marginX = 0;
            for (String card : cardsToPrint) {
                GraphicalCard graphicalCard = new GraphicalCard(stream, card, godCards.get(card));
                graphicalCard.draw(relX + 40 + marginX, relY + marginY);
                marginX += GraphicalCard.getWidth() + 20;
            }
        } else if (cardsToPrint.size() == 3) {
            int marginX = 0;
            for (String card : cardsToPrint) {
                GraphicalCard graphicalCard = new GraphicalCard(stream, card, godCards.get(card));
                graphicalCard.draw(relX + 25 + marginX, relY + marginY);
                marginX += GraphicalCard.getWidth() + 10;
            }
        }
    }
}