package it.polimi.ingsw.model.cardReader;

import it.polimi.ingsw.model.cardReader.enums.TriggerType;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class CardFileTest {

    /**
     * Verify that data provided is reachable via getters
     */
    @Test
    void testGetters() {
        String nameTest = "TEST01";
        String descrTest = "DESCR01";
        List<CardRule> rules = new ArrayList<>();

        CardFile cardFile = new CardFile(nameTest, descrTest, rules);
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

        List<CardRule> rules = new ArrayList<>();
        rules.add(CardRuleTest.getEmptyCardRule());
        CardFile cardFile = new CardFile(nameTest, descrTest, rules);
        assertEquals(cardFile.getRules(), rules);

        rules = CardRuleTest.getRandomCardRuleList();
        CardFile cardFile1 = new CardFile(nameTest, descrTest, rules);
        assertEquals(cardFile1.getRules(), rules);
    }

    /**
     * Verify that rules filtering is okay
     */
    @Test
    void testRuleFiltering(){
        String nameTest = "TEST01";
        String descrTest = "DESCR01";
        List<CardRule> rules = CardRuleTest.getRulesWithAllTriggerTypes();

        CardFile cardFile = new CardFile(nameTest,descrTest,rules);
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
        List<CardRule> rules = CardRuleTest.getRulesWithAllTriggerTypes();

        CardFile cardFile1 = new CardFile(nameTest,descrTest,rules);
        CardFile cardFile2 = new CardFile(nameTest,descrTest,rules);

        assertEquals(cardFile1,cardFile2);
        assertEquals(cardFile1.hashCode(), cardFile2.hashCode());
    }

    public static CardFile getNormalCardFile(){
        String nameTest = "TEST01";
        String descrTest = "DESCR01";
        List<CardRule> rules = CardRuleTest.getRulesWithAllTriggerTypes();
        return new CardFile(nameTest,descrTest,rules);
    }
    public static CardFile getCardFileWithWrongStatementSubject(){
        String nameTest = "TEST01";
        String descrTest = "DESCR01";
        List<CardRule> rules = CardRuleTest.getRuleWithWrongSubject();
        return new CardFile(nameTest,descrTest,rules);
    }
    public static CardFile getCardFileWithWrongStatementObject(){
        String nameTest = "TEST01";
        String descrTest = "DESCR01";
        List<CardRule> rules = CardRuleTest.getRuleWithWrongObject();
        return new CardFile(nameTest,descrTest,rules);
    }
    public static CardFile getCardFileWithWrongEffect(){
        String nameTest = "TEST01";
        String descrTest = "DESCR01";
        List<CardRule> rules = CardRuleTest.getRuleWithWrongEffect();
        return new CardFile(nameTest,descrTest,rules);
    }
}