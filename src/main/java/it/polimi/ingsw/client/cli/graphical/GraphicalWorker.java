package it.polimi.ingsw.client.cli.graphical;

import it.polimi.ingsw.client.cli.utilities.CharStream;
import it.polimi.ingsw.client.cli.utilities.colors.BackColor;
import it.polimi.ingsw.client.cli.utilities.OutputUtilities;

import java.awt.*;

class GraphicalWorker implements CharFigure {

    private final BackColor color;
    private final CharStream stream;
    private final int RATIO_X;
    private final int RATIO_Y;
    private final Integer number;
    private final String playerName;
    private final int marginX = 8;
    private final int marginY = 3;

    /**
     * This constructor initializes the stream used by the GraphicalWorker to print itself, its color, its lengths on
     * the X and Y axes, its number and the associated playerID.
     * @param stream is the CharStream used to print.
     * @param color is the player color associated to it.
     * @param RATIO_X is the length on the X axis.
     * @param RATIO_Y is the length on the Y axis.
     * @param number is the worker number.
     * @param playerName is the associated playerID.
     */
    public GraphicalWorker(CharStream stream, Color color, int RATIO_X, int RATIO_Y, Integer number, String playerName){
        this.stream = stream;
        this.color = OutputUtilities.fromColorToBackColor(color);
        this.RATIO_X = RATIO_X;
        this.RATIO_Y = RATIO_Y;
        this.number = number;
        this.playerName = playerName;
    }

    /**
     * This method returns the GraphicalWorker's number.
     * @return an integer.
     */
    public Integer getNumber() {
        return number;
    }

    /**
     * This method returns the GraphicalWorker's associated player.
     * @return a String containing the playerID.
     */
    public String getPlayerName() {
        return playerName;
    }

    /**
     * This method is used to display the GraphicalWorker on the stream. Since workers are always displayed
     * relatively to the GraphicalCells this method draws on the stream's default position..
     */
    @Override
    public void draw() { draw(CharStream.defaultX, CharStream.defaultY); }

    /**
     * This method is used to display the GraphicalWorker on the stream. GraphicalWorkers are always displayed
     * relatively to the GraphicalCells.
     * @param relX is the GraphicalCell's relative X coordinate.
     * @param relY is the GraphicalCell's relative Y coordinate.
     */
    @Override
    public void draw(int relX, int relY) {
        relX += marginX;
        relY += marginY;
        for(int i = 0; i <= RATIO_X; ++i){
            for(int j = 0; j <= RATIO_Y; ++j){
                if(i == 1 && j == 1) stream.addChar('*', i + relX, j + relY, color);
                if(i == 2 && j == 1) stream.addChar(playerName.charAt(0), i + relX, j + relY, color);
                if(i == 3 && j == 1) stream.addChar((char)(number + '0'), i + relX, j + relY, color);
                if(i == 4 && j == 1) stream.addChar('*', i + relX, j + relY, color);
            }
        }
    }
}