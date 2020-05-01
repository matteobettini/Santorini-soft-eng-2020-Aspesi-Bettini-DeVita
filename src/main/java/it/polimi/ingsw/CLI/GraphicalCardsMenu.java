package it.polimi.ingsw.CLI;

import it.polimi.ingsw.CLI.enums.BackColor;
import it.polimi.ingsw.CLI.enums.ForeColor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GraphicalCardsMenu implements CharFigure {

    private CharStream stream;
    private Map<String, String> godCards;
    private List<String> chosenCards;

    public GraphicalCardsMenu(Map<String, String> godCards){
        this.godCards = godCards;
        this.chosenCards = new ArrayList<>();
    }

    public void setStream(CharStream stream) {
        this.stream = stream;
    }

    public int getRequiredWidth(){
        if(!chosenCards.isEmpty()) return 160;
        return GraphicalCard.getWidth() * 4 + 20;
    }

    public int getRequiredHeight(){
        if(!chosenCards.isEmpty()) return GraphicalCard.getHeight() + 20;
        int count = godCards.size();
        while(count % 4 != 0){
            count ++;
        }
        return (count / 4) * GraphicalCard.getHeight() + 2 * (count / 4 + 1) + 7;
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
        if(stream == null) return;

        if(!chosenCards.isEmpty()){
            stream.setMessage("CARDS", relX + 40, relY + 2, ForeColor.ANSI_BLACK, BackColor.ANSI_BG_RED, null);
            stream.setMessage("IN", relX + 85, relY + 2, ForeColor.ANSI_BLACK, BackColor.ANSI_BG_RED, null);
            stream.setMessage("GAME", relX + 102, relY + 2, ForeColor.ANSI_BLACK, BackColor.ANSI_BG_RED, null);
            int marginX = 10;
            if(chosenCards.size() == 2){
                int marginY = 0;
                for(String card : chosenCards) {
                    GraphicalCard graphicalCard = new GraphicalCard(stream, card, godCards.get(card));
                    graphicalCard.draw(relX + 40 + marginY, relY + marginX);
                    marginY += GraphicalCard.getWidth() + 20;
                }
            }
            else if(chosenCards.size() == 3){
                int marginY = 0;
                for(String card : chosenCards){
                    GraphicalCard graphicalCard = new GraphicalCard(stream, card, godCards.get(card));
                    graphicalCard.draw(relX + 25 + marginY, relY + marginX);
                    marginY += GraphicalCard.getWidth() + 10;
                }
            }
            return;
        }

        int marginForTitle = 7;
        stream.setMessage("GOD", relX + 45, relY + 2, ForeColor.ANSI_BLACK, BackColor.ANSI_BG_YELLOW, null);
        stream.setMessage("CARDS", relX + 75, relY + 2, ForeColor.ANSI_BLACK, BackColor.ANSI_BG_YELLOW, null);
        int countY = 0;
        int countX = 0;
        for(String godCard : godCards.keySet()){
            GraphicalCard graphicalCard = new GraphicalCard(stream, godCard, godCards.get(godCard));
            graphicalCard.draw(relX + GraphicalCard.getWidth() * countY + 4 * (countY + 1) ,relY + marginForTitle + GraphicalCard.getHeight() * countX + 2 * (countX + 1));
            countY++;
            if(countY == 4){
                countY = 0;
                countX++;
            }
        }
    }

}