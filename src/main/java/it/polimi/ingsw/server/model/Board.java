package it.polimi.ingsw.server.model;

import it.polimi.ingsw.common.enums.BuildingType;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class represents the Board used during a Match. It is instantiated through a constructor
 * that has as parameters the number of row and columns. It contains rows * columns Cells and the number of available
 * buildings that can be used by the Players in order to build on the Cells.
 */
class Board {

    private final Cell[][] boardRep;
    private final Map<BuildingType, Integer> buildingsCounter;

    public static final int NUM_OF_FIRST_FLOOR = 22;
    public static final int NUM_OF_SECOND_FLOOR = 18;
    public static final int NUM_OF_THIRD_FLOOR = 14;
    public static final int NUM_OF_DOME = 18;

    public static final int ROWS = 5;
    public static final int COLUMNS = 5;

    Board(){
        this.boardRep = new Cell[ROWS][COLUMNS];
        this.buildingsCounter = new HashMap<>();

        for (int i = 0; i < ROWS; i++){
            for(int j = 0; j < COLUMNS; j++){
                Point p = new Point(i, j);
                boardRep[i][j] = new Cell(p);
            }
        }

        buildingsCounter.put(BuildingType.FIRST_FLOOR, NUM_OF_FIRST_FLOOR);
        buildingsCounter.put(BuildingType.SECOND_FLOOR, NUM_OF_SECOND_FLOOR);
        buildingsCounter.put(BuildingType.THIRD_FLOOR, NUM_OF_THIRD_FLOOR);
        buildingsCounter.put(BuildingType.DOME, NUM_OF_DOME);
    }

    /**
     * Getter that returns the Cell given its position on the Board.
     * @param p is the position of the Cell in coordinates x and y.
     * @return a Cell.
     */
    public Cell getCell(Point p){
        if(p == null) return null;
        if(p.x >= 0 && p.x < ROWS && p.y >= 0 && p.y < COLUMNS) return boardRep[p.x][p.y];
        return null;
    }

    //Could be entirely replaced by availableBuildings
    /**
     * This method checks if there are available buildings of the given BuildingType.
     * @param b is the BuildingType to check.
     * @return true if there is availability, false otherwise.
     */
    public boolean canUseBuilding(BuildingType b){
        assert b != null;
        return buildingsCounter.get(b) > 0;
    }

    /**
     * This method checks if it is possible to use a building given its type and then consumes it.
     * @param b is the BuildingType of the building to check and then use.
     * @return true if there is availability and the building is used, false otherwise.
     */
    public boolean useBuilding(BuildingType b){
        assert b!= null;
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
        assert b!= null;
        return buildingsCounter.get(b);
    }

    /**
     * This method restocks the number of available buildings of the given BuildingType.
     * @param b is the BuildingType to restock.
     */
    public boolean restockBuilding(BuildingType b, int howMany){
        assert b != null;
        if(howMany <= 0) return false;
        int current = buildingsCounter.get(b);
        switch(b){
            case FIRST_FLOOR:
                if(current + howMany > NUM_OF_FIRST_FLOOR) return false;
                buildingsCounter.put(BuildingType.FIRST_FLOOR, current + howMany);
                break;
            case SECOND_FLOOR:
                if(current + howMany > NUM_OF_SECOND_FLOOR) return false;
                buildingsCounter.put(BuildingType.SECOND_FLOOR, current + howMany);
                break;
            case THIRD_FLOOR:
                if(current + howMany > NUM_OF_THIRD_FLOOR) return false;
                buildingsCounter.put(BuildingType.THIRD_FLOOR, current + howMany);
                break;
            case DOME:
                if(current + howMany > NUM_OF_DOME) return false;
                buildingsCounter.put(BuildingType.DOME, current + howMany);
                break;
        }
        return true;
    }

    /**
     *  It's true if the two points are adjacent
     *
     * @param p1 point 1
     * @param p2 point 2
     * @param considerEquals considers p1 as adjacent to himself
     * @return true if are they adjacent
     */
    public static boolean areAdjacent(Point p1, Point p2,boolean considerEquals){
        assert (p1 != null && p2 != null);
        if(considerEquals && p1.equals(p2))
            return true;
        return (p2.x == p1.x && p2.y == p1.y - 1) || (p2.x == p1.x && p2.y == p1.y + 1) || (p2.x == p1.x - 1 && p2.y == p1.y) || (p2.x == p1.x + 1 && p2.y == p1.y) || (p2.x == p1.x + 1 && p2.y == p1.y + 1) || (p2.x == p1.x + 1 && p2.y == p1.y - 1) || (p2.x == p1.x - 1 && p2.y == p1.y - 1) || (p2.x == p1.x - 1 && p2.y == p1.y + 1);
    }

    /**
     * This methods checks whether a given point resides on the perimeter of the board
     * @param point the point to evaluate
     * @return true if it is on the perimeter, false otherwise
     */
    public static boolean isOnPerimeter(Point point){
        assert point != null;
        if(point.x == 0 || point.x == ROWS-1){
            return point.y >= 0 && point.y < COLUMNS;
        } else if(point.y == 0 || point.y == COLUMNS-1) {
            return point.x >= 0 && point.x < ROWS;
        }
        return false;
    }

    /**
     * This method returns a list of level deltas among different move steps, including start position
     * @param moves List of move points, not including start position
     * @param startPosition Point of start for the movement
     * @return List of level deltas for this movement
     */
    public List<Integer> getMoveDeltas(List<Point> moves, Point startPosition){
        assert (moves != null && moves.size()>0 && startPosition != null);

        List<Integer> result = new ArrayList<>();

        result.add(getCell(moves.get(0)).getHeight() - getCell(startPosition).getHeight());

        for(int i = 0; i< moves.size()-1; i++){
            int differenceInHeight = (getCell(moves.get(i+1)).getHeight() - getCell(moves.get(i)).getHeight());
            result.add(differenceInHeight);
        }

        return result;
    }

    /**
     * Getter for the available buildings.
     * @return a Map containing the number of available buildings for each type of building.
     */
    public Map<BuildingType, Integer> getBuildingsCounter() {
        Map<BuildingType, Integer> helper = new HashMap<>();
        for(BuildingType buildingType : buildingsCounter.keySet()){
            helper.put(buildingType, buildingsCounter.get(buildingType));
        }
        return helper;
    }

    /**
     * This method checks if the passed object equals the Board.
     * @param obj is the object to check.
     * @return true if obj has all the Cells equal to the ones of this and the buildingsCounters have the same keys and values,
     * false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(obj == null) return false;
        if(this.getClass() != obj.getClass()) return false;
        Board other = (Board)obj;
        for (int i = 0; i < ROWS; i++){
            for(int j = 0; j < COLUMNS; j++){
                if(!other.boardRep[i][j].equals(this.boardRep[i][j])) return false;
            }
        }

        if(!this.buildingsCounter.keySet().equals(other.buildingsCounter.keySet())) return false;
        for(BuildingType building : this.buildingsCounter.keySet()){
            if(!other.buildingsCounter.get(building).equals(this.buildingsCounter.get(building))) return false;
        }


        return true;
    }


    /**
     * This method performs a cloning of the Board.
     * @return the cloned Board.
     */
    @Override
    protected Board clone(){
        Board clonedBoard = new Board();
        for (int i = 0; i < ROWS; i++){
            for(int j = 0; j < COLUMNS; j++){
                clonedBoard.boardRep[i][j] = this.boardRep[i][j].clone();
            }
        }
        for(BuildingType building : this.buildingsCounter.keySet()){
            clonedBoard.buildingsCounter.put(building, this.buildingsCounter.get(building));
        }

        return clonedBoard;
    }


}
