package it.polimi.ingsw.CLI;

import it.polimi.ingsw.model.enums.BuildingType;

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
     * This method returns a Point given the coordinates used in the GraphicalBoard
     * to display the board' positions to the user.
     * @param x is the coordinate X that goes from 1 to #columns.
     * @param y is the coordinate Y that goes from A to the (#rows)th letter of the alphabet.
     * @return a Point containing the coordinates of the translated position on the board.
     */
    public Point getPoint(int x, char y){
        y = Character.toUpperCase(y);
        if(x <= 0 || x > rows || y < 'A' || y > 'E') return null;
        x--;
        int helper = Character.getNumericValue(y) - Character.getNumericValue('A');
        return new Point(x, helper);
    }

    /**
     * This method returns a Point given the coordinates used in the GraphicalBoard
     * to display the board' positions to the user.
     * @param point is a String containing the coordinates used to display positions in the GraphicalBoard (ex. A1, B3,...).
     * @return a Point containing the coordinates of the translated position on the board.
     */
    public Point getPoint(String point){
        return InputUtilities.POSITION_PATTERN.matcher(point).matches() ? getPoint(Character.getNumericValue(point.charAt(1)), Character.toUpperCase(point.charAt(0))) : null;
    }

    /**
     * This method returns the coordinates used to display positions on the GraphicalBoard given the real positions'
     * coordinates in the board.
     * @param position is a Point containing the coordinate X that goes from 0...columns and the coordinate Y that goes from 0...rows.
     * @return a String containing the coordinates used to display positions in the GraphicalBoard (ex. A1, B3,...).
     */
    public String getCoordinates(Point position){
        if(position.x < 0 || position.x >= rows || position.y < 0 || position.y >= columns) return null;
        String coordinates = Character.toString((char) ('A' + position.y));
        coordinates = coordinates.concat(Integer.toString(position.x + 1));
        return coordinates;
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
     * This method checks if there is a dome on the given position.
     * @param p1 is the position of the Cell to check.
     * @return true fif there is a dome, false otherwise.
     */
    public boolean thereIsDome(Point p1){
        return getCell(p1).getBuildings().contains(BuildingType.DOME); //|| (getCell(p1).getLevel() - getCell(lastPosition).getLevel()) > 1;
    }

    /**
     * This method checks if a given worker can move from his last position on the board.
     * @param worker is the String containing the worker's id.
     * @param lastPosition is the last position of the worker.
     * @return true if the worker can move to some position, false otherwise.
     */
    public boolean canMove(String worker, Point lastPosition){
        if(lastPosition == null) lastPosition = getWorkerPosition(worker);
        List<Point> adjacentPoints = getAdjacentPoints(lastPosition);
        return adjacentPoints.stream().anyMatch(p -> !thereIsDome(p));
    }

    /**
     * This method checks if a given worker can move from his position on the board.
     * @param worker is the String containing the worker's id.
     * @return true if the worker can move to some position, false otherwise.
     */
    public boolean canMove(String worker){
        return canMove(worker, null);
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

        for(int i = 1; i <= fromBuildingTypeToInt(BuildingType.DOME); ++i) possibleBuildingsInCell.add(fromIntToBuildingType(i));

        for(Point pos : adjacentPoints){
            List<BuildingType> alreadyInserted = currentBuilds.get(pos) == null ? new ArrayList<>() : currentBuilds.get(pos);
            List<BuildingType> buildingTypes = possibleBuildingsInCell.stream().filter(b -> (!getCell(pos).getBuildings().contains(b) && !alreadyInserted.contains(b))).collect(Collectors.toList());
            if(!buildingTypes.isEmpty()) possibleBuildings.put(pos, buildingTypes);
        }

        return possibleBuildings;
    }

    /**
     * This method map BuildingType to the corresponding number. (ex. DOME -> 4)
     * @param buildingType is the BuildingType enum to map.
     * @return an integer associated with the given enum.
     */
    public int fromBuildingTypeToInt(BuildingType buildingType){

        if(buildingType == null) return 0;

        switch (buildingType){
            case FIRST_FLOOR:
                return 1;
            case SECOND_FLOOR:
                return 2;
            case THIRD_FLOOR:
                return 3;
            case DOME:
                return 4;
        }

        return 0;
    }

    /**
     * This method map an integer to the corresponding BuildingType. (ex. 2 -> SECOND_FLOOR)
     * @param level is the integer enum to map.
     * @return a BuildingType enum associated with the given integer.
     */
    public BuildingType fromIntToBuildingType(int level){

        switch(level){
            case 1:
                return BuildingType.FIRST_FLOOR;
            case 2:
                return BuildingType.SECOND_FLOOR;
            case 3:
                return BuildingType.THIRD_FLOOR;
        }
        return BuildingType.DOME;
    }

}
