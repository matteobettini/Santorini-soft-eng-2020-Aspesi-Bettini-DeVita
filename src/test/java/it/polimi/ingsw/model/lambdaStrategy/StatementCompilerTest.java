package it.polimi.ingsw.model.lambdaStrategy;

import it.polimi.ingsw.model.InternalModel;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Worker;
import it.polimi.ingsw.model.cardReader.RuleStatement;
import it.polimi.ingsw.model.cardReader.RuleStatementImplTest;
import it.polimi.ingsw.model.cardReader.enums.StatementType;
import it.polimi.ingsw.model.cardReader.enums.StatementVerbType;
import it.polimi.ingsw.model.enums.BuildingType;
import it.polimi.ingsw.model.enums.LevelType;
import it.polimi.ingsw.model.turnInfo.BuildData;
import it.polimi.ingsw.model.turnInfo.MoveData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    /*
        Testing with a board having 0 buildings on it that esxists delta more than 0 is always false
        moving without touching neither workers nor domes
     */
    @Test
    void existsDeltaMore_Test1(){
/*
          0    1     2    3    4
        +----+----+----+----+----+
    0   | A1 |    |    |    |    |
        +----+----+----+----+----+
    1   |    |    |    |    |    |
        +----+----+----+----+----+
    2   |    | B1 | A2 | B2 |    |
        +----+----+----+----+----+
    3   |    |    |    | D2 | 3  |
        +----+----+----+----+----+
    4   |    |    | D1 | 2  | 1  |
        +----+----+----+----+----+
*/
        RuleStatement ruleStatement = RuleStatementImplTest.getStatement(StatementType.IF,"YOU", StatementVerbType.EXISTS_DELTA_MORE,"0");
        LambdaStatement lambdaStatement = StatementCompiler.compileStatement(model, ruleStatement, Mirko);

        List<Point> moves = new ArrayList<>();
        Point point1 = new Point(4,4);
        Point point2 = new Point(3,4);
        Point point3 = new Point(4, 3);
        moves.add(point1);
        moves.add(point2);
        moves.add(point3);
        MoveData moveData = new MoveData(Mirko, MirkoW2, moves);
        assertFalse(lambdaStatement.evaluate(moveData, null));

    }

    /*
        Testing with a board having 0 buildings on it that NIF esxists delta more than 0 is always true
        moving without touching domes but touching workers
     */
    @Test
    void existsDeltaMore_Test2() {
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

        RuleStatement ruleStatement = RuleStatementImplTest.getStatement(StatementType.NIF,"YOU", StatementVerbType.EXISTS_DELTA_MORE,"0");
        LambdaStatement lambdaStatement = StatementCompiler.compileStatement(model, ruleStatement, Mirko);

        List<Point> moves = new ArrayList<>();
        Point point1 = new Point(3,2);
        Point point2 = new Point(2,2);
        Point point3 = new Point(1, 2);
        Point point4 = new Point(0, 2);

        moves.add(point1);
        moves.add(point2);
        moves.add(point3);
        moves.add(point4);
        MoveData moveData = new MoveData(Mirko, MirkoW2, moves);
        assertTrue(lambdaStatement.evaluate(moveData, null));

    }

    /*
        Testing in a path with some first floors that exists delta more than one
        is always false
     */
    @Test
    void existsDeltaMore_Test3(){
         /*
          0    1     2    3    4
        +----+----+----+----+----+
    0   | A1 | FF |    | FF | FF |
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

        model.getBoard().getCell(new Point(1, 0)).addBuilding(BuildingType.FIRST_FLOOR);
        model.getBoard().getCell(new Point(3, 0)).addBuilding(BuildingType.FIRST_FLOOR);
        model.getBoard().getCell(new Point(4, 0)).addBuilding(BuildingType.FIRST_FLOOR);

        RuleStatement ruleStatement = RuleStatementImplTest.getStatement(StatementType.NIF,"YOU", StatementVerbType.EXISTS_DELTA_MORE,"1");
        LambdaStatement lambdaStatement = StatementCompiler.compileStatement(model, ruleStatement, Andrea);

        List<Point> moves = new ArrayList<>();
        Point point1 = new Point(1,0);
        Point point2 = new Point(2,0);
        Point point3 = new Point(3, 0);
        Point point4 = new Point(4, 0);
        Point point5 = new Point(4, 1);

        moves.add(point1);
        moves.add(point2);
        moves.add(point3);
        moves.add(point4);
        moves.add(point5);
        MoveData moveData = new MoveData(Andrea, AndreaW1, moves);
        assertTrue(lambdaStatement.evaluate(moveData, null));
    }

    /*
        Testing in a path with some first floors and second floors that exists delta more than 2
         is always false
     */
    @Test
    void existsDeltaMore_Test4(){
        /*
          0    1     2    3    4
        +----+----+----+----+----+
    0   | A1 | SF |    | FF | SF |
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

        model.getBoard().getCell(new Point(1, 0)).addBuilding(BuildingType.FIRST_FLOOR);
        model.getBoard().getCell(new Point(1, 0)).addBuilding(BuildingType.SECOND_FLOOR);
        model.getBoard().getCell(new Point(3, 0)).addBuilding(BuildingType.FIRST_FLOOR);
        model.getBoard().getCell(new Point(4, 0)).addBuilding(BuildingType.FIRST_FLOOR);
        model.getBoard().getCell(new Point(4, 0)).addBuilding(BuildingType.SECOND_FLOOR);


        RuleStatement ruleStatement = RuleStatementImplTest.getStatement(StatementType.IF,"YOU", StatementVerbType.EXISTS_DELTA_MORE,"2");
        LambdaStatement lambdaStatement = StatementCompiler.compileStatement(model, ruleStatement, Andrea);

        List<Point> moves = new ArrayList<>();
        Point point1 = new Point(1,0);
        Point point2 = new Point(2,0);
        Point point3 = new Point(3, 0);
        Point point4 = new Point(4, 0);
        Point point5 = new Point(4, 1);

        moves.add(point1);
        moves.add(point2);
        moves.add(point3);
        moves.add(point4);
        moves.add(point5);
        MoveData moveData = new MoveData(Andrea, AndreaW1, moves);
        assertFalse(lambdaStatement.evaluate(moveData, null));
    }

    /*
        Testing in a path with some first floors and second floors and third floors that exists delta more than 3
         is always false
     */
    @Test
    void existsDeltaMore_Test5(){
        /*
          0    1     2    3    4
        +----+----+----+----+----+
    0   | A1 | SF |    |    |    |
        +----+----+----+----+----+
    1   |    |    |    |    |    |
        +----+----+----+----+----+
    2   |    | B1 | A2 | B2 |    |
        |    | TF | FF | TF |    |
        +----+----+----+----+----+
    3   |    |    |    | D2 |    |
        +----+----+----+----+----+
    4   |    |    | D1 |    |    |
        +----+----+----+----+----+
*/

        model.getBoard().getCell(new Point(1, 0)).addBuilding(BuildingType.FIRST_FLOOR);
        model.getBoard().getCell(new Point(1, 0)).addBuilding(BuildingType.SECOND_FLOOR);

        String id = model.getBoard().getCell(new Point(1, 2)).getWorkerID();
        model.getBoard().getCell(new Point(1, 2)).removeWorker();
        model.getBoard().getCell(new Point(1, 2)).addBuilding(BuildingType.FIRST_FLOOR);
        model.getBoard().getCell(new Point(1, 2)).addBuilding(BuildingType.SECOND_FLOOR);
        model.getBoard().getCell(new Point(1, 2)).addBuilding(BuildingType.THIRD_FLOOR);
        model.getBoard().getCell(new Point(1, 2)).setWorker(id);

        id = model.getBoard().getCell(new Point(2, 2)).getWorkerID();
        model.getBoard().getCell(new Point(2, 2)).removeWorker();
        model.getBoard().getCell(new Point(2, 2)).addBuilding(BuildingType.FIRST_FLOOR);
        model.getBoard().getCell(new Point(2, 2)).setWorker(id);

        id = model.getBoard().getCell(new Point(3, 2)).getWorkerID();
        model.getBoard().getCell(new Point(3, 2)).removeWorker();
        model.getBoard().getCell(new Point(3, 2)).addBuilding(BuildingType.FIRST_FLOOR);
        model.getBoard().getCell(new Point(3, 2)).addBuilding(BuildingType.SECOND_FLOOR);
        model.getBoard().getCell(new Point(3, 2)).addBuilding(BuildingType.THIRD_FLOOR);
        model.getBoard().getCell(new Point(3, 2)).setWorker(id);


        RuleStatement ruleStatement = RuleStatementImplTest.getStatement(StatementType.IF,"YOU", StatementVerbType.EXISTS_DELTA_MORE,"3");
        LambdaStatement lambdaStatement = StatementCompiler.compileStatement(model, ruleStatement, Andrea);

        List<Point> moves = new ArrayList<>();
        Point point1 = new Point(1,0);
        Point point2 = new Point(1,1);
        Point point3 = new Point(1,2);
        Point point4 = new Point(2, 2);
        Point point5 = new Point(3, 2);
        Point point6 = new Point(4, 2);

        moves.add(point1);
        moves.add(point2);
        moves.add(point3);
        moves.add(point4);
        moves.add(point5);
        moves.add(point6);
        MoveData moveData = new MoveData(Andrea, AndreaW1, moves);
        assertFalse(lambdaStatement.evaluate(moveData, null));
    }

    /*
       Testing in a path with some first floors and second floors and third floors and domes that
        it doesnt exist a delta more than 4
        or that exists delta more than 3 is true
    */
    @Test
    void existsDeltaMore_Test6(){
        /*
          0    1     2    3    4
        +----+----+----+----+----+
    0   | A1 | DM |    |    |    |
        +----+----+----+----+----+
    1   |    |    |    |    |    |
        +----+----+----+----+----+
    2   |    | B1 | A2 | B2 |    |
        |    | DM | FF | TF |    |
        +----+----+----+----+----+
    3   |    |    |    | D2 |    |
        +----+----+----+----+----+
    4   |    |    | D1 |    |    |
        +----+----+----+----+----+
*/

        model.getBoard().getCell(new Point(1, 0)).addBuilding(BuildingType.FIRST_FLOOR);
        model.getBoard().getCell(new Point(1, 0)).addBuilding(BuildingType.SECOND_FLOOR);
        model.getBoard().getCell(new Point(1, 0)).addBuilding(BuildingType.DOME);

        String id = model.getBoard().getCell(new Point(1, 2)).getWorkerID();
        model.getBoard().getCell(new Point(1, 2)).removeWorker();
        model.getBoard().getCell(new Point(1, 2)).addBuilding(BuildingType.FIRST_FLOOR);
        model.getBoard().getCell(new Point(1, 2)).addBuilding(BuildingType.DOME);
        model.getBoard().getCell(new Point(1, 2)).setWorker(id);

        id = model.getBoard().getCell(new Point(2, 2)).getWorkerID();
        model.getBoard().getCell(new Point(2, 2)).removeWorker();
        model.getBoard().getCell(new Point(2, 2)).addBuilding(BuildingType.FIRST_FLOOR);
        model.getBoard().getCell(new Point(2, 2)).setWorker(id);

        id = model.getBoard().getCell(new Point(3, 2)).getWorkerID();
        model.getBoard().getCell(new Point(3, 2)).removeWorker();
        model.getBoard().getCell(new Point(3, 2)).addBuilding(BuildingType.FIRST_FLOOR);
        model.getBoard().getCell(new Point(3, 2)).addBuilding(BuildingType.SECOND_FLOOR);
        model.getBoard().getCell(new Point(3, 2)).addBuilding(BuildingType.THIRD_FLOOR);
        model.getBoard().getCell(new Point(3, 2)).setWorker(id);


        RuleStatement ruleStatement = RuleStatementImplTest.getStatement(StatementType.IF,"YOU", StatementVerbType.EXISTS_DELTA_MORE,"3");
        LambdaStatement lambdaStatement = StatementCompiler.compileStatement(model, ruleStatement, Andrea);

        List<Point> moves = new ArrayList<>();
        Point point1 = new Point(1,0);
        Point point2 = new Point(1,1);
        Point point3 = new Point(1,2);
        Point point4 = new Point(2, 2);
        Point point5 = new Point(3, 2);
        Point point6 = new Point(4, 2);

        moves.add(point1);
        moves.add(point2);
        moves.add(point3);
        moves.add(point4);
        moves.add(point5);
        moves.add(point6);
        MoveData moveData = new MoveData(Andrea, AndreaW1, moves);
        assertTrue(lambdaStatement.evaluate(moveData, null));
    }

    /*
        Testing with a board having 0 buildings on it that esxists delta less than 0 is always false
        moving without touching neither workers nor domes
     */
    @Test
    void existsDeltaLess_Test1(){
/*
          0    1     2    3    4
        +----+----+----+----+----+
    0   | A1 |    |    |    |    |
        +----+----+----+----+----+
    1   |    |    |    |    |    |
        +----+----+----+----+----+
    2   |    | B1 | A2 | B2 |    |
        +----+----+----+----+----+
    3   |    |    |    | D2 | 3  |
        +----+----+----+----+----+
    4   |    |    | D1 | 2  | 1  |
        +----+----+----+----+----+
*/
        RuleStatement ruleStatement = RuleStatementImplTest.getStatement(StatementType.IF,"YOU", StatementVerbType.EXISTS_DELTA_LESS,"0");
        LambdaStatement lambdaStatement = StatementCompiler.compileStatement(model, ruleStatement, Mirko);

        List<Point> moves = new ArrayList<>();
        Point point1 = new Point(4,4);
        Point point2 = new Point(3,4);
        Point point3 = new Point(4, 3);
        moves.add(point1);
        moves.add(point2);
        moves.add(point3);
        MoveData moveData = new MoveData(Mirko, MirkoW2, moves);
        assertFalse(lambdaStatement.evaluate(moveData, null));

    }

    /*
        Testing with a board having 0 buildings on it that NIF esxists delta less than 0 is always true
        moving without touching domes but touching workers
     */
    @Test
    void existsDeltaLess_Test2() {
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

        RuleStatement ruleStatement = RuleStatementImplTest.getStatement(StatementType.NIF,"YOU", StatementVerbType.EXISTS_DELTA_LESS,"0");
        LambdaStatement lambdaStatement = StatementCompiler.compileStatement(model, ruleStatement, Mirko);

        List<Point> moves = new ArrayList<>();
        Point point1 = new Point(3,2);
        Point point2 = new Point(2,2);
        Point point3 = new Point(1, 2);
        Point point4 = new Point(0, 2);

        moves.add(point1);
        moves.add(point2);
        moves.add(point3);
        moves.add(point4);
        MoveData moveData = new MoveData(Mirko, MirkoW2, moves);
        assertTrue(lambdaStatement.evaluate(moveData, null));

    }

    /*
        Testing in a path with some first floors that exists delta less than -1
        is always false
     */
    @Test
    void existsDeltaLess_Test3(){
         /*
          0    1     2    3    4
        +----+----+----+----+----+
    0   | A1 | FF |    | FF | FF |
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

        model.getBoard().getCell(new Point(1, 0)).addBuilding(BuildingType.FIRST_FLOOR);
        model.getBoard().getCell(new Point(3, 0)).addBuilding(BuildingType.FIRST_FLOOR);
        model.getBoard().getCell(new Point(4, 0)).addBuilding(BuildingType.FIRST_FLOOR);

        RuleStatement ruleStatement = RuleStatementImplTest.getStatement(StatementType.NIF,"YOU", StatementVerbType.EXISTS_DELTA_LESS,"-1");
        LambdaStatement lambdaStatement = StatementCompiler.compileStatement(model, ruleStatement, Andrea);

        List<Point> moves = new ArrayList<>();
        Point point1 = new Point(1,0);
        Point point2 = new Point(2,0);
        Point point3 = new Point(3, 0);
        Point point4 = new Point(4, 0);
        Point point5 = new Point(4, 1);

        moves.add(point1);
        moves.add(point2);
        moves.add(point3);
        moves.add(point4);
        moves.add(point5);
        MoveData moveData = new MoveData(Andrea, AndreaW1, moves);
        assertTrue(lambdaStatement.evaluate(moveData, null));
    }

    /*
         Testing in a path with some first floors and second floors that exists delta less than -2
         is always false
     */
    @Test
    void existsDeltaLess_Test4(){
        /*
          0    1     2    3    4
        +----+----+----+----+----+
    0   | A1 | SF |    | FF | SF |
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

        model.getBoard().getCell(new Point(1, 0)).addBuilding(BuildingType.FIRST_FLOOR);
        model.getBoard().getCell(new Point(1, 0)).addBuilding(BuildingType.SECOND_FLOOR);
        model.getBoard().getCell(new Point(3, 0)).addBuilding(BuildingType.FIRST_FLOOR);
        model.getBoard().getCell(new Point(4, 0)).addBuilding(BuildingType.FIRST_FLOOR);
        model.getBoard().getCell(new Point(4, 0)).addBuilding(BuildingType.SECOND_FLOOR);


        RuleStatement ruleStatement = RuleStatementImplTest.getStatement(StatementType.IF,"YOU", StatementVerbType.EXISTS_DELTA_LESS,"-2");
        LambdaStatement lambdaStatement = StatementCompiler.compileStatement(model, ruleStatement, Andrea);

        List<Point> moves = new ArrayList<>();
        Point point1 = new Point(1,0);
        Point point2 = new Point(2,0);
        Point point3 = new Point(3, 0);
        Point point4 = new Point(4, 0);
        Point point5 = new Point(4, 1);

        moves.add(point1);
        moves.add(point2);
        moves.add(point3);
        moves.add(point4);
        moves.add(point5);
        MoveData moveData = new MoveData(Andrea, AndreaW1, moves);
        assertFalse(lambdaStatement.evaluate(moveData, null));
    }

    /*
        Testing in a path with some first floors and second floors and third floors that exists delta less than -3
         is always false
     */
    @Test
    void existsDeltaLess_Test5(){
        /*
          0    1     2    3    4
        +----+----+----+----+----+
    0   | A1 | SF |    |    |    |
        +----+----+----+----+----+
    1   |    |    |    |    |    |
        +----+----+----+----+----+
    2   |    | B1 | A2 | B2 |    |
        |    | TF | FF | TF |    |
        +----+----+----+----+----+
    3   |    |    |    | D2 |    |
        +----+----+----+----+----+
    4   |    |    | D1 |    |    |
        +----+----+----+----+----+
*/

        model.getBoard().getCell(new Point(1, 0)).addBuilding(BuildingType.FIRST_FLOOR);
        model.getBoard().getCell(new Point(1, 0)).addBuilding(BuildingType.SECOND_FLOOR);

        String id = model.getBoard().getCell(new Point(1, 2)).getWorkerID();
        model.getBoard().getCell(new Point(1, 2)).removeWorker();
        model.getBoard().getCell(new Point(1, 2)).addBuilding(BuildingType.FIRST_FLOOR);
        model.getBoard().getCell(new Point(1, 2)).addBuilding(BuildingType.SECOND_FLOOR);
        model.getBoard().getCell(new Point(1, 2)).addBuilding(BuildingType.THIRD_FLOOR);
        model.getBoard().getCell(new Point(1, 2)).setWorker(id);

        id = model.getBoard().getCell(new Point(2, 2)).getWorkerID();
        model.getBoard().getCell(new Point(2, 2)).removeWorker();
        model.getBoard().getCell(new Point(2, 2)).addBuilding(BuildingType.FIRST_FLOOR);
        model.getBoard().getCell(new Point(2, 2)).setWorker(id);

        id = model.getBoard().getCell(new Point(3, 2)).getWorkerID();
        model.getBoard().getCell(new Point(3, 2)).removeWorker();
        model.getBoard().getCell(new Point(3, 2)).addBuilding(BuildingType.FIRST_FLOOR);
        model.getBoard().getCell(new Point(3, 2)).addBuilding(BuildingType.SECOND_FLOOR);
        model.getBoard().getCell(new Point(3, 2)).addBuilding(BuildingType.THIRD_FLOOR);
        model.getBoard().getCell(new Point(3, 2)).setWorker(id);


        RuleStatement ruleStatement = RuleStatementImplTest.getStatement(StatementType.IF,"YOU", StatementVerbType.EXISTS_DELTA_LESS,"-3");
        LambdaStatement lambdaStatement = StatementCompiler.compileStatement(model, ruleStatement, Andrea);

        List<Point> moves = new ArrayList<>();
        Point point1 = new Point(1,0);
        Point point2 = new Point(1,1);
        Point point3 = new Point(1,2);
        Point point4 = new Point(2, 2);
        Point point5 = new Point(3, 2);
        Point point6 = new Point(4, 2);

        moves.add(point1);
        moves.add(point2);
        moves.add(point3);
        moves.add(point4);
        moves.add(point5);
        moves.add(point6);
        MoveData moveData = new MoveData(Andrea, AndreaW1, moves);
        assertFalse(lambdaStatement.evaluate(moveData, null));
    }

    /*
       Testing in a path with some first floors and second floors and third floors and domes that
        it doesnt exist a delta less than -4
        or that exists delta less than -3 is true
    */
    @Test
    void existsDeltaLess_Test6(){
        /*
          0    1     2    3    4
        +----+----+----+----+----+
    0   | A1 | DM |    |    |    |
        +----+----+----+----+----+
    1   |    |    |    |    |    |
        +----+----+----+----+----+
    2   |    | B1 | A2 | B2 |    |
        |    | DM | FF | TF |    |
        +----+----+----+----+----+
    3   |    |    |    | D2 |    |
        +----+----+----+----+----+
    4   |    |    | D1 |    |    |
        +----+----+----+----+----+
*/

        model.getBoard().getCell(new Point(1, 0)).addBuilding(BuildingType.FIRST_FLOOR);
        model.getBoard().getCell(new Point(1, 0)).addBuilding(BuildingType.SECOND_FLOOR);
        model.getBoard().getCell(new Point(1, 0)).addBuilding(BuildingType.DOME);

        String id = model.getBoard().getCell(new Point(1, 2)).getWorkerID();
        model.getBoard().getCell(new Point(1, 2)).removeWorker();
        model.getBoard().getCell(new Point(1, 2)).addBuilding(BuildingType.FIRST_FLOOR);
        model.getBoard().getCell(new Point(1, 2)).addBuilding(BuildingType.DOME);
        model.getBoard().getCell(new Point(1, 2)).setWorker(id);

        id = model.getBoard().getCell(new Point(2, 2)).getWorkerID();
        model.getBoard().getCell(new Point(2, 2)).removeWorker();
        model.getBoard().getCell(new Point(2, 2)).addBuilding(BuildingType.FIRST_FLOOR);
        model.getBoard().getCell(new Point(2, 2)).setWorker(id);

        id = model.getBoard().getCell(new Point(3, 2)).getWorkerID();
        model.getBoard().getCell(new Point(3, 2)).removeWorker();
        model.getBoard().getCell(new Point(3, 2)).addBuilding(BuildingType.FIRST_FLOOR);
        model.getBoard().getCell(new Point(3, 2)).addBuilding(BuildingType.SECOND_FLOOR);
        model.getBoard().getCell(new Point(3, 2)).addBuilding(BuildingType.THIRD_FLOOR);
        model.getBoard().getCell(new Point(3, 2)).setWorker(id);


        RuleStatement ruleStatement = RuleStatementImplTest.getStatement(StatementType.IF,"YOU", StatementVerbType.EXISTS_DELTA_LESS,"-3");
        LambdaStatement lambdaStatement = StatementCompiler.compileStatement(model, ruleStatement, Andrea);

        List<Point> moves = new ArrayList<>();
        Point point1 = new Point(1,0);
        Point point2 = new Point(1,1);
        Point point3 = new Point(1,2);
        Point point4 = new Point(2, 2);
        Point point5 = new Point(3, 2);
        Point point6 = new Point(4, 2);

        moves.add(point1);
        moves.add(point2);
        moves.add(point3);
        moves.add(point4);
        moves.add(point5);
        moves.add(point6);
        MoveData moveData = new MoveData(Andrea, AndreaW1, moves);
        assertTrue(lambdaStatement.evaluate(moveData, null));
    }

    /*
        This test verifies that build dome returns true when a player
        wants to build only one dome on any type of object
    */
    @Test
    void buildDome_Test1(){
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
*/      model.getBoard().getCell(new Point(1, 3)).addBuilding(BuildingType.FIRST_FLOOR);

        model.getBoard().getCell(new Point(0, 3)).addBuilding(BuildingType.FIRST_FLOOR);
        model.getBoard().getCell(new Point(0, 3)).addBuilding(BuildingType.SECOND_FLOOR);


        model.getBoard().getCell(new Point(0, 2)).addBuilding(BuildingType.FIRST_FLOOR);
        model.getBoard().getCell(new Point(0, 2)).addBuilding(BuildingType.SECOND_FLOOR);
        model.getBoard().getCell(new Point(0, 2)).addBuilding(BuildingType.THIRD_FLOOR);


        for(LevelType lt : LevelType.values()) {
            if (lt == LevelType.DOME) {
                break;
            }
            RuleStatement ruleStatement = RuleStatementImplTest.getStatement(StatementType.IF, "YOU", StatementVerbType.BUILD_DOME, lt.toString());
            LambdaStatement lambdaStatement = StatementCompiler.compileStatement(model, ruleStatement, Matteo);

            Map<Point,List<BuildingType>> builds = new HashMap<>();

            Point point1 = new Point(2,3);
            List<BuildingType> buildsInPoint = new ArrayList<>();
            buildsInPoint.add(BuildingType.DOME);
            builds.put(point1, buildsInPoint);

            Point point2 = new Point(1,3);
            buildsInPoint = new ArrayList<>();
            buildsInPoint.add(BuildingType.DOME);
            builds.put(point2, buildsInPoint);

            Point point3 = new Point(0,3);
            buildsInPoint = new ArrayList<>();
            buildsInPoint.add(BuildingType.DOME);
            builds.put(point3, buildsInPoint);

            Point point4 = new Point(0, 2);
            buildsInPoint = new ArrayList<>();
            buildsInPoint.add(BuildingType.DOME);
            builds.put(point4, buildsInPoint);

            Point point5 = new Point(0, 1);
            Point point6 = new Point(1, 1);
            Point point7 = new Point(2,1);


            BuildData buildData = new BuildData(Matteo, MatteoW1, builds);

            assertTrue(lambdaStatement.evaluate(null, buildData));
        }
    }

    /*
       This test verifies that build dome returns true when a player
       wants to build only one dome on the specified type of object
       and returns false in all other cases
    */
    @Test
    void buildDome_Test2(){
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
*/      model.getBoard().getCell(new Point(1, 3)).addBuilding(BuildingType.FIRST_FLOOR);

        model.getBoard().getCell(new Point(0, 3)).addBuilding(BuildingType.FIRST_FLOOR);
        model.getBoard().getCell(new Point(0, 3)).addBuilding(BuildingType.SECOND_FLOOR);


        model.getBoard().getCell(new Point(0, 2)).addBuilding(BuildingType.FIRST_FLOOR);
        model.getBoard().getCell(new Point(0, 2)).addBuilding(BuildingType.SECOND_FLOOR);
        model.getBoard().getCell(new Point(0, 2)).addBuilding(BuildingType.THIRD_FLOOR);


        for(LevelType lt : LevelType.values()) {
            if (lt == LevelType.DOME) {
                break;
            }
            RuleStatement ruleStatement = RuleStatementImplTest.getStatement(StatementType.NIF, "YOU", StatementVerbType.BUILD_DOME, lt.toString());
            LambdaStatement lambdaStatement = StatementCompiler.compileStatement(model, ruleStatement, Matteo);

            Map<Point,List<BuildingType>> builds = new HashMap<>();

            /*Point point1 = new Point(2,3);
            List<BuildingType> buildsInPoint = new ArrayList<>();
            buildsInPoint.add(BuildingType.DOME);
            builds.put(point1, buildsInPoint);*/

            /*Point point2 = new Point(1,3);
            buildsInPoint = new ArrayList<>();
            buildsInPoint.add(BuildingType.DOME);
            builds.put(point2, buildsInPoint);*/

            Point point3 = new Point(0,3);
            List<BuildingType> buildsInPoint = new ArrayList<>();
            buildsInPoint.add(BuildingType.DOME);
            builds.put(point3, buildsInPoint);

          /*  Point point4 = new Point(0, 2);
            buildsInPoint = new ArrayList<>();
            buildsInPoint.add(BuildingType.DOME);
            builds.put(point4, buildsInPoint);*/

            Point point5 = new Point(0, 1);
            Point point6 = new Point(1, 1);
            Point point7 = new Point(2,1);


            BuildData buildData = new BuildData(Matteo, MatteoW1, builds);

            if(lt == LevelType.SECOND_FLOOR)
                assertFalse(lambdaStatement.evaluate(null, buildData));
            else
                assertTrue(lambdaStatement.evaluate(null, buildData));
        }
    }

    /*
       This test makes a player want to build only domes and
       only on third floors, having as statement objects all
       level types apart from third floors and domes, therefore the statement should always be false
    */
    @Test
    void buildDome_Test3(){
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
        model.getBoard().getCell(new Point(2, 3)).addBuilding(BuildingType.FIRST_FLOOR);
        model.getBoard().getCell(new Point(2, 3)).addBuilding(BuildingType.SECOND_FLOOR);
        model.getBoard().getCell(new Point(2, 3)).addBuilding(BuildingType.THIRD_FLOOR);

        model.getBoard().getCell(new Point(1, 3)).addBuilding(BuildingType.FIRST_FLOOR);
        model.getBoard().getCell(new Point(1, 3)).addBuilding(BuildingType.SECOND_FLOOR);
        model.getBoard().getCell(new Point(1, 3)).addBuilding(BuildingType.THIRD_FLOOR);

        model.getBoard().getCell(new Point(0, 3)).addBuilding(BuildingType.FIRST_FLOOR);
        model.getBoard().getCell(new Point(0, 3)).addBuilding(BuildingType.SECOND_FLOOR);
        model.getBoard().getCell(new Point(0, 3)).addBuilding(BuildingType.THIRD_FLOOR);

        model.getBoard().getCell(new Point(0, 2)).addBuilding(BuildingType.FIRST_FLOOR);
        model.getBoard().getCell(new Point(0, 2)).addBuilding(BuildingType.SECOND_FLOOR);
        model.getBoard().getCell(new Point(0, 2)).addBuilding(BuildingType.THIRD_FLOOR);


        for(LevelType lt : LevelType.values()) {
            if (lt == LevelType.DOME || lt == LevelType.THIRD_FLOOR) {
                break;
            }
            RuleStatement ruleStatement = RuleStatementImplTest.getStatement(StatementType.IF, "YOU", StatementVerbType.BUILD_DOME, lt.toString());
            LambdaStatement lambdaStatement = StatementCompiler.compileStatement(model, ruleStatement, Matteo);

            Map<Point,List<BuildingType>> builds = new HashMap<>();

            Point point1 = new Point(2,3);
            List<BuildingType> buildsInPoint = new ArrayList<>();
            buildsInPoint.add(BuildingType.DOME);
            builds.put(point1, buildsInPoint);

            Point point2 = new Point(1,3);
            buildsInPoint = new ArrayList<>();
            buildsInPoint.add(BuildingType.DOME);
            builds.put(point2, buildsInPoint);

            Point point3 = new Point(0,3);
            buildsInPoint = new ArrayList<>();
            buildsInPoint.add(BuildingType.DOME);
            builds.put(point3, buildsInPoint);

            Point point4 = new Point(0, 2);
            buildsInPoint = new ArrayList<>();
            buildsInPoint.add(BuildingType.DOME);
            builds.put(point4, buildsInPoint);

            Point point5 = new Point(0, 1);
            Point point6 = new Point(1, 1);
            Point point7 = new Point(2,1);


            BuildData buildData = new BuildData(Matteo, MatteoW1, builds);

            assertFalse(lambdaStatement.evaluate(null, buildData));
        }
    }

    /*
       It is like build dome test 1 but this time everything
       is built by the player
    */
    @Test
    void buildDome_Test4(){
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

        for(LevelType lt : LevelType.values()) {
            if (lt == LevelType.DOME) {
                break;
            }
            RuleStatement ruleStatement = RuleStatementImplTest.getStatement(StatementType.IF, "YOU", StatementVerbType.BUILD_DOME, lt.toString());
            LambdaStatement lambdaStatement = StatementCompiler.compileStatement(model, ruleStatement, Matteo);

            Map<Point,List<BuildingType>> builds = new HashMap<>();

            Point point1 = new Point(2,3);
            List<BuildingType> buildsInPoint = new ArrayList<>();
            buildsInPoint.add(BuildingType.DOME);
            builds.put(point1, buildsInPoint);

            Point point2 = new Point(1,3);
            List<BuildingType> buildsInPoint2 = new ArrayList<>();
            buildsInPoint2.add(BuildingType.FIRST_FLOOR);
            buildsInPoint2.add(BuildingType.DOME);
            builds.put(point2, buildsInPoint2);

            Point point3 = new Point(0,3);
            List<BuildingType> buildsInPoint3 = new ArrayList<>();
            buildsInPoint3.add(BuildingType.FIRST_FLOOR);
            buildsInPoint3.add(BuildingType.SECOND_FLOOR);
            buildsInPoint3.add(BuildingType.DOME);
            builds.put(point3, buildsInPoint3);

            Point point4 = new Point(0, 2);
            List<BuildingType> buildsInPoint4 = new ArrayList<>();
            buildsInPoint4.add(BuildingType.FIRST_FLOOR);
            buildsInPoint4.add(BuildingType.SECOND_FLOOR);
            buildsInPoint4.add(BuildingType.THIRD_FLOOR);
            buildsInPoint4.add(BuildingType.DOME);
            builds.put(point4, buildsInPoint4);

            Point point5 = new Point(0, 1);
            Point point6 = new Point(1, 1);
            Point point7 = new Point(2,1);



            BuildData buildData = new BuildData(Matteo, MatteoW1, builds);

            assertTrue(lambdaStatement.evaluate(null, buildData));
        }
    }

    /*
        The player builds dome on second and third level
   */
    @Test
    void buildDome_Test5(){
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

        for(LevelType lt : LevelType.values()) {
            if (lt == LevelType.DOME) {
                break;
            }
            RuleStatement ruleStatement = RuleStatementImplTest.getStatement(StatementType.IF, "YOU", StatementVerbType.BUILD_DOME, lt.toString());
            LambdaStatement lambdaStatement = StatementCompiler.compileStatement(model, ruleStatement, Matteo);

            Map<Point,List<BuildingType>> builds = new HashMap<>();

            Point point1 = new Point(2,3);
            List<BuildingType> buildsInPoint = new ArrayList<>();
            buildsInPoint.add(BuildingType.FIRST_FLOOR);
            builds.put(point1, buildsInPoint);

            Point point2 = new Point(1,3);
            List<BuildingType> buildsInPoint2 = new ArrayList<>();
            buildsInPoint2.add(BuildingType.FIRST_FLOOR);
            buildsInPoint2.add(BuildingType.SECOND_FLOOR);
            builds.put(point2, buildsInPoint2);

            Point point3 = new Point(0,3);
            List<BuildingType> buildsInPoint3 = new ArrayList<>();
            buildsInPoint3.add(BuildingType.FIRST_FLOOR);
            buildsInPoint3.add(BuildingType.SECOND_FLOOR);
            buildsInPoint3.add(BuildingType.DOME);
            builds.put(point3, buildsInPoint3);

            Point point4 = new Point(0, 2);
            List<BuildingType> buildsInPoint4 = new ArrayList<>();
            buildsInPoint4.add(BuildingType.FIRST_FLOOR);
            buildsInPoint4.add(BuildingType.SECOND_FLOOR);
            buildsInPoint4.add(BuildingType.THIRD_FLOOR);
            buildsInPoint4.add(BuildingType.DOME);
            builds.put(point4, buildsInPoint4);

            Point point5 = new Point(0, 1);
            Point point6 = new Point(1, 1);
            Point point7 = new Point(2,1);



            BuildData buildData = new BuildData(Matteo, MatteoW1, builds);

            if(lt == LevelType.GROUND || lt == LevelType.FIRST_FLOOR)
                assertFalse(lambdaStatement.evaluate(null, buildData));
            else
                assertTrue(lambdaStatement.evaluate(null, buildData));

        }
    }

    /*
        STRANGE CASE
        Building a dome on a dome it is ok for the statement
        if dome is the object
    */
    @Test
    void buildDome_STRANGE_CASE(){
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
        model.getBoard().getCell(new Point(2, 3)).addBuilding(BuildingType.DOME);

        RuleStatement ruleStatement = RuleStatementImplTest.getStatement(StatementType.IF, "YOU", StatementVerbType.BUILD_DOME, "DOME");
        LambdaStatement lambdaStatement = StatementCompiler.compileStatement(model, ruleStatement, Matteo);

        Map<Point,List<BuildingType>> builds = new HashMap<>();

        Point point1 = new Point(2,3);
        List<BuildingType> buildsInPoint = new ArrayList<>();
        buildsInPoint.add(BuildingType.DOME);
        builds.put(point1, buildsInPoint);


        BuildData buildData = new BuildData(Matteo, MatteoW1, builds);

        assertTrue(lambdaStatement.evaluate(null, buildData));
    }

    /*
       This test verifies that build dome except returns false when called with ground as object
       and the player wants to build a dome on the ground
       returns true with all other objects
   */
    @Test
    void buildDomeExcept_Test1(){
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
        model.getBoard().getCell(new Point(1, 3)).addBuilding(BuildingType.FIRST_FLOOR);

        model.getBoard().getCell(new Point(0, 3)).addBuilding(BuildingType.FIRST_FLOOR);
        model.getBoard().getCell(new Point(0, 3)).addBuilding(BuildingType.SECOND_FLOOR);


        model.getBoard().getCell(new Point(0, 2)).addBuilding(BuildingType.FIRST_FLOOR);
        model.getBoard().getCell(new Point(0, 2)).addBuilding(BuildingType.SECOND_FLOOR);
        model.getBoard().getCell(new Point(0, 2)).addBuilding(BuildingType.THIRD_FLOOR);


        for(LevelType lt : LevelType.values()) {
            if (lt == LevelType.DOME) {
                break;
            }
            RuleStatement ruleStatement = RuleStatementImplTest.getStatement(StatementType.IF, "YOU", StatementVerbType.BUILD_DOME_EXCEPT, lt.toString());
            LambdaStatement lambdaStatement = StatementCompiler.compileStatement(model, ruleStatement, Matteo);

            Map<Point,List<BuildingType>> builds = new HashMap<>();

            Point point1 = new Point(2,3);
            List<BuildingType> buildsInPoint = new ArrayList<>();
            buildsInPoint.add(BuildingType.DOME);
            builds.put(point1, buildsInPoint);

          /*  Point point2 = new Point(1,3);
            buildsInPoint = new ArrayList<>();
            buildsInPoint.add(BuildingType.DOME);
            builds.put(point2, buildsInPoint);

            Point point3 = new Point(0,3);
            buildsInPoint = new ArrayList<>();
            buildsInPoint.add(BuildingType.DOME);
            builds.put(point3, buildsInPoint);

            Point point4 = new Point(0, 2);
            buildsInPoint = new ArrayList<>();
            buildsInPoint.add(BuildingType.DOME);
            builds.put(point4, buildsInPoint);*/

           /* Point point5 = new Point(0, 1);
            Point point6 = new Point(1, 1);
            Point point7 = new Point(2,1);*/

            BuildData buildData = new BuildData(Matteo, MatteoW1, builds);

            if(lt == LevelType.GROUND)
                assertFalse(lambdaStatement.evaluate(null, buildData));
            else
                assertTrue(lambdaStatement.evaluate(null, buildData));
        }
    }

    /*
       This test verifies that build dome except returns always true when
       a player wants to build two domes on two different levels
    */
    @Test
    void buildDomeExcept_Test2(){
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

        for(LevelType lt : LevelType.values()) {
            if (lt == LevelType.DOME) {
                break;
            }
            RuleStatement ruleStatement = RuleStatementImplTest.getStatement(StatementType.NIF, "YOU", StatementVerbType.BUILD_DOME_EXCEPT, lt.toString());
            LambdaStatement lambdaStatement = StatementCompiler.compileStatement(model, ruleStatement, Matteo);

            Map<Point,List<BuildingType>> builds = new HashMap<>();

       /*     Point point1 = new Point(2,3);
            List<BuildingType> buildsInPoint = new ArrayList<>();
            buildsInPoint.add(BuildingType.DOME);
            builds.put(point1, buildsInPoint);*/

            /*Point point2 = new Point(1,3);
            List<BuildingType> buildsInPoint2 = new ArrayList<>();
            buildsInPoint2.add(BuildingType.FIRST_FLOOR);
            buildsInPoint2.add(BuildingType.DOME);
            builds.put(point2, buildsInPoint2);*/

            Point point3 = new Point(0,3);
            List<BuildingType> buildsInPoint3 = new ArrayList<>();
            buildsInPoint3.add(BuildingType.FIRST_FLOOR);
            buildsInPoint3.add(BuildingType.SECOND_FLOOR);
            buildsInPoint3.add(BuildingType.DOME);
            builds.put(point3, buildsInPoint3);

            Point point4 = new Point(0, 2);
            List<BuildingType> buildsInPoint4 = new ArrayList<>();
            buildsInPoint4.add(BuildingType.FIRST_FLOOR);
            buildsInPoint4.add(BuildingType.SECOND_FLOOR);
            buildsInPoint4.add(BuildingType.THIRD_FLOOR);
            buildsInPoint4.add(BuildingType.DOME);
            builds.put(point4, buildsInPoint4);

            Point point5 = new Point(0, 1);
            Point point6 = new Point(1, 1);
            Point point7 = new Point(2,1);



            BuildData buildData = new BuildData(Matteo, MatteoW1, builds);

            assertFalse(lambdaStatement.evaluate(null, buildData));

        }
    }

    /*
       This test verifies that build dome except returns false when called with second level as object
       and the player wants to build a dome on the second level
       returns true with all other objects
   */
    @Test
    void buildDomeExcept_Test3(){
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
        model.getBoard().getCell(new Point(2, 3)).addBuilding(BuildingType.FIRST_FLOOR);
        model.getBoard().getCell(new Point(2, 3)).addBuilding(BuildingType.SECOND_FLOOR);

        model.getBoard().getCell(new Point(1, 3)).addBuilding(BuildingType.FIRST_FLOOR);
        model.getBoard().getCell(new Point(1, 3)).addBuilding(BuildingType.SECOND_FLOOR);

        model.getBoard().getCell(new Point(0, 3)).addBuilding(BuildingType.FIRST_FLOOR);
        model.getBoard().getCell(new Point(0, 3)).addBuilding(BuildingType.SECOND_FLOOR);

        model.getBoard().getCell(new Point(0, 2)).addBuilding(BuildingType.FIRST_FLOOR);
        model.getBoard().getCell(new Point(0, 2)).addBuilding(BuildingType.SECOND_FLOOR);


        for(LevelType lt : LevelType.values()) {
            if (lt == LevelType.DOME) {
                break;
            }
            RuleStatement ruleStatement = RuleStatementImplTest.getStatement(StatementType.IF, "YOU", StatementVerbType.BUILD_DOME_EXCEPT, lt.toString());
            LambdaStatement lambdaStatement = StatementCompiler.compileStatement(model, ruleStatement, Matteo);

            Map<Point,List<BuildingType>> builds = new HashMap<>();

            Point point1 = new Point(2,3);
            List<BuildingType> buildsInPoint = new ArrayList<>();
            buildsInPoint.add(BuildingType.DOME);
            builds.put(point1, buildsInPoint);

            Point point2 = new Point(1,3);
            buildsInPoint = new ArrayList<>();
            buildsInPoint.add(BuildingType.DOME);
            builds.put(point2, buildsInPoint);

            Point point3 = new Point(0,3);
            buildsInPoint = new ArrayList<>();
            buildsInPoint.add(BuildingType.DOME);
            builds.put(point3, buildsInPoint);

            Point point4 = new Point(0, 2);
            buildsInPoint = new ArrayList<>();
            buildsInPoint.add(BuildingType.DOME);
            builds.put(point4, buildsInPoint);

          /*  Point point5 = new Point(0, 1);
            Point point6 = new Point(1, 1);
            Point point7 = new Point(2,1);*/


            BuildData buildData = new BuildData(Matteo, MatteoW1, builds);

            if(lt == LevelType.SECOND_FLOOR)
                assertFalse(lambdaStatement.evaluate(null, buildData));
            else
                assertTrue(lambdaStatement.evaluate(null, buildData));

        }
    }

    /*
       This test verifies that build dome except returns always true when
       a player wants to build a dome on a different level from object
   */
    @Test
    void buildDomeExcept_Test4(){
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
        model.getBoard().getCell(new Point(2, 3)).addBuilding(BuildingType.FIRST_FLOOR);

        model.getBoard().getCell(new Point(1, 3)).addBuilding(BuildingType.FIRST_FLOOR);
        model.getBoard().getCell(new Point(1, 3)).addBuilding(BuildingType.SECOND_FLOOR);

        model.getBoard().getCell(new Point(0, 3)).addBuilding(BuildingType.FIRST_FLOOR);
        model.getBoard().getCell(new Point(0, 3)).addBuilding(BuildingType.SECOND_FLOOR);
        model.getBoard().getCell(new Point(0, 3)).addBuilding(BuildingType.THIRD_FLOOR);

        model.getBoard().getCell(new Point(0, 2)).addBuilding(BuildingType.FIRST_FLOOR);
        model.getBoard().getCell(new Point(0, 2)).addBuilding(BuildingType.SECOND_FLOOR);
        model.getBoard().getCell(new Point(0, 2)).addBuilding(BuildingType.THIRD_FLOOR);
        model.getBoard().getCell(new Point(0, 2)).addBuilding(BuildingType.DOME);



        for(LevelType lt : LevelType.values()) {
            if (lt == LevelType.DOME) {
                break;
            }
            RuleStatement ruleStatement = RuleStatementImplTest.getStatement(StatementType.IF, "YOU", StatementVerbType.BUILD_DOME_EXCEPT, lt.toString());
            LambdaStatement lambdaStatement = StatementCompiler.compileStatement(model, ruleStatement, Matteo);

            Map<Point,List<BuildingType>> builds = new HashMap<>();
            List<BuildingType> buildsInPoint = new ArrayList<>();

            if(lt == LevelType.THIRD_FLOOR) {
                Point point1 = new Point(2, 3);
                buildsInPoint.add(BuildingType.DOME);
                builds.put(point1, buildsInPoint);
            }

            if(lt == LevelType.SECOND_FLOOR) {
                Point point2 = new Point(1, 3);
                buildsInPoint = new ArrayList<>();
                buildsInPoint.add(BuildingType.DOME);
                builds.put(point2, buildsInPoint);
            }

            if(lt == LevelType.FIRST_FLOOR) {
                Point point3 = new Point(0, 3);
                buildsInPoint = new ArrayList<>();
                buildsInPoint.add(BuildingType.DOME);
                builds.put(point3, buildsInPoint);
            }

            if(lt == LevelType.GROUND) {
                Point point4 = new Point(0, 2);
                buildsInPoint = new ArrayList<>();
                buildsInPoint.add(BuildingType.DOME);
                builds.put(point4, buildsInPoint);
            }

          /*  Point point5 = new Point(0, 1);
            Point point6 = new Point(1, 1);
            Point point7 = new Point(2,1);*/


            BuildData buildData = new BuildData(Matteo, MatteoW1, builds);

            if(lt == LevelType.SECOND_FLOOR)
                assertFalse(lambdaStatement.evaluate(null, buildData));
            else
                assertTrue(lambdaStatement.evaluate(null, buildData));

        }
    }

    /*
        Tests a move with 3 interactions on a wide range of different levels
     */
    @Test
    void interactionNum_Test1(){
        /*
          0    1     2    3    4
        +----+----+----+----+----+
    0   | A1 | SF |    |    |    |
        +----+----+----+----+----+
    1   |    |    |    |    |    |
        +----+----+----+----+----+
    2   |    | B1 | A2 | B2 |    |
        |    | TF | FF | TF |    |
        +----+----+----+----+----+
    3   |    |    |    | D2 |    |
        +----+----+----+----+----+
    4   |    |    | D1 |    |    |
        +----+----+----+----+----+
*/

        model.getBoard().getCell(new Point(1, 0)).addBuilding(BuildingType.FIRST_FLOOR);
        model.getBoard().getCell(new Point(1, 0)).addBuilding(BuildingType.SECOND_FLOOR);

        String id = model.getBoard().getCell(new Point(1, 2)).getWorkerID();
        model.getBoard().getCell(new Point(1, 2)).removeWorker();
        model.getBoard().getCell(new Point(1, 2)).addBuilding(BuildingType.FIRST_FLOOR);
        model.getBoard().getCell(new Point(1, 2)).addBuilding(BuildingType.SECOND_FLOOR);
        model.getBoard().getCell(new Point(1, 2)).addBuilding(BuildingType.THIRD_FLOOR);
        model.getBoard().getCell(new Point(1, 2)).setWorker(id);

        id = model.getBoard().getCell(new Point(2, 2)).getWorkerID();
        model.getBoard().getCell(new Point(2, 2)).removeWorker();
        model.getBoard().getCell(new Point(2, 2)).addBuilding(BuildingType.FIRST_FLOOR);
        model.getBoard().getCell(new Point(2, 2)).setWorker(id);

        id = model.getBoard().getCell(new Point(3, 2)).getWorkerID();
        model.getBoard().getCell(new Point(3, 2)).removeWorker();
        model.getBoard().getCell(new Point(3, 2)).addBuilding(BuildingType.FIRST_FLOOR);
        model.getBoard().getCell(new Point(3, 2)).addBuilding(BuildingType.SECOND_FLOOR);
        model.getBoard().getCell(new Point(3, 2)).addBuilding(BuildingType.THIRD_FLOOR);
        model.getBoard().getCell(new Point(3, 2)).setWorker(id);


        RuleStatement ruleStatement = RuleStatementImplTest.getStatement(StatementType.IF,"YOU", StatementVerbType.INTERACTION_NUM,"3");
        LambdaStatement lambdaStatement = StatementCompiler.compileStatement(model, ruleStatement, Andrea);

        List<Point> moves = new ArrayList<>();
        Point point1 = new Point(1,0);
        Point point2 = new Point(1,1);
        Point point3 = new Point(1,2);
        Point point4 = new Point(2, 2);
        Point point5 = new Point(3, 2);
        Point point6 = new Point(4, 2);

        moves.add(point1);
        moves.add(point2);
        moves.add(point3);
        moves.add(point4);
        moves.add(point5);
        moves.add(point6);
        MoveData moveData = new MoveData(Andrea, AndreaW1, moves);
        assertTrue(lambdaStatement.evaluate(moveData, null));
    }

    /*
       Tests a move with 7 interactions on a wide range of different levels
       in a sequance of move that touches some cells 2 times
    */
    @Test
    void interactionNum_Test2(){
        /*
          0    1     2    3    4
        +----+----+----+----+----+
    0   | A1 | SF |    |    |    |
        +----+----+----+----+----+
    1   |    |    |    |    |    |
        +----+----+----+----+----+
    2   |    | B1 | A2 | B2 |    |
        |    | TF | FF | TF |    |
        +----+----+----+----+----+
    3   |    |    |    | D2 |    |
        +----+----+----+----+----+
    4   |    |    | D1 |    |    |
        +----+----+----+----+----+
*/

        model.getBoard().getCell(new Point(1, 0)).addBuilding(BuildingType.FIRST_FLOOR);
        model.getBoard().getCell(new Point(1, 0)).addBuilding(BuildingType.SECOND_FLOOR);

        String id = model.getBoard().getCell(new Point(1, 2)).getWorkerID();
        model.getBoard().getCell(new Point(1, 2)).removeWorker();
        model.getBoard().getCell(new Point(1, 2)).addBuilding(BuildingType.FIRST_FLOOR);
        model.getBoard().getCell(new Point(1, 2)).addBuilding(BuildingType.SECOND_FLOOR);
        model.getBoard().getCell(new Point(1, 2)).addBuilding(BuildingType.THIRD_FLOOR);
        model.getBoard().getCell(new Point(1, 2)).setWorker(id);

        id = model.getBoard().getCell(new Point(2, 2)).getWorkerID();
        model.getBoard().getCell(new Point(2, 2)).removeWorker();
        model.getBoard().getCell(new Point(2, 2)).addBuilding(BuildingType.FIRST_FLOOR);
        model.getBoard().getCell(new Point(2, 2)).setWorker(id);

        id = model.getBoard().getCell(new Point(3, 2)).getWorkerID();
        model.getBoard().getCell(new Point(3, 2)).removeWorker();
        model.getBoard().getCell(new Point(3, 2)).addBuilding(BuildingType.FIRST_FLOOR);
        model.getBoard().getCell(new Point(3, 2)).addBuilding(BuildingType.SECOND_FLOOR);
        model.getBoard().getCell(new Point(3, 2)).addBuilding(BuildingType.THIRD_FLOOR);
        model.getBoard().getCell(new Point(3, 2)).setWorker(id);


        RuleStatement ruleStatement = RuleStatementImplTest.getStatement(StatementType.NIF,"YOU", StatementVerbType.INTERACTION_NUM,"7");
        LambdaStatement lambdaStatement = StatementCompiler.compileStatement(model, ruleStatement, Andrea);

        List<Point> moves = new ArrayList<>();
        Point point1 = new Point(1,0);
        Point point2 = new Point(1,1);
        Point point3 = new Point(1,2);
        Point point4 = new Point(2, 2);
        Point point5 = new Point(3, 2);
        Point point6 = new Point(3, 3);
        Point point7 = new Point(2, 4);
        Point point8 = new Point(3, 3);
        Point point9 = new Point(3, 2);




        moves.add(point1);
        moves.add(point2);
        moves.add(point3);
        moves.add(point4);
        moves.add(point5);
        moves.add(point6);
        moves.add(point7);
        moves.add(point8);
        moves.add(point9);

        MoveData moveData = new MoveData(Andrea, AndreaW1, moves);
        assertFalse(lambdaStatement.evaluate(moveData, null));
    }


}