package it.polimi.ingsw.model.cardReader.cardValidator;

import it.polimi.ingsw.model.cardReader.CardFile;
import it.polimi.ingsw.model.cardReader.CardRule;
import it.polimi.ingsw.model.cardReader.RuleEffect;
import it.polimi.ingsw.model.cardReader.RuleStatement;
import it.polimi.ingsw.model.cardReader.enums.EffectType;
import it.polimi.ingsw.model.cardReader.enums.TriggerType;
import it.polimi.ingsw.model.enums.PlayerState;

import java.util.List;

/**
 * This class offers a method to patch CardFile with
 * part of the default strategy.
 * Default cardFile must have ONE AND ONLY ONE:
 * - move rule with allow
 * - build rule with allow
 */
public class CardPatcher {

    /**
     * This method adds missing statements/parts of the specified CardFile using
     * the default strategy
     * @param defaultCard CardFile of the default strategy
     * @param card CardFile of the card to be patched
     */
    public void patchCard(CardFile defaultCard, CardFile card){
        assert(defaultCard.getRules(TriggerType.MOVE).stream().filter(this::isPatchableCompliant).count() == 1);
        assert(defaultCard.getRules(TriggerType.BUILD).stream().filter(this::isPatchableCompliant).count() == 1);

        for(CardRule rule : defaultCard.getRules()){
            if (isPatchableCompliant(rule)){
                //Add its statements where missing
                for(RuleStatement stm : rule.getStatements()){
                    applyStatementToCardRule(rule.getTrigger(), stm, card);
                }
                //Add its default next state where missing
                applyNextStateToCardFile(rule.getTrigger(),rule.getEffect().getNextState(),card);
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
     * This method patches the cardFile adding default player next state in the compatible rules where was not specified.
     * @param triggerDefault Default card rule trigger
     * @param nextStateDefault Default next state to be added
     * @param card CardFile where we want to add the next state
     */
    private void applyNextStateToCardFile(TriggerType triggerDefault, PlayerState nextStateDefault, CardFile card){
        List<CardRule> similarRules = card.getRules(triggerDefault);
        for(CardRule rule : similarRules){
            if (isPatchableCompliant(rule)){
                applyNextStateToCardRule(nextStateDefault, rule);
            }
        }
    }

    /**
     * This method patches the card player next state with the default, if was not specified
     * @param defaultNextState Default player next state
     * @param rule Rule to be patched
     */
    private void applyNextStateToCardRule(PlayerState defaultNextState, CardRule rule){
        RuleEffect ruleEffect = rule.getEffect();
        if (ruleEffect.getNextState() == PlayerState.UNKNOWN){
            ruleEffect.setNextState(defaultNextState);
        }
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
