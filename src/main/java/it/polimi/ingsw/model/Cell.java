package it.polimi.ingsw.model;

import it.polimi.ingsw.model.enums.BuildingType;
import it.polimi.ingsw.model.enums.LevelType;
import it.polimi.ingsw.model.exceptions.DomeException;
import it.polimi.ingsw.model.exceptions.NoWorkerPresentException;
import it.polimi.ingsw.model.exceptions.WorkerAlreadyPresentException;

import java.awt.*;
import java.util.List;

/**
 * This class represents a Cell of the Board.
 * It contains the Buildings and it may contain a Worker.
 * It is uniquely identified by its position on the Board.
 * @version 1.0
 */

public abstract class Cell implements Cloneable{

    Cell(Point position){


    }

    /**
     *  This method returns the Cell position.
     * @return Cell Position.
     */
    public abstract Point getPosition();

    /**
     * This method builds on the Cell.
     * @param building is the BuildingType of the building to add.
     * @return true if it succeeded to build onto the previous building or the ground, false otherwise.
     */
    public abstract boolean addBuilding(BuildingType building);

    /**
     * This method checks if it is possible to build on the Cell.
     * @param building is the BuildingType of the building to check.
     * @return true if it is possible to build onto the previous building or the ground, false otherwise.
     */
    public abstract boolean canBuild(BuildingType building);

    /**
     * This method checks if it is possible to build on the Cell.
     * @param building is the List of BuildingType of the buildings to check.
     * @return true if it is possible to build onto the previous building or the ground, false otherwise.
     */
    public abstract boolean canBuild(List<BuildingType> building);

    /**
     * This method returns the level of the cell (i.e GROUND if there are no buildings).
     * @return the LevelType associated to the Cell.
     */
    public abstract LevelType getTopBuilding();

    /**
     * This method sets on the Cell the Worker passed as an argument.
     * @param workerID is the ID ot the Worker to set.
     * @throws WorkerAlreadyPresentException if there is already a Worker set.
     */
    public abstract void setWorker(String workerID) throws WorkerAlreadyPresentException, DomeException;

    /**
     * The method returns the Worker placed on the Cell.
     * @return the Worker ID if present, null otherwise.
     * @throws NoWorkerPresentException if there is no Worker.
     */
    public abstract String getWorkerID() throws NoWorkerPresentException;

    /**
     * This method removes the Worker placed on the Cell.
     * @throws NoWorkerPresentException if there is no Worker.
     */
    public abstract void removeWorker() throws NoWorkerPresentException;

    /**
     * This method checks if the passed object equals the Worker.
     * @param obj is the object to check.
     * @return true if obj is identical to the Worker, false otherwise.
     */
    @Override
    public abstract boolean equals(Object obj);
    /*{
        if (obj == null)
            return false;
        if (!(obj instanceof Cell)) return false;

        return ((Cell)obj).getPosition().equals(position);
    }*/

    /**
     * This method returns a clone of the Cell.
     * @return the cloned Cell
     */
    @Override
    protected abstract Cell clone();
}
