package it.polimi.ingsw.model.cardReader;

import it.polimi.ingsw.model.cardReader.enums.TriggerType;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class CardFileImplTest {

    /**
     * Verify that data provided is reachable via getters
     */
    @Test
    void testGetters() {
        String nameTest = "TEST01";
        String descrTest = "DESCR01";
        List<CardRuleImpl> rules = new ArrayList<>();

        CardFile cardFile = new CardFileImpl(nameTest, descrTest, rules);
        assertEquals(cardFile.getName(), nameTest);
        assertEquals(cardFile.getDescription(), descrTest);
        assertEquals(cardFile.getRules(), rules);
    }

    /**
     * Verify that rules provided is reachable via getters
     */
    @Test
    void testRuleGetter(){
        String nameTest = "TEST01";
        String descrTest = "DESCR01";

        List<CardRuleImpl> rules = new ArrayList<>();
        rules.add(CardRuleImplTest.getEmptyCardRule());
        CardFile cardFile = new CardFileImpl(nameTest, descrTest, rules);
        assertEquals(cardFile.getRules(), rules);

        rules = CardRuleImplTest.getRandomCardRuleList();
        CardFile cardFile1 = new CardFileImpl(nameTest, descrTest, rules);
        assertEquals(cardFile1.getRules(), rules);
    }

    /**
     * Verify that rules filtering is okay
     */
    @Test
    void testRuleFiltering(){
        String nameTest = "TEST01";
        String descrTest = "DESCR01";
        List<CardRuleImpl> rules = CardRuleImplTest.getRulesWithAllTriggerTypes();

        CardFile cardFile = new CardFileImpl(nameTest,descrTest,rules);
        for(TriggerType trigger : TriggerType.values()){
            List<CardRule> correctFilteredRules = rules.stream().filter(r->r.getTrigger() == trigger).collect(Collectors.toList());
            List<CardRule> output = cardFile.getRules(trigger);
            assertEquals(correctFilteredRules, output);
        }
    }

    /**
     * Verify equals and hashcode
     */
    @Test
    void testEqualsAndHash(){
        String nameTest = "TEST01";
        String descrTest = "DESCR01";
        List<CardRuleImpl> rules = CardRuleImplTest.getRulesWithAllTriggerTypes();

        CardFile cardFile1 = new CardFileImpl(nameTest,descrTest,rules);
        CardFile cardFile2 = new CardFileImpl(nameTest,descrTest,rules);

        assertEquals(cardFile1,cardFile2);
        assertEquals(cardFile1.hashCode(), cardFile2.hashCode());
    }

    public static CardFile getNormalCardFile(){
        String nameTest = "TEST01";
        String descrTest = "NormalCard";
        List<CardRuleImpl> rules = CardRuleImplTest.getRulesWithAllTriggerTypes();
        return new CardFileImpl(nameTest,descrTest,rules);
    }
    public static CardFile getCardFileWithWrongStatementSubject(){
        String nameTest = "TEST02";
        String descrTest = "WrongStatementSubject";
        List<CardRuleImpl> rules = CardRuleImplTest.getRuleWithWrongSubject();
        return new CardFileImpl(nameTest,descrTest,rules);
    }
    public static CardFile getCardFileWithWrongStatementObject(){
        String nameTest = "TEST03";
        String descrTest = "WrongStatementObject";
        List<CardRuleImpl> rules = CardRuleImplTest.getRuleWithWrongObject();
        return new CardFileImpl(nameTest,descrTest,rules);
    }
    public static CardFile getCardFileWithWrongEffect(){
        String nameTest = "TEST04";
        String descrTest = "WrongEffect";
        List<CardRuleImpl> rules = CardRuleImplTest.getRuleWithWrongEffect();
        return new CardFileImpl(nameTest,descrTest,rules);
    }
    public static CardFile getCardFileWithMixedStatementsOnMove(){
        String nameTest = "TEST05";
        String descrTest = "MixedStatements";
        List<CardRuleImpl> rules = CardRuleImplTest.getRuleWithMixedStatementsOnMove();
        return new CardFileImpl(nameTest,descrTest,rules);
    }
    public static CardFileImpl getCardFileWithMixedStatementsOnBuild(){
        String nameTest = "TEST05";
        String descrTest = "MixedStatements";
        List<CardRuleImpl> rules = CardRuleImplTest.getRuleWithMixedStatementsOnBuild();
        return new CardFileImpl(nameTest,descrTest,rules);
    }
}