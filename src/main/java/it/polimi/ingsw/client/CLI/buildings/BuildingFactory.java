package it.polimi.ingsw.client.CLI.buildings;

import it.polimi.ingsw.client.CLI.CharFigure;
import it.polimi.ingsw.client.CLI.CharStream;
import it.polimi.ingsw.common.enums.BuildingType;

public class BuildingFactory {

    /**
     * This method is the factory for the graphical building. Four possible instances are possible based on the given building type.
     * @param stream is the object used by the graphical buildings to display themselves.
     * @param buildingType is the type of the building used to return a certain CharFigure instance.
     * @param RATEOX is the length on the X axis.
     * @param RATEOY  is the length on the Y axis.
     * @return a new instance of CharFigure that will override the dra method.
     */
    public static CharFigure getBuilding(CharStream stream, BuildingType buildingType, int RATEOX, int RATEOY){
        switch (buildingType){
            case FIRST_FLOOR:
                return new FirstFloorBuilding(stream, RATEOX, RATEOY);
            case SECOND_FLOOR:
                return new SecondFloorBuilding(stream, RATEOX / 2, RATEOY / 2);
            case THIRD_FLOOR:
                return new ThirdFloorBuilding(stream, RATEOX / 4, RATEOY / 4);
            case DOME:
                return new DomeBuilding(stream);
            default:
                return null;
        }
    }
}