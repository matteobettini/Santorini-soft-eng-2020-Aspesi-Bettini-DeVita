package it.polimi.ingsw.model.lambdaStrategy;

import it.polimi.ingsw.model.Board;
import it.polimi.ingsw.model.InternalModel;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Worker;
import it.polimi.ingsw.model.cardReader.RuleEffect;
import it.polimi.ingsw.model.cardReader.RuleEffectImplTest;
import it.polimi.ingsw.model.cardReader.enums.EffectType;
import it.polimi.ingsw.model.enums.BuildingType;
import it.polimi.ingsw.model.enums.LevelType;
import it.polimi.ingsw.model.enums.PlayerState;
import it.polimi.ingsw.model.lambdaStrategy.exceptions.PlayerLostSignal;
import it.polimi.ingsw.model.lambdaStrategy.exceptions.PlayerWonSignal;
import it.polimi.ingsw.model.turnInfo.BuildData;
import it.polimi.ingsw.model.turnInfo.MoveData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    void setUp(){
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

        model.getBoard().getCell(new Point(1,0)).addBuilding(BuildingType.FIRST_FLOOR);

        model.getBoard().getCell(new Point(2,0)).addBuilding(BuildingType.FIRST_FLOOR);
        model.getBoard().getCell(new Point(2,0)).addBuilding(BuildingType.SECOND_FLOOR);
    }

    @AfterEach
    void tearDown() {

    }

    /*
        Tests an allow move effect both in simulation and in non simulation
     */
    @Test
    void allowEffectMove_Test1() throws PlayerWonSignal, PlayerLostSignal {

        RuleEffect ruleEffect = RuleEffectImplTest.getRuleEffect(EffectType.ALLOW, PlayerState.MOVED, null);

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
        an a case where the workar can build
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
        RuleEffect ruleEffect = RuleEffectImplTest.getRuleEffect(EffectType.ALLOW, PlayerState.BUILT, null);

        LambdaEffect lambdaEffect = EffectCompiler.compileEffect(model, ruleEffect);

        Map<Point,List<BuildingType>> builds = new HashMap<>();

        Point point1 = new Point(0,1);
        List<BuildingType> buildsInPoint = new ArrayList<>();
        buildsInPoint.add(BuildingType.FIRST_FLOOR);
        buildsInPoint.add(BuildingType.DOME);
        builds.put(point1, buildsInPoint);

        Point point2 = new Point(1,1);
        buildsInPoint = new ArrayList<>();
        buildsInPoint.add(BuildingType.FIRST_FLOOR);
        buildsInPoint.add(BuildingType.SECOND_FLOOR);
        buildsInPoint.add(BuildingType.THIRD_FLOOR);
        builds.put(point2, buildsInPoint);

        Point point3 = new Point(1,0);
        buildsInPoint = new ArrayList<>();
        buildsInPoint.add(BuildingType.SECOND_FLOOR);
        builds.put(point3, buildsInPoint);


        BuildData buildData = new BuildData(Andrea, AndreaW1, builds);


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
        an a case where the workar cannot build
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
        RuleEffect ruleEffect = RuleEffectImplTest.getRuleEffect(EffectType.ALLOW, PlayerState.BUILT, null);

        LambdaEffect lambdaEffect = EffectCompiler.compileEffect(model, ruleEffect);

        Map<Point,List<BuildingType>> builds = new HashMap<>();

        Point point1 = new Point(0,1);
        List<BuildingType> buildsInPoint = new ArrayList<>();
        buildsInPoint.add(BuildingType.DOME);
        buildsInPoint.add(BuildingType.FIRST_FLOOR);
        builds.put(point1, buildsInPoint);

        Point point2 = new Point(1,1);
        buildsInPoint = new ArrayList<>();
        buildsInPoint.add(BuildingType.FIRST_FLOOR);
        buildsInPoint.add(BuildingType.SECOND_FLOOR);
        buildsInPoint.add(BuildingType.THIRD_FLOOR);
        builds.put(point2, buildsInPoint);

        Point point3 = new Point(1,0);
        buildsInPoint = new ArrayList<>();
        buildsInPoint.add(BuildingType.DOME);
        builds.put(point3, buildsInPoint);


        BuildData buildData = new BuildData(Andrea, AndreaW1, builds);


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
        Tests a scuccesfull push in a straight line
     */
    @Test
    void setOpponentPositonEffect_PUSH_STRAIGHT_Test1() throws PlayerWonSignal, PlayerLostSignal {
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

        RuleEffect ruleEffect = RuleEffectImplTest.getRuleEffect(EffectType.SET_OPPONENT_POSITION, PlayerState.MOVED, "PUSH_STRAIGHT");

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
       Tests a scuccesfull push in a straight line with more than one move
    */
    @Test
    void setOpponentPositonEffect_PUSH_STRAIGHT_Test2() throws PlayerWonSignal, PlayerLostSignal {
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

        model.getBoard().getCell(new Point(2,2)).removeWorker();
        model.getBoard().getCell(new Point(2,1)).setWorker(AndreaW2.getID());
        AndreaW2.setPosition(new Point(2,1));
        RuleEffect ruleEffect = RuleEffectImplTest.getRuleEffect(EffectType.SET_OPPONENT_POSITION, PlayerState.MOVED, "PUSH_STRAIGHT");

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
        Tests an unsuccesful push because of a worker
     */
    @Test
    void setOpponentPositonEffect_PUSH_STRAIGHT_Test3() throws PlayerWonSignal, PlayerLostSignal {
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

        RuleEffect ruleEffect = RuleEffectImplTest.getRuleEffect(EffectType.SET_OPPONENT_POSITION, PlayerState.MOVED, "PUSH_STRAIGHT");

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
       Tests an unsuccesful push because of a dome
    */
    @Test
    void setOpponentPositonEffect_PUSH_STRAIGHT_Test4() throws PlayerWonSignal, PlayerLostSignal {
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

        RuleEffect ruleEffect = RuleEffectImplTest.getRuleEffect(EffectType.SET_OPPONENT_POSITION, PlayerState.MOVED, "PUSH_STRAIGHT");

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


    @Test
    void winEffect(){

        RuleEffect ruleEffect = RuleEffectImplTest.getRuleEffect(EffectType.WIN,null,null);
        LambdaEffect lambdaEffect = EffectCompiler.compileEffect(model, ruleEffect);

        MoveData moveData = new MoveData(null,null,null);

        try {
            lambdaEffect.apply(moveData, null, true);
        } catch (PlayerWonSignal | PlayerLostSignal playerWonSignal) {
            fail();
        }

        try {
            lambdaEffect.apply(moveData, null, false);
            fail();
        } catch (PlayerWonSignal ignored){

        } catch ( PlayerLostSignal playerLostSignal){
            fail();
        }

        BuildData buildData = new BuildData(null,null,null);

        try {
            lambdaEffect.apply(null, buildData, true);
        } catch (PlayerWonSignal | PlayerLostSignal playerWonSignal) {
            fail();
        }

        try {
            lambdaEffect.apply(null, buildData, false);
            fail();
        } catch (PlayerWonSignal ignored){

        } catch ( PlayerLostSignal playerLostSignal){
            fail();
        }
    }

    @Test
    void denyEffect(){

        RuleEffect ruleEffect = RuleEffectImplTest.getRuleEffect(EffectType.DENY,null,null);
        LambdaEffect lambdaEffect = EffectCompiler.compileEffect(model, ruleEffect);

        MoveData moveData = new MoveData(null,null,null);

        try {
            lambdaEffect.apply(moveData, null, true);
        } catch (PlayerWonSignal | PlayerLostSignal playerWonSignal) {
            fail();
        }

        try {
            lambdaEffect.apply(moveData, null, false);
            fail();
        } catch (PlayerWonSignal playerWonSignal){
            fail();
        } catch ( PlayerLostSignal ignored){

        }

        BuildData buildData = new BuildData(null,null,null);

        try {
            lambdaEffect.apply(null, buildData, true);
        } catch (PlayerWonSignal | PlayerLostSignal playerWonSignal) {
            fail();
        }

        try {
            lambdaEffect.apply(null, buildData, false);
            fail();
        } catch (PlayerWonSignal playerWonSignal){
            fail();
        } catch ( PlayerLostSignal ignored){

        }


    }
}