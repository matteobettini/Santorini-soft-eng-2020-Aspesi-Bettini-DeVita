package it.polimi.ingsw.model;

import it.polimi.ingsw.model.enums.BuildingType;
import it.polimi.ingsw.model.enums.LevelType;
import it.polimi.ingsw.model.lambdaStrategy.StatementCompiler;
import it.polimi.ingsw.model.turnInfo.BuildData;
import it.polimi.ingsw.model.turnInfo.MoveData;
import it.polimi.ingsw.packets.InvalidPacketException;
import it.polimi.ingsw.packets.PacketBuild;
import it.polimi.ingsw.packets.PacketMove;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This class contains all the actual instances of the Model data.
 * It is protected from the external of the package and it handles the entry points of the Model.
 */
public class InternalModel {

    private final Board board;
    private final List<Player> players;
    private Player winner;
    private final List<Player> losers;


    public InternalModel(List<String> players){
        this.players = new ArrayList<>();
        for(String p : players){
            this.players.add(new Player(p));
        }
        this.board = new Board();
        this.losers = new ArrayList<>();
    }

    /**
     * Getter that returns the Board used in the match.
     * @return an instance of Board.
     */
    public Board getBoard(){ return this.board; }

    /**
     * Getter that returns the List of subscribed Players to the match.
     * @return a List of instances of Players.
     */
    public List<Player> getPlayers(){ return this.players; }

    /**
     * Getter that returns the Player given his nickname.
     * @param playerNick is a String that contains the Player nickname.
     * @return the Player that has playerNick as nickname.
     */
    public Player getPlayerByNick(String playerNick){
        assert playerNick != null;
        for(Player p : this.players){
            if(p.getNickname().equals(playerNick)) return p;
        }
        return null;
    }

    public Player getWinner() { return this.winner; }

    public List<Player> getLosers() { return this.losers; }

    public void addLoser(Player loser){
        assert loser != null;
        losers.add(loser);
    }

    public void setWinner(Player winner){ this.winner = winner; }

    /**
     * Getter that returns the Worker given its ID.
     * @param workerID is the ID of the Worker.
     * @return the Worker that has workerID as ID.
     */
    public Worker getWorkerByID(String workerID){
        if(workerID == null) return null;
        for(Player p : this.players){
            for(Worker w : p.getWorkers()){
                if(w.getID().equals(workerID)) return w;
            }
        }
        return null;
    }

    /**
     * This is a method that converts the packetMove received through the connection
     * into an instance of MoveData.
     * @param packetMove is the packet containing the data of the move action performed by
     * the Player in his turn.
     * @return a MoveData obtained by the conversion of a packetMove.
     */
    public MoveData packetMoveToMoveData(PacketMove packetMove) throws InvalidPacketException {
        assert packetMove != null;
        
        Player p = getPlayerByNick(packetMove.getPlayerNickname());
        Worker w = getWorkerByID(packetMove.getWorkerID());
        List<Point> moves = packetMove.getMove();
        
        if(moves == null || moves.isEmpty() || p == null || w == null) throw new InvalidPacketException();

        for (Point move : moves) if (move == null) throw new InvalidPacketException();
        
        Point myPosition = w.getPosition();
        Point myFirstMovePosition = moves.get(0);
        if (!(Board.areAdjacent(myPosition, myFirstMovePosition, false) && board.getCell(myFirstMovePosition).getTopBuilding() != LevelType.DOME)){
            throw new InvalidPacketException();
        }
        
        for (int i = 0; i < moves.size() - 1; i++) {
            if(!Board.areAdjacent(moves.get(i), moves.get(i+1), false) || board.getCell(moves.get(i+1)).getTopBuilding() == LevelType.DOME) {
                throw new InvalidPacketException();
            }
        }
        
        return new MoveData(p, w, moves);
    }

    /**
     * This is a method that converts the packetBuild received through the connection
     * into an instance of BuildData.
     * @param packetBuild is the packet containing the data of the build action performed by
     * the Player in his turn.
     * @return a BuildData obtained by the conversion of a packetBuild.
     */
    public BuildData packetBuildToBuildData(PacketBuild packetBuild) throws InvalidPacketException {
        assert packetBuild != null;
        
        Player p = getPlayerByNick(packetBuild.getPlayerNickname());
        Worker w = getWorkerByID(packetBuild.getWorkerID());
        Map<Point,List<BuildingType>> builds = packetBuild.getBuilds();
        List<Point> buildsOrder = packetBuild.getDataOrder();
        
        if(buildsOrder == null || builds == null || buildsOrder.isEmpty() || builds.isEmpty() || buildsOrder.size() != builds.size()  || p == null || w == null) throw new InvalidPacketException();
        
        for(Point pos : builds.keySet()) {
            if (builds.get(pos) == null || builds.get(pos).isEmpty() || board.getCell(pos) == null)
                throw new InvalidPacketException();
            if (!Board.areAdjacent(pos, w.getPosition(), true))
                throw new InvalidPacketException();

        }

        for(Point point : buildsOrder)
            if(!builds.containsKey(point))
                throw new InvalidPacketException();


        return new BuildData(p, w, builds, buildsOrder);
    }



}
