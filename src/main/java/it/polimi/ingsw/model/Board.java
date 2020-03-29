package it.polimi.ingsw.model;

import it.polimi.ingsw.model.enums.BuildingType;

import java.awt.*;

/**
 * This class represents the Board used during a Match. It is instantiated through a constructor
 * that has as parameters the number of row and columns. It contains rows * columns Cells and the number of available
 * buildings that can be used by the Players in order to build on the Cells.
 */
public abstract class Board {


    /**
     * Getter that returns the Cell given its position on the Board.
     * @param p is the position of the Cell in coordinates x and y.
     * @return a Cell.
     */
    public abstract Cell getCell(Point p);

    /**
     * This method checks if there are available buildings of the given BuildingType.
     * @param b is the BuildingType to check.
     * @return true if there is availability, false otherwise.
     */
    public abstract boolean canUseBuilding(BuildingType b);

    /**
     * This method checks if it is possible to use a building given its type and then consumes it.
     * @param b is the BuildingType of the building to check and then use.
     * @return true if there is availability and the building is used, false otherwise.
     */
    public abstract boolean useBuilding(BuildingType b);


    /**
     * This method restocks the number of available buildings of the given BuildingType.
     * @param b is the BuildingType to restock.
     */
    public abstract void restockBuilding(BuildingType b);

    /**
     * This method performs a cloning of the Board.
     * @return the cloned Board.
     */
    @Override
    protected abstract Board clone();


}
