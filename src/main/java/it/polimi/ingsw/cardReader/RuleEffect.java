package it.polimi.ingsw.cardReader;

import it.polimi.ingsw.cardReader.enums.EffectType;

public abstract class RuleEffect {

    /**
     * Getter for the effect type of this rule effect
     * @return Enum value corresponding to this effect type
     */
    public abstract EffectType getType();
}
