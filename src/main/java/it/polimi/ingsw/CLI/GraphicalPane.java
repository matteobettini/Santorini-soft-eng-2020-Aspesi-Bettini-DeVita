package it.polimi.ingsw.CLI;

import it.polimi.ingsw.CLI.enums.BackColor;
import it.polimi.ingsw.CLI.enums.ForeColor;

public class GraphicalPane implements CharFigure{
    private final CharStream stream;
    private final int height;
    private final int width;
    private final ForeColor parentFore;
    private final BackColor parentBack;

    public GraphicalPane(CharStream stream, int width, int height, ForeColor parentFore, BackColor parentBack){
        this.stream = stream;
        this.height = height;
        this.width = width;
        this.parentBack = parentBack;
        this.parentFore = parentFore;
    }

    @Override
    public void draw() {
        draw(0,0);
    }

    @Override
    public void draw(int relX, int relY) {
        for(int i = 0; i <= width; ++i){
            for(int j = 0; j <= height; ++j){
                //FIRST THE CONDITIONS FOR THE TOP
                if(i == 0 && j == 0) stream.addChar('╔', i + relX, j + relY, parentFore, parentBack);
                else if(i == 0 && j == height) stream.addChar('╚', i + relX, j + relY, parentFore, parentBack);
                    //CONDITIONS FOR THE BOTTOM'╗'
                else if(i == width  && j == 0) stream.addChar('╗', i + relX, j + relY, parentFore, parentBack);
                else if(i == width && j == height) stream.addChar('╝', i + relX, j + relY, parentFore, parentBack);
                    //STRAIGHT LINES
                else if(j > 0 && j < height && (i == 0 || i == width) ) stream.addChar('║', i + relX, j + relY, parentFore, parentBack);
                else if(i > 0 && i < width && (j == 0 || j == height)) stream.addChar('═', i + relX, j + relY, parentFore, parentBack);
                else stream.addChar('\0',i + relX, j + relY, null, parentBack);
            }
        }
    }
}