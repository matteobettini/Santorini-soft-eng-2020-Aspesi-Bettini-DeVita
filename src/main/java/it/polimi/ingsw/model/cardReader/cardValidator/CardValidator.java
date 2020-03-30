package it.polimi.ingsw.model.cardReader.cardValidator;

import it.polimi.ingsw.model.cardReader.CardFile;
import it.polimi.ingsw.model.cardReader.CardRule;
import it.polimi.ingsw.model.cardReader.RuleStatement;
import it.polimi.ingsw.model.cardReader.cardValidator.exceptions.InvalidRuleEffectException;
import it.polimi.ingsw.model.cardReader.cardValidator.exceptions.InvalidStatementObjectException;
import it.polimi.ingsw.model.cardReader.cardValidator.exceptions.InvalidStatementSubjectException;
import it.polimi.ingsw.model.cardReader.cardValidator.validators.EffectValidator;
import it.polimi.ingsw.model.cardReader.cardValidator.validators.StatementValidator;
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
}
