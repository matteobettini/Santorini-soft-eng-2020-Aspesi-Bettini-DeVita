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

class InternalModelTestMirko {

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


    /**
     * Test if Hephaestus can correctly build FIRST_FLOOR and SECOND_FLOOR at the same time but on the same spot. (b1)
     * Test if Hephaestus cannot build on different positions (b2 and b3)
     */
    @Test
    void testCompiledCardStrategyHephaestus1(){

        /*

                  0    1     2    3    4   X
                +----+----+----+----+----+
            0   |    |    |    |    |    |
                +----+----+----+----+----+
            1   |    |    |    |    |    |
                |    |    |    |    |    |
                +----+----+----+----+----+
            2   | M1 |    |    |    |    |
                |    |    |    |    |    |
                +----+----+----+----+----+
            3   | m  | b1 |    |    |    |
                +----+----+----+----+----+
            4   | b2 | b3 |    |    |    |
                +----+----+----+----+----+
            Y
        */


        CardFile hephaestus = cardFactory.getCards().stream().filter(c->c.getName().equals("Hephaestus")).findAny().orElse(null);

        Map<Player, CardFile> map = new HashMap<>();
        map.put(Mirko, hephaestus);
        model.compileCardStrategy(map);

        Board board = model.getBoard();

        Point startCell = new Point(0,2);
        Point endCell = new Point(0,3);

        board.getCell(startCell).setWorker(MirkoW1.getID());
        MirkoW1.setPosition(startCell);

        List<Point> points = new LinkedList<>();
        points.add(endCell);

        MoveData moveData = new MoveData(Mirko, MirkoW1, points);

        try{
            assertTrue(model.makeMove(moveData));
        } catch (PlayerWonSignal | PlayerLostSignal playerWonSignal) {
            assert false;
        }

        assertEquals(MirkoW1.getPosition(),endCell);
        assertEquals(board.getCell(endCell).getWorkerID(),MirkoW1.getID());

        Mirko.setPlayerState(PlayerState.MOVED);

        Point buildPoint = new Point(1,3);

        Map<Point, List<BuildingType>> builds = new HashMap<>();
        List<BuildingType> buildingTypes = new LinkedList<>();
        buildingTypes.add(BuildingType.FIRST_FLOOR);
        buildingTypes.add(BuildingType.SECOND_FLOOR);
        builds.put(buildPoint,buildingTypes);

        try{
            assertTrue(model.makeBuild(new BuildData(Mirko, MirkoW1, builds, null)));
        } catch (PlayerWonSignal | PlayerLostSignal playerWonSignal) {
            assert false;
        }

        assertEquals(board.getCell(buildPoint).getTopBuilding(), LevelType.SECOND_FLOOR);

        //SECOND CASE
        Point buildPoint1 = new Point(0,4);
        Point buildPoint2 = new Point(1,4);
        builds = new HashMap<>();
        buildingTypes = new LinkedList<>();
        buildingTypes.add(BuildingType.FIRST_FLOOR);
        builds.put(buildPoint1,buildingTypes);
        buildingTypes = new LinkedList<>();
        buildingTypes.add(BuildingType.SECOND_FLOOR);
        builds.put(buildPoint1,buildingTypes);

        try{
            assertFalse(model.makeBuild(new BuildData(Mirko, MirkoW1, builds, null)));
        } catch (PlayerWonSignal | PlayerLostSignal playerWonSignal) {
            assert false;
        }

        assertEquals(board.getCell(buildPoint1).getTopBuilding(), LevelType.GROUND);
        assertEquals(board.getCell(buildPoint2).getTopBuilding(), LevelType.GROUND);

    }

    /**
     * Test if Hephaestus cannot build two blocks on the same spot because one of them is a Dome.
     */
    @Test
    void testCompiledCardStrategyHephaestus2(){

        /*
                  0    1     2    3    4   X
                +----+----+----+----+----+
            0   |    |    |    |    |    |
                +----+----+----+----+----+
            1   |    |    |    |    |    |
                |    |    |    |    |    |
                +----+----+----+----+----+
            2   | M1 |    |    |    |    |
                |    |    |    |    |    |
                +----+----+----+----+----+
            3   | m  | b  |    |    |    |
                +----+----+----+----+----+
            4   |    |    |    |    |    |
                +----+----+----+----+----+
            Y
        */

        CardFile hephaestus = cardFactory.getCards().stream().filter(c->c.getName().equals("Hephaestus")).findAny().orElse(null);

        Map<Player, CardFile> map = new HashMap<>();
        map.put(Mirko, hephaestus);
        model.compileCardStrategy(map);

        Board board = model.getBoard();

        Point startCell = new Point(0,2);
        Point endCell = new Point(0,3);

        board.getCell(startCell).setWorker(MirkoW1.getID());
        MirkoW1.setPosition(startCell);

        List<Point> points = new LinkedList<>();
        points.add(endCell);

        MoveData moveData = new MoveData(Mirko, MirkoW1, points);

        try{
            assertTrue(model.makeMove(moveData));
        } catch (PlayerWonSignal | PlayerLostSignal playerWonSignal) {
            assert false;
        }

        assertEquals(MirkoW1.getPosition(),endCell);
        assertEquals(board.getCell(endCell).getWorkerID(),MirkoW1.getID());

        Mirko.setPlayerState(PlayerState.MOVED);

        Point buildPoint = new Point(1,3);

        board.getCell(buildPoint).addBuilding(BuildingType.FIRST_FLOOR);
        board.getCell(buildPoint).addBuilding(BuildingType.SECOND_FLOOR);

        Map<Point, List<BuildingType>> builds = new HashMap<>();
        List<BuildingType> buildingTypes = new LinkedList<>();
        buildingTypes.add(BuildingType.THIRD_FLOOR);
        buildingTypes.add(BuildingType.DOME);
        builds.put(buildPoint,buildingTypes);

        try{
            assertFalse(model.makeBuild(new BuildData(Mirko, MirkoW1, builds, null)));
        } catch (PlayerWonSignal | PlayerLostSignal playerWonSignal) {
            assert false;
        }

    }

    /**
     * Test if Hephaestus cannot move because he is blocked by the other Workers and a Dome.
     */
    @Test
    void testCompiledCardStrategyHephaestus3(){

        /*
                  0    1     2    3    4   X
                +----+----+----+----+----+
            0   |    |    |    |    |    |
                +----+----+----+----+----+
            1   |    |    |    |    |    |
                | A2 | D  |    |    |    |
                +----+----+----+----+----+
            2   | M1 |  A1|    |    |    |
                |    |    |    |    |    |
                +----+----+----+----+----+
            3   | M2 | M1 |    |    |    |
                +----+----+----+----+----+
            4   |    |    |    |    |    |
                +----+----+----+----+----+
            Y
        */

        CardFile hephaestus = cardFactory.getCards().stream().filter(c->c.getName().equals("Hephaestus")).findAny().orElse(null);

        Map<Player, CardFile> map = new HashMap<>();
        map.put(Mirko, hephaestus);
        model.compileCardStrategy(map);

        Board board = model.getBoard();

        Point startCell = new Point(0,2);
        Point endCell1 = new Point(0,3);
        Point endCell2 = new Point(1,1);
        Point andreaW1Pos = new Point(1,2);
        Point andreaW2Pos = new Point(0,1);
        Point matteoW1Pos = new Point(1,3);
        Point matteoW2Pos = new Point(0,3);

        board.getCell(startCell).setWorker(MirkoW1.getID());
        MirkoW1.setPosition(startCell);

        board.getCell(andreaW1Pos).setWorker(AndreaW1.getID());
        AndreaW1.setPosition(andreaW1Pos);
        board.getCell(andreaW2Pos).setWorker(AndreaW2.getID());
        AndreaW2.setPosition(andreaW2Pos);

        board.getCell(matteoW1Pos).setWorker(MatteoW1.getID());
        MatteoW1.setPosition(matteoW1Pos);
        board.getCell(matteoW2Pos).setWorker(MatteoW2.getID());
        MatteoW2.setPosition(matteoW2Pos);

        board.getCell(endCell2).addBuilding(BuildingType.FIRST_FLOOR);
        board.getCell(endCell2).addBuilding(BuildingType.SECOND_FLOOR);
        board.getCell(endCell2).addBuilding(BuildingType.THIRD_FLOOR);
        board.getCell(endCell2).addBuilding(BuildingType.DOME);

        List<Point> points = new LinkedList<>();
        points.add(endCell1);

        MoveData moveData = new MoveData(Mirko, MirkoW1, points);

        try{
            assertFalse(model.makeMove(moveData));
        } catch (PlayerWonSignal | PlayerLostSignal playerWonSignal) {
            assert false;
        }

        points = new LinkedList<>();
        points.add(endCell2);

        moveData = new MoveData(Mirko, MirkoW1, points);

        try{
            assertFalse(model.makeMove(moveData));
        } catch (PlayerWonSignal | PlayerLostSignal playerWonSignal) {
            assert false;
        }


    }

    /**
     * Test if Hephaestus cannot build where he moved and cannot build onto another Worker.
     */
    @Test
    void testCompiledCardStrategyHephaestus4(){

         /*
                  0    1     2    3    4   X
                +----+----+----+----+----+
            0   |    |    |    |    |    |
                +----+----+----+----+----+
            1   |    |    |    |    |    |
                | A2 | D  |    |    |    |
                +----+----+----+----+----+
            2   | M1 |  A1|    |    |    |
                |    |  FF|    |    |    |
                +----+----+----+----+----+
            3   | M2 |    |    |    |    |
                +----+----+----+----+----+
            4   |    |    |    |    |    |
                +----+----+----+----+----+
            Y
        */

        CardFile hephaestus = cardFactory.getCards().stream().filter(c->c.getName().equals("Hephaestus")).findAny().orElse(null);

        Map<Player, CardFile> map = new HashMap<>();
        map.put(Mirko, hephaestus);
        model.compileCardStrategy(map);

        Board board = model.getBoard();

        Point startCell = new Point(0,2);
        Point endCell = new Point(1,3);
        Point andreaW1Pos = new Point(1,2);
        Point andreaW2Pos = new Point(0,1);
        Point matteoW2Pos = new Point(0,3);

        board.getCell(startCell).setWorker(MirkoW1.getID());
        MirkoW1.setPosition(startCell);

        board.getCell(andreaW1Pos).addBuilding(BuildingType.FIRST_FLOOR);
        board.getCell(andreaW1Pos).setWorker(AndreaW1.getID());
        AndreaW1.setPosition(andreaW1Pos);
        board.getCell(andreaW2Pos).setWorker(AndreaW2.getID());
        AndreaW2.setPosition(andreaW2Pos);

        board.getCell(matteoW2Pos).setWorker(MatteoW2.getID());
        MatteoW2.setPosition(matteoW2Pos);

        List<Point> points = new LinkedList<>();
        points.add(endCell);

        MoveData moveData = new MoveData(Mirko, MirkoW1, points);

        try{
                assertTrue(model.makeMove(moveData));
        } catch (PlayerWonSignal | PlayerLostSignal playerWonSignal) {
            assert false;
        }

        assertEquals(MirkoW1.getPosition(),endCell);
        assertEquals(board.getCell(endCell).getWorkerID(),MirkoW1.getID());

        Mirko.setPlayerState(PlayerState.MOVED);

        Point buildPoint = new Point(1,3);

        Map<Point, List<BuildingType>> builds = new HashMap<>();
        List<BuildingType> buildingTypes = new LinkedList<>();
        buildingTypes.add(BuildingType.FIRST_FLOOR);
        buildingTypes.add(BuildingType.SECOND_FLOOR);
        builds.put(buildPoint,buildingTypes);

        try{
            assertFalse(model.makeBuild(new BuildData(Mirko, MirkoW1, builds, null)));
        } catch (PlayerWonSignal | PlayerLostSignal playerWonSignal) {
            assert false;
        }

        assertEquals(board.getCell(buildPoint).getTopBuilding(), LevelType.GROUND);

        buildPoint = new Point(1,2);

        builds = new HashMap<>();
        buildingTypes = new LinkedList<>();
        buildingTypes.add(BuildingType.FIRST_FLOOR);
        buildingTypes.add(BuildingType.SECOND_FLOOR);
        builds.put(buildPoint,buildingTypes);

        try{
            assertFalse(model.makeBuild(new BuildData(Mirko, MirkoW1, builds, null)));
        } catch (PlayerWonSignal | PlayerLostSignal playerWonSignal) {
            assert false;
        }

        assertEquals(board.getCell(buildPoint).getTopBuilding(), LevelType.FIRST_FLOOR);

    }

}