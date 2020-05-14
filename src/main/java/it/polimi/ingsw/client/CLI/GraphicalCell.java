package it.polimi.ingsw.client.CLI;

import it.polimi.ingsw.client.CLI.buildings.BuildingFactory;
import it.polimi.ingsw.common.enums.BuildingType;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GraphicalCell implements CharFigure{
    private final CharStream stream;
    private final List<CharFigure> buildings;
    private GraphicalWorker worker;
    private final Point position;
    private final int RATEOX;
    private final int RATEOY;

    /**
     * This constructor initializes the GraphicalCell'stream used to print itself, its position
     * on the GraphicalBoard, its lengths on the X/Y axis.
     * @param position is the position on th GraphicalBoard.
     * @param stream is the CharStream used to print itself.
     * @param RATEOX is the length on the X axis.
     * @param RATEOY is the length on the Y axis.
     */
    public GraphicalCell(Point position, CharStream stream, int RATEOX, int RATEOY){
        this.stream = stream;
        this.buildings = new ArrayList<>();
        this.position = position;
        this.RATEOX = RATEOX;
        this.RATEOY= RATEOY;
    }

    /**
     * This method sets the GraphicalWorker on the cell given the worker's id.
     * @param workerID is the String containing the worker's id.
     */
    public void setWorker(String workerID) {

        MatchData matchData = MatchData.getInstance();

        String playerName;
        int workerNumber;

        for(String player : matchData.getIds().keySet()){
            if(matchData.getIds().get(player).contains(workerID)){
                playerName = player;
                workerNumber = matchData.getWorkerNumber(workerID);
                Color color = matchData.getPlayersColor().get(playerName);
                if(playerName != null) this.worker = new GraphicalWorker(stream, color,RATEOX / 4, RATEOY / 4, workerNumber, playerName);
            }
        }
    }

    /**
     * This methods returns the GraphicalWorker on the cell, null if there isn't one.
     * @return an instance of GraphicalWorker.
     */
    public GraphicalWorker getWorker(){
        return this.worker;
    }

    /**
     * This method removes the worker from the GraphicalCell.
     */
    public void removeWorker(){
        this.worker = null;
    }

    /**
     * This method returns the position of the GraphicalCell on th GraphicalBoard.
     * @return a Point with the X and Y coordinates.
     */
    public Point getPosition() {
        return position;
    }

    /**
     * This method add a CharFigure building instance to the list of buildings on the GraphicalCell.
     * @param buildingType is the given BuildingType to add.
     */
    public void addBuilding(BuildingType buildingType){
        buildings.add(BuildingFactory.getBuilding(stream, buildingType, RATEOX, RATEOY));
    }

    /**
     * This method add a CharFigure list of building instances to the list of buildings on the GraphicalCell.
     * @param buildings the given list of BuildingTypes to add.
     */
    public void addBuildings(List<BuildingType> buildings){
        for(BuildingType buildingType : buildings) addBuilding(buildingType);
    }

    /**
     * This method draws the GraphicalCell on the stream. Since GraphicalCells are always
     * drawn relatively to the GraphicalBoard this method is not implemented.
     */
    @Override
    public void draw() { }

    /**
     * This method draws the GraphicalCell relatively to the GraphicalBoard.
     * @param relX is the relative X coordinate.
     * @param relY  is the relative Y coordinate.
     */
    @Override
    public void draw(int relX, int relY) {
        for(CharFigure building : buildings) building.draw(relX, relY);
        if(worker != null) worker.draw(relX, relY);
    }
}
