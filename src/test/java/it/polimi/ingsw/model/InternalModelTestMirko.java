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
import it.polimi.ingsw.packets.InvalidPacketException;
import it.polimi.ingsw.packets.PacketBuild;
import it.polimi.ingsw.packets.PacketMove;
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

        Mirko.setCard(hephaestus);
        model.compileCardStrategy();

        Board board = model.getBoard();

        Point startCell = new Point(0,2);
        Point endCell = new Point(0,3);

        board.getCell(startCell).setWorker(MirkoW1.getID());
        MirkoW1.setPosition(startCell);

        List<Point> points = new LinkedList<>();
        points.add(endCell);

        PacketMove packetMove = new PacketMove(Mirko.getNickname(),MirkoW1.getID(), points);

        assertNotNull(packetMove);

        try{
            MoveData moveData = model.packetMoveToMoveData(packetMove);
            assertTrue(model.makeMove(moveData));
        } catch (PlayerWonSignal | PlayerLostSignal | InvalidPacketException e) {
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
        List<Point> dataOrder = new ArrayList<>();
        dataOrder.add(buildPoint);

        PacketBuild packetBuild = new PacketBuild(Mirko.getNickname(),MirkoW1.getID(),builds,dataOrder);

        assertNotNull(packetBuild);

        try{
            BuildData buildData = model.packetBuildToBuildData(packetBuild);
            assertTrue(model.makeBuild(buildData));
        } catch (InvalidPacketException | PlayerWonSignal | PlayerLostSignal e) {
            assert false;
        }

        assertEquals(board.getCell(buildPoint).getTopBuilding(), LevelType.SECOND_FLOOR);
        assertEquals(PlayerState.BUILT,Mirko.getState());

        Mirko.setPlayerState(PlayerState.MOVED);
        assertEquals(PlayerState.MOVED,Mirko.getState());

        //SECOND CASE
        Point buildPoint1 = new Point(0,4);
        Point buildPoint2 = new Point(1,4);
        builds = new HashMap<>();
        buildingTypes = new LinkedList<>();
        buildingTypes.add(BuildingType.FIRST_FLOOR);
        builds.put(buildPoint1,buildingTypes);
        buildingTypes = new LinkedList<>();
        buildingTypes.add(BuildingType.SECOND_FLOOR);
        builds.put(buildPoint2,buildingTypes);
        dataOrder = new ArrayList<>();
        dataOrder.add(buildPoint1);
        dataOrder.add(buildPoint2);

        packetBuild = new PacketBuild(Mirko.getNickname(),MirkoW1.getID(),builds,dataOrder);

        assertNotNull(packetBuild);

        try{
            BuildData buildData = model.packetBuildToBuildData(packetBuild);
            assertFalse(model.makeBuild(buildData));
        } catch (InvalidPacketException | PlayerWonSignal | PlayerLostSignal e) {
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

        Mirko.setCard(hephaestus);
        model.compileCardStrategy();

        Board board = model.getBoard();

        Point startCell = new Point(0,2);
        Point endCell = new Point(0,3);

        board.getCell(startCell).setWorker(MirkoW1.getID());
        MirkoW1.setPosition(startCell);

        List<Point> points = new LinkedList<>();
        points.add(endCell);

        PacketMove packetMove = new PacketMove(Mirko.getNickname(),MirkoW1.getID(), points);

        assertNotNull(packetMove);

        try{
            MoveData moveData = model.packetMoveToMoveData(packetMove);
            assertTrue(model.makeMove(moveData));
        } catch (PlayerWonSignal | PlayerLostSignal | InvalidPacketException e) {
            assert false;
        }

        assertEquals(Mirko.getState(),PlayerState.MOVED);
        assertEquals(MirkoW1.getPosition(),endCell);
        assertEquals(board.getCell(endCell).getWorkerID(),MirkoW1.getID());



        Point buildPoint = new Point(1,3);

        board.getCell(buildPoint).addBuilding(BuildingType.FIRST_FLOOR);
        board.getCell(buildPoint).addBuilding(BuildingType.SECOND_FLOOR);

        Map<Point, List<BuildingType>> builds = new HashMap<>();
        List<BuildingType> buildingTypes = new LinkedList<>();
        buildingTypes.add(BuildingType.THIRD_FLOOR);
        buildingTypes.add(BuildingType.DOME);
        builds.put(buildPoint,buildingTypes);
        List<Point> dataOrder = new ArrayList<>();
        dataOrder.add(buildPoint);

        PacketBuild packetBuild = new PacketBuild(Mirko.getNickname(),MirkoW1.getID(),builds,dataOrder);

        assertNotNull(packetBuild);

        try{
            BuildData buildData = model.packetBuildToBuildData(packetBuild);
            assertFalse(model.makeBuild(buildData));
        } catch (InvalidPacketException | PlayerWonSignal | PlayerLostSignal e) {
            assert false;
        }

        assertEquals(Mirko.getState(),PlayerState.MOVED);
        assertEquals(board.getCell(buildPoint).getTopBuilding(), LevelType.SECOND_FLOOR);

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

        Mirko.setCard(hephaestus);
        model.compileCardStrategy();

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

        PacketMove packetMove = new PacketMove(Mirko.getNickname(),MirkoW1.getID(), points);

        assertNotNull(packetMove);

        try{
            MoveData moveData = model.packetMoveToMoveData(packetMove);
            assertFalse(model.makeMove(moveData));
        } catch (PlayerWonSignal | PlayerLostSignal | InvalidPacketException e) {
            assert false;
        }

        assertEquals(MirkoW1.getPosition(),startCell);
        assertEquals(PlayerState.TURN_STARTED, Mirko.getState());

        points = new LinkedList<>();
        points.add(endCell2);

        packetMove = new PacketMove(Mirko.getNickname(),MirkoW1.getID(), points);

        assertNotNull(packetMove);

        try{
            MoveData moveData = model.packetMoveToMoveData(packetMove);
            assertFalse(model.makeMove(moveData));
            assert false;
        } catch (PlayerWonSignal | PlayerLostSignal | InvalidPacketException e) {
            assert true;
        }

        assertEquals(MirkoW1.getPosition(),startCell);
        assertEquals(PlayerState.TURN_STARTED, Mirko.getState());


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

        Mirko.setCard(hephaestus);
        model.compileCardStrategy();

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

        PacketMove packetMove = new PacketMove(Mirko.getNickname(),MirkoW1.getID(), points);

        assertNotNull(packetMove);

        try{
            MoveData moveData = model.packetMoveToMoveData(packetMove);
            assertTrue(model.makeMove(moveData));
        } catch (PlayerWonSignal | PlayerLostSignal | InvalidPacketException e) {
            assert false;
        }

        assertEquals(MirkoW1.getPosition(),endCell);
        assertEquals(board.getCell(endCell).getWorkerID(),MirkoW1.getID());

        assertEquals(Mirko.getState(),PlayerState.MOVED);

        Point buildPoint = new Point(1,3);

        Map<Point, List<BuildingType>> builds = new HashMap<>();
        List<BuildingType> buildingTypes = new LinkedList<>();
        buildingTypes.add(BuildingType.FIRST_FLOOR);
        buildingTypes.add(BuildingType.SECOND_FLOOR);
        builds.put(buildPoint,buildingTypes);
        List<Point> dataOrder = new ArrayList<>();
        dataOrder.add(buildPoint);

        PacketBuild packetBuild = new PacketBuild(Mirko.getNickname(),MirkoW1.getID(),builds,dataOrder);

        assertNotNull(packetBuild);

        try{
            BuildData buildData = model.packetBuildToBuildData(packetBuild);
            assertFalse(model.makeBuild(buildData));
        } catch (InvalidPacketException | PlayerWonSignal | PlayerLostSignal e) {
            assert false;
        }

        assertEquals(board.getCell(buildPoint).getTopBuilding(), LevelType.GROUND);

        assertEquals(Mirko.getState(),PlayerState.MOVED);

        buildPoint = new Point(1,2);

        builds = new HashMap<>();
        buildingTypes = new LinkedList<>();
        buildingTypes.add(BuildingType.FIRST_FLOOR);
        buildingTypes.add(BuildingType.SECOND_FLOOR);
        builds.put(buildPoint,buildingTypes);
        dataOrder = new ArrayList<>();
        dataOrder.add(buildPoint);

        packetBuild = new PacketBuild(Mirko.getNickname(),MirkoW1.getID(),builds,dataOrder);

        assertNotNull(packetBuild);

        try{
            BuildData buildData = model.packetBuildToBuildData(packetBuild);
            assertFalse(model.makeBuild(buildData));
        } catch (InvalidPacketException | PlayerWonSignal | PlayerLostSignal e) {
            assert false;
        }

        assertEquals(Mirko.getState(),PlayerState.MOVED);
        assertEquals(board.getCell(buildPoint).getTopBuilding(), LevelType.FIRST_FLOOR);

    }

    //Tests from 1-9 test if the method that converts a PacketMove into a MoveData correctly returns the InvalidPacketException.
    //Test 10 tests the correct execution.
    /**
     * Test if the argument PlayerNickname is not one of the registered Player.
     */
    @Test
    void testPacketMoveToMoveData1(){
        //CASE WRONG PLAYER
        List<Point> moves = new ArrayList<>();
        moves.add(new Point(0,0));
        PacketMove packetMove = new PacketMove("Marco", MirkoW1.getID(),moves);
        try{
            MoveData moveData = model.packetMoveToMoveData(packetMove);
            assert false;
        } catch (InvalidPacketException e) {
            assert true;
        }

        /*CASE MOVES IS NULL
        moves = null;

        packetMove = new PacketMove("Mirko", MirkoW1.getID(),moves);
        try{
            MoveData moveData = model.packetMoveToMoveData(packetMove);
            assert false;
        } catch (InvalidPacketException e) {
            assert true;
        }*/


        /*CASE A POINT IS NULL
        moves = new ArrayList<>();
        moves.add(null);

        packetMove = new PacketMove("Mirko", MirkoW1.getID(),moves);
        try{
            MoveData moveData = model.packetMoveToMoveData(packetMove);
            assert false;
        } catch (InvalidPacketException e) {
            assert true;
        }*/

    }

    /**
     * Test if the argument workerID is not one of the Worker in the game.
     */
    @Test
    void testPacketMoveToMoveData2(){
        //CASE WRONG WORKER
        List<Point> moves = new ArrayList<>();
        moves.add(new Point(0,0));

        PacketMove packetMove = new PacketMove(Mirko.getNickname(), "MirkoW3",moves);
        try{
            MoveData moveData = model.packetMoveToMoveData(packetMove);
            assert false;
        } catch (InvalidPacketException e) {
            assert true;
        }
    }

    /**
     * Test if the moves list is empty.
     */
    @Test
    void testPacketMoveToMoveData3(){
        //CASE MOVES IS EMPTY

        List<Point> moves = new ArrayList<>();

        PacketMove packetMove = new PacketMove(Mirko.getNickname(), MirkoW1.getID(),moves);
        try{
            MoveData moveData = model.packetMoveToMoveData(packetMove);
            assert false;
        } catch (InvalidPacketException e) {
            assert true;
        }
    }

    /**
     * Test if one of the points is not on the Board.
     */
    @Test
    void testPacketMoveToMoveData4(){
        //CASE A POINT IS NOT ON THE BOARD
        List<Point >moves = new ArrayList<>();
        moves.add(new Point(6,6));

        PacketMove packetMove = new PacketMove(Mirko.getNickname(), MirkoW1.getID(),moves);
        try{
            MoveData moveData = model.packetMoveToMoveData(packetMove);
            assert false;
        } catch (InvalidPacketException e) {
            assert true;
        }
    }

    /**
     * Test if in the first move position there is a DOME.
     */
    @Test
    void testPacketMoveToMoveData5(){
        //CASE IN FIRST MOVE THERE IS A DOME
        Point startPosition = new Point(0,0);
        MirkoW1.setPosition(startPosition);
        Point firstMove = new Point(0,1);
        model.getBoard().getCell(firstMove).addBuilding(BuildingType.DOME);

        List<Point> moves = new ArrayList<>();
        moves.add(firstMove);

        PacketMove packetMove = new PacketMove("Mirko", MirkoW1.getID(),moves);
        try{
            MoveData moveData = model.packetMoveToMoveData(packetMove);
            assert false;
        } catch (InvalidPacketException e) {
            assert true;
        }
    }

    /**
     * Test if the first move position is not adjacent to the start position.
     */
    @Test
    void testPacketMoveToMoveData6(){
        //CASE THE FIRST MOVE IS NOT ADJACENT TO THE START POSITION

        Point startPosition = new Point(0,0);
        MirkoW1.setPosition(startPosition);
        Point firstMove = new Point(0,2);

        List<Point> moves = new ArrayList<>();
        moves.add(firstMove);

        PacketMove packetMove = new PacketMove("Mirko", MirkoW1.getID(),moves);
        try{
            MoveData moveData = model.packetMoveToMoveData(packetMove);
            assert false;
        } catch (InvalidPacketException e) {
            assert true;
        }
    }

    /**
     * Test if the second move position is not adjacent to the first move position.
     */
    @Test
    void testPacketMoveToMoveData7(){
        //CASE THE SECOND MOVE IS NOT ADJACENT TO THE FIRST MOVE

        Point startPosition = new Point(0,0);
        MirkoW1.setPosition(startPosition);
        Point firstMove = new Point(1,0);
        Point secondMove = new Point(0,3);

        List<Point> moves = new ArrayList<>();
        moves.add(firstMove);
        moves.add(secondMove);

        PacketMove packetMove = new PacketMove("Mirko", MirkoW1.getID(),moves);
        try{
            MoveData moveData = model.packetMoveToMoveData(packetMove);
            assert false;
        } catch (InvalidPacketException e) {
            assert true;
        }
    }

    /**
     * Test if in the second move position there is a DOME.
     */
    @Test
    void testPacketMoveToMoveData8(){
        //CASE THE SECOND MOVE IS A DOME

        Point startPosition = new Point(0,0);
        MirkoW1.setPosition(startPosition);
        Point firstMove = new Point(0,1);
        Point secondMove = new Point(0,2);
        model.getBoard().getCell(secondMove).addBuilding(BuildingType.DOME);

        List<Point> moves = new ArrayList<>();
        moves.add(firstMove);
        moves.add(secondMove);

        PacketMove packetMove = new PacketMove("Mirko", MirkoW1.getID(),moves);
        try{
            MoveData moveData = model.packetMoveToMoveData(packetMove);
            assert false;
        } catch (InvalidPacketException e) {
            assert true;
        }
    }

    /**
     * The Worker belongs to another Player.
     */
    @Test
    void testPacketMoveToMoveData9(){
        List<Point> moves = new ArrayList<>();
        moves.add(new Point(0,0));
        PacketMove packetMove = new PacketMove(Andrea.getNickname(), MirkoW1.getID(),moves);
        try{
            MoveData moveData = model.packetMoveToMoveData(packetMove);
            assert false;
        } catch (InvalidPacketException e) {
            assert true;
        }
    }

    /**
     * Test if the method correctly convert the packet because all the conditions are satisfied.
     */
    @Test
    void testPacketMoveToMoveData10(){
        //CASE EVERYTHING SHOULD BE FINE
        Point startPosition = new Point(0,0);
        MirkoW1.setPosition(startPosition);
        Point firstMove = new Point(1,0);
        Point secondMove = new Point(1,1);
        model.getBoard().getCell(firstMove).addBuilding(BuildingType.FIRST_FLOOR);
        model.getBoard().getCell(secondMove).addBuilding(BuildingType.FIRST_FLOOR);
        model.getBoard().getCell(secondMove).addBuilding(BuildingType.SECOND_FLOOR);

        List<Point> moves = new ArrayList<>();
        moves.add(firstMove);
        moves.add(secondMove);

        PacketMove packetMove = new PacketMove("Mirko", MirkoW1.getID(),moves);
        try{
            MoveData moveData = model.packetMoveToMoveData(packetMove);
            assert true;
        } catch (InvalidPacketException e) {
            assert false;
        }

    }


    //Tests from 1 - 10 test the method that converts a PacketBuild into a BuildData correctly returns the InvalidPacketException.
    //Test 11 tests the correct execution.
    /**
     * The argument PlayerNickname is not one of the registered Player.
     */
    @Test
    void testPacketBuildToBuildData1(){

        Map<Point, List<BuildingType>> builds = new HashMap<>();

        List<Point> orderBuilds = new ArrayList<>();

        Point startPosition = new Point(0,0);
        Point firstBuild = new Point(0,1);

        List<BuildingType> buildings = new ArrayList<>();
        buildings.add(BuildingType.FIRST_FLOOR);
        buildings.add(BuildingType.SECOND_FLOOR);
        builds.put(firstBuild,buildings);

        orderBuilds.add(firstBuild);

        MirkoW1.setPosition(startPosition);


        //CASE WRONG PLAYER
        PacketBuild packetBuild = new PacketBuild("Marco",MirkoW1.getID(),builds,orderBuilds);

        try{
            BuildData buildData = model.packetBuildToBuildData(packetBuild);
            assert false;
        } catch (InvalidPacketException e) {
            assert true;
        }

        /*CASE BUILDSORDER IS NULL

        packetBuild = new PacketBuild(Mirko.getNickname(),MirkoW1.getID(),builds,null);

        try{
            BuildData buildData = model.packetBuildToBuildData(packetBuild);
            assert false;
        } catch (InvalidPacketException e) {
            assert true;
        }*/


        /*CASE BUILD IS NULL
        orderBuilds = new ArrayList<>();
        orderBuilds.add(firstBuild);
        packetBuild = new PacketBuild(Mirko.getNickname(),MirkoW1.getID(),null,orderBuilds);

        try{
            BuildData buildData = model.packetBuildToBuildData(packetBuild);
            assert false;
        } catch (InvalidPacketException e) {
            assert true;
        }*/


        /*CASE BUILDINGTYPE IN BUILDS IS NULL
        builds = new HashMap<>();
        orderBuilds = new ArrayList<>();
        builds.put(firstBuild,null);
        orderBuilds.add(firstBuild);

        packetBuild = new PacketBuild(Mirko.getNickname(),MirkoW1.getID(),builds,orderBuilds);

        try{
            BuildData buildData = model.packetBuildToBuildData(packetBuild);
            assert false;
        } catch (InvalidPacketException e) {
            assert true;
        }*/

    }

    /**
     * The argument workerID is not one of the Worker in the game.
     */
    @Test
    void testPacketBuildToBuildData2(){

        Map<Point, List<BuildingType>> builds = new HashMap<>();

        List<Point> orderBuilds = new ArrayList<>();
        Point startPosition = new Point(0,0);
        Point firstBuild = new Point(0,1);

        List<BuildingType> buildings = new ArrayList<>();
        buildings.add(BuildingType.FIRST_FLOOR);
        buildings.add(BuildingType.SECOND_FLOOR);

        builds.put(firstBuild,buildings);
        orderBuilds.add(firstBuild);

        MirkoW1.setPosition(startPosition);

        //CASE WRONG WORKER
        PacketBuild packetBuild = new PacketBuild(Mirko.getNickname(),"MirkoW3",builds,orderBuilds);

        try{
            BuildData buildData = model.packetBuildToBuildData(packetBuild);
            assert false;
        } catch (InvalidPacketException e) {
            assert true;
        }
    }

    /**
     * The buildsOrder List is empty.
     */
    @Test
    void testPacketBuildToBuildData3(){

        Map<Point, List<BuildingType>> builds = new HashMap<>();

        List<Point> orderBuilds = new ArrayList<>();

        Point startPosition = new Point(0,0);
        Point firstBuild = new Point(0,1);

        List<BuildingType> buildings = new ArrayList<>();
        buildings.add(BuildingType.FIRST_FLOOR);
        buildings.add(BuildingType.SECOND_FLOOR);

        builds.put(firstBuild,buildings);

        MirkoW1.setPosition(startPosition);

        //CASE BUILDSORDER EMPTY

        PacketBuild packetBuild = new PacketBuild(Mirko.getNickname(),MirkoW1.getID(),builds,orderBuilds);

        try{
            BuildData buildData = model.packetBuildToBuildData(packetBuild);
            assert false;
        } catch (InvalidPacketException e) {
            assert true;
        }
    }

    /**
     * The size of buildsOrder is not equal to the size of builds.
     */
    @Test
    void testPacketBuildToBuildData4(){
        Map<Point, List<BuildingType>> builds = new HashMap<>();

        List<Point> orderBuilds = new ArrayList<>();

        Point startPosition = new Point(0,0);
        Point firstBuild = new Point(0,1);
        Point secondBuild = new Point(1,0);

        List<BuildingType> buildings = new ArrayList<>();
        buildings.add(BuildingType.FIRST_FLOOR);
        buildings.add(BuildingType.SECOND_FLOOR);

        builds.put(firstBuild,buildings);
        builds.put(secondBuild, buildings); //BUILDS HAS SIZE 2

        orderBuilds.add(firstBuild); //ORDERBUILDS HAS SIZE 1

        MirkoW1.setPosition(startPosition);

        //CASE BUILDSORDER.size() != BUILDS.size()


        PacketBuild packetBuild = new PacketBuild(Mirko.getNickname(),MirkoW1.getID(),builds,orderBuilds);

        try{
            BuildData buildData = model.packetBuildToBuildData(packetBuild);
            assert false;
        } catch (InvalidPacketException e) {
            assert true;
        }
    }

    /**
     * The builds Map is empty.
     */
    @Test
    void testPacketBuildToBuildData5(){

        Map<Point, List<BuildingType>> builds = new HashMap<>();

        Point startPosition = new Point(0,0);
        Point firstBuild = new Point(0,1);

        List<Point> orderBuilds = new ArrayList<>();
        orderBuilds.add(firstBuild);

        MirkoW1.setPosition(startPosition);

        //CASE BUILDS IS EMPTY

        PacketBuild packetBuild = new PacketBuild(Mirko.getNickname(),MirkoW1.getID(),builds,orderBuilds);

        try{
            BuildData buildData = model.packetBuildToBuildData(packetBuild);
            assert false;
        } catch (InvalidPacketException e) {
            assert true;
        }
    }

    /**
     * There is a buildingType empty list in builds Map.
     */
    @Test
    void testPacketBuildToBuildData6(){

        Map<Point, List<BuildingType>> builds = new HashMap<>();

        Point startPosition = new Point(0,0);
        Point firstBuild = new Point(0,1);

        List<BuildingType> buildings = new ArrayList<>();
        builds.put(firstBuild,buildings);

        List<Point> orderBuilds = new ArrayList<>();
        orderBuilds.add(firstBuild);

        MirkoW1.setPosition(startPosition);

        //CASE BUILDINGTYPE LIST IN BUILDS IS EMPTY
        PacketBuild packetBuild = new PacketBuild(Mirko.getNickname(),MirkoW1.getID(),builds,orderBuilds);

        try{
            BuildData buildData = model.packetBuildToBuildData(packetBuild);
            assert false;
        } catch (InvalidPacketException e) {
            assert true;
        }
    }

    /**
     * There is a point not on the Board in builds Map.
     */
    @Test
    void testPacketBuildToBuildData7(){
        Map<Point, List<BuildingType>> builds = new HashMap<>();

        List<Point> orderBuilds = new ArrayList<>();

        Point startPosition = new Point(0,0);
        Point out = new Point(6,6);

        List<BuildingType> buildings = new ArrayList<>();
        buildings.add(BuildingType.FIRST_FLOOR);

        builds.put(out,buildings);
        orderBuilds.add(out);

        MirkoW1.setPosition(startPosition);

        //CASE POINT IN BUILDS IS NOT ON THE BOARD

        PacketBuild packetBuild = new PacketBuild(Mirko.getNickname(),MirkoW1.getID(),builds,orderBuilds);

        try{
            BuildData buildData = model.packetBuildToBuildData(packetBuild);
            assert false;
        } catch (InvalidPacketException e) {
            assert true;
        }
    }

    /**
     * There is a not adjacent points in builds.
     */
    @Test
    void testPacketBuildToBuildData8(){
        Map<Point, List<BuildingType>> builds = new HashMap<>();

        List<Point> orderBuilds = new ArrayList<>();

        Point startPosition = new Point(0,0);

        List<BuildingType> buildings = new ArrayList<>();
        buildings.add(BuildingType.FIRST_FLOOR);

        Point notAdjacent = new Point(0,3);

        builds.put(notAdjacent,buildings);
        orderBuilds.add(notAdjacent);


        MirkoW1.setPosition(startPosition);

        //CASE BUILD POINT NOT ADJACENT

        PacketBuild packetBuild = new PacketBuild(Mirko.getNickname(),MirkoW1.getID(),builds,orderBuilds);

        try{
            BuildData buildData = model.packetBuildToBuildData(packetBuild);
            assert false;
        } catch (InvalidPacketException e) {
            assert true;
        }
    }

    /**
     * There is a point in builds Map that is different from the one in buildsOrder List.
     */
    @Test
    void testPacketBuildToBuildData9(){
        Map<Point, List<BuildingType>> builds = new HashMap<>();

        List<Point> orderBuilds = new ArrayList<>();

        Point startPosition = new Point(0,0);

        Point firstBuild = new Point(0,1);

        Point differentBuild = new Point(1,0);

        List<BuildingType> buildings = new ArrayList<>();
        buildings.add(BuildingType.FIRST_FLOOR);
        buildings.add(BuildingType.SECOND_FLOOR);
        builds.put(firstBuild,buildings);

        orderBuilds.add(differentBuild);

        MirkoW1.setPosition(startPosition);

        //CASE BUILD POINT IS NOT THE SAME IN BUILDSORDER

        PacketBuild packetBuild = new PacketBuild(Mirko.getNickname(),MirkoW1.getID(),builds,orderBuilds);

        try{
            BuildData buildData = model.packetBuildToBuildData(packetBuild);
            assert false;
        } catch (InvalidPacketException e) {
            assert true;
        }
    }

    /**
     * The Worker belongs to another player.
     */
    @Test
    void testPacketBuildToBuildData10(){
        Map<Point, List<BuildingType>> builds = new HashMap<>();

        List<Point> orderBuilds = new ArrayList<>();

        Point startPosition = new Point(0,0);
        Point firstBuild = new Point(0,1);

        List<BuildingType> buildings = new ArrayList<>();
        buildings.add(BuildingType.FIRST_FLOOR);
        buildings.add(BuildingType.SECOND_FLOOR);
        builds.put(firstBuild,buildings);

        orderBuilds.add(firstBuild);

        MirkoW1.setPosition(startPosition);


        //CASE WRONG PLAYER
        PacketBuild packetBuild = new PacketBuild(Andrea.getNickname(),MirkoW1.getID(),builds,orderBuilds);

        try{
            BuildData buildData = model.packetBuildToBuildData(packetBuild);
            assert false;
        } catch (InvalidPacketException e) {
            assert true;
        }
    }

    /**
     * The method correctly converts the packet because all the conditions are satisfied.
     */
    @Test
    void testPacketBuildToBuildData11(){
        //CASE EVERYTHING SHOULD BE FINE
        Map<Point, List<BuildingType>> builds = new HashMap<>();

        List<Point> orderBuilds = new ArrayList<>();

        Point startPosition = new Point(0,0);

        Point firstBuild = new Point(0,1);

        Point secondBuild = new Point(1,0);

        List<BuildingType> buildings = new ArrayList<>();
        buildings.add(BuildingType.FIRST_FLOOR);
        buildings.add(BuildingType.SECOND_FLOOR);

        builds.put(firstBuild,buildings);

        buildings = new ArrayList<>();
        buildings.add(BuildingType.FIRST_FLOOR);

        builds.put(secondBuild,buildings);

        orderBuilds.add(firstBuild);
        orderBuilds.add(secondBuild);

        MirkoW1.setPosition(startPosition);

        PacketBuild packetBuild = new PacketBuild(Mirko.getNickname(),MirkoW1.getID(),builds,orderBuilds);

        try{
            BuildData buildData = model.packetBuildToBuildData(packetBuild);
            assert true;
        } catch (InvalidPacketException e) {
            assert false;
        }

    }

}