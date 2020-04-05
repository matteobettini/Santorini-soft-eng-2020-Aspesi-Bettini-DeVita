package it.polimi.ingsw.model.lambdaStrategy;

import it.polimi.ingsw.model.InternalModel;
import it.polimi.ingsw.model.cardReader.RuleStatement;
import it.polimi.ingsw.model.cardReader.RuleStatementImplTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

class StatementCompilerTest {

    private LambdaStatement lambdaStatement;
    private List<RuleStatement> ruleStatements;
    private InternalModel model;

    @BeforeEach
    void setUp() {
        ruleStatements = new ArrayList<>();
        ruleStatements.addAll(RuleStatementImplTest.getMoveStatementList());
        List<String> playersNicks = new ArrayList<>();
        playersNicks.add("giorgio");
        playersNicks.add("mario");
    }

    @AfterEach
    void tearDown() {
    }


    @Test
    void compiledStatementShouldNotBeNull() {

    }
}