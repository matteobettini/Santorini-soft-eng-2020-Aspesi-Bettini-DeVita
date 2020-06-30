package it.polimi.ingsw.client.cli.graphical;

import it.polimi.ingsw.client.cli.utilities.CharStream;
import it.polimi.ingsw.client.cli.utilities.colors.BackColor;
import it.polimi.ingsw.client.cli.utilities.colors.ForeColor;

public class GraphicalStartMenu implements CharFigure {
    private final CharStream stream;
    private final int width;
    private final int height;
    public static final int DEFAULT_WIDTH = 159;
    public static final int DEFAULT_HEIGHT = 30;
    private static final int marginTitleX = 46;
    private static final int marginTitleY = 10;

    /**
     * This constructor initializes the stream used by the GraphicalStartMenu to print itself, its width and height.
     * @param stream is the CharStream used to display the menu.
     * @param width is the width of the graphical menu.
     * @param height is the height of the graphical menu.
     */
    public GraphicalStartMenu(CharStream stream, int width, int height){
        this.stream = stream;
        this.width = width;
        this.height = height;
    }

    /**
     * This constructor initializes the stream used by the GraphicalStartMenu to print itself.
     * @param stream is the CharStream used to display the menu.
     */
    public GraphicalStartMenu(CharStream stream){
        this(stream, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    /**
     * This method draws the GraphicalStartMenu on the stream's default position.
     */
    @Override
    public void draw() {
        draw(CharStream.defaultX, CharStream.defaultY);
    }

    /**
     * This method draws the GraphicalStartMenu.
     * @param relX is the relative X coordinate.
     * @param relY is the relative Y coordinate.
     */
    @Override
    public void draw(int relX, int relY) {
        for(int i = 0; i < width; ++i) {
            for (int j = 0; j < height; ++j) {
                stream.addColor(i ,j , null, BackColor.ANSI_BG_CYAN);
            }
        }

        String title = "SANTORINI";
        stream.setMessage(title, relX + marginTitleX, relY + marginTitleY, ForeColor.ANSI_BLACK, BackColor.ANSI_BRIGHT_BG_BLUE,BackColor.ANSI_BG_CYAN);
        stream.addString(relX + marginTitleX + 18, relY + marginTitleY + 7, "Welcome to Santorini Board-Game",   ForeColor.ANSI_BRIGHT_BLUE, BackColor.ANSI_BG_CYAN);
        stream.addString(relX + marginTitleX - 10, relY + marginTitleY + 9, "This video-game adaption was created by Andrea Aspesi, Matteo Bettini and Mirko De Vita",   ForeColor.ANSI_BRIGHT_BLUE, BackColor.ANSI_BG_CYAN);
    }
}