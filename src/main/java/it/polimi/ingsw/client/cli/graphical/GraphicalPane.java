package it.polimi.ingsw.client.cli.graphical;

import it.polimi.ingsw.client.cli.utilities.CharStream;
import it.polimi.ingsw.client.cli.utilities.colors.BackColor;
import it.polimi.ingsw.client.cli.utilities.colors.ForeColor;

class GraphicalPane implements CharFigure {
    private final CharStream stream;
    private final int height;
    private final int width;
    private final ForeColor foreColor;
    private final BackColor backColor;

    /**
     * This constructor initializes the stream used by the GraphicalPane to print itself, its width and height and
     * its box color/character colors.
     * @param stream is the CharStream used to print.
     * @param width is the width of the GraphicalPane.
     * @param height is the height of the GraphicalPane.
     * @param foreColor is the character's color.
     * @param backColor is the box's color.
     */
    public GraphicalPane(CharStream stream, int width, int height, ForeColor foreColor, BackColor backColor){
        this.stream = stream;
        this.height = height;
        this.width = width;
        this.backColor = backColor;
        this.foreColor = foreColor;
    }

    /**
     * This constructor initializes the stream used by the GraphicalPane to print itself, its width and height and
     * its box color.
     * @param stream is the CharStream used to print.
     * @param width is the width of the GraphicalPane.
     * @param height is the height of the GraphicalPane.
     * @param backColor is the box's color.
     */
    public GraphicalPane(CharStream stream, int width, int height, BackColor backColor){
        this(stream, width, height, null, backColor);
    }

    /**
     * This method draws the GraphicalPane on the stream. Since they are always drawn relatively to the game-scene, this
     * method is not implemented.
     */
    @Override
    public void draw() { }

    /**
     * This method draws the GraphicalPane on the stream given the relative coordinates.
     * @param relX is the relative X coordinate.
     * @param relY is the relative Y coordinate.
     */
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
                else stream.addChar('\0',i + relX, j + relY, backColor);
            }
        }
    }
}