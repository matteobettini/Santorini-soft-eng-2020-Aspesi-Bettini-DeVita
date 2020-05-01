package it.polimi.ingsw.CLI;

import it.polimi.ingsw.CLI.enums.BackColor;
import it.polimi.ingsw.CLI.enums.ForeColor;

public class DomeBuilding extends GraphicalBuilding{
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
        BackColor parentBack = BackColor.ANSI_BG_BLUE;
        ForeColor parentFore = ForeColor.ANSI_BLACK;

        stream.addChar('*', relX , relY, parentFore, parentBack);
        stream.addChar('*', relX + 1 , relY , parentFore, parentBack);
        stream.addChar('*', relX + 2, relY, parentFore, parentBack);
        stream.addChar('*', relX + 3, relY, parentFore, parentBack);
        stream.addChar('*', relX - 1, relY + 1, parentFore, parentBack);
        stream.addColor(relX , relY + 1, parentFore, parentBack);
        stream.addColor(relX + 1, relY + 1, parentFore, parentBack);
        stream.addColor(relX + 2, relY + 1, parentFore, parentBack);
        stream.addColor(relX + 3, relY + 1, parentFore, parentBack);
        stream.addChar('*', relX + 4, relY + 1, parentFore, parentBack);
        stream.addChar('*', relX , relY + 2, parentFore, parentBack);
        stream.addChar('*', relX + 1, relY + 2, parentFore, parentBack);
        stream.addChar('*', relX + 2, relY + 2, parentFore, parentBack);
        stream.addChar('*', relX + 3, relY + 2, parentFore, parentBack);
    }
}
