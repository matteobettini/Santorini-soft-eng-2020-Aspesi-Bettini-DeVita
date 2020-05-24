package it.polimi.ingsw.client.cli.match_data;

import it.polimi.ingsw.client.cli.utilities.InputUtilities;
import it.polimi.ingsw.common.enums.BuildingType;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Board {

    private final Cell[][] cells;
    private static final int rows = 5;
    private static final int columns = 5;

    /**
     * This constructor initialize the board rep by instantiating a matrix of #(rows * columns) Cell instances.
     */
    public Board(){
        this.cells = new Cell[rows][columns];
        for (int i = 0; i < rows; ++i){
            for(int j = 0; j < columns; ++j){
                cells[i][j] = new Cell(new Point(i, j));
            }
        }
    }

    /**
     * Getter for the board's rows
     * @return the number of rows.
     */
    public static int getRows() {
        return rows;
    }

    /**
     * Getter for the board's columns
     * @return the number of columns.
     */
    public static int getColumns() {
        return columns;
    }

    /**
     * This method returns the current number of workers on the board. It is used in the setting of initial workers'
     * position in order to display the GraphicalBoard if the number of workers on the board is 0.
     * @return the number of workers on the board.
     */
    public int getNumberOfWorkers(){
        int count = 0;
        for(int i = 0; i < rows; ++i){
            for(int j = 0; j < columns; ++j){
                String worker = getCell(new Point(i, j)).getWorker();
                if(worker != null) count++;
            }
        }
        return count;
    }

    /**
     * This method returns the position of the given worker on the board.
     * @param worker is the String containing the id of the requested worker.
     * @return a Point which is the  actual worker's position on the board.
     */
    public Point getWorkerPosition(String worker){
        if(worker == null) return null;
        Point workerPosition = null;
        for(int i = 0; i < rows; ++i){
            for(int j = 0; j < columns; ++j){
                Point helper = new Point(i,j);
                String w = getCell(helper).getWorker();
                if(w != null && w.equals(worker)) workerPosition = helper;
            }
        }
        return workerPosition;
    }

    /**
     * This method removes all the worker on the board. It is used when a PacketUpdateBoard is received and thus
     * the new workers' position should be set.
     */
    public void resetWorkers(){
        for (int i = 0; i < rows; ++i){
            for(int j = 0; j < columns; ++j){
                cells[i][j].removeWorker();
            }
        }
    }

    /**
     * This method returns an instance of Cell given its position on the board.
     * @param pos is the position of the Cell on the board.
     * @return an instance of Cell.
     */
    public Cell getCell(Point pos) {
        if(pos == null) return null;
        if(pos.x >= 0 && pos.x < rows && pos.y >= 0 && pos.y < columns) return cells[pos.x][pos.y];
        return null;
    }

    /**
     * This method checks if two points can be considered adjacent.
     * @param p1 is the first point to check.
     * @param p2 is the second point to check.
     * @return true if p2 and p2 are adjacent, false otherwise.
     */
    public boolean areAdjacent(Point p1, Point p2){
        return (p2.x == p1.x && p2.y == p1.y - 1) || (p2.x == p1.x && p2.y == p1.y + 1) || (p2.x == p1.x - 1 && p2.y == p1.y) || (p2.x == p1.x + 1 && p2.y == p1.y) || (p2.x == p1.x + 1 && p2.y == p1.y + 1) || (p2.x == p1.x + 1 && p2.y == p1.y - 1) || (p2.x == p1.x - 1 && p2.y == p1.y - 1) || (p2.x == p1.x - 1 && p2.y == p1.y + 1);
    }

    /**
     * This method returns all the adjacent position to the given one (excluded itself).
     * @param point is the position to check.
     * @return a list of adjacent positions to point.
     */
    public List<Point> getAdjacentPoints(Point point){
        return getAdjacentPoints(point, false);
    }

    /**
     * This method returns all the adjacent position to the given one (included itself).
     * @param point is the position to check.
     * @param considerEquals is true if the position should be considered adjacent to itself, false otherwise.
     * @return a list of adjacent positions to point.
     */
    public List<Point> getAdjacentPoints(Point point, boolean considerEquals){
        List<Point> adjacentPoints = new ArrayList<>();
        for (int i = 0; i < rows; ++i){
            for(int j = 0; j < columns; ++j){
                Point helper = new Point(i, j);
                if(areAdjacent(helper, point)) adjacentPoints.add(helper);
            }
        }
        if(considerEquals) adjacentPoints.add(point);
        return adjacentPoints;
    }

    /**
     * This method returns a map of possible buildings to build by the given worker and also excludes the ones that are already build.
     * @param worker is the String containing the workerID to check.
     * @param currentBuilds is a map of the already chosen buildings.
     * @return a map that associates adjacent positions to the worker with the possible buildings that can be built by it.
     */
    public Map<Point, List<BuildingType>> getPossibleBuildings(String worker, Map<Point, List<BuildingType>> currentBuilds){
        List<Point> adjacentPoints = getAdjacentPoints(getWorkerPosition(worker), true);

        Map<Point, List<BuildingType>> possibleBuildings = new HashMap<>();

        List<BuildingType> possibleBuildingsInCell = new ArrayList<>();

        for(int i = InputUtilities.fromBuildingTypeToInt(BuildingType.FIRST_FLOOR); i <= InputUtilities.fromBuildingTypeToInt(BuildingType.DOME); ++i) possibleBuildingsInCell.add(InputUtilities.fromIntToBuildingType(i));

        for(Point pos : adjacentPoints){
            List<BuildingType> alreadyInserted = currentBuilds.get(pos) == null ? new ArrayList<>() : currentBuilds.get(pos);
            List<BuildingType> buildingTypes = possibleBuildingsInCell.stream().filter(b -> (!getCell(pos).getBuildings().contains(b) && !alreadyInserted.contains(b))).collect(Collectors.toList());
            if(!buildingTypes.isEmpty()) possibleBuildings.put(pos, buildingTypes);
        }

        return possibleBuildings;
    }

}
