package it.polimi.ingsw.cardReader;

import it.polimi.ingsw.cardReader.enums.TriggerType;

import java.util.List;

public abstract class CardFile {

    public abstract String getName();

    public abstract String getDescription();

    public abstract List<CardRule> getRules();

    public abstract List<CardRule> getRules(TriggerType trigger);
}
