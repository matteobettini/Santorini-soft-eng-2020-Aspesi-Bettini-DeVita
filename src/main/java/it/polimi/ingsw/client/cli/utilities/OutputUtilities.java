package it.polimi.ingsw.client.cli.utilities;

import it.polimi.ingsw.client.cli.colors.BackColor;
import it.polimi.ingsw.client.cli.colors.ForeColor;
import it.polimi.ingsw.client.cli.match_data.MatchData;
import it.polimi.ingsw.client.cli.graphical.GraphicalBoard;
import it.polimi.ingsw.client.cli.graphical.GraphicalCardsMenu;
import it.polimi.ingsw.client.cli.graphical.GraphicalMatchMenu;
import it.polimi.ingsw.client.cli.graphical.GraphicalOcean;
import it.polimi.ingsw.common.enums.ActionType;
import it.polimi.ingsw.common.utils.Pair;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OutputUtilities {

    public static final String ANSI_RESET  = "\u001B[0m";

    /**
     * This method displays the actions of other players to the user.
     * @param actionType is the ActionType to display.
     * @param activePlayer is the player's id that is performing the action.
     * @param playerColor is the Color associated to the activePlayer.
     */
    public static void displayOthersActions(ActionType actionType, String activePlayer, Color playerColor){
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
            case SET_WORKERS_POSITION:
                System.out.println("\n" + fromColorToBackColor(playerColor).getCode() + activePlayer + " is setting his workers' positions..." + ANSI_RESET);
                return;
            case CHOOSE_START_PLAYER:
                System.out.println("\n" + activePlayer + " is choosing the starting player...");
                return;
            default:
                action = "action";
        }
        System.out.println("\n" + fromColorToBackColor(playerColor).getCode() + activePlayer + " is performing his " + action + "..." + ANSI_RESET);
    }

    /**
     * This method displays the actions of other players to the user.
     * @param actionType is the ActionType to display.
     * @param activePlayer is the player's id that is performing the action.
     */
    public static void displayOthersActions(ActionType actionType, String activePlayer){
        displayOthersActions(actionType, activePlayer, Color.BLACK);
    }

    /**
     * This method maps a Color into the corresponding ForeColor.
     * @param color is the Color to Map
     * @return a ForeColor
     */
    public static ForeColor fromColorToForeColor(Color color){
        if(color == null) return ForeColor.ANSI_WHITE;
        if(color.equals(Color.CYAN)) return ForeColor.ANSI_CYAN;
        else if(color.equals(Color.WHITE)) return ForeColor.ANSI_PURPLE;
        else if(color.equals(Color.ORANGE))return ForeColor.ANSI_YELLOW;
        else return ForeColor.ANSI_WHITE;
    }

    /**
     * This method maps a Color into the corresponding BackColor.
     * @param color is the Color to Map
     * @return a BackColor
     */
    public static BackColor fromColorToBackColor(Color color){
        if(color == null) return BackColor.ANSI_BG_WHITE;
        if(color.equals(Color.CYAN)) return BackColor.ANSI_BG_CYAN;
        else if(color.equals(Color.WHITE)) return BackColor.ANSI_BG_PURPLE;
        else if(color.equals(Color.ORANGE)) return BackColor.ANSI_BG_YELLOW;
        else return BackColor.ANSI_BG_WHITE;
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
