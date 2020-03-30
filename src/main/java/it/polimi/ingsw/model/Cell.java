package it.polimi.ingsw.model;

import it.polimi.ingsw.model.enums.BuildingType;
import it.polimi.ingsw.model.enums.LevelType;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * This class represents a Cell of the Board.
 * It contains the Buildings and it may contain a Worker.
 * It is uniquely identified by its position on the Board.
 * @version 1.0
 */

public class Cell{

    private String workerID;
    private final Point position;
    private final List<BuildingType> buildings;


    Cell(Point position){
        this.position = position;
        this.buildings = new ArrayList<>();
    }

    /**
     *  This method returns the Cell position.
     * @return Cell Position.
     */
    public Point getPosition(){ return this.position; }

    /**
     * This method builds on the Cell.
     * @param b is the BuildingType of the building to add.
     * @return true if it succeeded to build onto the previous building or the ground, false otherwise.
     */
    public boolean addBuilding(BuildingType b){
        assert b != null;
        if(canBuild(b)) {
            buildings.add(b);
            return true;
        }

        return false;

    }

    /**
     * This method checks if it is possible to build on the Cell.
     * @param b is the BuildingType of the building to check.
     * @return true if it is possible to build onto the previous building or the ground, false otherwise.
     */
    public boolean canBuild(BuildingType b){
        assert b!= null;
        if(workerID != null) return false;
        LevelType level = this.getTopBuilding();
        switch (b) {
            case FIRST_FLOOR:
                if(level == LevelType.GROUND){
                    return true;
                }
                break;
            case SECOND_FLOOR:
                if(level == LevelType.FIRST_FLOOR){
                    return true;
                }
                break;
            case THIRD_FLOOR:
                if(level == LevelType.SECOND_FLOOR){
                    return true;
                }
                break;
            case DOME:
                if(level != LevelType.DOME){
                    return true;
                }
            default:
                return false;
        }

        return false;
    }

    /**
     * This method checks if it is possible to build on the Cell.
     * @param b is the List of BuildingType of the buildings to check.
     * @return true if it is possible to build onto the previous building or the ground, false otherwise.
     */
    public boolean canBuild(List<BuildingType> b){
        BuildingType temp;
        assert b != null;
        if(workerID != null) return false;
        if (b.size() <= 0 || !canBuild(b.get(0))) return false;
        temp = b.get(0);
        for(int i = 1; i < b.size(); ++i) {
            if(b.get(i) == BuildingType.FIRST_FLOOR) return false;
            else if(b.get(i) == BuildingType.SECOND_FLOOR && temp != BuildingType.FIRST_FLOOR) return false;
            else if(b.get(i) == BuildingType.THIRD_FLOOR && temp != BuildingType.SECOND_FLOOR) return false;
            else if(b.get(i) == BuildingType.DOME && temp == BuildingType.DOME) return false;
            temp = b.get(i);
        }
        return true;
    }

    /**
     * This method returns the level of the cell (i.e GROUND if there are no buildings).
     * @return the LevelType associated to the Cell.
     */
    public LevelType getTopBuilding(){
        if(buildings.size() == 0) return LevelType.GROUND;
        BuildingType currentTop = buildings.get(buildings.size() - 1);
        switch(currentTop){
            case FIRST_FLOOR:
                return LevelType.FIRST_FLOOR;
            case SECOND_FLOOR:
                return LevelType.SECOND_FLOOR;
            case THIRD_FLOOR:
                return LevelType.THIRD_FLOOR;
            case DOME:
                return LevelType.DOME;
        }
        return LevelType.GROUND;
    }

    /**
     * This method sets on the Cell the Worker passed as an argument.
     * @param workerID is the ID ot the Worker to set.
     */
    public boolean setWorker(String workerID){
        assert workerID != null;
        if(this.getTopBuilding() == LevelType.DOME) return false;
        if(this.workerID != null) return false;
        this.workerID = workerID;
        return true;
    }

    /**
     * The method returns the Worker placed on the Cell.
     * @return the Worker ID if present, null otherwise.
     */
    public String getWorkerID() { return this.workerID; }

    /**
     * This method removes the Worker placed on the Cell.
     */
    public boolean removeWorker() {
        if (workerID == null) return false;
        this.workerID = null;
        return true;
    }

    /**
     * This method checks if the Cell has a Worker.
     * @return true if the is a Worker, false otherwise;
     */
    public boolean hasWorker(){ return workerID != null; }

    /**
     * This method checks if the Cell is occupied by a DOME or a Worker.
     * @return true if the Cell is occupied, false otherwise;
     */
    public boolean isOccupied(){ return getTopBuilding() == LevelType.DOME || hasWorker();}

    /**
     * This method checks if the passed object equals the Worker.
     * @param obj is the object to check.
     * @return true if obj is identical to the Worker, false otherwise.
     */
    @Override
    public boolean equals(Object obj){
        if(this == obj) return true;
        if(obj == null) return false;
        if(this.getClass() != obj.getClass()) return false;
        Cell other = (Cell)obj;
        return other.getPosition().equals(this.position);
    }

    /**
     * This method returns a clone of the Cell.
     * @return the cloned Cell
     */
    @Override
    protected Cell clone(){
        Cell clonedCell = new Cell(new Point(this.position));
        if(hasWorker()) clonedCell.setWorker(this.workerID);
        clonedCell.buildings.addAll(this.buildings);

        return clonedCell;
    }

    @Override
    public int hashCode() {
        return Objects.hash(position);
    }
}
