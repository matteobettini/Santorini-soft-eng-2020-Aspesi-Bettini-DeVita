package it.polimi.ingsw.model.cardReader;

import it.polimi.ingsw.model.cardReader.exceptions.InvalidRuleEffectException;

/**
 * This class offers a method to validate card effect's semantic
 */
class EffectValidator {

    /**
     * Validate card effect semantic
     * @param effect Effect to be validated
     * @throws InvalidRuleEffectException   If problems are found during the scan.
     *                                      In the message it's always reported the reason.
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
        }
    }

    private static void allowValidate(RuleEffect effect) throws InvalidRuleEffectException {
        if (effect.getAllowType() == null) return; //Will be patched
        switch (effect.getAllowType()){
            case STANDARD:
                allowStandardValidate(effect);
                break;
            case SET_OPPONENT:
                setOpponentPositionValidate(effect);
                break;
            default:
                assert false;
        }
    }

    private static void allowStandardValidate(RuleEffect effect) throws InvalidRuleEffectException {
        if (effect.getData() != null){
            throw new InvalidRuleEffectException("[ALLOW][STANDARD] Effect data not supported");
        }
    }

    private static void setOpponentPositionValidate(RuleEffect effect) throws InvalidRuleEffectException {
        String effect_data = effect.getData();
        if (effect_data == null){
            throw new InvalidRuleEffectException("[ALLOW][SET_OPPONENT] Effect data is required. None provided");
        }
        switch (effect_data){
            case "SWAP":
            case "PUSH_STRAIGHT":
                break;
            default:
                throw new InvalidRuleEffectException("[ALLOW][SET_OPPONENT] Data '" + effect_data + "' is not supported");
        }
    }

    private static void denyValidate(RuleEffect effect) throws InvalidRuleEffectException {
        if (effect.getAllowType() != null){
            throw new InvalidRuleEffectException("[DENY] Allow subtype tag not allowed");
        }
        if (effect.getData() != null){
            throw new InvalidRuleEffectException("[DENY] Effect data tag not supported");
        }
        if (effect.getNextState() != null){
            throw new InvalidRuleEffectException("[DENY] Effect player next state tag not supported");
        }
    }

    private static void winValidate(RuleEffect effect)  throws InvalidRuleEffectException{
        if (effect.getAllowType() != null){
            throw new InvalidRuleEffectException("[WIN] Allow subtype tag not allowed");
        }
        if (effect.getData() != null){
            throw new InvalidRuleEffectException("[WIN] Effect data tag not supported");
        }
        if (effect.getNextState() != null){
            throw new InvalidRuleEffectException("[WIN] Effect player next state tag not supported");
        }
    }
}
