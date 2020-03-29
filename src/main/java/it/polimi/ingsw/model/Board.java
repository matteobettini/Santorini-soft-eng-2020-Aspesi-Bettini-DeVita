package it.polimi.ingsw.model;

import it.polimi.ingsw.model.enums.BuildingType;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * This class represents the Board used during a Match. It is instantiated through a constructor
 * that has as parameters the number of row and columns. It contains rows * columns Cells and the number of available
 * buildings that can be used by the Players in order to build on the Cells.
 */
public class Board {

    private Cell[][] boardRep;
    private Map<BuildingType, Integer> buildingsCounter;

    Board(){
        this.boardRep = new Cell[5][5];
        this.buildingsCounter = new HashMap<>();

        for (int i = 0; i < 5; i++){
            for(int j = 0; j < 5; j++){
                Point p = new Point(i, j);
                boardRep[i][j] = new Cell(p);
            }
        }

        buildingsCounter.put(BuildingType.FIRST_FLOOR, 22);
        buildingsCounter.put(BuildingType.SECOND_FLOOR, 18);
        buildingsCounter.put(BuildingType.THIRD_FLOOR, 14);
        buildingsCounter.put(BuildingType.DOME, 18);
    }

    /**
     * Getter that returns the Cell given its position on the Board.
     * @param p is the position of the Cell in coordinates x and y.
     * @return a Cell.
     */
    public Cell getCell(Point p){ return boardRep[(int) p.getX()][(int) p.getY()];}

    //Could be entirely replaced by availableBuildings
    /**
     * This method checks if there are available buildings of the given BuildingType.
     * @param b is the BuildingType to check.
     * @return true if there is availability, false otherwise.
     */
    public boolean canUseBuilding(BuildingType b){
        return buildingsCounter.get(b) > 0;
    }

    /**
     * This method checks if it is possible to use a building given its type and then consumes it.
     * @param b is the BuildingType of the building to check and then use.
     * @return true if there is availability and the building is used, false otherwise.
     */
    public boolean useBuilding(BuildingType b){
        int currentBuilding = buildingsCounter.get(b);
        if(currentBuilding == 0) return false;
        buildingsCounter.put(b, currentBuilding - 1);
        return true;
    }

    /**
     * This method returns the number of available buildings provided their type.
     * @param b is the BuildingType to check.
     * @return an int representing the number of available buildings of the BuildingType b.
     */
    public int availableBuildings(BuildingType b){
        return buildingsCounter.get(b);
    }

    /**
     * This method restocks the number of available buildings of the given BuildingType.
     * @param b is the BuildingType to restock.
     */
    public void restockBuilding(BuildingType b){
        switch(b){
            case FIRST_FLOOR:
                buildingsCounter.put(BuildingType.FIRST_FLOOR, 22);
                break;
            case SECOND_FLOOR:
                buildingsCounter.put(BuildingType.SECOND_FLOOR, 18);
                break;
            case THIRD_FLOOR:
                buildingsCounter.put(BuildingType.THIRD_FLOOR, 14);
                break;
            case DOME:
                buildingsCounter.put(BuildingType.DOME, 18);
                break;
        }
    }

    /**
     * This method performs a cloning of the Board.
     * @return the cloned Board.
     */
    @Override
    protected Board clone(){
        Board clonedBoard = new Board();
        for (int i = 0; i < 5; i++){
            for(int j = 0; j < 5; j++){
                clonedBoard.boardRep[i][j] = this.boardRep[i][j].clone();
            }
        }
        clonedBoard.buildingsCounter.put(BuildingType.FIRST_FLOOR, this.buildingsCounter.get(BuildingType.FIRST_FLOOR));
        clonedBoard.buildingsCounter.put(BuildingType.SECOND_FLOOR, this.buildingsCounter.get(BuildingType.SECOND_FLOOR));
        clonedBoard.buildingsCounter.put(BuildingType.THIRD_FLOOR, this.buildingsCounter.get(BuildingType.THIRD_FLOOR));
        clonedBoard.buildingsCounter.put(BuildingType.DOME, this.buildingsCounter.get(BuildingType.DOME));

        return clonedBoard;
    }


}
