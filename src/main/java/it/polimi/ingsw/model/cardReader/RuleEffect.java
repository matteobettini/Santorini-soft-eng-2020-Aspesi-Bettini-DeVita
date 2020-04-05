package it.polimi.ingsw.model.cardReader;

import it.polimi.ingsw.model.cardReader.enums.EffectType;
import it.polimi.ingsw.model.enums.PlayerState;

public interface RuleEffect {
    /**
     * Getter for the effect type of this rule effect
     * @return Enum value corresponding to this effect type
     */
    EffectType getType();
    /**
     * Getter for the player's next state
     * @return Enum value corresponding to the desired player next state
     */
    PlayerState getNextState();
    /**
     * Getter for the unconverted data tag of the effect.
     * Will be compiled into code during rule compilation.
     * @return XML Element containing effect additional info
     *         null, if data is not present
     */
    String getData();
}
