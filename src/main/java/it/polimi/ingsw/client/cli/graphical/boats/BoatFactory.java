package it.polimi.ingsw.client.cli.graphical.boats;

import it.polimi.ingsw.client.cli.graphical.CharFigure;
import it.polimi.ingsw.client.cli.utilities.CharStream;

public class BoatFactory {
    public static final int TYPE0 = 0;
    public static final int TYPE1 = 1;
    public static final int TYPE2 = 2;
    public static final int TYPE3 = 3;
    public static final int numberOfTypes = 4;

    /**
     * This method is the factory for the graphical boats that will eventually appear on the graphical ocean.
     * There are 4 different types of boats, so the factory will return a new instance based on the given board number.
     * @param stream is the object used to set colors and characters and then print them.
     * @param boardNumber is the number of the wanted boat.
     * @return a new instance of char figure that will be able to dra itself through the stream.
     */
    public static CharFigure getBoat(CharStream stream, int boardNumber){
        switch (boardNumber){
            case TYPE0:
                return new BoatType0(stream);
            case TYPE1:
                return new BoatType1(stream);
            case TYPE2:
                return new BoatType2(stream);
            case TYPE3:
                return new BoatType3(stream);
            default:
                return null;
        }
    }
}
