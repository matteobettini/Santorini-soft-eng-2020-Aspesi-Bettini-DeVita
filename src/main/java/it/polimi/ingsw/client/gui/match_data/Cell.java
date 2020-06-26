package it.polimi.ingsw.client.gui.match_data;

import it.polimi.ingsw.common.enums.BuildingType;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;

/**
 * Consistent board. It's the client-side copy of server-side cell model.
 */
public class Cell {

    private final Point position;
    private final List<BuildingType> buildings;
    private String workerID;

    public Cell(Point position) {
        this.position = position;
        this.buildings = new LinkedList<>();
        this.workerID = null;
    }

    /**
     * Gets cell's position in the map
     * @return Point
     */
    public Point getPosition() {
        return new Point(position);
    }

    /**
     * Adds a building to this cell
     * @param buildingType Building
     */
    public void addBuilding(BuildingType buildingType){
        buildings.add(buildingType);
    }

    /**
     * Adds a list of building to this cell
     * @param newBuildings List of buildings
     */
    public void addBuildings(List<BuildingType> newBuildings){
        buildings.addAll(newBuildings);
    }

    /**
     * Gets all buildings of this cell
     * @return List of buildings
     */
    public List<BuildingType> getBuildings() {
        return new LinkedList<>(buildings);
    }

    /**
     * Adds a worker to this cell
     * @param workerID Worker ID
     */
    public void setWorker(String workerID){
        this.workerID = workerID;
    }

    /**
     * Gets the worker on this cell, if any
     * @return Worker ID, null if none
     */
    public String getWorker(){
        return this.workerID;
    }

    /**
     * Removes worker from this cell
     */
    public void removeWorker(){
        this.workerID = null;
    }

    /**
     * Clears buildings and stored worker of this cell
     */
    public void clear(){
        buildings.clear();
        this.workerID = null;
    }
}
