package it.polimi.ingsw.client.gui.graphical.buildings;

import it.polimi.ingsw.common.enums.BuildingType;
import javafx.geometry.Point3D;
import javafx.scene.Group;

public class BuildingFactory {
    public static GraphicalBuilding getBuilding(BuildingType buildingType, Group container, Point3D position){
        switch (buildingType){
            case DOME:
                return new GraphicalDome(container, position);
            case THIRD_FLOOR:
                return new GraphicalThirdFloor(container, position);
            case SECOND_FLOOR:
                return new GraphicalSecondFloor(container, position);
            case FIRST_FLOOR:
                return new GraphicalFirstFloor(container, position);
        }
        assert false;
        return null;
    }
}
