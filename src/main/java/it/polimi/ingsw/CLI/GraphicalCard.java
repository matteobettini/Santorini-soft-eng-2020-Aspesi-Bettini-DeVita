package it.polimi.ingsw.CLI;

import it.polimi.ingsw.CLI.colors.BackColor;
import it.polimi.ingsw.CLI.colors.ForeColor;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GraphicalCard implements CharFigure {
    private final String cardName;
    private final CharStream stream;
    private final String description;
    private static final int width = 35;
    private static final int height = 20;

    public GraphicalCard(CharStream stream, String cardName, String description){
        this.cardName = cardName;
        this.description = description;
        this.stream = stream;
    }

    public static int getWidth() {
        return width;
    }

    public static int getHeight() {
        return height;
    }

    @Override
    public void draw() {
        draw(0,0);
    }

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
                nextLine += 1;
                currentSpace = width - 1;
            }
            stream.addString(relX + width  - currentSpace, relY + nextLine, word.concat(" "), descColor, BackColor.ANSI_BRIGHT_BG_WHITE);
            currentSpace -= word.length() + 1;
            if(word.contains(":")) descColor = ForeColor.ANSI_BLACK;
        }

    }
}
