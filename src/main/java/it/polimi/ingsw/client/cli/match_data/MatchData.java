package it.polimi.ingsw.client.cli.match_data;

import it.polimi.ingsw.client.cli.graphical.GraphicalBoard;
import it.polimi.ingsw.client.cli.utilities.CharStream;
import it.polimi.ingsw.client.communication.Client;
import it.polimi.ingsw.client.communication.ClientImpl;
import it.polimi.ingsw.common.enums.BuildingType;
import it.polimi.ingsw.common.utils.Pair;

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
    private List<String> losers;
    private Board board;
    private Map<BuildingType, Integer> buildingsCounter;
    private Map<BuildingType, Integer> buildingsTempCounter;
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
     * Also temporary buildings counter is restored to the last backup.
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

        //RESTORE TEMPORARY BUILDINGS
        buildingsTempCounter.clear();
        for(BuildingType buildingType : buildingsCounter.keySet()){
            this.buildingsTempCounter.put(buildingType, buildingsCounter.get(buildingType));
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
        this.buildingsTempCounter = new HashMap<>();
        this.losers = new ArrayList<>();
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
     * This method decrements by one the building counter given and its BuildingType.
     * @param building is the given BuildingType.
     */
    public void decrementCounter(BuildingType building){
        decrementCounter(building, 1);
    }

    /**
     * This method decrements the temporary building counter given the quantity and its BuildingType.
     * @param building is the given BuildingType.
     * @param howMany is the given quantity to decrement.
     */
    public void decrementTempCounter(BuildingType building, int howMany){
        int count = buildingsTempCounter.get(building);
        buildingsTempCounter.put(building, count - howMany);
    }

    /**
     * This method decrements by one the temporary building counter given its BuildingType.
     * @param building is the given BuildingType.
     */
    public void decrementTempCounter(BuildingType building){
       decrementTempCounter(building, 1);
    }

    /**
     * This method returns the Client instance.
     * @return a Client.
     */
    public Client getClient() {
        return client;
    }

    /**
     * Getter for the temporary buildings counter.
     * @return a map that associates BuildingType to the current available number (not the confirmed number after the board updating).
     */
    public Map<BuildingType, Integer> getBuildingsTempCounter() {
        Map<BuildingType, Integer> buildingsCounter = new HashMap<>();
        for(BuildingType building : buildingsTempCounter.keySet()) buildingsCounter.put(building, buildingsTempCounter.get(building));
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
        Map<String, String> allCards = new HashMap<>();
        for(String card : this.allCards.keySet()) allCards.put(card, this.allCards.get(card));
        return allCards;
    }

    /**
     * This method returns the ids received from the server.
     * @return a Map that associates player' ids to their List of workers' ids.
     */
    public Map<String, List<String>> getIds() {
        Map<String, List<String>> ids = new HashMap<>();
        for(String player : this.ids.keySet()){
            ids.put(player, new ArrayList<>(this.ids.get(player)));
        }
        return ids;
    }

    /**
     * This method returns the players' colors received from the server.
     * @return a Map that associates players' ids to their colors.
     */
    public Map<String, Color> getPlayersColor() {
        Map<String, Color> playersColor = new HashMap<>();
        for(String player : this.playersColor.keySet()){
            playersColor.put(player, new Color(this.playersColor.get(player).getRGB()));
        }
        return playersColor;
    }

    /**
     * This method returns the players' cards.
     * @return a Map that associates players to their cards.
     */
    public Map<String, Pair<String, String>> getPlayersCards() {
        Map<String, Pair<String, String>> playersCards = new HashMap<>();
        for(String player : this.playersCards.keySet()){
            Pair<String, String> card = new Pair<>(this.playersCards.get(player).getFirst(), this.playersCards.get(player).getSecond());
            playersCards.put(player, card);
        }
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
    public List<String> getLosers() {
        return losers;
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
        for(BuildingType buildingType : buildingsCounter.keySet()){
            this.buildingsTempCounter.put(buildingType, buildingsCounter.get(buildingType));
        }
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
    public void addLoser(String loser) {
        this.losers.add(loser);
    }

    /**
     * Setter for the current active player's nickname.
     */
    public void setCurrentActivePlayer(String currentActivePlayer) {
        this.currentActivePlayer = currentActivePlayer;
    }
}
