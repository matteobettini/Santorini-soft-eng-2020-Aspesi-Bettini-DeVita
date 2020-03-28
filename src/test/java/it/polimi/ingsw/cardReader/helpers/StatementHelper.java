package it.polimi.ingsw.cardReader.helpers;

import it.polimi.ingsw.cardReader.CardRule;
import it.polimi.ingsw.cardReader.RuleStatement;
import it.polimi.ingsw.cardReader.enums.StatementType;
import it.polimi.ingsw.cardReader.enums.StatementVerbType;
import it.polimi.ingsw.cardReader.enums.TriggerType;

import java.util.ArrayList;
import java.util.List;

public class StatementHelper {

    public static RuleStatement getStatement(){
        StatementType typeT = StatementType.IF;
        String subjT = "TESTSUBJ";
        StatementVerbType verbT = StatementVerbType.PLAYER_EQUALS;
        String objT = "TESTOBJ";

        return new RuleStatement(typeT,subjT,verbT,objT);
    }

    public static List<RuleStatement> getStatementList(){
        List<RuleStatement> res = new ArrayList<>();
        res.add(new RuleStatement(StatementType.IF, "TEST", StatementVerbType.POSITION_EQUALS, "TEST2"));
        res.add(new RuleStatement(StatementType.NIF, "TEST1", StatementVerbType.BUILD_DOME_EXCEPT, "TEST2"));
        res.add(new RuleStatement(StatementType.NIF, "TEST2", StatementVerbType.EXISTS_DELTA_MORE, "TEST1"));
        res.add(new RuleStatement(StatementType.IF, "TEST3", StatementVerbType.BUILD_NUM, "TEST3"));
        return res;
    }
}
