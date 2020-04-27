package it.polimi.ingsw.cards;

import it.polimi.ingsw.cards.enums.AllowType;
import it.polimi.ingsw.cards.enums.EffectType;
import it.polimi.ingsw.model.enums.PlayerState;

import java.util.Objects;

/**
 * This class represent a rule effect, which will be applied if
 * its rule evaluates true.
 * It MAY also carry additional information used during effect compilation into lambda effect
 */
class RuleEffectImpl implements RuleEffect {

    private final EffectType type;
    private AllowType allowType;
    private PlayerState playerNextState;
    private final String data;

    public RuleEffectImpl(EffectType type, AllowType allowType, PlayerState playerNextState, String data) {
        assert(type != null);
        this.type = type;
        this.allowType = allowType;
        this.playerNextState = playerNextState;
        this.data = data;
    }
    public RuleEffectImpl(EffectType type, PlayerState playerNextState) {
        this(type,null,playerNextState,null);
    }

    /**
     * Getter for the effect type of this rule effect
     * @return Enum value corresponding to this effect type
     */
    public EffectType getType(){
        return this.type;
    }

    /**
     * Getter for the player's next state
     * @return Enum value corresponding to the desired player next state
     */
    public PlayerState getNextState(){
        return this.playerNextState;
    }

    /**
     * Setter for the player's next state
     * @param nextState Enum value to be applied to the player after the rule successfully activates
     */
    public void setNextState(PlayerState nextState){
        this.playerNextState = nextState;
    }

    /**
     * Setter for the allow subtype
     * @param type Enum value corresponding at the subtype of this allow
     */
    public void setAllowType(AllowType type){ this.allowType = type;}

    /**
     * Getter for additional personalization of the effect
     * @return String element, containing info to personalize the effect. Null if none is present
     */
    public String getData(){
        return this.data;
    }

    /**
     * Getter for the subtype of a rule effect.
     * @return AllowType value if the rule is allow, otherwise null
     */
    public AllowType getAllowType(){
        return this.allowType;
    }

    /**
     * Compares two RuleEffect, using the internal state instead of the memory location
     * @param o Object to compare to
     * @return True if the two objects contains the same information
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RuleEffectImpl that = (RuleEffectImpl) o;
        return type == that.type &&
                Objects.equals(allowType,that.allowType) &&
                Objects.equals(playerNextState,that.playerNextState) &&
                Objects.equals(data, that.data);
    }

    /**
     * Return an hash code for this class, using the internal information
     * @return The generated hashcode
     */
    @Override
    public int hashCode() {
        return Objects.hash(type, allowType, playerNextState, data);
    }
}
