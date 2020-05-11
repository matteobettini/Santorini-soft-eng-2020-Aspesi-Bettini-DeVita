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
    private final int DEFAULT_WIDTH = 159;
    private final int DEFAULT_HEIGHT = 50;

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
        this.stream = new CharStream(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        this.graphicalBoard = new GraphicalBoard(stream);
        this.buildingsCounter = new HashMap<>();
        this.loser = null;
        this.winner = null;
    }

    public void printMatch(){
        printMatch(false, false);
    }

    public void printMatch(boolean youWin, boolean gameOver){
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

    public void printCards(){
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
