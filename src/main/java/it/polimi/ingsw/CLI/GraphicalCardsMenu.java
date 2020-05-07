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
            stream.setMessage("CARDS", relX + 40, relY + 2, ForeColor.ANSI_BLACK, BackColor.ANSI_BG_RED);
            stream.setMessage("IN", relX + 85, relY + 2, ForeColor.ANSI_BLACK, BackColor.ANSI_BG_RED);
            stream.setMessage("GAME", relX + 102, relY + 2, ForeColor.ANSI_BLACK, BackColor.ANSI_BG_RED);
            int marginX = 10;
            if (chosenCards.size() == 2) {
                int marginY = 0;
                for (String card : chosenCards) {
                    GraphicalCard graphicalCard = new GraphicalCard(stream, card, godCards.get(card));
                    graphicalCard.draw(relX + 40 + marginY, relY + marginX);
                    marginY += GraphicalCard.getWidth() + 20;
                }
            } else if (chosenCards.size() == 3) {
                int marginY = 0;
                for (String card : chosenCards) {
                    GraphicalCard graphicalCard = new GraphicalCard(stream, card, godCards.get(card));
                    graphicalCard.draw(relX + 25 + marginY, relY + marginX);
                    marginY += GraphicalCard.getWidth() + 10;
                }
            }
            return;
        }

        if(availableCards.size() != godCards.size()){
            stream.setMessage("CHOOSE", relX + 33, relY + 2, ForeColor.ANSI_BLACK, BackColor.ANSI_BRIGHT_BG_GREEN);
            stream.setMessage("A", relX + 85, relY + 2, ForeColor.ANSI_BLACK, BackColor.ANSI_BRIGHT_BG_GREEN);
            stream.setMessage("CARD", relX + 98, relY + 2, ForeColor.ANSI_BLACK, BackColor.ANSI_BRIGHT_BG_GREEN);
            int marginX = 10;
            if(availableCards.size() == 2){
                int marginY = 0;
                for(String card : availableCards) {
                    GraphicalCard graphicalCard = new GraphicalCard(stream, card, godCards.get(card));
                    graphicalCard.draw(relX + 40 + marginY, relY + marginX);
                    marginY += GraphicalCard.getWidth() + 20;
                }
            }
            else if(availableCards.size() == 3){
                int marginY = 0;
                for(String card : availableCards){
                    GraphicalCard graphicalCard = new GraphicalCard(stream, card, godCards.get(card));
                    graphicalCard.draw(relX + 25 + marginY, relY + marginX);
                    marginY += GraphicalCard.getWidth() + 10;
                }
            }
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
}