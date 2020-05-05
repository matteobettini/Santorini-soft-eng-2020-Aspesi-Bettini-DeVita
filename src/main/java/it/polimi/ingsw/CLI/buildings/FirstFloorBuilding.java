package it.polimi.ingsw.CLI.buildings;

import it.polimi.ingsw.CLI.CharStream;
import it.polimi.ingsw.CLI.GraphicalBuilding;
import it.polimi.ingsw.CLI.enums.BackColor;
import it.polimi.ingsw.CLI.enums.ForeColor;

public class FirstFloorBuilding extends GraphicalBuilding {
    private final CharStream stream;
    private final int RATEOX;
    private final int RATEOY;

    public FirstFloorBuilding(CharStream stream,int RATEOX, int RATEOY){
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
        BackColor parentBack = BackColor.ANSI_BG_WHITE;
        ForeColor parentFore = ForeColor.ANSI_BLACK;
        for(int i = 1; i < RATEOX; ++i) {
            for (int j = 1; j < RATEOY; ++j) {
                stream.addColor(i + relX, j + relY, parentFore, parentBack);
            }
        }
    }
}