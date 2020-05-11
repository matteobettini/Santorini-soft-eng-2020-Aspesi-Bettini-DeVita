package it.polimi.ingsw.CLI;

import it.polimi.ingsw.CLI.enums.BackColor;
import it.polimi.ingsw.CLI.enums.ForeColor;

import java.util.List;

public class GraphicalStartMenu implements CharFigure{
    private final CharStream stream;
    private final int width;
    private final int height;
    public static final int DEFAULT_WIDTH = 159;
    public static final int DEFAULT_HEIGHT = 30;

    public GraphicalStartMenu(CharStream stream, int width, int height){
        this.stream = stream;
        this.width = width;
        this.height = height;
    }

    public GraphicalStartMenu(CharStream stream){
        this(stream, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    @Override
    public void draw() {
        draw(0,0);
    }

    @Override
    public void draw(int relX, int relY) {
        for(int i = 0; i < width; ++i) {
            for (int j = 0; j < height; ++j) {
                stream.addColor(i ,j , null, BackColor.ANSI_BG_CYAN);
            }
        }

        int marginY = 10;
        int marginX = 46;
        String title = "SANTORINI";
        stream.setMessage(title, relX + marginX, relY + marginY, ForeColor.ANSI_BLACK, BackColor.ANSI_BRIGHT_BG_BLUE,BackColor.ANSI_BG_CYAN);
        stream.addString(relX + marginX + 18, relY + marginY + 7, "Welcome to Santorini Board-Game",   ForeColor.ANSI_BRIGHT_BLUE, BackColor.ANSI_BG_CYAN);
        stream.addString(relX + marginX - 10, relY + marginY + 9, "This video-game adaption was created by Andrea Aspesi, Matteo Bettini and Mirko De Vita",   ForeColor.ANSI_BRIGHT_BLUE, BackColor.ANSI_BG_CYAN);
    }
}