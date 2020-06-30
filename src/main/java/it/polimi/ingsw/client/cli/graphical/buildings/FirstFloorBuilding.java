package it.polimi.ingsw.client.cli.graphical.buildings;

import it.polimi.ingsw.client.cli.graphical.CharFigure;
import it.polimi.ingsw.client.cli.utilities.CharStream;
import it.polimi.ingsw.client.cli.utilities.colors.BackColor;
import it.polimi.ingsw.client.cli.utilities.colors.ForeColor;

class FirstFloorBuilding implements CharFigure {
    private final CharStream stream;
    private final int RATIO_X;
    private final int RATIO_Y;
    private static final int marginX = 0;
    private static final int marginY = 0;
    private final BackColor backColor = BuildingFactory.floorColor;
    private final ForeColor foreColor = BuildingFactory.sidesColor;

    /**
     * /**
     * This method is the constructor for the graphical first floor that implements the CharFigure interface.
     * @param stream is the object used by the graphical first floor in order to display itself.
     * @param RATIO_X is the length on the X axis.
     * @param RATIO_Y is the length on the Y axis.
     */
    public FirstFloorBuilding(CharStream stream, int RATIO_X, int RATIO_Y){
        this.RATIO_X = RATIO_X;
        this.RATIO_Y = RATIO_Y;
        this.stream = stream;
    }

    /**
     * This method is overridden from the the CharFigure interface.
     * Since the building position on the stream is relative to the one of the graphical board this method is not used.
     */
    @Override
    public void draw() { draw(CharStream.defaultX, CharStream.defaultY); }


    /**
     * This method will set colors and characters used to display the first floor through the stream.
     * Colors of the First Floor: Grey.
     * @param relX is the position on the X axis and it is relative to a certain position on the board.
     * @param relY is the position on the Y axis and it is relative to a certain position on the board.
     */
    @Override
    public void draw(int relX, int relY) {
        relX += marginX;
        relY += marginY;
        for(int i = 1; i < RATIO_X; ++i) {
            for (int j = 1; j < RATIO_Y; ++j) {
                stream.addColor(i + relX, j + relY, foreColor, backColor);
            }
        }
    }
}