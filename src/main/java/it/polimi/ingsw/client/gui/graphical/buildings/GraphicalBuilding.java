package it.polimi.ingsw.client.gui.graphical.buildings;

/**
 * Represents a 3D building, which manages its own graphics
 */
public interface GraphicalBuilding {
    void setSelected(boolean isSelected); //Change selection status of this object according to isSelected flag
    void remove(); //Remove this building from graphics
    double getHeight(); //Returns Y-Height of this building
}
