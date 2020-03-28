package it.polimi.ingsw.cardReader;

import it.polimi.ingsw.cardReader.enums.StatementType;
import it.polimi.ingsw.cardReader.enums.StatementVerbType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RuleStatementTest {

    /**
     * Verify that data provided is reachable via getters
     */
    @Test
    void testGetters() {
        StatementType typeT = StatementType.IF;
        String subjT = "TESTSUBJ";
        StatementVerbType verbT = StatementVerbType.PLAYER_EQUALS;
        String objT = "TESTOBJ";

        RuleStatement ruleStatement = new RuleStatement(typeT,subjT,verbT,objT);
        assertEquals(ruleStatement.getType(), typeT);
        assertEquals(ruleStatement.getSubject(), subjT);
        assertEquals(ruleStatement.getVerb(), verbT);
        assertEquals(ruleStatement.getObject(), objT);
    }

    /**
     * Test equals and hash
     */
    @Test
    void testEqualsAndHash(){
        StatementType typeT = StatementType.IF;
        String subjT = "TESTSUBJ";
        StatementVerbType verbT = StatementVerbType.PLAYER_EQUALS;
        String objT = "TESTOBJ";

        RuleStatement ruleStatement1 = new RuleStatement(typeT,subjT,verbT,objT);
        RuleStatement ruleStatement2 = new RuleStatement(typeT,subjT,verbT,objT);

        assertEquals(ruleStatement1,ruleStatement2);
        assertEquals(ruleStatement1.hashCode(), ruleStatement2.hashCode());
    }
}