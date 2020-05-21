package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.cards.CardFactory;
import it.polimi.ingsw.server.cards.RuleEffect;
import it.polimi.ingsw.server.cards.RuleEffectImplTest;
import it.polimi.ingsw.server.cards.enums.AllowType;
import it.polimi.ingsw.server.cards.enums.EffectType;
import it.polimi.ingsw.server.cards.exceptions.InvalidCardException;
import it.polimi.ingsw.common.enums.BuildingType;
import it.polimi.ingsw.server.model.enums.LevelType;
import it.polimi.ingsw.server.model.enums.PlayerState;
import it.polimi.ingsw.server.model.exceptions.PlayerLostSignal;
import it.polimi.ingsw.server.model.exceptions.PlayerWonSignal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EffectCompilerTest {

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
    0   | A1 | FF | SF |    |    |
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
    void setUp() throws InvalidCardException {
        List<String> players = new ArrayList<>();
        players.add("Andrea");
        players.add("Matteo");
        players.add("Mirko");
        model = new InternalModel(players, CardFactory.getInstance());
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

        model.getBoard().getCell(new Point(1,0)).addBuilding(BuildingType.FIRST_FLOOR);

        model.getBoard().getCell(new Point(2,0)).addBuilding(BuildingType.FIRST_FLOOR);
        model.getBoard().getCell(new Point(2,0)).addBuilding(BuildingType.SECOND_FLOOR);
    }

    /*
        Tests an allow move effect both in simulation and in non simulation
     */
    @Test
    void allowEffectMove_Test1() throws PlayerWonSignal, PlayerLostSignal {

        RuleEffect ruleEffect = RuleEffectImplTest.getRuleEffect(EffectType.ALLOW, AllowType.STANDARD, PlayerState.MOVED, null);

        LambdaEffect lambdaEffect = EffectCompiler.compileEffect(model, ruleEffect);

        List<Point> moves = new ArrayList<>();
        Point point1 = new Point(1,0);
        Point point2 = new Point(2,0);

        moves.add(point1);
        moves.add(point2);

        MoveData moveData = new MoveData(Andrea, AndreaW1, moves);


        assertTrue(lambdaEffect.apply(moveData,null,true));
        assertEquals(AndreaW1.getPosition(), new Point(0,0));
        assertEquals(model.getBoard().getCell(new Point(0,0)).getWorkerID(), AndreaW1.getID());
        assertFalse(model.getBoard().getCell(point2).hasWorker());
        assertEquals(Andrea.getState(), PlayerState.TURN_STARTED);

        assertTrue(lambdaEffect.apply(moveData,null,false));
        assertEquals(AndreaW1.getPosition(), point2);
        assertEquals(AndreaW1.getID(),model.getBoard().getCell(point2).getWorkerID());
        assertFalse(model.getBoard().getCell( new Point(0,0)).hasWorker());
        assertEquals(Andrea.getState(), PlayerState.MOVED);

    }


    /*
        Tests an allow build effect both in simulation and in non simulation
        in a case where the worker can build
     */
    @Test
    void allowEffectBuild_Test1() throws PlayerWonSignal, PlayerLostSignal {
          /*
          0    1     2    3    4
        +----+----+----+----+----+
    0   | A1 | FF | SF |    |    |
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
        RuleEffect ruleEffect = RuleEffectImplTest.getRuleEffect(EffectType.ALLOW, AllowType.STANDARD, PlayerState.BUILT, null);

        LambdaEffect lambdaEffect = EffectCompiler.compileEffect(model, ruleEffect);

        Map<Point,List<BuildingType>> builds = new HashMap<>();
        List<Point> buildOrder = new LinkedList<>();

        Point point1 = new Point(0,1);
        buildOrder.add(point1);
        List<BuildingType> buildsInPoint = new ArrayList<>();
        buildsInPoint.add(BuildingType.FIRST_FLOOR);
        buildsInPoint.add(BuildingType.DOME);
        builds.put(point1, buildsInPoint);

        Point point2 = new Point(1,1);
        buildOrder.add(point2);
        buildsInPoint = new ArrayList<>();
        buildsInPoint.add(BuildingType.FIRST_FLOOR);
        buildsInPoint.add(BuildingType.SECOND_FLOOR);
        buildsInPoint.add(BuildingType.THIRD_FLOOR);
        builds.put(point2, buildsInPoint);

        Point point3 = new Point(1,0);
        buildOrder.add(point3);
        buildsInPoint = new ArrayList<>();
        buildsInPoint.add(BuildingType.SECOND_FLOOR);
        builds.put(point3, buildsInPoint);


        BuildData buildData = new BuildData(Andrea, AndreaW1, builds, buildOrder);


        assertTrue(lambdaEffect.apply(null,buildData,true));
        assertEquals(model.getBoard().getCell(new Point(0,1)).getTopBuilding(), LevelType.GROUND);
        assertEquals(model.getBoard().getCell(new Point(1,1)).getTopBuilding(), LevelType.GROUND);
        assertEquals(model.getBoard().getCell(new Point(1,0)).getTopBuilding(), LevelType.FIRST_FLOOR);
        assertEquals(model.getBoard().getCell(new Point(0,0)).getWorkerID(), AndreaW1.getID());
        assertEquals(Andrea.getState(), PlayerState.TURN_STARTED);

        assertTrue(lambdaEffect.apply(null,buildData,false));
        assertEquals(model.getBoard().getCell(new Point(0,1)).getTopBuilding(), LevelType.DOME);
        assertEquals(model.getBoard().getCell(new Point(1,1)).getTopBuilding(), LevelType.THIRD_FLOOR);
        assertEquals(model.getBoard().getCell(new Point(1,0)).getTopBuilding(), LevelType.SECOND_FLOOR);
        assertTrue(model.getBoard().getCell( new Point(0,0)).hasWorker());
        assertEquals(Andrea.getState(), PlayerState.BUILT);

        assertEquals(model.getBoard().availableBuildings(BuildingType.FIRST_FLOOR), Board.NUM_OF_FIRST_FLOOR-2);
        assertEquals(model.getBoard().availableBuildings(BuildingType.SECOND_FLOOR), Board.NUM_OF_SECOND_FLOOR-2);
        assertEquals(model.getBoard().availableBuildings(BuildingType.THIRD_FLOOR), Board.NUM_OF_THIRD_FLOOR-1);
        assertEquals(model.getBoard().availableBuildings(BuildingType.DOME), Board.NUM_OF_DOME-1);
    }

    /*
        Tests an allow build effect both in simulation and in non simulation
        an a case where the worker cannot build
     */
    @Test
    void allowEffectBuild_Test2() throws PlayerWonSignal, PlayerLostSignal {
          /*
          0    1     2    3    4
        +----+----+----+----+----+
    0   | A1 | FF | SF |    |    |
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
        RuleEffect ruleEffect = RuleEffectImplTest.getRuleEffect(EffectType.ALLOW, AllowType.STANDARD, PlayerState.BUILT, null);

        LambdaEffect lambdaEffect = EffectCompiler.compileEffect(model, ruleEffect);

        Map<Point,List<BuildingType>> builds = new HashMap<>();
        List<Point> buildOrder = new LinkedList<>();

        Point point1 = new Point(0,1);
        buildOrder.add(point1);
        List<BuildingType> buildsInPoint = new ArrayList<>();
        buildsInPoint.add(BuildingType.DOME);
        buildsInPoint.add(BuildingType.FIRST_FLOOR);
        builds.put(point1, buildsInPoint);

        Point point2 = new Point(1,1);
        buildOrder.add(point2);
        buildsInPoint = new ArrayList<>();
        buildsInPoint.add(BuildingType.FIRST_FLOOR);
        buildsInPoint.add(BuildingType.SECOND_FLOOR);
        buildsInPoint.add(BuildingType.THIRD_FLOOR);
        builds.put(point2, buildsInPoint);

        Point point3 = new Point(1,0);
        buildOrder.add(point3);
        buildsInPoint = new ArrayList<>();
        buildsInPoint.add(BuildingType.DOME);
        builds.put(point3, buildsInPoint);


        BuildData buildData = new BuildData(Andrea, AndreaW1, builds, buildOrder);


        assertFalse(lambdaEffect.apply(null,buildData,true));
        assertEquals(model.getBoard().getCell(new Point(0,1)).getTopBuilding(), LevelType.GROUND);
        assertEquals(model.getBoard().getCell(new Point(1,1)).getTopBuilding(), LevelType.GROUND);
        assertEquals(model.getBoard().getCell(new Point(1,0)).getTopBuilding(), LevelType.FIRST_FLOOR);
        assertEquals(model.getBoard().getCell(new Point(0,0)).getWorkerID(), AndreaW1.getID());
        assertEquals(Andrea.getState(), PlayerState.TURN_STARTED);

        assertFalse(lambdaEffect.apply(null,buildData,false));
        assertEquals(model.getBoard().getCell(new Point(0,1)).getTopBuilding(), LevelType.GROUND);
        assertEquals(model.getBoard().getCell(new Point(1,1)).getTopBuilding(), LevelType.GROUND);
        assertEquals(model.getBoard().getCell(new Point(1,0)).getTopBuilding(), LevelType.FIRST_FLOOR);
        assertEquals(model.getBoard().getCell(new Point(0,0)).getWorkerID(), AndreaW1.getID());
        assertEquals(Andrea.getState(), PlayerState.TURN_STARTED);


    }

    /*
        Tests a successful push in a straight line
     */
    @Test
    void setOpponentPositionEffect_PUSH_STRAIGHT_Test1() throws PlayerWonSignal, PlayerLostSignal {
           /*
          0    1     2    3    4
        +----+----+----+----+----+
    0   | A1 | FF | SF |    |    |
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

        RuleEffect ruleEffect = RuleEffectImplTest.getRuleEffect(EffectType.ALLOW, AllowType.SET_OPPONENT, PlayerState.MOVED, "PUSH_STRAIGHT");

        LambdaEffect lambdaEffect = EffectCompiler.compileEffect(model, ruleEffect);

        List<Point> moves = new ArrayList<>();
        Point point1 = new Point(3,2);

        moves.add(point1);


        MoveData moveData = new MoveData(Andrea, AndreaW2, moves);


        assertTrue(lambdaEffect.apply(moveData,null,true));
        assertEquals(AndreaW2.getPosition(), new Point(2,2));
        assertEquals(model.getBoard().getCell(new Point(2,2)).getWorkerID(), AndreaW2.getID());
        assertFalse(model.getBoard().getCell(new Point(4,2)).hasWorker());
        assertEquals(model.getBoard().getCell(new Point(3,2)).getWorkerID(), MatteoW2.getID());
        assertEquals(MatteoW2.getPosition(), new Point(3,2));
        assertEquals(Andrea.getState(), PlayerState.TURN_STARTED);

        assertTrue(lambdaEffect.apply(moveData,null,false));

        assertEquals(AndreaW2.getPosition(), point1);
        assertEquals(model.getBoard().getCell(point1).getWorkerID(), AndreaW2.getID());

        assertFalse(model.getBoard().getCell(new Point(2,2)).hasWorker());

        assertEquals(MatteoW2.getPosition(), new Point(4,2));
        assertEquals(model.getBoard().getCell(new Point(4,2)).getWorkerID(), MatteoW2.getID());

        assertEquals(Andrea.getState(), PlayerState.MOVED);
    }

    /*
       Tests a successful push in a straight line with more than one move
    */
    @Test
    void setOpponentPositionEffect_PUSH_STRAIGHT_Test2() throws PlayerWonSignal, PlayerLostSignal {
           /*
          0    1     2    3    4
        +----+----+----+----+----+
    0   | A1 | FF | SF |    |    |
        +----+----+----+----+----+
    1   |    |    | A2 |    |    |
        +----+----+----+----+----+
    2   |    | B1 |    | B2 |    |
        +----+----+----+----+----+
    3   |    |    |    | D2 |    |
        +----+----+----+----+----+
    4   |    |    | D1 |    |    |
        +----+----+----+----+----+
*/

        model.getBoard().getCell(new Point(2,2)).removeWorker();
        model.getBoard().getCell(new Point(2,1)).setWorker(AndreaW2.getID());
        AndreaW2.setPosition(new Point(2,1));
        RuleEffect ruleEffect = RuleEffectImplTest.getRuleEffect(EffectType.ALLOW, AllowType.SET_OPPONENT, PlayerState.MOVED, "PUSH_STRAIGHT");

        LambdaEffect lambdaEffect = EffectCompiler.compileEffect(model, ruleEffect);

        List<Point> moves = new ArrayList<>();
        Point point1 = new Point(2,2);
        Point point2 = new Point(3,2);

        moves.add(point1);
        moves.add(point2);


        MoveData moveData = new MoveData(Andrea, AndreaW2, moves);

        assertTrue(lambdaEffect.apply(moveData,null,true));
        assertEquals(AndreaW2.getPosition(), new Point(2,1));
        assertEquals(model.getBoard().getCell(new Point(2,1)).getWorkerID(), AndreaW2.getID());

        assertFalse(model.getBoard().getCell(new Point(4,2)).hasWorker());

        assertEquals(model.getBoard().getCell(new Point(3,2)).getWorkerID(), MatteoW2.getID());
        assertEquals(MatteoW2.getPosition(), new Point(3,2));
        assertEquals(Andrea.getState(), PlayerState.TURN_STARTED);

        assertTrue(lambdaEffect.apply(moveData,null,false));

        assertEquals(AndreaW2.getPosition(), point2);
        assertEquals(model.getBoard().getCell(point2).getWorkerID(), AndreaW2.getID());

        assertFalse(model.getBoard().getCell(new Point(2,1)).hasWorker());

        assertEquals(MatteoW2.getPosition(), new Point(4,2));
        assertEquals(model.getBoard().getCell(new Point(4,2)).getWorkerID(), MatteoW2.getID());

        assertEquals(Andrea.getState(), PlayerState.MOVED);
    }

    /*
        Tests an unsuccessful push because of a worker
     */
    @Test
    void setOpponentPositionEffect_PUSH_STRAIGHT_Test3() throws PlayerWonSignal, PlayerLostSignal {
           /*
          0    1     2    3    4
        +----+----+----+----+----+
    0   | A1 | FF | SF |    |    |
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

        RuleEffect ruleEffect = RuleEffectImplTest.getRuleEffect(EffectType.ALLOW, AllowType.SET_OPPONENT, PlayerState.MOVED, "PUSH_STRAIGHT");

        LambdaEffect lambdaEffect = EffectCompiler.compileEffect(model, ruleEffect);

        List<Point> moves = new ArrayList<>();
        Point point1 = new Point(2,2);

        moves.add(point1);


        MoveData moveData = new MoveData(Matteo, MatteoW1, moves);


        assertFalse(lambdaEffect.apply(moveData,null,true));
        assertEquals(MatteoW1.getPosition(), new Point(1,2));
        assertEquals(model.getBoard().getCell(new Point(1,2)).getWorkerID(), MatteoW1.getID());

        assertTrue(model.getBoard().getCell(new Point(3,2)).hasWorker());

        assertEquals(model.getBoard().getCell(new Point(2,2)).getWorkerID(), AndreaW2.getID());
        assertEquals(AndreaW2.getPosition(), new Point(2,2));
        assertEquals(Matteo.getState(), PlayerState.TURN_STARTED);

        assertFalse(lambdaEffect.apply(moveData,null,false));

        assertEquals(MatteoW1.getPosition(), new Point(1,2));
        assertEquals(model.getBoard().getCell(new Point(1,2)).getWorkerID(), MatteoW1.getID());

        assertTrue(model.getBoard().getCell(new Point(3,2)).hasWorker());

        assertEquals(model.getBoard().getCell(new Point(2,2)).getWorkerID(), AndreaW2.getID());
        assertEquals(AndreaW2.getPosition(), new Point(2,2));
        assertEquals(Matteo.getState(), PlayerState.TURN_STARTED);


    }

    /*
       Tests an unsuccessful push because of a dome
    */
    @Test
    void setOpponentPositionEffect_PUSH_STRAIGHT_Test4() throws PlayerWonSignal, PlayerLostSignal {
           /*
          0    1     2    3    4
        +----+----+----+----+----+
    0   | A1 | FF | SF |    |    |
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
        model.getBoard().getCell(new Point(3,2)).removeWorker();
        model.getBoard().getCell(new Point(3,2)).addBuilding(BuildingType.DOME);

        RuleEffect ruleEffect = RuleEffectImplTest.getRuleEffect(EffectType.ALLOW, AllowType.SET_OPPONENT, PlayerState.MOVED, "PUSH_STRAIGHT");

        LambdaEffect lambdaEffect = EffectCompiler.compileEffect(model, ruleEffect);

        List<Point> moves = new ArrayList<>();
        Point point1 = new Point(2,2);

        moves.add(point1);


        MoveData moveData = new MoveData(Matteo, MatteoW1, moves);


        assertFalse(lambdaEffect.apply(moveData,null,true));
        assertEquals(MatteoW1.getPosition(), new Point(1,2));
        assertEquals(model.getBoard().getCell(new Point(1,2)).getWorkerID(), MatteoW1.getID());

        assertFalse(model.getBoard().getCell(new Point(3,2)).hasWorker());
        assertTrue(model.getBoard().getCell(new Point(3,2)).isOccupied());

        assertEquals(model.getBoard().getCell(new Point(2,2)).getWorkerID(), AndreaW2.getID());
        assertEquals(AndreaW2.getPosition(), new Point(2,2));
        assertEquals(Matteo.getState(), PlayerState.TURN_STARTED);

        assertFalse(lambdaEffect.apply(moveData,null,false));

        assertEquals(MatteoW1.getPosition(), new Point(1,2));
        assertEquals(model.getBoard().getCell(new Point(1,2)).getWorkerID(), MatteoW1.getID());

        assertFalse(model.getBoard().getCell(new Point(3,2)).hasWorker());
        assertTrue(model.getBoard().getCell(new Point(3,2)).isOccupied());

        assertEquals(model.getBoard().getCell(new Point(2,2)).getWorkerID(), AndreaW2.getID());
        assertEquals(AndreaW2.getPosition(), new Point(2,2));
        assertEquals(Matteo.getState(), PlayerState.TURN_STARTED);


    }

    /*
        Tests everything about a win effect
     */
    @Test
    void winEffect(){

        RuleEffect ruleEffect = RuleEffectImplTest.getRuleEffect(EffectType.WIN,null,null,null);
        LambdaEffect lambdaEffect = EffectCompiler.compileEffect(model, ruleEffect);

        Player testPlayer = model.getPlayers().get(0);
        Worker workerTest = testPlayer.getWorkers().get(0);
        List<Point> emptyList = new LinkedList<>();
        MoveData moveData = new MoveData(testPlayer,workerTest,emptyList);

        try {
            lambdaEffect.apply(moveData, null, true);
        } catch (PlayerWonSignal | PlayerLostSignal playerWonSignal) {
            assert false;
        }

        try {
            lambdaEffect.apply(moveData, null, false);
            assert false;
        } catch (PlayerWonSignal playerWonSignal){
            assert true;
        } catch ( PlayerLostSignal playerLostSignal){
            assert false;
        }

        Map<Point, List<BuildingType>> data = new HashMap<>();
        BuildData buildData = new BuildData(testPlayer,workerTest,data, emptyList);

        try {
            lambdaEffect.apply(null, buildData, true);
            assert true;
        } catch (PlayerWonSignal | PlayerLostSignal playerWonSignal) {
            assert false;
        }

        try {
            lambdaEffect.apply(null, buildData, false);
            assert false;
        } catch (PlayerWonSignal playerWonSignal){
            assert true;
        } catch ( PlayerLostSignal playerLostSignal){
            assert false;
        }
    }

    /*
        Tests everything about a deny effect
     */
    @Test
    void denyEffect(){

        RuleEffect ruleEffect = RuleEffectImplTest.getRuleEffect(EffectType.DENY,null,null,null);
        LambdaEffect lambdaEffect = EffectCompiler.compileEffect(model, ruleEffect);

        Player testPlayer = model.getPlayers().get(0);
        Worker workerTest = testPlayer.getWorkers().get(0);
        List<Point> emptyList = new LinkedList<>();
        MoveData moveData = new MoveData(testPlayer,workerTest,emptyList);

        try {
            lambdaEffect.apply(moveData, null, true);
        } catch (PlayerWonSignal | PlayerLostSignal playerSignal) {
            assert false;
        }

        try {
            lambdaEffect.apply(moveData, null, false);
            assert false;
        } catch (PlayerWonSignal playerWonSignal){
            assert false;
        } catch ( PlayerLostSignal playerLostSignal){
            assert true;
        }

        Map<Point, List<BuildingType>> data = new HashMap<>();
        BuildData buildData = new BuildData(testPlayer,workerTest,data, emptyList);

        try {
            lambdaEffect.apply(null, buildData, true);
        } catch (PlayerWonSignal | PlayerLostSignal playerSignal) {
            assert false;
        }

        try {
            lambdaEffect.apply(null, buildData, false);
            assert false;
        } catch (PlayerWonSignal playerWonSignal){
            assert false;
        } catch ( PlayerLostSignal playerLostSignal){
            assert true;
        }


    }

    /*
       Tests a succesfull swap
    */
    @Test
    void setOpponentPositionEffect_SWAP_Test1() throws PlayerWonSignal, PlayerLostSignal {
           /*
          0    1     2    3    4
        +----+----+----+----+----+
    0   | A1 | FF | SF |    |    |
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

        RuleEffect ruleEffect = RuleEffectImplTest.getRuleEffect(EffectType.ALLOW, AllowType.SET_OPPONENT, PlayerState.MOVED, "SWAP");

        LambdaEffect lambdaEffect = EffectCompiler.compileEffect(model, ruleEffect);

        List<Point> moves = new ArrayList<>();
        Point point1 = new Point(3,2);

        moves.add(point1);


        MoveData moveData = new MoveData(Andrea, AndreaW2, moves);


        assertTrue(lambdaEffect.apply(moveData,null,true));

        assertEquals(AndreaW2.getPosition(), new Point(2,2));
        assertEquals(model.getBoard().getCell(new Point(2,2)).getWorkerID(), AndreaW2.getID());

        assertEquals(model.getBoard().getCell(new Point(3,2)).getWorkerID(), MatteoW2.getID());
        assertEquals(MatteoW2.getPosition(), new Point(3,2));

        assertEquals(Andrea.getState(), PlayerState.TURN_STARTED);

        assertTrue(lambdaEffect.apply(moveData,null,false));

        assertEquals(AndreaW2.getPosition(), point1);
        assertEquals(model.getBoard().getCell(point1).getWorkerID(), AndreaW2.getID());

        assertEquals(MatteoW2.getPosition(), new Point(2,2));
        assertEquals(model.getBoard().getCell(new Point(2,2)).getWorkerID(), MatteoW2.getID());

        assertEquals(Andrea.getState(), PlayerState.MOVED);
    }


    /*
      Tests a successful swap with more than one move
   */
    @Test
    void setOpponentPositionEffect_SWAP_Test2() throws PlayerWonSignal, PlayerLostSignal {
           /*
          0    1     2    3    4
        +----+----+----+----+----+
    0   | A1 | FF | SF |    |    |
        +----+----+----+----+----+
    1   |    |    | A2 |    |    |
        +----+----+----+----+----+
    2   |    | B1 |    | B2 |    |
        +----+----+----+----+----+
    3   |    |    |    | D2 |    |
        +----+----+----+----+----+
    4   |    |    | D1 |    |    |
        +----+----+----+----+----+
*/

        model.getBoard().getCell(new Point(2,2)).removeWorker();
        model.getBoard().getCell(new Point(2,1)).setWorker(AndreaW2.getID());
        AndreaW2.setPosition(new Point(2,1));
        RuleEffect ruleEffect = RuleEffectImplTest.getRuleEffect(EffectType.ALLOW, AllowType.SET_OPPONENT, PlayerState.MOVED, "SWAP");

        LambdaEffect lambdaEffect = EffectCompiler.compileEffect(model, ruleEffect);

        List<Point> moves = new ArrayList<>();
        Point point1 = new Point(2,2);
        Point point2 = new Point(3,2);

        moves.add(point1);
        moves.add(point2);


        MoveData moveData = new MoveData(Andrea, AndreaW2, moves);

        assertTrue(lambdaEffect.apply(moveData,null,true));

        assertEquals(AndreaW2.getPosition(), new Point(2,1));
        assertEquals(model.getBoard().getCell(new Point(2,1)).getWorkerID(), AndreaW2.getID());

        assertEquals(model.getBoard().getCell(point2).getWorkerID(), MatteoW2.getID());
        assertEquals(MatteoW2.getPosition(), point2);

        assertEquals(Andrea.getState(), PlayerState.TURN_STARTED);

        assertTrue(lambdaEffect.apply(moveData,null,false));

        assertEquals(AndreaW2.getPosition(), point2);
        assertEquals(model.getBoard().getCell(point2).getWorkerID(), AndreaW2.getID());

        assertFalse(model.getBoard().getCell(new Point(2,1)).hasWorker());

        assertEquals(MatteoW2.getPosition(), point1);
        assertEquals(model.getBoard().getCell(point1).getWorkerID(), MatteoW2.getID());

        assertEquals(Andrea.getState(), PlayerState.MOVED);
    }

    /*
      Tests a unsuccessful swap with more than one move and trying to put one of your workers on a third floor
   */
    @Test
    void setOpponentPositionEffect_SWAP_Test3() throws PlayerWonSignal, PlayerLostSignal {
           /*
          0    1     2    3    4
        +----+----+----+----+----+
    0   | A1 | FF | TF |    |    |
        +----+----+----+----+----+
    1   |    |    | A2 |    |    |
        +----+----+----+----+----+
    2   |    | B1 |    | B2 |    |
        +----+----+----+----+----+
    3   |    |    |    | D2 |    |
        +----+----+----+----+----+
    4   |    |    | D1 |    |    |
        +----+----+----+----+----+
*/
        model.getBoard().getCell(new Point(2,0)).addBuilding(BuildingType.THIRD_FLOOR);
        model.getBoard().getCell(new Point(2,2)).removeWorker();
        model.getBoard().getCell(new Point(2,1)).setWorker(AndreaW2.getID());
        AndreaW2.setPosition(new Point(2,1));
        RuleEffect ruleEffect = RuleEffectImplTest.getRuleEffect(EffectType.ALLOW, AllowType.SET_OPPONENT, PlayerState.MOVED, "SWAP");

        LambdaEffect lambdaEffect = EffectCompiler.compileEffect(model, ruleEffect);

        List<Point> moves = new ArrayList<>();
        Point point1 = new Point(1,0);
        Point point2 = new Point(2,0);
        Point point3 = new Point(2,1);

        moves.add(point1);
        moves.add(point2);
        moves.add(point3);


        MoveData moveData = new MoveData(Andrea, AndreaW1, moves);

        assertFalse(lambdaEffect.apply(moveData,null,true));

        assertEquals(AndreaW1.getPosition(), new Point(0,0));
        assertEquals(model.getBoard().getCell(new Point(0,0)).getWorkerID(), AndreaW1.getID());

        assertEquals(model.getBoard().getCell(point3).getWorkerID(), AndreaW2.getID());
        assertEquals(AndreaW2.getPosition(), point3);

        assertEquals(Andrea.getState(), PlayerState.TURN_STARTED);

        assertFalse(lambdaEffect.apply(moveData,null,false));

        assertEquals(AndreaW1.getPosition(), new Point(0,0));
        assertEquals(model.getBoard().getCell(new Point(0,0)).getWorkerID(), AndreaW1.getID());

        assertEquals(model.getBoard().getCell(point3).getWorkerID(), AndreaW2.getID());
        assertEquals(AndreaW2.getPosition(), point3);

        assertEquals(Andrea.getState(), PlayerState.TURN_STARTED);

    }

    /*
      Tests a successful swap with more than one move and trying to put an opponent's worker on a third floor
   */
    @Test
    void setOpponentPositionEffect_SWAP_Test4() throws PlayerWonSignal, PlayerLostSignal {
           /*
          0    1     2    3    4
        +----+----+----+----+----+
    0   | A1 | FF | TF |    |    |
        +----+----+----+----+----+
    1   |    |    | B2 |    |    |
        +----+----+----+----+----+
    2   |    | B1 | A2 |    |    |
        +----+----+----+----+----+
    3   |    |    |    | D2 |    |
        +----+----+----+----+----+
    4   |    |    | D1 |    |    |
        +----+----+----+----+----+
*/
        model.getBoard().getCell(new Point(2,0)).addBuilding(BuildingType.THIRD_FLOOR);

        model.getBoard().getCell(new Point(3,2)).removeWorker();
        assertTrue(model.getBoard().getCell(new Point(2,1)).setWorker(MatteoW2.getID()));
        MatteoW2.setPosition(new Point(2,1));
        RuleEffect ruleEffect = RuleEffectImplTest.getRuleEffect(EffectType.ALLOW, AllowType.SET_OPPONENT, PlayerState.MOVED, "SWAP");

        LambdaEffect lambdaEffect = EffectCompiler.compileEffect(model, ruleEffect);

        List<Point> moves = new ArrayList<>();
        Point point1 = new Point(1,0);
        Point point2 = new Point(2,0);
        Point point3 = new Point(2,1);

        moves.add(point1);
        moves.add(point2);
        moves.add(point3);


        MoveData moveData = new MoveData(Andrea, AndreaW1, moves);

        assertTrue(lambdaEffect.apply(moveData,null,true));

        assertEquals(AndreaW1.getPosition(), new Point(0,0));
        assertEquals(model.getBoard().getCell(new Point(0,0)).getWorkerID(), AndreaW1.getID());

        assertEquals(model.getBoard().getCell(point3).getWorkerID(), MatteoW2.getID());
        assertEquals(MatteoW2.getPosition(), point3);

        assertEquals(Andrea.getState(), PlayerState.TURN_STARTED);

        assertTrue(lambdaEffect.apply(moveData,null,false));

        assertEquals(AndreaW1.getPosition(), point3);
        assertEquals(model.getBoard().getCell(point3).getWorkerID(), AndreaW1.getID());

        assertFalse(model.getBoard().getCell(new Point(0,0)).hasWorker());

        assertEquals(MatteoW2.getPosition(), point2);
        assertEquals(model.getBoard().getCell(point2).getWorkerID(), MatteoW2.getID());

        assertEquals(Andrea.getState(), PlayerState.MOVED);

    }

    /*
        Test a successful build under with a first floor and a second floor
     */
    @Test
    void buildUnderEffect_Test1() throws PlayerWonSignal, PlayerLostSignal {
      /*
          0    1     2    3    4
        +----+----+----+----+----+
    0   | A1 | FF | SF |    |    |
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
        RuleEffect ruleEffect = RuleEffectImplTest.getRuleEffect(EffectType.ALLOW, AllowType.BUILD_UNDER, PlayerState.BUILT, null);

        LambdaEffect lambdaEffect = EffectCompiler.compileEffect(model, ruleEffect);

        Map<Point,List<BuildingType>> builds = new HashMap<>();
        List<Point> buildOrder = new LinkedList<>();

        Point point1 = new Point(0,0);
        buildOrder.add(point1);
        buildOrder.add(point1);
        List<BuildingType> buildsInPoint = new ArrayList<>();
        buildsInPoint.add(BuildingType.FIRST_FLOOR);
        buildsInPoint.add(BuildingType.SECOND_FLOOR);
        builds.put(point1, buildsInPoint);


        BuildData buildData = new BuildData(Andrea, AndreaW1, builds, buildOrder);

        assertTrue(lambdaEffect.apply(null,buildData,true));
        assertEquals(model.getBoard().getCell(point1).getTopBuilding(), LevelType.GROUND);
        assertEquals(model.getBoard().getCell(point1).getWorkerID(), AndreaW1.getID());
        assertEquals(Andrea.getState(), PlayerState.TURN_STARTED);

        assertTrue(lambdaEffect.apply(null,buildData,false));
        assertEquals(model.getBoard().getCell(point1).getTopBuilding(), LevelType.SECOND_FLOOR);
        assertTrue(model.getBoard().getCell(point1).hasWorker());
        assertEquals(Andrea.getState(), PlayerState.BUILT);

        assertEquals(model.getBoard().availableBuildings(BuildingType.FIRST_FLOOR), Board.NUM_OF_FIRST_FLOOR-1);
        assertEquals(model.getBoard().availableBuildings(BuildingType.SECOND_FLOOR), Board.NUM_OF_SECOND_FLOOR-1);

    }

    /*
       Test an unsuccessful build under with a first floor,second floor, third floor and a dome
    */
    @Test
    void buildUnderEffect_Test2() throws PlayerWonSignal, PlayerLostSignal {
      /*
          0    1     2    3    4
        +----+----+----+----+----+
    0   |A1  | FF | SF |    |    |
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

        RuleEffect ruleEffect = RuleEffectImplTest.getRuleEffect(EffectType.ALLOW, AllowType.BUILD_UNDER, PlayerState.BUILT, null);

        LambdaEffect lambdaEffect = EffectCompiler.compileEffect(model, ruleEffect);

        Map<Point,List<BuildingType>> builds = new HashMap<>();
        List<Point> buildOrder = new LinkedList<>();

        Point point1 = new Point(0,0);
        buildOrder.add(point1);
        buildOrder.add(point1);
        List<BuildingType> buildsInPoint = new ArrayList<>();
        buildsInPoint.add(BuildingType.FIRST_FLOOR);
        buildsInPoint.add(BuildingType.SECOND_FLOOR);
        buildsInPoint.add(BuildingType.THIRD_FLOOR);
        buildsInPoint.add(BuildingType.DOME);
        builds.put(point1, buildsInPoint);


        BuildData buildData = new BuildData(Andrea, AndreaW1, builds, buildOrder);

        assertFalse(lambdaEffect.apply(null,buildData,true));
        assertEquals(model.getBoard().getCell(point1).getTopBuilding(), LevelType.GROUND);
        assertEquals(model.getBoard().getCell(point1).getWorkerID(), AndreaW1.getID());
        assertEquals(Andrea.getState(), PlayerState.TURN_STARTED);

        assertFalse(lambdaEffect.apply(null,buildData,false));
        assertEquals(model.getBoard().getCell(point1).getTopBuilding(), LevelType.GROUND);
        assertEquals(model.getBoard().getCell(point1).getWorkerID(), AndreaW1.getID());
        assertEquals(Andrea.getState(), PlayerState.TURN_STARTED);

        assertEquals(model.getBoard().availableBuildings(BuildingType.FIRST_FLOOR), Board.NUM_OF_FIRST_FLOOR);
        assertEquals(model.getBoard().availableBuildings(BuildingType.SECOND_FLOOR), Board.NUM_OF_SECOND_FLOOR);
        assertEquals(model.getBoard().availableBuildings(BuildingType.THIRD_FLOOR), Board.NUM_OF_THIRD_FLOOR);
        assertEquals(model.getBoard().availableBuildings(BuildingType.DOME), Board.NUM_OF_DOME);

    }

}