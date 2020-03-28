package it.polimi.ingsw.cardReader;

import it.polimi.ingsw.cardReader.enums.TriggerType;

import java.util.List;

public abstract class CardRule {

    public abstract List<RuleStatement> getStatements();

    public abstract void addStatement(RuleStatement stm);

    public abstract RuleEffect getEffect();

    public abstract TriggerType getTrigger();
}
