package it.polimi.ingsw.server.cards;

import it.polimi.ingsw.server.cards.enums.AllowType;
import it.polimi.ingsw.server.cards.enums.EffectType;
import it.polimi.ingsw.server.model.enums.PlayerState;

public interface RuleEffect {
    /**
     * Getter for the effect type of this rule effect
     * @return Enum value corresponding to this effect type
     */
    EffectType getType();
    /**
     * Getter for the subtype of a rule effect.
     * @return AllowType value if the rule is allow, otherwise null
     */
    AllowType getAllowType();
    /**
     * Getter for the player's next state
     * @return Enum value corresponding to the desired player next state
     */
    PlayerState getNextState();
    /**
     * Getter for additional personalization of the effect
     * @return String element, containing info to personalize the effect. Null if none is present
     */
    String getData();
}
