package it.polimi.ingsw.CLI;

import javafx.util.Pair;

import java.awt.*;
import java.util.List;
import java.util.Map;

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

    public Point getPoint(int x, char y){
        x--;
        y = Character.toUpperCase(y);
        int helper = Character.getNumericValue(y) - Character.getNumericValue('A');
        if( x >= 0 && x <= rows && helper >= 0 && helper <= columns)  return new Point(x, helper);
        else return null;
    }

    public void resetWorkers(){
        for (int i = 0; i < rows; ++i){
            for(int j = 0; j < columns; ++j){
                cells[i][j].removeWorker();
            }
        }
    }

    public Cell getCell(Point pos) {
        if(pos == null) return null;
        if(pos.x >= 0 && pos.x < rows && pos.y >= 0 && pos.y < columns) return cells[pos.x][pos.y];
        return null;
    }

}
