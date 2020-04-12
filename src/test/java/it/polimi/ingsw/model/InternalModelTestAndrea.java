package it.polimi.ingsw.model;

import it.polimi.ingsw.model.cardReader.CardFactory;
import it.polimi.ingsw.model.cardReader.CardFile;
import it.polimi.ingsw.model.cardReader.exceptions.CardLoadingException;
import it.polimi.ingsw.model.cardReader.exceptions.InvalidCardException;
import it.polimi.ingsw.model.enums.BuildingType;
import it.polimi.ingsw.model.enums.LevelType;
import it.polimi.ingsw.model.enums.PlayerState;
import it.polimi.ingsw.model.lambdaStrategy.exceptions.PlayerLostSignal;
import it.polimi.ingsw.model.lambdaStrategy.exceptions.PlayerWonSignal;
import it.polimi.ingsw.model.turnInfo.BuildData;
import it.polimi.ingsw.model.turnInfo.MoveData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InternalModelTestAndrea {

    private static CardFactory cardFactory;
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

    @BeforeAll
    static void init() throws CardLoadingException, InvalidCardException {
        //CardFactory
        cardFactory = CardFactory.getInstance();
    }

    @BeforeEach
    void createModel(){
        List<String> players = new ArrayList<String>();
        players.add("Andrea");
        players.add("Matteo");
        players.add("Mirko");
        model = new InternalModel(players, cardFactory);
        Andrea = model.getPlayerByNick("Andrea");
        Matteo = model.getPlayerByNick("Matteo");
        Mirko = model.getPlayerByNick("Mirko");
        MatteoW1 = Matteo.getWorkers().get(0);
        MatteoW2 = Matteo.getWorkers().get(1);
        MirkoW1 = Mirko.getWorkers().get(0);
        MirkoW2 = Mirko.getWorkers().get(1);
        AndreaW1 = Andrea.getWorkers().get(0);
        AndreaW2 = Andrea.getWorkers().get(1);
    }

    @Test
    void testCompileCardStrategy1()  {
        model.compileCardStrategy(); //Compiling only default strategy

        //Test packet
        Board board = model.getBoard();
        board.getCell(new Point(1,0)).addBuilding(BuildingType.FIRST_FLOOR);
        board.getCell(new Point(0,0)).setWorker(AndreaW1.getID());
        AndreaW1.setPosition(new Point(0,0));

        List<Point> points = new LinkedList<>();
        points.add(new Point(1,0));

        MoveData moveData = new MoveData(Andrea, AndreaW1, points);
        try{
            assertTrue(model.makeMove(moveData));
        } catch (PlayerWonSignal | PlayerLostSignal playerWonSignal) {
            assert false;
        }

        assertEquals(AndreaW1.getPosition(), new Point(1,0));
        assertEquals(board.getCell(new Point(1,0)).getWorkerID(), AndreaW1.getID());
        assertNull(board.getCell(new Point(0,0)).getWorkerID());

        points.add(new Point(1,1));
        moveData = new MoveData(Andrea, AndreaW1, points);
        try{
            assertFalse(model.makeMove(moveData));
        } catch (PlayerWonSignal | PlayerLostSignal playerWonSignal) {
            assert false;
        }
    }

    @Test
    void testCompileCardStrategy2()  {
        model.compileCardStrategy(); //Compiling only default strategy

        //Test packet
        Board board = model.getBoard();
        board.getCell(new Point(1,0)).addBuilding(BuildingType.FIRST_FLOOR);
        board.getCell(new Point(1,0)).addBuilding(BuildingType.SECOND_FLOOR);
        board.getCell(new Point(0,0)).setWorker(AndreaW1.getID());
        AndreaW1.setPosition(new Point(0,0));

        List<Point> points = new LinkedList<>();
        points.add(new Point(1,0));

        MoveData moveData = new MoveData(Andrea, AndreaW1, points);
        try{
            assertFalse(model.makeMove(moveData));
        } catch (PlayerWonSignal | PlayerLostSignal playerWonSignal) {
            assert false;
        }

        points.clear();
        points.add(new Point(1,1));

        moveData = new MoveData(Andrea, AndreaW1, points);
        try{
            assertTrue(model.makeMove(moveData));
        } catch (PlayerWonSignal | PlayerLostSignal playerWonSignal) {
            assert false;
        }
    }

    @Test
    void testCompileCardStrategy3() {
        model.compileCardStrategy(); //Compiling only default strategy

        //Test packet
        Board board = model.getBoard();
        board.getCell(new Point(1,0)).addBuilding(BuildingType.FIRST_FLOOR);
        board.getCell(new Point(0,0)).setWorker(AndreaW1.getID());
        AndreaW1.setPosition(new Point(0,0));
        Andrea.setPlayerState(PlayerState.MOVED);

        //Single build on ground
        Point buildPoint = new Point(0,1);
        Map<Point, List<BuildingType>> builds = new HashMap<>();
        List<BuildingType> buildingTypes = new LinkedList<>();
        buildingTypes.add(BuildingType.FIRST_FLOOR);
        builds.put(buildPoint,buildingTypes);

        try{
            assertTrue(model.makeBuild(new BuildData(Andrea, AndreaW1, builds, null)));
        } catch (PlayerWonSignal | PlayerLostSignal playerWonSignal) {
            assert false;
        }

        assertEquals(board.getCell(buildPoint).getTopBuilding(), LevelType.FIRST_FLOOR);
        assertEquals(board.getCell(new Point(1,0)).getTopBuilding(), LevelType.FIRST_FLOOR);
    }

    @Test
    void testCompileCardStrategy4() {
        model.compileCardStrategy(); //Compiling only default strategy

        //Test packet
        Board board = model.getBoard();
        board.getCell(new Point(1,0)).addBuilding(BuildingType.FIRST_FLOOR);
        board.getCell(new Point(0,0)).setWorker(AndreaW1.getID());
        AndreaW1.setPosition(new Point(0,0));
        Andrea.setPlayerState(PlayerState.MOVED);

        //Single build on ground
        Point buildPoint = new Point(0,1);
        Map<Point, List<BuildingType>> builds = new HashMap<>();
        List<BuildingType> buildingTypes = new LinkedList<>();
        buildingTypes.add(BuildingType.FIRST_FLOOR);
        buildingTypes.add(BuildingType.SECOND_FLOOR);
        builds.put(buildPoint,buildingTypes);

        try{
            assertFalse(model.makeBuild(new BuildData(Andrea, AndreaW1, builds, null)));
        } catch (PlayerWonSignal | PlayerLostSignal playerWonSignal) {
            assert false;
        }

        assertEquals(board.getCell(buildPoint).getTopBuilding(), LevelType.GROUND);
        assertEquals(board.getCell(new Point(1,0)).getTopBuilding(), LevelType.FIRST_FLOOR);
    }

    @Test
    void testCompileCardStrategyApollo1()  {
        CardFile apollo = cardFactory.getCards().stream().filter(c->c.getName().equals("Apollo")).findAny().orElse(null);

        Andrea.setCard(apollo);
        model.compileCardStrategy();


        //Test packet
        Point startCell = new Point(0,0);
        Point endCell = new Point(1,0);

        Board board = model.getBoard();
        board.getCell(endCell).addBuilding(BuildingType.FIRST_FLOOR);

        board.getCell(startCell).setWorker(AndreaW1.getID());
        AndreaW1.setPosition(startCell);

        board.getCell(endCell).setWorker(MirkoW1.getID());
        MirkoW1.setPosition(endCell);

        List<Point> points = new LinkedList<>();
        points.add(endCell);

        MoveData moveData = new MoveData(Andrea, AndreaW1, points);
        try{
            assertTrue(model.makeMove(moveData));
        } catch (PlayerWonSignal | PlayerLostSignal playerWonSignal) {
            assert false;
        }
        assertEquals(AndreaW1.getPosition(), endCell);
        assertEquals(MirkoW1.getPosition(),startCell);
        assertEquals(board.getCell(startCell).getWorkerID(),MirkoW1.getID());
        assertEquals(board.getCell(endCell).getWorkerID(),AndreaW1.getID());
    }

    @Test
    void testCompileCardStrategyApollo2(){
        CardFile apollo = cardFactory.getCards().stream().filter(c->c.getName().equals("Apollo")).findAny().orElse(null);

        Andrea.setCard(apollo);
        model.compileCardStrategy();

        //Test packet
        Board board = model.getBoard();
        /*
                  0    1     2    3    4   X
                +----+----+----+----+----+
            0   | DD | DD | A2 |    |    |
                +----+----+----+----+----+
            1   | DD | SF | DD |    |    |
                |    | A1 |    |    |    |
                +----+----+----+----+----+
            2   | M1 | FF | D1 |    |    |
                |    | M2 |    |    |    |
                +----+----+----+----+----+
            3   |    |    |    |    |    |
                +----+----+----+----+----+
            4   |    |    |    |    |    |
                +----+----+----+----+----+
            Y
        */

        Point p00 = new Point(0,0);
        board.getCell(p00).addBuilding(BuildingType.DOME);
        Point p01 = new Point(0,1);
        board.getCell(p01).addBuilding(BuildingType.DOME);
        Point p10 = new Point(1,0);
        board.getCell(p10).addBuilding(BuildingType.DOME);
        Point p11 = new Point(1,1);
        board.getCell(p11).addBuilding(BuildingType.FIRST_FLOOR);
        board.getCell(p11).addBuilding(BuildingType.SECOND_FLOOR);
        Point p20 = new Point(2,0);
        board.getCell(p20).setWorker(AndreaW2.getID());
        AndreaW2.setPosition(p20);
        Point p21 = new Point(2,1);
        board.getCell(p21).addBuilding(BuildingType.DOME);
        Point p02 = new Point(0,2);
        board.getCell(p02).setWorker(MatteoW1.getID());
        MatteoW1.setPosition(p02);
        Point p12 = new Point(1,2);
        board.getCell(p12).addBuilding(BuildingType.FIRST_FLOOR);
        board.getCell(p12).setWorker(MatteoW2.getID());
        MatteoW2.setPosition(p12);
        Point p22 = new Point(2,2);
        board.getCell(p22).setWorker(MirkoW1.getID());
        MirkoW1.setPosition(p22);

        board.getCell(new Point(1,1)).setWorker(AndreaW1.getID());
        AndreaW1.setPosition(new Point(1,1));

        assertTrue(model.canMove(Andrea, AndreaW1));

        Set<Point> expected = new HashSet<>();
        expected.add(p02);
        expected.add(p12);
        expected.add(p22);
        assertEquals(expected, model.getPossibleMoves(Andrea, AndreaW1));
    }

    @Test
    void testCompileCardStrategyArtemis1()  {
        CardFile artemis = cardFactory.getCards().stream().filter(c->c.getName().equals("Artemis")).findAny().orElse(null);

        Andrea.setCard(artemis);
        model.compileCardStrategy();

        //Test packet
        Point startCell = new Point(0,0);
        Point occCell = new Point(1,0);
        Point endPoint = new Point(1,1);

        Board board = model.getBoard();
        board.getCell(endPoint).addBuilding(BuildingType.FIRST_FLOOR);

        board.getCell(startCell).setWorker(AndreaW1.getID());
        AndreaW1.setPosition(startCell);

        board.getCell(occCell).setWorker(MirkoW1.getID());
        MirkoW1.setPosition(occCell);

        List<Point> points = new LinkedList<>();
        points.add(occCell);
        points.add(endPoint);

        MoveData moveData = new MoveData(Andrea, AndreaW1, points);
        try{
            assertFalse(model.makeMove(moveData));
        } catch (PlayerWonSignal | PlayerLostSignal playerWonSignal) {
            assert false;
        }
        points.clear();
        points.add(new Point(0,1));
        points.add(endPoint);
        try{
            assertTrue(model.makeMove(moveData));
        } catch (PlayerWonSignal | PlayerLostSignal ex){
            assert false;
        }
        assertNull(board.getCell(startCell).getWorkerID());
        assertEquals(board.getCell(endPoint).getWorkerID(), AndreaW1.getID());
    }

    @Test
    void testCompileCardStrategyMinotaur1()  {
        CardFile minotaur = cardFactory.getCards().stream().filter(c->c.getName().equals("Minotaur")).findAny().orElse(null);

        Andrea.setCard(minotaur);
        model.compileCardStrategy();


        //Test packet
        Point startCell = new Point(0,0);
        Point endCell = new Point(1,0);

        Board board = model.getBoard();
        board.getCell(endCell).addBuilding(BuildingType.FIRST_FLOOR);

        board.getCell(startCell).setWorker(AndreaW1.getID());
        AndreaW1.setPosition(startCell);

        board.getCell(endCell).setWorker(MirkoW1.getID());
        MirkoW1.setPosition(endCell);

        List<Point> points = new LinkedList<>();
        points.add(endCell);

        MoveData moveData = new MoveData(Andrea, AndreaW1, points);
        try{
            assertTrue(model.makeMove(moveData));
        } catch (PlayerWonSignal | PlayerLostSignal playerWonSignal) {
            assert false;
        }
        assertEquals(AndreaW1.getPosition(), endCell);
        assertEquals(MirkoW1.getPosition(),new Point(2,0));
        assertEquals(board.getCell(new Point(2,0)).getWorkerID(),MirkoW1.getID());
        assertEquals(board.getCell(endCell).getWorkerID(),AndreaW1.getID());
    }

    @Test
    void testCompileCardStrategyPan1()  {
        CardFile pan = cardFactory.getCards().stream().filter(c->c.getName().equals("Pan")).findAny().orElse(null);

        Andrea.setCard(pan);
        model.compileCardStrategy();


        //Test packet
        Point startCell = new Point(0,0);
        Point endCell = new Point(1,0);

        Board board = model.getBoard();
        board.getCell(startCell).addBuilding(BuildingType.FIRST_FLOOR);
        board.getCell(startCell).addBuilding(BuildingType.SECOND_FLOOR);

        board.getCell(startCell).setWorker(AndreaW1.getID());
        AndreaW1.setPosition(startCell);

        board.getCell(endCell).setWorker(MirkoW1.getID());
        MirkoW1.setPosition(endCell);

        List<Point> points = new LinkedList<>();
        points.add(endCell);

        MoveData moveData = new MoveData(Andrea, AndreaW1, points);
        try{
            assertFalse(model.makeMove(moveData));
        } catch (PlayerWonSignal | PlayerLostSignal playerWonSignal) {
            assert false;
        }
        board.getCell(endCell).removeWorker();
        try{
            assertTrue(model.makeMove(moveData));
            assert false;
        } catch (PlayerWonSignal ex){
            assert true;
        } catch (PlayerLostSignal ex){
            assert false;
        }
    }

    @Test
    void testCompileCardStrategyAthena1()  {
        CardFile athena = cardFactory.getCards().stream().filter(c->c.getName().equals("Athena")).findAny().orElse(null);

        Mirko.setCard(athena);
        model.compileCardStrategy();


        //Test packet
        Point startCell = new Point(0,0);
        Point endCell = new Point(1,0);
        Point mirkoStartCell = new Point(2,0);
        Point mirkoEndCell = new Point(2,1);

        Board board = model.getBoard();
        board.getCell(endCell).addBuilding(BuildingType.FIRST_FLOOR);
        board.getCell(mirkoEndCell).addBuilding(BuildingType.FIRST_FLOOR);

        board.getCell(startCell).setWorker(AndreaW1.getID());
        AndreaW1.setPosition(startCell);
        board.getCell(mirkoStartCell).setWorker(MirkoW1.getID());
        MirkoW1.setPosition(mirkoStartCell);


        List<Point> points = new LinkedList<>();
        points.add(endCell);

        MoveData moveData = new MoveData(Andrea, AndreaW1, points);
        try{
            assertTrue(model.makeMove(moveData));
        } catch (PlayerWonSignal | PlayerLostSignal ex) {
            assert false;
        }
        //Restore initial position
        board.getCell(endCell).removeWorker();
        AndreaW1.setPosition(startCell);
        board.getCell(startCell).setWorker(AndreaW1.getID());
        Andrea.setPlayerState(PlayerState.TURN_STARTED);

        //Move up mirko
        List<Point> pointsMirko = new LinkedList<>();
        pointsMirko.add(mirkoEndCell);

        try{
            assertTrue(model.makeMove(new MoveData(Mirko,MirkoW1,pointsMirko)));
        } catch (PlayerWonSignal | PlayerLostSignal ex) {
            assert false;
        }

        try{
            assertTrue(model.makeMove(moveData));
            assert false;
        } catch (PlayerWonSignal ex){
            assert false;
        } catch (PlayerLostSignal ex){
            assert true;
        }
    }

    @Test
    void testCompileCardStrategyDemeter1()  {
        CardFile demeter = cardFactory.getCards().stream().filter(c->c.getName().equals("Demeter")).findAny().orElse(null);
        Andrea.setCard(demeter);
        model.compileCardStrategy();


        //Test packet
        Board board = model.getBoard();
        board.getCell(new Point(1,0)).addBuilding(BuildingType.FIRST_FLOOR);
        board.getCell(new Point(0,0)).setWorker(AndreaW1.getID());
        AndreaW1.setPosition(new Point(0,0));
        Andrea.setPlayerState(PlayerState.MOVED);

        //Single build on ground
        Point buildPoint1 = new Point(0,1);
        Point buildPoint2 = new Point(1,0);
        Map<Point, List<BuildingType>> builds = new HashMap<>();
        List<BuildingType> buildingTypes = new LinkedList<>();
        buildingTypes.add(BuildingType.FIRST_FLOOR);
        builds.put(buildPoint1,buildingTypes);
        buildingTypes = new LinkedList<>();
        buildingTypes.add(BuildingType.SECOND_FLOOR);
        builds.put(buildPoint2,buildingTypes);

        try{
            assertTrue(model.makeBuild(new BuildData(Andrea, AndreaW1, builds, null)));
        } catch (PlayerWonSignal | PlayerLostSignal playerWonSignal) {
            assert false;
        }

        assertEquals(board.getCell(buildPoint1).getTopBuilding(), LevelType.FIRST_FLOOR);
        assertEquals(board.getCell(buildPoint2).getTopBuilding(), LevelType.SECOND_FLOOR);
    }

    @Test
    void testCompileCardStrategyDemeter2()  {
        CardFile demeter = cardFactory.getCards().stream().filter(c->c.getName().equals("Demeter")).findAny().orElse(null);
        Andrea.setCard(demeter);
        model.compileCardStrategy();


        //Test packet
        Board board = model.getBoard();
        board.getCell(new Point(1,0)).addBuilding(BuildingType.FIRST_FLOOR);
        board.getCell(new Point(0,0)).setWorker(AndreaW1.getID());
        AndreaW1.setPosition(new Point(0,0));
        Andrea.setPlayerState(PlayerState.MOVED);

        //Single build on ground
        Point buildPoint1 = new Point(0,1);
        Point buildPoint2 = new Point(1,0);
        Map<Point, List<BuildingType>> builds = new HashMap<>();
        List<BuildingType> buildingTypes = new LinkedList<>();
        buildingTypes.add(BuildingType.FIRST_FLOOR);
        builds.put(buildPoint1,buildingTypes);
        buildingTypes = new LinkedList<>();
        buildingTypes.add(BuildingType.SECOND_FLOOR);
        builds.put(buildPoint1,buildingTypes);

        try{
            assertFalse(model.makeBuild(new BuildData(Andrea, AndreaW1, builds, null)));
        } catch (PlayerWonSignal | PlayerLostSignal playerWonSignal) {
            assert false;
        }

        assertEquals(board.getCell(buildPoint1).getTopBuilding(), LevelType.GROUND);
        assertEquals(board.getCell(buildPoint2).getTopBuilding(), LevelType.FIRST_FLOOR);
    }

    @Test
    void testAllowedMoves() {
        model.compileCardStrategy(); //Compiling only default strategy

        //Test packet
        Board board = model.getBoard();
        /*
                  0    1     2    3    4  X
                +----+----+----+----+----+
            0   | FF |    |    |    |    |
                +----+----+----+----+----+
            1   | SF | SF | TF |    |    |
                |    |    | M1 |    |    |
                +----+----+----+----+----+
            2   |    | FF | TF |    |    |
                |    | M2 |    |    |    |
                +----+----+----+----+----+
            3   |    |    |    |    |    |
                +----+----+----+----+----+
            4   |    |    |    |    |    |
                +----+----+----+----+----+
            Y
        */
        Point p00 = new Point(0,0);
        board.getCell(p00).addBuilding(BuildingType.FIRST_FLOOR);
        Point p01 = new Point(0,1);
        board.getCell(p01).addBuilding(BuildingType.FIRST_FLOOR);
        board.getCell(p01).addBuilding(BuildingType.SECOND_FLOOR);
        Point p11 = new Point(1,1);
        board.getCell(p11).addBuilding(BuildingType.FIRST_FLOOR);
        board.getCell(p11).addBuilding(BuildingType.SECOND_FLOOR);
        Point p10 = new Point(1,0);
        Point p20 = new Point(2,0);
        Point p02 = new Point(0,2);
        Point p12 = new Point(1,2);
        board.getCell(p12).addBuilding(BuildingType.FIRST_FLOOR);
        board.getCell(p12).setWorker(MatteoW2.getID());
        MatteoW2.setPosition(p12);
        Point p22 = new Point(2,2);
        board.getCell(p22).addBuilding(BuildingType.FIRST_FLOOR);
        board.getCell(p22).addBuilding(BuildingType.SECOND_FLOOR);
        board.getCell(p22).addBuilding(BuildingType.THIRD_FLOOR);
        Point p21 = new Point(2,1);
        board.getCell(p21).addBuilding(BuildingType.FIRST_FLOOR);
        board.getCell(p21).addBuilding(BuildingType.SECOND_FLOOR);
        board.getCell(p21).addBuilding(BuildingType.THIRD_FLOOR);
        board.getCell(p21).setWorker(MatteoW1.getID());
        MatteoW1.setPosition(p21);

        board.getCell(p11).setWorker(AndreaW1.getID());
        AndreaW1.setPosition(p11);

        //Expected results
        Set<Point> expected = new HashSet<>();
        expected.add(p00);
        expected.add(p20);
        expected.add(p10);
        expected.add(p01);
        expected.add(p02);
        expected.add(p22);

        Set<Point> returned = model.getPossibleMoves(Andrea,AndreaW1);
        assertEquals(returned,expected);
        assertTrue(model.canMove(Andrea, AndreaW1));
    }

    @Test
    void testNoMovesPossible1(){
        model.compileCardStrategy(); //Compiling only default strategy

        //Test packet
        Board board = model.getBoard();
        /*
                  0    1     2    3    4
                +----+----+----+----+----+
            0   | DD | DD | A2 |    |    |
                +----+----+----+----+----+
            1   | DD | SF | DD |    |    |
                +----+----+----+----+----+
            2   | M1 | FF | D1 |    |    |
                |    | M2 |    |    |    |
                +----+----+----+----+----+
            3   |    |    |    |    |    |
                +----+----+----+----+----+
            4   |    |    |    |    |    |
                +----+----+----+----+----+
        */

        Point p00 = new Point(0,0);
        board.getCell(p00).addBuilding(BuildingType.DOME);
        Point p01 = new Point(0,1);
        board.getCell(p01).addBuilding(BuildingType.DOME);
        Point p10 = new Point(1,0);
        board.getCell(p10).addBuilding(BuildingType.DOME);
        Point p11 = new Point(1,1);
        board.getCell(p11).addBuilding(BuildingType.FIRST_FLOOR);
        board.getCell(p11).addBuilding(BuildingType.SECOND_FLOOR);
        Point p20 = new Point(2,0);
        board.getCell(p20).setWorker(AndreaW2.getID());
        AndreaW2.setPosition(p20);
        Point p21 = new Point(2,1);
        board.getCell(p21).addBuilding(BuildingType.DOME);
        Point p02 = new Point(0,2);
        board.getCell(p02).setWorker(MatteoW1.getID());
        MatteoW1.setPosition(p02);
        Point p12 = new Point(1,2);
        board.getCell(p12).addBuilding(BuildingType.FIRST_FLOOR);
        board.getCell(p12).setWorker(MatteoW2.getID());
        MatteoW2.setPosition(p12);
        Point p22 = new Point(2,2);
        board.getCell(p22).setWorker(MirkoW1.getID());
        MirkoW1.setPosition(p22);

        board.getCell(p11).setWorker(AndreaW1.getID());
        AndreaW1.setPosition(p11);

        assertFalse(model.canMove(Andrea, AndreaW1));
        assert(model.getPossibleMoves(Andrea, AndreaW1).size() == 0);
    }

    @Test
    void testAllowedBuilds() {
        model.compileCardStrategy(); //Compiling only default strategy

        //Test packet
        Board board = model.getBoard();
        /*
                  0    1     2    3    4
                +----+----+----+----+----+
            0   | FF | DD |    |    |    |
                +----+----+----+----+----+
            1   | SF | SF | TF |    |    |
                |    |    | M1 |    |    |
                +----+----+----+----+----+
            2   |    | FF | TF |    |    |
                |    | M2 |    |    |    |
                +----+----+----+----+----+
            3   |    |    |    |    |    |
                +----+----+----+----+----+
            4   |    |    |    |    |    |
                +----+----+----+----+----+
        */
        Point p00 = new Point(0,0);
        board.getCell(p00).addBuilding(BuildingType.FIRST_FLOOR);
        Point p01 = new Point(0,1);
        board.getCell(p01).addBuilding(BuildingType.FIRST_FLOOR);
        board.getCell(p01).addBuilding(BuildingType.SECOND_FLOOR);
        Point p11 = new Point(1,1);
        board.getCell(p11).addBuilding(BuildingType.FIRST_FLOOR);
        board.getCell(p11).addBuilding(BuildingType.SECOND_FLOOR);
        Point p10 = new Point(1,0);
        board.getCell(p10).addBuilding(BuildingType.DOME);
        Point p20 = new Point(2,0);
        Point p02 = new Point(0,2);
        Point p12 = new Point(1,2);
        board.getCell(p12).addBuilding(BuildingType.FIRST_FLOOR);
        board.getCell(p12).setWorker(MatteoW2.getID());
        MatteoW2.setPosition(p12);
        Point p22 = new Point(2,2);
        board.getCell(p22).addBuilding(BuildingType.FIRST_FLOOR);
        board.getCell(p22).addBuilding(BuildingType.SECOND_FLOOR);
        board.getCell(p22).addBuilding(BuildingType.THIRD_FLOOR);
        Point p21 = new Point(2,1);
        board.getCell(p21).addBuilding(BuildingType.FIRST_FLOOR);
        board.getCell(p21).addBuilding(BuildingType.SECOND_FLOOR);
        board.getCell(p21).addBuilding(BuildingType.THIRD_FLOOR);
        board.getCell(p21).setWorker(MatteoW1.getID());
        MatteoW1.setPosition(p21);

        board.getCell(p11).setWorker(AndreaW1.getID());
        AndreaW1.setPosition(p11);

        //Expected results
        Set<Point> expected = new HashSet<>();
        expected.add(p00);
        expected.add(p20);
        expected.add(p01);
        expected.add(p02);
        expected.add(p22);

        Andrea.setPlayerState(PlayerState.MOVED);
        Set<Point> returned = model.getPossibleBuilds(Andrea,AndreaW1);
        assertEquals(returned,expected);
        assertTrue(model.canBuild(Andrea, AndreaW1));
    }
}