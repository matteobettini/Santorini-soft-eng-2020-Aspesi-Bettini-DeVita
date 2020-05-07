package it.polimi.ingsw.CLI;

import it.polimi.ingsw.Client;
import it.polimi.ingsw.ClientImpl;
import javafx.util.Pair;

import java.awt.*;
import java.util.List;
import java.util.Map;

public class ViewModel {

    private static ViewModel viewModelSingleton = null;

    private Map<String, String> allCards;
    private Map<String, List<String >> ids;
    private Map<String, Color> playersColor;
    private Map<String, Pair<String, String>> playersCards;
    private boolean hardcore;
    private String playerName;
    private String winner;
    private String loser;
    private Board board;

    private GraphicalBoard graphicalBoard;
    private GraphicalMatchMenu graphicalMatchMenu;
    private CharStream stream;

    private Client client;

    private ViewModel(){
        this.board = new Board();
        this.stream = new CharStream(159, 50);
        this.graphicalBoard = new GraphicalBoard(stream);
        this.graphicalMatchMenu = new GraphicalMatchMenu(stream);
    }

    public Client getClient() {
        return client;
    }

    public GraphicalBoard getGraphicalBoard() {
        return graphicalBoard;
    }

    public GraphicalMatchMenu getGraphicalMatchMenu() {
        return graphicalMatchMenu;
    }

    public CharStream getStream() {
        return stream;
    }

    public static ViewModel getInstance(){
        if(viewModelSingleton == null){
            viewModelSingleton = new ViewModel();
        }

        return viewModelSingleton;
    }

    public Map<String, String> getAllCards() {
        return allCards;
    }

    public Map<String, List<String>> getIds() {
        return ids;
    }

    public Map<String, Color> getPlayersColor() {
        return playersColor;
    }

    public Map<String, Pair<String, String>> getPlayersCards() {
        return playersCards;
    }

    public boolean isHardcore() {
        return hardcore;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getWinner() {
        return winner;
    }

    public String getLoser() {
        return loser;
    }

    public Board getBoard() {
        return board;
    }

    public void setClient() {
        this.client = new ClientImpl();
    }

    public void setAllCards(Map<String, String> allCards) {
        this.allCards = allCards;
    }

    public void setIds(Map<String, List<String>> ids) {
        this.ids = ids;
    }

    public void setPlayersColor(Map<String, Color> playersColor) {
        this.playersColor = playersColor;
    }

    public void setPlayersCards(Map<String, Pair<String, String>> playersCards) {
        this.playersCards = playersCards;
    }

    public void setHardcore(boolean hardcore) {
        this.hardcore = hardcore;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public void setLoser(String loser) {
        this.loser = loser;
    }
}
