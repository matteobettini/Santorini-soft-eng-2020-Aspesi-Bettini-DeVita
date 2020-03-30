package it.polimi.ingsw.model.cardReader.cardValidator.validators;

import it.polimi.ingsw.model.cardReader.RuleEffect;
import it.polimi.ingsw.model.cardReader.cardValidator.exceptions.InvalidRuleEffectException;
import it.polimi.ingsw.model.enums.PlayerState;

/**
 * This class offers a method to validate card effect's semantic
 */
public class EffectValidator {

    /**
     * Validate card effect semantic
     * @param effect Effect to be validated
     * @throws InvalidRuleEffectException If problems are found during the scan.
     *                                In the message it's always reported the reason.
     */
    public static void checkRuleEffect(RuleEffect effect) throws InvalidRuleEffectException {
        switch (effect.getType()){
            case ALLOW:
                allowValidate(effect);
                break;
            case DENY:
                denyValidate(effect);
                break;
            case WIN:
                winValidate(effect);
                break;
            case SET_OPPONENT_POSITION:
                setOpponentPositionValidate(effect);
                break;
        }
    }

    private static void allowValidate(RuleEffect effect) throws InvalidRuleEffectException {
        if (effect.getData() != null){
            throw new InvalidRuleEffectException("[ALLOW] Effect data not supported");
        }
    }

    private static void denyValidate(RuleEffect effect) throws InvalidRuleEffectException {
        if (effect.getData() != null){
            throw new InvalidRuleEffectException("[DENY] Effect data not supported");
        }
        if (effect.getNextState() != PlayerState.UNKNOWN){
            throw new InvalidRuleEffectException("[DENY] Effect player next state tag not supported");
        }
    }

    private static void winValidate(RuleEffect effect)  throws InvalidRuleEffectException{
        if (effect.getData() != null){
            throw new InvalidRuleEffectException("[WIN] Effect data not supported");
        }
        if (effect.getNextState() != PlayerState.UNKNOWN){
            throw new InvalidRuleEffectException("[WIN] Effect player next state tag not supported");
        }
    }

    private static void setOpponentPositionValidate(RuleEffect effect) throws InvalidRuleEffectException {
        if (effect.getData() == null){
            throw new InvalidRuleEffectException("[SET_OPPONENT_POSITION] Effect data is required. None provided");
        }
        String effect_data = effect.getData();
        switch (effect_data){
            case "SWAP":
            case "PUSH_STRAIGHT":
                break;
            default:
                throw new InvalidRuleEffectException("[SET_OPPONENT_POSITION] Data '" + effect_data + "' is not supported");
        }
    }
}
