package it.polimi.ingsw.CLI;

import it.polimi.ingsw.CLI.enums.BackColor;
import it.polimi.ingsw.CLI.enums.ForeColor;

import java.awt.*;
import java.util.List;

public class GraphicalBoard implements CharFigure{
    private final int rows = 5;
    private final int columns = 5;
    private final CharStream stream;
    private final GraphicalCell[][] graphicalCells;
    private final int RATEOX = 20;
    private final int RATEOY = 8;
    private List<Point> possiblePositions;

    public GraphicalBoard(CharStream stream){
        this.stream = stream;
        this.graphicalCells = new GraphicalCell[rows][columns];
        for(int i = 0; i < rows; ++i) {
            for (int j = 0; j < columns; ++j) {
                graphicalCells[i][j] = new GraphicalCell(new Point(i, j), stream, RATEOX, RATEOY);
            }
        }
    }

    @Override
    public void draw() {
        int defaultX = 50;
        int defaultY = 5;
        draw(defaultX, defaultY);
    }

    @Override
    public void draw(int relX, int relY) {

        BackColor parentBack = BackColor.ANSI_BG_YELLOW;
        ForeColor parentFore = ForeColor.ANSI_BLACK;

        char[] coordinatesYaxis = new char[5];
        coordinatesYaxis[0] = 'A';
        coordinatesYaxis[1] = 'B';
        coordinatesYaxis[2] = 'C';
        coordinatesYaxis[3] = 'D';
        coordinatesYaxis[4] = 'E';

        int indexY = 0;

        char[] coordinatesXaxis = new char[5];
        coordinatesXaxis[0] = '1';
        coordinatesXaxis[1] = '2';
        coordinatesXaxis[2] = '3';
        coordinatesXaxis[3] = '4';
        coordinatesXaxis[4] = '5';

        int indexX = 0;

        for(int i = 0; i <= rows * RATEOX; ++i){
            for(int j = 0; j <= columns * RATEOY; ++j){
                //FIRST THE CONDITIONS FOR THE TOP'╠''╦'
                if(i == 0 && j == 0) stream.addChar('╔', i + relX, j + relY, parentFore, parentBack);
                else if(i == 0  && j % RATEOY == 0 && j != columns * RATEOY) stream.addChar('╠', i + relX, j + relY, parentFore, parentBack);
                else if(i == 0 && j == columns * RATEOY) stream.addChar('╚', i + relX, j + relY, parentFore, parentBack);
                    //CONDITIONS FOR THE BOTTOM'╣''╩' '╗' '╚'
                else if(i == rows * RATEOX && j == 0) stream.addChar('╗', i + relX, j + relY, parentFore, parentBack);
                else if(i == rows * RATEOX  && j % RATEOY == 0 && j != columns * RATEOY) stream.addChar('╣', i + relX, j + relY, parentFore, parentBack);
                else if(i == rows * RATEOX && j == columns * RATEOY) stream.addChar('╝', i + relX, j + relY, parentFore, parentBack);
                    //INTERMEDIATE'╦''╠'
                else if(i % RATEOX == 0 && i != rows * RATEOX && j == 0) stream.addChar('╦', i + relX, j + relY, parentFore,parentBack);
                else if(i % RATEOX == 0 && i != rows * RATEOX && j == columns * RATEOY) stream.addChar('╩', i + relX, j + relY, parentFore, parentBack);
                else if(i % RATEOX == 0 && j % RATEOY == 0) stream.addChar('╬', i + relX, j + relY, parentFore, parentBack);
                    //STRAIGHT LINES'═''║'
                else if(i % RATEOX == 0) stream.addChar('║', i + relX, j + relY, parentFore, parentBack);
                else if(j % RATEOY == 0) stream.addChar('═', i + relX, j + relY, parentFore, parentBack);
                else stream.addColor(i + relX, j + relY, parentFore, BackColor.ANSI_BG_GREEN);

                if(i % 10 == 0 && j == 0 && i % RATEOX != 0){
                    stream.addChar(coordinatesXaxis[indexX], i + relX, j + relY - 1, parentFore, parentBack);
                    ++indexX;
                }
                if(j % 4 == 0 && i == 0 && j % RATEOY != 0){
                    stream.addChar(coordinatesYaxis[indexY], i + relX - 1, j + relY, parentFore, parentBack);
                    ++indexY;
                }
            }
        }


        for(int i = 0; i < rows; ++i){
            for(int j = 0; j < columns; ++j){
                graphicalCells[i][j].draw(relX + i * RATEOX, relY + j * RATEOY);
            }
        }

        if(possiblePositions != null){
            highlightActions(possiblePositions, relX, relY);
            //highlightActions(notPossiblePositions, relX, relY, BackColor.ANSI_BG_YELLOW);
        }

    }

    public void resetPossibleActions(){
        this.possiblePositions = null;
    }

    /*public void setPossibleActions(List<Point> possiblePositions, String playerID , Integer workerNumber){
        MatchData matchData = MatchData.getInstance();
        Board board = matchData.getBoard();

        List<Point> adjacentPoints = board.getAdjacents(getWorkerPosition(playerID, workerNumber));

        this.notPossiblePositions = adjacentPoints.stream().filter( p -> !possiblePositions.contains(p)).collect(Collectors.toList());

        this.possiblePositions = possiblePositions;
    }*/

    public void setPossibleActions(List<Point> possiblePositions){
        this.possiblePositions = possiblePositions;
    }

    private void highlightActions(List<Point> positions, int relX, int relY){
        ForeColor foreColor = ForeColor.ANSI_BLACK;
        BackColor backColor = BackColor.ANSI_BRIGHT_BG_GREEN;
        if(positions == null) return;
        for(Point pos : positions){
            int X = pos.x * RATEOX;
            int Y = pos.y * RATEOY;
            for(int i = 1; i < RATEOX; ++ i){
                stream.addColor(i + relX + X, relY + Y + 1, foreColor, backColor);
            }
            for(int i = 1; i < RATEOY; ++ i){
                stream.addColor(relX + X + 1, i + relY + Y, foreColor, backColor);
            }
            for(int i = 1; i < RATEOX; ++ i){
                stream.addColor(i + relX + X, relY + Y + RATEOY - 1, foreColor, backColor);
            }
            for(int i = 1; i < RATEOY; ++ i){
                stream.addColor(relX + X + RATEOX - 1, i + relY + Y, foreColor, backColor);
            }
        }
    }

    public GraphicalCell getCell(Point pos){
        if (pos.x < 0 || pos.x >= rows || pos.y < 0 || pos.y >= columns) return null;
        return graphicalCells[pos.x][pos.y];
    }

    public void removeWorker(String playerID, Integer workerNumber){
        Point position = getWorkerPosition(playerID, workerNumber);
        if(position == null) return;
        graphicalCells[position.x][position.y].removeWorker();
    }

    public Point getWorkerPosition(String playerID, Integer workerNumber){
        for(int i = 0; i < rows; ++i) {
            for (int j = 0; j < columns; ++j) {
                GraphicalWorker graphicalWorker = graphicalCells[i][j].getWorker();
                if(graphicalWorker != null && graphicalWorker.getPlayerName() != null && graphicalWorker.getNumber() != null){
                    if(playerID.equals(graphicalWorker.getPlayerName()) && workerNumber.equals(graphicalWorker.getNumber())) return new Point(i, j);
                }
            }
        }
        return null;
    }

    public void resetWorkers(){
        for(int i = 0; i < rows; ++i) {
            for (int j = 0; j < columns; ++j) {
                graphicalCells[i][j].removeWorker();
            }
        }
    }

}
