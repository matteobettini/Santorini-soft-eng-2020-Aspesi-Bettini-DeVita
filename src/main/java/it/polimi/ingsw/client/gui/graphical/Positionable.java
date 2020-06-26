package it.polimi.ingsw.client.gui.graphical;

import javafx.geometry.Point3D;

import java.awt.*;

public interface Positionable {
    String getID();
    void move(Point position);
    void updatePosition(Point3D newPosition);
}
