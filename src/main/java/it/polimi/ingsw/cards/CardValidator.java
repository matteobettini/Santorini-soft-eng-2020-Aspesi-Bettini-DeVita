package it.polimi.ingsw.cards;

import it.polimi.ingsw.cards.enums.EffectType;
import it.polimi.ingsw.cards.exceptions.InvalidRuleEffectException;
import it.polimi.ingsw.cards.exceptions.InvalidStatementObjectException;
import it.polimi.ingsw.cards.exceptions.InvalidStatementSubjectException;
import it.polimi.ingsw.cards.enums.TriggerType;
import it.polimi.ingsw.cards.exceptions.InvalidCardException;

/**
 * This class offers a method to validate semantically a CardFile
 */
class CardValidator {

    /**
     * This method is used to validate a CardFile semantically
     * @param card CardFile to be validated
     * @throws InvalidCardException If problems are found during the scan.
     *                              In the message it's always reported the reason.
     */
    public static void checkCardFile(CardFile card) throws InvalidCardException {
        for(CardRule cardRule : card.getRules()){
            checkCardRule(cardRule);
        }
    }

    private static void checkCardRule(CardRule rule) throws InvalidCardException {
        //Check mixed statements
        checkMixedStatements(rule);
        //Check statements
        try{
            for(RuleStatement stm : rule.getStatements()){
                StatementValidator.checkRuleStatement(stm);
            }
        } catch (InvalidStatementSubjectException e) {
            throw new InvalidCardException("[RULE_STATEMENTS][SUBJECT]" + e.getMessage());
        } catch (InvalidStatementObjectException e) {
            throw new InvalidCardException("[RULE_STATEMENTS][OBJECT]" + e.getMessage());
        }
        //Check Effect
        try{
            EffectValidator.checkRuleEffect(rule.getEffect());
        } catch (InvalidRuleEffectException e) {
            throw new InvalidCardException("[EFFECT]" + e.getMessage());
        }
        //Check effect allow subtypes
        checkAllowSubtypes(rule);
    }

    private static void checkMixedStatements(CardRule rule) throws InvalidCardException{
        for(RuleStatement stm : rule.getStatements()){
            if (!isStatementPlacementValid(stm,rule.getTrigger())){
                throw new InvalidCardException("[RULE]Mixed statements: '" + stm.getVerb().toString() + "' found in '" + rule.getTrigger().toString() + "'");
            }
        }
    }

    private static boolean isStatementPlacementValid(RuleStatement stm, TriggerType ruleTrigger){
        switch (ruleTrigger){
            case MOVE:
                switch (stm.getVerb()){
                    case PLAYER_EQUALS:
                    case STATE_EQUALS:
                    case HAS_FLAG:
                    case MOVE_LENGTH:
                    case EXISTS_DELTA_MORE:
                    case EXISTS_DELTA_LESS:
                    case INTERACTION_NUM:
                    case EXISTS_LEVEL_TYPE:
                    case POSITION_EQUALS:
                        return true;
                }
                break;
            case BUILD:
                switch (stm.getVerb()){
                    case PLAYER_EQUALS:
                    case STATE_EQUALS:
                    case HAS_FLAG:
                    case BUILD_NUM:
                    case BUILD_DOME:
                    case BUILD_DOME_EXCEPT:
                    case BUILD_IN_SAME_SPOT:
                        return true;
                }
                break;
            default:
                assert false;
        }
        return false;
    }

    private static void checkAllowSubtypes(CardRule rule) throws InvalidCardException{
        if (rule.getEffect().getType() != EffectType.ALLOW) return; //Obviously skip if it's not allow
        if (rule.getEffect().getAllowType() == null) return; //Will be patched
        switch (rule.getTrigger()){
            case MOVE:
                switch (rule.getEffect().getAllowType()){
                    case STANDARD:
                    case SET_OPPONENT:
                        return;
                }
                break;
            case BUILD:
                switch (rule.getEffect().getAllowType()){
                    case STANDARD:
                        return;
                }
                break;
        }
        throw new InvalidCardException("[RULE]Mixed allow subtypes: '" + rule.getEffect().getAllowType().toString() + "' found in '" + rule.getTrigger().toString() + "'");
    }
}
