package it.polimi.ingsw.server.cards;

import it.polimi.ingsw.server.cards.enums.AllowType;
import it.polimi.ingsw.server.cards.enums.EffectType;
import it.polimi.ingsw.server.cards.enums.TriggerType;
import it.polimi.ingsw.server.model.enums.PlayerState;

import java.util.List;

/**
 * This class offers a method to patch CardFile with
 * part of the default strategy.
 * Default cardFile must have ONE AND ONLY ONE:
 * - move rule with allow
 * - build rule with allow
 */
class CardPatcher {

    /**
     * This method adds missing statements/parts of the specified CardFile using
     * the default strategy
     * @param defaultCard CardFile of the default strategy
     * @param card CardFile of the card to be patched
     */
    public static void patchCard(CardFile defaultCard, CardFileImpl card){
        assert(defaultCard.getRules(TriggerType.MOVE).stream().filter(CardPatcher::canBeUsedToPatch).count() == 1);
        assert(defaultCard.getRules(TriggerType.BUILD).stream().filter(CardPatcher::canBeUsedToPatch).count() == 1);

        for(CardRule rule : defaultCard.getRules()){
            if (canBeUsedToPatch(rule)){
                //Add its statements where missing
                for(RuleStatement stm : rule.getStatements()){
                    applyStatementToCardRule(rule.getTrigger(), stm, card);
                }
                //Add its default allow type where missing
                applySubtypeToCardFile(rule.getTrigger(), rule.getEffect().getAllowType(), card);
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
    private static void applyStatementToCardRule(TriggerType triggerDefault, RuleStatement stmDefault, CardFileImpl card){
        List<CardRuleImpl> similarRules = card.getRulesInternal(triggerDefault);
        for(CardRuleImpl rule : similarRules){
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
    private static boolean containsDefaultStatement(RuleStatement stmDefault, CardRule rule){
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
    private static void applyNextStateToCardFile(TriggerType triggerDefault, PlayerState nextStateDefault, CardFileImpl card){
        List<CardRuleImpl> similarRules = card.getRulesInternal(triggerDefault);
        for(CardRuleImpl rule : similarRules){
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
    private static void applyNextStateToCardRule(PlayerState defaultNextState, CardRuleImpl rule){
        RuleEffectImpl ruleEffect = rule.getEffectInternal();
        if (ruleEffect.getNextState() == null){
            ruleEffect.setNextState(defaultNextState);
        }
    }

    /**
     * This method patches the cardFile allow subtype in the compatible rules where was not specified.
     * @param triggerDefault Default card rule trigger
     * @param typeDefault Default allow subtype
     * @param card CardFile where we want to add the subtype
     */
    private static void applySubtypeToCardFile(TriggerType triggerDefault, AllowType typeDefault, CardFileImpl card){
        List<CardRuleImpl> similarRules = card.getRulesInternal(triggerDefault);
        for(CardRuleImpl rule : similarRules){
            if (isPatchableCompliant(rule)){
                applySubtypeToCardRule(typeDefault, rule);
            }
        }
    }

    /**
     * This method patches the cardRule allow subtype in the compatible rules where was not specified.
     * @param typeDefault Default allow subtype
     * @param rule Rule to be patched
     */
    private static void applySubtypeToCardRule(AllowType typeDefault, CardRuleImpl rule){
        RuleEffectImpl ruleEffect = rule.getEffectInternal();
        if (ruleEffect.getAllowType() == null){
            ruleEffect.setAllowType(typeDefault);
        }
    }

    /**
     * This methods returns true if the rule can be patched be used to patch
     * @param rule CardRule to analyse
     * @return True if the rule can be patched, False otherwise
     */
    private static boolean isPatchableCompliant(CardRule rule){
        return rule.getEffect().getType() == EffectType.ALLOW;
    }
    /**
     * This methods returns true if the rule can be used to patch
     * @param rule CardRule to analyse
     * @return True if the rule can be used to patch, False otherwise
     */
    private static boolean canBeUsedToPatch(CardRule rule){
        return rule.getEffect().getType() == EffectType.ALLOW && rule.getEffect().getAllowType() == AllowType.STANDARD;
    }
}
