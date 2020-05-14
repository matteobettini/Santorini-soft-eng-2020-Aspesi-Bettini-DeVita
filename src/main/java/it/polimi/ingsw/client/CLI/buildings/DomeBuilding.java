package it.polimi.ingsw.client.CLI.buildings;

import it.polimi.ingsw.client.CLI.CharFigure;
import it.polimi.ingsw.client.CLI.CharStream;
import it.polimi.ingsw.client.CLI.colors.BackColor;
import it.polimi.ingsw.client.CLI.colors.ForeColor;

class DomeBuilding implements CharFigure {
    private final CharStream stream;

    /**
     * This method is the constructor for the graphical dome that implements the CharFigure interface.
     * @param stream is the object used by the graphical dome in order to display itself.
     */
    public DomeBuilding(CharStream stream){
        this.stream = stream;
    }

    /**
     * This method is overridden from the the CharFigure interface.
     * Since the building position on the stream is relative to the one of the graphical board this method is not used.
     */
    @Override
    public void draw() { }

    /**
     * This method will set colors and characters used to display the dome through the stream.
     * Colors of the Dome: Blue.
     * @param relX is the position on the X axis and it is relative to a certain position on the board.
     * @param relY is the position on the Y axis and it is relative to a certain position on the board.
     */
    @Override
    public void draw(int relX, int relY) {
        relX += 9;
        relY += 3;
        BackColor backColor = BackColor.ANSI_BG_BLUE;
        ForeColor foreColor = ForeColor.ANSI_BLACK;

        stream.addChar('*', relX , relY, foreColor, backColor);
        stream.addChar('*', relX + 1 , relY , foreColor, backColor);
        stream.addChar('*', relX + 2, relY, foreColor, backColor);
        stream.addChar('*', relX + 3, relY, foreColor, backColor);
        stream.addChar('*', relX - 1, relY + 1, foreColor, backColor);
        stream.addColor(relX , relY + 1, foreColor, backColor);
        stream.addColor(relX + 1, relY + 1, foreColor, backColor);
        stream.addColor(relX + 2, relY + 1, foreColor, backColor);
        stream.addColor(relX + 3, relY + 1, foreColor, backColor);
        stream.addChar('*', relX + 4, relY + 1, foreColor, backColor);
        stream.addChar('*', relX , relY + 2, foreColor, backColor);
        stream.addChar('*', relX + 1, relY + 2, foreColor, backColor);
        stream.addChar('*', relX + 2, relY + 2, foreColor, backColor);
        stream.addChar('*', relX + 3, relY + 2, foreColor, backColor);
    }
}
