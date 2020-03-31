package it.polimi.ingsw.model.cardReader.cardValidator;

import it.polimi.ingsw.model.cardReader.CardFile;
import it.polimi.ingsw.model.cardReader.CardRule;
import it.polimi.ingsw.model.cardReader.RuleStatement;
import it.polimi.ingsw.model.cardReader.cardValidator.exceptions.InvalidRuleEffectException;
import it.polimi.ingsw.model.cardReader.cardValidator.exceptions.InvalidStatementObjectException;
import it.polimi.ingsw.model.cardReader.cardValidator.exceptions.InvalidStatementSubjectException;
import it.polimi.ingsw.model.cardReader.cardValidator.validators.EffectValidator;
import it.polimi.ingsw.model.cardReader.cardValidator.validators.StatementValidator;
import it.polimi.ingsw.model.cardReader.enums.TriggerType;
import it.polimi.ingsw.model.cardReader.exceptions.InvalidCardException;

/**
 * This class offers a method to validate semantically a CardFile
 */
public class CardValidator {

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
            throw new InvalidCardException("[SUBJECT]" + e.getMessage());
        } catch (InvalidStatementObjectException e) {
            throw new InvalidCardException("[OBJECT]" + e.getMessage());
        }
        //Check Effect
        try{
            EffectValidator.checkRuleEffect(rule.getEffect());
        } catch (InvalidRuleEffectException e) {
            throw new InvalidCardException("[EFFECT]" + e.getMessage());
        }
    }

    private static void checkMixedStatements(CardRule rule) throws InvalidCardException{
        for(RuleStatement stm : rule.getStatements()){
            if (!isPlacementValid(stm,rule.getTrigger())){
                throw new InvalidCardException("[RULE]Mixed statements: '" + stm.getVerb().toString() + "' found in '" + rule.getTrigger().toString() + "'");
            }
        }
    }

    private static boolean isPlacementValid(RuleStatement stm, TriggerType ruleTrigger){
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
                    case LEVEL_TYPE:
                    case POSITION_EQUALS:
                        return true;
                    default:
                        return false;
                }
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
                    default:
                        return false;
                }
        }
        assert false;
        return false;
    }
}
