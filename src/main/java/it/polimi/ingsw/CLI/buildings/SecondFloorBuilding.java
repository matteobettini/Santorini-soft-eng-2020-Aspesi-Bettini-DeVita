package it.polimi.ingsw.CLI.buildings;

import it.polimi.ingsw.CLI.CharFigure;
import it.polimi.ingsw.CLI.CharStream;
import it.polimi.ingsw.CLI.enums.BackColor;
import it.polimi.ingsw.CLI.enums.ForeColor;

public class SecondFloorBuilding implements CharFigure {
    private final CharStream stream;
    private final int RATEOX;
    private final int RATEOY;

    public SecondFloorBuilding(CharStream stream, int RATEOX, int RATEOY){
        this.RATEOX = RATEOX;
        this.RATEOY = RATEOY;
        this.stream = stream;
    }

    @Override
    public void draw() {
        draw(0,0);
    }

    @Override
    public void draw(int relX, int relY) {
        BackColor backColor = BackColor.ANSI_BG_WHITE;
        ForeColor foreColor = ForeColor.ANSI_BLACK;
        relX += 5;
        relY += 2;
        for(int i = 0; i <= RATEOX; ++i){
            for(int j = 0; j <= RATEOY; ++j){
                if(i == 0 && j == 0) stream.addChar('╔', i + relX, j + relY, foreColor, backColor);
                else if(i == 0 && j ==  RATEOY) stream.addChar('╚', i + relX, j + relY, foreColor, backColor);
                else if(i == RATEOX && j == 0) stream.addChar('╗', i + relX, j + relY, foreColor, backColor);
                else if(i == RATEOX && j == RATEOY) stream.addChar('╝', i + relX, j + relY, foreColor, backColor);
                else if(i > 0 && i < RATEOX && j == 0) stream.addChar('═', i + relX, j + relY, foreColor, backColor);
                else if(i > 0 && i < RATEOX && j == RATEOY) stream.addChar('═', i +relX, j + relY, foreColor, backColor);
                else if(i == 0) stream.addChar('║', i + relX, j + relY, foreColor, backColor);
                else if(i == RATEOX) stream.addChar('║', i + relX, j + relY, foreColor, backColor);
                else stream.addColor(i + relX, j + relY, null , backColor);
            }
        }
    }
}
