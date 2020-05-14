package it.polimi.ingsw.client.CLI;

import it.polimi.ingsw.common.enums.BuildingType;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Cell {
    private final Point position;
    private final List<BuildingType> buildings;
    private String worker;

    /**
     * This is the constructor that initializes the position of the cell, its buildings to an empty list
     * and its worker to null.
     * @param pos is the position of the new Cell instance.
     */
    public Cell(Point pos){
        this.position = pos;
        this.buildings = new ArrayList<>();
        this.worker = null;
    }

    /**
     * This method returns a list buildings on the Cell.
     * @return a list of BuildingType enums.
     */
    public List<BuildingType> getBuildings() {
        return buildings;
    }

    /**
     * This method returns the last building on the Cell.
     * @return a BuildingType enum.
     */
    public BuildingType getTopBuilding(){
        return buildings.isEmpty() ? null : buildings.get(buildings.size() - 1);
    }

    /**
     * This method returns the Cell's position.
     * @return an instance of Point.
     */
    public Point getPosition() {
        return position;
    }

    /**
     * This method returns the id of the worker on the Cell, null if there isn't one.
     * @return a String corresponding to the worker's id.
     */
    public String getWorker() {
        return worker;
    }

    /**
     * This method sets the given worker on the Cell.
     * @param worker is the ID of the given worker.
     */
    public void setWorker(String worker) {
        this.worker = worker;
    }

    /**
     * This method removes the worker from the Cell if there is one.
     */
    public void removeWorker() {
        this.worker = null;
    }

    /**
     * This method adds a given BuildingType to the list of buildings on the Cell.
     * @param building is the BuildingType enum to add.
     */
    public void addBuilding(BuildingType building) {
        buildings.add(building);
    }

    /**
     * This method adds a list of given BuildingTypes to the list of buildings on the Cell.
     * @param buildings is the list of BuildingTypes to add.
     */
    public void addBuildings(List<BuildingType> buildings) {
        for(BuildingType building : buildings) addBuilding(building);
    }
}
