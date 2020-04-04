package it.polimi.ingsw.model.lambdaStrategy;

import it.polimi.ingsw.model.cardReader.CardRule;
import it.polimi.ingsw.model.cardReader.CardRuleTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class RuleCompilerTest {

    private List<CardRule> cardRules;

    @BeforeEach
    void setUp() {
        cardRules = CardRuleTest.getRandomCardRuleList();

    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void compiledRuleShouldNotBeNull() {
    }

}