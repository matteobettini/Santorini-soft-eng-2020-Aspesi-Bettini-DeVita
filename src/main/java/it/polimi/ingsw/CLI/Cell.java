package it.polimi.ingsw.CLI;

import it.polimi.ingsw.model.enums.BuildingType;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Cell {
    private final Point position;
    private final List<BuildingType> buildings;
    private String worker;

    public Cell(Point pos){
        this.position = pos;
        this.buildings = new ArrayList<>();
    }

    public List<BuildingType> getBuildings() {
        return buildings;
    }

    public BuildingType getTopBuilding(){
        return buildings.isEmpty() ? null : buildings.get(buildings.size() - 1);
    }

    public int getLevel(){
        return getTopBuilding() == null ? 0 : InputUtilities.buildingTypeToChar(getTopBuilding());
    }

    public Point getPosition() {
        return position;
    }

    public String getWorker() {
        return worker;
    }

    public void setWorker(String worker) {
        this.worker = worker;
    }

    public void removeWorker() {
        this.worker = null;
    }

    public void addBuilding(BuildingType building) {
        buildings.add(building);
    }

    public void addBuildings(List<BuildingType> buildings) {
        for(BuildingType building : buildings) addBuilding(building);
    }
}
