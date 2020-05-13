package it.polimi.ingsw.CLI.boats;

import it.polimi.ingsw.CLI.CharFigure;
import it.polimi.ingsw.CLI.CharStream;
import it.polimi.ingsw.CLI.boats.BoatType0;
import it.polimi.ingsw.CLI.boats.BoatType1;
import it.polimi.ingsw.CLI.boats.BoatType2;
import it.polimi.ingsw.CLI.boats.BoatType3;

public class BoatFactory {
    /**
     * This method is the factory for the graphical boats that will eventually appear on the graphical ocean.
     * There are 4 different types of boats, so the factory will return a new instance based on the given board number.
     * @param stream is the object used to set colors and characters and then print them.
     * @param boardNumber is the number of the wanted boat.
     * @return a new instance of char figure that will be able to dra itself through the stream.
     */
    public static CharFigure getBoat(CharStream stream, int boardNumber){
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
