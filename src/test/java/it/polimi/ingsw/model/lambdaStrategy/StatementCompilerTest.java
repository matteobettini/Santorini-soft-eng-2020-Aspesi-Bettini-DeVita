package it.polimi.ingsw.model.lambdaStrategy;

import it.polimi.ingsw.model.InternalModel;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Worker;
import it.polimi.ingsw.model.cardReader.RuleEffectImplTest;
import it.polimi.ingsw.model.cardReader.RuleStatement;
import it.polimi.ingsw.model.cardReader.RuleStatementImplTest;
import it.polimi.ingsw.model.cardReader.enums.StatementType;
import it.polimi.ingsw.model.cardReader.enums.StatementVerbType;
import it.polimi.ingsw.model.enums.BuildingType;
import it.polimi.ingsw.model.enums.LevelType;
import it.polimi.ingsw.model.enums.PlayerFlag;
import it.polimi.ingsw.model.enums.PlayerState;
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

    /*
        In-depth test of player equals
     */
    @Test
    void playerEquals_Test1() {
        for (Player cardOwner : model.getPlayers()){
            RuleStatement playerEqualsStatement = RuleStatementImplTest.getStatement(StatementType.IF, "YOU", StatementVerbType.PLAYER_EQUALS, "CARD_OWNER");
            LambdaStatement compiledStatement = StatementCompiler.compileStatement(model, playerEqualsStatement, cardOwner);

            // WITH IF
            for (Player p : model.getPlayers()) {
                MoveData moveData = new MoveData(p, null, null);
                BuildData buildData = new BuildData(p, null, null, null);
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
                BuildData buildData = new BuildData(p, null, null, null);
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
    /**
     * Testing the statement STATE_EQUALS when the object is TURN_STARTED.
     * If the Statement type is IF the evaluation should be true.
     * If the Statement type is NIF the evaluation should be false.
     */
    @Test
    void stateEquals_Test1(){

        RuleStatement playerEqualsStatement = RuleStatementImplTest.getStatement(StatementType.IF, "YOU", StatementVerbType.STATE_EQUALS, "TURN_STARTED");
        LambdaStatement compiledStatement = StatementCompiler.compileStatement(model, playerEqualsStatement, Andrea);

        // WITH IF AND TURN_STARTED
        Player p = Andrea;
        MoveData moveData = new MoveData(p, null, null);
        BuildData buildData = new BuildData(p, null, null, null);
        assert (compiledStatement.evaluate(moveData, null));
        assert (compiledStatement.evaluate(null, buildData));

        p.setPlayerState(PlayerState.MOVED);

        assert (!compiledStatement.evaluate(moveData, null));
        assert (!compiledStatement.evaluate(null, buildData));

        p.setPlayerState(PlayerState.BUILT);

        assert (!compiledStatement.evaluate(moveData, null));
        assert (!compiledStatement.evaluate(null, buildData));

        p.setPlayerState(PlayerState.FIRST_BUILT);

        assert (!compiledStatement.evaluate(moveData, null));
        assert (!compiledStatement.evaluate(null, buildData));

        p.setPlayerState(PlayerState.END_TURN);

        assert (!compiledStatement.evaluate(moveData, null));
        assert (!compiledStatement.evaluate(null, buildData));





        playerEqualsStatement = RuleStatementImplTest.getStatement(StatementType.NIF, "YOU", StatementVerbType.STATE_EQUALS, "TURN_STARTED");
        compiledStatement = StatementCompiler.compileStatement(model, playerEqualsStatement, Andrea);

        // WITH NIF AND TURN_STARTED
        p.setPlayerState(PlayerState.TURN_STARTED);
        assert (!compiledStatement.evaluate(moveData, null));
        assert (!compiledStatement.evaluate(null, buildData));

        p.setPlayerState(PlayerState.MOVED);

        assert (compiledStatement.evaluate(moveData, null));
        assert (compiledStatement.evaluate(null, buildData));

        p.setPlayerState(PlayerState.BUILT);

        assert (compiledStatement.evaluate(moveData, null));
        assert (compiledStatement.evaluate(null, buildData));

        p.setPlayerState(PlayerState.FIRST_BUILT);

        assert (compiledStatement.evaluate(moveData, null));
        assert (compiledStatement.evaluate(null, buildData));

        p.setPlayerState(PlayerState.END_TURN);

        assert (compiledStatement.evaluate(moveData, null));
        assert (compiledStatement.evaluate(null, buildData));


    }
    /**
     * Testing the statement STATE_EQUALS when the object is MOVED.
     * If the Statement type is IF the evaluation should be true.
     * If the Statement type is NIF the evaluation should be false.
     */
    @Test
    void stateEquals_Test2(){

        RuleStatement playerEqualsStatement = RuleStatementImplTest.getStatement(StatementType.IF, "YOU", StatementVerbType.STATE_EQUALS, "MOVED");
        LambdaStatement compiledStatement = StatementCompiler.compileStatement(model, playerEqualsStatement, Andrea);

        Player p = Andrea;
        MoveData moveData = new MoveData(p, null, null);
        BuildData buildData = new BuildData(p, null, null, null);

        // WITH IF AND MOVED
        p.setPlayerState(PlayerState.MOVED);
        assert (compiledStatement.evaluate(moveData, null));
        assert (compiledStatement.evaluate(null, buildData));

        p.setPlayerState(PlayerState.TURN_STARTED);

        assert (!compiledStatement.evaluate(moveData, null));
        assert (!compiledStatement.evaluate(null, buildData));

        p.setPlayerState(PlayerState.BUILT);

        assert (!compiledStatement.evaluate(moveData, null));
        assert (!compiledStatement.evaluate(null, buildData));

        p.setPlayerState(PlayerState.FIRST_BUILT);

        assert (!compiledStatement.evaluate(moveData, null));
        assert (!compiledStatement.evaluate(null, buildData));

        p.setPlayerState(PlayerState.END_TURN);

        assert (!compiledStatement.evaluate(moveData, null));
        assert (!compiledStatement.evaluate(null, buildData));

        playerEqualsStatement = RuleStatementImplTest.getStatement(StatementType.NIF, "YOU", StatementVerbType.STATE_EQUALS, "MOVED");
        compiledStatement = StatementCompiler.compileStatement(model, playerEqualsStatement, Andrea);

        // WITH NIF AND MOVED
        p.setPlayerState(PlayerState.MOVED);
        assert (!compiledStatement.evaluate(moveData, null));
        assert (!compiledStatement.evaluate(null, buildData));

        p.setPlayerState(PlayerState.TURN_STARTED);

        assert (compiledStatement.evaluate(moveData, null));
        assert (compiledStatement.evaluate(null, buildData));

        p.setPlayerState(PlayerState.BUILT);

        assert (compiledStatement.evaluate(moveData, null));
        assert (compiledStatement.evaluate(null, buildData));

        p.setPlayerState(PlayerState.FIRST_BUILT);

        assert (compiledStatement.evaluate(moveData, null));
        assert (compiledStatement.evaluate(null, buildData));

        p.setPlayerState(PlayerState.END_TURN);

        assert (compiledStatement.evaluate(moveData, null));
        assert (compiledStatement.evaluate(null, buildData));
    }
    /**
     * Testing the statement STATE_EQUALS when the object is BUILT.
     * If the Statement type is IF the evaluation should be true.
     * If the Statement type is NIF the evaluation should be false.
     */
    @Test
    void stateEquals_Test3(){

        RuleStatement playerEqualsStatement = RuleStatementImplTest.getStatement(StatementType.IF, "YOU", StatementVerbType.STATE_EQUALS, "BUILT");
        LambdaStatement compiledStatement = StatementCompiler.compileStatement(model, playerEqualsStatement, Andrea);

        Player p = Andrea;
        MoveData moveData = new MoveData(p, null, null);
        BuildData buildData = new BuildData(p, null, null, null);


        // WITH IF AND BUILT
        p.setPlayerState(PlayerState.BUILT);
        assert (compiledStatement.evaluate(moveData, null));
        assert (compiledStatement.evaluate(null, buildData));

        p.setPlayerState(PlayerState.TURN_STARTED);

        assert (!compiledStatement.evaluate(moveData, null));
        assert (!compiledStatement.evaluate(null, buildData));

        p.setPlayerState(PlayerState.MOVED);

        assert (!compiledStatement.evaluate(moveData, null));
        assert (!compiledStatement.evaluate(null, buildData));

        p.setPlayerState(PlayerState.FIRST_BUILT);

        assert (!compiledStatement.evaluate(moveData, null));
        assert (!compiledStatement.evaluate(null, buildData));

        p.setPlayerState(PlayerState.END_TURN);

        assert (!compiledStatement.evaluate(moveData, null));
        assert (!compiledStatement.evaluate(null, buildData));

        playerEqualsStatement = RuleStatementImplTest.getStatement(StatementType.NIF, "YOU", StatementVerbType.STATE_EQUALS, "BUILT");
        compiledStatement = StatementCompiler.compileStatement(model, playerEqualsStatement, Andrea);

        // WITH NIF AND BUILT
        p.setPlayerState(PlayerState.BUILT);
        assert (!compiledStatement.evaluate(moveData, null));
        assert (!compiledStatement.evaluate(null, buildData));

        p.setPlayerState(PlayerState.TURN_STARTED);

        assert (compiledStatement.evaluate(moveData, null));
        assert (compiledStatement.evaluate(null, buildData));

        p.setPlayerState(PlayerState.MOVED);

        assert (compiledStatement.evaluate(moveData, null));
        assert (compiledStatement.evaluate(null, buildData));

        p.setPlayerState(PlayerState.FIRST_BUILT);

        assert (compiledStatement.evaluate(moveData, null));
        assert (compiledStatement.evaluate(null, buildData));

        p.setPlayerState(PlayerState.END_TURN);

        assert (compiledStatement.evaluate(moveData, null));
        assert (compiledStatement.evaluate(null, buildData));


    }
    /**
     * Testing the statement STATE_EQUALS when the object is FIRST_BUILT.
     * If the Statement type is IF the evaluation should be true.
     * If the Statement type is NIF the evaluation should be false.
     */
    @Test
    void stateEquals_Test4(){

        RuleStatement playerEqualsStatement = RuleStatementImplTest.getStatement(StatementType.IF, "YOU", StatementVerbType.STATE_EQUALS, "FIRST_BUILT");
        LambdaStatement compiledStatement = StatementCompiler.compileStatement(model, playerEqualsStatement, Andrea);

        Player p = Andrea;
        MoveData moveData = new MoveData(p, null, null);
        BuildData buildData = new BuildData(p, null, null, null);

        // WITH IF AND FIRST_BUILT
        p.setPlayerState(PlayerState.FIRST_BUILT);
        assert (compiledStatement.evaluate(moveData, null));
        assert (compiledStatement.evaluate(null, buildData));

        p.setPlayerState(PlayerState.TURN_STARTED);

        assert (!compiledStatement.evaluate(moveData, null));
        assert (!compiledStatement.evaluate(null, buildData));

        p.setPlayerState(PlayerState.MOVED);

        assert (!compiledStatement.evaluate(moveData, null));
        assert (!compiledStatement.evaluate(null, buildData));

        p.setPlayerState(PlayerState.BUILT);

        assert (!compiledStatement.evaluate(moveData, null));
        assert (!compiledStatement.evaluate(null, buildData));

        p.setPlayerState(PlayerState.END_TURN);

        assert (!compiledStatement.evaluate(moveData, null));
        assert (!compiledStatement.evaluate(null, buildData));

        playerEqualsStatement = RuleStatementImplTest.getStatement(StatementType.NIF, "YOU", StatementVerbType.STATE_EQUALS, "FIRST_BUILT");
        compiledStatement = StatementCompiler.compileStatement(model, playerEqualsStatement, Andrea);

        // WITH NIF AND FIRST_BUILT
        p.setPlayerState(PlayerState.FIRST_BUILT);
        assert (!compiledStatement.evaluate(moveData, null));
        assert (!compiledStatement.evaluate(null, buildData));

        p.setPlayerState(PlayerState.TURN_STARTED);

        assert (compiledStatement.evaluate(moveData, null));
        assert (compiledStatement.evaluate(null, buildData));

        p.setPlayerState(PlayerState.MOVED);

        assert (compiledStatement.evaluate(moveData, null));
        assert (compiledStatement.evaluate(null, buildData));

        p.setPlayerState(PlayerState.BUILT);

        assert (compiledStatement.evaluate(moveData, null));
        assert (compiledStatement.evaluate(null, buildData));

        p.setPlayerState(PlayerState.END_TURN);

        assert (compiledStatement.evaluate(moveData, null));
        assert (compiledStatement.evaluate(null, buildData));
    }
    /**
     * Testing the statement STATE_EQUALS when the object is END_TURN.
     * If the Statement type is IF the evaluation should be true.
     * If the Statement type is NIF the evaluation should be false.
     */
    @Test
    void stateEquals_Test5(){

        RuleStatement playerEqualsStatement = RuleStatementImplTest.getStatement(StatementType.IF, "YOU", StatementVerbType.STATE_EQUALS, "END_TURN");
        LambdaStatement compiledStatement = StatementCompiler.compileStatement(model, playerEqualsStatement, Andrea);

        Player p = Andrea;
        MoveData moveData = new MoveData(p, null, null);
        BuildData buildData = new BuildData(p, null, null, null);

        // WITH IF AND END_TURN
        p.setPlayerState(PlayerState.END_TURN);
        assert (compiledStatement.evaluate(moveData, null));
        assert (compiledStatement.evaluate(null, buildData));

        p.setPlayerState(PlayerState.TURN_STARTED);

        assert (!compiledStatement.evaluate(moveData, null));
        assert (!compiledStatement.evaluate(null, buildData));

        p.setPlayerState(PlayerState.MOVED);

        assert (!compiledStatement.evaluate(moveData, null));
        assert (!compiledStatement.evaluate(null, buildData));

        p.setPlayerState(PlayerState.BUILT);

        assert (!compiledStatement.evaluate(moveData, null));
        assert (!compiledStatement.evaluate(null, buildData));

        p.setPlayerState(PlayerState.FIRST_BUILT);

        assert (!compiledStatement.evaluate(moveData, null));
        assert (!compiledStatement.evaluate(null, buildData));

        playerEqualsStatement = RuleStatementImplTest.getStatement(StatementType.NIF, "YOU", StatementVerbType.STATE_EQUALS, "END_TURN");
        compiledStatement = StatementCompiler.compileStatement(model, playerEqualsStatement, Andrea);

        // WITH NIF AND END_TURN
        p.setPlayerState(PlayerState.END_TURN);
        assert (!compiledStatement.evaluate(moveData, null));
        assert (!compiledStatement.evaluate(null, buildData));

        p.setPlayerState(PlayerState.TURN_STARTED);

        assert (compiledStatement.evaluate(moveData, null));
        assert (compiledStatement.evaluate(null, buildData));

        p.setPlayerState(PlayerState.MOVED);

        assert (compiledStatement.evaluate(moveData, null));
        assert (compiledStatement.evaluate(null, buildData));

        p.setPlayerState(PlayerState.BUILT);

        assert (compiledStatement.evaluate(moveData, null));
        assert (compiledStatement.evaluate(null, buildData));

        p.setPlayerState(PlayerState.FIRST_BUILT);

        assert (compiledStatement.evaluate(moveData, null));
        assert (compiledStatement.evaluate(null, buildData));
    }
    /**
     * Testing the Statement Verb HAS_FLAG:
     * When the obj is not a flag contained in Player the evaluation should be false with the Statement type IF, true if contained.
     * When the obj is not a flag contained in Player the evaluation should be true with the Statement type NIF, false if contained.
     */
    @Test
    void hasFlag_Test1(){
        RuleStatement playerEqualsStatement = RuleStatementImplTest.getStatement(StatementType.NIF, "YOU", StatementVerbType.HAS_FLAG, "MOVED_UP_ONCE");
        LambdaStatement compiledStatement = StatementCompiler.compileStatement(model, playerEqualsStatement, Andrea);

        Player p = Andrea;
        MoveData moveData = new MoveData(p, null, null);
        BuildData buildData = new BuildData(p, null, null, null);

        assert (compiledStatement.evaluate(moveData, null));
        assert (compiledStatement.evaluate(null, buildData));

        playerEqualsStatement = RuleStatementImplTest.getStatement(StatementType.IF, "YOU", StatementVerbType.HAS_FLAG, "MOVED_UP_ONCE");
        compiledStatement = StatementCompiler.compileStatement(model, playerEqualsStatement, Andrea);


        assert (!compiledStatement.evaluate(moveData, null));
        assert (!compiledStatement.evaluate(null, buildData));

        p.addFlag(PlayerFlag.MOVED_UP_ONCE);

        assert (compiledStatement.evaluate(moveData, null));
        assert (compiledStatement.evaluate(null, buildData));

        playerEqualsStatement = RuleStatementImplTest.getStatement(StatementType.NIF, "YOU", StatementVerbType.HAS_FLAG, "MOVED_UP_ONCE");
        compiledStatement = StatementCompiler.compileStatement(model, playerEqualsStatement, Andrea);

        assert (!compiledStatement.evaluate(moveData, null));
        assert (!compiledStatement.evaluate(null, buildData));

    }
    /**
     * Testing the Statement Verb HAS_FLAG:
     * When the obj is not a flag contained in CardOwner the evaluation should be false with the Statement type IF, true if contained.
     * When the obj is not a flag contained in CardOwner the evaluation should be true with the Statement type NIF, false if contained.
     */
    @Test
    void hasFlag_Test2(){
        RuleStatement playerEqualsStatement = RuleStatementImplTest.getStatement(StatementType.NIF, "CARD_OWNER", StatementVerbType.HAS_FLAG, "MOVED_UP_ONCE");
        LambdaStatement compiledStatement = StatementCompiler.compileStatement(model, playerEqualsStatement, Andrea);

        Player p = Andrea;
        MoveData moveData = new MoveData(p, null, null);
        BuildData buildData = new BuildData(p, null, null, null);

        assert (compiledStatement.evaluate(moveData, null));
        assert (compiledStatement.evaluate(null, buildData));

        playerEqualsStatement = RuleStatementImplTest.getStatement(StatementType.IF, "CARD_OWNER", StatementVerbType.HAS_FLAG, "MOVED_UP_ONCE");
        compiledStatement = StatementCompiler.compileStatement(model, playerEqualsStatement, Andrea);


        assert (!compiledStatement.evaluate(moveData, null));
        assert (!compiledStatement.evaluate(null, buildData));

        p.addFlag(PlayerFlag.MOVED_UP_ONCE);

        assert (compiledStatement.evaluate(moveData, null));
        assert (compiledStatement.evaluate(null, buildData));

        playerEqualsStatement = RuleStatementImplTest.getStatement(StatementType.NIF, "CARD_OWNER", StatementVerbType.HAS_FLAG, "MOVED_UP_ONCE");
        compiledStatement = StatementCompiler.compileStatement(model, playerEqualsStatement, Andrea);

        assert (!compiledStatement.evaluate(moveData, null));
        assert (!compiledStatement.evaluate(null, buildData));

    }
    /**
     * Testing the Statement Verb MOVE_LENGTH when the obj is 0.
     * If there is a single move, then the evaluation should be false with the Statement Type IF.
     * If there is a single move, then the evaluation should be true with the Statement Type NIF.
     */
    @Test
    void moveLenght_Test1(){
        RuleStatement playerEqualsStatement = RuleStatementImplTest.getStatement(StatementType.IF, "YOU", StatementVerbType.MOVE_LENGTH, "0");
        LambdaStatement compiledStatement = StatementCompiler.compileStatement(model, playerEqualsStatement, Andrea);

        Player p = Andrea;
        List<Point> moves = new ArrayList<>();

        moves.add(new Point(1,1));

        MoveData moveData = new MoveData(p, AndreaW1, moves);

        assert (!compiledStatement.evaluate(moveData, null));

        playerEqualsStatement = RuleStatementImplTest.getStatement(StatementType.NIF, "YOU", StatementVerbType.MOVE_LENGTH, "0");
        compiledStatement = StatementCompiler.compileStatement(model, playerEqualsStatement, Andrea);

        assert (compiledStatement.evaluate(moveData, null));
    }
    /**
     * Testing the Statement Verb MOVE_LENGTH when the obj is 1.
     * If there is a single move, then the evaluation should be true with the Statement Type IF.
     * If there is a single move, then the evaluation should be false with the Statement Type NIF.
     * If there are two moves, then the evaluation should be false with the Statement Type IF.
     * If there are two moves, then the evaluation should be true with the Statement Type NIF.
     */
    @Test
    void moveLenght_Test2(){
        RuleStatement playerEqualsStatement = RuleStatementImplTest.getStatement(StatementType.IF, "YOU", StatementVerbType.MOVE_LENGTH, "1");
        LambdaStatement compiledStatement = StatementCompiler.compileStatement(model, playerEqualsStatement, Andrea);


        //MOVE FROM 0,0 TO 1,1
        Player p = Andrea;
        List<Point> moves = new ArrayList<>();

        moves.add(new Point(1,1));

        MoveData moveData = new MoveData(p, AndreaW1, moves);

        assert (compiledStatement.evaluate(moveData, null));

        moves.add(new Point(2,2));

        moveData = new MoveData(p, AndreaW1, moves);

        assert (!compiledStatement.evaluate(moveData, null));

        moves.clear();

        playerEqualsStatement = RuleStatementImplTest.getStatement(StatementType.NIF, "YOU", StatementVerbType.MOVE_LENGTH, "1");
        compiledStatement = StatementCompiler.compileStatement(model, playerEqualsStatement, Andrea);

        moves.add(new Point(1,1));

        moveData = new MoveData(p, AndreaW1, moves);

        assert (!compiledStatement.evaluate(moveData, null));

        moves.add(new Point(2,2));

        moveData = new MoveData(p, AndreaW1, moves);

        assert (compiledStatement.evaluate(moveData, null));

    }
    /**
     * Testing the Statement Verb MOVE_LENGTH when the obj is 2.
     * If there is a single move, then the evaluation should be false with the Statement Type IF.
     * If there is a single move, then the evaluation should be true with the Statement Type NIF.
     * If there are two moves, then the evaluation should be true with the Statement Type IF.
     * If there are two moves, then the evaluation should be false with the Statement Type NIF.
     * If there are more than two moves, then the evaluation should be false with the Statement Type IF, true with NIF.
     */
    @Test
    void moveLenght_Test3(){
        RuleStatement playerEqualsStatement = RuleStatementImplTest.getStatement(StatementType.IF, "YOU", StatementVerbType.MOVE_LENGTH, "2");
        LambdaStatement compiledStatement = StatementCompiler.compileStatement(model, playerEqualsStatement, Andrea);

        //MOVE FROM 0,0 TO 2,2 WITH TWO ADJACENT MOVES

        Player p = Andrea;
        List<Point> moves = new ArrayList<>();

        moves.add(new Point(1,1));

        MoveData moveData = new MoveData(p, AndreaW1, moves);

        assert (!compiledStatement.evaluate(moveData, null));

        moves.add(new Point(2,2));

        moveData = new MoveData(p, AndreaW1, moves);

        assert (compiledStatement.evaluate(moveData, null));

        moves.add(new Point(3,3));

        moveData = new MoveData(p, AndreaW1, moves);

        assert (!compiledStatement.evaluate(moveData, null));

        moves.add(new Point(4,4));

        moveData = new MoveData(p, AndreaW1, moves);

        assert (!compiledStatement.evaluate(moveData, null));

        moves.clear();


        playerEqualsStatement = RuleStatementImplTest.getStatement(StatementType.NIF, "YOU", StatementVerbType.MOVE_LENGTH, "2");
        compiledStatement = StatementCompiler.compileStatement(model, playerEqualsStatement, Andrea);

        moves.add(new Point(1,1));

        moveData = new MoveData(p, AndreaW1, moves);

        assert (compiledStatement.evaluate(moveData, null));

        moves.add(new Point(2,2));

        moveData = new MoveData(p, AndreaW1, moves);

        assert (!compiledStatement.evaluate(moveData, null));

        moves.add(new Point(3,3));

        moveData = new MoveData(p, AndreaW1, moves);

        assert (compiledStatement.evaluate(moveData, null));

        moves.add(new Point(4,4));

        moveData = new MoveData(p, AndreaW1, moves);

        assert (compiledStatement.evaluate(moveData, null));
    }
    /*
        Testing with a board having 0 buildings on it that exists delta more than 0 is always false
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


        List<Point> moves = new ArrayList<>();
        Point point1 = new Point(4,4);
        Point point2 = new Point(3,4);
        Point point3 = new Point(4, 3);
        moves.add(point1);
        moves.add(point2);
        moves.add(point3);
        MoveData moveData = new MoveData(Mirko, MirkoW2, moves);

        RuleStatement ruleStatement = RuleStatementImplTest.getStatement(StatementType.IF,"YOU", StatementVerbType.EXISTS_DELTA_MORE,"0");
        LambdaStatement lambdaStatement = StatementCompiler.compileStatement(model, ruleStatement, Mirko);
        assertFalse(lambdaStatement.evaluate(moveData, null));

        ruleStatement = RuleStatementImplTest.getStatement(StatementType.NIF,"YOU", StatementVerbType.EXISTS_DELTA_MORE,"0");
        lambdaStatement = StatementCompiler.compileStatement(model, ruleStatement, Mirko);
        assertTrue(lambdaStatement.evaluate(moveData, null));

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

        RuleStatement ruleStatement = RuleStatementImplTest.getStatement(StatementType.NIF,"YOU", StatementVerbType.EXISTS_DELTA_MORE,"0");
        LambdaStatement lambdaStatement = StatementCompiler.compileStatement(model, ruleStatement, Mirko);
        assertTrue(lambdaStatement.evaluate(moveData, null));

        ruleStatement = RuleStatementImplTest.getStatement(StatementType.IF,"YOU", StatementVerbType.EXISTS_DELTA_MORE,"0");
        lambdaStatement = StatementCompiler.compileStatement(model, ruleStatement, Mirko);
        assertFalse(lambdaStatement.evaluate(moveData, null));

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

        RuleStatement ruleStatement = RuleStatementImplTest.getStatement(StatementType.IF,"YOU", StatementVerbType.EXISTS_DELTA_MORE,"0");
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

        RuleStatement ruleStatement = RuleStatementImplTest.getStatement(StatementType.IF,"YOU", StatementVerbType.EXISTS_DELTA_MORE,"2");
        LambdaStatement lambdaStatement = StatementCompiler.compileStatement(model, ruleStatement, Andrea);

        assertTrue(lambdaStatement.evaluate(moveData, null));

        ruleStatement = RuleStatementImplTest.getStatement(StatementType.NIF,"YOU", StatementVerbType.EXISTS_DELTA_MORE,"2");
        lambdaStatement = StatementCompiler.compileStatement(model, ruleStatement, Andrea);

        assertFalse(lambdaStatement.evaluate(moveData, null));


    }
    /*
       Testing in a path with some first floors and second floors and third floors that
        it doesnt exist a delta more than 3
        or that exists delta more than 2 is true
    */
    @Test
    void existsDeltaMore_Test6(){
        /*
          0    1     2    3    4
        +----+----+----+----+----+
    0   | A1 | SF |    |    |    |
        +----+----+----+----+----+
    1   |    |    |    |    |    |
        +----+----+----+----+----+
    2   |    | B1 | A2 | B2 |    |
        |    | FF |    | TF |    |
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
        model.getBoard().getCell(new Point(1, 2)).setWorker(id);



        id = model.getBoard().getCell(new Point(3, 2)).getWorkerID();
        model.getBoard().getCell(new Point(3, 2)).removeWorker();
        model.getBoard().getCell(new Point(3, 2)).addBuilding(BuildingType.FIRST_FLOOR);
        model.getBoard().getCell(new Point(3, 2)).addBuilding(BuildingType.SECOND_FLOOR);
        model.getBoard().getCell(new Point(3, 2)).addBuilding(BuildingType.THIRD_FLOOR);
        model.getBoard().getCell(new Point(3, 2)).setWorker(id);


        RuleStatement ruleStatement = RuleStatementImplTest.getStatement(StatementType.NIF,"YOU", StatementVerbType.EXISTS_DELTA_MORE,"3");
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

        RuleStatement ruleStatement = RuleStatementImplTest.getStatement(StatementType.NIF,"YOU", StatementVerbType.EXISTS_DELTA_LESS,"-1");
        LambdaStatement lambdaStatement = StatementCompiler.compileStatement(model, ruleStatement, Andrea);
        assertTrue(lambdaStatement.evaluate(moveData, null));

        ruleStatement = RuleStatementImplTest.getStatement(StatementType.IF,"YOU", StatementVerbType.EXISTS_DELTA_LESS,"-1");
        lambdaStatement = StatementCompiler.compileStatement(model, ruleStatement, Andrea);
        assertFalse(lambdaStatement.evaluate(moveData, null));
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
       Testing in a path with some first floors and second floors and third floors that
        it doesnt exist a delta less than -3
        or that exists delta less than -2 is true
    */
    @Test
    void existsDeltaLess_Test6(){
        /*
          0    1     2    3    4
        +----+----+----+----+----+
    0   | A1 | SF |    |    |    |
        +----+----+----+----+----+
    1   |    |    |    |    |    |
        +----+----+----+----+----+
    2   |    | B1 | A2 | B2 |    |
        |    | FF |    | TF |    |
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
        model.getBoard().getCell(new Point(1, 2)).setWorker(id);



        id = model.getBoard().getCell(new Point(3, 2)).getWorkerID();
        model.getBoard().getCell(new Point(3, 2)).removeWorker();
        model.getBoard().getCell(new Point(3, 2)).addBuilding(BuildingType.FIRST_FLOOR);
        model.getBoard().getCell(new Point(3, 2)).addBuilding(BuildingType.SECOND_FLOOR);
        model.getBoard().getCell(new Point(3, 2)).addBuilding(BuildingType.THIRD_FLOOR);
        model.getBoard().getCell(new Point(3, 2)).setWorker(id);




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


        RuleStatement ruleStatement = RuleStatementImplTest.getStatement(StatementType.IF,"YOU", StatementVerbType.EXISTS_DELTA_LESS,"-2");
        LambdaStatement lambdaStatement = StatementCompiler.compileStatement(model, ruleStatement, Andrea);
        assertTrue(lambdaStatement.evaluate(moveData, null));

        ruleStatement = RuleStatementImplTest.getStatement(StatementType.NIF,"YOU", StatementVerbType.EXISTS_DELTA_LESS,"-3");
        lambdaStatement = StatementCompiler.compileStatement(model, ruleStatement, Andrea);
        assertTrue(lambdaStatement.evaluate(moveData, null));
    }
    /**
     * Testing the start position in two cases:
     * - The Worker is on a Ground Level and the evaluation should be true
     * - The Worker is on a First Floor Level and the evaluation should be false
     *
     * Testing also NIF with false in the first case and true in the other
     */
    @Test
    void laevelType_Test1(){
        RuleStatement ruleStatement = RuleStatementImplTest.getStatement(StatementType.NIF,"START_POSITION", StatementVerbType.LEVEL_TYPE,"GROUND");
        LambdaStatement compiledStatement = StatementCompiler.compileStatement(model, ruleStatement, Andrea);

        Player p = Andrea;

        MoveData moveData = new MoveData(p, AndreaW1, null);

        assert (!compiledStatement.evaluate(moveData, null));


        ruleStatement = RuleStatementImplTest.getStatement(StatementType.IF,"START_POSITION", StatementVerbType.LEVEL_TYPE,"GROUND");
        compiledStatement = StatementCompiler.compileStatement(model, ruleStatement, Andrea);


        assert (compiledStatement.evaluate(moveData, null));

        Point pos = AndreaW1.getPosition();
        model.getBoard().getCell(pos).removeWorker();
        model.getBoard().getCell(pos).addBuilding(BuildingType.FIRST_FLOOR);
        model.getBoard().getCell(pos).setWorker(AndreaW1.getID());

        assert (!compiledStatement.evaluate(moveData, null));

        ruleStatement = RuleStatementImplTest.getStatement(StatementType.IF,"START_POSITION", StatementVerbType.LEVEL_TYPE,"FIRST_FLOOR");
        compiledStatement = StatementCompiler.compileStatement(model, ruleStatement, Andrea);

        assert (compiledStatement.evaluate(moveData, null));

        ruleStatement = RuleStatementImplTest.getStatement(StatementType.NIF,"START_POSITION", StatementVerbType.LEVEL_TYPE,"GROUND");
        compiledStatement = StatementCompiler.compileStatement(model, ruleStatement, Andrea);

        assert (compiledStatement.evaluate(moveData, null));
    }
    /**
     * Testing the final position after a move:
     * The Worker moves from 0,0 (GROUND) to 1,1 (FIRST_FLOOR)
     * The evaluation should be true with FIRST:FLOOR as FINAL_POSITION
     */
    @Test
    void testLevelType2(){

        RuleStatement ruleStatement = RuleStatementImplTest.getStatement(StatementType.IF,"FINAL_POSITION", StatementVerbType.LEVEL_TYPE,"FIRST_FLOOR");
        LambdaStatement compiledStatement = StatementCompiler.compileStatement(model, ruleStatement, Andrea);

        Player p = Andrea;
        List<Point> moves = new ArrayList<>();

        Point pos = new Point(1,1);
        moves.add(pos);

        model.getBoard().getCell(pos).addBuilding(BuildingType.FIRST_FLOOR);

        MoveData moveData = new MoveData(p, AndreaW1, moves);

        assert (compiledStatement.evaluate(moveData, null));

        ruleStatement = RuleStatementImplTest.getStatement(StatementType.NIF,"FINAL_POSITION", StatementVerbType.LEVEL_TYPE,"FIRST_FLOOR");
        compiledStatement = StatementCompiler.compileStatement(model, ruleStatement, Andrea);

        assert (!compiledStatement.evaluate(moveData, null));

        model.getBoard().getCell(pos).addBuilding(BuildingType.SECOND_FLOOR);

        assert (compiledStatement.evaluate(moveData, null));

        ruleStatement = RuleStatementImplTest.getStatement(StatementType.IF,"FINAL_POSITION", StatementVerbType.LEVEL_TYPE,"SECOND_FLOOR");
        compiledStatement = StatementCompiler.compileStatement(model, ruleStatement, Andrea);

        assert (compiledStatement.evaluate(moveData, null));

        ruleStatement = RuleStatementImplTest.getStatement(StatementType.NIF,"FINAL_POSITION", StatementVerbType.LEVEL_TYPE,"SECOND_FLOOR");
        compiledStatement = StatementCompiler.compileStatement(model, ruleStatement, Andrea);

        assert (!compiledStatement.evaluate(moveData, null));

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

        RuleStatement ruleStatement = RuleStatementImplTest.getStatement(StatementType.NIF,"YOU", StatementVerbType.INTERACTION_NUM,"7");
        LambdaStatement lambdaStatement = StatementCompiler.compileStatement(model, ruleStatement, Andrea);
        assertFalse(lambdaStatement.evaluate(moveData, null));

        ruleStatement = RuleStatementImplTest.getStatement(StatementType.IF,"YOU", StatementVerbType.INTERACTION_NUM,"7");
        lambdaStatement = StatementCompiler.compileStatement(model, ruleStatement, Andrea);
        assertTrue(lambdaStatement.evaluate(moveData, null));
    }
    /*
       Test position equals when concluding a move in the start position
    */
    @Test
    void positionEquals_Test1(){

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

        List<Point> moves = new ArrayList<>();
        Point point1 = new Point(1,1);
        Point point2 = new Point(0,1);
        Point point3 = new Point(0,2);
        Point point4 = new Point(1, 2);

        moves.add(point1);
        moves.add(point2);
        moves.add(point3);
        moves.add(point4);

        MoveData moveData = new MoveData(Matteo, MatteoW1, moves);

        RuleStatement ruleStatement1 = RuleStatementImplTest.getStatement(StatementType.IF, "FINAL_POSITION", StatementVerbType.POSITION_EQUALS, "START_POSITION");
        RuleStatement ruleStatement2 = RuleStatementImplTest.getStatement(StatementType.IF, "FINAL_POSITION", StatementVerbType.POSITION_EQUALS, "OPPONENTS");
        LambdaStatement lambdaStatement1 = StatementCompiler.compileStatement(model, ruleStatement1, Matteo);
        LambdaStatement lambdaStatement2 = StatementCompiler.compileStatement(model, ruleStatement2, Matteo);

        assertTrue(lambdaStatement1.evaluate(moveData, null));
        assertFalse(lambdaStatement2.evaluate(moveData, null));

        ruleStatement1 = RuleStatementImplTest.getStatement(StatementType.NIF, "FINAL_POSITION", StatementVerbType.POSITION_EQUALS, "START_POSITION");
        ruleStatement2 = RuleStatementImplTest.getStatement(StatementType.NIF, "FINAL_POSITION", StatementVerbType.POSITION_EQUALS, "OPPONENTS");
        lambdaStatement1 = StatementCompiler.compileStatement(model, ruleStatement1, Matteo);
        lambdaStatement2 = StatementCompiler.compileStatement(model, ruleStatement2, Matteo);

        assertFalse(lambdaStatement1.evaluate(moveData, null));
        assertTrue(lambdaStatement2.evaluate(moveData, null));
    }
    /*
      Test position equals when concluding a move in an opponents position
   */
    @Test
    void positionEquals_Test2(){

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

        List<Point> moves = new ArrayList<>();
        Point point1 = new Point(1,1);
        Point point2 = new Point(2,1);
        Point point3 = new Point(2,2);

        moves.add(point1);
        moves.add(point2);
        moves.add(point3);


        MoveData moveData = new MoveData(Matteo, MatteoW1, moves);

        RuleStatement ruleStatement1 = RuleStatementImplTest.getStatement(StatementType.IF, "FINAL_POSITION", StatementVerbType.POSITION_EQUALS, "START_POSITION");
        RuleStatement ruleStatement2 = RuleStatementImplTest.getStatement(StatementType.IF, "FINAL_POSITION", StatementVerbType.POSITION_EQUALS, "OPPONENTS");
        LambdaStatement lambdaStatement1 = StatementCompiler.compileStatement(model, ruleStatement1, Matteo);
        LambdaStatement lambdaStatement2 = StatementCompiler.compileStatement(model, ruleStatement2, Matteo);

        assertFalse(lambdaStatement1.evaluate(moveData, null));
        assertTrue(lambdaStatement2.evaluate(moveData, null));

        ruleStatement1 = RuleStatementImplTest.getStatement(StatementType.NIF, "FINAL_POSITION", StatementVerbType.POSITION_EQUALS, "START_POSITION");
        ruleStatement2 = RuleStatementImplTest.getStatement(StatementType.NIF, "FINAL_POSITION", StatementVerbType.POSITION_EQUALS, "OPPONENTS");
        lambdaStatement1 = StatementCompiler.compileStatement(model, ruleStatement1, Matteo);
        lambdaStatement2 = StatementCompiler.compileStatement(model, ruleStatement2, Matteo);

        assertTrue(lambdaStatement1.evaluate(moveData, null));
        assertFalse(lambdaStatement2.evaluate(moveData, null));
    }
    /*
        Test position equals when concluding a move in an empty cell
    */
    @Test
    void positionEquals_Test3(){

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

        List<Point> moves = new ArrayList<>();
        Point point1 = new Point(1,1);
        Point point2 = new Point(2,1);


        moves.add(point1);
        moves.add(point2);


        MoveData moveData = new MoveData(Matteo, MatteoW1, moves);

        RuleStatement ruleStatement1 = RuleStatementImplTest.getStatement(StatementType.IF, "FINAL_POSITION", StatementVerbType.POSITION_EQUALS, "START_POSITION");
        RuleStatement ruleStatement2 = RuleStatementImplTest.getStatement(StatementType.IF, "FINAL_POSITION", StatementVerbType.POSITION_EQUALS, "OPPONENTS");
        LambdaStatement lambdaStatement1 = StatementCompiler.compileStatement(model, ruleStatement1, Matteo);
        LambdaStatement lambdaStatement2 = StatementCompiler.compileStatement(model, ruleStatement2, Matteo);

        assertFalse(lambdaStatement1.evaluate(moveData, null));
        assertFalse(lambdaStatement2.evaluate(moveData, null));

        ruleStatement1 = RuleStatementImplTest.getStatement(StatementType.NIF, "FINAL_POSITION", StatementVerbType.POSITION_EQUALS, "START_POSITION");
        ruleStatement2 = RuleStatementImplTest.getStatement(StatementType.NIF, "FINAL_POSITION", StatementVerbType.POSITION_EQUALS, "OPPONENTS");
        lambdaStatement1 = StatementCompiler.compileStatement(model, ruleStatement1, Matteo);
        lambdaStatement2 = StatementCompiler.compileStatement(model, ruleStatement2, Matteo);

        assertTrue(lambdaStatement1.evaluate(moveData, null));
        assertTrue(lambdaStatement2.evaluate(moveData, null));
    }
    /*
        Test position equals when concluding a move in a cell with your other worker
    */
    @Test
    void positionEquals_Test4(){

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

        List<Point> moves = new ArrayList<>();
        Point point1 = new Point(1,1);
        Point point2 = new Point(2,1);
        Point point3 = new Point(3,1);
        Point point4 = new Point(3,2);


        moves.add(point1);
        moves.add(point2);
        moves.add(point3);
        moves.add(point4);


        MoveData moveData = new MoveData(Matteo, MatteoW1, moves);

        RuleStatement ruleStatement1 = RuleStatementImplTest.getStatement(StatementType.IF, "FINAL_POSITION", StatementVerbType.POSITION_EQUALS, "START_POSITION");
        RuleStatement ruleStatement2 = RuleStatementImplTest.getStatement(StatementType.IF, "FINAL_POSITION", StatementVerbType.POSITION_EQUALS, "OPPONENTS");
        LambdaStatement lambdaStatement1 = StatementCompiler.compileStatement(model, ruleStatement1, Matteo);
        LambdaStatement lambdaStatement2 = StatementCompiler.compileStatement(model, ruleStatement2, Matteo);

        assertFalse(lambdaStatement1.evaluate(moveData, null));
        assertFalse(lambdaStatement2.evaluate(moveData, null));

        ruleStatement1 = RuleStatementImplTest.getStatement(StatementType.NIF, "FINAL_POSITION", StatementVerbType.POSITION_EQUALS, "START_POSITION");
        ruleStatement2 = RuleStatementImplTest.getStatement(StatementType.NIF, "FINAL_POSITION", StatementVerbType.POSITION_EQUALS, "OPPONENTS");
        lambdaStatement1 = StatementCompiler.compileStatement(model, ruleStatement1, Matteo);
        lambdaStatement2 = StatementCompiler.compileStatement(model, ruleStatement2, Matteo);

        assertTrue(lambdaStatement1.evaluate(moveData, null));
        assertTrue(lambdaStatement2.evaluate(moveData, null));
    }
    /*
          Tests build num with 1 building
    */
    @Test
    void buildNum_Test1(){
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
        Map<Point,List<BuildingType>> builds = new HashMap<>();

        Point point1 = new Point(2,3);
        List<BuildingType> buildsInPoint = new ArrayList<>();
        buildsInPoint.add(BuildingType.DOME);
        builds.put(point1, buildsInPoint);



        BuildData buildData = new BuildData(Matteo, MatteoW1, builds, null);

        RuleStatement ruleStatement = RuleStatementImplTest.getStatement(StatementType.IF, "YOU", StatementVerbType.BUILD_NUM, "1");
        LambdaStatement lambdaStatement = StatementCompiler.compileStatement(model, ruleStatement, Matteo);

        assertTrue(lambdaStatement.evaluate(null, buildData));

        ruleStatement = RuleStatementImplTest.getStatement(StatementType.NIF, "YOU", StatementVerbType.BUILD_NUM, "1");
        lambdaStatement = StatementCompiler.compileStatement(model, ruleStatement, Matteo);

        assertFalse(lambdaStatement.evaluate(null, buildData));

        for(int i =0; i<1; i++){
            ruleStatement = RuleStatementImplTest.getStatement(StatementType.IF, "YOU", StatementVerbType.BUILD_NUM, String.valueOf(i));
            lambdaStatement = StatementCompiler.compileStatement(model, ruleStatement, Matteo);

            assertFalse(lambdaStatement.evaluate(null, buildData));

            ruleStatement = RuleStatementImplTest.getStatement(StatementType.NIF, "YOU", StatementVerbType.BUILD_NUM, String.valueOf(i));
            lambdaStatement = StatementCompiler.compileStatement(model, ruleStatement, Matteo);

            assertTrue(lambdaStatement.evaluate(null, buildData));
        }
        for(int i =2; i<10; i++){
            ruleStatement = RuleStatementImplTest.getStatement(StatementType.IF, "YOU", StatementVerbType.BUILD_NUM, String.valueOf(i));
            lambdaStatement = StatementCompiler.compileStatement(model, ruleStatement, Matteo);

            assertFalse(lambdaStatement.evaluate(null, buildData));

            ruleStatement = RuleStatementImplTest.getStatement(StatementType.NIF, "YOU", StatementVerbType.BUILD_NUM, String.valueOf(i));
            lambdaStatement = StatementCompiler.compileStatement(model, ruleStatement, Matteo);

            assertTrue(lambdaStatement.evaluate(null, buildData));
        }



    }
    /*
        Tests build num with a lot of buildings
     */
    @Test
    void buildNum_Test2(){
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



        BuildData buildData = new BuildData(Matteo, MatteoW1, builds, null);

        RuleStatement ruleStatement = RuleStatementImplTest.getStatement(StatementType.IF, "YOU", StatementVerbType.BUILD_NUM, "10");
        LambdaStatement lambdaStatement = StatementCompiler.compileStatement(model, ruleStatement, Matteo);

        assertTrue(lambdaStatement.evaluate(null, buildData));

        ruleStatement = RuleStatementImplTest.getStatement(StatementType.NIF, "YOU", StatementVerbType.BUILD_NUM, "10");
        lambdaStatement = StatementCompiler.compileStatement(model, ruleStatement, Matteo);

        assertFalse(lambdaStatement.evaluate(null, buildData));

        for(int i =0; i<10; i++){
            ruleStatement = RuleStatementImplTest.getStatement(StatementType.IF, "YOU", StatementVerbType.BUILD_NUM, String.valueOf(i));
            lambdaStatement = StatementCompiler.compileStatement(model, ruleStatement, Matteo);

            assertFalse(lambdaStatement.evaluate(null, buildData));

            ruleStatement = RuleStatementImplTest.getStatement(StatementType.NIF, "YOU", StatementVerbType.BUILD_NUM, String.valueOf(i));
            lambdaStatement = StatementCompiler.compileStatement(model, ruleStatement, Matteo);

            assertTrue(lambdaStatement.evaluate(null, buildData));
        }


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

            BuildData buildData = new BuildData(Matteo, MatteoW1, builds, null);

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



            BuildData buildData = new BuildData(Matteo, MatteoW1, builds, null);

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


            BuildData buildData = new BuildData(Matteo, MatteoW1, builds, null);

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
    2   | DM | B1 | A2 | B2 |    |
        +----+----+----+----+----+
    3   | TF | SF | FF | D2 |    |
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


            BuildData buildData = new BuildData(Matteo, MatteoW1, builds, null);

            RuleStatement ruleStatement = RuleStatementImplTest.getStatement(StatementType.IF, "YOU", StatementVerbType.BUILD_DOME_EXCEPT, lt.toString());
            LambdaStatement lambdaStatement = StatementCompiler.compileStatement(model, ruleStatement, Matteo);
            if(lt == LevelType.SECOND_FLOOR)
                assertFalse(lambdaStatement.evaluate(null, buildData));
            else
                assertTrue(lambdaStatement.evaluate(null, buildData));

            ruleStatement = RuleStatementImplTest.getStatement(StatementType.NIF, "YOU", StatementVerbType.BUILD_DOME_EXCEPT, lt.toString());
            lambdaStatement = StatementCompiler.compileStatement(model, ruleStatement, Matteo);
            if(lt == LevelType.SECOND_FLOOR)
                assertTrue(lambdaStatement.evaluate(null, buildData));
            else
                assertFalse(lambdaStatement.evaluate(null, buildData));
        }
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


            BuildData buildData = new BuildData(Matteo, MatteoW1, builds, null);

            RuleStatement ruleStatement = RuleStatementImplTest.getStatement(StatementType.IF, "YOU", StatementVerbType.BUILD_DOME, lt.toString());
            LambdaStatement lambdaStatement = StatementCompiler.compileStatement(model, ruleStatement, Matteo);
            assertTrue(lambdaStatement.evaluate(null, buildData));

            ruleStatement = RuleStatementImplTest.getStatement(StatementType.NIF, "YOU", StatementVerbType.BUILD_DOME, lt.toString());
            lambdaStatement = StatementCompiler.compileStatement(model, ruleStatement, Matteo);
            assertFalse(lambdaStatement.evaluate(null, buildData));
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


            BuildData buildData = new BuildData(Matteo, MatteoW1, builds, null);

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


            BuildData buildData = new BuildData(Matteo, MatteoW1, builds, null);

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



            BuildData buildData = new BuildData(Matteo, MatteoW1, builds, null);

            RuleStatement ruleStatement = RuleStatementImplTest.getStatement(StatementType.IF, "YOU", StatementVerbType.BUILD_DOME, lt.toString());
            LambdaStatement lambdaStatement = StatementCompiler.compileStatement(model, ruleStatement, Matteo);
            assertTrue(lambdaStatement.evaluate(null, buildData));

            ruleStatement = RuleStatementImplTest.getStatement(StatementType.NIF, "YOU", StatementVerbType.BUILD_DOME, lt.toString());
            lambdaStatement = StatementCompiler.compileStatement(model, ruleStatement, Matteo);
            assertFalse(lambdaStatement.evaluate(null, buildData));
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



            BuildData buildData = new BuildData(Matteo, MatteoW1, builds, null);

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


        BuildData buildData = new BuildData(Matteo, MatteoW1, builds, null);

        assertTrue(lambdaStatement.evaluate(null, buildData));
    }
    /*
        Testing building in same spot fails with a lot of buildings in diff spots
     */
    @Test
    void buildInSameSpot_Test1(){
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



        BuildData buildData = new BuildData(Matteo, MatteoW1, builds, null);

        RuleStatement ruleStatement = RuleStatementImplTest.getStatement(StatementType.IF, "YOU", StatementVerbType.BUILD_IN_SAME_SPOT, "ALL");
        LambdaStatement lambdaStatement = StatementCompiler.compileStatement(model, ruleStatement, Matteo);

        assertFalse(lambdaStatement.evaluate(null, buildData));

        ruleStatement = RuleStatementImplTest.getStatement(StatementType.NIF, "YOU", StatementVerbType.BUILD_IN_SAME_SPOT, "ALL");
        lambdaStatement = StatementCompiler.compileStatement(model, ruleStatement, Matteo);

        assertTrue(lambdaStatement.evaluate(null, buildData));

    }
    /*
        Testing build in same spot succeeds whaen building in just one spot
     */
    @Test
    void buildInSameSpot_Test2(){
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
        Map<Point,List<BuildingType>> builds = new HashMap<>();

        Point point4 = new Point(0, 2);
        List<BuildingType> buildsInPoint4 = new ArrayList<>();
        buildsInPoint4.add(BuildingType.FIRST_FLOOR);
        buildsInPoint4.add(BuildingType.SECOND_FLOOR);
        buildsInPoint4.add(BuildingType.THIRD_FLOOR);
        buildsInPoint4.add(BuildingType.DOME);
        builds.put(point4, buildsInPoint4);



        BuildData buildData = new BuildData(Matteo, MatteoW1, builds, null);

        RuleStatement ruleStatement = RuleStatementImplTest.getStatement(StatementType.IF, "YOU", StatementVerbType.BUILD_IN_SAME_SPOT, "ALL");
        LambdaStatement lambdaStatement = StatementCompiler.compileStatement(model, ruleStatement, Matteo);

        assertTrue(lambdaStatement.evaluate(null, buildData));

        ruleStatement = RuleStatementImplTest.getStatement(StatementType.NIF, "YOU", StatementVerbType.BUILD_IN_SAME_SPOT, "ALL");
        lambdaStatement = StatementCompiler.compileStatement(model, ruleStatement, Matteo);

        assertFalse(lambdaStatement.evaluate(null, buildData));



    }
}
