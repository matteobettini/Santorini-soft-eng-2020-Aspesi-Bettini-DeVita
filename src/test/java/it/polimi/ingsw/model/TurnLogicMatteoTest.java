package it.polimi.ingsw.model;

import it.polimi.ingsw.cards.CardFactory;
import it.polimi.ingsw.cards.CardFile;
import it.polimi.ingsw.cards.exceptions.CardLoadingException;
import it.polimi.ingsw.cards.exceptions.InvalidCardException;
import it.polimi.ingsw.model.enums.ActionType;
import it.polimi.ingsw.model.enums.BuildingType;
import it.polimi.ingsw.packets.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.*;
import java.util.List;


import static org.junit.jupiter.api.Assertions.*;

class TurnLogicMatteoTest {

    static class Client {
        private final Queue<PacketPossibleBuilds> packetPossibleBuilds;
        private final Queue<PacketPossibleMoves> packetPossibleMoves;
        private final Queue<PacketDoAction> packetDoAction;
        private final Queue<PacketUpdateBoard> packetUpdateBoard;

        public Client(TurnLogic turnLogic){
            packetDoAction = new LinkedList<>();
            packetPossibleBuilds = new LinkedList<>();
            packetPossibleMoves = new LinkedList<>();
            packetUpdateBoard = new LinkedList<>();

            turnLogic.addPacketDoActionObserver(this.packetDoAction::add);
            turnLogic.addPacketPossibleBuildsObserver(this.packetPossibleBuilds::add);
            turnLogic.addPacketPossibleMovesObserver(this.packetPossibleMoves::add);
            turnLogic.addPacketUpdateBoardObserver(this.packetUpdateBoard::add);

        }

        public PacketPossibleBuilds getPacketPossibleBuilds() {
            return packetPossibleBuilds.poll();
        }
        public PacketPossibleMoves getPacketPossibleMoves() {
            return packetPossibleMoves.poll();
        }
        public PacketDoAction getPacketDoAction() {
            return packetDoAction.poll();
        }
        public PacketUpdateBoard getPacketUpdateBoard() {
            return packetUpdateBoard.poll();
        }
    }


    private static CardFactory cardFactory;
    private InternalModel model;
    private TurnLogic turnLogic;
    private Client client;
    private Player Matteo;
    private Player Mirko;
    private Player Andrea;
    private Worker MatteoW1;
    private Worker MatteoW2;
    private Worker AndreaW1;
    private Worker AndreaW2;
    private Worker MirkoW1;
    private Worker MirkoW2;

    // UTILS
    //Initializing points
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
    Point point31 = new Point(3, 1);

    // Build utils
    Map<String, Map<Point, List<BuildingType>>> realPossibleBuilds = new HashMap<>();
    Map<Point, List<BuildingType>> possiblBuildsW1 = new HashMap<>();
    Map<Point, List<BuildingType>> possiblBuildsW2 = new HashMap<>();
    Map<Point, List<BuildingType>> builds = new HashMap<>();
    List<BuildingType> buildsInThisPoint = new ArrayList<>();
    List<Point> dataOrder = new ArrayList<>();
    PacketBuild packetBuild;

    //Move utils
    List<Point> moves = new ArrayList<>();
    Map<String, Set<Point>> realPossibleMoves = new HashMap<>();
    Set<Point> possibleMovesW1 = new HashSet<>();
    Set<Point> possibleMovesW2 = new HashSet<>();
    PacketMove packetMove;

    @BeforeAll
    static void init() throws CardLoadingException, InvalidCardException {
        //CardFactory
        cardFactory = CardFactory.getInstance();
    }

    @BeforeEach
    void setUp() {
        List<String> players = new ArrayList<>();
        players.add("Andrea");
        players.add("Matteo");
        players.add("Mirko");

        model = new InternalModel(players, cardFactory, true);
        turnLogic = new TurnLogic(model);
        client = new Client(turnLogic);
        Andrea = model.getPlayerByNick("Andrea");
        Matteo = model.getPlayerByNick("Matteo");
        Mirko = model.getPlayerByNick("Mirko");
        MatteoW1 = Matteo.getWorkers().get(0);
        MatteoW2 = Matteo.getWorkers().get(1);
        MirkoW1 = Mirko.getWorkers().get(0);
        MirkoW2 = Mirko.getWorkers().get(1);
        AndreaW1 = Andrea.getWorkers().get(0);
        AndreaW2 = Andrea.getWorkers().get(1);

        /* AT THE BEGINNING OF EACH TEST IT IS RECOMMENDED TO SET A CARD FILE FOR EACH PLAYER
           AND THEN CALL COMPILE CARD STRATEGY IN THE MODEL
        */
    }

    /*
        A few simultated turns with gods prometheus, ahtena and pan
        Specifically, it is tested the loss caused by denies in hardcore mode
        and the win caused by being the last player in game
     */
   @Test
   void match_PrometheusPanAthena_denyLoss(){

        /*
                  0    1     2    3    4   X
                +----+----+----+----+----+
            0   |    |    |    |    |    |
                +----+----+----+----+----+
            1   |    |    |    |    |    |
                |    |    |    |    |    |
                +----+----+----+----+----+
            2   |    |    | M1 |    |    |
                |    |    |    |    |    |
                +----+----+----+----+----+
            3   |    | D2 |    |    | D1 |
                |    |    |    |    |    |
                +----+----+----+----+----+
            4   | A2 | A1 |    | M2 |    |
                |    |    |    |    |    |
                +----+----+----+----+----+
            Y
        */

       //UTILITIES
       // Initializing cards
       CardFile prometheus = cardFactory.getCards().stream().filter(x -> x.getName().equals("Prometheus")).findFirst().orElse(null);
       CardFile athena = cardFactory.getCards().stream().filter(x -> x.getName().equals("Athena")).findFirst().orElse(null);
       CardFile pan = cardFactory.getCards().stream().filter(x -> x.getName().equals("Pan")).findFirst().orElse(null);
       Matteo.setCard(athena);
       Andrea.setCard(prometheus);
       Mirko.setCard(pan);
       model.compileCardStrategy();


       //Setup the map
       model.getBoard().getCell(point22).setWorker(MatteoW1.getID());
       MatteoW1.setPosition(point22);

       model.getBoard().getCell(point34).setWorker(MatteoW2.getID());
       MatteoW2.setPosition(point34);

       model.getBoard().getCell(point14).setWorker(AndreaW1.getID());
       AndreaW1.setPosition(point14);

       model.getBoard().getCell(point04).setWorker(AndreaW2.getID());
       AndreaW2.setPosition(point04);

       model.getBoard().getCell(point43).setWorker(MirkoW1.getID());
       MirkoW1.setPosition(point43);

       model.getBoard().getCell(point13).setWorker(MirkoW2.getID());
       MirkoW2.setPosition(point13);

       //START
       turnLogic.start();

       //IT ASKS FOR A BUILD OR A MOVE
       PacketDoAction packetDoAction = client.getPacketDoAction();
       assertNotNull(packetDoAction);
       assertEquals(packetDoAction.getTo(),Andrea.getNickname());
       assertEquals(packetDoAction.getActionType(), ActionType.MOVE_BUILD);

       //ANDREA ASKS FOR THE POSSIBLE MOVES
       moves.clear();
       packetMove = new PacketMove("Andrea", null, moves);
       turnLogic.getPossibleMoves("Andrea", packetMove);

       PacketPossibleMoves packetPossibleMoves = client.getPacketPossibleMoves();
       assertNotNull(packetPossibleMoves);
       assertEquals(packetPossibleMoves.getTo(),Andrea.getNickname());

       possibleMovesW1.clear();
       possibleMovesW1.add(point03);
       possibleMovesW1.add(point23);
       possibleMovesW1.add(point24);

       possibleMovesW2.clear();
       possibleMovesW2.add(point03);

       realPossibleMoves.clear();
       realPossibleMoves.put("Andrea.1", possibleMovesW1);
       realPossibleMoves.put("Andrea.2", possibleMovesW2);

       assertEquals(packetPossibleMoves.getPossibleMoves(), realPossibleMoves);

       //ANDREA ASKS FOR THE POSSIBLE BUILDS
       builds.clear();
       dataOrder.clear();

       packetBuild = new PacketBuild("Andrea", "Andrea.1",builds, dataOrder);
       turnLogic.getPossibleBuilds("Andrea", packetBuild);

       PacketPossibleBuilds packetPossibleBuilds = client.getPacketPossibleBuilds();
       assertNotNull(packetPossibleBuilds);
       assertEquals(packetPossibleBuilds.getTo(),Andrea.getNickname());

       List<BuildingType> possibleInThisPoint = new ArrayList<>();
       possibleInThisPoint.add(BuildingType.FIRST_FLOOR);
       possiblBuildsW1.put(point03, possibleInThisPoint);
       possiblBuildsW1.put(point23, possibleInThisPoint);
       possiblBuildsW1.put(point24, possibleInThisPoint);
       possiblBuildsW2.put(point03, possibleInThisPoint);
       realPossibleBuilds.put("Andrea.1",possiblBuildsW1);
       realPossibleBuilds.put("Andrea.2",possiblBuildsW2);

       assertEquals(realPossibleBuilds, packetPossibleBuilds.getPossibleBuilds());

       //ANDREA BUILDS IN 2,4
       /*
                  0    1     2    3    4   X
                +----+----+----+----+----+
            0   |    |    |    |    |    |
                +----+----+----+----+----+
            1   |    |    |    |    |    |
                |    |    |    |    |    |
                +----+----+----+----+----+
            2   |    |    | M1 |    |    |
                |    |    |    |    |    |
                +----+----+----+----+----+
            3   |    | D2 |    |    | D1 |
                |    |    |    |    |    |
                +----+----+----+----+----+
            4   | A2 | A1 |    | M2 |    |
                |    |    | FF |    |    |
                +----+----+----+----+----+
            Y
        */
       makeSimpleBuild(Andrea, AndreaW1,point24,BuildingType.FIRST_FLOOR);

       //UPDATE BOARD
       PacketUpdateBoard packetUpdateBoard = client.getPacketUpdateBoard();
       assertNotNull(packetUpdateBoard);
       assertNull(packetUpdateBoard.getWorkersPositions());

       //IT ASKS FOR A MOVE
       packetDoAction = client.getPacketDoAction();
       assertNotNull(packetDoAction);
       assertEquals(packetDoAction.getTo(),Andrea.getNickname());
       assertEquals(packetDoAction.getActionType(), ActionType.MOVE);

       //ANDREA ASKS FOR THE POSSIBLE MOVES
       moves.clear();
       packetMove = new PacketMove("Andrea", "Andrea.1", moves);
       turnLogic.getPossibleMoves("Andrea", packetMove);

       packetPossibleMoves = client.getPacketPossibleMoves();
       assertNotNull(packetPossibleMoves);
       assertEquals(packetPossibleMoves.getTo(),Andrea.getNickname());

       possibleMovesW1.clear();
       possibleMovesW1.add(point03);
       possibleMovesW1.add(point23);

       possibleMovesW2.clear();

       realPossibleMoves.clear();
       realPossibleMoves.put("Andrea.1", possibleMovesW1);
       realPossibleMoves.put("Andrea.2", possibleMovesW2);

       assertEquals(packetPossibleMoves.getPossibleMoves(), realPossibleMoves);

       // ANDREA MOVES IN 2,3
        /*
                  0    1     2    3    4   X
                +----+----+----+----+----+
            0   |    |    |    |    |    |
                +----+----+----+----+----+
            1   |    |    |    |    |    |
                |    |    |    |    |    |
                +----+----+----+----+----+
            2   |    |    | M1 |    |    |
                |    |    |    |    |    |
                +----+----+----+----+----+
            3   |    | D2 | A1 |    | D1 |
                |    |    |    |    |    |
                +----+----+----+----+----+
            4   | A2 |    |    | M2 |    |
                |    |    | FF |    |    |
                +----+----+----+----+----+
            Y
        */

       makeSimpleMove(Andrea, AndreaW1, point23);

       //UPDATE BOARD
       packetUpdateBoard = client.getPacketUpdateBoard();
       assertNotNull(packetUpdateBoard);
       assertNull(packetUpdateBoard.getNewBuildings());
       Map<String,Point> workersPositions = new HashMap<>();
       workersPositions.put("Andrea.1", point23);
       workersPositions.put("Andrea.2", point04);
       workersPositions.put("Matteo.1", point22);
       workersPositions.put("Matteo.2", point34);
       workersPositions.put("Mirko.1", point43);
       workersPositions.put("Mirko.2", point13);
       assertEquals(workersPositions, packetUpdateBoard.getWorkersPositions());


       //IT ASKS FOR A BUILD TO ANDREA
       packetDoAction = client.getPacketDoAction();
       assertNotNull(packetDoAction);
       assertEquals(packetDoAction.getTo(),Andrea.getNickname());
       assertEquals(packetDoAction.getActionType(), ActionType.BUILD);

       //ANDREA ASKS FOR THE POSSIBLE BUILDS
       builds.clear();
       dataOrder.clear();

       packetBuild = new PacketBuild("Andrea", "Andrea.1",builds, dataOrder);
       turnLogic.getPossibleBuilds("Andrea", packetBuild);

       packetPossibleBuilds = client.getPacketPossibleBuilds();
       assertNotNull(packetPossibleBuilds);
       assertEquals(packetPossibleBuilds.getTo(),Andrea.getNickname());

       List<BuildingType> possibleIn24 = new ArrayList<>();
       realPossibleBuilds.clear();
       possibleInThisPoint.clear();
       possiblBuildsW1.clear();
       possiblBuildsW2.clear();
       possibleInThisPoint.add(BuildingType.FIRST_FLOOR);
       possibleIn24.add(BuildingType.SECOND_FLOOR);
       possiblBuildsW1.put(point14, possibleInThisPoint);
       possiblBuildsW1.put(point12, possibleInThisPoint);
       possiblBuildsW1.put(point24, possibleIn24);
       possiblBuildsW1.put(point33, possibleInThisPoint);
       possiblBuildsW1.put(point32, possibleInThisPoint);
       realPossibleBuilds.put("Andrea.1",possiblBuildsW1);
       realPossibleBuilds.put("Andrea.2",possiblBuildsW2);

       assertEquals(realPossibleBuilds, packetPossibleBuilds.getPossibleBuilds());

       //ANDREA BUILDS IN 3,2
       /*
                  0    1     2    3    4   X
                +----+----+----+----+----+
            0   |    |    |    |    |    |
                +----+----+----+----+----+
            1   |    |    |    |    |    |
                |    |    |    |    |    |
                +----+----+----+----+----+
            2   |    |    | M1 |    |    |
                |    |    |    | FF |    |
                +----+----+----+----+----+
            3   |    | D2 | A1 |    | D1 |
                |    |    |    |    |    |
                +----+----+----+----+----+
            4   | A2 |    |    | M2 |    |
                |    |    | FF |    |    |
                +----+----+----+----+----+
            Y
        */
       makeSimpleBuild(Andrea, AndreaW1,point32,BuildingType.FIRST_FLOOR);

       //UPDATE BOARD
       packetUpdateBoard = client.getPacketUpdateBoard();
       assertNotNull(packetUpdateBoard);
       assertNull(packetUpdateBoard.getWorkersPositions());

       //IT ASKS FOR A MOVE TO MATTEO
       packetDoAction = client.getPacketDoAction();
       assertNotNull(packetDoAction);
       assertEquals(packetDoAction.getTo(),Matteo.getNickname());
       assertEquals(packetDoAction.getActionType(), ActionType.MOVE);

       //MATTEO ASKS FOR THE POSSIBLE MOVES
       moves.clear();
       packetMove = new PacketMove("Matteo", null, moves);
       turnLogic.getPossibleMoves("Matteo", packetMove);

       packetPossibleMoves = client.getPacketPossibleMoves();
       assertNotNull(packetPossibleMoves);
       assertEquals(packetPossibleMoves.getTo(),Matteo.getNickname());

       possibleMovesW1.clear();
       possibleMovesW1.add(point12);
       possibleMovesW1.add(point11);
       possibleMovesW1.add(point21);
       possibleMovesW1.add(point31);
       possibleMovesW1.add(point32);
       possibleMovesW1.add(point33);


       possibleMovesW2.clear();
       possibleMovesW2.add(point24);
       possibleMovesW2.add(point33);
       possibleMovesW2.add(point44);

       realPossibleMoves.clear();
       realPossibleMoves.put(MatteoW1.getID(), possibleMovesW1);
       realPossibleMoves.put(MatteoW2.getID(), possibleMovesW2);

       assertEquals(packetPossibleMoves.getPossibleMoves(), realPossibleMoves);


       //MATTEO MOVES TO 3,2
        /*
                  0    1     2    3    4   X
                +----+----+----+----+----+
            0   |    |    |    |    |    |
                +----+----+----+----+----+
            1   |    |    |    |    |    |
                |    |    |    |    |    |
                +----+----+----+----+----+
            2   |    |    |    | M1 |    |
                |    |    |    | FF |    |
                +----+----+----+----+----+
            3   |    | D2 | A1 |    | D1 |
                |    |    |    |    |    |
                +----+----+----+----+----+
            4   | A2 |    |    | M2 |    |
                |    |    | FF |    |    |
                +----+----+----+----+----+
            Y
        */
       makeSimpleMove(Matteo, MatteoW1, point32);

       //UPDATE BOARD
       packetUpdateBoard = client.getPacketUpdateBoard();
       assertNotNull(packetUpdateBoard);
       assertNull(packetUpdateBoard.getNewBuildings());
       workersPositions.clear();
       workersPositions.put("Andrea.1", point23);
       workersPositions.put("Andrea.2", point04);
       workersPositions.put("Matteo.1", point32);
       workersPositions.put("Matteo.2", point34);
       workersPositions.put("Mirko.1", point43);
       workersPositions.put("Mirko.2", point13);
       assertEquals(workersPositions, packetUpdateBoard.getWorkersPositions());

       //IT ASKS FOR A BUILD TO MATTEO
       packetDoAction = client.getPacketDoAction();
       assertNotNull(packetDoAction);
       assertEquals(packetDoAction.getTo(),Matteo.getNickname());
       assertEquals(packetDoAction.getActionType(), ActionType.BUILD);

       //MATTEO BUILDS IN 3,3
       /*
                  0    1     2    3    4   X
                +----+----+----+----+----+
            0   |    |    |    |    |    |
                +----+----+----+----+----+
            1   |    |    |    |    |    |
                |    |    |    |    |    |
                +----+----+----+----+----+
            2   |    |    |    | M1 |    |
                |    |    |    | FF |    |
                +----+----+----+----+----+
            3   |    | D2 | A1 |    | D1 |
                |    |    |    | FF |    |
                +----+----+----+----+----+
            4   | A2 |    |    | M2 |    |
                |    |    | FF |    |    |
                +----+----+----+----+----+
            Y
        */
       makeSimpleBuild(Matteo, MatteoW1, point33, BuildingType.FIRST_FLOOR);

       //UPDATE BOARD
       packetUpdateBoard = client.getPacketUpdateBoard();
       assertNotNull(packetUpdateBoard);
       assertNull(packetUpdateBoard.getWorkersPositions());

       //IT ASKS FOR A MOVE TO MIRKO
       packetDoAction = client.getPacketDoAction();
       assertNotNull(packetDoAction);
       assertEquals(packetDoAction.getTo(),Mirko.getNickname());
       assertEquals(packetDoAction.getActionType(), ActionType.MOVE);

       //MIRKO TRIES TO MOVE TO 3,3 WITH WORKER 1 AND LOSES
       /*
                  0    1     2    3    4   X
                +----+----+----+----+----+
            0   |    |    |    |    |    |
                +----+----+----+----+----+
            1   |    |    |    |    |    |
                |    |    |    |    |    |
                +----+----+----+----+----+
            2   |    |    |    | M1 |    |
                |    |    |    | FF |    |
                +----+----+----+----+----+
            3   |    |    | A1 |    |    |
                |    |    |    | FF |    |
                +----+----+----+----+----+
            4   | A2 |    |    | M2 |    |
                |    |    | FF |    |    |
                +----+----+----+----+----+
            Y
        */
       makeSimpleMove(Mirko,MirkoW1,point33);

       //UPDATE BOARD WITH LOSS OF MIRKO
       packetUpdateBoard = client.getPacketUpdateBoard();
       assertNotNull(packetUpdateBoard);
       assertNull(packetUpdateBoard.getNewBuildings());
       assertNull(packetUpdateBoard.getWorkersPositions());
       assertEquals(Mirko.getNickname(), packetUpdateBoard.getPlayerLostID());

       //IT ASKS FOR A MOVE OR BUILD TO ANDREA
       packetDoAction = client.getPacketDoAction();
       assertNotNull(packetDoAction);
       assertEquals(packetDoAction.getTo(),Andrea.getNickname());
       assertEquals(packetDoAction.getActionType(), ActionType.MOVE_BUILD);

       //ANDREA ASKS FOR THE POSSIBLE MOVES
       moves.clear();
       packetMove = new PacketMove("Andrea", null, moves);
       turnLogic.getPossibleMoves("Andrea", packetMove);

       packetPossibleMoves = client.getPacketPossibleMoves();
       assertNotNull(packetPossibleMoves);
       assertEquals(packetPossibleMoves.getTo(),Andrea.getNickname());

       possibleMovesW1.clear();
       possibleMovesW1.add(point13);
       possibleMovesW1.add(point14);
       possibleMovesW1.add(point22);
       possibleMovesW1.add(point33);
       possibleMovesW1.add(point24);
       possibleMovesW1.add(point12);

       possibleMovesW2.clear();
       possibleMovesW2.add(point13);
       possibleMovesW2.add(point03);
       possibleMovesW2.add(point14);

       realPossibleMoves.clear();
       realPossibleMoves.put("Andrea.1", possibleMovesW1);
       realPossibleMoves.put("Andrea.2", possibleMovesW2);

       assertEquals(packetPossibleMoves.getPossibleMoves(), realPossibleMoves);

       //ANDREA TRIES TO MOVE TO 3,3 WITH WORKER 1 AND LOSES
         /*
                  0    1     2    3    4   X
                +----+----+----+----+----+
            0   |    |    |    |    |    |
                +----+----+----+----+----+
            1   |    |    |    |    |    |
                |    |    |    |    |    |
                +----+----+----+----+----+
            2   |    |    |    | M1 |    |
                |    |    |    | FF |    |
                +----+----+----+----+----+
            3   |    |    |    |    |    |
                |    |    |    | FF |    |
                +----+----+----+----+----+
            4   |    |    |    | M2 |    |
                |    |    | FF |    |    |
                +----+----+----+----+----+
            Y
        */
       makeSimpleMove(Andrea,AndreaW1,point33);

       //UPDATE BOARD WITH LOSS OF ANDREA
       packetUpdateBoard = client.getPacketUpdateBoard();
       assertNotNull(packetUpdateBoard);
       assertNull(packetUpdateBoard.getNewBuildings());
       assertNull(packetUpdateBoard.getWorkersPositions());
       assertNull(packetUpdateBoard.getPlayerWonID());
       assertEquals(Andrea.getNickname(), packetUpdateBoard.getPlayerLostID());

       //UPDATE BOARD WITH WIN OF MATTEO
       packetUpdateBoard = client.getPacketUpdateBoard();
       assertNotNull(packetUpdateBoard);
       assertNull(packetUpdateBoard.getNewBuildings());
       assertNull(packetUpdateBoard.getWorkersPositions());
       assertNull(packetUpdateBoard.getPlayerLostID());
       assertEquals(Matteo.getNickname(), packetUpdateBoard.getPlayerWonID());

       //CHECK THE LOSERS ARE RIGHT IN THE MODEL
       assertEquals(Matteo,model.getWinner());
       List<Player> losers = new ArrayList<>();
       losers.add(Mirko);
       losers.add(Andrea);
       assertEquals(losers, model.getLosers());

         /*
                  0    1     2    3    4   X
                +----+----+----+----+----+
            0   |    |    |    |    |    |
                +----+----+----+----+----+
            1   |    |    |    |    |    |
                |    |    |    |    |    |
                +----+----+----+----+----+
            2   |    |    |    | M1 |    |
                |    |    |    | FF |    |
                +----+----+----+----+----+
            3   |    |    | A1 |    |    |
                |    |    |    | FF |    |
                +----+----+----+----+----+
            4   | A2 |    |    | M2 |    |
                |    |    | FF |    |    |
                +----+----+----+----+----+
            Y
        */

       //CHECK THE MAP IS CORRECT
       assertFalse(model.getBoard().getCell(point23).isOccupied());
       assertFalse(model.getBoard().getCell(point04).isOccupied());
       assertNull(Andrea.getWorkers().get(0).getPosition());
       assertNull(Andrea.getWorkers().get(1).getPosition());

   }

    /*
         It simulates a move as pan
         Specifically, it is tested pan's win
      */
    @Test
    void match_PrometheusPanAthena_panWins(){

        /*
                  0    1     2    3    4   X
                +----+----+----+----+----+
            0   |    |    |    |    |    |
                +----+----+----+----+----+
            1   |    |    |    |    |    |
                |    |    |    |    |    |
                +----+----+----+----+----+
            2   |    |    | M1 |    |    |
                |    |    |    |    |    |
                +----+----+----+----+----+
            3   |    | D2 |    |    | D1 |
                |    |    |    |    |    |
                +----+----+----+----+----+
            4   | A2 | A1 |    | M2 |    |
                |    | SF |    |    |    |
                +----+----+----+----+----+
        */
        //UTILITIES
        // Initializing cards
        CardFile prometheus = cardFactory.getCards().stream().filter(x -> x.getName().equals("Prometheus")).findFirst().orElse(null);
        CardFile athena = cardFactory.getCards().stream().filter(x -> x.getName().equals("Athena")).findFirst().orElse(null);
        CardFile pan = cardFactory.getCards().stream().filter(x -> x.getName().equals("Pan")).findFirst().orElse(null);
        Matteo.setCard(prometheus);
        Andrea.setCard(pan);
        Mirko.setCard(athena);
        model.compileCardStrategy();

        //Setup the map
        model.getBoard().getCell(point14).addBuilding(BuildingType.FIRST_FLOOR);
        model.getBoard().getCell(point14).addBuilding(BuildingType.SECOND_FLOOR);

        model.getBoard().getCell(point22).setWorker(MatteoW1.getID());
        MatteoW1.setPosition(point22);

        model.getBoard().getCell(point34).setWorker(MatteoW2.getID());
        MatteoW2.setPosition(point34);

        model.getBoard().getCell(point14).setWorker(AndreaW1.getID());
        AndreaW1.setPosition(point14);

        model.getBoard().getCell(point04).setWorker(AndreaW2.getID());
        AndreaW2.setPosition(point04);

        model.getBoard().getCell(point43).setWorker(MirkoW1.getID());
        MirkoW1.setPosition(point43);

        model.getBoard().getCell(point13).setWorker(MirkoW2.getID());
        MirkoW2.setPosition(point13);

        //START
        turnLogic.start();

        //IT ASKS FOR A MOVE
        PacketDoAction packetDoAction = client.getPacketDoAction();
        assertNotNull(packetDoAction);
        assertEquals(packetDoAction.getTo(),Andrea.getNickname());
        assertEquals(packetDoAction.getActionType(), ActionType.MOVE);

        //ANDREA ASKS FOR THE POSSIBLE MOVES
        moves.clear();
        packetMove = new PacketMove("Andrea", null, moves);
        turnLogic.getPossibleMoves("Andrea", packetMove);

        PacketPossibleMoves packetPossibleMoves = client.getPacketPossibleMoves();
        assertNotNull(packetPossibleMoves);
        assertEquals(packetPossibleMoves.getTo(),Andrea.getNickname());

        possibleMovesW1.clear();
        possibleMovesW1.add(point03);
        possibleMovesW1.add(point23);
        possibleMovesW1.add(point24);

        possibleMovesW2.clear();
        possibleMovesW2.add(point03);

        realPossibleMoves.clear();
        realPossibleMoves.put("Andrea.1", possibleMovesW1);
        realPossibleMoves.put("Andrea.2", possibleMovesW2);

        assertEquals(packetPossibleMoves.getPossibleMoves(), realPossibleMoves);

        // ANDREA MOVES IN 2,3 AND WINS
            /*
                  0    1     2    3    4   X
                +----+----+----+----+----+
            0   |    |    |    |    |    |
                +----+----+----+----+----+
            1   |    |    |    |    |    |
                |    |    |    |    |    |
                +----+----+----+----+----+
            2   |    |    | M1 |    |    |
                |    |    |    |    |    |
                +----+----+----+----+----+
            3   |    | D2 | A1 |    | D1 |
                |    |    |    |    |    |
                +----+----+----+----+----+
            4   | A2 |    |    | M2 |    |
                |    | SF |    |    |    |
                +----+----+----+----+----+
        */

        makeSimpleMove(Andrea, AndreaW1, point23);

        //UPDATE BOARD
        PacketUpdateBoard packetUpdateBoard = client.getPacketUpdateBoard();
        assertNotNull(packetUpdateBoard);
        assertNull(packetUpdateBoard.getNewBuildings());
        Map<String,Point> workersPositions = new HashMap<>();
        workersPositions.put("Andrea.1", point23);
        workersPositions.put("Andrea.2", point04);
        workersPositions.put("Matteo.1", point22);
        workersPositions.put("Matteo.2", point34);
        workersPositions.put("Mirko.1", point43);
        workersPositions.put("Mirko.2", point13);
        assertEquals(workersPositions, packetUpdateBoard.getWorkersPositions());

        //UPDATE BOARD HAS WIN OF ANDREA
        assertNotNull(packetUpdateBoard);
        assertNull(packetUpdateBoard.getNewBuildings());
        assertEquals(Andrea.getNickname(), packetUpdateBoard.getPlayerWonID());

        assertEquals(Andrea,model.getWinner());
        assertEquals(0, model.getLosers().size());
    }

    /*
        It is a game with two players
        The first, having Apollo, loses because he is impossibilitated
        to build after a swap
        The second, consequentially, immediatly wins
     */
    @Test
    void impossibilitatedToBuildLoss(){

        /*
                  0    1     2    3    4   X
                +----+----+----+----+----+
            0   |    |    |    |    |    |
                +----+----+----+----+----+
            1   |    |    |    |    |    |
                |    |    |    |    |    |
                +----+----+----+----+----+
            2   |    |    |    |    |    |
                | DM | DM | DM |    |    |
                +----+----+----+----+----+
            3   |    | D2 |    |    | D1 |
                | DM |    | DM |    |    |
                +----+----+----+----+----+
            4   | A2 | A1 |    |    |    |
                |    |    | DM |    |    |
                +----+----+----+----+----+
        */
        //UTILITIES
        //I want to play with two players so i reinitialize everithing
        List<String> players = new ArrayList<>();
        players.add("Andrea");
        players.add("Mirko");
        model = new InternalModel(players, cardFactory, true);
        turnLogic = new TurnLogic(model);
        client = new Client(turnLogic);
        Andrea = model.getPlayerByNick("Andrea");
        Mirko = model.getPlayerByNick("Mirko");

        MirkoW1 = Mirko.getWorkers().get(0);
        MirkoW2 = Mirko.getWorkers().get(1);
        AndreaW1 = Andrea.getWorkers().get(0);
        AndreaW2 = Andrea.getWorkers().get(1);

        // Initializing cards
        CardFile apollo = cardFactory.getCards().stream().filter(x -> x.getName().equals("Apollo")).findFirst().orElse(null);
        CardFile athena = cardFactory.getCards().stream().filter(x -> x.getName().equals("Athena")).findFirst().orElse(null);
        Andrea.setCard(apollo);
        Mirko.setCard(athena);
        model.compileCardStrategy();

        //Setup the map
        model.getBoard().getCell(point24).addBuilding(BuildingType.DOME);
        model.getBoard().getCell(point23).addBuilding(BuildingType.DOME);
        model.getBoard().getCell(point12).addBuilding(BuildingType.DOME);
        model.getBoard().getCell(point02).addBuilding(BuildingType.DOME);
        model.getBoard().getCell(point03).addBuilding(BuildingType.DOME);
        model.getBoard().getCell(point22).addBuilding(BuildingType.DOME);


        model.getBoard().getCell(point14).setWorker(AndreaW1.getID());
        AndreaW1.setPosition(point14);

        model.getBoard().getCell(point04).setWorker(AndreaW2.getID());
        AndreaW2.setPosition(point04);

        model.getBoard().getCell(point43).setWorker(MirkoW1.getID());
        MirkoW1.setPosition(point43);

        model.getBoard().getCell(point13).setWorker(MirkoW2.getID());
        MirkoW2.setPosition(point13);

        //START
        turnLogic.start();

        //IT ASKS FOR A MOVE
        PacketDoAction packetDoAction = client.getPacketDoAction();
        assertNotNull(packetDoAction);
        assertEquals(packetDoAction.getTo(),Andrea.getNickname());
        assertEquals(packetDoAction.getActionType(), ActionType.MOVE);

        //ANDREA ASKS FOR THE POSSIBLE MOVES
        moves.clear();
        packetMove = new PacketMove("Andrea", null, moves);
        turnLogic.getPossibleMoves("Andrea", packetMove);

        PacketPossibleMoves packetPossibleMoves = client.getPacketPossibleMoves();
        assertNotNull(packetPossibleMoves);
        assertEquals(packetPossibleMoves.getTo(),Andrea.getNickname());

        possibleMovesW1.clear();
        possibleMovesW1.add(point13);

        possibleMovesW2.clear();
        possibleMovesW2.add(point13);

        realPossibleMoves.clear();
        realPossibleMoves.put("Andrea.1", possibleMovesW1);
        realPossibleMoves.put("Andrea.2", possibleMovesW2);

        assertEquals(packetPossibleMoves.getPossibleMoves(), realPossibleMoves);


        // ANDREA MOVES IN 1,3 AND CANNOT BUILD AND THEREFORE LOSES
           /*
                  0    1     2    3    4   X
                +----+----+----+----+----+
            0   |    |    |    |    |    |
                +----+----+----+----+----+
            1   |    |    |    |    |    |
                |    |    |    |    |    |
                +----+----+----+----+----+
            2   |    |    |    |    |    |
                | DM | DM | DM |    |    |
                +----+----+----+----+----+
            3   |    |    |    |    | D1 |
                | DM |    | DM |    |    |
                +----+----+----+----+----+
            4   |    | D2 |    |    |    |
                |    |    | DM |    |    |
                +----+----+----+----+----+
        */

        makeSimpleMove(Andrea, AndreaW1, point13);

        //UPDATE BOARD WITH APPROVED MOVE OF ANDREA
        PacketUpdateBoard packetUpdateBoard = client.getPacketUpdateBoard();
        assertNotNull(packetUpdateBoard);
        assertNull(packetUpdateBoard.getNewBuildings());
        assertNull(packetUpdateBoard.getPlayerWonID());
        assertNull(packetUpdateBoard.getPlayerLostID());
        Map<String,Point> workersPositions = new HashMap<>();
        workersPositions.put("Andrea.1", point13);
        workersPositions.put("Andrea.2", point04);
        workersPositions.put("Mirko.1", point43);
        workersPositions.put("Mirko.2", point14);
        assertEquals(workersPositions, packetUpdateBoard.getWorkersPositions());

        //UPDATE BOARD WITH LOSS OF ANDREA
        packetUpdateBoard = client.getPacketUpdateBoard();
        assertNotNull(packetUpdateBoard);
        assertNull(packetUpdateBoard.getNewBuildings());
        assertNull(packetUpdateBoard.getWorkersPositions());
        assertNull(packetUpdateBoard.getPlayerWonID());
        assertEquals(Andrea.getNickname(), packetUpdateBoard.getPlayerLostID());


        //CHECK THE LOSER IS CORRECTLY PLACED IN THE MODEL
        assertEquals(1, model.getLosers().size());
        assertEquals(Andrea,model.getLosers().get(0));

        //CHECK THE MAP IS CORRECT
        assertFalse(model.getBoard().getCell(point04).isOccupied());
        assertFalse(model.getBoard().getCell(point13).isOccupied());
        assertNull(Andrea.getWorkers().get(0).getPosition());
        assertNull(Andrea.getWorkers().get(1).getPosition());
        assertEquals(point14, MirkoW2.getPosition());
        assertEquals(MirkoW2.getID(), model.getBoard().getCell(point14).getWorkerID());

        //UPDATE BOARD WITH WIN OF MIRKO
        packetUpdateBoard = client.getPacketUpdateBoard();
        assertNotNull(packetUpdateBoard);
        assertNull(packetUpdateBoard.getNewBuildings());
        assertNull(packetUpdateBoard.getWorkersPositions());
        assertNull(packetUpdateBoard.getPlayerLostID());
        assertEquals(Mirko.getNickname(), packetUpdateBoard.getPlayerWonID());

        //CHECK THE WINNER IN THE MODEL
        assertEquals(Mirko, model.getWinner());

    }


   void makeSimpleMove(Player player, Worker worker, Point point){
        moves.clear();

       moves.add(point);
       packetMove = new PacketMove(player.getNickname(), worker.getID(), moves);
       try {
           turnLogic.consumePacketMove(player.getNickname(), packetMove);
       } catch (InvalidPacketException e) {
           assert false;
       }

   }

    void makeSimpleBuild(Player player, Worker worker, Point point, BuildingType buildingType) {
        builds.clear();
        buildsInThisPoint.clear();
        dataOrder.clear();

        buildsInThisPoint.add(buildingType);
        builds.put(point, buildsInThisPoint);
        dataOrder.add(point);
        packetBuild = new PacketBuild(player.getNickname(), worker.getID(), builds, dataOrder);

        try {
            turnLogic.consumePacketBuild(player.getNickname(), packetBuild);
        } catch (InvalidPacketException e) {
            assert false;
        }

    }

}