package it.polimi.ingsw.CLI;

import it.polimi.ingsw.model.enums.ActionType;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OutputUtilities {

    public static void displayOthersActions(ActionType actionType, String activePlayer){
        String action;
        switch(actionType){
            case MOVE:
                action = "move";
                break;
            case BUILD:
                action = "build";
                break;
            case MOVE_BUILD:
                action = "move or build";
                break;
            default:
                action = "action";
        }
        System.out.println("\n" + activePlayer + " is performing his " + action + "...");
    }

    public static void printMatch(){
        printMatch(false, false);
    }

    public static void printMatch(boolean youWin, boolean gameOver){
        MatchData matchData = MatchData.getInstance();
        CharStream stream = matchData.getStream();
        GraphicalBoard graphicalBoard = matchData.getGraphicalBoard();

        GraphicalOcean graphicalOcean = new GraphicalOcean(stream,stream.getWidth(), stream.getHeight());
        GraphicalMatchMenu graphicalMatchMenu = new GraphicalMatchMenu(stream);
        graphicalOcean.draw();
        graphicalBoard.draw();
        graphicalMatchMenu.setYouWin(youWin);
        graphicalMatchMenu.setGameOver(gameOver);
        graphicalMatchMenu.draw();
        stream.print(System.out);
        stream.reset();
    }

    public static void printCards(){
        MatchData matchData = MatchData.getInstance();

        Map<String, Pair<String, String>> playersCards = matchData.getPlayersCards();

        Map<String, String> allCards = matchData.getAllCards();

        List<String> godCards = new ArrayList<>();

        for(String player : playersCards.keySet()){
            godCards.add(playersCards.get(player).getKey());
        }
        GraphicalCardsMenu graphicalCardsMenu = new GraphicalCardsMenu();
        graphicalCardsMenu.setGodCards(allCards);
        graphicalCardsMenu.setChosenCards(godCards);
        CharStream stream = new CharStream(graphicalCardsMenu.getRequiredWidth(), graphicalCardsMenu.getRequiredHeight());
        graphicalCardsMenu.setStream(stream);
        graphicalCardsMenu.draw();
        stream.print(System.out);
        stream.reset();
    }


}
