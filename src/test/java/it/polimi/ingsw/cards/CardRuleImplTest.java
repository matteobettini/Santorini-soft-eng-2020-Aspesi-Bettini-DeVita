package it.polimi.ingsw.cards;

import it.polimi.ingsw.cards.enums.TriggerType;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CardRuleImplTest {
    /**
     * Verify that data provided is reachable via getters
     */
    @Test
    void testGetters() {
        TriggerType triggerT = TriggerType.MOVE;
        List<RuleStatementImpl> statementsT = RuleStatementImplTest.getMoveStatementList();
        RuleEffectImpl effectT = RuleEffectImplTest.getRuleEffectWithData();

        CardRuleImpl rule = new CardRuleImpl(triggerT,statementsT,effectT);
        assertEquals(rule.getTrigger(), triggerT);
        assertEquals(rule.getStatements(), statementsT);
        assertEquals(rule.getStatementsInternal(), statementsT);
        assertEquals(rule.getEffect(), effectT);
    }

    /**
     * Verify that a new statement added to an empty card rule is reachable
     */
    @Test
    void testAddStatementToEmpty(){
        CardRuleImpl cardRule = getEmptyCardRule();
        assertEquals(cardRule.getStatements().size(), 0);
        RuleStatement statement = RuleStatementImplTest.getStatement();
        cardRule.addStatement(statement);
        assertEquals(cardRule.getStatements().size(),1);
        assertTrue(cardRule.getStatements().contains(statement));
    }
    /**
     * Verify that a new statement added to an not empty card rule is reachable
     */
    @Test
    void testAddStatement(){
        CardRuleImpl cardRule = getCardRule();
        int oldSize = cardRule.getStatements().size();
        RuleStatement statement = RuleStatementImplTest.getStatement();
        cardRule.addStatement(statement);
        assertEquals(cardRule.getStatements().size(),oldSize + 1);
        assertTrue(cardRule.getStatements().contains(statement));
    }

    /**
     * Verify equals and hashcode
     */
    @Test
    void testEqualsAndHash(){
        TriggerType triggerT = TriggerType.MOVE;
        List<RuleStatementImpl> statementsT = RuleStatementImplTest.getMoveStatementList();
        RuleEffectImpl effectT = RuleEffectImplTest.getRuleEffectWithData();

        CardRule rule1 = new CardRuleImpl(triggerT,statementsT,effectT);
        CardRule rule2 = new CardRuleImpl(triggerT,statementsT,effectT);

        assertEquals(rule1,rule2);
        assertEquals(rule1.hashCode(), rule2.hashCode());
    }

    /**
     * Helpers t
     */
    public static CardRuleImpl getEmptyCardRule(){
        TriggerType triggerT = TriggerType.MOVE;
        List<RuleStatementImpl> statementsT = new ArrayList<>();
        RuleEffectImpl effect = RuleEffectImplTest.getRuleEffectWithNullData();
        return new CardRuleImpl(triggerT,statementsT,effect);
    }
    public static CardRuleImpl getCardRule(){
        TriggerType triggerT = TriggerType.MOVE;
        List<RuleStatementImpl> statementsT = new ArrayList<>(RuleStatementImplTest.getMoveStatementList());
        RuleEffectImpl effect = RuleEffectImplTest.getRuleEffectWithData();

        return new CardRuleImpl(triggerT,statementsT,effect);
    }

    public static List<CardRuleImpl> getRandomCardRuleList(){
        List<CardRuleImpl> res = new ArrayList<>();
        res.add(new CardRuleImpl(TriggerType.MOVE, RuleStatementImplTest.getMoveStatementList(), RuleEffectImplTest.getRuleEffectWithData()));
        res.add(new CardRuleImpl(TriggerType.MOVE, RuleStatementImplTest.getMoveStatementList(), RuleEffectImplTest.getRuleEffectWithNullData()));
        return res;
    }

    public static List<CardRuleImpl> getRulesWithAllTriggerTypes(){
        List<CardRuleImpl> res = new ArrayList<>();
        res.add(new CardRuleImpl(TriggerType.BUILD, RuleStatementImplTest.getBuildStatementList(), RuleEffectImplTest.getRuleEffectWithNullData()));
        res.add(new CardRuleImpl(TriggerType.MOVE, RuleStatementImplTest.getMoveStatementList(), RuleEffectImplTest.getRuleEffectWithNullData()));
        res.add(new CardRuleImpl(TriggerType.BUILD, RuleStatementImplTest.getBuildStatementList(), RuleEffectImplTest.getRuleEffectWithNullData()));
        res.add(new CardRuleImpl(TriggerType.MOVE, RuleStatementImplTest.getMoveStatementList(), RuleEffectImplTest.getRuleEffectWithData()));
        res.add(new CardRuleImpl(TriggerType.MOVE, RuleStatementImplTest.getMoveStatementList(), RuleEffectImplTest.getRuleEffectWithData()));
        return res;
    }

    public static List<CardRuleImpl> getRuleWithWrongSubject(){
        List<CardRuleImpl> res = new ArrayList<>();
        res.add(new CardRuleImpl(TriggerType.MOVE, RuleStatementImplTest.getStatementsWithWrongSubject(), RuleEffectImplTest.getRuleEffectWithNullData()));
        return res;
    }
    public static List<CardRuleImpl> getRuleWithWrongObject(){
        List<CardRuleImpl> res = new ArrayList<>();
        res.add(new CardRuleImpl(TriggerType.MOVE, RuleStatementImplTest.getStatementsWithWrongObject(), RuleEffectImplTest.getRuleEffectWithNullData()));
        return res;
    }
    public static List<CardRuleImpl> getRuleWithWrongEffect(){
        List<CardRuleImpl> res = new ArrayList<>();
        res.add(new CardRuleImpl(TriggerType.BUILD, RuleStatementImplTest.getBuildStatementList(), RuleEffectImplTest.getRuleEffectWithWrongData()));
        return res;
    }
    public static List<CardRuleImpl> getRuleWithMixedStatementsOnMove(){
        List<CardRuleImpl> res = new ArrayList<>();
        res.add(new CardRuleImpl(TriggerType.MOVE, RuleStatementImplTest.getMixedStatementOnMoveList(), RuleEffectImplTest.getRuleEffectWithNullData()));
        return res;
    }
    public static List<CardRuleImpl> getRuleWithMixedStatementsOnBuild(){
        List<CardRuleImpl> res = new ArrayList<>();
        res.add(new CardRuleImpl(TriggerType.BUILD, RuleStatementImplTest.getMixedStatementOnBuildList(), RuleEffectImplTest.getRuleEffectWithNullData()));
        return res;
    }
    public static List<CardRuleImpl> getRuleWithMixedAllowSubtypesOnBuild(){
        List<CardRuleImpl> res = new ArrayList<>();
        res.add(new CardRuleImpl(TriggerType.BUILD, RuleStatementImplTest.getBuildStatementList(), RuleEffectImplTest.getRuleEffectWithWrongSubtype()));
        return res;
    }

    public static CardRuleImpl getRule(TriggerType triggerType, List<RuleStatementImpl> statements, RuleEffectImpl effect){
        return new CardRuleImpl(triggerType,statements,effect);
    }
}