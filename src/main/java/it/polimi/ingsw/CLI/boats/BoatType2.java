package it.polimi.ingsw.CLI.boats;

import it.polimi.ingsw.CLI.CharFigure;
import it.polimi.ingsw.CLI.CharStream;
import it.polimi.ingsw.CLI.enums.BackColor;
import it.polimi.ingsw.CLI.enums.ForeColor;

public class BoatType2 implements CharFigure {

    private final CharStream stream;

    public BoatType2(CharStream stream){
        this.stream = stream;
    }

    @Override
    public void draw() {
        draw(0,0);
    }

    @Override
    public void draw(int relX, int relY) {
        stream.addChar('_',relX - 2, relY + 1, ForeColor.ANSI_BRIGHT_WHITE, BackColor.ANSI_BRIGHT_BG_CYAN);
        stream.addChar('_',relX + 5, relY + 1, ForeColor.ANSI_BRIGHT_WHITE, BackColor.ANSI_BRIGHT_BG_CYAN);
        stream.addColor(relX - 1, relY + 1, BackColor.ANSI_BRIGHT_BG_RED);
        stream.addColor(relX , relY + 1, BackColor.ANSI_BRIGHT_BG_RED);
        stream.addColor(relX + 1 , relY + 1, BackColor.ANSI_BRIGHT_BG_RED);
        stream.addColor(relX + 2, relY + 1, BackColor.ANSI_BRIGHT_BG_RED);
        stream.addColor(relX + 3, relY + 1, BackColor.ANSI_BRIGHT_BG_RED);
        stream.addColor(relX + 4, relY + 1, BackColor.ANSI_BRIGHT_BG_RED);
        stream.addColor(relX + 1, relY, BackColor.ANSI_BRIGHT_BG_BLACK);
        stream.addColor(relX + 2, relY, BackColor.ANSI_BRIGHT_BG_BLACK);
        stream.addChar('|',relX + 2, relY, ForeColor.ANSI_BRIGHT_WHITE, BackColor.ANSI_BRIGHT_BG_BLACK);
    }
}
