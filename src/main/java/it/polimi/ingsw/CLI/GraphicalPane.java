package it.polimi.ingsw.CLI;

import it.polimi.ingsw.CLI.colors.BackColor;
import it.polimi.ingsw.CLI.colors.ForeColor;

public class GraphicalPane implements CharFigure{
    private final CharStream stream;
    private final int height;
    private final int width;
    private final ForeColor foreColor;
    private final BackColor backColor;

    public GraphicalPane(CharStream stream, int width, int height, ForeColor foreColor, BackColor backColor){
        this.stream = stream;
        this.height = height;
        this.width = width;
        this.backColor = backColor;
        this.foreColor = foreColor;
    }

    public GraphicalPane(CharStream stream, int width, int height, BackColor backColor){
        this(stream, width, height, null, backColor);
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
                if(i == 0 && j == 0) stream.addChar('╔', i + relX, j + relY, foreColor, backColor);
                else if(i == 0 && j == height) stream.addChar('╚', i + relX, j + relY, foreColor, backColor);
                    //CONDITIONS FOR THE BOTTOM'╗'
                else if(i == width  && j == 0) stream.addChar('╗', i + relX, j + relY, foreColor, backColor);
                else if(i == width && j == height) stream.addChar('╝', i + relX, j + relY, foreColor, backColor);
                    //STRAIGHT LINES
                else if(j > 0 && j < height && (i == 0 || i == width) ) stream.addChar('║', i + relX, j + relY, foreColor, backColor);
                else if(i > 0 && i < width && (j == 0 || j == height)) stream.addChar('═', i + relX, j + relY, foreColor, backColor);
                else stream.addChar('\0',i + relX, j + relY, null, backColor);
            }
        }
    }
}