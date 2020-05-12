package it.polimi.ingsw.CLI.buildings;

import it.polimi.ingsw.CLI.CharFigure;
import it.polimi.ingsw.CLI.CharStream;
import it.polimi.ingsw.CLI.colors.BackColor;
import it.polimi.ingsw.CLI.colors.ForeColor;

class DomeBuilding implements CharFigure {
    private final CharStream stream;

    public DomeBuilding(CharStream stream){
        this.stream = stream;
    }

    @Override
    public void draw() {
        draw(0,0);
    }

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
