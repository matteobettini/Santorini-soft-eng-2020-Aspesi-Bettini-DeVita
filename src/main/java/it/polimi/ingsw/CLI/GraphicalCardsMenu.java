package it.polimi.ingsw.CLI;

import it.polimi.ingsw.CLI.enums.BackColor;
import it.polimi.ingsw.CLI.enums.ForeColor;

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

    public GraphicalCardsMenu(){
        this.godCards = new HashMap<>();
        this.chosenCards = new ArrayList<>();
        this.availableCards = new ArrayList<>();
        this.cardsPerRow = 4;
    }

    public void setStream(CharStream stream) {
        this.stream = stream;
    }

    public int getRequiredWidth(){
        if(!chosenCards.isEmpty() || availableCards.size() != godCards.size()) return 159;
        return GraphicalCard.getWidth() * cardsPerRow + 20 - 1;
    }

    public int getRequiredHeight(){
        if(!chosenCards.isEmpty() || availableCards.size() != godCards.size()) return GraphicalCard.getHeight() + 20;
        int count = godCards.size();
        while(count % cardsPerRow != 0){
            count ++;
        }
        return (count / cardsPerRow) * GraphicalCard.getHeight() + 2 * (count / cardsPerRow + 1) + 7;
    }

    public void setGodCards(Map<String, String> godCards) {
        this.godCards = godCards;
        if(godCards.size() % 3 == 0) cardsPerRow = 3;
        else cardsPerRow = 4;
    }

    public void setAvailableCards(List<String> availableCards) {
        this.availableCards = availableCards;
    }

    public void setChosenCards(List<String> chosenCards) {
        this.chosenCards = chosenCards;
    }

    @Override
    public void draw() {
        draw(0,0);
    }

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

        if(godCards.isEmpty()) return;

        int marginForTitle = 7;
        int marginForHeading = (cardsPerRow * 10) - (cardsPerRow <= 2 ? 20 : 0);
        stream.setMessage("GOD", relX + marginForHeading + 5, relY + 2, ForeColor.ANSI_BLACK, BackColor.ANSI_BG_YELLOW);
        stream.setMessage("CARDS", relX + marginForHeading + 35, relY + 2, ForeColor.ANSI_BLACK, BackColor.ANSI_BG_YELLOW);
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