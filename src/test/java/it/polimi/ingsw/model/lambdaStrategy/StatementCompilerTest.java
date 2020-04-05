package it.polimi.ingsw.model.lambdaStrategy;

import it.polimi.ingsw.model.InternalModel;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Worker;
import it.polimi.ingsw.model.cardReader.RuleStatement;
import it.polimi.ingsw.model.cardReader.RuleStatementImplTest;
import it.polimi.ingsw.model.cardReader.enums.StatementType;
import it.polimi.ingsw.model.cardReader.enums.StatementVerbType;
import it.polimi.ingsw.model.turnInfo.BuildData;
import it.polimi.ingsw.model.turnInfo.MoveData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

class StatementCompilerTest {

    private InternalModel model;
    private Player Matteo;
    private Player Mirko;
    private Player Andrea;
    private Worker MatteoW1;
    private Worker MatteoW2;
    private Worker AndreaW1;
    private Worker AndreaW2;
    private Worker MirkoW1;
    private Worker MirkoW2;



/*
          0    1     2    3    4
        +----+----+----+----+----+
    0   | A1 |    |    |    |    |
        +----+----+----+----+----+
    1   |    |    |    |    |    |
        +----+----+----+----+----+
    2   |    | B1 | A2 | B2 |    |
        +----+----+----+----+----+
    3   |    |    |    | D2 |    |
        +----+----+----+----+----+
    4   |    |    | D1 |    |    |
        +----+----+----+----+----+
*/

    @BeforeEach
    void setUp() {
        List<String> players = new ArrayList<String>();
        players.add("Andrea");
        players.add("Matteo");
        players.add("Mirko");
        model = new InternalModel(players);
        Andrea = model.getPlayerByNick("Andrea");
        Matteo = model.getPlayerByNick("Matteo");
        Mirko = model.getPlayerByNick("Mirko");
        MatteoW1 = Matteo.getWorkers().get(0);
        MatteoW2 = Matteo.getWorkers().get(1);
        MirkoW1 = Mirko.getWorkers().get(0);
        MirkoW2 = Mirko.getWorkers().get(1);
        AndreaW1 = Andrea.getWorkers().get(0);
        AndreaW2 = Andrea.getWorkers().get(1);


        model.getBoard().getCell(new Point(0,0)).setWorker(AndreaW1.getID());
        model.getBoard().getCell(new Point(2,2)).setWorker(AndreaW2.getID());
        model.getBoard().getCell(new Point(1,2)).setWorker(MatteoW1.getID());
        model.getBoard().getCell(new Point(3,2)).setWorker(MatteoW2.getID());
        model.getBoard().getCell(new Point(2,4)).setWorker(MirkoW1.getID());
        model.getBoard().getCell(new Point(3,3)).setWorker(MirkoW2.getID());

        AndreaW1.setPosition(new Point(0,0));
        AndreaW2.setPosition(new Point(2,2));
        MatteoW1.setPosition(new Point(1,2));
        MatteoW2.setPosition(new Point(3,2));
        MirkoW1.setPosition(new Point(2,4));
        MirkoW2.setPosition(new Point(3,3));
    }

    @AfterEach
    void tearDown() {
        model = null;
    }


    @Test
    void testPlayerEquals() {
        for (Player cardOwner : model.getPlayers()){
            RuleStatement playerEqualsStatement = RuleStatementImplTest.getStatement(StatementType.IF, "YOU", StatementVerbType.PLAYER_EQUALS, "CARD_OWNER");
            LambdaStatement compiledStatement = StatementCompiler.compileStatement(model, playerEqualsStatement, cardOwner);

            // WITH IF
            for (Player p : model.getPlayers()) {
                MoveData moveData = new MoveData(p, null, null);
                BuildData buildData = new BuildData(p, null, null);
                if (p.equals(cardOwner)) {
                    assert (compiledStatement.evaluate(moveData, null));
                    assert (compiledStatement.evaluate(null, buildData));
                } else {
                    assert (!compiledStatement.evaluate(null, buildData));
                    assert (!compiledStatement.evaluate(moveData, null));
                }
            }

            playerEqualsStatement = RuleStatementImplTest.getStatement(StatementType.NIF, "YOU", StatementVerbType.PLAYER_EQUALS, "CARD_OWNER");
            compiledStatement = StatementCompiler.compileStatement(model, playerEqualsStatement, cardOwner);

            // WITH NIF
            for (Player p : model.getPlayers()) {
                MoveData moveData = new MoveData(p, null, null);
                BuildData buildData = new BuildData(p, null, null);
                if (p.equals(cardOwner)) {
                    assert (!compiledStatement.evaluate(moveData, null));
                    assert (!compiledStatement.evaluate(null, buildData));
                } else {
                    assert (compiledStatement.evaluate(null, buildData));
                    assert (compiledStatement.evaluate(moveData, null));
                }
            }
        }
    }

    // Testing with a board having 0 buildings on it that esxists delta more than 0
    // is always false moving without touching neither workers nor domes
    @Test
    void testExistsDeltaMore1(){

        /*


            0     1      2      3      4
        +------+------+------+------+------+
    0   |   A1 |      |      |      |      |
        |      |      |      |      |      |
        +------+------+------+------+------+
    1   |      |      |      |      |      |
        |      |      |      |      |      |
        +------+------+------+------+------+
    2   |      |      |      |      |      |
        |      |      |      |      |      |
        +------+------+------+------+------+
    3   |      |      |      |  A2  |      |
        |      |      |      |      |      |
        +------+------+------+------+------+
    4   |      |      |  3   |  2   |  1   |
        |      |      |      |      |      |
        +------+------+------+------+------+
*/

        RuleStatement ruleStatement = RuleStatementImplTest.getStatement(StatementType.IF,"YOU", StatementVerbType.EXISTS_DELTA_MORE,"-34");
        LambdaStatement lambdaStatement = StatementCompiler.compileStatement(model, ruleStatement, Andrea);

        List<Point> moves = new ArrayList<>();
        Point point1 = new Point(4,4);
        Point point2 = new Point(3,4);
        Point point3 = new Point(2, 4);
        moves.add(point1);
        moves.add(point2);
        moves.add(point3);
        MoveData moveData = new MoveData(Andrea, AndreaW2, moves);

        assert(lambdaStatement.evaluate(moveData, null));

    }

    // Testing with a board having 0 buildings on it that esxists delta more than 0
    // is always false moving without touching neither workers nor domes
    @Test
    void testExistsDeltaMore2(){

    }


    @Test
    void compiledStatementShouldNotBeNull() {

    }
}