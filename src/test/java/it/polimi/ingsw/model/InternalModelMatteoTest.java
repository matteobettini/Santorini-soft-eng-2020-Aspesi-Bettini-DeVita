package it.polimi.ingsw.model;

import it.polimi.ingsw.model.cardReader.CardFactory;
import it.polimi.ingsw.model.cardReader.CardFile;
import it.polimi.ingsw.model.cardReader.exceptions.CardLoadingException;
import it.polimi.ingsw.model.cardReader.exceptions.InvalidCardException;
import it.polimi.ingsw.model.enums.BuildingType;
import it.polimi.ingsw.model.enums.LevelType;
import it.polimi.ingsw.model.enums.PlayerState;
import it.polimi.ingsw.model.exceptions.PlayerLostSignal;
import it.polimi.ingsw.model.exceptions.PlayerWonSignal;
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

public class InternalModelMatteoTest {

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

    /*
        Tests a complete set of actions in this order: first build, attempt to go up on the block built
        denied, move and lastly the normal buiild
     */
    @Test
    void promethus_Test1(){
        /*
                  0    1     2    3    4   X
                +----+----+----+----+----+
            0   |    |    |    |    |    |
                +----+----+----+----+----+
            1   |    |    |    |    |    |
                |    |    | FF |    |    |
                +----+----+----+----+----+
            2   |    |    | M1 |    |    |
                |    |    |    |    |    |
                +----+----+----+----+----+
            3   |    |    |    |    |    |
                +----+----+----+----+----+
            4   |    |    |    |    |    |
                +----+----+----+----+----+
            Y
        */

        // Initializing cards
        CardFile prometheus = cardFactory.getCards().stream().filter(x -> x.getName().equals("Prometheus")).findFirst().orElse(null);
        CardFile artemis = cardFactory.getCards().stream().filter(x -> x.getName().equals("Artemis")).findFirst().orElse(null);
        CardFile athena = cardFactory.getCards().stream().filter(x -> x.getName().equals("Athena")).findFirst().orElse(null);
        Matteo.setCard(prometheus);
        Andrea.setCard(artemis);
        Mirko.setCard(athena);
        model.compileCardStrategy();

        //Initializing positions
        Point point1 = new Point(2,2);
        Point point2 = new Point(2, 1);
        MatteoW1.setPosition(point1);
        model.getBoard().getCell(point1).setWorker(MatteoW1.getID());

        // FIRST BUILD
        Map<Point, List<BuildingType>> builds = new HashMap<>();
        List<Point> buildsOrder = new ArrayList<>();
        List<BuildingType> buildsInThisPoint = new ArrayList<>();

        buildsInThisPoint.add(BuildingType.FIRST_FLOOR);
        builds.put(point2,buildsInThisPoint);
        buildsOrder.add(point2);

        PacketBuild matteoFirstBuild = new PacketBuild("Matteo", "Matteo.1", builds, buildsOrder);
        BuildData buildData = null;
        try {
            buildData = model.packetBuildToBuildData(matteoFirstBuild);
        } catch (InvalidPacketException e) {
            assert false;
        }
        assertNotNull(buildData);
        try {
            assertTrue(model.makeBuild(buildData));
        } catch (PlayerWonSignal | PlayerLostSignal playerWonSignal) {
            assert false;
        }

        //CHECK THE BUILDING IS THERE

        assertSame(model.getBoard().getCell(point2).getTopBuilding(), LevelType.FIRST_FLOOR);
        assertEquals(point1, MatteoW1.getPosition());
        assertEquals(MatteoW1.getID(), model.getBoard().getCell(point1).getWorkerID());


        // TRYING TO MOVE UP ON THE BLOCK JUST PLACED
        List<Point> moves = new ArrayList<>();
        moves.add(point2);
        PacketMove packetMove = new PacketMove("Matteo", "Matteo.1", moves);
        MoveData moveData = null;
        try{
            moveData = model.packetMoveToMoveData(packetMove);
        } catch (InvalidPacketException e){
            assert false;
        }
        assertNotNull(moveData);
        try {
            assertFalse(model.makeMove(moveData));
        } catch (PlayerWonSignal | PlayerLostSignal playerWonSignal) {
            assert false;
        }

        assertEquals(PlayerState.FIRST_BUILT, Matteo.getState());
        assertSame(model.getBoard().getCell(point2).getTopBuilding(), LevelType.FIRST_FLOOR);
        assertEquals(point1, MatteoW1.getPosition());
        assertEquals(MatteoW1.getID(), model.getBoard().getCell(point1).getWorkerID());


        //TESTING THE POSSIBLE MOVES DON'T INCLUDE GOING UP
        /*
                  0    1     2    3    4   X
                +----+----+----+----+----+
            0   |    |    |    |    |    |
                +----+----+----+----+----+
            1   |    | A1 |    |    |    |
                |    |    | FF |    |    |
                +----+----+----+----+----+
            2   |    |    | M1 |    |    |
                |    | FF |    | FF |    |
                +----+----+----+----+----+
            3   |    |    | FF |    |    |
                +----+----+----+----+----+
            4   |    |    |    |    |    |
                +----+----+----+----+----+
            Y
        */
        model.getBoard().getCell(new Point(1,1)).setWorker(AndreaW1.getID());
        model.getBoard().getCell(new Point(1,2)).addBuilding(BuildingType.FIRST_FLOOR);

        model.getBoard().getCell(new Point(3,2)).addBuilding(BuildingType.FIRST_FLOOR);

        model.getBoard().getCell(new Point(2,3)).addBuilding(BuildingType.FIRST_FLOOR);

        Set<Point> realPossibleMoves = new HashSet<>();
        //ealPossibleMoves.add(new Point(1,1));
        realPossibleMoves.add(new Point(3,1));
        realPossibleMoves.add(new Point(1,3));
        realPossibleMoves.add(new Point(3,3));

        assertEquals(realPossibleMoves, model.getPossibleMoves(Matteo, MatteoW1));

        //NOW LET'S SIMULATE THE MOVE
        /*
                  0    1     2    3    4   X
                +----+----+----+----+----+
            0   |    |    |    |    |    |
                +----+----+----+----+----+
            1   |    | M1 |    |    |    |
                |    |    | FF |    |    |
                +----+----+----+----+----+
            2   |    |    |    |    |    |
                |    | FF |    | FF |    |
                +----+----+----+----+----+
            3   |    |    | FF |    |    |
                +----+----+----+----+----+
            4   |    |    |    |    |    |
                +----+----+----+----+----+
            Y
        */
        model.getBoard().getCell(new Point(1,1)).removeWorker();
        moves = new ArrayList<>();
        moves.add(new Point(1,1));
        packetMove = new PacketMove("Matteo", "Matteo.1", moves);
        moveData = null;
        try{
            moveData = model.packetMoveToMoveData(packetMove);
        } catch (InvalidPacketException e){
            assert false;
        }
        assertNotNull(moveData);
        try {
            assertTrue(model.makeMove(moveData));
        } catch (PlayerWonSignal | PlayerLostSignal playerWonSignal) {
            assert false;
        }

        assertEquals(PlayerState.MOVED, Matteo.getState());
        assertEquals(new Point(1,1), MatteoW1.getPosition());
        assertEquals(MatteoW1.getID(), model.getBoard().getCell(new Point(1,1)).getWorkerID());

        // AND NOW THE LAST BUILD
        /*
                  0    1     2    3    4   X
                +----+----+----+----+----+
            0   | FF |    |    |    |    |
                +----+----+----+----+----+
            1   |    | M1 |    |    |    |
                |    |    | FF |    |    |
                +----+----+----+----+----+
            2   |    |    |    |    |    |
                |    | FF |    | FF |    |
                +----+----+----+----+----+
            3   |    |    | FF |    |    |
                +----+----+----+----+----+
            4   |    |    |    |    |    |
                +----+----+----+----+----+
            Y
        */

        builds = new HashMap<>();
        buildsOrder = new ArrayList<>();
        buildsInThisPoint = new ArrayList<>();

        buildsInThisPoint.add(BuildingType.FIRST_FLOOR);
        builds.put(new Point(0,0),buildsInThisPoint);
        buildsOrder.add(new Point(0,0));

        PacketBuild matteoSecondBuild = new PacketBuild("Matteo", "Matteo.1", builds, buildsOrder);
        buildData = null;
        try {
            buildData = model.packetBuildToBuildData(matteoSecondBuild);
        } catch (InvalidPacketException e) {
            assert false;
        }
        assertNotNull(buildData);
        try {
            assertTrue(model.makeBuild(buildData));
        } catch (PlayerWonSignal | PlayerLostSignal playerWonSignal) {
            assert false;
        }

        //CHECK THE BUILDING IS THERE

        assertSame(model.getBoard().getCell(new Point(0,0)).getTopBuilding(), LevelType.FIRST_FLOOR);
        assertEquals(new Point(1,1), MatteoW1.getPosition());
        assertEquals(MatteoW1.getID(), model.getBoard().getCell(new Point(1,1)).getWorkerID());
        assertEquals(PlayerState.BUILT, Matteo.getState());

    }

    /*
       Tests a complete set of actions in this order: move and normal buiild
    */
    @Test
    void promethus_Test2(){
         /*
                  0    1     2    3    4   X
                +----+----+----+----+----+
            0   |    |    |    |    |    |
                +----+----+----+----+----+
            1   |    |    |    |    |    |
                |    |    |    |    |    |
                +----+----+----+----+----+
            2   |    |    |    |    |    |
                |    |    |    |    |    |
                +----+----+----+----+----+
            3   | FF | TF | A1 |    |    |
                |    |    | FF |    |    |
                +----+----+----+----+----+
            4   | SF | M1 | DM |    |    |
                +----+----+----+----+----+
            Y
        */

        //UTILITIES
        // Initializing cards
        CardFile prometheus = cardFactory.getCards().stream().filter(x -> x.getName().equals("Prometheus")).findFirst().orElse(null);
        CardFile artemis = cardFactory.getCards().stream().filter(x -> x.getName().equals("Artemis")).findFirst().orElse(null);
        CardFile athena = cardFactory.getCards().stream().filter(x -> x.getName().equals("Athena")).findFirst().orElse(null);
        Matteo.setCard(prometheus);
        Andrea.setCard(artemis);
        Mirko.setCard(athena);
        model.compileCardStrategy();


        //Initializing positions
        Point point04 = new Point(0,4);
        Point point03 = new Point(0, 3);
        Point point14 = new Point(1,4);
        Point point13 = new Point(1, 3);
        Point point24 = new Point(2,4);
        Point point23 = new Point(2, 3);
        Point point12 = new Point(1, 2);
        Point point02 = new Point(0, 2);



        // Build utils
        Map<Point, List<BuildingType>> builds = new HashMap<>();
        List<Point> buildsOrder = new ArrayList<>();
        List<BuildingType> buildsInThisPoint = new ArrayList<>();
        PacketBuild packetBuild;
        BuildData buildData;

        //Move utils
        List<Point> moves = new ArrayList<>();
        Set<Point> realPossibleMoves = new HashSet<>();
        PacketMove packetMove;
        MoveData moveData;

        //Setup the map
        model.getBoard().getCell(point14).setWorker(MatteoW1.getID());
        MatteoW1.setPosition(point14);

        model.getBoard().getCell(point04).addBuilding(BuildingType.FIRST_FLOOR);
        model.getBoard().getCell(point04).addBuilding(BuildingType.SECOND_FLOOR);

        model.getBoard().getCell(point03).addBuilding(BuildingType.FIRST_FLOOR);

        model.getBoard().getCell(point13).addBuilding(BuildingType.FIRST_FLOOR);
        model.getBoard().getCell(point13).addBuilding(BuildingType.SECOND_FLOOR);
        model.getBoard().getCell(point13).addBuilding(BuildingType.THIRD_FLOOR);

        model.getBoard().getCell(point23).addBuilding(BuildingType.FIRST_FLOOR);
        model.getBoard().getCell(point23).setWorker(AndreaW1.getID());
        AndreaW1.setPosition(point23);

        model.getBoard().getCell(point24).addBuilding(BuildingType.DOME);


        realPossibleMoves.add(point03);
        assertEquals(realPossibleMoves, model.getPossibleMoves(Matteo, MatteoW1));

        //NOW I MOVE THERE
        /*
                  0    1     2    3    4   X
                +----+----+----+----+----+
            0   |    |    |    |    |    |
                +----+----+----+----+----+
            1   |    |    |    |    |    |
                |    |    |    |    |    |
                +----+----+----+----+----+
            2   |    |    |    |    |    |
                |    |    |    |    |    |
                +----+----+----+----+----+
            3   | FF | TF | A1 |    |    |
                | M1 |    | FF |    |    |
                +----+----+----+----+----+
            4   | SF |    | DM |    |    |
                +----+----+----+----+----+
            Y
        */
        moves.clear();
        moves.add(point03);
        packetMove = new PacketMove("Matteo", "Matteo.1", moves);
        moveData = null;
        try{
            moveData = model.packetMoveToMoveData(packetMove);
        } catch (InvalidPacketException e){
            assert false;
        }
        assertNotNull(moveData);
        try {
            assertTrue(model.makeMove(moveData));
        } catch (PlayerWonSignal | PlayerLostSignal playerWonSignal) {
            assert false;
        }
        assertEquals(PlayerState.MOVED, Matteo.getState());
        assertEquals(point03, MatteoW1.getPosition());
        assertEquals(MatteoW1.getID(), model.getBoard().getCell(point03).getWorkerID());

        //NOW LET'S TRY AN IMPOSSIBLE BUILD
        buildsInThisPoint.add(BuildingType.DOME);
        builds.put(point04,buildsInThisPoint);
        buildsOrder.add(point04);

        packetBuild = new PacketBuild("Matteo", "Matteo.1", builds, buildsOrder);
        buildData = null;
        try {
            buildData = model.packetBuildToBuildData(packetBuild);
        } catch (InvalidPacketException e) {
            assert false;
        }
        assertNotNull(buildData);
        try {
            assertFalse(model.makeBuild(buildData));
        } catch (PlayerWonSignal | PlayerLostSignal playerWonSignal) {
            assert false;
        }

        //NOW LET'S SEE IF THE POSSIBLE BUILDS MATCH
        Set<Point> realPossibleBuilds = new HashSet<>();
        realPossibleBuilds.add(point04);
        realPossibleBuilds.add(point14);
        realPossibleBuilds.add(point13);
        realPossibleBuilds.add(point12);
        realPossibleBuilds.add(point02);

        assertEquals(realPossibleBuilds, model.getPossibleBuilds(Matteo,MatteoW1));

        //NOW LET'S MAKE A VALID BUILD
           /*
                  0    1     2    3    4   X
                +----+----+----+----+----+
            0   |    |    |    |    |    |
                +----+----+----+----+----+
            1   |    |    |    |    |    |
                |    |    |    |    |    |
                +----+----+----+----+----+
            2   |    |    |    |    |    |
                |    |    |    |    |    |
                +----+----+----+----+----+
            3   | FF | DM | A1 |    |    |
                | M1 |    | FF |    |    |
                +----+----+----+----+----+
            4   | SF |    | DM |    |    |
                +----+----+----+----+----+
            Y
        */
        buildsInThisPoint.clear();
        builds.clear();
        buildsOrder.clear();
        buildsInThisPoint.add(BuildingType.DOME);
        builds.put(point13,buildsInThisPoint);
        buildsOrder.add(point13);

        packetBuild = new PacketBuild("Matteo", "Matteo.1", builds, buildsOrder);
        buildData = null;
        try {
            buildData = model.packetBuildToBuildData(packetBuild);
        } catch (InvalidPacketException e) {
            assert false;
        }
        assertNotNull(buildData);
        try {
            assertTrue(model.makeBuild(buildData));
        } catch (PlayerWonSignal | PlayerLostSignal playerWonSignal) {
            assert false;
        }

        assertSame(model.getBoard().getCell(point13).getTopBuilding(), LevelType.DOME);
        assertEquals(point03, MatteoW1.getPosition());
        assertEquals(MatteoW1.getID(), model.getBoard().getCell(point03).getWorkerID());
        assertEquals(PlayerState.BUILT, Matteo.getState());
    }

    /*
        Prometheus tries to win building before moving and then moving on the block
        just built on the first floor, but gets denied
     */
    @Test
    void prometheus_Test3(){
        /*
                  0    1     2    3    4   X
                +----+----+----+----+----+
            0   |    |    |    |    |    |
                +----+----+----+----+----+
            1   |    |    |    |    |    |
                |    |    |    |    |    |
                +----+----+----+----+----+
            2   |    |    |    |    |    |
                |    |    |    |    |    |
                +----+----+----+----+----+
            3   |    |    |    |    |    |
                |    |    |    |    |    |
                +----+----+----+----+----+
            4   | SF | M1 |    |    |    |
                |    | SF |    |    |    |
                +----+----+----+----+----+
            Y
        */

        //UTILITIES
        // Initializing cards
        CardFile prometheus = cardFactory.getCards().stream().filter(x -> x.getName().equals("Prometheus")).findFirst().orElse(null);
        CardFile artemis = cardFactory.getCards().stream().filter(x -> x.getName().equals("Artemis")).findFirst().orElse(null);
        CardFile athena = cardFactory.getCards().stream().filter(x -> x.getName().equals("Athena")).findFirst().orElse(null);
        Matteo.setCard(prometheus);
        Andrea.setCard(artemis);
        Mirko.setCard(athena);
        model.compileCardStrategy();


        //Initializing positions
        Point point04 = new Point(0,4);
        Point point03 = new Point(0, 3);
        Point point14 = new Point(1,4);
        Point point13 = new Point(1, 3);
        Point point24 = new Point(2,4);
        Point point23 = new Point(2, 3);
        Point point12 = new Point(1, 2);
        Point point02 = new Point(0, 2);

        // Build utils
        Set<Point> realPossibleBuilds = new HashSet<>();
        Map<Point, List<BuildingType>> builds = new HashMap<>();
        List<Point> buildsOrder = new ArrayList<>();
        List<BuildingType> buildsInThisPoint = new ArrayList<>();
        PacketBuild packetBuild;
        BuildData buildData;

        //Move utils
        List<Point> moves = new ArrayList<>();
        Set<Point> realPossibleMoves = new HashSet<>();
        PacketMove packetMove;
        MoveData moveData;

        //Setup the map
        model.getBoard().getCell(point04).addBuilding(BuildingType.FIRST_FLOOR);
        model.getBoard().getCell(point04).addBuilding(BuildingType.SECOND_FLOOR);

        model.getBoard().getCell(point14).addBuilding(BuildingType.FIRST_FLOOR);
        model.getBoard().getCell(point14).addBuilding(BuildingType.SECOND_FLOOR);


        model.getBoard().getCell(point14).setWorker(MatteoW1.getID());
        MatteoW1.setPosition(point14);

        //NOW LET'S MAKE THE FIRST  BUILD
         /*
                  0    1     2    3    4   X
                +----+----+----+----+----+
            0   |    |    |    |    |    |
                +----+----+----+----+----+
            1   |    |    |    |    |    |
                |    |    |    |    |    |
                +----+----+----+----+----+
            2   |    |    |    |    |    |
                |    |    |    |    |    |
                +----+----+----+----+----+
            3   |    |    |    |    |    |
                |    |    |    |    |    |
                +----+----+----+----+----+
            4   | TF | M1 |    |    |    |
                |    | SF |    |    |    |
                +----+----+----+----+----+
            Y
        */
        buildsInThisPoint.clear();
        builds.clear();
        buildsOrder.clear();
        buildsInThisPoint.add(BuildingType.THIRD_FLOOR);
        builds.put(point04,buildsInThisPoint);
        buildsOrder.add(point04);

        packetBuild = new PacketBuild("Matteo", "Matteo.1", builds, buildsOrder);
        buildData = null;
        try {
            buildData = model.packetBuildToBuildData(packetBuild);
        } catch (InvalidPacketException e) {
            assert false;
        }
        assertNotNull(buildData);
        try {
            assertTrue(model.makeBuild(buildData));
        } catch (PlayerWonSignal | PlayerLostSignal playerWonSignal) {
            assert false;
        }

        assertSame(model.getBoard().getCell(point04).getTopBuilding(), LevelType.THIRD_FLOOR);
        assertEquals(PlayerState.FIRST_BUILT, Matteo.getState());

        //NOW TRY TO WIN BY MOVING UP
        moves.clear();
        moves.add(point04);
        packetMove = new PacketMove("Matteo", "Matteo.1", moves);
        moveData = null;
        try{
            moveData = model.packetMoveToMoveData(packetMove);
        } catch (InvalidPacketException e){
            assert false;
        }
        assertNotNull(moveData);
        try {
            assertFalse(model.makeMove(moveData));
        } catch (PlayerWonSignal e) {
            assert false;
        } catch (PlayerLostSignal e){
            assert false;
        }
        assertEquals(PlayerState.FIRST_BUILT, Matteo.getState());
        assertEquals(point14, MatteoW1.getPosition());
        assertEquals(MatteoW1.getID(), model.getBoard().getCell(point14).getWorkerID());

        //NOW LET'S PLACE HIM ON THE THIRD FLOOR
        model.getBoard().getCell(point14).removeWorker();
        model.getBoard().getCell(point14).addBuilding(BuildingType.THIRD_FLOOR);
        model.getBoard().getCell(point14).setWorker(MatteoW1.getID());

        //NOW LET'S MOVE FROM A THIRD FLOOR TO ANOTHER
 /*
                  0    1     2    3    4   X
                +----+----+----+----+----+
            0   |    |    |    |    |    |
                +----+----+----+----+----+
            1   |    |    |    |    |    |
                |    |    |    |    |    |
                +----+----+----+----+----+
            2   |    |    |    |    |    |
                |    |    |    |    |    |
                +----+----+----+----+----+
            3   |    |    |    |    |    |
                |    |    |    |    |    |
                +----+----+----+----+----+
            4   | TF |    |    |    |    |
                | M1 | TF |    |    |    |
                +----+----+----+----+----+
            Y
        */
        moves.clear();
        moves.add(point04);
        packetMove = new PacketMove("Matteo", "Matteo.1", moves);
        moveData = null;
        try{
            moveData = model.packetMoveToMoveData(packetMove);
        } catch (InvalidPacketException e){
            assert false;
        }
        assertNotNull(moveData);
        try {
            assertTrue(model.makeMove(moveData));
        } catch (PlayerWonSignal e) {
            assert false;
        } catch (PlayerLostSignal e){
            assert false;
        }
        assertEquals(PlayerState.MOVED, Matteo.getState());
        assertEquals(point04, MatteoW1.getPosition());
        assertEquals(MatteoW1.getID(), model.getBoard().getCell(point04).getWorkerID());

        //AND NOW LET'S CHECK POSSIBLE BUILDS AND LET'S BUILD A DOME WHERE WE CAME FROM

        realPossibleBuilds.add(point03);
        realPossibleBuilds.add(point13);
        realPossibleBuilds.add(point14);
        assertEquals(realPossibleBuilds, model.getPossibleBuilds(Matteo, MatteoW1));

        buildsInThisPoint.clear();
        builds.clear();
        buildsOrder.clear();
        buildsInThisPoint.add(BuildingType.DOME);
        builds.put(point14,buildsInThisPoint);
        buildsOrder.add(point14);

        packetBuild = new PacketBuild("Matteo", "Matteo.1", builds, buildsOrder);
        buildData = null;
        try {
            buildData = model.packetBuildToBuildData(packetBuild);
        } catch (InvalidPacketException e) {
            assert false;
        }
        assertNotNull(buildData);
        try {
            assertTrue(model.makeBuild(buildData));
        } catch (PlayerWonSignal | PlayerLostSignal playerWonSignal) {
            assert false;
        }

        assertSame(model.getBoard().getCell(point14).getTopBuilding(), LevelType.DOME);
        assertEquals(PlayerState.BUILT, Matteo.getState());

    }

    /*
        Test that pan wins by going from a third floor to a first or to a ground
        and does not win when going to a second floor or to a third
     */
    @Test
    void pan_Test1(){
        /*
                  0    1     2    3    4   X
                +----+----+----+----+----+
            0   |    |    |    |    |    |
                +----+----+----+----+----+
            1   |    |    |    |    |    |
                |    |    |    |    |    |
                +----+----+----+----+----+
            2   |    |    |    |    |    |
                |    |    |    |    |    |
                +----+----+----+----+----+
            3   |    | SF |    |    |    |
                |    |    |    |    |    |
                +----+----+----+----+----+
            4   | FF | M1 | TF |    |    |
                |    | TF |    |    |    |
                +----+----+----+----+----+
            Y
        */

        //UTILITIES
        // Initializing cards
        CardFile pan = cardFactory.getCards().stream().filter(x -> x.getName().equals("Pan")).findFirst().orElse(null);
        CardFile artemis = cardFactory.getCards().stream().filter(x -> x.getName().equals("Artemis")).findFirst().orElse(null);
        CardFile athena = cardFactory.getCards().stream().filter(x -> x.getName().equals("Athena")).findFirst().orElse(null);
        Matteo.setCard(pan);
        Andrea.setCard(artemis);
        Mirko.setCard(athena);
        model.compileCardStrategy();


        //Initializing positions
        Point point04 = new Point(0,4);
        Point point03 = new Point(0, 3);
        Point point14 = new Point(1,4);
        Point point13 = new Point(1, 3);
        Point point24 = new Point(2,4);
        Point point23 = new Point(2, 3);
        Point point12 = new Point(1, 2);
        Point point02 = new Point(0, 2);

        // Build utils
        Set<Point> realPossibleBuilds = new HashSet<>();
        Map<Point, List<BuildingType>> builds = new HashMap<>();
        List<Point> buildsOrder = new ArrayList<>();
        List<BuildingType> buildsInThisPoint = new ArrayList<>();
        PacketBuild packetBuild;
        BuildData buildData;

        //Move utils
        List<Point> moves = new ArrayList<>();
        Set<Point> realPossibleMoves = new HashSet<>();
        PacketMove packetMove;
        MoveData moveData;

        //Setup the map
        model.getBoard().getCell(point04).addBuilding(BuildingType.FIRST_FLOOR);

        model.getBoard().getCell(point14).addBuilding(BuildingType.FIRST_FLOOR);
        model.getBoard().getCell(point14).addBuilding(BuildingType.SECOND_FLOOR);
        model.getBoard().getCell(point14).addBuilding(BuildingType.THIRD_FLOOR);

        model.getBoard().getCell(point13).addBuilding(BuildingType.FIRST_FLOOR);
        model.getBoard().getCell(point13).addBuilding(BuildingType.SECOND_FLOOR);

        model.getBoard().getCell(point24).addBuilding(BuildingType.FIRST_FLOOR);
        model.getBoard().getCell(point24).addBuilding(BuildingType.SECOND_FLOOR);
        model.getBoard().getCell(point24).addBuilding(BuildingType.THIRD_FLOOR);

        model.getBoard().getCell(point14).setWorker(MatteoW1.getID());
        MatteoW1.setPosition(point14);

        //WINNING BY STEPPING DOWN TO A FIRST FLOOR
         /*
                  0    1     2    3    4   X
                +----+----+----+----+----+
            0   |    |    |    |    |    |
                +----+----+----+----+----+
            1   |    |    |    |    |    |
                |    |    |    |    |    |
                +----+----+----+----+----+
            2   |    |    |    |    |    |
                |    |    |    |    |    |
                +----+----+----+----+----+
            3   |    | SF |    |    |    |
                |    |    |    |    |    |
                +----+----+----+----+----+
            4   | FF | M1 |    |    |    |
                |    | TF |    |    |    |
                +----+----+----+----+----+
            Y
        */
        moves.clear();
        moves.add(point04);
        packetMove = new PacketMove("Matteo", "Matteo.1", moves);
        moveData = null;
        try{
            moveData = model.packetMoveToMoveData(packetMove);
        } catch (InvalidPacketException e){
            assert false;
        }
        assertNotNull(moveData);
        try {
            assertTrue(model.makeMove(moveData));
            assert false;
        } catch (PlayerWonSignal e) {
            assert true;
        } catch (PlayerLostSignal e){
            assert false;
        }
        assertEquals(PlayerState.MOVED, Matteo.getState());
        assertEquals(point04, MatteoW1.getPosition());
        assertEquals(MatteoW1.getID(), model.getBoard().getCell(point04).getWorkerID());

        //reset
        Matteo.setPlayerState(PlayerState.TURN_STARTED);
        MatteoW1.setPosition(point14);
        model.getBoard().getCell(point04).removeWorker();
        model.getBoard().getCell(point14).setWorker(MatteoW1.getID());

        //WINNING BY STEPPING DOWN TO A GROUND FLOOR
         /*
                  0    1     2    3    4   X
                +----+----+----+----+----+
            0   |    |    |    |    |    |
                +----+----+----+----+----+
            1   |    |    |    |    |    |
                |    |    |    |    |    |
                +----+----+----+----+----+
            2   |    |    |    |    |    |
                |    |    |    |    |    |
                +----+----+----+----+----+
            3   |    | SF |    |    |    |
                |    |    |    |    |    |
                +----+----+----+----+----+
            4   | FF | M1 |    |    |    |
                |    | TF |    |    |    |
                +----+----+----+----+----+
            Y
        */
        moves.clear();
        moves.add(point03);
        packetMove = new PacketMove("Matteo", "Matteo.1", moves);
        moveData = null;
        try{
            moveData = model.packetMoveToMoveData(packetMove);
        } catch (InvalidPacketException e){
            assert false;
        }
        assertNotNull(moveData);
        try {
            assertTrue(model.makeMove(moveData));
            assert false;
        } catch (PlayerWonSignal e) {
            assert true;
        } catch (PlayerLostSignal e){
            assert false;
        }

        assertEquals(PlayerState.MOVED, Matteo.getState());
        assertEquals(point03, MatteoW1.getPosition());
        assertEquals(MatteoW1.getID(), model.getBoard().getCell(point03).getWorkerID());

        //reset
        Matteo.setPlayerState(PlayerState.TURN_STARTED);
        MatteoW1.setPosition(point14);
        model.getBoard().getCell(point03).removeWorker();
        model.getBoard().getCell(point14).setWorker(MatteoW1.getID());

        // NOT WINNING BY STEPPING DOWN TO A SECOND FLOOR
         /*
                  0    1     2    3    4   X
                +----+----+----+----+----+
            0   |    |    |    |    |    |
                +----+----+----+----+----+
            1   |    |    |    |    |    |
                |    |    |    |    |    |
                +----+----+----+----+----+
            2   |    |    |    |    |    |
                |    |    |    |    |    |
                +----+----+----+----+----+
            3   |    | SF |    |    |    |
                |    |    |    |    |    |
                +----+----+----+----+----+
            4   | FF | M1 |    |    |    |
                |    | TF |    |    |    |
                +----+----+----+----+----+
            Y
        */
        moves.clear();
        moves.add(point13);
        packetMove = new PacketMove("Matteo", "Matteo.1", moves);
        moveData = null;
        try{
            moveData = model.packetMoveToMoveData(packetMove);
        } catch (InvalidPacketException e){
            assert false;
        }
        assertNotNull(moveData);
        try {
            assertTrue(model.makeMove(moveData));
        } catch (PlayerWonSignal | PlayerLostSignal e) {
            assert false;
        }
        assertEquals(PlayerState.MOVED, Matteo.getState());
        assertEquals(point13, MatteoW1.getPosition());
        assertEquals(MatteoW1.getID(), model.getBoard().getCell(point13).getWorkerID());

        //reset
        Matteo.setPlayerState(PlayerState.TURN_STARTED);
        MatteoW1.setPosition(point14);
        model.getBoard().getCell(point13).removeWorker();
        model.getBoard().getCell(point14).setWorker(MatteoW1.getID());

        // NOT WINNING BY STEPPING TO A THIRD FLOOR
         /*
                  0    1     2    3    4   X
                +----+----+----+----+----+
            0   |    |    |    |    |    |
                +----+----+----+----+----+
            1   |    |    |    |    |    |
                |    |    |    |    |    |
                +----+----+----+----+----+
            2   |    |    |    |    |    |
                |    |    |    |    |    |
                +----+----+----+----+----+
            3   |    | SF |    |    |    |
                |    |    |    |    |    |
                +----+----+----+----+----+
            4   | FF | M1 |    |    |    |
                |    | TF |    |    |    |
                +----+----+----+----+----+
            Y
        */
        moves.clear();
        moves.add(point24);
        packetMove = new PacketMove("Matteo", "Matteo.1", moves);
        moveData = null;
        try{
            moveData = model.packetMoveToMoveData(packetMove);
        } catch (InvalidPacketException e){
            assert false;
        }
        assertNotNull(moveData);
        try {
            assertTrue(model.makeMove(moveData));
        } catch (PlayerWonSignal | PlayerLostSignal e) {
            assert false;
        }
        assertEquals(PlayerState.MOVED, Matteo.getState());
        assertEquals(point24, MatteoW1.getPosition());
        assertEquals(MatteoW1.getID(), model.getBoard().getCell(point24).getWorkerID());


    }

    /*
        A full turn by all of the players in which at the end pan can
        win in two ways
     */
    @Test
    void pan_hephaestus_artemis_simulatedTurns(){
         /*
                  0    1     2    3    4   X
                +----+----+----+----+----+
            0   |    |    |    |    |    |
                +----+----+----+----+----+
            1   |    |    |    |    |    |
                |    |    |    |    |    |
                +----+----+----+----+----+
            2   |    | M1 |    |    |    |
                |    |    |    |    |    |
                +----+----+----+----+----+
            3   | D1 |    |    |    |    |
                | SF |    |    |    |    |
                +----+----+----+----+----+
            4   |    |    |    | A1 |    |
                |    |    |    |    |    |
                +----+----+----+----+----+
            Y
        */
        //UTILITIES
        // Initializing cards
        CardFile hephaestus = cardFactory.getCards().stream().filter(x -> x.getName().equals("Hephaestus")).findFirst().orElse(null);
        CardFile artemis = cardFactory.getCards().stream().filter(x -> x.getName().equals("Artemis")).findFirst().orElse(null);
        CardFile pan = cardFactory.getCards().stream().filter(x -> x.getName().equals("Pan")).findFirst().orElse(null);
        Matteo.setCard(hephaestus);
        Andrea.setCard(artemis);
        Mirko.setCard(pan);
        model.compileCardStrategy();

        //Initializing positions
        Point point04 = new Point(0,4);
        Point point03 = new Point(0, 3);
        Point point14 = new Point(1,4);
        Point point13 = new Point(1, 3);
        Point point24 = new Point(2,4);
        Point point23 = new Point(2, 3);
        Point point12 = new Point(1, 2);
        Point point02 = new Point(0, 2);
        Point point34 = new Point(3, 4);
        Point point33 = new Point(3, 3);
        Point point43 = new Point(4, 3);
        Point point44 = new Point(4, 4);
        Point point22 = new Point(2, 2);
        Point point32 = new Point(3, 2);
        Point point42 = new Point(4, 2);
        Point point01 = new Point(0, 1);
        Point point11 = new Point(1, 1);
        Point point21 = new Point(2, 1);

        // Build utils
        Set<Point> realPossibleBuilds = new HashSet<>();
        Map<Point, List<BuildingType>> builds = new HashMap<>();
        List<Point> buildsOrder = new ArrayList<>();
        List<BuildingType> buildsInThisPoint = new ArrayList<>();
        PacketBuild packetBuild;
        BuildData buildData;

        //Move utils
        List<Point> moves = new ArrayList<>();
        Set<Point> realPossibleMoves = new HashSet<>();
        PacketMove packetMove;
        MoveData moveData;

        //Setup the map
        model.getBoard().getCell(point03).addBuilding(BuildingType.FIRST_FLOOR);
        model.getBoard().getCell(point03).addBuilding(BuildingType.SECOND_FLOOR);

        model.getBoard().getCell(point03).setWorker(MirkoW1.getID());
        MirkoW1.setPosition(point03);

        model.getBoard().getCell(point12).setWorker(MatteoW1.getID());
        MatteoW1.setPosition(point12);

        model.getBoard().getCell(point34).setWorker(AndreaW1.getID());
        AndreaW1.setPosition(point34);

        //FIRST MOVE OF ANDRE AS ARTEMIS
        /*
                  0    1     2    3    4   X
                +----+----+----+----+----+
            0   |    |    |    |    |    |
                +----+----+----+----+----+
            1   |    |    |    |    |    |
                |    |    |    |    |    |
                +----+----+----+----+----+
            2   |    | M1 | A1 |    |    |
                |    |    |    |    |    |
                +----+----+----+----+----+
            3   | D1 |    |    |    |    |
                | SF |    |    |    |    |
                +----+----+----+----+----+
            4   |    |    |    |    |    |
                |    |    |    |    |    |
                +----+----+----+----+----+
                  0    1     2    3    4
            Y
        */
        moves.clear();
        moves.add(point23);
        moves.add(point22);
        packetMove = new PacketMove("Andrea", "Andrea.1", moves);
        moveData = null;
        try{
            moveData = model.packetMoveToMoveData(packetMove);
        } catch (InvalidPacketException e){
            assert false;
        }
        assertNotNull(moveData);
        try {
            assertTrue(model.makeMove(moveData));
        } catch (PlayerWonSignal e) {
            assert false;
        } catch (PlayerLostSignal e){
            assert false;
        }
        assertEquals(PlayerState.MOVED, Andrea.getState());
        assertEquals(point22, AndreaW1.getPosition());
        assertEquals(AndreaW1.getID(), model.getBoard().getCell(point22).getWorkerID());

        //NOW A BUILD OF ANDRE AS ARTEMIS
         /*
                  0    1     2    3    4   X
                +----+----+----+----+----+
            0   |    |    |    |    |    |
                +----+----+----+----+----+
            1   |    |    |    |    |    |
                |    |    |    |    |    |
                +----+----+----+----+----+
            2   |    | M1 | A1 |    |    |
                |    |    |    |    |    |
                +----+----+----+----+----+
            3   | D1 | FF |    |    |    |
                | SF |    |    |    |    |
                +----+----+----+----+----+
            4   |    |    |    |    |    |
                |    |    |    |    |    |
                +----+----+----+----+----+
                  0    1     2    3    4
            Y
        */

        buildsInThisPoint.clear();
        builds.clear();
        buildsOrder.clear();
        buildsInThisPoint.add(BuildingType.FIRST_FLOOR);
        builds.put(point13,buildsInThisPoint);
        buildsOrder.add(point13);

        packetBuild = new PacketBuild("Andrea", "Andrea.1", builds, buildsOrder);
        buildData = null;
        try {
            buildData = model.packetBuildToBuildData(packetBuild);
        } catch (InvalidPacketException e) {
            assert false;
        }
        assertNotNull(buildData);
        try {
            assertTrue(model.makeBuild(buildData));
        } catch (PlayerWonSignal | PlayerLostSignal playerWonSignal) {
            assert false;
        }

        assertSame(model.getBoard().getCell(point13).getTopBuilding(), LevelType.FIRST_FLOOR);
        assertEquals(PlayerState.BUILT, Andrea.getState());

       //NOW IT'S MATTEO'S TURN TO MOVE
          /*
                  0    1     2    3    4   X
                +----+----+----+----+----+
            0   |    |    |    |    |    |
                +----+----+----+----+----+
            1   |    |    |    |    |    |
                |    |    |    |    |    |
                +----+----+----+----+----+
            2   | M1 |    | A1 |    |    |
                |    |    |    |    |    |
                +----+----+----+----+----+
            3   | D1 | FF |    |    |    |
                | SF |    |    |    |    |
                +----+----+----+----+----+
            4   |    |    |    |    |    |
                |    |    |    |    |    |
                +----+----+----+----+----+
                  0    1     2    3    4
            Y
        */
        realPossibleMoves.clear();
        realPossibleMoves.add(point02);
        realPossibleMoves.add(point01);
        realPossibleMoves.add(point11);
        realPossibleMoves.add(point21);
        realPossibleMoves.add(point23);
        realPossibleMoves.add(point13);
        assertEquals(realPossibleMoves, model.getPossibleMoves(Matteo, MatteoW1));
        moves.clear();
        moves.add(point02);
        packetMove = new PacketMove("Matteo", "Matteo.1", moves);
        moveData = null;
        try{
            moveData = model.packetMoveToMoveData(packetMove);
        } catch (InvalidPacketException e){
            assert false;
        }
        assertNotNull(moveData);
        try {
            assertTrue(model.makeMove(moveData));
        } catch (PlayerWonSignal | PlayerLostSignal e) {
            assert false;
        }
        assertEquals(PlayerState.MOVED, Matteo.getState());
        assertEquals(point02, MatteoW1.getPosition());
        assertEquals(MatteoW1.getID(), model.getBoard().getCell(point02).getWorkerID());

        //NOW IT'S MATTEO'S TURN TO MOVE
          /*
                  0    1     2    3    4   X
                +----+----+----+----+----+
            0   |    |    |    |    |    |
                +----+----+----+----+----+
            1   |    |    |    |    |    |
                |    |    |    |    |    |
                +----+----+----+----+----+
            2   | M1 |    | A1 |    |    |
                |    |    |    |    |    |
                +----+----+----+----+----+
            3   | D1 | TF |    |    |    |
                | SF |    |    |    |    |
                +----+----+----+----+----+
            4   |    |    |    |    |    |
                |    |    |    |    |    |
                +----+----+----+----+----+
                  0    1     2    3    4
            Y
        */

        buildsInThisPoint.clear();
        builds.clear();
        buildsOrder.clear();
        buildsInThisPoint.add(BuildingType.SECOND_FLOOR);
        buildsInThisPoint.add(BuildingType.THIRD_FLOOR);
        builds.put(point13,buildsInThisPoint);
        buildsOrder.add(point13);

        packetBuild = new PacketBuild("Matteo", "Matteo.1", builds, buildsOrder);
        buildData = null;
        try {
            buildData = model.packetBuildToBuildData(packetBuild);
        } catch (InvalidPacketException e) {
            assert false;
        }
        assertNotNull(buildData);
        try {
            assertTrue(model.makeBuild(buildData));
        } catch (PlayerWonSignal | PlayerLostSignal playerWonSignal) {
            assert false;
        }

        assertSame(model.getBoard().getCell(point13).getTopBuilding(), LevelType.THIRD_FLOOR);
        assertEquals(PlayerState.BUILT, Matteo.getState());

        //NOW IT'S MIRKOS MOVE, HE CAN WIN BOTH BY STEPPING DOWN AND BY GOING UP, LET'S TEST BOTH!

        //STEPPING DOWN
         /*
                  0    1     2    3    4   X
                +----+----+----+----+----+
            0   |    |    |    |    |    |
                +----+----+----+----+----+
            1   |    |    |    |    |    |
                |    |    |    |    |    |
                +----+----+----+----+----+
            2   | M1 | D1 | A1 |    |    |
                |    |    |    |    |    |
                +----+----+----+----+----+
            3   |    | TF |    |    |    |
                | SF |    |    |    |    |
                +----+----+----+----+----+
            4   |    |    |    |    |    |
                |    |    |    |    |    |
                +----+----+----+----+----+
                  0    1     2    3    4
            Y
        */
        moves.clear();
        moves.add(point12);
        packetMove = new PacketMove("Mirko", "Mirko.1", moves);
        moveData = null;
        try{
            moveData = model.packetMoveToMoveData(packetMove);
        } catch (InvalidPacketException e){
            assert false;
        }
        assertNotNull(moveData);
        try {
            assertTrue(model.makeMove(moveData));
            assert false;
        } catch (PlayerWonSignal e) {
            assert true;
        } catch (PlayerLostSignal e){
            assert false;
        }
        assertEquals(PlayerState.MOVED, Mirko.getState());
        assertEquals(point12, MirkoW1.getPosition());
        assertEquals(MirkoW1.getID(), model.getBoard().getCell(point12).getWorkerID());

        //reset
        Mirko.setPlayerState(PlayerState.TURN_STARTED);
        MirkoW1.setPosition(point03);
        model.getBoard().getCell(point12).removeWorker();
        model.getBoard().getCell(point03).setWorker(MirkoW1.getID());

        //STEPPING UP
         /*
                  0    1     2    3    4   X
                +----+----+----+----+----+
            0   |    |    |    |    |    |
                +----+----+----+----+----+
            1   |    |    |    |    |    |
                |    |    |    |    |    |
                +----+----+----+----+----+
            2   | M1 |    | A1 |    |    |
                |    |    |    |    |    |
                +----+----+----+----+----+
            3   |    | TF |    |    |    |
                | SF | D1 |    |    |    |
                +----+----+----+----+----+
            4   |    |    |    |    |    |
                |    |    |    |    |    |
                +----+----+----+----+----+
                  0    1     2    3    4
            Y
        */

        moves.clear();
        moves.add(point13);
        packetMove = new PacketMove("Mirko", "Mirko.1", moves);
        moveData = null;
        try{
            moveData = model.packetMoveToMoveData(packetMove);
        } catch (InvalidPacketException e){
            assert false;
        }
        assertNotNull(moveData);
        try {
            assertTrue(model.makeMove(moveData));
            assert false;
        } catch (PlayerWonSignal e) {
            assert true;
        } catch (PlayerLostSignal e){
            assert false;
        }
        assertEquals(PlayerState.MOVED, Mirko.getState());
        assertEquals(point13, MirkoW1.getPosition());
        assertEquals(MirkoW1.getID(), model.getBoard().getCell(point13).getWorkerID());


    }



}
