package it.polimi.ingsw.client.cli.graphical.buildings;

import it.polimi.ingsw.client.cli.graphical.CharFigure;
import it.polimi.ingsw.client.cli.utilities.CharStream;
import it.polimi.ingsw.client.cli.colors.BackColor;
import it.polimi.ingsw.client.cli.colors.ForeColor;

class SecondFloorBuilding implements CharFigure {
    private final CharStream stream;
    private final int RATEOX;
    private final int RATEOY;

    /**
     * /**
     * This method is the constructor for the graphical second floor that implements the CharFigure interface.
     * @param stream is the object used by the graphical second floor in order to display itself.
     * @param RATEOX is the length on the X axis.
     * @param RATEOY is the length on the Y axis.
     */
    public SecondFloorBuilding(CharStream stream, int RATEOX, int RATEOY){
        this.RATEOX = RATEOX;
        this.RATEOY = RATEOY;
        this.stream = stream;
    }

    /**
     * This method is overridden from the the CharFigure interface.
     * Since the building position on the stream is relative to the one of the graphical board this method is not used.
     */
    @Override
    public void draw() { }

    /**
     * This method will set colors and characters used to display the second floor through the stream.
     * Colors of the Second Floor: Grey.
     * @param relX is the position on the X axis and it is relative to a certain position on the board.
     * @param relY is the position on the Y axis and it is relative to a certain position on the board.
     */
    @Override
    public void draw(int relX, int relY) {
        BackColor backColor = BackColor.ANSI_BG_WHITE;
        ForeColor foreColor = ForeColor.ANSI_BLACK;
        relX += 5;
        relY += 2;
        for(int i = 0; i <= RATEOX; ++i){
            for(int j = 0; j <= RATEOY; ++j){
                if(i == 0 && j == 0) stream.addChar('╔', i + relX, j + relY, foreColor, backColor);
                else if(i == 0 && j ==  RATEOY) stream.addChar('╚', i + relX, j + relY, foreColor, backColor);
                else if(i == RATEOX && j == 0) stream.addChar('╗', i + relX, j + relY, foreColor, backColor);
                else if(i == RATEOX && j == RATEOY) stream.addChar('╝', i + relX, j + relY, foreColor, backColor);
                else if(i > 0 && i < RATEOX && j == 0) stream.addChar('═', i + relX, j + relY, foreColor, backColor);
                else if(i > 0 && i < RATEOX && j == RATEOY) stream.addChar('═', i +relX, j + relY, foreColor, backColor);
                else if(i == 0) stream.addChar('║', i + relX, j + relY, foreColor, backColor);
                else if(i == RATEOX) stream.addChar('║', i + relX, j + relY, foreColor, backColor);
                else stream.addColor(i + relX, j + relY, null , backColor);
            }
        }
    }
}
