package it.polimi.ingsw.client.gui.graphical.buildings;

import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.paint.Color;

/**
 * Represents a 3D third floor
 */
class GraphicalThirdFloor extends GraphicalBuildingImpl implements GraphicalBuilding {

    private static final String MESH_PATH = "/client/mesh/third_floor.stl";
    private static final Color ORIGINAL_COLOR = Color.WHITE;
    private static final Color SELECTED_COLOR = Color.GREEN;
    private static final double HEIGHT = 3.755846;

    GraphicalThirdFloor(Group container, Point3D position){
        super(container,position,ORIGINAL_COLOR,SELECTED_COLOR,MESH_PATH);
    }

    @Override
    public double getHeight() {
        return HEIGHT;
    }
}
