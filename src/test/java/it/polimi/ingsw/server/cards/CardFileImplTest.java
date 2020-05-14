package it.polimi.ingsw.server.cards;

import it.polimi.ingsw.server.cards.enums.*;
import it.polimi.ingsw.server.cards.exceptions.InvalidCardException;
import it.polimi.ingsw.server.model.enums.PlayerState;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedList;
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

        CardFileImpl cardFile = new CardFileImpl(nameTest, descrTest, rules);
        assertEquals(cardFile.getName(), nameTest);
        assertEquals(cardFile.getDescription(), descrTest);
        assertEquals(cardFile.getRules(), rules);
        assertEquals(cardFile.getRulesInternal(), rules);
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

        CardFileImpl cardFile = new CardFileImpl(nameTest,descrTest,rules);
        for(TriggerType trigger : TriggerType.values()){
            List<CardRule> correctFilteredRules = rules.stream().filter(r->r.getTrigger() == trigger).collect(Collectors.toList());
            assertEquals(correctFilteredRules, cardFile.getRules(trigger));
            assertEquals(correctFilteredRules, cardFile.getRulesInternal(trigger));
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

    public static CardFileImpl getAllowOpponents(TriggerType triggerType, PlayerState state) {
        String nameTest = "TEST06";
        String descrTest = "AllowOpponents";
        List<RuleStatementImpl> statements = new LinkedList<>();
        RuleStatementImpl stmt = RuleStatementImplTest.getStatement(StatementType.NIF, "YOU", StatementVerbType.PLAYER_EQUALS, "CARD_OWNER");
        statements.add(stmt);
        stmt = RuleStatementImplTest.getStatement(StatementType.IF, "YOU", StatementVerbType.STATE_EQUALS, state.name());
        statements.add(stmt);
        RuleEffectImpl effect = RuleEffectImplTest.getRuleEffect(EffectType.ALLOW, AllowType.STANDARD, PlayerState.MOVED, null);
        List<CardRuleImpl> rules = new LinkedList<>();
        rules.add(CardRuleImplTest.getRule(triggerType, statements, effect));
        CardFileImpl cardFile = new CardFileImpl(nameTest,descrTest,rules);
        try{
            CardValidator.checkCardFile(cardFile);
        } catch (InvalidCardException e) {
            assert false;
        }
        return cardFile;
    }
    public static CardFileImpl getAllowOpponents(TriggerType triggerType) {
        String nameTest = "TEST07";
        String descrTest = "AllowOpponents";
        List<RuleStatementImpl> statements = new LinkedList<>();
        RuleStatementImpl stmt = RuleStatementImplTest.getStatement(StatementType.NIF, "YOU", StatementVerbType.PLAYER_EQUALS, "CARD_OWNER");
        statements.add(stmt);
        RuleEffectImpl effect = RuleEffectImplTest.getRuleEffect(EffectType.ALLOW, AllowType.STANDARD, PlayerState.MOVED, null);
        List<CardRuleImpl> rules = new LinkedList<>();
        rules.add(CardRuleImplTest.getRule(triggerType, statements, effect));
        CardFileImpl cardFile = new CardFileImpl(nameTest,descrTest,rules);
        try{
            CardValidator.checkCardFile(cardFile);
        } catch (InvalidCardException e) {
            assert false;
        }
        return cardFile;
    }
    public static CardFileImpl getAllowOpponentsAllStatesExcluded(TriggerType triggerType, PlayerState state) {
        String nameTest = "TEST08";
        String descrTest = "AllowNegatedState";
        List<RuleStatementImpl> statements = new LinkedList<>();
        RuleStatementImpl stmt = RuleStatementImplTest.getStatement(StatementType.NIF, "YOU", StatementVerbType.PLAYER_EQUALS, "CARD_OWNER");
        statements.add(stmt);
        stmt = RuleStatementImplTest.getStatement(StatementType.NIF, "YOU", StatementVerbType.STATE_EQUALS, state.name());
        statements.add(stmt);
        RuleEffectImpl effect = RuleEffectImplTest.getRuleEffect(EffectType.ALLOW, AllowType.STANDARD, PlayerState.MOVED, null);
        List<CardRuleImpl> rules = new LinkedList<>();
        rules.add(CardRuleImplTest.getRule(triggerType, statements, effect));
        CardFileImpl cardFile = new CardFileImpl(nameTest,descrTest,rules);
        try{
            CardValidator.checkCardFile(cardFile);
        } catch (InvalidCardException e) {
            assert false;
        }
        return cardFile;
    }
    public static CardFileImpl getAllowAllStatesExcluded(TriggerType triggerType, PlayerState state) {
        String nameTest = "TEST09";
        String descrTest = "AllowAllStatesExcluded";
        List<RuleStatementImpl> statements = new LinkedList<>();
        RuleStatementImpl stmt = RuleStatementImplTest.getStatement(StatementType.IF, "YOU", StatementVerbType.PLAYER_EQUALS, "CARD_OWNER");
        statements.add(stmt);
        stmt = RuleStatementImplTest.getStatement(StatementType.NIF, "YOU", StatementVerbType.STATE_EQUALS, state.name());
        statements.add(stmt);
        RuleEffectImpl effect = RuleEffectImplTest.getRuleEffect(EffectType.ALLOW, AllowType.STANDARD, PlayerState.MOVED, null);
        List<CardRuleImpl> rules = new LinkedList<>();
        rules.add(CardRuleImplTest.getRule(triggerType, statements, effect));
        CardFileImpl cardFile = new CardFileImpl(nameTest,descrTest,rules);
        try{
            CardValidator.checkCardFile(cardFile);
        } catch (InvalidCardException e) {
            assert false;
        }
        return cardFile;
    }
    public static CardFileImpl getAllowAll(TriggerType triggerType, PlayerState state) {
        String nameTest = "TEST10";
        String descrTest = "AllowAll";
        List<RuleStatementImpl> statements = new LinkedList<>();
        RuleStatementImpl stmt = RuleStatementImplTest.getStatement(StatementType.IF, "YOU", StatementVerbType.STATE_EQUALS, state.name());
        statements.add(stmt);
        RuleEffectImpl effect = RuleEffectImplTest.getRuleEffect(EffectType.ALLOW, AllowType.STANDARD, PlayerState.MOVED, null);
        List<CardRuleImpl> rules = new LinkedList<>();
        rules.add(CardRuleImplTest.getRule(triggerType, statements, effect));
        CardFileImpl cardFile = new CardFileImpl(nameTest,descrTest,rules);
        try{
            CardValidator.checkCardFile(cardFile);
        } catch (InvalidCardException e) {
            assert false;
        }
        return cardFile;
    }
    public static CardFileImpl getCardFileWithMixedAllowSubtypesOnBuild(){
        String nameTest = "TEST11";
        String descrTest = "MixedAllowSubtypes";
        List<CardRuleImpl> rules = CardRuleImplTest.getRuleWithMixedAllowSubtypesOnBuild();
        return new CardFileImpl(nameTest,descrTest,rules);
    }
}