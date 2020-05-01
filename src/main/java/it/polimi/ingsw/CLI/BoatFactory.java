package it.polimi.ingsw.CLI;

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
