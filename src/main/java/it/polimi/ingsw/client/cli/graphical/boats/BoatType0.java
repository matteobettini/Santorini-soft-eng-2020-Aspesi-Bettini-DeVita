package it.polimi.ingsw.client.cli.graphical.boats;

import it.polimi.ingsw.client.cli.graphical.CharFigure;
import it.polimi.ingsw.client.cli.graphical.GraphicalOcean;
import it.polimi.ingsw.client.cli.utilities.CharStream;
import it.polimi.ingsw.client.cli.utilities.colors.BackColor;
import it.polimi.ingsw.client.cli.utilities.colors.ForeColor;

class BoatType0 implements CharFigure {
    private final CharStream stream;
    private static final BackColor sailColor = BackColor.ANSI_BRIGHT_BG_WHITE;
    private static final ForeColor mastColor = ForeColor.ANSI_BLACK;
    private static final BackColor boatColor = BackColor.ANSI_BG_YELLOW;

    /**
     * The constructor only need to know the stream in order to print itself.
     * @param stream is the object used to set colors and characters to be able to print them.
     */
    public BoatType0(CharStream stream){
        this.stream = stream;
    }

    /**
     * This method is overridden from the CharFigure interface.
     * Since the board position on the stream is relative to the one of the graphical ocean this method is not used.
     */
    @Override
    public void draw() { draw(CharStream.defaultX, CharStream.defaultY);  }

    /**
     * This method will set colors and characters used to display the board through the stream.
     * Colors of the BoatType0: Orange with White sail.
     * @param relX is the position on the X axis.
     * @param relY is the position on the Y axis.
     */
    @Override
    public void draw(int relX, int relY) {
        stream.addChar('_',relX - 1, relY + 1,  GraphicalOcean.waveColor, GraphicalOcean.waterColor);
        stream.addChar('_',relX + 4, relY + 1,  GraphicalOcean.waveColor, GraphicalOcean.waterColor);
        stream.addColor(relX + 2, relY + 1, boatColor);
        stream.addColor(relX, relY + 1, boatColor);
        stream.addColor(relX + 1 , relY + 1, boatColor);
        stream.addColor(relX + 2, relY + 1, boatColor);
        stream.addColor(relX + 3, relY + 1, boatColor);
        stream.addColor(relX + 1, relY, sailColor);
        stream.addColor(relX + 2, relY, sailColor);
        stream.addChar('|',relX + 2, relY, mastColor, sailColor);
    }
}
