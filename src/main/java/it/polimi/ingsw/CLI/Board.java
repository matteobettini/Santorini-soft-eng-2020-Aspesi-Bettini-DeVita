package it.polimi.ingsw.CLI;

import javafx.util.Pair;

import java.awt.*;
import java.util.List;
import java.util.Map;

public class Board {
    private Map<String, String> allCards;
    private Map<String, List<String >> ids;
    private Map<String, Color> playersColor;
    private Map<String, Pair<String, String>> playersCards;
    private final Cell[][] cells;
    private static final int rows = 5;
    private static final int columns = 5;
    private boolean hardcore;
    private String playerName;
    private String winner;
    private String loser;

    public Board(){
        this.cells = new Cell[rows][columns];
        for (int i = 0; i < rows; ++i){
            for(int j = 0; j < columns; ++j){
                cells[i][j] = new Cell(new Point(i, j));
            }
        }
    }

    public String getPlayerName() {
        return playerName;
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

    public Map<String, String> getAllCards() {
        return allCards;
    }

    public void resetWorkers(){
        for (int i = 0; i < rows; ++i){
            for(int j = 0; j < columns; ++j){
                cells[i][j].removeWorker();
            }
        }
    }

    public Cell getCell(Point pos) {
        if(pos == null) return null;
        if(pos.x >= 0 && pos.x < rows && pos.y >= 0 && pos.y < columns) return cells[pos.x][pos.y];
        return null;
    }

    public boolean isHardcore() {
        return hardcore;
    }

    public void setWinner(String winner) { this.winner = winner; }

    public void setLoser(String loser) { this.loser = loser; }

    public void setAllCards(Map<String, String> allCards) {
        this.allCards = allCards;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public void setIds(Map<String, List<String >>  ids) {
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
}
