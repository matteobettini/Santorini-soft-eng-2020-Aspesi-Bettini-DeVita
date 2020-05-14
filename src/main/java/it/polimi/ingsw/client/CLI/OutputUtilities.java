package it.polimi.ingsw.client.CLI;

import it.polimi.ingsw.common.enums.ActionType;
import it.polimi.ingsw.common.utils.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OutputUtilities {

    /**
     * This method displays the actions of other players to the user.
     * @param actionType is the ActionType to display.
     * @param activePlayer is the player's id that is performing the action.
     */
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

    /**
     * This method prints the GraphicalMatchMenu and the GraphicalBoard to the user (without game over or you win boxes).
     */
    public static void printMatch(){
        printMatch(false, false);
    }

    /**
     * This method prints the GraphicalMatchMenu and the GraphicalBoard to the user.
     * @param youWin is true if the message you win has to be displayed in a GraphicalPane, false otherwise.
     * @param gameOver  is true if the message game over has to be displayed in a GraphicalPane, false otherwise.
     */
    public static void printMatch(boolean youWin, boolean gameOver){
        assert !youWin || !gameOver;
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

    /**
     * This method prints GraphicalCards corresponding to the ones in game when requested during the worker choice.
     */
    public static void printCards(){
        MatchData matchData = MatchData.getInstance();

        Map<String, Pair<String, String>> playersCards = matchData.getPlayersCards();

        Map<String, String> allCards = matchData.getAllCards();

        List<String> godCards = new ArrayList<>();

        for(String player : playersCards.keySet()){
            godCards.add(playersCards.get(player).getFirst());
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
