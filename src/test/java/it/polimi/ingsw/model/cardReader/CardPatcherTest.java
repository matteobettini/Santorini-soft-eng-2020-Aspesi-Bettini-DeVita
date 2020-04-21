package it.polimi.ingsw.model.cardReader;

import it.polimi.ingsw.model.cardReader.enums.*;
import it.polimi.ingsw.model.cardReader.exceptions.InvalidCardException;
import it.polimi.ingsw.model.enums.PlayerState;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CardPatcherTest {

    @Test
    void testPatchCard() {
        CardFile defaultStrategy = getDefaultStrategyExample();
        //Create test card
        CardFileImpl card = getCardExample();
        CardPatcher.patchCard(defaultStrategy,card);
        //Verify that the patched card is still valid
        try{
            CardValidator.checkCardFile(card);
        } catch (InvalidCardException e) {
            assert false;
        }

        //Check inherited statements
        for(CardRule rule : defaultStrategy.getRules()){
            if (rule.getEffect().getType() == EffectType.ALLOW){
                for(RuleStatement stm : rule.getStatements()){
                    for(CardRule cardRule : card.getRules()){
                        if(cardRule.getTrigger() == rule.getTrigger()){
                            if(cardRule.getEffect().getType() == EffectType.ALLOW){
                                boolean found = false;
                                for(RuleStatement cardStm : cardRule.getStatements()){
                                    if (cardStm.getVerb() == stm.getVerb() && cardStm.getSubject().equals(stm.getSubject())) {
                                        found = true;
                                        break;
                                    }
                                }
                                assert (found);
                                assertNotEquals(cardRule.getEffect().getNextState(), null);
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    void testNextStates(){
        CardFile defaultStrategy = getDefaultStrategyExample();
        //Create test card
        CardFileImpl card = getCardExample();
        CardPatcher.patchCard(defaultStrategy,card);
        //Check that all allow rules have a valid next state
        for(CardRule rule : card.getRules()){
            if (rule.getEffect().getType() == EffectType.WIN || rule.getEffect().getType() == EffectType.DENY){
                assertNull(rule.getEffect().getNextState());
            }else{
                assertNotNull(rule.getEffect().getNextState());
            }
        }
    }

    public static CardFileImpl getCardExample(){
        //Generate default MOVE ALLOW strategy
        List<RuleStatementImpl> statements = new ArrayList<>();
        statements.add(new RuleStatementImpl(StatementType.IF, "YOU", StatementVerbType.STATE_EQUALS, "TURN_STARTED"));
        statements.add(new RuleStatementImpl(StatementType.IF, "YOU", StatementVerbType.MOVE_LENGTH, "1"));
        RuleEffectImpl effect = new RuleEffectImpl(EffectType.ALLOW, AllowType.STANDARD,null, null);
        List<CardRuleImpl> rules = new ArrayList<>();
        rules.add(new CardRuleImpl(TriggerType.MOVE, statements,effect));
        //Generate default BUILD ALLOW strategy
        statements = new ArrayList<>();
        statements.add(new RuleStatementImpl(StatementType.IF, "YOU", StatementVerbType.STATE_EQUALS, "MOVED"));
        effect = new RuleEffectImpl(EffectType.ALLOW, AllowType.STANDARD, PlayerState.FIRST_BUILT, null);
        rules.add(new CardRuleImpl(TriggerType.BUILD, statements,effect));
        //Generate default BUILD DENY strategy
        statements = new ArrayList<>();
        statements.add(new RuleStatementImpl(StatementType.IF, "YOU", StatementVerbType.STATE_EQUALS, "MOVED"));
        effect = new RuleEffectImpl(EffectType.DENY, null);
        rules.add(new CardRuleImpl(TriggerType.BUILD, statements,effect));
        //Generate default MOVE WIN strategy
        statements = new ArrayList<>();
        statements.add(new RuleStatementImpl(StatementType.IF, "YOU", StatementVerbType.STATE_EQUALS, "MOVED"));
        effect = new RuleEffectImpl(EffectType.WIN, null);
        rules.add(new CardRuleImpl(TriggerType.MOVE, statements,effect));
        CardFileImpl cardFile = new CardFileImpl("Card Test", "", rules);
        try{
            CardValidator.checkCardFile(cardFile);
        } catch (InvalidCardException e) {
            assert false;
        }
        return cardFile;
    }

    public static CardFileImpl getDefaultStrategyExample(){
        //Generate default MOVE ALLOW strategy
        List<RuleStatementImpl> statements = new ArrayList<>();
        statements.add(new RuleStatementImpl(StatementType.IF, "YOU", StatementVerbType.STATE_EQUALS, "TURN_STARTED"));
        statements.add(new RuleStatementImpl(StatementType.IF, "YOU", StatementVerbType.MOVE_LENGTH, "1"));
        statements.add(new RuleStatementImpl(StatementType.NIF, "YOU", StatementVerbType.EXISTS_DELTA_MORE, "1"));
        RuleEffectImpl effect = new RuleEffectImpl(EffectType.ALLOW, AllowType.STANDARD, PlayerState.MOVED, null);
        List<CardRuleImpl> rules = new ArrayList<>();
        rules.add(new CardRuleImpl(TriggerType.MOVE, statements,effect));
        //Generate default BUILD ALLOW strategy
        statements = new ArrayList<>();
        statements.add(new RuleStatementImpl(StatementType.IF, "YOU", StatementVerbType.STATE_EQUALS, "MOVED"));
        statements.add(new RuleStatementImpl(StatementType.IF, "YOU", StatementVerbType.BUILD_NUM, "1"));
        effect = new RuleEffectImpl(EffectType.ALLOW, AllowType.STANDARD, PlayerState.BUILT, null);
        rules.add(new CardRuleImpl(TriggerType.BUILD, statements,effect));

        CardFileImpl defaultStrategy = new CardFileImpl("Default", "", rules);
        try{
            CardValidator.checkCardFile(defaultStrategy);
        } catch (InvalidCardException e) {
            assert false;
        }
        return defaultStrategy;
    }
}