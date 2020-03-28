package it.polimi.ingsw.model;

import it.polimi.ingsw.cardReader.CardFile;
import it.polimi.ingsw.model.enums.PlayerState;

import java.util.List;

public abstract class Player {

    public abstract String getNickname();

    public abstract CardFile getCard();

    public abstract PlayerState getState();

    public abstract List<Worker> getWorkers();

    public abstract void setPlayerState(PlayerState ps);

    public abstract void setCard(CardFile c);

    @Override
    public abstract boolean equals(Object obj);

    @Override
    protected abstract Player clone();
}
