package it.polimi.ingsw.CLI.boats;

import it.polimi.ingsw.CLI.CharFigure;
import it.polimi.ingsw.CLI.CharStream;
import it.polimi.ingsw.CLI.enums.BackColor;
import it.polimi.ingsw.CLI.enums.ForeColor;

public class BoatType1 implements CharFigure {

    private final CharStream stream;

    public BoatType1(CharStream stream){
        this.stream = stream;
    }

    @Override
    public void draw() {
        draw(0,0);
    }

    @Override
    public void draw(int relX, int relY) {
        stream.addChar('_',relX - 3, relY + 1, ForeColor.ANSI_BRIGHT_WHITE, BackColor.ANSI_BRIGHT_BG_CYAN);
        stream.addChar('_',relX + 6, relY + 1, ForeColor.ANSI_BRIGHT_WHITE, BackColor.ANSI_BRIGHT_BG_CYAN);
        stream.addColor(relX - 2, relY + 1, null, BackColor.ANSI_BG_WHITE);
        stream.addColor(relX - 1, relY + 1, null, BackColor.ANSI_BG_WHITE);
        stream.addColor(relX, relY + 1, null, BackColor.ANSI_BG_WHITE);
        stream.addColor(relX + 1 , relY + 1, null, BackColor.ANSI_BG_WHITE);
        stream.addColor(relX + 2, relY + 1, null, BackColor.ANSI_BG_WHITE);
        stream.addColor(relX + 3, relY + 1, null, BackColor.ANSI_BG_WHITE);
        stream.addColor(relX + 4, relY + 1, null, BackColor.ANSI_BG_WHITE);
        stream.addColor(relX + 5, relY + 1, null, BackColor.ANSI_BG_WHITE);
        stream.addColor(relX + 1, relY, null, BackColor.ANSI_BRIGHT_BG_WHITE);
        stream.addColor(relX + 2, relY, null, BackColor.ANSI_BRIGHT_BG_WHITE);
        stream.addChar('|',relX + 2, relY, ForeColor.ANSI_BLACK, BackColor.ANSI_BRIGHT_BG_WHITE);
    }
}
