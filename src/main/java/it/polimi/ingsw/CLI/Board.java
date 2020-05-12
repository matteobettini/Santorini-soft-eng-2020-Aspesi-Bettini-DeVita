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

    public Board(){
        this.cells = new Cell[rows][columns];
        for (int i = 0; i < rows; ++i){
            for(int j = 0; j < columns; ++j){
                cells[i][j] = new Cell(new Point(i, j));
            }
        }
    }

    public static int getRows() {
        return rows;
    }

    public static int getColumns() {
        return columns;
    }

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

    public Point getPoint(int x, char y){
        y = Character.toUpperCase(y);
        if(x <= 0 || x > rows || y < 'A' || y > 'E') return null;
        x--;
        int helper = Character.getNumericValue(y) - Character.getNumericValue('A');
        return new Point(x, helper);
    }

    public String getCoordinates(Point position){
        if(position.x < 0 || position.x >= rows || position.y < 0 || position.y >= columns) return null;
        String coordinates = Character.toString((char) ('A' + position.y));
        coordinates = coordinates.concat(Integer.toString(position.x + 1));
        return coordinates;
    }

    public void resetWorkers(){
        for (int i = 0; i < rows; ++i){
            for(int j = 0; j < columns; ++j){
                cells[i][j].removeWorker();
            }
        }
    }

    public boolean thereIsDome(Point p1){
        return getCell(p1).getBuildings().contains(BuildingType.DOME); //|| (getCell(p1).getLevel() - getCell(lastPosition).getLevel()) > 1;
    }

    public boolean canMove(String worker, Point lastPosition){
        if(lastPosition == null) lastPosition = getWorkerPosition(worker);
        List<Point> adjacentPoints = getAdjacentPoints(lastPosition);
        return adjacentPoints.stream().anyMatch(p -> !thereIsDome(p));
    }

    public boolean canMove(String worker){
        return canMove(worker, null);
    }

    public Cell getCell(Point pos) {
        if(pos == null) return null;
        if(pos.x >= 0 && pos.x < rows && pos.y >= 0 && pos.y < columns) return cells[pos.x][pos.y];
        return null;
    }

    public boolean areAdjacent(Point p1, Point p2){
        return (p2.x == p1.x && p2.y == p1.y - 1) || (p2.x == p1.x && p2.y == p1.y + 1) || (p2.x == p1.x - 1 && p2.y == p1.y) || (p2.x == p1.x + 1 && p2.y == p1.y) || (p2.x == p1.x + 1 && p2.y == p1.y + 1) || (p2.x == p1.x + 1 && p2.y == p1.y - 1) || (p2.x == p1.x - 1 && p2.y == p1.y - 1) || (p2.x == p1.x - 1 && p2.y == p1.y + 1);
    }

    public List<Point> getAdjacentPoints(Point point){
        return getAdjacentPoints(point, false);
    }

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

    public Map<Point, List<BuildingType>> getPossibleBuildings(String worker, Map<Point, List<BuildingType>> currentBuilds){
        List<Point> adjacentPoints = getAdjacentPoints(getWorkerPosition(worker), true);

        Map<Point, List<BuildingType>> possibleBuildings = new HashMap<>();

        List<BuildingType> possibleBuildingsInCell = new ArrayList<>();

        for(int i = 1; i <= fromBuildingTypeToInt(BuildingType.DOME); ++i) possibleBuildingsInCell.add(fromIntToBuildingType(i));

        for(Point pos : adjacentPoints){
            List<BuildingType> alreadyInserted = currentBuilds.get(pos) == null ? new ArrayList<>() : currentBuilds.get(pos);
            List<BuildingType> buildingTypes = possibleBuildingsInCell.stream().filter(b -> (!getCell(pos).getBuildings().contains(b) && !alreadyInserted.contains(b))).collect(Collectors.toList());
            possibleBuildings.put(pos, buildingTypes);
        }

        return possibleBuildings;
    }

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
