package it.polimi.ingsw.CLI;

import it.polimi.ingsw.Client;
import it.polimi.ingsw.ClientImpl;
import it.polimi.ingsw.model.enums.BuildingType;
import javafx.util.Pair;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MatchData {

    private static MatchData matchDataSingleton = null;

    private Map<String, String> allCards;
    private Map<String, List<String >> ids;
    private Map<String, Color> playersColor;
    private Map<String, Pair<String, String>> playersCards;
    private boolean hardcore;
    private String playerName;
    private String winner;
    private String loser;
    private Board board;
    private Map<BuildingType, Integer> buildingsCounter;
    private final int DEFAULT_MATCH_WIDTH = 159;
    private final int DEFAULT_MATCH_HEIGHT = 50;

    private GraphicalBoard graphicalBoard;
    private CharStream stream;

    private Client client;

    private String currentActivePlayer;

    public static MatchData getInstance(){
        if(matchDataSingleton == null){
            matchDataSingleton = new MatchData();
        }

        return matchDataSingleton;
    }

    private MatchData(){
        reset();
    }

    public void makeGraphicalBoardEqualToBoard(){
        graphicalBoard = new GraphicalBoard(stream);

        for(int i = 0; i < Board.getRows(); ++i){
            for(int j = 0; j < Board.getColumns(); ++j){
                Point position = new Point(i, j);
                Cell cell = board.getCell(position);

                if(cell != null){
                    if(cell.getWorker() != null) graphicalBoard.getCell(position).setWorker(cell.getWorker());

                    for(BuildingType buildingType : cell.getBuildings()) graphicalBoard.getCell(position).addBuilding(buildingType);
                }

            }
        }
    }

    public void reset(){
        this.board = new Board();
        this.stream = new CharStream(DEFAULT_MATCH_WIDTH, DEFAULT_MATCH_HEIGHT);
        this.graphicalBoard = new GraphicalBoard(stream);
        this.buildingsCounter = new HashMap<>();
        this.loser = null;
        this.winner = null;
    }

    public void decrementCounter(BuildingType building,int howMany){
        int count = buildingsCounter.get(building);
        buildingsCounter.put(building, count - howMany);
    }

    public Map<BuildingType, Integer> getBuildingsCounter() {
        return buildingsCounter;
    }

    public Client getClient() {
        return client;
    }

    public GraphicalBoard getGraphicalBoard() {
        return graphicalBoard;
    }

    public CharStream getStream() {
        return stream;
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

    public String getCurrentActivePlayer() { return currentActivePlayer; }

    public String getWinner() {
        return winner;
    }

    public String getLoser() {
        return loser;
    }

    public Board getBoard() {
        return board;
    }

    public Integer getWorkerNumber(String workerID){
       for(String player : ids.keySet()){
           if(ids.get(player).contains(workerID)){
               return ids.get(player).indexOf(workerID) + 1;
           }
       }
       return null;
    }

    public void setClient() {
        this.client = new ClientImpl();
    }

    public void setAllCards(Map<String, String> allCards) {
        this.allCards = allCards;
    }

    public void setCounter(Map<BuildingType, Integer> buildingsCounter){
        this.buildingsCounter = buildingsCounter;
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

    public void setCurrentActivePlayer(String currentActivePlayer) { this.currentActivePlayer = currentActivePlayer; }
}
