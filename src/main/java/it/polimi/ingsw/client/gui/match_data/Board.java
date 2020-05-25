package it.polimi.ingsw.client.gui.match_data;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class Board {

    private static final int ROWS = 5;
    private static final int COLS = 5;

    private Cell[][] cells;

    public Board() {
        this.cells = new Cell[ROWS][COLS];
        for(int x=0;x<ROWS;x++){
            for(int y=0;y<COLS;y++){
                this.cells[x][y] = new Cell(new Point(x,y));
            }
        }
    }

    public Cell getCell(int x, int y){
        if (x >= 0 && x < ROWS && y>=0 && y< COLS)
            return cells[x][y];
        return null;
    }
    public Cell getCell(Point point){
        return getCell(point.x,point.y);
    }

    public Map<String, Point> getWorkersPosition(){
        Map<String,Point> result = new HashMap<>();
        for(int x=0;x<ROWS;x++){
            for(int y=0;y<COLS;y++){
                Cell cell = this.cells[x][y];
                String w = cell.getWorker();
                if (w != null)
                    result.put(w,new Point(x,y));
            }
        }
        return result;
    }

    public void clearWorkersPosition(){
        for(int x=0;x<ROWS;x++){
            for(int y=0;y<COLS;y++){
                this.cells[x][y].removeWorker();
            }
        }
    }

    public void clear(){
        for(int x=0;x<ROWS;x++){
            for(int y=0;y<COLS;y++){
                this.cells[x][y].clear();
            }
        }
    }
}
