package it.polimi.ingsw.cardReader.cardValidator;

import it.polimi.ingsw.cardReader.CardFile;
import it.polimi.ingsw.cardReader.CardRule;
import it.polimi.ingsw.cardReader.RuleStatement;
import it.polimi.ingsw.cardReader.enums.EffectType;
import it.polimi.ingsw.cardReader.enums.TriggerType;

import java.util.List;

/**
 * This class offers a method to patch CardFile with
 * part of the default strategy
 */
public class CardPatcher {

    /**
     * This method adds missing statements/parts of the specified CardFile using
     * the default strategy
     * @param defaultCard CardFile of the default strategy
     * @param card CardFile of the card to be patched
     */
    public void patchCard(CardFile defaultCard, CardFile card){
        for(CardRule rule : defaultCard.getRules()){
            if (isPatchableCompliant(rule)){
                for(RuleStatement stm : rule.getStatements()){
                    applyStatementToCardRule(rule.getTrigger(), stm, card);
                }
            }
        }
    }

    /**
     * This method patches all card rules with same trigger, adding missing default statement.
     * This operation is done only for compliant statements (ALLOW, SET_OPPONENT_POSITION statements)
     * @param triggerDefault Default card rule trigger
     * @param stmDefault Default statement to add
     * @param card CardFile where we want to add the statement
     */
    private void applyStatementToCardRule(TriggerType triggerDefault, RuleStatement stmDefault, CardFile card){
        List<CardRule> similarRules = card.getRules(triggerDefault);
        for(CardRule rule : similarRules){
            if (isPatchableCompliant(rule)){
                if (!containsDefaultStatement(stmDefault, rule)){
                    rule.addStatement(stmDefault);
                }
            }
        }
    }

    /**
     * This method searches for the statement in a CardRule
     * @param stmDefault Default statement to be searched
     * @param rule Rule where to search
     * @return True if the statement is found, False otherwise
     */
    private boolean containsDefaultStatement(RuleStatement stmDefault, CardRule rule){
        for(RuleStatement stm : rule.getStatements()){
            if (stm.getSubject().equals(stmDefault.getSubject()) && stm.getVerb().equals(stmDefault.getVerb())){
                return true;
            }
        }
        return false;
    }

    /**
     * This methods returns true if the rule can be patched/can be used to patch
     * @param rule CardRule to analyse
     * @return True if the rule can be patched/can be used to patch, False otherwise
     */
    private boolean isPatchableCompliant(CardRule rule){
        return rule.getEffect().getType() == EffectType.ALLOW || rule.getEffect().getType() == EffectType.SET_OPPONENT_POSITION;
    }
}
