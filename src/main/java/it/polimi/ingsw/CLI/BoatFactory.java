package it.polimi.ingsw.CLI;

import it.polimi.ingsw.CLI.boats.BoatType0;
import it.polimi.ingsw.CLI.boats.BoatType1;
import it.polimi.ingsw.CLI.boats.BoatType2;
import it.polimi.ingsw.CLI.boats.BoatType3;

public class BoatFactory {
    public static GraphicalBoat getBoat(CharStream stream, int boardNumber){
        switch (boardNumber){
            case 0:
                return new BoatType0(stream);
            case 1:
                return new BoatType1(stream);
            case 2:
                return new BoatType2(stream);
            case 3:
                return new BoatType3(stream);
            default:
                return null;
        }
    }
}
