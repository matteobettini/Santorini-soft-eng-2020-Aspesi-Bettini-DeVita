package it.polimi.ingsw.CLI;

import it.polimi.ingsw.CLI.enums.BackColor;
import it.polimi.ingsw.CLI.enums.ForeColor;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GraphicalBoard implements CharFigure{
    private final int rows = 5;
    private final int columns = 5;
    private final CharStream stream;
    private final GraphicalCell[][] graphicalCells;
    private final int RATEOX = 20;
    private final int RATEOY = 8;
    private List<Point> possiblePositions;
    private List<Point> notPossiblePositions;

    public GraphicalBoard(CharStream stream){
        this.stream = stream;
        this.graphicalCells = new GraphicalCell[5][5];
        for(int i = 0; i < rows; ++i) {
            for (int j = 0; j < columns; ++j) {
                graphicalCells[i][j] = new GraphicalCell(new Point(i, j), stream, RATEOX, RATEOY);
            }
        }
    }

    @Override
    public void draw() {
        draw(50,5);
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

        if(possiblePositions != null) possibleActions(possiblePositions, relX, relY);
        if(notPossiblePositions != null) notPossibleActions(notPossiblePositions, relX, relY);

        for(int i = 0; i < rows; ++i){
            for(int j = 0; j < columns; ++j){
                graphicalCells[i][j].draw(relX + i * RATEOX, relY + j * RATEOY);
            }
        }

    }

    public void setPossibleActions(List<Point> possiblePositions){
        this.possiblePositions = possiblePositions;
    }

    public void setNotPossibleActions(List<Point> notPossiblePositions){
        this.notPossiblePositions = notPossiblePositions;
    }

    private void possibleActions(List<Point> possiblePositions, int relX, int relY){
        ForeColor parentFore = ForeColor.ANSI_BLACK;
        BackColor parentBack = BackColor.ANSI_BRIGHT_BG_GREEN;
        for(Point pos : possiblePositions){
            int X = pos.x * RATEOX;
            int Y = pos.y * RATEOY;
            for(int i = 0; i <= RATEOX; ++ i){
                stream.addColor(i + relX + X, relY + Y, parentFore, parentBack);
            }
            for(int i = 0; i <= RATEOY; ++ i){
                stream.addColor(relX + X, i + relY + Y, parentFore, parentBack);
            }
            for(int i = 0; i <= RATEOX; ++ i){
                stream.addColor(i + relX + X, relY + Y + RATEOY, parentFore, parentBack);
            }
            for(int i = 0; i <= RATEOY; ++ i){
                stream.addColor(relX + X + RATEOX, i + relY + Y, parentFore, parentBack);
            }
        }
    }

    private void notPossibleActions(List<Point> possiblePositions, int relX, int relY){
        ForeColor parentFore = ForeColor.ANSI_BLACK;
        BackColor parentBack = BackColor.ANSI_BRIGHT_BG_RED;
        for(Point pos : possiblePositions){
            int X = pos.x * RATEOX;
            int Y = pos.y * RATEOY;
            for(int i = 0; i <= RATEOX; ++ i){
                stream.addColor(i + relX + X, relY + Y, parentFore, parentBack);
            }
            for(int i = 0; i <= RATEOY; ++ i){
                stream.addColor(relX + X, i + relY + Y, parentFore, parentBack);
            }
            for(int i = 0; i <= RATEOX; ++ i){
                stream.addColor(i + relX + X, relY + Y + RATEOY, parentFore, parentBack);
            }
            for(int i = 0; i <= RATEOY; ++ i){
                stream.addColor(relX + X + RATEOX, i + relY + Y, parentFore, parentBack);
            }
        }
    }

    public GraphicalCell getCell(Point pos){
        if (pos.x < 0 || pos.x >= 5 || pos.y < 0 || pos.y >= 5) return null;
        return graphicalCells[pos.x][pos.y];
    }


}
