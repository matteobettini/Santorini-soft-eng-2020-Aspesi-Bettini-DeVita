package it.polimi.ingsw.CLI;

import it.polimi.ingsw.CLI.enums.BackColor;

import java.awt.*;

public class GraphicalWorker implements CharFigure{

    private final BackColor color;
    private final CharStream stream;
    private final int RATEOX;
    private final int RATEOY;
    private final Integer number;
    private final String playerName;

    public GraphicalWorker(CharStream stream, Color color, int RATEOX, int RATEOY, Integer number, String playerName){
        this.stream = stream;
        if(color.equals(Color.CYAN)){
            this.color = BackColor.ANSI_BRIGHT_BG_CYAN;
        }
        else if(color.equals(Color.WHITE)){
            this.color = BackColor.ANSI_BG_PURPLE;
        }
        else if(color.equals(Color.ORANGE)){
            this.color = BackColor.ANSI_BG_YELLOW;
        }
        else this.color = BackColor.ANSI_BG_WHITE;
        this.RATEOX = RATEOX;
        this.RATEOY = RATEOY;
        this.number = number;
        this.playerName = playerName;
    }

    public Integer getNumber() {
        return number;
    }

    public String getPlayerName() {
        return playerName;
    }

    @Override
    public void draw() {
        draw(0,0);
    }

    @Override
    public void draw(int relX, int relY) {
        relX += 8;
        relY += 3;
        for(int i = 0; i <= RATEOX; ++i){
            for(int j = 0; j <= RATEOY; ++j){
                //stream.addColor(i + relX, j + relY, null, color);
                //if(i == 1 && j == 2) stream.addChar( '♙',i + relX, j + relY, null, color);
                if(i == 1 && j == 1) stream.addChar('*', i + relX, j + relY, color);
                if(i == 2 && j == 1) stream.addChar(playerName.charAt(0), i + relX, j + relY, color);
                if(i == 3 && j == 1) stream.addChar((char)(number + '0'), i + relX, j + relY, color);
                if(i == 4 && j == 1) stream.addChar('*', i + relX, j + relY, color);
            }
        }
    }
}