package it.polimi.ingsw.CLI;

import it.polimi.ingsw.CLI.enums.BackColor;
import it.polimi.ingsw.CLI.enums.ForeColor;

public class ThirdFloorBuilding extends GraphicalBuilding{
    private final CharStream stream;
    private final int RATEOX;
    private final int RATEOY;

    public ThirdFloorBuilding(CharStream stream, int RATEOX, int RATEOY){
        this.RATEOX = RATEOX;
        this.RATEOY = RATEOY;
        this.stream = stream;
    }

    public int getRATEOX() {
        return RATEOX;
    }

    public int getRATEOY() {
        return RATEOY;
    }

    @Override
    public void draw() {
        draw(0,0);
    }

    @Override
    public void draw(int relX, int relY) {
        relX += 8;
        relY+= 3;
        BackColor parentBack = BackColor.ANSI_BG_WHITE;
        ForeColor parentFore = ForeColor.ANSI_BLACK;
        for(int i = 0; i <= RATEOX; ++i){
            for(int j = 0; j <= RATEOY; ++j){
                if(i == 0 && j == 0) stream.addChar('╔', i + relX, j + relY, parentFore, parentBack);
                else if(i == 0 && j ==  RATEOY) stream.addChar('╚', i + relX, j + relY, parentFore, parentBack);
                else if(i == RATEOX && j == 0) stream.addChar('╗', i + relX, j + relY, parentFore, parentBack);
                else if(i == RATEOX && j == RATEOY) stream.addChar('╝', i + relX, j + relY, parentFore, parentBack);
                else if(i > 0 && i < RATEOX && j == 0) stream.addChar('═', i + relX, j + relY, parentFore, parentBack);
                else if(i > 0 && i < RATEOX && j == RATEOY) stream.addChar('═', i +relX, j + relY, parentFore, parentBack);
                else if(i == 0) stream.addChar('║', i + relX, j + relY, parentFore, parentBack);
                else if(i == RATEOX) stream.addChar('║', i + relX, j + relY, parentFore, parentBack);
                else stream.addColor(i + relX, j + relY, null , parentBack);
            }
        }
    }
}
