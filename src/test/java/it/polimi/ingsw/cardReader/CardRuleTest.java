package it.polimi.ingsw.cardReader;

import it.polimi.ingsw.cardReader.enums.TriggerType;
import it.polimi.ingsw.cardReader.helpers.EffectHelper;
import it.polimi.ingsw.cardReader.helpers.RuleHelper;
import it.polimi.ingsw.cardReader.helpers.StatementHelper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CardRuleTest {
    /**
     * Verify that data provided is reachable via getters
     */
    @Test
    void testGetters() {
        TriggerType triggerT = TriggerType.MOVE;
        List<RuleStatement> statementsT = StatementHelper.getStatementList();
        RuleEffect effectT = EffectHelper.getRuleEffect();

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
        CardRule cardRule = RuleHelper.getEmptyCardRule();
        assertEquals(cardRule.getStatements().size(), 0);
        RuleStatement statement = StatementHelper.getStatement();
        cardRule.addStatement(statement);
        assertEquals(cardRule.getStatements().size(),1);
        assertTrue(cardRule.getStatements().contains(statement));
    }

    /**
     * Verify equals and hashcode
     */
    @Test
    void testEqualsAndHash(){
        TriggerType triggerT = TriggerType.MOVE;
        List<RuleStatement> statementsT = StatementHelper.getStatementList();
        RuleEffect effectT = EffectHelper.getRuleEffect();

        CardRule rule1 = new CardRule(triggerT,statementsT,effectT);
        CardRule rule2 = new CardRule(triggerT,statementsT,effectT);

        assertEquals(rule1,rule2);
        assertEquals(rule1.hashCode(), rule2.hashCode());
    }
}