package it.polimi.ingsw.model.cardReader;

import it.polimi.ingsw.model.cardReader.enums.StatementType;
import it.polimi.ingsw.model.cardReader.enums.StatementVerbType;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RuleStatementImplTest {

    /**
     * Verify that data provided is reachable via getters
     */
    @Test
    void testGetters() {
        StatementType typeT = StatementType.IF;
        String subjT = "TESTSUBJ";
        StatementVerbType verbT = StatementVerbType.PLAYER_EQUALS;
        String objT = "TESTOBJ";

        RuleStatement ruleStatement = new RuleStatementImpl(typeT,subjT,verbT,objT);
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

        RuleStatement ruleStatement1 = new RuleStatementImpl(typeT,subjT,verbT,objT);
        RuleStatement ruleStatement2 = new RuleStatementImpl(typeT,subjT,verbT,objT);

        assertEquals(ruleStatement1,ruleStatement2);
        assertEquals(ruleStatement1.hashCode(), ruleStatement2.hashCode());
    }

    /**
     * Helpers for other tests
     */
    public static RuleStatementImpl getStatement(){
        StatementType typeT = StatementType.IF;
        String subjT = "YOU";
        StatementVerbType verbT = StatementVerbType.PLAYER_EQUALS;
        String objT = "CARD_OWNER";

        return new RuleStatementImpl(typeT,subjT,verbT,objT);
    }

    public static List<RuleStatementImpl> getBuildStatementList(){
        List<RuleStatementImpl> res = new ArrayList<>();
        res.add(new RuleStatementImpl(StatementType.IF, "YOU", StatementVerbType.PLAYER_EQUALS, "CARD_OWNER"));
        res.add(new RuleStatementImpl(StatementType.NIF, "YOU", StatementVerbType.BUILD_DOME_EXCEPT, "THIRD_FLOOR"));
        res.add(new RuleStatementImpl(StatementType.IF, "YOU", StatementVerbType.BUILD_NUM, "1"));
        return res;
    }
    public static List<RuleStatementImpl> getMoveStatementList(){
        List<RuleStatementImpl> res = new ArrayList<>();
        res.add(new RuleStatementImpl(StatementType.IF, "YOU", StatementVerbType.PLAYER_EQUALS, "CARD_OWNER"));
        res.add(new RuleStatementImpl(StatementType.NIF, "YOU", StatementVerbType.MOVE_LENGTH, "1"));
        res.add(new RuleStatementImpl(StatementType.IF, "YOU", StatementVerbType.EXISTS_DELTA_MORE, "1"));
        return res;
    }

    public static List<RuleStatementImpl> getMixedStatementOnBuildList(){
        List<RuleStatementImpl> res = new ArrayList<>();
        res.add(new RuleStatementImpl(StatementType.IF, "YOU", StatementVerbType.PLAYER_EQUALS, "CARD_OWNER"));
        res.add(new RuleStatementImpl(StatementType.NIF, "YOU", StatementVerbType.BUILD_DOME_EXCEPT, "THIRD_FLOOR"));
        res.add(new RuleStatementImpl(StatementType.NIF, "YOU", StatementVerbType.EXISTS_DELTA_MORE, "1"));
        res.add(new RuleStatementImpl(StatementType.IF, "YOU", StatementVerbType.BUILD_NUM, "1"));
        return res;
    }
    public static List<RuleStatementImpl> getMixedStatementOnMoveList(){
        List<RuleStatementImpl> res = new ArrayList<>();
        res.add(new RuleStatementImpl(StatementType.IF, "YOU", StatementVerbType.PLAYER_EQUALS, "CARD_OWNER"));
        res.add(new RuleStatementImpl(StatementType.NIF, "YOU", StatementVerbType.BUILD_DOME_EXCEPT, "THIRD_FLOOR"));
        res.add(new RuleStatementImpl(StatementType.NIF, "YOU", StatementVerbType.EXISTS_DELTA_MORE, "1"));
        res.add(new RuleStatementImpl(StatementType.IF, "YOU", StatementVerbType.MOVE_LENGTH, "1"));
        return res;
    }

    public static RuleStatementImpl getStatement(String subj, StatementVerbType verb, String obj){
        return new RuleStatementImpl(StatementType.IF, subj,verb,obj);
    }

    public static List<RuleStatementImpl> getStatementsWithWrongSubject(){
        List<RuleStatementImpl> res = new ArrayList<>();
        res.add(new RuleStatementImpl(StatementType.IF, "TTTT", StatementVerbType.PLAYER_EQUALS, "CARD_OWNER"));
        return res;
    }
    public static List<RuleStatementImpl> getStatementsWithWrongObject(){
        List<RuleStatementImpl> res = new ArrayList<>();
        res.add(new RuleStatementImpl(StatementType.IF, "YOU", StatementVerbType.PLAYER_EQUALS, "TTTT"));
        return res;
    }

}