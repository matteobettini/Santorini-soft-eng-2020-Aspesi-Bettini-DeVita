package it.polimi.ingsw.model;

import java.awt.*;

public abstract class Worker {

    public abstract String getID();

    public abstract void setPosition(Point cell);

    public abstract Point getPosition();

    public abstract Player getPlayer();

    @Override
    public abstract boolean equals(Object obj);

    @Override
    protected abstract Worker clone();

}
