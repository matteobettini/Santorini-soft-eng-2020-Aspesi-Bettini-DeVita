package it.polimi.ingsw.CLI;

import it.polimi.ingsw.CLI.buildings.DomeBuilding;
import it.polimi.ingsw.CLI.buildings.FirstFloorBuilding;
import it.polimi.ingsw.CLI.buildings.SecondFloorBuilding;
import it.polimi.ingsw.CLI.buildings.ThirdFloorBuilding;
import it.polimi.ingsw.CLI.enums.BuildingType;

public class BuildingFactory {
    public static GraphicalBuilding getBuilding(CharStream stream, BuildingType buildingType, int RATEOX, int RATEOY){
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