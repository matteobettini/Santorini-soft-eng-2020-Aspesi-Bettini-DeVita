package it.polimi.ingsw.client.gui.match_data;

import it.polimi.ingsw.common.enums.BuildingType;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;

public class Cell {

    private final Point position;
    private final List<BuildingType> buildings;
    private String workerID;

    public Cell(Point position) {
        this.position = position;
        this.buildings = new LinkedList<>();
        this.workerID = null;
    }

    public void addBuilding(BuildingType buildingType){
        buildings.add(buildingType);
    }
    public void addBuildings(List<BuildingType> newBuildings){
        buildings.addAll(newBuildings);
    }

    public List<BuildingType> getBuildings() {
        return new LinkedList<>(buildings);
    }

    public void setWorker(String workerID){
        this.workerID = workerID;
    }

    public String getWorker(){
        return this.workerID;
    }

    public void removeWorker(){
        this.workerID = null;
    }

    public void clear(){
        buildings.clear();
        this.workerID = null;
    }
}
