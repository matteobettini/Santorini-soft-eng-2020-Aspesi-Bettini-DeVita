package it.polimi.ingsw.client.gui.match_data;

import it.polimi.ingsw.client.communication.Client;
import it.polimi.ingsw.client.communication.ClientImpl;
import it.polimi.ingsw.common.enums.BuildingType;
import it.polimi.ingsw.common.utils.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.polimi.ingsw.common.utils.observe.Observer;
import javafx.scene.paint.Color;

/**
 * This class contains information needed for a match.
 * Every match is reset and repopulated
 */
public class MatchData {
    private static MatchData instance = null;

    private String IP;
    private Integer Port;

    private Client client;

    private String username;

    private List<String> matchPlayers;
    private boolean isMatchHardcore;
    private Map<String, List<String>> ids;
    private Map<String, Color> playersColor;
    private Map<String, Pair<String, String>> playersCards;
    private Map<BuildingType, Integer> buildings;
    private Map<BuildingType, Integer> buildingsTemp;
    private Observer<BuildingType> onBuildingNumChanged;

    private Board consistentBoard;

    private MatchData(){
        IP = "127.0.0.1";
        Port = 4567;
        buildings = new HashMap<>();
        buildingsTemp = new HashMap<>();
        consistentBoard = new Board();
        onBuildingNumChanged = null;
    }

    public static MatchData getInstance() {
        if (instance == null) instance = new MatchData();
        return instance;
    }

    /*
         Client
     */
    public void setNewClient(){
        this.client = new ClientImpl();
    }
    public Client getClient() {
        assert (client != null);
        return client;
    }

    public String getIP() {
        return IP;
    }
    public void setIP(String IP) {
        this.IP = IP;
    }
    public Integer getPort() {
        return Port;
    }
    public void setPort(Integer port) {
        Port = port;
    }

    /*
        User info
     */

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    /*
        Match info
     */
    public List<String> getMatchPlayers(){
        return matchPlayers;
    }
    public void setMatchPlayers(List<String> matchPlayers) {
        this.matchPlayers = matchPlayers;
    }

    public boolean isMatchHardcore() {
        return isMatchHardcore;
    }
    public void setMatchHardcore(boolean matchHardcore) {
        isMatchHardcore = matchHardcore;
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
    public void setBuildings(Map<BuildingType, Integer> buildings) {
        this.buildings = buildings;
    }

    public List<String> getWorkersIds(String playerID){
        assert ids != null;
        assert ids.containsKey(playerID);
        return ids.get(playerID);
    }
    public String getWorkerPlayer(String workerID){
        assert ids != null;
        for(String playerID : ids.keySet()){
            if (ids.get(playerID).contains(workerID))
                return playerID;
        }
        return null;
    }
    public Color getPlayerColor(String playerID){
        assert playersColor != null;
        assert playersColor.containsKey(playerID);
        return playersColor.get(playerID);
    }
    public Color getWorkerColor(String workerID){
        assert playersColor != null;
        //Get player from worker
        String playerID = getWorkerPlayer(workerID);
        assert playerID != null;
        //Get color from player
        return getPlayerColor(playerID);
    }
    public Pair<String,String> getPlayerCard(String playerID){
        assert playersCards != null;
        assert playersCards.containsKey(playerID);
        return playersCards.get(playerID);
    }

    public Board getConsistentBoard(){
        return consistentBoard;
    }

    /*
        Buildings counter
        -> Supply an handler to be kept updated when number changes
     */
    public void useBuildings(List<BuildingType> builds){
        clearBuildingsAsUsed();
        for(BuildingType b : builds){
            assert (buildings.containsKey(b));
            buildings.put(b, buildings.get(b) -1);
            updateBuildingListener(b);
        }
    }
    public void setBuildingAsUsed(BuildingType b){
        Integer num = 1;
        if (buildingsTemp.containsKey(b)){
            num = buildingsTemp.get(b);
            num += 1;
        }
        buildingsTemp.put(b, num);
        updateBuildingListener(b);
    }
    public void clearBuildingsAsUsed(){
        buildingsTemp.clear();
    }
    public Integer getBuildingNum(BuildingType b){
        assert buildings.containsKey(b);
        return (buildingsTemp.containsKey(b) ? buildings.get(b) - buildingsTemp.get(b) : buildings.get(b));
    }
    private void updateBuildingListener(BuildingType b){
        if (onBuildingNumChanged != null)
            onBuildingNumChanged.update(b);
    }
    public void addBuildingNumChangedListener(Observer<BuildingType> observer){
        this.onBuildingNumChanged = observer;
    }

    /**
     * Reset information stored
     */
    public void clearMatchData(){
        this.matchPlayers = null;
        this.isMatchHardcore = false;
        this.ids = null;
        this.playersColor = null;
        this.playersCards = null;
        this.buildings = new HashMap<>();
        this.buildingsTemp = new HashMap<>();
        this.onBuildingNumChanged = null;
    }
}
