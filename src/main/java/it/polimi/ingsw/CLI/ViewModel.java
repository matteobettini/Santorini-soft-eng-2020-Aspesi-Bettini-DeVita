package it.polimi.ingsw.CLI;

import it.polimi.ingsw.Client;
import it.polimi.ingsw.ClientImpl;
import it.polimi.ingsw.model.enums.BuildingType;
import javafx.util.Pair;

import java.awt.*;
import java.util.HashMap;
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
    private Map<BuildingType, Integer> buildingsCounter;

    private GraphicalBoard graphicalBoard;
    private CharStream stream;

    private Client client;

    private String currentActivePlayer;

    private ViewModel(){
        this.board = new Board();
        this.stream = new CharStream(159, 50);
        this.graphicalBoard = new GraphicalBoard(stream);
        this.buildingsCounter = new HashMap<>();
        buildingsCounter.put(BuildingType.FIRST_FLOOR, 22);
        buildingsCounter.put(BuildingType.SECOND_FLOOR, 18);
        buildingsCounter.put(BuildingType.THIRD_FLOOR, 14);
        buildingsCounter.put(BuildingType.DOME, 18);
        this.loser = null;
        this.winner = null;
    }

    public void makeGraphicalBoardEqualToBoard(){
        graphicalBoard = new GraphicalBoard(stream);

        for(int i = 0; i < Board.getRows(); ++i){
            for(int j = 0; i < Board.getColumns(); ++j){
                Point position = new Point(i, j);
                Cell cell = board.getCell(position);

                if(cell.getWorker() != null) graphicalBoard.getCell(position).setWorker(cell.getWorker());

                for(BuildingType buildingType : cell.getBuildings()) graphicalBoard.getCell(position).addBuilding(buildingType);
            }
        }
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

    public void setCurrentActivePlayer(String currentActivePlayer) { this.currentActivePlayer = currentActivePlayer; }
}
