package it.polimi.ingsw.model.cardReader;

import it.polimi.ingsw.model.cardReader.enums.TriggerType;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CardRuleTest {
    /**
     * Verify that data provided is reachable via getters
     */
    @Test
    void testGetters() {
        TriggerType triggerT = TriggerType.MOVE;
        List<RuleStatement> statementsT = RuleStatementTest.getMoveStatementList();
        RuleEffect effectT = RuleEffectTest.getRuleEffectWithData();

        CardRule rule = new CardRule(triggerT,statementsT,effectT);
        assertEquals(rule.getTrigger(), triggerT);
        assertEquals(rule.getStatements(), statementsT);
        assertEquals(rule.getEffect(), effectT);
    }

    /**
     * Verify that a new statement added to an empty card rule is reachable
     */
    @Test
    void testAddStatementToEmpty(){
        CardRule cardRule = getEmptyCardRule();
        assertEquals(cardRule.getStatements().size(), 0);
        RuleStatement statement = RuleStatementTest.getStatement();
        cardRule.addStatement(statement);
        assertEquals(cardRule.getStatements().size(),1);
        assertTrue(cardRule.getStatements().contains(statement));
    }
    /**
     * Verify that a new statement added to an not empty card rule is reachable
     */
    @Test
    void testAddStatement(){
        CardRule cardRule = getCardRule();
        int oldSize = cardRule.getStatements().size();
        RuleStatement statement = RuleStatementTest.getStatement();
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
        List<RuleStatement> statementsT = RuleStatementTest.getMoveStatementList();
        RuleEffect effectT = RuleEffectTest.getRuleEffectWithData();

        CardRule rule1 = new CardRule(triggerT,statementsT,effectT);
        CardRule rule2 = new CardRule(triggerT,statementsT,effectT);

        assertEquals(rule1,rule2);
        assertEquals(rule1.hashCode(), rule2.hashCode());
    }

    /**
     * Helpers t
     */
    public static CardRule getEmptyCardRule(){
        TriggerType triggerT = TriggerType.MOVE;
        List<RuleStatement> statementsT = new ArrayList<>();
        RuleEffect effect = RuleEffectTest.getRuleEffectWithNullData();
        return new CardRule(triggerT,statementsT,effect);
    }
    public static CardRule getCardRule(){
        TriggerType triggerT = TriggerType.MOVE;
        List<RuleStatement> statementsT = new ArrayList<>(RuleStatementTest.getMoveStatementList());
        RuleEffect effect = RuleEffectTest.getRuleEffectWithData();

        return new CardRule(triggerT,statementsT,effect);
    }

    public static List<CardRule> getRandomCardRuleList(){
        List<CardRule> res = new ArrayList<>();
        res.add(new CardRule(TriggerType.MOVE, RuleStatementTest.getMoveStatementList(), RuleEffectTest.getRuleEffectWithData()));
        res.add(new CardRule(TriggerType.MOVE, RuleStatementTest.getMoveStatementList(), RuleEffectTest.getRuleEffectWithNullData()));
        return res;
    }

    public static List<CardRule> getRulesWithAllTriggerTypes(){
        List<CardRule> res = new ArrayList<>();
        res.add(new CardRule(TriggerType.BUILD, RuleStatementTest.getBuildStatementList(), RuleEffectTest.getRuleEffectWithData()));
        res.add(new CardRule(TriggerType.MOVE, RuleStatementTest.getMoveStatementList(), RuleEffectTest.getRuleEffectWithNullData()));
        res.add(new CardRule(TriggerType.BUILD, RuleStatementTest.getBuildStatementList(), RuleEffectTest.getRuleEffectWithNullData()));
        res.add(new CardRule(TriggerType.MOVE, RuleStatementTest.getMoveStatementList(), RuleEffectTest.getRuleEffectWithData()));
        res.add(new CardRule(TriggerType.MOVE, RuleStatementTest.getMoveStatementList(), RuleEffectTest.getRuleEffectWithData()));
        return res;
    }

    public static List<CardRule> getRuleWithWrongSubject(){
        List<CardRule> res = new ArrayList<>();
        res.add(new CardRule(TriggerType.MOVE, RuleStatementTest.getStatementsWithWrongSubject(), RuleEffectTest.getRuleEffectWithNullData()));
        return res;
    }
    public static List<CardRule> getRuleWithWrongObject(){
        List<CardRule> res = new ArrayList<>();
        res.add(new CardRule(TriggerType.MOVE, RuleStatementTest.getStatementsWithWrongObject(), RuleEffectTest.getRuleEffectWithNullData()));
        return res;
    }
    public static List<CardRule> getRuleWithWrongEffect(){
        List<CardRule> res = new ArrayList<>();
        res.add(new CardRule(TriggerType.BUILD, RuleStatementTest.getBuildStatementList(), RuleEffectTest.getRuleEffectWithWrongData()));
        return res;
    }
    public static List<CardRule> getRuleWithMixedStatementsOnMove(){
        List<CardRule> res = new ArrayList<>();
        res.add(new CardRule(TriggerType.MOVE, RuleStatementTest.getMixedStatementOnMoveList(), RuleEffectTest.getRuleEffectWithNullData()));
        return res;
    }
    public static List<CardRule> getRuleWithMixedStatementsOnBuild(){
        List<CardRule> res = new ArrayList<>();
        res.add(new CardRule(TriggerType.BUILD, RuleStatementTest.getMixedStatementOnBuildList(), RuleEffectTest.getRuleEffectWithNullData()));
        return res;
    }
}