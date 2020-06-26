package it.polimi.ingsw.client.gui.match_data;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Consistent board. It's the client-side copy of server-side model.
 * Only updates with approved incremental moves arriving from server via
 * UpdateBoardPacket.
 * It's used to revert an keep consistent GraphicalModel, that can become inconsistent
 * after a non-approved move/build
 */
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

    /**
     * Gets a Consistent Cell by coords
     * @param x X coord
     * @param y Y coord
     * @return Consistent cell, null if coords are outside map
     */
    public Cell getCell(int x, int y){
        if (x >= 0 && x < ROWS && y>=0 && y< COLS)
            return cells[x][y];
        return null;
    }

    /**
     * Gets a Consistent Cell by coords
     * @param point Coord point
     * @return Consistent cell, null if coord are outside map
     */
    public Cell getCell(Point point){
        return getCell(point.x,point.y);
    }

    /**
     * Gets all worker positions at last consistent state
     * @return For each worker, its last approved position
     */
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

    /**
     * Clears all consistent workers' positions
     */
    public void clearWorkersPosition(){
        for(int x=0;x<ROWS;x++){
            for(int y=0;y<COLS;y++){
                this.cells[x][y].removeWorker();
            }
        }
    }

    /**
     * Removes a worker from consistent model (after a player has lost, for example)
     * @param workerID Worker id to be removed
     */
    public void removeWorker(String workerID){
        assert workerID != null;
        for(int x=0;x<ROWS;x++){
            for(int y=0;y<COLS;y++){
                Cell cell = this.cells[x][y];
                if (workerID.equals(cell.getWorker()))
                    cell.removeWorker();
            }
        }
    }

    /**
     * Clears all data stored in cells
     */
    public void clear(){
        for(int x=0;x<ROWS;x++){
            for(int y=0;y<COLS;y++){
                this.cells[x][y].clear();
            }
        }
    }
}
