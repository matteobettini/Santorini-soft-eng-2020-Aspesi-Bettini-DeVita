package it.polimi.ingsw.model;

import it.polimi.ingsw.model.enums.BuildingType;
import it.polimi.ingsw.model.exceptions.NoBuildingSignal;
import it.polimi.ingsw.model.exceptions.NoWorkerPresentException;
import it.polimi.ingsw.model.exceptions.WorkerAlreadyPresentException;

import java.awt.*;

/**
 * This class represents a Cell of the Board
 * It contains the Buildings and it may contain a Worker
 * It is uniquely identified by its position on the Board
 * @version 1.0
 */

public abstract class Cell implements Cloneable{

    Cell(Point position){


    }

    /**
     *  This method returns the Cell position
     * @return Cell Position
     */
    public abstract Point getPosition();

    /**
     * This method builds in the Cell
     * @param building
     * @return true if it succeeded to build onto the previous building, false otherwise
     */
    public abstract boolean addBuilding(BuildingType building);

    /**
     * This method checks if it possible to build in the Cell
     * @param building
     * @return true if it is possible to build onto the previous building, false otherwise
     */
    public abstract boolean canBuild(BuildingType building);

    /**
     * This method returns the Building currently at the top
     * @return the building on the top
     * @throws NoBuildingSignal if there is no building
     */
    public abstract BuildingType getTopBuilding() throws NoBuildingSignal;

    /**
     * This method sets the Worker passed as an argument in the Cell
     * @param workerID
     * @throws WorkerAlreadyPresentException
     */
    public abstract void setWorker(String workerID) throws WorkerAlreadyPresentException;

    /**
     * The method returns the Worker placed on the Cell
     * @return the Worker ID if present, null otherwise
     */
    public abstract String getWorkerID();

    /**
     * This method removes the Worker place on the Cell
     * @throws NoWorkerPresentException if there is no Worker
     */
    public abstract void removeWorker() throws NoWorkerPresentException;

    @Override
    public abstract boolean equals(Object obj);
    /*{
        if (obj == null)
            return false;
        if (!(obj instanceof Cell)) return false;

        return ((Cell)obj).getPosition().equals(position);
    }*/

    /**
     * This method returns a clone of the Cell
     * @return the cloned Cell
     */
    @Override
    protected abstract Cell clone();
}
