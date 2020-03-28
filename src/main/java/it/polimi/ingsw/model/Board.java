package it.polimi.ingsw.model;

import it.polimi.ingsw.model.enums.BuildingType;

import java.awt.*;

public abstract class Board {

    public abstract Cell getCell(Point p);

    public abstract boolean canUseBuilding(BuildingType b);

    public abstract boolean useBuilding(BuildingType b);

    public abstract void restockBuilding(BuildingType b);

    @Override
    protected abstract Board clone();


}
