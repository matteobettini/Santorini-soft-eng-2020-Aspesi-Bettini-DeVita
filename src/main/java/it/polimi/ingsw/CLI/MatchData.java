package it.polimi.ingsw.CLI;

import it.polimi.ingsw.Client;
import it.polimi.ingsw.ClientImpl;
import it.polimi.ingsw.model.enums.BuildingType;
import javafx.util.Pair;

import java.awt.*;
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
    private static final int DEFAULT_MATCH_WIDTH = 159;
    private static final int DEFAULT_MATCH_HEIGHT = 50;
    public static String DEFAULT_ADDRESS = "127.0.0.1";
    public static int DEFAULT_PORT = 4567;

    private GraphicalBoard graphicalBoard;
    private CharStream stream;

    private Client client;

    private String currentActivePlayer;

    /**
     * This method instantiates and returns the MatchData singleton if not already existent, otherwise
     * it only returns the existent one.
     * @return an instance of MatchData.
     */
    public static MatchData getInstance(){
        if(matchDataSingleton == null){
            matchDataSingleton = new MatchData();
        }
        return matchDataSingleton;
    }

    /**
     * This constructor call the method reset that instantiates the new attributes.
     */
    private MatchData(){
        reset();
    }

    /**
     * This method makes the GraphicalBoard equal to the board by updating
     * workers' positions and buildings.
     */
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

    /**
     * This method instantiates the MatchData attributes needed to start a new match.
     */
    public void reset(){
        this.client = null;
        this.board = new Board();
        this.stream = new CharStream(DEFAULT_MATCH_WIDTH, DEFAULT_MATCH_HEIGHT);
        this.graphicalBoard = new GraphicalBoard(stream);
        this.buildingsCounter = new HashMap<>();
        this.loser = null;
        this.winner = null;
    }

    /**
     * This method decrements the building counter given the quantity and its BuildingType.
     * @param building is the given BuildingType.
     * @param howMany is the given quantity to decrement.
     */
    public void decrementCounter(BuildingType building,int howMany){
        int count = buildingsCounter.get(building);
        buildingsCounter.put(building, count - howMany);
    }

    /**
     * This method returns the Client instance.
     * @return a Client.
     */
    public Client getClient() {
        return client;
    }

    /**
     * Getter for the buildings counter.
     * @return a map that associates BuildingType to the current available number.
     */
    public Map<BuildingType, Integer> getBuildingsCounter() {
        return buildingsCounter;
    }

    /**
     * This method returns the Graphical Board instance.
     * @return a GraphicalBoard.
     */
    public GraphicalBoard getGraphicalBoard() {
        return graphicalBoard;
    }

    /**
     * This method returns the CharStream instance.
     * @return a CharStream.
     */
    public CharStream getStream() {
        return stream;
    }

    /**
     * This method returns the cards received from the server.
     * @return a Map that associates cards' names to their descriptions.
     */
    public Map<String, String> getAllCards() {
        return allCards;
    }

    /**
     * This method returns the ids received from the server.
     * @return a Map that associates player' ids to their List of workers' ids.
     */
    public Map<String, List<String>> getIds() {
        return ids;
    }

    /**
     * This method returns the players' colors received from the server.
     * @return a Map that associates players' ids to their colors.
     */
    public Map<String, Color> getPlayersColor() {
        return playersColor;
    }

    /**
     * This method returns the players' cards.
     * @return a Map that associates players to their cards.
     */
    public Map<String, Pair<String, String>> getPlayersCards() {
        return playersCards;
    }

    /**
     * This method returns a boolean based on the current game-mode.
     * @return true if the match is on hardcore mode, false otherwise.
     */
    public boolean isHardcore() {
        return hardcore;
    }

    /**
     * This method returns the user nickname.
     * @return a String containing the nickname.
     */
    public String getPlayerName() {
        return playerName;
    }

    /**
     * This method returns the current active player (the one who is doing the action).
     * @return a String containing the active player's nickname.
     */
    public String getCurrentActivePlayer() { return currentActivePlayer; }

    /**
     * This method returns the winner's nickname.
     * @return a String containing the winner's nickname.
     */
    public String getWinner() {
        return winner;
    }

    /**
     * This method returns the loser's nickname.
     * @return a String containing the loser's nickname.
     */
    public String getLoser() {
        return loser;
    }

    /**
     * This method returns the current instance of Board.
     * @return a Board.
     */
    public Board getBoard() {
        return board;
    }

    /**
     * This method returns the worker's number given its id.
     * @param workerID is the given worker's id.
     * @return an Integer.
     */
    public Integer getWorkerNumber(String workerID){
       for(String player : ids.keySet()){
           if(ids.get(player).contains(workerID)){
               return ids.get(player).indexOf(workerID) + 1;
           }
       }
       return null;
    }

    /**
     * This method creates and sets a new Client instance.
     */
    public void setNewClient() {
        this.client = new ClientImpl();
    }

    /**
     * This method sets the cards received from the server.
     */
    public void setAllCards(Map<String, String> allCards) {
        this.allCards = allCards;
    }

    /**
     * Setter for the buildings counter.
     */
    public void setCounter(Map<BuildingType, Integer> buildingsCounter){
        this.buildingsCounter = buildingsCounter;
    }

    /**
     * Setter for the players and workers' ids.
     */
    public void setIds(Map<String, List<String>> ids) {
        this.ids = ids;
    }

    /**
     * Setter for the players' colors.
     */
    public void setPlayersColor(Map<String, Color> playersColor) {
        this.playersColor = playersColor;
    }

    /**
     * Setter for the players' cards.
     */
    public void setPlayersCards(Map<String, Pair<String, String>> playersCards) {
        this.playersCards = playersCards;
    }

    /**
     * Setter for the game-mode.
     */
    public void setHardcore(boolean hardcore) {
        this.hardcore = hardcore;
    }

    /**
     * Setter for the user nickname.
     */
    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    /**
     * Setter for the winner's nickname.
     */
    public void setWinner(String winner) {
        this.winner = winner;
    }

    /**
     * Setter for the loser's nickname.
     */
    public void setLoser(String loser) {
        this.loser = loser;
    }

    /**
     * Setter for the current active player's nickname.
     */
    public void setCurrentActivePlayer(String currentActivePlayer) {
        this.currentActivePlayer = currentActivePlayer;
    }
}
