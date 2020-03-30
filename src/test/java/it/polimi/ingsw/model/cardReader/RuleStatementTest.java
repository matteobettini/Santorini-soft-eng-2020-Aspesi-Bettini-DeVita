package it.polimi.ingsw.model.cardReader;

import it.polimi.ingsw.model.cardReader.enums.StatementType;
import it.polimi.ingsw.model.cardReader.enums.StatementVerbType;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RuleStatementTest {

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

    /**
     * Helpers for other tests
     */
    public static RuleStatement getStatement(){
        StatementType typeT = StatementType.IF;
        String subjT = "YOU";
        StatementVerbType verbT = StatementVerbType.PLAYER_EQUALS;
        String objT = "CARD_OWNER";

        return new RuleStatement(typeT,subjT,verbT,objT);
    }

    public static List<RuleStatement> getStatementList(){
        List<RuleStatement> res = new ArrayList<>();
        res.add(new RuleStatement(StatementType.IF, "YOU", StatementVerbType.PLAYER_EQUALS, "CARD_OWNER"));
        res.add(new RuleStatement(StatementType.NIF, "YOU", StatementVerbType.BUILD_DOME_EXCEPT, "THIRD_FLOOR"));
        res.add(new RuleStatement(StatementType.NIF, "YOU", StatementVerbType.EXISTS_DELTA_MORE, "1"));
        res.add(new RuleStatement(StatementType.IF, "YOU", StatementVerbType.BUILD_NUM, "1"));
        return res;
    }

    public static RuleStatement getStatement(String subj, StatementVerbType verb, String obj){
        return new RuleStatement(StatementType.IF, subj,verb,obj);
    }

    public static List<RuleStatement> getStatementsWithWrongSubject(){
        List<RuleStatement> res = new ArrayList<>();
        res.add(new RuleStatement(StatementType.IF, "TTTT", StatementVerbType.PLAYER_EQUALS, "CARD_OWNER"));
        return res;
    }
    public static List<RuleStatement> getStatementsWithWrongObject(){
        List<RuleStatement> res = new ArrayList<>();
        res.add(new RuleStatement(StatementType.IF, "YOU", StatementVerbType.PLAYER_EQUALS, "TTTT"));
        return res;
    }
}