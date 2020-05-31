package it.polimi.ingsw.client.cli.graphical.buildings;

import it.polimi.ingsw.client.cli.graphical.CharFigure;
import it.polimi.ingsw.client.cli.utilities.CharStream;
import it.polimi.ingsw.common.enums.BuildingType;

public class BuildingFactory {

    /**
     * This method is the factory for the graphical building. Four possible instances are possible based on the given building type.
     * @param stream is the object used by the graphical buildings to display themselves.
     * @param buildingType is the type of the building used to return a certain CharFigure instance.
     * @param RATIO_X is the length on the X axis.
     * @param RATIO_Y  is the length on the Y axis.
     * @return a new instance of CharFigure that will override the dra method.
     */
    public static CharFigure getBuilding(CharStream stream, BuildingType buildingType, int RATIO_X, int RATIO_Y){
        switch (buildingType){
            case FIRST_FLOOR:
                return new FirstFloorBuilding(stream, RATIO_X, RATIO_Y);
            case SECOND_FLOOR:
                return new SecondFloorBuilding(stream, RATIO_X / 2, RATIO_Y / 2);
            case THIRD_FLOOR:
                return new ThirdFloorBuilding(stream, RATIO_X / 4, RATIO_Y / 4);
            case DOME:
                return new DomeBuilding(stream);
            default:
                return null;
        }
    }
}