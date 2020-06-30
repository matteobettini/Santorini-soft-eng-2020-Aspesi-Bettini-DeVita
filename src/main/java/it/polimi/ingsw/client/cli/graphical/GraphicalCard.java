package it.polimi.ingsw.client.cli.graphical;

import it.polimi.ingsw.client.cli.utilities.CharStream;
import it.polimi.ingsw.client.cli.utilities.colors.BackColor;
import it.polimi.ingsw.client.cli.utilities.colors.ForeColor;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

class GraphicalCard implements CharFigure {
    private final String cardName;
    private final CharStream stream;
    private final String description;
    private static final int width = 35;
    private static final int height = 20;

    /**
     * This constructor initializes the card's name, description and the stream used to display itself.
     * @param stream is the CharStream used to display the GraphicalCard.
     * @param cardName is the String containing the god card's name.
     * @param description is the String containing the god card's description.
     */
    public GraphicalCard(CharStream stream, String cardName, String description){
        this.cardName = cardName;
        this.description = description;
        this.stream = stream;
    }

    /**
     * This method returns the default width of a GraphicalCard.
     * @return an integer representing the width.
     */
    public static int getWidth() {
        return width;
    }

    /**
     * This method returns the default height of a GraphicalCard.
     * @return an integer representing the height.
     */
    public static int getHeight() {
        return height;
    }

    /**
     * This method is used to display the GraphicalCard on the stream. Since cards are always displayed
     * relatively to the GraphicalCardsMenu this method draws on the stream's default position .
     */
    @Override
    public void draw() { draw(CharStream.defaultX, CharStream.defaultY); }

    /**
     * This method is used to display the GraphicalCard on the stream. GraphicalCards are always displayed
     * relatively to the GraphicalCardsMenu.
     * @param relX is the GraphicalCardMenu's relative X coordinate.
     * @param relY is the GraphicalCardMenu's relative Y coordinate.
     */
    @Override
    public void draw(int relX, int relY) {
        for(int i = 0; i <= width; ++i){
            for(int j = 0; j <= height; ++j){
                if(i == 0 && j == 0) stream.addChar('╭',i + relX, j + relY);
                else if(i == 0 && j == height) stream.addChar('╰',i + relX, j + relY);
                else if(i == width && j == 0) stream.addChar('╮',i + relX, j + relY);
                else if(i == width && j == height) stream.addChar('╯',i + relX, j + relY);
                else stream.addColor(i + relX, j + relY,  BackColor.ANSI_BRIGHT_BG_WHITE);
            }
        }
        int midPointCard = relX + 18;
        int midPointName = cardName.length()/2;
        stream.addString(midPointCard - midPointName, relY, cardName.substring(0,midPointName + 1), ForeColor.ANSI_BLACK, BackColor.ANSI_BRIGHT_BG_WHITE);
        stream.addString(midPointCard , relY, cardName.substring(midPointName), ForeColor.ANSI_BLACK, BackColor.ANSI_BRIGHT_BG_WHITE);
        List<String> desc = Arrays.stream(description.split("\\s+")).collect(Collectors.toList());
        int currentSpace = width - 1;
        int nextLine = 4;
        ForeColor descColor = ForeColor.ANSI_BLUE;
        for(String word : desc){
            if(currentSpace - (word.length() + 1) < 0){
                if(nextLine == height - 1 || word.length() >= width){
                    stream.addString(relX + width / 2, relY + nextLine + 1,"...", descColor, BackColor.ANSI_BRIGHT_BG_WHITE);
                    return;
                }
                nextLine += 1;
                currentSpace = width - 1;
            }
            stream.addString(relX + width  - currentSpace, relY + nextLine, word.concat(" "), descColor, BackColor.ANSI_BRIGHT_BG_WHITE);
            currentSpace -= word.length() + 1;
            if(word.contains(":")) descColor = ForeColor.ANSI_BLACK;
        }

    }
}
