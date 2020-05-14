package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.cards.CardFactory;
import it.polimi.ingsw.server.cards.CardFile;
import it.polimi.ingsw.server.cards.exceptions.CardLoadingException;
import it.polimi.ingsw.server.cards.exceptions.InvalidCardException;
import it.polimi.ingsw.common.enums.BuildingType;
import it.polimi.ingsw.server.model.enums.LevelType;
import it.polimi.ingsw.server.model.enums.PlayerState;
import it.polimi.ingsw.server.model.exceptions.PlayerLostSignal;
import it.polimi.ingsw.server.model.exceptions.PlayerWonSignal;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InternalModelAndreaTest {

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
        List<String> players = new ArrayList<>();
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

    /**
     * Test move with only default strategy.
     *  - AndreaW1 moves from (0,0)[GROUND] to (1,0)[FIRST_FLOOR]
     *      -> Move should be allowed
     *  - AndreaW1 then try to move again to (1,1)[GROUND]
     *      -> Move should not be allowed because player already moved
     */
    @Test
    void testCompileCardStrategy1()  {
        model.compileCardStrategy(); //Compiling only default strategy

        //Player init
        Andrea.setPlayerState(PlayerState.TURN_STARTED);

        //Populate Board
        Board board = model.getBoard();

        Point p00 = new Point(0,0);
        board.getCell(p00).setWorker(AndreaW1.getID());
        AndreaW1.setPosition(p00);

        Point p10 = new Point(1,0);
        board.getCell(p10).addBuilding(BuildingType.FIRST_FLOOR);

        //Generate move info
        List<Point> points = new LinkedList<>();
        points.add(p10);

        MoveData moveData = new MoveData(Andrea, AndreaW1, points);
        try{
            assertTrue(model.makeMove(moveData));
        } catch (PlayerWonSignal | PlayerLostSignal playerWonSignal) {
            assert false;
        }

        //Redundant assert for effects application (already tested)
        assertEquals(AndreaW1.getPosition(), p10);
        assertEquals(board.getCell(p10).getWorkerID(), AndreaW1.getID());
        assertNull(board.getCell(p00).getWorkerID());

        //Try to move again
        Point p11 = new Point(1,1);
        points.clear();
        points.add(p11);
        moveData = new MoveData(Andrea, AndreaW1, points);
        try{
            assertFalse(model.makeMove(moveData));
        } catch (PlayerWonSignal | PlayerLostSignal playerWonSignal) {
            assert false;
        }
    }

    /**
     * Test move with only default strategy.
     * - AndreaW1 moves from (0,0)[GROUND] to (1,0)[SECOND_FLOOR]
     *   -> Move should not be allowed
     * - AndreaW1 tries to move now to (1,1)[GROUND]
     *   -> Move should be allowed because player didn't move yet
     */
    @Test
    void testCompileCardStrategy2()  {
        model.compileCardStrategy(); //Compiling only default strategy

        //Player init
        Andrea.setPlayerState(PlayerState.TURN_STARTED);

        //Populate Board
        Board board = model.getBoard();

        Point p00 = new Point(0,0);
        board.getCell(p00).setWorker(AndreaW1.getID());
        AndreaW1.setPosition(p00);

        Point p10 = new Point(1,0);
        board.getCell(p10).addBuilding(BuildingType.FIRST_FLOOR);
        board.getCell(p10).addBuilding(BuildingType.SECOND_FLOOR);

        //Generate move info
        List<Point> points = new LinkedList<>();
        points.add(p10);

        MoveData moveData = new MoveData(Andrea, AndreaW1, points);
        try{
            assertFalse(model.makeMove(moveData));
        } catch (PlayerWonSignal | PlayerLostSignal playerWonSignal) {
            assert false;
        }

        //Try to move again
        Point p11 = new Point(1,1);
        points.clear();
        points.add(p11);

        moveData = new MoveData(Andrea, AndreaW1, points);
        try{
            assertTrue(model.makeMove(moveData));
        } catch (PlayerWonSignal | PlayerLostSignal playerWonSignal) {
            assert false;
        }
    }

    /**
     * Test build with only default strategy.
     * - AndreaW1, in position (0,0)[GROUND] tries to build to (0,1)[GROUND] a FIRST_FLOOR
     *   -> Build should be allowed and all previous buildings should remain on map
     */
    @Test
    void testCompileCardStrategy3() {
        model.compileCardStrategy(); //Compiling only default strategy

        //Player init
        Andrea.setPlayerState(PlayerState.MOVED);

        //Populate Board
        Board board = model.getBoard();

        Point p00 = new Point(0,0);
        board.getCell(p00).setWorker(AndreaW1.getID());
        AndreaW1.setPosition(p00);

        Point p10 = new Point(1,0);
        board.getCell(p10).addBuilding(BuildingType.FIRST_FLOOR);


        //Single build on ground
        Point p01 = new Point(0,1);
        Map<Point, List<BuildingType>> builds = new HashMap<>();
        List<BuildingType> buildingTypes = new LinkedList<>();
        List<Point> buildOrder = new LinkedList<>();
        buildOrder.add(p01);
        buildingTypes.add(BuildingType.FIRST_FLOOR);
        builds.put(p01,buildingTypes);

        try{
            assertTrue(model.makeBuild(new BuildData(Andrea, AndreaW1, builds, buildOrder)));
        } catch (PlayerWonSignal | PlayerLostSignal playerWonSignal) {
            assert false;
        }

        assertEquals(board.getCell(p01).getTopBuilding(), LevelType.FIRST_FLOOR);
        assertEquals(board.getCell(p10).getTopBuilding(), LevelType.FIRST_FLOOR);
    }

    /**
     * Test build with only default strategy.
     * - AndreaW1, in position (0,0)[GROUND] tries to build to (0,1)[GROUND] a FIRST_FLOOR and SECOND_FLOOR
     *   -> Build should not be allowed, because only a single building is allowed
     *      Nothing should be changed
     */
    @Test
    void testCompileCardStrategy4() {
        model.compileCardStrategy(); //Compiling only default strategy

        //Player init
        Andrea.setPlayerState(PlayerState.MOVED);

        //Populate Board
        Board board = model.getBoard();
        Point p00 = new Point(0,0);
        board.getCell(p00).setWorker(AndreaW1.getID());
        AndreaW1.setPosition(p00);

        Point p10 = new Point(1,0);
        board.getCell(p10).addBuilding(BuildingType.FIRST_FLOOR);


        //Single build on ground
        Point p01 = new Point(0,1);
        Map<Point, List<BuildingType>> builds = new HashMap<>();
        List<BuildingType> buildingTypes = new LinkedList<>();
        List<Point> buildOrder = new LinkedList<>();
        buildOrder.add(p01);
        buildingTypes.add(BuildingType.FIRST_FLOOR);
        buildingTypes.add(BuildingType.SECOND_FLOOR);
        builds.put(p01,buildingTypes);

        try{
            assertFalse(model.makeBuild(new BuildData(Andrea, AndreaW1, builds, buildOrder)));
        } catch (PlayerWonSignal | PlayerLostSignal playerWonSignal) {
            assert false;
        }

        assertEquals(board.getCell(p00).getTopBuilding(), LevelType.GROUND);
        assertEquals(board.getCell(p10).getTopBuilding(), LevelType.FIRST_FLOOR);
    }

    /**
     * Test move with Apollo (Andrea)
     * - AndreaW1 (0,0)[GROUND] try to move to (1,0)[FIRST_FLOOR] where there is MirkoW1
     *   -> Move should be allowed, and workers should swap
     */
    @Test
    void testCompileCardStrategyApollo1()  {
        //Prepare rules
        CardFile apollo = cardFactory.getCards().stream().filter(c->c.getName().equals("Apollo")).findAny().orElse(null);
        assertNotNull(apollo);
        Andrea.setCard(apollo);
        model.compileCardStrategy();

        //Init player
        Andrea.setPlayerState(PlayerState.TURN_STARTED);

        //Populate Board
        Board board = model.getBoard();

        Point p00 = new Point(0,0);
        board.getCell(p00).setWorker(AndreaW1.getID());
        AndreaW1.setPosition(p00);

        Point p10 = new Point(1,0);
        board.getCell(p10).addBuilding(BuildingType.FIRST_FLOOR);
        board.getCell(p10).setWorker(MirkoW1.getID());
        MirkoW1.setPosition(p10);

        List<Point> points = new LinkedList<>();
        points.add(p10);

        MoveData moveData = new MoveData(Andrea, AndreaW1, points);
        try{
            assertTrue(model.makeMove(moveData));
        } catch (PlayerWonSignal | PlayerLostSignal playerWonSignal) {
            assert false;
        }
        assertEquals(AndreaW1.getPosition(), p10);
        assertEquals(board.getCell(p10).getWorkerID(),AndreaW1.getID());
        assertEquals(MirkoW1.getPosition(),p00);
        assertEquals(board.getCell(p00).getWorkerID(),MirkoW1.getID());
    }

    /**
     * More elaborated test using possible moves and Apollo (Andrea)
     * - AndreaW1 (1,1)[SECOND_FLOOR]
     */
    @Test
    void testCompileCardStrategyApollo2(){
        //Prepare rules
        CardFile apollo = cardFactory.getCards().stream().filter(c->c.getName().equals("Apollo")).findAny().orElse(null);
        assertNotNull(apollo);
        Andrea.setCard(apollo);
        model.compileCardStrategy();

        //Init player
        Andrea.setPlayerState(PlayerState.TURN_STARTED);

        //Populate Board
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
        Point p10 = new Point(1,0);
        board.getCell(p10).addBuilding(BuildingType.DOME);
        Point p20 = new Point(2,0);
        board.getCell(p20).setWorker(AndreaW2.getID());
        AndreaW2.setPosition(p20);

        Point p01 = new Point(0,1);
        board.getCell(p01).addBuilding(BuildingType.DOME);

        Point p11 = new Point(1,1);
        board.getCell(p11).addBuilding(BuildingType.FIRST_FLOOR);
        board.getCell(p11).addBuilding(BuildingType.SECOND_FLOOR);
        board.getCell(p11).setWorker(AndreaW1.getID());
        AndreaW1.setPosition(p11);
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

        //Check AndreaW1 can move
        assertTrue(model.canMove(Andrea, AndreaW1));

        Set<Point> expected = new HashSet<>();
        expected.add(p02); //MatteoW1
        expected.add(p12); //MatteoW2
        expected.add(p22); //MirkoW1
        assertEquals(expected, model.getPossibleMoves(Andrea, AndreaW1));
    }

    /**
     * Test move with Artemis (Andrea)
     * - AndreaW1 (0,0)[GROUND] try to move to (1,1)[FIRST_FLOOR] but passing over (1,0) where there is MirkoW1
     *   -> Move should not be allowed, because cell is occupied
     * - AndreaW1 (0,0)[GROUND] try to move to (1,1)[FIRST_FLOOR] but passing over (0,1) free
     *   -> Move should be allowed
     */
    @Test
    void testCompileCardStrategyArtemis1()  {
        //Prepare rules
        CardFile artemis = cardFactory.getCards().stream().filter(c->c.getName().equals("Artemis")).findAny().orElse(null);
        assertNotNull(artemis);
        Andrea.setCard(artemis);
        model.compileCardStrategy();

        //Init player
        Andrea.setPlayerState(PlayerState.TURN_STARTED);

        //Populate Board
        Board board = model.getBoard();

        Point p00 = new Point(0,0);
        board.getCell(p00).setWorker(AndreaW1.getID());
        AndreaW1.setPosition(p00);

        Point p10 = new Point(1,0);
        board.getCell(p10).setWorker(MirkoW1.getID());
        MirkoW1.setPosition(p10);

        Point p11 = new Point(1,1);
        board.getCell(p11).addBuilding(BuildingType.FIRST_FLOOR);

        //Generate move info
        List<Point> points = new LinkedList<>();
        points.add(p10);
        points.add(p11);

        MoveData moveData = new MoveData(Andrea, AndreaW1, points);
        try{
            assertFalse(model.makeMove(moveData));
        } catch (PlayerWonSignal | PlayerLostSignal playerWonSignal) {
            assert false;
        }

        //Tries second time
        Point p01 = new Point(0,1);
        points.clear();
        points.add(p01);
        points.add(p11);
        try{
            assertTrue(model.makeMove(moveData));
        } catch (PlayerWonSignal | PlayerLostSignal ex){
            assert false;
        }
        assertNull(board.getCell(p00).getWorkerID());
        assertEquals(board.getCell(p10).getWorkerID(), MirkoW1.getID());
        assertEquals(board.getCell(p11).getWorkerID(), AndreaW1.getID());
    }

    /**
     * Test move with Minotaur (Andrea)
     * - AndreaW1 (0,0)[GROUND] try to move to (1,1)[FIRST_FLOOR] where there is MirkoW1
     *   -> Move should be allowed, and Mirko pushed to (2,2)
     */
    @Test
    void testCompileCardStrategyMinotaur1()  {
        //Prepare rules
        CardFile minotaur = cardFactory.getCards().stream().filter(c->c.getName().equals("Minotaur")).findAny().orElse(null);
        assertNotNull(minotaur);
        Andrea.setCard(minotaur);
        model.compileCardStrategy();

        //Init player
        Andrea.setPlayerState(PlayerState.TURN_STARTED);

        //Populate board
        Board board = model.getBoard();

        Point p00 = new Point(0,0);
        board.getCell(p00).setWorker(AndreaW1.getID());
        AndreaW1.setPosition(p00);

        Point p11 = new Point(1,1);
        board.getCell(p11).addBuilding(BuildingType.FIRST_FLOOR);
        board.getCell(p11).setWorker(MirkoW1.getID());
        MirkoW1.setPosition(p11);

        //Generate move data
        List<Point> points = new LinkedList<>();
        points.add(p11);

        MoveData moveData = new MoveData(Andrea, AndreaW1, points);
        try{
            assertTrue(model.makeMove(moveData));
        } catch (PlayerWonSignal | PlayerLostSignal playerWonSignal) {
            assert false;
        }

        Point p22 = new Point(2,2);
        assertEquals(AndreaW1.getPosition(), p11);
        assertEquals(board.getCell(p11).getWorkerID(),AndreaW1.getID());
        assertEquals(MirkoW1.getPosition(),p22);
        assertEquals(board.getCell(p22).getWorkerID(),MirkoW1.getID());
    }

    /**
     * Test build with Hephaestus (Andrea)
     * - AndreaW1 (0,0)[GROUND] try to build to (1,0)[SECOND_FLOOR] a THIRD_FLOOR and a DOME
     *   -> Build should fail, because Hephaestus cannot build THIRD_FLOOR + DOME
     * - AndreaW1 (0,0)[GROUND] try to build to (1,0)[SECOND_FLOOR] a THIRD_FLOOR and to (0,1)[FIRST_FLOOR] a SECOND_FLOOR
     *   -> Build should fail, because Hephaestus cannot build in two different cells
     * - AndreaW1 (0,0)[GROUND] try to build to (0,1)[FIRST_FLOOR] a SECOND_FLOOR + THIRD_FLOOR
     *   -> Build should be allowed
     */
    @Test
    void testCompileCardStrategyHephaestus1()  {
        //Prepare rules
        CardFile hephaestus = cardFactory.getCards().stream().filter(c->c.getName().equals("Hephaestus")).findAny().orElse(null);
        assertNotNull(hephaestus);
        Andrea.setCard(hephaestus);
        model.compileCardStrategy();

        //Init player
        Andrea.setPlayerState(PlayerState.MOVED);

        //Populate board
        Board board = model.getBoard();

        Point p00 = new Point(0,0);
        board.getCell(p00).setWorker(AndreaW1.getID());
        AndreaW1.setPosition(p00);

        Point p10 = new Point(1,0);
        board.getCell(p10).addBuilding(BuildingType.FIRST_FLOOR);
        board.getCell(p10).addBuilding(BuildingType.SECOND_FLOOR);

        Point p01 = new Point(0,1);
        board.getCell(p01).addBuilding(BuildingType.FIRST_FLOOR);

        //Generate build data
        List<Point> buildOrder = new LinkedList<>();
        buildOrder.add(p10);
        Map<Point, List<BuildingType>> buildings = new HashMap<>();
        List<BuildingType> list = new LinkedList<>();
        list.add(BuildingType.THIRD_FLOOR);
        list.add(BuildingType.DOME);
        buildings.put(p10, list);

        BuildData buildData = new BuildData(Andrea, AndreaW1, buildings, buildOrder);
        try{
            assertFalse(model.makeBuild(buildData));
        } catch (PlayerWonSignal | PlayerLostSignal playerWonSignal) {
            assert false;
        }

        //Second try
        buildOrder.clear();
        buildings.clear();
        buildOrder.add(p10);
        list.clear();
        list.add(BuildingType.THIRD_FLOOR);
        buildings.put(p10, new LinkedList<>(list));
        buildOrder.add(p01);
        list.clear();
        list.add(BuildingType.SECOND_FLOOR);
        buildings.put(p01, new LinkedList<>(list));

        buildData = new BuildData(Andrea, AndreaW1, buildings, buildOrder);
        try{
            assertFalse(model.makeBuild(buildData));
        } catch (PlayerWonSignal | PlayerLostSignal playerWonSignal) {
            assert false;
        }

        //Third try
        buildOrder.clear();
        buildings.clear();
        list.clear();
        buildOrder.add(p01);
        list.add(BuildingType.SECOND_FLOOR);
        list.add(BuildingType.THIRD_FLOOR);
        buildings.put(p01, list);

        buildData = new BuildData(Andrea, AndreaW1, buildings, buildOrder);
        try{
            assertTrue(model.makeBuild(buildData));
        } catch (PlayerWonSignal | PlayerLostSignal playerWonSignal) {
            assert false;
        }
        assertEquals(board.getCell(p01).getTopBuilding(), LevelType.THIRD_FLOOR);
    }

    /**
     * Test move and win with Pan (Andrea)
     * - AndreaW1 moves from (0,0)[SECOND_FLOOR] to (1,0)[GROUND] where there is MirkoW1
     *   -> Move is not allowed because Mirko is there
     * - AndreaW1 moves from (0,0)[SECOND_FLOOR] to (1,0)[GROUND], now empty
     *   -> Move is allowed (default) and Andrea wins
     */
    @Test
    void testCompileCardStrategyPan1()  {
        //Prepare rules
        CardFile pan = cardFactory.getCards().stream().filter(c->c.getName().equals("Pan")).findAny().orElse(null);
        assertNotNull(pan);
        Andrea.setCard(pan);
        model.compileCardStrategy();

        //Init player
        Andrea.setPlayerState(PlayerState.TURN_STARTED);

        //Populate board
        Board board = model.getBoard();

        Point p00 = new Point(0,0);
        board.getCell(p00).addBuilding(BuildingType.FIRST_FLOOR);
        board.getCell(p00).addBuilding(BuildingType.SECOND_FLOOR);
        board.getCell(p00).setWorker(AndreaW1.getID());
        AndreaW1.setPosition(p00);

        Point p10 = new Point(1,0);
        board.getCell(p10).setWorker(MirkoW1.getID());
        MirkoW1.setPosition(p10);

        //Generate move info
        List<Point> points = new LinkedList<>();
        points.add(p10);

        MoveData moveData = new MoveData(Andrea, AndreaW1, points);
        try{
            assertFalse(model.makeMove(moveData));
        } catch (PlayerWonSignal | PlayerLostSignal playerWonSignal) {
            assert false;
        }

        //Second try
        board.getCell(p10).removeWorker();
        try{
            assertTrue(model.makeMove(moveData));
            assert false;
        } catch (PlayerWonSignal ex){
            assert true;
        } catch (PlayerLostSignal ex){
            assert false;
        }
    }

    /**
     * Test move with Athena (Mirko)'s opponents (Andrea)
     * - AndreaW1 moves from (0,0)[GROUND] to (1,0)[FIRST_FLOOR]
     *   -> Move is allowed (default)
     * - (reset Andrea)MirkoW1 moves from (2,0)[GROUND] to (2,1)[FIRST_FLOOR]
     *   -> Move is allowed thanks to his default allow
     * - AndreaW1 moves from (0,0)[GROUND] to (1,0)[FIRST_FLOOR]
     *   -> Move make Andrea lose
     */
    @Test
    void testCompileCardStrategyAthena1()  {
        //Prepare rules
        CardFile athena = cardFactory.getCards().stream().filter(c->c.getName().equals("Athena")).findAny().orElse(null);
        assertNotNull(athena);
        Mirko.setCard(athena);
        model.compileCardStrategy();

        //Init player
        Andrea.setPlayerState(PlayerState.TURN_STARTED);

        //Populate board
        Board board = model.getBoard();
        /*
                  0    1     2    3    4   X
                +----+----+----+----+----+
            0   | A1 | FF | D1 |    |    |
                +----+----+----+----+----+
            1   |    |    | FF |    |    |
                +----+----+----+----+----+
            2   |    |    |    |    |    |
                +----+----+----+----+----+
            3   |    |    |    |    |    |
                +----+----+----+----+----+
            4   |    |    |    |    |    |
                +----+----+----+----+----+
            Y
        */
        Point p00 = new Point(0,0);
        board.getCell(p00).setWorker(AndreaW1.getID());
        AndreaW1.setPosition(p00);

        Point p10 = new Point(1,0);
        board.getCell(p10).addBuilding(BuildingType.FIRST_FLOOR);

        Point p20 = new Point(2,0);
        board.getCell(p20).setWorker(MirkoW1.getID());
        MirkoW1.setPosition(p20);

        Point p21 = new Point(2,1);
        board.getCell(p21).addBuilding(BuildingType.FIRST_FLOOR);

        //Generate move data
        List<Point> points = new LinkedList<>();
        points.add(p10);
        MoveData moveData = new MoveData(Andrea, AndreaW1, points);
        try{
            assertTrue(model.makeMove(moveData));
        } catch (PlayerWonSignal | PlayerLostSignal ex) {
            assert false;
        }

        //Restore Andrea
        board.getCell(p10).removeWorker();
        AndreaW1.setPosition(p00);
        board.getCell(p00).setWorker(AndreaW1.getID());
        Andrea.setPlayerState(PlayerState.TURN_STARTED);

        //Move up mirko
        List<Point> pointsMirko = new LinkedList<>();
        pointsMirko.add(p21);
        try{
            assertTrue(model.makeMove(new MoveData(Mirko,MirkoW1,pointsMirko)));
        } catch (PlayerWonSignal | PlayerLostSignal ex) {
            assert false;
        }

        //Now move up andrea
        try{
            assertTrue(model.makeMove(moveData));
            assert false;
        } catch (PlayerWonSignal ex){
            assert false;
        } catch (PlayerLostSignal ex){
            assert true;
        }
    }

    /**
     * Test build with Demeter (Andrea)
     * - AndreaW1 (0,0)[GROUND] try to build to (1,0)[FIRST_FLOOR] a SECOND_FLOOR + THIRD_FLOOR
     *   -> Build should not be allowed
     * - AndreaW1 (0,0)[GROUND] try to build to (0,1)[GROUND] a FIRST_FLOOR and to (1,0)[FIRST_FLOOR] a SECOND_FLOOR
     *   -> Build should be allowed
     */
    @Test
    void testCompileCardStrategyDemeter1()  {
        //Prepare rules
        CardFile demeter = cardFactory.getCards().stream().filter(c->c.getName().equals("Demeter")).findAny().orElse(null);
        assertNotNull(demeter);
        Andrea.setCard(demeter);
        model.compileCardStrategy();

        //Init player
        Andrea.setPlayerState(PlayerState.MOVED);

        //Populate board
        Board board = model.getBoard();

        Point p00 = new Point(0,0);
        board.getCell(p00).setWorker(AndreaW1.getID());
        AndreaW1.setPosition(p00);

        Point p10 = new Point(1,0);
        board.getCell(p10).addBuilding(BuildingType.FIRST_FLOOR);

        Point p01 = new Point(0,1);

        //First build
        Map<Point, List<BuildingType>> builds = new HashMap<>();
        List<BuildingType> buildingTypes = new LinkedList<>();
        List<Point> buildOrder = new LinkedList<>();

        buildOrder.add(p10);
        buildingTypes.add(BuildingType.SECOND_FLOOR);
        buildingTypes.add(BuildingType.THIRD_FLOOR);
        builds.put(p10,buildingTypes);
        try{
            assertFalse(model.makeBuild(new BuildData(Andrea, AndreaW1, builds, buildOrder)));
        } catch (PlayerWonSignal | PlayerLostSignal playerWonSignal) {
            assert false;
        }

        //Second build
        builds = new HashMap<>();
        buildingTypes = new LinkedList<>();
        buildOrder = new LinkedList<>();

        buildOrder.add(p01);
        buildingTypes.add(BuildingType.FIRST_FLOOR);
        builds.put(p01,buildingTypes);

        buildingTypes = new LinkedList<>();
        buildingTypes.add(BuildingType.SECOND_FLOOR);
        builds.put(p10,buildingTypes);
        try{
            assertTrue(model.makeBuild(new BuildData(Andrea, AndreaW1, builds, buildOrder)));
        } catch (PlayerWonSignal | PlayerLostSignal playerWonSignal) {
            assert false;
        }

        //Final check
        assertEquals(board.getCell(p01).getTopBuilding(), LevelType.FIRST_FLOOR);
        assertEquals(board.getCell(p10).getTopBuilding(), LevelType.SECOND_FLOOR);
    }

    /**
     * Test build with Demeter (Andrea)
     * - AndreaW1 (0,0)[GROUND] try to build to (0,1)[GROUND] a FIRST_FLOOR and to (1,0)[FIRST_FLOOR] a THIRD_FLOOR
     *   -> Build should not be allowed, and no effects should be appeared on board
     */
    @Test
    void testCompileCardStrategyDemeter2()  {
        //Prepare rules
        CardFile demeter = cardFactory.getCards().stream().filter(c->c.getName().equals("Demeter")).findAny().orElse(null);
        assertNotNull(demeter);
        Andrea.setCard(demeter);
        model.compileCardStrategy();

        //Init player
        Andrea.setPlayerState(PlayerState.MOVED);

        //Populate board
        Board board = model.getBoard();

        Point p00 = new Point(0,0);
        board.getCell(p00).setWorker(AndreaW1.getID());
        AndreaW1.setPosition(p00);

        Point p10 = new Point(1,0);
        board.getCell(p10).addBuilding(BuildingType.FIRST_FLOOR);

        Point p01 = new Point(0,1);

        //Generate build info
        Map<Point, List<BuildingType>> builds = new HashMap<>();
        List<BuildingType> buildingTypes = new LinkedList<>();
        List<Point> buildOrder = new LinkedList<>();
        buildOrder.add(p01);
        buildingTypes.add(BuildingType.FIRST_FLOOR);
        builds.put(p01,buildingTypes);
        buildOrder.add(p10);
        buildingTypes = new LinkedList<>();
        buildingTypes.add(BuildingType.THIRD_FLOOR);
        builds.put(p10,buildingTypes);

        try{
            assertFalse(model.makeBuild(new BuildData(Andrea, AndreaW1, builds, buildOrder)));
        } catch (PlayerWonSignal | PlayerLostSignal playerWonSignal) {
            assert false;
        }

        assertEquals(board.getCell(p01).getTopBuilding(), LevelType.GROUND);
        assertEquals(board.getCell(p10).getTopBuilding(), LevelType.FIRST_FLOOR);
    }

    /**
     * Test allowed moves with default strategy, starting from a second floor
     */
    @Test
    void testAllowedMoves() {
        model.compileCardStrategy(); //Compiling only default strategy

        //Init player
        Andrea.setPlayerState(PlayerState.TURN_STARTED);

        //Test packet
        Board board = model.getBoard();
        /*
                  0    1     2    3    4  X
                +----+----+----+----+----+
            0   | FF |    |    |    |    |
                +----+----+----+----+----+
            1   | SF | SF | TF |    |    |
                |    | A1 | M1 |    |    |
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
        Point p10 = new Point(1,0);
        Point p20 = new Point(2,0);

        Point p01 = new Point(0,1);
        board.getCell(p01).addBuilding(BuildingType.FIRST_FLOOR);
        board.getCell(p01).addBuilding(BuildingType.SECOND_FLOOR);
        Point p11 = new Point(1,1);
        board.getCell(p11).addBuilding(BuildingType.FIRST_FLOOR);
        board.getCell(p11).addBuilding(BuildingType.SECOND_FLOOR);
        board.getCell(p11).setWorker(AndreaW1.getID());
        AndreaW1.setPosition(p11);
        Point p21 = new Point(2,1);
        board.getCell(p21).addBuilding(BuildingType.FIRST_FLOOR);
        board.getCell(p21).addBuilding(BuildingType.SECOND_FLOOR);
        board.getCell(p21).addBuilding(BuildingType.THIRD_FLOOR);
        board.getCell(p21).setWorker(MatteoW1.getID());
        MatteoW1.setPosition(p21);

        Point p02 = new Point(0,2);
        Point p12 = new Point(1,2);
        board.getCell(p12).addBuilding(BuildingType.FIRST_FLOOR);
        board.getCell(p12).setWorker(MatteoW2.getID());
        MatteoW2.setPosition(p12);
        Point p22 = new Point(2,2);
        board.getCell(p22).addBuilding(BuildingType.FIRST_FLOOR);
        board.getCell(p22).addBuilding(BuildingType.SECOND_FLOOR);
        board.getCell(p22).addBuilding(BuildingType.THIRD_FLOOR);

        //Expected results
        Set<Point> expected = new HashSet<>();
        expected.add(p00);
        expected.add(p10);
        expected.add(p20);

        expected.add(p01);
        expected.add(p02);
        expected.add(p22);

        Set<Point> returned = model.getPossibleMoves(Andrea,AndreaW1);
        assertEquals(returned,expected);
        assertTrue(model.canMove(Andrea, AndreaW1));
    }

    /**
     * Test cannot move on a worker surrounded with domes and players, and only default strategy
     */
    @Test
    void testNoMovesPossible1(){
        model.compileCardStrategy(); //Compiling only default strategy

        //Init player
        Andrea.setPlayerState(PlayerState.TURN_STARTED);

        //Populate Board
        Board board = model.getBoard();
        /*
                  0    1     2    3    4
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
        */

        Point p00 = new Point(0,0);
        board.getCell(p00).addBuilding(BuildingType.DOME);
        Point p10 = new Point(1,0);
        board.getCell(p10).addBuilding(BuildingType.DOME);
        Point p20 = new Point(2,0);
        board.getCell(p20).setWorker(AndreaW2.getID());
        AndreaW2.setPosition(p20);

        Point p01 = new Point(0,1);
        board.getCell(p01).addBuilding(BuildingType.DOME);
        Point p11 = new Point(1,1);
        board.getCell(p11).addBuilding(BuildingType.FIRST_FLOOR);
        board.getCell(p11).addBuilding(BuildingType.SECOND_FLOOR);
        board.getCell(p11).setWorker(AndreaW1.getID());
        AndreaW1.setPosition(p11);
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

        assertFalse(model.canMove(Andrea, AndreaW1));
        assert(model.getPossibleMoves(Andrea, AndreaW1).size() == 0);
    }

    /**
     * Test can build by player and worker
     */
    @Test
    void testAllowedBuilds() {
        model.compileCardStrategy(); //Compiling only default strategy

        //Init player
        Andrea.setPlayerState(PlayerState.MOVED);

        //Populate board
        Board board = model.getBoard();
        /*
                  0    1     2    3    4
                +----+----+----+----+----+
            0   | FF | DD |    |    |    |
                +----+----+----+----+----+
            1   | SF | SF | TF |    |    |
                |    | A1 | M1 |    |    |
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
        Point p10 = new Point(1,0);
        board.getCell(p10).addBuilding(BuildingType.DOME);
        Point p20 = new Point(2,0);

        Point p01 = new Point(0,1);
        board.getCell(p01).addBuilding(BuildingType.FIRST_FLOOR);
        board.getCell(p01).addBuilding(BuildingType.SECOND_FLOOR);
        Point p11 = new Point(1,1);
        board.getCell(p11).addBuilding(BuildingType.FIRST_FLOOR);
        board.getCell(p11).addBuilding(BuildingType.SECOND_FLOOR);
        board.getCell(p11).setWorker(AndreaW1.getID());
        AndreaW1.setPosition(p11);
        Point p21 = new Point(2,1);
        board.getCell(p21).addBuilding(BuildingType.FIRST_FLOOR);
        board.getCell(p21).addBuilding(BuildingType.SECOND_FLOOR);
        board.getCell(p21).addBuilding(BuildingType.THIRD_FLOOR);
        board.getCell(p21).setWorker(MatteoW1.getID());
        MatteoW1.setPosition(p21);

        Point p02 = new Point(0,2);
        Point p12 = new Point(1,2);
        board.getCell(p12).addBuilding(BuildingType.FIRST_FLOOR);
        board.getCell(p12).setWorker(MatteoW2.getID());
        MatteoW2.setPosition(p12);
        Point p22 = new Point(2,2);
        board.getCell(p22).addBuilding(BuildingType.FIRST_FLOOR);
        board.getCell(p22).addBuilding(BuildingType.SECOND_FLOOR);
        board.getCell(p22).addBuilding(BuildingType.THIRD_FLOOR);

        //Expected results
        Set<Point> expected = new HashSet<>();
        expected.add(p00);
        expected.add(p20);

        expected.add(p01);
        expected.add(p02);
        expected.add(p22);


        Set<Point> returned = model.getPossibleBuilds(Andrea,AndreaW1).keySet();
        assertEquals(returned,expected);
        assertTrue(model.canBuild(Andrea, AndreaW1));
    }

    /**
     * Test can move by incremental move packet (Default, Artemis)
     */
    @Test
    void testMoveHelp(){
        model.compileCardStrategy(); //Compiling only default strategy

        //Populate board
        Board board = model.getBoard();
        /*
                  0    1     2    3    4   X
                +----+----+----+----+----+
            0   |    | FF |    |    |    |
                | A1 |    | M1 |    |    |
                +----+----+----+----+----+
            1   | SF |    | DD |    |    |
                |    |    |    |    |    |
                +----+----+----+----+----+
            2   | DD | FF | TF |    |    |
                |    | M2 |    |    |    |
                +----+----+----+----+----+
            3   |    |    |    |    |    |
                +----+----+----+----+----+
            4   |    |    |    |    |    |
                +----+----+----+----+----+
            Y
        */
        Point p00 = new Point(0,0);
        Cell c00 = board.getCell(p00);
        c00.setWorker(AndreaW1.getID());
        AndreaW1.setPosition(p00);
        Point p10 = new Point(1,0);
        Cell c10 = board.getCell(p10);
        c10.addBuilding(BuildingType.FIRST_FLOOR);
        Point p20 = new Point(2,0);
        Cell c20 = board.getCell(p20);
        c20.setWorker(MatteoW1.getID());
        MatteoW1.setPosition(p20);

        Point p01 = new Point(0,1);
        Cell c01 = board.getCell(p01);
        c01.addBuilding(BuildingType.FIRST_FLOOR);
        c01.addBuilding(BuildingType.SECOND_FLOOR);
        Point p11 = new Point(1,1);
        Point p21 = new Point(2,1);
        Cell c21 = board.getCell(p21);
        c01.addBuilding(BuildingType.FIRST_FLOOR);
        c01.addBuilding(BuildingType.SECOND_FLOOR);
        c01.addBuilding(BuildingType.THIRD_FLOOR);
        c21.addBuilding(BuildingType.DOME);

        Point p02 = new Point(0,2);
        Cell c02 = board.getCell(p02);
        c02.addBuilding(BuildingType.DOME);
        Point p12 = new Point(1,2);
        Cell c12 = board.getCell(p12);
        c12.addBuilding(BuildingType.FIRST_FLOOR);
        c12.setWorker(MatteoW2.getID());
        MatteoW2.setPosition(p12);
        Point p22 = new Point(2,2);
        Cell c22 = board.getCell(p22);
        c22.addBuilding(BuildingType.FIRST_FLOOR);
        c22.addBuilding(BuildingType.SECOND_FLOOR);
        c22.addBuilding(BuildingType.THIRD_FLOOR);

        //Generate move data
        List<Point> already = new LinkedList<>();
        already.add(p11);
        MoveData data = new MoveData(Andrea, AndreaW1, already);

        //Test 1 -> Default
        Set<Point> result = model.getPossibleMoves(data);
        assert(result.size() == 0); //Already moved, cannot do it again

        //Test 2 -> Artemis
        CardFile artemis = cardFactory.getCards().stream().filter(c->c.getName().equals("Artemis")).findAny().orElse(null);
        assertNotNull(artemis);
        Andrea.setCard(artemis);
        model.compileCardStrategy();

        Set<Point> expected = new HashSet<>();
        expected.add(p10);

        result = model.getPossibleMoves(data);
        assertEquals(result,expected);
    }

    /**
     * Test can build by incremental build packet (Default, Demeter, Hephaestus)
     */
    @Test
    void testBuildHelp(){
        model.compileCardStrategy(); //Compiling only default strategy

        //Init player
        Andrea.setPlayerState(PlayerState.MOVED);

        //Populate Board
        Board board = model.getBoard();
        /*
                  0    1     2    3    4   X
                +----+----+----+----+----+
            0   |    | FF |    |    |    |
                |    |    | M1 |    |    |
                +----+----+----+----+----+
            1   | SF |    | DD |    |    |
                |    | A1 |    |    |    |
                +----+----+----+----+----+
            2   | DD | FF | TF |    |    |
                |    | M2 |    |    |    |
                +----+----+----+----+----+
            3   |    |    |    |    |    |
                +----+----+----+----+----+
            4   |    |    |    |    |    |
                +----+----+----+----+----+
            Y
        */
        Point p00 = new Point(0,0);
        Point p10 = new Point(1,0);
        Cell c10 = board.getCell(p10);
        c10.addBuilding(BuildingType.FIRST_FLOOR);
        Point p20 = new Point(2,0);
        Cell c20 = board.getCell(p20);
        c20.setWorker(MatteoW1.getID());
        MatteoW1.setPosition(p20);

        Point p01 = new Point(0,1);
        Cell c01 = board.getCell(p01);
        c01.addBuilding(BuildingType.FIRST_FLOOR);
        c01.addBuilding(BuildingType.SECOND_FLOOR);
        Point p11 = new Point(1,1);
        Cell c11 = board.getCell(p11);
        c11.setWorker(AndreaW1.getID());
        AndreaW1.setPosition(p11);
        Point p21 = new Point(2,1);
        Cell c21 = board.getCell(p21);
        c21.addBuilding(BuildingType.FIRST_FLOOR);
        c21.addBuilding(BuildingType.SECOND_FLOOR);
        c21.addBuilding(BuildingType.THIRD_FLOOR);
        c21.addBuilding(BuildingType.DOME);

        Point p02 = new Point(0,2);
        Cell c02 = board.getCell(p02);
        c02.addBuilding(BuildingType.DOME);
        Point p12 = new Point(1,2);
        Cell c12 = board.getCell(p12);
        c12.addBuilding(BuildingType.FIRST_FLOOR);
        c12.setWorker(MatteoW2.getID());
        MatteoW2.setPosition(p12);
        Point p22 = new Point(2,2);
        Cell c22 = board.getCell(p22);
        c22.addBuilding(BuildingType.FIRST_FLOOR);
        c22.addBuilding(BuildingType.SECOND_FLOOR);
        c22.addBuilding(BuildingType.THIRD_FLOOR);

        //Generate build info
        Map<Point, List<BuildingType>> already = new HashMap<>();
        List<BuildingType> buildings = new LinkedList<>();
        buildings.add(BuildingType.FIRST_FLOOR);
        already.put(p00, buildings);
        List<Point> dataOrder = new LinkedList<>();
        dataOrder.add(p00);
        BuildData data = new BuildData(Andrea, AndreaW1, already, dataOrder);

        //Test 1 -> Default
        Set<Point> result = model.getPossibleBuilds(data).keySet();
        assert(result.size() == 0); //Already built, cannot do it again

        //Test 2 -> Demeter
        CardFile demeter = cardFactory.getCards().stream().filter(c->c.getName().equals("Demeter")).findAny().orElse(null);
        assertNotNull(demeter);
        Andrea.setCard(demeter);
        model.compileCardStrategy();

        Set<Point> expected = new HashSet<>();
        expected.add(p10);
        expected.add(p01);
        expected.add(p22);

        result = model.getPossibleBuilds(data).keySet();
        assertEquals(result,expected);

        //Test 2 -> Hephaestus
        CardFile hephaestus = cardFactory.getCards().stream().filter(c->c.getName().equals("Hephaestus")).findAny().orElse(null);
        assertNotNull(hephaestus);
        Andrea.setCard(hephaestus);
        model.compileCardStrategy();

        expected = new HashSet<>();
        expected.add(p00);

        result = model.getPossibleBuilds(data).keySet();
        assertEquals(result,expected);
    }

    /**
     * Test building suggestions by player and worker
     */
    @Test
    void testAllowedBuildsAdvanced1() {
        model.compileCardStrategy(); //Compiling only default strategy

        //Init player state
        Andrea.setPlayerState(PlayerState.MOVED);

        //Populate board
        Board board = model.getBoard();
        /*
                  0    1     2    3    4
                +----+----+----+----+----+
            0   | FF | DD |    |    |    |
                +----+----+----+----+----+
            1   | SF | SF | TF |    |    |
                |    | A1 | M1 |    |    |
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
        Point p10 = new Point(1,0);
        board.getCell(p10).addBuilding(BuildingType.DOME);
        Point p20 = new Point(2,0);

        Point p01 = new Point(0,1);
        board.getCell(p01).addBuilding(BuildingType.FIRST_FLOOR);
        board.getCell(p01).addBuilding(BuildingType.SECOND_FLOOR);
        Point p11 = new Point(1,1);
        board.getCell(p11).addBuilding(BuildingType.FIRST_FLOOR);
        board.getCell(p11).addBuilding(BuildingType.SECOND_FLOOR);
        board.getCell(p11).setWorker(AndreaW1.getID());
        AndreaW1.setPosition(p11);
        Point p21 = new Point(2,1);
        board.getCell(p21).addBuilding(BuildingType.FIRST_FLOOR);
        board.getCell(p21).addBuilding(BuildingType.SECOND_FLOOR);
        board.getCell(p21).addBuilding(BuildingType.THIRD_FLOOR);
        board.getCell(p21).setWorker(MatteoW1.getID());
        MatteoW1.setPosition(p21);

        Point p02 = new Point(0,2);
        Point p12 = new Point(1,2);
        board.getCell(p12).addBuilding(BuildingType.FIRST_FLOOR);
        board.getCell(p12).setWorker(MatteoW2.getID());
        MatteoW2.setPosition(p12);
        Point p22 = new Point(2,2);
        board.getCell(p22).addBuilding(BuildingType.FIRST_FLOOR);
        board.getCell(p22).addBuilding(BuildingType.SECOND_FLOOR);
        board.getCell(p22).addBuilding(BuildingType.THIRD_FLOOR);


        //Expected results
        Map<Point, List<BuildingType>> expected = new HashMap<>();
        List<BuildingType> buildings = new LinkedList<>();
        buildings.add(BuildingType.SECOND_FLOOR);
        expected.put(p00, new LinkedList<>(buildings));

        buildings.clear();
        buildings.add(BuildingType.FIRST_FLOOR);
        expected.put(p20, new LinkedList<>(buildings));

        buildings.clear();
        buildings.add(BuildingType.THIRD_FLOOR);
        expected.put(p01, new LinkedList<>(buildings));

        buildings.clear();
        buildings.add(BuildingType.FIRST_FLOOR);
        expected.put(p02, new LinkedList<>(buildings));

        buildings.clear();
        buildings.add(BuildingType.DOME);
        expected.put(p22, new LinkedList<>(buildings));

        Map<Point, List<BuildingType>> returned = model.getPossibleBuilds(Andrea,AndreaW1);
        assert (expected.size() == returned.size());
        for(Point point : expected.keySet()){
            assertEquals(expected.get(point), returned.get(point));
        }
    }

    /**
     * Test building suggestions by player and worker with Atlas
     */
    @Test
    void testAllowedBuildsAdvanced2() {
        CardFile atlas = cardFactory.getCards().stream().filter(c->c.getName().equals("Atlas")).findAny().orElse(null);
        assertNotNull(atlas);
        Andrea.setCard(atlas);
        model.compileCardStrategy();

        //Init player state
        Andrea.setPlayerState(PlayerState.MOVED);

        //Test packet
        Board board = model.getBoard();
        /*
                  0    1     2    3    4
                +----+----+----+----+----+
            0   | FF | DD |    |    |    |
                +----+----+----+----+----+
            1   | SF | SF | TF |    |    |
                |    | A1 | M1 |    |    |
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
        Point p10 = new Point(1,0);
        board.getCell(p10).addBuilding(BuildingType.DOME);
        Point p20 = new Point(2,0);

        Point p01 = new Point(0,1);
        board.getCell(p01).addBuilding(BuildingType.FIRST_FLOOR);
        board.getCell(p01).addBuilding(BuildingType.SECOND_FLOOR);
        Point p11 = new Point(1,1);
        board.getCell(p11).addBuilding(BuildingType.FIRST_FLOOR);
        board.getCell(p11).addBuilding(BuildingType.SECOND_FLOOR);
        board.getCell(p11).setWorker(AndreaW1.getID());
        AndreaW1.setPosition(p11);
        Point p21 = new Point(2,1);
        board.getCell(p21).addBuilding(BuildingType.FIRST_FLOOR);
        board.getCell(p21).addBuilding(BuildingType.SECOND_FLOOR);
        board.getCell(p21).addBuilding(BuildingType.THIRD_FLOOR);
        board.getCell(p21).setWorker(MatteoW1.getID());
        MatteoW1.setPosition(p21);

        Point p02 = new Point(0,2);
        Point p12 = new Point(1,2);
        board.getCell(p12).addBuilding(BuildingType.FIRST_FLOOR);
        board.getCell(p12).setWorker(MatteoW2.getID());
        MatteoW2.setPosition(p12);
        Point p22 = new Point(2,2);
        board.getCell(p22).addBuilding(BuildingType.FIRST_FLOOR);
        board.getCell(p22).addBuilding(BuildingType.SECOND_FLOOR);
        board.getCell(p22).addBuilding(BuildingType.THIRD_FLOOR);

        //Expected results
        Map<Point, List<BuildingType>> expected = new HashMap<>();
        List<BuildingType> buildings = new LinkedList<>();
        buildings.add(BuildingType.SECOND_FLOOR);
        buildings.add(BuildingType.DOME);
        expected.put(p00, new LinkedList<>(buildings));

        buildings.clear();
        buildings.add(BuildingType.FIRST_FLOOR);
        buildings.add(BuildingType.DOME);
        expected.put(p20, new LinkedList<>(buildings));

        buildings.clear();
        buildings.add(BuildingType.THIRD_FLOOR);
        buildings.add(BuildingType.DOME);
        expected.put(p01, new LinkedList<>(buildings));

        buildings.clear();
        buildings.add(BuildingType.FIRST_FLOOR);
        buildings.add(BuildingType.DOME);
        expected.put(p02, new LinkedList<>(buildings));

        buildings.clear();
        buildings.add(BuildingType.DOME);
        expected.put(p22, new LinkedList<>(buildings));

        Map<Point, List<BuildingType>> returned = model.getPossibleBuilds(Andrea,AndreaW1);
        assert (expected.size() == returned.size());
        for(Point point : expected.keySet()){
            assertEquals(expected.get(point), returned.get(point));
        }
    }

    /**
     * Test building suggestions by incremental build (Default, Demeter, Hephaestus)
     */
    @Test
    void testBuildHelpAdvanced1(){
        model.compileCardStrategy(); //Compiling only default strategy

        //Init player
        Andrea.setPlayerState(PlayerState.MOVED);

        //Populate board
        Board board = model.getBoard();
        /*
                  0    1     2    3    4   X
                +----+----+----+----+----+
            0   |    | FF |    |    |    |
                |    |    | M1 |    |    |
                +----+----+----+----+----+
            1   | SF |    | DD |    |    |
                |    | A1 |    |    |    |
                +----+----+----+----+----+
            2   | DD | FF | TF |    |    |
                |    | M2 |    |    |    |
                +----+----+----+----+----+
            3   |    |    |    |    |    |
                +----+----+----+----+----+
            4   |    |    |    |    |    |
                +----+----+----+----+----+
            Y
        */
        Point p00 = new Point(0,0);
        Point p10 = new Point(1,0);
        Cell c10 = board.getCell(p10);
        c10.addBuilding(BuildingType.FIRST_FLOOR);
        Point p20 = new Point(2,0);
        Cell c20 = board.getCell(p20);
        c20.setWorker(MatteoW1.getID());
        MatteoW1.setPosition(p20);

        Point p01 = new Point(0,1);
        Cell c01 = board.getCell(p01);
        c01.addBuilding(BuildingType.FIRST_FLOOR);
        c01.addBuilding(BuildingType.SECOND_FLOOR);
        Point p11 = new Point(1,1);
        Cell c11 = board.getCell(p11);
        c11.setWorker(AndreaW1.getID());
        AndreaW1.setPosition(p11);
        Point p21 = new Point(2,1);
        Cell c21 = board.getCell(p21);
        c21.addBuilding(BuildingType.FIRST_FLOOR);
        c21.addBuilding(BuildingType.SECOND_FLOOR);
        c21.addBuilding(BuildingType.THIRD_FLOOR);
        c21.addBuilding(BuildingType.DOME);

        Point p02 = new Point(0,2);
        Cell c02 = board.getCell(p02);
        c02.addBuilding(BuildingType.DOME);
        Point p12 = new Point(1,2);
        Cell c12 = board.getCell(p12);
        c12.addBuilding(BuildingType.FIRST_FLOOR);
        c12.setWorker(MatteoW2.getID());
        MatteoW2.setPosition(p12);
        Point p22 = new Point(2,2);
        Cell c22 = board.getCell(p22);
        c22.addBuilding(BuildingType.FIRST_FLOOR);
        c22.addBuilding(BuildingType.SECOND_FLOOR);
        c22.addBuilding(BuildingType.THIRD_FLOOR);

        //Generate build data
        Map<Point, List<BuildingType>> already = new HashMap<>();
        List<BuildingType> buildings = new LinkedList<>();
        buildings.add(BuildingType.FIRST_FLOOR);
        already.put(p00, buildings);
        List<Point> dataOrder = new LinkedList<>();
        dataOrder.add(p00);
        BuildData data = new BuildData(Andrea, AndreaW1, already, dataOrder);

        //Test 1 -> Default
        Map<Point, List<BuildingType>> result = model.getPossibleBuilds(data);
        assert(result.size() == 0); //Already built, cannot do it again

        //Test 2 -> Demeter
        CardFile demeter = cardFactory.getCards().stream().filter(c->c.getName().equals("Demeter")).findAny().orElse(null);
        assertNotNull(demeter);
        Andrea.setCard(demeter);
        model.compileCardStrategy();

        Map<Point, List<BuildingType>> expected = new HashMap<>();

        List<BuildingType> expectedBuildings = new LinkedList<>();

        expectedBuildings.add(BuildingType.SECOND_FLOOR);
        expected.put(p10,new LinkedList<>(expectedBuildings));

        expectedBuildings.clear();
        expectedBuildings.add(BuildingType.THIRD_FLOOR);
        expected.put(p01,new LinkedList<>(expectedBuildings));

        expectedBuildings.clear();
        expectedBuildings.add(BuildingType.DOME);
        expected.put(p22,new LinkedList<>(expectedBuildings));

        result = model.getPossibleBuilds(data);

        assert result.size() == expected.size();
        for(Point point : expected.keySet()){
            assertEquals(expected.get(point),result.get(point));
        }

        //Test 2 -> Hephaestus
        CardFile hephaestus = cardFactory.getCards().stream().filter(c->c.getName().equals("Hephaestus")).findAny().orElse(null);
        assertNotNull(hephaestus);
        Andrea.setCard(hephaestus);
        model.compileCardStrategy();

        expected = new HashMap<>();
        expectedBuildings.clear();
        expectedBuildings.add(BuildingType.SECOND_FLOOR);
        expected.put(p00, expectedBuildings);

        result = model.getPossibleBuilds(data);
        assert result.size() == expected.size();
        for(Point point : expected.keySet()){
            assertEquals(expected.get(point),result.get(point));
        }
    }

    /*
        Player start test
     */
    @Test
    void playerStartTest(){
        List<String> players = new ArrayList<>();
        players.add("Andrea");
        players.add("Matteo");
        players.add("Mirko");
        model = new InternalModel(players, cardFactory);
        Andrea = model.getPlayerByNick("Andrea");
        Matteo = model.getPlayerByNick("Matteo");
        Mirko = model.getPlayerByNick("Mirko");
        List<Player> playerList = model.getPlayers();
        assertEquals(playerList.get(0), Andrea);
        assertEquals(playerList.get(1), Matteo);
        assertEquals(playerList.get(2), Mirko);
        model.setStartPlayer(Matteo);
        playerList = model.getPlayers();
        assertEquals(playerList.get(0), Matteo);
        assertEquals(playerList.get(1), Mirko);
        assertEquals(playerList.get(2), Andrea);
        model.setStartPlayer(Matteo);
        playerList = model.getPlayers();
        assertEquals(playerList.get(0), Matteo);
        assertEquals(playerList.get(1), Mirko);
        assertEquals(playerList.get(2), Andrea);
        model.setStartPlayer(Andrea);
        playerList = model.getPlayers();
        assertEquals(playerList.get(0), Andrea);
        assertEquals(playerList.get(1), Matteo);
        assertEquals(playerList.get(2), Mirko);
        model.setStartPlayer(Mirko);
        playerList = model.getPlayers();
        assertEquals(playerList.get(0), Mirko);
        assertEquals(playerList.get(1), Andrea);
        assertEquals(playerList.get(2), Matteo);
    }

    /*
        Test that when simulating a winning move, no more possible moves
        are returned, even if the player has such ability
     */
    @Test
    void testAllowedMovesAfterWin(){
        //Prepare rules
        CardFile artemis = cardFactory.getCards().stream().filter(c->c.getName().equals("Artemis")).findAny().orElse(null);
        assertNotNull(artemis);
        Andrea.setCard(artemis);
        model.compileCardStrategy();

        //Init player
        Andrea.setPlayerState(PlayerState.TURN_STARTED);

        //Populate board
        Board board = model.getBoard();
        /*
                  0    1     2    3    4   X
                +----+----+----+----+----+
            0   | SF | TF |    |    |    |
                | A1 |    |    |    |    |
                +----+----+----+----+----+
            1   |    |    |    |    |    |
                +----+----+----+----+----+
            2   |    |    |    |    |    |
                +----+----+----+----+----+
            3   |    |    |    |    |    |
                +----+----+----+----+----+
            4   |    |    |    |    |    |
                +----+----+----+----+----+
            Y
        */
        Point p00 = new Point(0,0);
        board.getCell(p00).addBuilding(BuildingType.FIRST_FLOOR);
        board.getCell(p00).addBuilding(BuildingType.SECOND_FLOOR);
        AndreaW1.setPosition(p00);
        board.getCell(p00).setWorker(AndreaW1.getID());

        Point p10 = new Point(1,0);
        board.getCell(p10).addBuilding(BuildingType.FIRST_FLOOR);
        board.getCell(p10).addBuilding(BuildingType.SECOND_FLOOR);
        board.getCell(p10).addBuilding(BuildingType.THIRD_FLOOR);

        Set<Point> allowed = model.getPossibleMoves(Andrea, AndreaW1);
        assert (allowed.contains(p10));

        List<Point> move = new LinkedList<>();
        move.add(p10);
        MoveData data = new MoveData(Andrea, AndreaW1, move);
        allowed = model.getPossibleMoves(data);
        assert (allowed.size() == 0);
    }
}