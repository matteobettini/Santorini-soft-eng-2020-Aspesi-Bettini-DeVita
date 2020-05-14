package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.cards.CardFactory;
import it.polimi.ingsw.server.cards.CardFile;
import it.polimi.ingsw.server.cards.exceptions.InvalidCardException;
import it.polimi.ingsw.common.enums.ActionType;
import it.polimi.ingsw.common.enums.BuildingType;
import it.polimi.ingsw.server.model.enums.LevelType;
import it.polimi.ingsw.server.model.enums.PlayerState;
import it.polimi.ingsw.common.packets.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.List;
import java.util.Queue;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class TurnLogicMirkoTest {

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
    private Client mockView;
    private Player Matteo;
    private Player Mirko;
    private Player Andrea;
    private Worker MatteoW1;
    private Worker MatteoW2;
    private Worker AndreaW1;
    private Worker AndreaW2;
    private Worker MirkoW1;
    private Worker MirkoW2;
    private final Point point00 = new Point(0,0);
    private final Point point01 = new Point(0,1);
    private final Point point02 = new Point(0,2);
    private final Point point03 = new Point(0,3);
    private final Point point04 = new Point(0,4);
    private final Point point10 = new Point(1,0);
    private final Point point11 = new Point(1,1);
    private final Point point12 = new Point(1,2);
    private final Point point13 = new Point(1,3);
    private final Point point14 = new Point(1,4);
    private final Point point20 = new Point(2,0);
    private final Point point21 = new Point(2,1);
    private final Point point22 = new Point(2,2);
    private final Point point23 = new Point(2,3);
    private final Point point24 = new Point(2,4);
    private final Point point30 = new Point(3,0);
    private final Point point31 = new Point(3,1);
    private final Point point32 = new Point(3,2);
    private final Point point33 = new Point(3,3);
    private final Point point34 = new Point(3,4);
    private final Point point40 = new Point(4,0);
    private final Point point41 = new Point(4,1);
    private final Point point42 = new Point(4,2);
    private final Point point43 = new Point(4,3);
    private final Point point44 = new Point(4,4);
    private final Point outOfBoardPosition = new Point(6,5);

    @BeforeAll
    static void init() throws InvalidCardException {
        //CardFactory
        cardFactory = CardFactory.getInstance();
    }
    @BeforeEach
    void setUp() {
        List<String> players = new ArrayList<>();
        players.add("Andrea");
        players.add("Matteo");
        players.add("Mirko");
        model = new InternalModel(players, cardFactory, false);
        turnLogic = new TurnLogic(model);
        mockView = new Client(turnLogic);
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
     * In this test these situations are tested:
     * - A player loses because can't move anymore.
     * - Demeter can build a second block but not in the same position of if his first build.
     * - A player wins because the others have previously lost.
     */
    @Test
    void pan_demeter_minotaur_Match(){

        /*

                  0    1     2    3    4   X
                +----+----+----+----+----+
            0   |    |    |    |    |    |
                +----+----+----+----+----+
            1   |    |    |    |    |    |
                |    | D1 |    | M1 |    |
                +----+----+----+----+----+
            2   |    |    |    |    |    |
                |A2  |    | D2 |    |    |
                +----+----+----+----+----+
            3   |    | A1 |    | M2 |    |
                +----+----+----+----+----+
            4   |    |    |    |    |    |
                +----+----+----+----+----+
            Y
        */

        //MOVE UTILS
        List<Point> moves = new ArrayList<>();
        PacketMove packetMove;
        Set<Point> possibleMovesW1 = new HashSet<>();
        Set<Point> possibleMovesW2 = new HashSet<>();

        //BUILDS UTILS
        List<BuildingType> buildings = new ArrayList<>();
        Map<Point, List<BuildingType>> builds = new HashMap<>();
        List<Point> buildsOrder = new ArrayList<>();
        PacketBuild packetBuild;
        Map<Point, List<BuildingType>> possibleBuilds = new HashMap<>();
        List<BuildingType> buildsHelper = new ArrayList<>();

        // Initializing cards
        CardFile pan = cardFactory.getCards().stream().filter(x -> x.getName().equals("Pan")).findFirst().orElse(null);
        CardFile demeter = cardFactory.getCards().stream().filter(x -> x.getName().equals("Demeter")).findFirst().orElse(null);
        CardFile minotaur = cardFactory.getCards().stream().filter(x -> x.getName().equals("Minotaur")).findFirst().orElse(null);
        Matteo.setCard(pan); //MX
        Andrea.setCard(demeter); //AX
        Mirko.setCard(minotaur); //DX
        model.compileCardStrategy();

        model.getBoard().getCell(point11).setWorker(MirkoW1.getID());
        MirkoW1.setPosition(point11);
        model.getBoard().getCell(point22).setWorker(MirkoW2.getID());
        MirkoW2.setPosition(point22);

        model.getBoard().getCell(point31).setWorker(MatteoW1.getID());
        MatteoW1.setPosition(point31);
        model.getBoard().getCell(point33).setWorker(MatteoW2.getID());
        MatteoW2.setPosition(point33);

        model.getBoard().getCell(point13).setWorker(AndreaW1.getID());
        AndreaW1.setPosition(point13);
        model.getBoard().getCell(point02).setWorker(AndreaW2.getID());
        AndreaW2.setPosition(point02);

        turnLogic.start();

        //FIRST MOVE: ANDREA MOVES INTO 1,2
        /*

                  0    1     2    3    4   X
                +----+----+----+----+----+
            0   |    |    |    |    |    |
                +----+----+----+----+----+
            1   |    |    |    |    |    |
                | x  | D1 |    | M1 |    |
                +----+----+----+----+----+
            2   |    |    |    |    |    |
                |A2  | X  | D2 |    |    |
                +----+--↑-+----+----+----+
            3   | x  | A1 | x  | M2 |    |
                +----+----+----+----+----+
            4   |  x | x  | x  |    |    |
                +----+----+----+----+----+
            Y
        */

        //FIRST WE CHECK ALL THE POSSIBLE MOVES
        possibleMovesW1.add(point12);
        possibleMovesW1.add(point23);
        possibleMovesW1.add(point03);
        possibleMovesW1.add(point04);
        possibleMovesW1.add(point14);
        possibleMovesW1.add(point24);

        possibleMovesW2.add(point01);
        possibleMovesW2.add(point03);
        possibleMovesW2.add(point12);

        PacketDoAction packetDoAction = mockView.getPacketDoAction();
        assertEquals(packetDoAction.getTo(), Andrea.getNickname());
        assertEquals(packetDoAction.getActionType(), ActionType.MOVE);

        packetMove = new PacketMove(Andrea.getNickname(),null,moves);
        turnLogic.getPossibleMoves(Andrea.getNickname(), packetMove);
        PacketPossibleMoves packetPossibleMoves = mockView.getPacketPossibleMoves();
        assertEquals(packetPossibleMoves.getTo(), Andrea.getNickname());

        for(String worker : packetPossibleMoves.getPossibleMoves().keySet()){
            if(worker.equals(AndreaW1.getID())) assertEquals(packetPossibleMoves.getPossibleMoves().get(worker), possibleMovesW1);
            else if(worker.equals(AndreaW2.getID())) assertEquals(packetPossibleMoves.getPossibleMoves().get(worker), possibleMovesW2);
            else assert false;
        }

        //WE CHECK THAT AFTER THE WANTED MOVE THERE ARE NO MORE POSSIBLE MOVES

        moves.add(point12);
        packetMove = new PacketMove(Andrea.getNickname(), AndreaW1.getID(), moves);
        turnLogic.getPossibleMoves(Andrea.getNickname(), packetMove);
        packetPossibleMoves = mockView.getPacketPossibleMoves();
        assertEquals(packetPossibleMoves.getTo(), Andrea.getNickname());
        for(String worker : packetPossibleMoves.getPossibleMoves().keySet()){
            assertEquals(packetPossibleMoves.getPossibleMoves().get(worker).size(), 0);
        }

        //WE CONSUME THE MOVE

        try {
            turnLogic.consumePacketMove(Andrea.getNickname(),packetMove);
        } catch (InvalidPacketException e) {
            assert false;
        }
        assertEquals(Andrea.getState(), PlayerState.MOVED);
        assertEquals(point12, AndreaW1.getPosition());
        assertEquals(AndreaW1.getID(), model.getBoard().getCell(point12).getWorkerID());
        assertNull(model.getBoard().getCell(point13).getWorkerID());

        PacketUpdateBoard packetUpdateBoard = mockView.getPacketUpdateBoard();
        for(String worker : packetUpdateBoard.getWorkersPositions().keySet()){
            assertEquals(model.getWorkerByID(worker).getPosition(), packetUpdateBoard.getWorkersPositions().get(worker));
        }

        packetDoAction = mockView.getPacketDoAction();
        assertEquals(packetDoAction.getTo(), Andrea.getNickname());
        assertEquals(packetDoAction.getActionType(), ActionType.BUILD);

        //FIRST BUILD: ANDREA BUILDS A FIRST_FLOOR INTO 1,3 AND A FIRST FLOOR INTO 2,3
        /*

                  0    1     2    3    4   X
                +----+----+----+----+----+
            0   |    |    |    |    |    |
                +----+----+----+----+----+
            1   |    |    |    |    |    |
                | x  | D1 | x  | M1 |    |
                +----+----+----+----+----+
            2   |    |    |    |    |    |
                |A2  | A1 | D2 |    |    |
                +----+----+----+----+----+
            3   | x  | XFF| XFF| M2 |    |
                +----+----+----+----+----+
            4   |    |    |    |    |    |
                +----+----+----+----+----+
            Y
        */

        //FIRST WE CHECK ALL THE POSSIBLE BUILDS
        buildsHelper.add(BuildingType.FIRST_FLOOR);
        possibleBuilds.put(point01, buildsHelper);
        possibleBuilds.put(point21, buildsHelper);
        possibleBuilds.put(point03, buildsHelper);
        possibleBuilds.put(point23, buildsHelper);
        possibleBuilds.put(point13, buildsHelper);


        packetBuild = new PacketBuild(Andrea.getNickname(), AndreaW1.getID(),builds, buildsOrder);
        turnLogic.getPossibleBuilds(Andrea.getNickname(), packetBuild);
        PacketPossibleBuilds packetPossibleBuilds = mockView.getPacketPossibleBuilds();
        assertEquals(packetPossibleBuilds.getTo(), Andrea.getNickname());

        for(String worker : packetPossibleBuilds.getPossibleBuilds().keySet()){
            if(worker.equals(AndreaW1.getID())) assertEquals(packetPossibleBuilds.getPossibleBuilds().get(worker), possibleBuilds);
            else if(worker.equals(AndreaW2.getID())) assertEquals(packetPossibleBuilds.getPossibleBuilds().get(worker).size(), 0);
            else assert false;
        }

        //WE CHECK THAT A SECOND BUILDING IS POSSIBLE
        possibleBuilds = new HashMap<>();
        buildsHelper = new ArrayList<>();
        buildsHelper.add(BuildingType.FIRST_FLOOR);
        possibleBuilds.put(point23, buildsHelper);
        possibleBuilds.put(point03, buildsHelper);
        possibleBuilds.put(point01, buildsHelper);
        possibleBuilds.put(point21, buildsHelper);

        buildings.add(BuildingType.FIRST_FLOOR);
        builds.put(point13, buildings);
        buildsOrder.add(point13);
        packetBuild = new PacketBuild(Andrea.getNickname(),AndreaW1.getID(),builds, buildsOrder);
        turnLogic.getPossibleBuilds(Andrea.getNickname(),packetBuild);
        packetPossibleBuilds = mockView.getPacketPossibleBuilds();
        assertEquals(packetPossibleBuilds.getTo(), Andrea.getNickname());

        for(String worker : packetPossibleBuilds.getPossibleBuilds().keySet()){
            if(worker.equals(AndreaW1.getID())) assertEquals(packetPossibleBuilds.getPossibleBuilds().get(worker), possibleBuilds);
            else if(worker.equals(AndreaW2.getID())) assertEquals(packetPossibleBuilds.getPossibleBuilds().get(worker).size(), 0);
            else assert false;
        }

        //WE CONSUME THE BUILD
        builds = new HashMap<>();
        buildsOrder = new ArrayList<>();
        buildings = new ArrayList<>();
        buildings.add(BuildingType.FIRST_FLOOR);
        builds.put(point13, buildings);
        builds.put(point23, buildings);
        buildsOrder.add(point13);
        buildsOrder.add(point23);

        packetBuild = new PacketBuild(Andrea.getNickname(), AndreaW1.getID(), builds, buildsOrder);

        try {
            turnLogic.consumePacketBuild(Andrea.getNickname(),packetBuild);
        } catch (InvalidPacketException e) {
            assert false;
        }

        assertEquals(Andrea.getState(), PlayerState.BUILT);
        assertSame(model.getBoard().getCell(point13).getTopBuilding(), LevelType.FIRST_FLOOR);
        assertSame(model.getBoard().getCell(point23).getTopBuilding(), LevelType.FIRST_FLOOR);

        packetUpdateBoard = mockView.getPacketUpdateBoard();
        for(Point pos : packetUpdateBoard.getNewBuildings().keySet()){
            List<BuildingType> helper = packetUpdateBoard.getNewBuildings().get(pos);
            assertEquals(model.getBoard().getCell(pos).getTopBuilding().toString(), helper.get(helper.size() - 1).toString());
        }

        //RESET

        //MOVE UTILS
        moves = new ArrayList<>();
        possibleMovesW1 = new HashSet<>();
        possibleMovesW2 = new HashSet<>();

        //BUILDS UTILS
        buildings = new ArrayList<>();
        builds = new HashMap<>();
        buildsOrder = new ArrayList<>();
        possibleBuilds = new HashMap<>();
        buildsHelper = new ArrayList<>();


        //WE CHECK THE NEXT PLAYER. IT SHOULD BE MATTEO

        packetDoAction = mockView.getPacketDoAction();
        assertEquals(packetDoAction.getTo(), Matteo.getNickname());
        assertEquals(packetDoAction.getActionType(), ActionType.MOVE);

        //MATTEO W1 MOVES INTO 2,1
        /*

                  0    1     2    3    4   X
                +----+----+----+----+----+
            0   |    |    |  x | x  | x  |
                +----+----+----+----+----+
            1   |    |    |    |    |    |
                |    | D1 | X  <- M1| x  |
                +----+----+----+----+----+
            2   |    |    |    |    |    |
                |A2  | A1 | D2 | x  | x  |
                +----+----+----+----+----+
            3   |    | FF | xFF| M2 | x  |
                +----+----+----+----+----+
            4   |    |    | x  | x  | x  |
                +----+----+----+----+----+
            Y
        */

        possibleMovesW1.add(point20);
        possibleMovesW1.add(point30);
        possibleMovesW1.add(point40);
        possibleMovesW1.add(point21);
        possibleMovesW1.add(point41);
        possibleMovesW1.add(point32);
        possibleMovesW1.add(point42);

        possibleMovesW2.add(point32);
        possibleMovesW2.add(point42);
        possibleMovesW2.add(point23);
        possibleMovesW2.add(point24);
        possibleMovesW2.add(point34);
        possibleMovesW2.add(point43);
        possibleMovesW2.add(point44);

        packetMove = new PacketMove(Matteo.getNickname(),null,moves);
        turnLogic.getPossibleMoves(Matteo.getNickname(), packetMove);
        packetPossibleMoves = mockView.getPacketPossibleMoves();
        assertEquals(packetPossibleMoves.getTo(), Matteo.getNickname());

        for(String worker : packetPossibleMoves.getPossibleMoves().keySet()){
            if(worker.equals(MatteoW1.getID())) assertEquals(packetPossibleMoves.getPossibleMoves().get(worker), possibleMovesW1);
            else if(worker.equals(MatteoW2.getID())) assertEquals(packetPossibleMoves.getPossibleMoves().get(worker), possibleMovesW2);
            else assert false;
        }

        //WE CHECK THAT AFTER THE WANTED MOVE THERE ARE NO MORE POSSIBLE MOVES

        moves.add(point21);
        packetMove = new PacketMove(Matteo.getNickname(), MatteoW1.getID(), moves);
        turnLogic.getPossibleMoves(Matteo.getNickname(), packetMove);
        packetPossibleMoves = mockView.getPacketPossibleMoves();
        assertEquals(packetPossibleMoves.getTo(), Matteo.getNickname());
        for(String worker : packetPossibleMoves.getPossibleMoves().keySet()){
            assertEquals(packetPossibleMoves.getPossibleMoves().get(worker).size(), 0);
        }

        //WE CONSUME THE MOVE

        try {
            turnLogic.consumePacketMove(Matteo.getNickname(),packetMove);
        } catch (InvalidPacketException e) {
            assert false;
        }
        assertEquals(Matteo.getState(), PlayerState.MOVED);
        assertEquals(point21, MatteoW1.getPosition());
        assertEquals(MatteoW1.getID(), model.getBoard().getCell(point21).getWorkerID());
        assertNull(model.getBoard().getCell(point31).getWorkerID());

        packetUpdateBoard = mockView.getPacketUpdateBoard();
        for(String worker : packetUpdateBoard.getWorkersPositions().keySet()){
            assertEquals(model.getWorkerByID(worker).getPosition(), packetUpdateBoard.getWorkersPositions().get(worker));
        }

        //MATTEO SHOULD BUILD WITH W1 NOW

        packetDoAction = mockView.getPacketDoAction();
        assertEquals(packetDoAction.getTo(), Matteo.getNickname());
        assertEquals(packetDoAction.getActionType(), ActionType.BUILD);

        //WE SET THE BUILDINGS SO THAT MIRKO'S WORKER CAN'T MOVE IN HIS TURN

        model.getBoard().getCell(point00).addBuilding(BuildingType.DOME);
        model.getBoard().getCell(point10).addBuilding(BuildingType.DOME);
        model.getBoard().getCell(point01).addBuilding(BuildingType.DOME);
        model.getBoard().getCell(point31).addBuilding(BuildingType.DOME);
        model.getBoard().getCell(point32).addBuilding(BuildingType.DOME);
        model.getBoard().getCell(point03).addBuilding(BuildingType.DOME);
        model.getBoard().getCell(point13).addBuilding(BuildingType.DOME);
        model.getBoard().getCell(point23).addBuilding(BuildingType.DOME);

        model.getBoard().getCell(point33).removeWorker();
        model.getBoard().getCell(point44).setWorker(MatteoW2.getID());
        MatteoW2.setPosition(point44);
        model.getBoard().getCell(point33).addBuilding(BuildingType.DOME);

        //WE SET A THIRD FLOOR IN 2,0 SO THAT MATTEO CAN BUILD A DOME

        model.getBoard().getCell(point20).addBuilding(BuildingType.FIRST_FLOOR);
        model.getBoard().getCell(point20).addBuilding(BuildingType.SECOND_FLOOR);
        model.getBoard().getCell(point20).addBuilding(BuildingType.THIRD_FLOOR);

        //MATTEO W1 BUILDS INTO 2,0
        /*

                  0    1     2    3    4   X
                +----+----+----+----+----+
            0   | C  |  C |  XC| x  |    |
                +----+----+----+----+----+
            1   | C  |    |    |    |    |
                |    | D1 | M1 | C  |    |
                +----+----+----+----+----+
            2   |    |    |    |    |    |
                |A2  | A1 | D2 | C  |    |
                +----+----+----+----+----+
            3   | C  | C  | C  | C  |    |
                +----+----+----+----+----+
            4   |    |    |    |    | M2 |
                +----+----+----+----+----+
            Y
        */

        //FIRST WE CHECK ALL THE POSSIBLE BUILDS
        buildsHelper.add(BuildingType.DOME);
        possibleBuilds.put(point20, buildsHelper);
        buildsHelper = new ArrayList<>();
        buildsHelper.add(BuildingType.FIRST_FLOOR);
        possibleBuilds.put(point30, buildsHelper);

        packetBuild = new PacketBuild(Matteo.getNickname(), MatteoW1.getID(),builds, buildsOrder);
        turnLogic.getPossibleBuilds(Matteo.getNickname(), packetBuild);
        packetPossibleBuilds = mockView.getPacketPossibleBuilds();
        assertEquals(packetPossibleBuilds.getTo(), Matteo.getNickname());

        for(String worker : packetPossibleBuilds.getPossibleBuilds().keySet()){
            if(worker.equals(MatteoW1.getID())) assertEquals(packetPossibleBuilds.getPossibleBuilds().get(worker), possibleBuilds);
            else if(worker.equals(MatteoW2.getID())) assertEquals(packetPossibleBuilds.getPossibleBuilds().get(worker).size(), 0);
            else assert false;
        }

        //WE CHECK THAT A SECOND BUILD IS NOT POSSIBLE
        buildings.add(BuildingType.DOME);
        builds.put(point20, buildings);
        buildsOrder.add(point20);
        packetBuild = new PacketBuild(Matteo.getNickname(),MatteoW1.getID(),builds, buildsOrder);
        turnLogic.getPossibleBuilds(Matteo.getNickname(),packetBuild);
        packetPossibleBuilds = mockView.getPacketPossibleBuilds();
        assertEquals(packetPossibleBuilds.getTo(), Matteo.getNickname());

        for(String worker : packetPossibleBuilds.getPossibleBuilds().keySet()){
            assertEquals(packetPossibleBuilds.getPossibleBuilds().get(worker).size(), 0);
        }

        //WE CONSUME THE BUILD

        try {
            turnLogic.consumePacketBuild(Matteo.getNickname(),packetBuild);
        } catch (InvalidPacketException e) {
            assert false;
        }

        assertEquals(Matteo.getState(), PlayerState.BUILT);
        assertSame(model.getBoard().getCell(point20).getTopBuilding(), LevelType.DOME);

        packetUpdateBoard = mockView.getPacketUpdateBoard();
        for(Point pos : packetUpdateBoard.getNewBuildings().keySet()){
            List<BuildingType> helper = packetUpdateBoard.getNewBuildings().get(pos);
            assertEquals(model.getBoard().getCell(pos).getTopBuilding().toString(), helper.get(helper.size() - 1).toString());
        }

        //MIRKO SHOULD LOSE BECAUSE HIS WORKERS CAN'T MOVE ANYMORE
        packetUpdateBoard = mockView.getPacketUpdateBoard();
        assertEquals(packetUpdateBoard.getPlayerLostID(), Mirko.getNickname());
        assertTrue(model.getLosers().contains(Mirko));

        assertNull(model.getBoard().getCell(point11).getWorkerID());
        assertNull(model.getBoard().getCell(point22).getWorkerID());

        //RESET

        //MOVE UTILS
        moves = new ArrayList<>();
        possibleMovesW1 = new HashSet<>();
        possibleMovesW2 = new HashSet<>();

        //BUILDS UTILS
        buildings = new ArrayList<>();
        builds = new HashMap<>();
        buildsOrder = new ArrayList<>();
        possibleBuilds = new HashMap<>();
        buildsHelper = new ArrayList<>();


        //WE CHECK THE NEXT PLAYER. HE SHOULD BE ANDREA

        packetDoAction = mockView.getPacketDoAction();
        assertEquals(packetDoAction.getTo(), Andrea.getNickname());
        assertEquals(packetDoAction.getActionType(), ActionType.MOVE);

        //WE SET DOMES SO THAT MATTEO WONT'B BE ABLE TO MOVE

        model.getBoard().getCell(point30).addBuilding(BuildingType.DOME);
        model.getBoard().getCell(point43).addBuilding(BuildingType.DOME);
        model.getBoard().getCell(point34).addBuilding(BuildingType.DOME);

        model.getBoard().getCell(point12).removeWorker();
        model.getBoard().getCell(point12).addBuilding(BuildingType.FIRST_FLOOR);
        model.getBoard().getCell(point12).addBuilding(BuildingType.SECOND_FLOOR);
        model.getBoard().getCell(point12).addBuilding(BuildingType.THIRD_FLOOR);
        model.getBoard().getCell(point12).setWorker(AndreaW1.getID());

        model.getBoard().getCell(point11).addBuilding(BuildingType.FIRST_FLOOR);
        model.getBoard().getCell(point11).addBuilding(BuildingType.SECOND_FLOOR);
        model.getBoard().getCell(point11).addBuilding(BuildingType.THIRD_FLOOR);


        //ANDREA'S W1 MOVES INTO 2,2
        /*

                  0    1     2    3    4   X
                +----+----+----+----+----+
            0   | C  |  C |  C | C  |    |
                +----+----+----+----+----+
            1   | C  |    |    |    |    |
                |    | xTF| M1 | C  |    |
                +----+----+----+----+----+
            2   |    | TF |    |    |    |
                |A2  | A1 ->x  | C  |    |
                +----+----+----+----+----+
            3   | C  | C  | C  | C  | C  |
                +----+----+----+----+----+
            4   |    |    |    | C  | M2 |
                +----+----+----+----+----+
            Y
        */

        possibleMovesW1.add(point22);
        possibleMovesW1.add(point11);

        packetMove = new PacketMove(Andrea.getNickname(),AndreaW1.getID(),moves);
        turnLogic.getPossibleMoves(Andrea.getNickname(), packetMove);
        packetPossibleMoves = mockView.getPacketPossibleMoves();
        assertEquals(packetPossibleMoves.getTo(), Andrea.getNickname());

        for(String worker : packetPossibleMoves.getPossibleMoves().keySet()){
            if(worker.equals(AndreaW1.getID())) assertEquals(packetPossibleMoves.getPossibleMoves().get(worker), possibleMovesW1);
            else if(worker.equals(AndreaW2.getID())) assertEquals(packetPossibleMoves.getPossibleMoves().get(worker), possibleMovesW2);
            else assert false;
        }

        //WE CHECK THAT AFTER THE WANTED MOVE THERE ARE NO MORE POSSIBLE MOVES

        moves.add(point22);
        packetMove = new PacketMove(Andrea.getNickname(), AndreaW1.getID(), moves);
        turnLogic.getPossibleMoves(Andrea.getNickname(), packetMove);
        packetPossibleMoves = mockView.getPacketPossibleMoves();
        assertEquals(packetPossibleMoves.getTo(), Andrea.getNickname());
        for(String worker : packetPossibleMoves.getPossibleMoves().keySet()){
            assertEquals(packetPossibleMoves.getPossibleMoves().get(worker).size(), 0);
        }

        //WE CONSUME THE MOVE

        try {
            turnLogic.consumePacketMove(Andrea.getNickname(),packetMove);
        } catch (InvalidPacketException e) {
            assert false;
        }
        assertEquals(Andrea.getState(), PlayerState.MOVED);
        assertEquals(point22, AndreaW1.getPosition());
        assertEquals(AndreaW1.getID(), model.getBoard().getCell(point22).getWorkerID());
        assertNull(model.getBoard().getCell(point12).getWorkerID());

        packetUpdateBoard = mockView.getPacketUpdateBoard();
        for(String worker : packetUpdateBoard.getWorkersPositions().keySet()){
            assertEquals(model.getWorkerByID(worker).getPosition(), packetUpdateBoard.getWorkersPositions().get(worker));
        }

        packetDoAction = mockView.getPacketDoAction();
        assertEquals(packetDoAction.getTo(), Andrea.getNickname());
        assertEquals(packetDoAction.getActionType(), ActionType.BUILD);

        //ANDREA'S W1 BUILDS A DOME INTO 1,2 AND ONE INTO 1,1
        /*

                  0    1     2    3    4   X
                +----+----+----+----+----+
            0   | C  |  C |  C |  C |    |
                +----+----+----+----+----+
            1   | C  |    |    |    |    |
                |    | xC | M1 | C  |    |
                +----+----+----+----+----+
            2   |    | XC |    |    |    |
                |A2  |    | A1 | C  |    |
                +----+----+----+----+----+
            3   | C  | C  | C  | C  | C  |
                +----+----+----+----+----+
            4   |    |    |    | C  | M2 |
                +----+----+----+----+----+
            Y
        */

        //FIRST WE CHECK ALL THE POSSIBLE BUILDS
        buildsHelper.add(BuildingType.DOME);
        possibleBuilds.put(point11, buildsHelper);
        possibleBuilds.put(point12, buildsHelper);


        packetBuild = new PacketBuild(Andrea.getNickname(), AndreaW1.getID(),builds, buildsOrder);
        turnLogic.getPossibleBuilds(Andrea.getNickname(), packetBuild);
        packetPossibleBuilds = mockView.getPacketPossibleBuilds();
        assertEquals(packetPossibleBuilds.getTo(), Andrea.getNickname());

        for(String worker : packetPossibleBuilds.getPossibleBuilds().keySet()){
            if(worker.equals(AndreaW1.getID())) assertEquals(packetPossibleBuilds.getPossibleBuilds().get(worker), possibleBuilds);
            else if(worker.equals(AndreaW2.getID())) assertEquals(packetPossibleBuilds.getPossibleBuilds().get(worker).size(), 0);
            else assert false;
        }

        //WE CHECK THAT A SECOND BUILDING IS POSSIBLE
        possibleBuilds = new HashMap<>();
        buildsHelper = new ArrayList<>();
        buildsHelper.add(BuildingType.DOME);
        possibleBuilds.put(point11, buildsHelper);

        buildings.add(BuildingType.DOME);
        builds.put(point12, buildings);
        buildsOrder.add(point12);
        packetBuild = new PacketBuild(Andrea.getNickname(),AndreaW1.getID(),builds, buildsOrder);
        turnLogic.getPossibleBuilds(Andrea.getNickname(),packetBuild);
        packetPossibleBuilds = mockView.getPacketPossibleBuilds();
        assertEquals(packetPossibleBuilds.getTo(), Andrea.getNickname());

        for(String worker : packetPossibleBuilds.getPossibleBuilds().keySet()){
            if(worker.equals(AndreaW1.getID())) assertEquals(packetPossibleBuilds.getPossibleBuilds().get(worker), possibleBuilds);
            else if(worker.equals(AndreaW2.getID())) assertEquals(packetPossibleBuilds.getPossibleBuilds().get(worker).size(), 0);
            else assert false;
        }

        //WE CONSUME THE BUILD
        builds = new HashMap<>();
        buildsOrder = new ArrayList<>();
        buildings = new ArrayList<>();
        buildings.add(BuildingType.DOME);
        builds.put(point12, buildings);
        builds.put(point11, buildings);
        buildsOrder.add(point12);
        buildsOrder.add(point11);

        packetBuild = new PacketBuild(Andrea.getNickname(), AndreaW1.getID(), builds, buildsOrder);

        try {
            turnLogic.consumePacketBuild(Andrea.getNickname(),packetBuild);
        } catch (InvalidPacketException e) {
            assert false;
        }

        assertEquals(Andrea.getState(), PlayerState.BUILT);
        assertSame(model.getBoard().getCell(point11).getTopBuilding(), LevelType.DOME);
        assertSame(model.getBoard().getCell(point12).getTopBuilding(), LevelType.DOME);

        packetUpdateBoard = mockView.getPacketUpdateBoard();
        for(Point pos : packetUpdateBoard.getNewBuildings().keySet()){
            List<BuildingType> helper = packetUpdateBoard.getNewBuildings().get(pos);
            assertEquals(model.getBoard().getCell(pos).getTopBuilding().toString(), helper.get(helper.size() - 1).toString());
        }

        //MATTEO SHOULD LOSE BECAUSE HIS WORKERS CAN'T MOVE ANYMORE
        packetUpdateBoard = mockView.getPacketUpdateBoard();
        assertEquals(packetUpdateBoard.getPlayerLostID(), Matteo.getNickname());
        assertTrue(model.getLosers().contains(Matteo));

        assertNull(model.getBoard().getCell(point21).getWorkerID());
        assertNull(model.getBoard().getCell(point44).getWorkerID());

        //ANDREA SHOULD HAVE WON BECAUSE HE IS THE LAST PLAYER IN GAME
        packetUpdateBoard = mockView.getPacketUpdateBoard();
        assertEquals(packetUpdateBoard.getPlayerWonID(), Andrea.getNickname());
        assertEquals(model.getWinner(),Andrea);

    }

    /**
     * getPossibleMoves should give the possible moves after the first one, but the latter shouldn't be one of them.
     */
    @Test
    void incrementalMovesArtemis(){
        /*

                  0    1     2    3    4   X
                +----+----+----+----+----+
            0   |    |    |    |    |    |
                +----+----+----+----+----+
            1   |    |    |    |    |    |
                |    | D1 |    | M1 |    |
                +----+----+----+----+----+
            2   |    |    |    |    |    |
                |A2  |    | D2 |    |    |
                +----+----+----+----+----+
            3   |    | A1 |    | M2 |    |
                +----+----+----+----+----+
            4   |    |    |    |    |    |
                +----+----+----+----+----+
            Y
        */

        //MOVE UTILS
        List<Point> moves = new ArrayList<>();
        PacketMove packetMove;
        Set<Point> possibleMovesW1 = new HashSet<>();
        Set<Point> possibleMovesW2 = new HashSet<>();

        //BUILDS UTILS
        List<BuildingType> buildings = new ArrayList<>();
        Map<Point, List<BuildingType>> builds = new HashMap<>();
        List<Point> buildsOrder = new ArrayList<>();
        PacketBuild packetBuild;
        Map<Point, List<BuildingType>> possibleBuilds = new HashMap<>();
        List<BuildingType> buildsHelper = new ArrayList<>();

        // Initializing cards
        CardFile pan = cardFactory.getCards().stream().filter(x -> x.getName().equals("Pan")).findFirst().orElse(null);
        CardFile artemis = cardFactory.getCards().stream().filter(x -> x.getName().equals("Artemis")).findFirst().orElse(null);
        CardFile minotaur = cardFactory.getCards().stream().filter(x -> x.getName().equals("Minotaur")).findFirst().orElse(null);
        Matteo.setCard(pan); //MX
        Andrea.setCard(artemis); //AX
        Mirko.setCard(minotaur); //DX
        model.compileCardStrategy();

        model.getBoard().getCell(point11).setWorker(MirkoW1.getID());
        MirkoW1.setPosition(point11);
        model.getBoard().getCell(point22).setWorker(MirkoW2.getID());
        MirkoW2.setPosition(point22);

        model.getBoard().getCell(point31).setWorker(MatteoW1.getID());
        MatteoW1.setPosition(point31);
        model.getBoard().getCell(point33).setWorker(MatteoW2.getID());
        MatteoW2.setPosition(point33);

        model.getBoard().getCell(point13).setWorker(AndreaW1.getID());
        AndreaW1.setPosition(point13);
        model.getBoard().getCell(point02).setWorker(AndreaW2.getID());
        AndreaW2.setPosition(point02);

        turnLogic.start();

        //FIRST MOVE: ANDREA MOVES INTO 1,2
        /*

                  0    1     2    3    4   X
                +----+----+----+----+----+
            0   |    |    |    |    |    |
                +----+----+----+----+----+
            1   |    |    |    |    |    |
                | xy |  D1|  y | M1 |    |
                +----+----+----+----+----+
            2   |    |    |    |    |    |
                |A2  | X  | D2 |    |    |
                +----+--↑-+----+----+----+
            3   | xy | A1 | xy | M2 |    |
                +----+----+----+----+----+
            4   |  x | x  | x  |    |    |
                +----+----+----+----+----+
            Y
        */

        //FIRST WE CHECK ALL THE POSSIBLE MOVES
        possibleMovesW1.add(point12);
        possibleMovesW1.add(point23);
        possibleMovesW1.add(point03);
        possibleMovesW1.add(point04);
        possibleMovesW1.add(point14);
        possibleMovesW1.add(point24);

        possibleMovesW2.add(point01);
        possibleMovesW2.add(point03);
        possibleMovesW2.add(point12);

        PacketDoAction packetDoAction = mockView.getPacketDoAction();
        assertEquals(packetDoAction.getTo(), Andrea.getNickname());
        assertEquals(packetDoAction.getActionType(), ActionType.MOVE);

        packetMove = new PacketMove(Andrea.getNickname(),null,moves);
        turnLogic.getPossibleMoves(Andrea.getNickname(), packetMove);
        PacketPossibleMoves packetPossibleMoves = mockView.getPacketPossibleMoves();
        assertEquals(packetPossibleMoves.getTo(), Andrea.getNickname());

        for(String worker : packetPossibleMoves.getPossibleMoves().keySet()){
            if(worker.equals(AndreaW1.getID())) assertEquals(packetPossibleMoves.getPossibleMoves().get(worker), possibleMovesW1);
            else if(worker.equals(AndreaW2.getID())) assertEquals(packetPossibleMoves.getPossibleMoves().get(worker), possibleMovesW2);
            else assert false;
        }

        //WE CHECK THAT AFTER THE WANTED MOVE THERE ARE NO MORE POSSIBLE MOVES
        possibleMovesW1 = new HashSet<>();
        possibleMovesW2 = new HashSet<>();


        //AFTER
        moves.add(point12);
        //THESE MOVES ARE POSSIBLE BECAUSE ANDREA HAS ARTEMIS AS GODCARD
        possibleMovesW1.add(point01);
        possibleMovesW1.add(point21);
        possibleMovesW1.add(point23);
        possibleMovesW1.add(point03);


        packetMove = new PacketMove(Andrea.getNickname(), AndreaW1.getID(), moves);
        turnLogic.getPossibleMoves(Andrea.getNickname(), packetMove);
        packetPossibleMoves = mockView.getPacketPossibleMoves();
        assertEquals(packetPossibleMoves.getTo(), Andrea.getNickname());

        for(String worker : packetPossibleMoves.getPossibleMoves().keySet()){
            if(worker.equals(AndreaW1.getID())) assertEquals(packetPossibleMoves.getPossibleMoves().get(worker), possibleMovesW1);
            else if(worker.equals(AndreaW2.getID())) assertEquals(packetPossibleMoves.getPossibleMoves().get(worker), possibleMovesW2);
            else assert false;
        }

        //WE CONSUME THE MOVE
        moves.add(point01);
        packetMove = new PacketMove(Andrea.getNickname(), AndreaW1.getID(), moves);

        try {
            turnLogic.consumePacketMove(Andrea.getNickname(),packetMove);
        } catch (InvalidPacketException e) {
            assert false;
        }
        assertEquals(Andrea.getState(), PlayerState.MOVED);
        assertEquals(point01, AndreaW1.getPosition());
        assertEquals(AndreaW1.getID(), model.getBoard().getCell(point01).getWorkerID());
        assertNull(model.getBoard().getCell(point12).getWorkerID());
        assertNull(model.getBoard().getCell(point13).getWorkerID());

        PacketUpdateBoard packetUpdateBoard = mockView.getPacketUpdateBoard();
        for(String worker : packetUpdateBoard.getWorkersPositions().keySet()){
            assertEquals(model.getWorkerByID(worker).getPosition(), packetUpdateBoard.getWorkersPositions().get(worker));
        }

        packetDoAction = mockView.getPacketDoAction();
        assertEquals(packetDoAction.getTo(), Andrea.getNickname());
        assertEquals(packetDoAction.getActionType(), ActionType.BUILD);

        /*

                  0    1     2    3    4   X
                +----+----+----+----+----+
            0   | XFF| x  |    |    |    |
                +----+----+----+----+----+
            1   |    |    |    |    |    |
                | A1 |  D1|    | M1 |    |
                +----+----+----+----+----+
            2   |    |    |    |    |    |
                |A2  | x  | D2 |    |    |
                +----+----+----+----+----+
            3   |    |    |    | M2 |    |
                +----+----+----+----+----+
            4   |    |    |    |    |    |
                +----+----+----+----+----+
            Y
        */

        //FIRST WE CHECK ALL THE POSSIBLE BUILDS
        buildsHelper.add(BuildingType.FIRST_FLOOR);
        possibleBuilds.put(point00, buildsHelper);
        possibleBuilds.put(point10, buildsHelper);
        possibleBuilds.put(point12, buildsHelper);


        packetBuild = new PacketBuild(Andrea.getNickname(), AndreaW1.getID(),builds, buildsOrder);
        turnLogic.getPossibleBuilds(Andrea.getNickname(), packetBuild);
        PacketPossibleBuilds packetPossibleBuilds = mockView.getPacketPossibleBuilds();
        assertEquals(packetPossibleBuilds.getTo(), Andrea.getNickname());

        for(String worker : packetPossibleBuilds.getPossibleBuilds().keySet()){
            if(worker.equals(AndreaW1.getID())) assertEquals(packetPossibleBuilds.getPossibleBuilds().get(worker), possibleBuilds);
            else if(worker.equals(AndreaW2.getID())) assertEquals(packetPossibleBuilds.getPossibleBuilds().get(worker).size(), 0);
            else assert false;
        }



        //WE CHECK THAT A SECOND BUILDING IS NOT POSSIBLE

        buildings.add(BuildingType.FIRST_FLOOR);
        builds.put(point00, buildings);
        buildsOrder.add(point00);
        packetBuild = new PacketBuild(Andrea.getNickname(),AndreaW1.getID(),builds, buildsOrder);
        turnLogic.getPossibleBuilds(Andrea.getNickname(),packetBuild);
        packetPossibleBuilds = mockView.getPacketPossibleBuilds();
        assertEquals(packetPossibleBuilds.getTo(), Andrea.getNickname());

        for(String worker : packetPossibleBuilds.getPossibleBuilds().keySet()){
            assertEquals(packetPossibleBuilds.getPossibleBuilds().get(worker).size(), 0);
        }

        //WE CONSUME THE BUILD

        packetBuild = new PacketBuild(Andrea.getNickname(), AndreaW1.getID(), builds, buildsOrder);

        try {
            turnLogic.consumePacketBuild(Andrea.getNickname(),packetBuild);
        } catch (InvalidPacketException e) {
            assert false;
        }

        assertEquals(Andrea.getState(), PlayerState.BUILT);
        assertSame(model.getBoard().getCell(point00).getTopBuilding(), LevelType.FIRST_FLOOR);

        packetUpdateBoard = mockView.getPacketUpdateBoard();
        for(Point pos : packetUpdateBoard.getNewBuildings().keySet()){
            List<BuildingType> helper = packetUpdateBoard.getNewBuildings().get(pos);
            assertEquals(model.getBoard().getCell(pos).getTopBuilding().toString(), helper.get(helper.size() - 1).toString());
        }

    }

    /**
     * getPossibleBuilds should give a possible build on the same spot of the previous one.
     * Case: Hephaestus wants to build a SF after a FF.
     */
    @Test
    void incrementalBuildsHephaestus(){
         /*

                  0    1     2    3    4   X
                +----+----+----+----+----+
            0   |    |    |    |    |    |
                +----+----+----+----+----+
            1   |    |    |    |    |    |
                |    | D1 |    | M1 |    |
                +----+----+----+----+----+
            2   |    |    |    |    |    |
                |A2  |    | D2 |    |    |
                +----+----+----+----+----+
            3   |    | A1 |    | M2 |    |
                +----+----+----+----+----+
            4   |    |    |    |    |    |
                +----+----+----+----+----+
            Y
        */

        //MOVE UTILS
        List<Point> moves = new ArrayList<>();
        PacketMove packetMove;
        Set<Point> possibleMovesW1 = new HashSet<>();
        Set<Point> possibleMovesW2 = new HashSet<>();

        //BUILDS UTILS
        List<BuildingType> buildings = new ArrayList<>();
        Map<Point, List<BuildingType>> builds = new HashMap<>();
        List<Point> buildsOrder = new ArrayList<>();
        PacketBuild packetBuild;
        Map<Point, List<BuildingType>> possibleBuilds = new HashMap<>();
        List<BuildingType> buildsHelper = new ArrayList<>();

        // Initializing cards
        CardFile pan = cardFactory.getCards().stream().filter(x -> x.getName().equals("Pan")).findFirst().orElse(null);
        CardFile hephaestus = cardFactory.getCards().stream().filter(x -> x.getName().equals("Hephaestus")).findFirst().orElse(null);
        CardFile minotaur = cardFactory.getCards().stream().filter(x -> x.getName().equals("Minotaur")).findFirst().orElse(null);
        Matteo.setCard(pan); //MX
        Andrea.setCard(hephaestus); //AX
        Mirko.setCard(minotaur); //DX
        model.compileCardStrategy();

        model.getBoard().getCell(point11).setWorker(MirkoW1.getID());
        MirkoW1.setPosition(point11);
        model.getBoard().getCell(point22).setWorker(MirkoW2.getID());
        MirkoW2.setPosition(point22);

        model.getBoard().getCell(point31).setWorker(MatteoW1.getID());
        MatteoW1.setPosition(point31);
        model.getBoard().getCell(point33).setWorker(MatteoW2.getID());
        MatteoW2.setPosition(point33);

        model.getBoard().getCell(point13).setWorker(AndreaW1.getID());
        AndreaW1.setPosition(point13);
        model.getBoard().getCell(point02).setWorker(AndreaW2.getID());
        AndreaW2.setPosition(point02);

        turnLogic.start();

        //FIRST MOVE: ANDREA MOVES INTO 2,3
        /*

                  0    1     2    3    4   X
                +----+----+----+----+----+
            0   |    |    |    |    |    |
                +----+----+----+----+----+
            1   |    |    |    |    |    |
                | x  | D1 |    | M1 |    |
                +----+----+----+----+----+
            2   |    |    |    |    |    |
                |A2  | x  | D2 |    |    |
                +----+----+----+----+----+
            3   | x  | A1 -> X | M2 |    |
                +----+----+----+----+----+
            4   |  x | x  | x  |    |    |
                +----+----+----+----+----+
            Y
        */

        //FIRST WE CHECK ALL THE POSSIBLE MOVES
        possibleMovesW1.add(point12);
        possibleMovesW1.add(point23);
        possibleMovesW1.add(point03);
        possibleMovesW1.add(point04);
        possibleMovesW1.add(point14);
        possibleMovesW1.add(point24);

        possibleMovesW2.add(point01);
        possibleMovesW2.add(point03);
        possibleMovesW2.add(point12);

        PacketDoAction packetDoAction = mockView.getPacketDoAction();
        assertEquals(packetDoAction.getTo(), Andrea.getNickname());
        assertEquals(packetDoAction.getActionType(), ActionType.MOVE);

        packetMove = new PacketMove(Andrea.getNickname(),null,moves);
        turnLogic.getPossibleMoves(Andrea.getNickname(), packetMove);
        PacketPossibleMoves packetPossibleMoves = mockView.getPacketPossibleMoves();
        assertEquals(packetPossibleMoves.getTo(), Andrea.getNickname());

        for(String worker : packetPossibleMoves.getPossibleMoves().keySet()){
            if(worker.equals(AndreaW1.getID())) assertEquals(packetPossibleMoves.getPossibleMoves().get(worker), possibleMovesW1);
            else if(worker.equals(AndreaW2.getID())) assertEquals(packetPossibleMoves.getPossibleMoves().get(worker), possibleMovesW2);
            else assert false;
        }

        //WE CHECK THAT AFTER THE WANTED MOVE THERE ARE NO MORE POSSIBLE MOVES

        moves.add(point23);
        packetMove = new PacketMove(Andrea.getNickname(), AndreaW1.getID(), moves);
        turnLogic.getPossibleMoves(Andrea.getNickname(), packetMove);
        packetPossibleMoves = mockView.getPacketPossibleMoves();
        assertEquals(packetPossibleMoves.getTo(), Andrea.getNickname());
        for(String worker : packetPossibleMoves.getPossibleMoves().keySet()){
            assertEquals(packetPossibleMoves.getPossibleMoves().get(worker).size(), 0);
        }

        //WE CONSUME THE MOVE

        try {
            turnLogic.consumePacketMove(Andrea.getNickname(),packetMove);
        } catch (InvalidPacketException e) {
            assert false;
        }
        assertEquals(Andrea.getState(), PlayerState.MOVED);
        assertEquals(point23, AndreaW1.getPosition());
        assertEquals(AndreaW1.getID(), model.getBoard().getCell(point23).getWorkerID());
        assertNull(model.getBoard().getCell(point13).getWorkerID());

        PacketUpdateBoard packetUpdateBoard = mockView.getPacketUpdateBoard();
        for(String worker : packetUpdateBoard.getWorkersPositions().keySet()){
            assertEquals(model.getWorkerByID(worker).getPosition(), packetUpdateBoard.getWorkersPositions().get(worker));
        }

        packetDoAction = mockView.getPacketDoAction();
        assertEquals(packetDoAction.getTo(), Andrea.getNickname());
        assertEquals(packetDoAction.getActionType(), ActionType.BUILD);

        //FIRST BUILD: ANDREA BUILDS A FIRST_FLOOR INTO 1,3 AND A FIRST FLOOR INTO 2,3
        /*

                  0    1     2    3    4   X
                +----+----+----+----+----+
            0   |    |    |    |    |    |
                +----+----+----+----+----+
            1   |    |    |    |    |    |
                |    | D1 |    | M1 |    |
                +----+----+----+----+----+
            2   |    |    |    |    |    |
                |A2  | x  | D2 | x  |    |
                +----+----+----+----+----+
            3   |    | XFF| A1 | M2 |    |
                +----+----+----+----+----+
            4   |    |  x | x  |  x |    |
                +----+----+----+----+----+
            Y
        */

        //FIRST WE CHECK ALL THE POSSIBLE BUILDS
        buildsHelper.add(BuildingType.FIRST_FLOOR);
        possibleBuilds.put(point12, buildsHelper);
        possibleBuilds.put(point32, buildsHelper);
        possibleBuilds.put(point13, buildsHelper);
        possibleBuilds.put(point14, buildsHelper);
        possibleBuilds.put(point24, buildsHelper);
        possibleBuilds.put(point34, buildsHelper);


        packetBuild = new PacketBuild(Andrea.getNickname(), AndreaW1.getID(),builds, buildsOrder);
        turnLogic.getPossibleBuilds(Andrea.getNickname(), packetBuild);
        PacketPossibleBuilds packetPossibleBuilds = mockView.getPacketPossibleBuilds();
        assertEquals(packetPossibleBuilds.getTo(), Andrea.getNickname());

        for(String worker : packetPossibleBuilds.getPossibleBuilds().keySet()){
            if(worker.equals(AndreaW1.getID())) assertEquals(packetPossibleBuilds.getPossibleBuilds().get(worker), possibleBuilds);
            else if(worker.equals(AndreaW2.getID())) assertEquals(packetPossibleBuilds.getPossibleBuilds().get(worker).size(), 0);
            else assert false;
        }

        //SECOND BUILD: ANDREA BUILDS A SECOND FLOOR INTO 1,3
        /*

                  0    1     2    3    4   X
                +----+----+----+----+----+
            0   |    |    |    |    |    |
                +----+----+----+----+----+
            1   |    |    |    |    |    |
                |    | D1 |    | M1 |    |
                +----+----+----+----+----+
            2   |    |    |    |    |    |
                |A2  |    | D2 |    |    |
                +----+----+----+----+----+
            3   |    | XSF| A1 | M2 |    |
                +----+----+----+----+----+
            4   |    |    |    |    |    |
                +----+----+----+----+----+
            Y
        */

        //WE CHECK THAT A SECOND BUILDING IS POSSIBLE
        possibleBuilds = new HashMap<>();
        buildsHelper = new ArrayList<>();
        buildsHelper.add(BuildingType.SECOND_FLOOR);
        possibleBuilds.put(point13, buildsHelper);

        buildings.add(BuildingType.FIRST_FLOOR);
        builds.put(point13, buildings);
        buildsOrder.add(point13);
        packetBuild = new PacketBuild(Andrea.getNickname(),AndreaW1.getID(),builds, buildsOrder);
        turnLogic.getPossibleBuilds(Andrea.getNickname(),packetBuild);
        packetPossibleBuilds = mockView.getPacketPossibleBuilds();
        assertEquals(packetPossibleBuilds.getTo(), Andrea.getNickname());

        for(String worker : packetPossibleBuilds.getPossibleBuilds().keySet()){
            if(worker.equals(AndreaW1.getID())) assertEquals(packetPossibleBuilds.getPossibleBuilds().get(worker), possibleBuilds);
            else if(worker.equals(AndreaW2.getID())) assertEquals(packetPossibleBuilds.getPossibleBuilds().get(worker).size(), 0);
            else assert false;
        }

        //WE CONSUME THE BUILD
        builds = new HashMap<>();
        buildsOrder = new ArrayList<>();
        buildings = new ArrayList<>();
        buildings.add(BuildingType.FIRST_FLOOR);
        buildsOrder.add(point13);
        buildings.add(BuildingType.SECOND_FLOOR);
        buildsOrder.add(point13);
        builds.put(point13, buildings);


        packetBuild = new PacketBuild(Andrea.getNickname(), AndreaW1.getID(), builds, buildsOrder);

        try {
            turnLogic.consumePacketBuild(Andrea.getNickname(),packetBuild);
        } catch (InvalidPacketException e) {
            assert false;
        }

        assertEquals(Andrea.getState(), PlayerState.BUILT);
        assertSame(model.getBoard().getCell(point13).getTopBuilding(), LevelType.SECOND_FLOOR);

        packetUpdateBoard = mockView.getPacketUpdateBoard();
        for(Point pos : packetUpdateBoard.getNewBuildings().keySet()){
            List<BuildingType> helper = packetUpdateBoard.getNewBuildings().get(pos);
            assertEquals(model.getBoard().getCell(pos).getTopBuilding().toString(), helper.get(helper.size() - 1).toString());
        }

    }

    /**
     * getPossibleBuilds shouldn't give a possible build on the same spot of the previous one.
     * Case: Hephaestus wants to build a TF, but he can't build a dome after that.
     */
    @Test
    void incrementalBuildsHephaestusNOTADOME(){
         /*

                  0    1     2    3    4   X
                +----+----+----+----+----+
            0   |    |    |    |    |    |
                +----+----+----+----+----+
            1   |    |    |    |    |    |
                |    | D1 |    | M1 |    |
                +----+----+----+----+----+
            2   |    |    |    |    |    |
                |A2  |    | D2 |    |    |
                +----+----+----+----+----+
            3   |    | A1 |    | M2 |    |
                +----+----+----+----+----+
            4   |    |    |    |    |    |
                +----+----+----+----+----+
            Y
        */

        //MOVE UTILS
        List<Point> moves = new ArrayList<>();
        PacketMove packetMove;
        Set<Point> possibleMovesW1 = new HashSet<>();
        Set<Point> possibleMovesW2 = new HashSet<>();

        //BUILDS UTILS
        List<BuildingType> buildings = new ArrayList<>();
        Map<Point, List<BuildingType>> builds = new HashMap<>();
        List<Point> buildsOrder = new ArrayList<>();
        PacketBuild packetBuild;
        Map<Point, List<BuildingType>> possibleBuilds = new HashMap<>();
        List<BuildingType> buildsHelper = new ArrayList<>();

        // Initializing cards
        CardFile pan = cardFactory.getCards().stream().filter(x -> x.getName().equals("Pan")).findFirst().orElse(null);
        CardFile hephaestus = cardFactory.getCards().stream().filter(x -> x.getName().equals("Hephaestus")).findFirst().orElse(null);
        CardFile minotaur = cardFactory.getCards().stream().filter(x -> x.getName().equals("Minotaur")).findFirst().orElse(null);
        Matteo.setCard(pan); //MX
        Andrea.setCard(hephaestus); //AX
        Mirko.setCard(minotaur); //DX
        model.compileCardStrategy();

        model.getBoard().getCell(point11).setWorker(MirkoW1.getID());
        MirkoW1.setPosition(point11);
        model.getBoard().getCell(point22).setWorker(MirkoW2.getID());
        MirkoW2.setPosition(point22);

        model.getBoard().getCell(point31).setWorker(MatteoW1.getID());
        MatteoW1.setPosition(point31);
        model.getBoard().getCell(point33).setWorker(MatteoW2.getID());
        MatteoW2.setPosition(point33);

        model.getBoard().getCell(point13).setWorker(AndreaW1.getID());
        AndreaW1.setPosition(point13);
        model.getBoard().getCell(point02).setWorker(AndreaW2.getID());
        AndreaW2.setPosition(point02);

        turnLogic.start();

        //FIRST MOVE: ANDREA MOVES INTO 2,3
        /*

                  0    1     2    3    4   X
                +----+----+----+----+----+
            0   |    |    |    |    |    |
                +----+----+----+----+----+
            1   |    |    |    |    |    |
                | x  | D1 |    | M1 |    |
                +----+----+----+----+----+
            2   |    |    |    |    |    |
                |A2  | x  | D2 |    |    |
                +----+----+----+----+----+
            3   | x  | A1 -> X | M2 |    |
                +----+----+----+----+----+
            4   |  x | x  | x  |    |    |
                +----+----+----+----+----+
            Y
        */

        //FIRST WE CHECK ALL THE POSSIBLE MOVES
        possibleMovesW1.add(point12);
        possibleMovesW1.add(point23);
        possibleMovesW1.add(point03);
        possibleMovesW1.add(point04);
        possibleMovesW1.add(point14);
        possibleMovesW1.add(point24);

        possibleMovesW2.add(point01);
        possibleMovesW2.add(point03);
        possibleMovesW2.add(point12);

        PacketDoAction packetDoAction = mockView.getPacketDoAction();
        assertEquals(packetDoAction.getTo(), Andrea.getNickname());
        assertEquals(packetDoAction.getActionType(), ActionType.MOVE);

        packetMove = new PacketMove(Andrea.getNickname(),null,moves);
        turnLogic.getPossibleMoves(Andrea.getNickname(), packetMove);
        PacketPossibleMoves packetPossibleMoves = mockView.getPacketPossibleMoves();
        assertEquals(packetPossibleMoves.getTo(), Andrea.getNickname());

        for(String worker : packetPossibleMoves.getPossibleMoves().keySet()){
            if(worker.equals(AndreaW1.getID())) assertEquals(packetPossibleMoves.getPossibleMoves().get(worker), possibleMovesW1);
            else if(worker.equals(AndreaW2.getID())) assertEquals(packetPossibleMoves.getPossibleMoves().get(worker), possibleMovesW2);
            else assert false;
        }

        //WE CHECK THAT AFTER THE WANTED MOVE THERE ARE NO MORE POSSIBLE MOVES

        moves.add(point23);
        packetMove = new PacketMove(Andrea.getNickname(), AndreaW1.getID(), moves);
        turnLogic.getPossibleMoves(Andrea.getNickname(), packetMove);
        packetPossibleMoves = mockView.getPacketPossibleMoves();
        assertEquals(packetPossibleMoves.getTo(), Andrea.getNickname());
        for(String worker : packetPossibleMoves.getPossibleMoves().keySet()){
            assertEquals(packetPossibleMoves.getPossibleMoves().get(worker).size(), 0);
        }

        //WE CONSUME THE MOVE

        try {
            turnLogic.consumePacketMove(Andrea.getNickname(),packetMove);
        } catch (InvalidPacketException e) {
            assert false;
        }
        assertEquals(Andrea.getState(), PlayerState.MOVED);
        assertEquals(point23, AndreaW1.getPosition());
        assertEquals(AndreaW1.getID(), model.getBoard().getCell(point23).getWorkerID());
        assertNull(model.getBoard().getCell(point13).getWorkerID());

        PacketUpdateBoard packetUpdateBoard = mockView.getPacketUpdateBoard();
        for(String worker : packetUpdateBoard.getWorkersPositions().keySet()){
            assertEquals(model.getWorkerByID(worker).getPosition(), packetUpdateBoard.getWorkersPositions().get(worker));
        }

        packetDoAction = mockView.getPacketDoAction();
        assertEquals(packetDoAction.getTo(), Andrea.getNickname());
        assertEquals(packetDoAction.getActionType(), ActionType.BUILD);


        //WE SET A SECOND FLOOR IN 1,2 SO THAT ANDREA'S WORKER WON'T BE ABLE TO BUILD A THIRD FLOOR AND THEN A DOME.

        model.getBoard().getCell(point12).addBuilding(BuildingType.FIRST_FLOOR);
        model.getBoard().getCell(point12).addBuilding(BuildingType.SECOND_FLOOR);

        // ANDREA BUILDS A THIRD_FLOOR INTO 1,2
        /*

                  0    1     2    3    4   X
                +----+----+----+----+----+
            0   |    |    |    |    |    |
                +----+----+----+----+----+
            1   |    |    |    |    |    |
                |    | D1 |    | M1 |    |
                +----+----+----+----+----+
            2   |    |    |    |    |    |
                |A2  | xTF| D2 | x  |    |
                +----+----+----+----+----+
            3   |    | x  | A1 | M2 |    |
                +----+----+----+----+----+
            4   |    |  x | x  |  x |    |
                +----+----+----+----+----+
            Y
        */

        //FIRST WE CHECK ALL THE POSSIBLE BUILDS
        buildsHelper.add(BuildingType.FIRST_FLOOR);
        possibleBuilds.put(point12, buildsHelper);
        possibleBuilds.put(point32, buildsHelper);
        possibleBuilds.put(point13, buildsHelper);
        possibleBuilds.put(point14, buildsHelper);
        possibleBuilds.put(point24, buildsHelper);
        possibleBuilds.put(point34, buildsHelper);

        buildsHelper = new ArrayList<>();
        buildsHelper.add(BuildingType.THIRD_FLOOR);
        possibleBuilds.put(point12, buildsHelper);


        packetBuild = new PacketBuild(Andrea.getNickname(), AndreaW1.getID(),builds, buildsOrder);
        turnLogic.getPossibleBuilds(Andrea.getNickname(), packetBuild);
        PacketPossibleBuilds packetPossibleBuilds = mockView.getPacketPossibleBuilds();
        assertEquals(packetPossibleBuilds.getTo(), Andrea.getNickname());

        for(String worker : packetPossibleBuilds.getPossibleBuilds().keySet()){
            if(worker.equals(AndreaW1.getID())) assertEquals(packetPossibleBuilds.getPossibleBuilds().get(worker), possibleBuilds);
            else if(worker.equals(AndreaW2.getID())) assertEquals(packetPossibleBuilds.getPossibleBuilds().get(worker).size(), 0);
            else assert false;
        }

        //WE CHECK THAT A SECOND BUILDING IS NOT POSSIBLE
        possibleBuilds.clear();
        buildsHelper.clear();

        buildings.add(BuildingType.THIRD_FLOOR);
        builds.put(point12, buildings);
        buildsOrder.add(point12);
        packetBuild = new PacketBuild(Andrea.getNickname(),AndreaW1.getID(),builds, buildsOrder);
        turnLogic.getPossibleBuilds(Andrea.getNickname(),packetBuild);
        packetPossibleBuilds = mockView.getPacketPossibleBuilds();
        assertEquals(packetPossibleBuilds.getTo(), Andrea.getNickname());

        for(String worker : packetPossibleBuilds.getPossibleBuilds().keySet()){
            assertEquals(packetPossibleBuilds.getPossibleBuilds().get(worker).size(), 0);
        }

        //WE FIRST TRY TO BUILD A DOME -> exception

        buildings.add(BuildingType.DOME);
        buildsOrder.add(point12);
        builds.put(point12, buildings);
        packetBuild = new PacketBuild(Andrea.getNickname(),AndreaW1.getID(),builds, buildsOrder);

        try {
            turnLogic.consumePacketBuild(Andrea.getNickname(),packetBuild);
        } catch (InvalidPacketException e) {
            assert true;
        }

        //WE THEN TRY TO APPLY A SIMPLE BUILD STRATEGY
        buildings.clear();
        buildsOrder.clear();
        buildings.add(BuildingType.THIRD_FLOOR);
        buildsOrder.add(point12);
        builds.put(point12, buildings);
        packetBuild = new PacketBuild(Andrea.getNickname(),AndreaW1.getID(),builds, buildsOrder);

        try {
            turnLogic.consumePacketBuild(Andrea.getNickname(),packetBuild);
        } catch (InvalidPacketException e) {
            assert false;
        }

        assertEquals(Andrea.getState(), PlayerState.BUILT);
        assertSame(model.getBoard().getCell(point12).getTopBuilding(), LevelType.THIRD_FLOOR);

        packetUpdateBoard = mockView.getPacketUpdateBoard();
        for(Point pos : packetUpdateBoard.getNewBuildings().keySet()){
            List<BuildingType> helper = packetUpdateBoard.getNewBuildings().get(pos);
            assertEquals(model.getBoard().getCell(pos).getTopBuilding().toString(), helper.get(helper.size() - 1).toString());
        }

    }

    /** 
     * In this test we try to move up after Athena has moved up:
     * If the mode is not set to hardcore -> InvalidPacketException
     */
    @Test
    void invalidPacket(){

        /*

                  0    1     2    3    4   X
                +----+----+----+----+----+
            0   |    |    |    |    |    |
                +----+----+----+----+----+
            1   |    |    |    |    |    |
                |    | D1 |    | M1 |    |
                +----+----+----+----+----+
            2   |    |    |    |    |    |
                |A2  |    | D2 |    |    |
                +----+----+----+----+----+
            3   |    | A1 |    | M2 |    |
                +----+----+----+----+----+
            4   |    |    |    |    |    |
                +----+----+----+----+----+
            Y
        */

        //MOVE UTILS
        List<Point> moves = new ArrayList<>();
        PacketMove packetMove;
        Set<Point> possibleMovesW1 = new HashSet<>();
        Set<Point> possibleMovesW2 = new HashSet<>();

        //BUILDS UTILS
        List<BuildingType> buildings = new ArrayList<>();
        Map<Point, List<BuildingType>> builds = new HashMap<>();
        List<Point> buildsOrder = new ArrayList<>();
        PacketBuild packetBuild;
        Map<Point, List<BuildingType>> possibleBuilds = new HashMap<>();
        List<BuildingType> buildsHelper = new ArrayList<>();

        // Initializing cards
        CardFile pan = cardFactory.getCards().stream().filter(x -> x.getName().equals("Pan")).findFirst().orElse(null);
        CardFile athena = cardFactory.getCards().stream().filter(x -> x.getName().equals("Athena")).findFirst().orElse(null);
        CardFile minotaur = cardFactory.getCards().stream().filter(x -> x.getName().equals("Minotaur")).findFirst().orElse(null);
        Matteo.setCard(pan); //MX
        Andrea.setCard(athena); //AX
        Mirko.setCard(minotaur); //DX
        model.compileCardStrategy();

        model.getBoard().getCell(point11).setWorker(MirkoW1.getID());
        MirkoW1.setPosition(point11);
        model.getBoard().getCell(point22).setWorker(MirkoW2.getID());
        MirkoW2.setPosition(point22);

        model.getBoard().getCell(point31).setWorker(MatteoW1.getID());
        MatteoW1.setPosition(point31);
        model.getBoard().getCell(point33).setWorker(MatteoW2.getID());
        MatteoW2.setPosition(point33);

        model.getBoard().getCell(point13).setWorker(AndreaW1.getID());
        AndreaW1.setPosition(point13);
        model.getBoard().getCell(point02).setWorker(AndreaW2.getID());
        AndreaW2.setPosition(point02);

        turnLogic.start();

        //FIRST WE SET A FIRST_FLOOR SO THAT ANDREA'S WORKER CAN MOVE UP

        model.getBoard().getCell(point12).addBuilding(BuildingType.FIRST_FLOOR);

        /*

                  0    1     2    3    4   X
                +----+----+----+----+----+
            0   |    |    |    |    |    |
                +----+----+----+----+----+
            1   |    |    |    |    |    |
                |    | D1 |    | M1 |    |
                +----+----+----+----+----+
            2   |    |    |    |    |    |
                |A2  -> FF| D2 |    |    |
                +----+----+----+----+----+
            3   |    | A1 |    | M2 |    |
                +----+----+----+----+----+
            4   |    |    |    |    |    |
                +----+----+----+----+----+
            Y
        */

        moves.add(point12);

        packetMove = new PacketMove(Andrea.getNickname(), AndreaW2.getID(), moves);

        try {
            turnLogic.consumePacketMove(Andrea.getNickname(),packetMove);
        } catch (InvalidPacketException e) {
            assert false;
        }
        assertEquals(Andrea.getState(), PlayerState.MOVED);
        assertEquals(point12, AndreaW2.getPosition());
        assertEquals(AndreaW2.getID(), model.getBoard().getCell(point12).getWorkerID());
        assertNull(model.getBoard().getCell(point02).getWorkerID());

        PacketUpdateBoard packetUpdateBoard = mockView.getPacketUpdateBoard();
        for(String worker : packetUpdateBoard.getWorkersPositions().keySet()){
            assertEquals(model.getWorkerByID(worker).getPosition(), packetUpdateBoard.getWorkersPositions().get(worker));
        }


        //ANDREA'S WORKER 2 BUILDS A FIRST_FLOOR INTO 2,3
        /*

                  0    1     2    3    4   X
                +----+----+----+----+----+
            0   |    |    |    |    |    |
                +----+----+----+----+----+
            1   |    |    |    |    |    |
                |    | D1 |    | M1 |    |
                +----+----+----+----+----+
            2   |    |    |    |    |    |
                |    |A2FF| D2 |    |    |
                +----+----+----+----+----+
            3   |    | A1 | xFF| M2 |    |
                +----+----+----+----+----+
            4   |    |    |    |    |    |
                +----+----+----+----+----+
            Y
        */

        buildings.add(BuildingType.FIRST_FLOOR);
        builds.put(point23, buildings);
        buildsOrder.add(point23);
        packetBuild = new PacketBuild(Andrea.getNickname(),AndreaW2.getID(),builds, buildsOrder);

        try {
            turnLogic.consumePacketBuild(Andrea.getNickname(),packetBuild);
        } catch (InvalidPacketException e) {
            assert false;
        }

        assertEquals(Andrea.getState(), PlayerState.BUILT);
        assertSame(model.getBoard().getCell(point23).getTopBuilding(), LevelType.FIRST_FLOOR);

        packetUpdateBoard = mockView.getPacketUpdateBoard();
        for(Point pos : packetUpdateBoard.getNewBuildings().keySet()){
            List<BuildingType> helper = packetUpdateBoard.getNewBuildings().get(pos);
            assertEquals(model.getBoard().getCell(pos).getTopBuilding().toString(), helper.get(helper.size() - 1).toString());
        }

        //RESET

        //MOVE UTILS
        moves = new ArrayList<>();
        possibleMovesW1 = new HashSet<>();
        possibleMovesW2 = new HashSet<>();

        //BUILDS UTILS
        buildings = new ArrayList<>();
        builds = new HashMap<>();
        buildsOrder = new ArrayList<>();

        //FIRST WE CHECK THAT POINT 2,3 IS NOT A POSSIBILITY

        /*

                  0    1     2    3    4   X
                +----+----+----+----+----+
            0   |    |    |  x | x  | x  |
                +----+----+----+----+----+
            1   |    |    |    |    |    |
                |    | D1 |  x | M1 | x  |
                +----+----+----+----+----+
            2   |    |    |    |    |    |
                |    |A2FF| D2 | no | x  |
                +----+----+----+----+----+
            3   |    | A1 | FF | M2 | x  |
                +----+----+----+----+----+
            4   |    |    | x  | x  | x  |
                +----+----+----+----+----+
            Y
       */

        possibleMovesW1.add(point20);
        possibleMovesW1.add(point30);
        possibleMovesW1.add(point40);
        possibleMovesW1.add(point21);
        possibleMovesW1.add(point32);
        possibleMovesW1.add(point42);
        possibleMovesW1.add(point41);

        possibleMovesW2.add(point32);
        possibleMovesW2.add(point42);
        possibleMovesW2.add(point24);
        possibleMovesW2.add(point34);
        possibleMovesW2.add(point44);
        possibleMovesW2.add(point43);


        packetMove = new PacketMove(Matteo.getNickname(),null,moves);
        turnLogic.getPossibleMoves(Matteo.getNickname(), packetMove);
        PacketPossibleMoves packetPossibleMoves = mockView.getPacketPossibleMoves();
        assertEquals(packetPossibleMoves.getTo(), Matteo.getNickname());

        for(String worker : packetPossibleMoves.getPossibleMoves().keySet()){
            if(worker.equals(MatteoW1.getID())) assertEquals(packetPossibleMoves.getPossibleMoves().get(worker), possibleMovesW1);
            else if(worker.equals(MatteoW2.getID())) assertEquals(packetPossibleMoves.getPossibleMoves().get(worker), possibleMovesW2);
            else assert false;
        }


        //NOW MATTEO'S WORKER 2 TRIES TO STEP UP ONTO POSITION 2,3 BUT HE CAN'T BECAUSE ANDREA HAS ATHENA AS GODCARD

        moves.add(point23);

        packetMove = new PacketMove(Matteo.getNickname(), MatteoW2.getID(), moves);

        try {
            turnLogic.consumePacketMove(Matteo.getNickname(),packetMove);
        } catch (InvalidPacketException e) {
            assert true;
        }


    }

    /**
     * In this test we try to move up after Athena has moved up:
     * If the mode is set to hardcore the Player should lose after the forbidden move.
     */
    @Test
    void hardcoreLoss(){

        //FIRST WE SET THE MODE TO HARDCORE

        List<String> players = new ArrayList<>();
        players.add("Andrea");
        players.add("Matteo");
        players.add("Mirko");
        model = new InternalModel(players, cardFactory, true);
        turnLogic = new TurnLogic(model);
        mockView = new Client(turnLogic);
        Andrea = model.getPlayerByNick("Andrea");
        Matteo = model.getPlayerByNick("Matteo");
        Mirko = model.getPlayerByNick("Mirko");
        MatteoW1 = Matteo.getWorkers().get(0);
        MatteoW2 = Matteo.getWorkers().get(1);
        MirkoW1 = Mirko.getWorkers().get(0);
        MirkoW2 = Mirko.getWorkers().get(1);
        AndreaW1 = Andrea.getWorkers().get(0);
        AndreaW2 = Andrea.getWorkers().get(1);

        /*

                  0    1     2    3    4   X
                +----+----+----+----+----+
            0   |    |    |    |    |    |
                +----+----+----+----+----+
            1   |    |    |    |    |    |
                |    | D1 |    | M1 |    |
                +----+----+----+----+----+
            2   |    |    |    |    |    |
                |A2  |    | D2 |    |    |
                +----+----+----+----+----+
            3   |    | A1 |    | M2 |    |
                +----+----+----+----+----+
            4   |    |    |    |    |    |
                +----+----+----+----+----+
            Y
        */

        //MOVE UTILS
        List<Point> moves = new ArrayList<>();
        PacketMove packetMove;
        Set<Point> possibleMovesW1 = new HashSet<>();
        Set<Point> possibleMovesW2 = new HashSet<>();

        //BUILDS UTILS
        List<BuildingType> buildings = new ArrayList<>();
        Map<Point, List<BuildingType>> builds = new HashMap<>();
        List<Point> buildsOrder = new ArrayList<>();
        PacketBuild packetBuild;
        Map<Point, List<BuildingType>> possibleBuilds = new HashMap<>();
        List<BuildingType> buildsHelper = new ArrayList<>();

        // Initializing cards
        CardFile pan = cardFactory.getCards().stream().filter(x -> x.getName().equals("Pan")).findFirst().orElse(null);
        CardFile athena = cardFactory.getCards().stream().filter(x -> x.getName().equals("Athena")).findFirst().orElse(null);
        CardFile minotaur = cardFactory.getCards().stream().filter(x -> x.getName().equals("Minotaur")).findFirst().orElse(null);
        Matteo.setCard(pan); //MX
        Andrea.setCard(athena); //AX
        Mirko.setCard(minotaur); //DX
        model.compileCardStrategy();

        model.getBoard().getCell(point11).setWorker(MirkoW1.getID());
        MirkoW1.setPosition(point11);
        model.getBoard().getCell(point22).setWorker(MirkoW2.getID());
        MirkoW2.setPosition(point22);

        model.getBoard().getCell(point31).setWorker(MatteoW1.getID());
        MatteoW1.setPosition(point31);
        model.getBoard().getCell(point33).setWorker(MatteoW2.getID());
        MatteoW2.setPosition(point33);

        model.getBoard().getCell(point13).setWorker(AndreaW1.getID());
        AndreaW1.setPosition(point13);
        model.getBoard().getCell(point02).setWorker(AndreaW2.getID());
        AndreaW2.setPosition(point02);

        turnLogic.start();

        //FIRST WE SET A FIRST_FLOOR SO THAT ANDREA'S WORKER CAN MOVE UP

        model.getBoard().getCell(point12).addBuilding(BuildingType.FIRST_FLOOR);

        PacketDoAction packetDoAction = mockView.getPacketDoAction();
        assertEquals(packetDoAction.getTo(), "Andrea");
        assertEquals(packetDoAction.getActionType(), ActionType.MOVE);

        /*

                  0    1     2    3    4   X
                +----+----+----+----+----+
            0   |    |    |    |    |    |
                +----+----+----+----+----+
            1   |    |    |    |    |    |
                |    | D1 |    | M1 |    |
                +----+----+----+----+----+
            2   |    |    |    |    |    |
                |A2  -> FF| D2 |    |    |
                +----+----+----+----+----+
            3   |    | A1 |    | M2 |    |
                +----+----+----+----+----+
            4   |    |    |    |    |    |
                +----+----+----+----+----+
            Y
        */

        moves.add(point12);

        packetMove = new PacketMove(Andrea.getNickname(), AndreaW2.getID(), moves);

        try {
            turnLogic.consumePacketMove(Andrea.getNickname(),packetMove);
        } catch (InvalidPacketException e) {
            assert false;
        }
        assertEquals(Andrea.getState(), PlayerState.MOVED);
        assertEquals(point12, AndreaW2.getPosition());
        assertEquals(AndreaW2.getID(), model.getBoard().getCell(point12).getWorkerID());
        assertNull(model.getBoard().getCell(point02).getWorkerID());

        PacketUpdateBoard packetUpdateBoard = mockView.getPacketUpdateBoard();
        for(String worker : packetUpdateBoard.getWorkersPositions().keySet()){
            assertEquals(model.getWorkerByID(worker).getPosition(), packetUpdateBoard.getWorkersPositions().get(worker));
        }

        packetDoAction = mockView.getPacketDoAction();
        assertEquals(packetDoAction.getTo(), "Andrea");
        assertEquals(packetDoAction.getActionType(), ActionType.BUILD);


        //ANDREA'S WORKER 2 BUILDS A FIRST_FLOOR INTO 2,3
        /*

                  0    1     2    3    4   X
                +----+----+----+----+----+
            0   |    |    |    |    |    |
                +----+----+----+----+----+
            1   |    |    |    |    |    |
                |    | D1 |    | M1 |    |
                +----+----+----+----+----+
            2   |    |    |    |    |    |
                |    |A2FF| D2 |    |    |
                +----+----+----+----+----+
            3   |    | A1 | xFF| M2 |    |
                +----+----+----+----+----+
            4   |    |    |    |    |    |
                +----+----+----+----+----+
            Y
        */

        buildings.add(BuildingType.FIRST_FLOOR);
        builds.put(point23, buildings);
        buildsOrder.add(point23);
        packetBuild = new PacketBuild(Andrea.getNickname(),AndreaW2.getID(),builds, buildsOrder);

        try {
            turnLogic.consumePacketBuild(Andrea.getNickname(),packetBuild);
        } catch (InvalidPacketException e) {
            assert false;
        }

        assertEquals(Andrea.getState(), PlayerState.BUILT);
        assertSame(model.getBoard().getCell(point23).getTopBuilding(), LevelType.FIRST_FLOOR);

        packetUpdateBoard = mockView.getPacketUpdateBoard();
        for(Point pos : packetUpdateBoard.getNewBuildings().keySet()){
            List<BuildingType> helper = packetUpdateBoard.getNewBuildings().get(pos);
            assertEquals(model.getBoard().getCell(pos).getTopBuilding().toString(), helper.get(helper.size() - 1).toString());
        }

        packetDoAction = mockView.getPacketDoAction();
        assertEquals(packetDoAction.getTo(), "Matteo");
        assertEquals(packetDoAction.getActionType(), ActionType.MOVE);

        //RESET

        //MOVE UTILS
        moves = new ArrayList<>();
        possibleMovesW1 = new HashSet<>();
        possibleMovesW2 = new HashSet<>();

        //BUILDS UTILS
        buildings = new ArrayList<>();
        builds = new HashMap<>();
        buildsOrder = new ArrayList<>();

        //FIRST WE CHECK THAT POINT 2,3 IS NOT A POSSIBILITY

        /*

                  0    1     2    3    4   X
                +----+----+----+----+----+
            0   |    |    |  x | x  | x  |
                +----+----+----+----+----+
            1   |    |    |    |    |    |
                |    | D1 |  x | M1 | x  |
                +----+----+----+----+----+
            2   |    |    |    |    |    |
                |    |A2FF| D2 | x  | x  |
                +----+----+----+----+----+
            3   |    | A1 | FF | M2 | x  |
                +----+----+----+----+----+
            4   |    |    | x  | x  | x  |
                +----+----+----+----+----+
            Y
       */

        possibleMovesW1.add(point20);
        possibleMovesW1.add(point30);
        possibleMovesW1.add(point40);
        possibleMovesW1.add(point21);
        possibleMovesW1.add(point32);
        possibleMovesW1.add(point42);
        possibleMovesW1.add(point41);

        possibleMovesW2.add(point32);
        possibleMovesW2.add(point42);
        possibleMovesW2.add(point24);
        possibleMovesW2.add(point34);
        possibleMovesW2.add(point44);
        possibleMovesW2.add(point43);
        possibleMovesW2.add(point23);


        packetMove = new PacketMove(Matteo.getNickname(),null,moves);
        turnLogic.getPossibleMoves(Matteo.getNickname(), packetMove);
        PacketPossibleMoves packetPossibleMoves = mockView.getPacketPossibleMoves();
        assertEquals(packetPossibleMoves.getTo(), Matteo.getNickname());

        for(String worker : packetPossibleMoves.getPossibleMoves().keySet()){
            if(worker.equals(MatteoW1.getID())) assertEquals(packetPossibleMoves.getPossibleMoves().get(worker), possibleMovesW1);
            else if(worker.equals(MatteoW2.getID())) assertEquals(packetPossibleMoves.getPossibleMoves().get(worker), possibleMovesW2);
            else assert false;
        }


        //NOW MATTEO'S WORKER 2 TRIES TO STEP UP ONTO POSITION 2,3 BUT HE CAN'T BECAUSE ANDREA HAS ATHENA AS GODCARD AND HE LOSES

        moves.add(point23);

        packetMove = new PacketMove(Matteo.getNickname(), MatteoW2.getID(), moves);

        try {
            turnLogic.consumePacketMove(Matteo.getNickname(),packetMove);
        } catch (InvalidPacketException e) {
            assert false;
        }

        packetUpdateBoard = mockView.getPacketUpdateBoard();
        assertEquals(packetUpdateBoard.getPlayerLostID(), "Matteo");
        packetDoAction = mockView.getPacketDoAction();
        assertEquals(packetDoAction.getTo(), "Mirko");
        assertEquals(packetDoAction.getActionType(), ActionType.MOVE);

        assertTrue(model.getLosers().contains(Matteo));
        assertEquals(model.getLosers().size(), 1);

    }


    //PACKETS MALFORMATIONS

    /**
     * A player not in the match tries to send a valid packet.
     * A player during his turn tries to send a packet generated by a player not in the match.
     * A player not in the match tries to send a packet generated by himself.
     */
    @Test
    void playerNotInTheMatch(){
        CardFile pan = cardFactory.getCards().stream().filter(x -> x.getName().equals("Pan")).findFirst().orElse(null);
        CardFile hephaestus = cardFactory.getCards().stream().filter(x -> x.getName().equals("Hephaestus")).findFirst().orElse(null);
        CardFile minotaur = cardFactory.getCards().stream().filter(x -> x.getName().equals("Minotaur")).findFirst().orElse(null);
        Matteo.setCard(pan); //MX
        Andrea.setCard(hephaestus); //AX
        Mirko.setCard(minotaur); //DX
        model.compileCardStrategy();

        model.getBoard().getCell(point11).setWorker(MirkoW1.getID());
        MirkoW1.setPosition(point11);
        model.getBoard().getCell(point22).setWorker(MirkoW2.getID());
        MirkoW2.setPosition(point22);

        model.getBoard().getCell(point31).setWorker(MatteoW1.getID());
        MatteoW1.setPosition(point31);
        model.getBoard().getCell(point33).setWorker(MatteoW2.getID());
        MatteoW2.setPosition(point33);

        model.getBoard().getCell(point13).setWorker(AndreaW1.getID());
        AndreaW1.setPosition(point13);
        model.getBoard().getCell(point02).setWorker(AndreaW2.getID());
        AndreaW2.setPosition(point02);

        turnLogic.start();

        PacketDoAction packetDoAction = mockView.getPacketDoAction();
        assertEquals(packetDoAction.getTo(), Andrea.getNickname());
        assertEquals(packetDoAction.getActionType(), ActionType.MOVE);

        List<Point> moves = new ArrayList<>();
        moves.add(point23);
        PacketMove packetMove = new PacketMove("Not In The Match", AndreaW1.getID(), moves);

        //A PLAYER NOT IN THE MATCH TRIES THE MOVE

        try {
            turnLogic.consumePacketMove("Not In The Match",packetMove);
        } catch (InvalidPacketException e) {
            assert true;
        }

        assertEquals(AndreaW1.getPosition(), point13);
        assertEquals(model.getBoard().getCell(point13).getWorkerID(), AndreaW1.getID());
        assertEquals(Andrea.getState(), PlayerState.TURN_STARTED);

        //A PLAYER NOT IN THE MATCH TRIES THE MOVE WITH ANDREA'S PACKET

        packetMove = new PacketMove(Andrea.getNickname(), AndreaW1.getID(), moves);

        try {
            turnLogic.consumePacketMove("Not In The Match",packetMove);
        } catch (InvalidPacketException e) {
            assert true;
        }

        assertEquals(AndreaW1.getPosition(), point13);
        assertEquals(model.getBoard().getCell(point13).getWorkerID(), AndreaW1.getID());
        assertEquals(Andrea.getState(), PlayerState.TURN_STARTED);

        //ANDREA TRIES THE MOVE WITH THE PLAYER NOT IN THE MATCH'S PACKET

        packetMove = new PacketMove("Not In The Match", AndreaW1.getID(), moves);

        try {
            turnLogic.consumePacketMove(Andrea.getNickname(),packetMove);
        } catch (InvalidPacketException e) {
            assert true;
        }

        assertEquals(AndreaW1.getPosition(), point13);
        assertEquals(model.getBoard().getCell(point13).getWorkerID(), AndreaW1.getID());
        assertEquals(Andrea.getState(), PlayerState.TURN_STARTED);

        //THE SAME SHOULD HAPPEN WITH PACKET BUILD

        //first a correct move by Andrea
        moves.clear();
        moves.add(point12);
        packetMove = new PacketMove(Andrea.getNickname(), AndreaW1.getID(), moves);

        try {
            turnLogic.consumePacketMove(Andrea.getNickname(),packetMove);
        } catch (InvalidPacketException e) {
            assert false;
        }

        assertEquals(Andrea.getState(), PlayerState.MOVED);

        //A PLAYER NOT IN THE MATCH TRIES THE BUILD
        List<BuildingType> buildings = new ArrayList<>();
        Map<Point, List<BuildingType>> builds = new HashMap<>();
        List<Point> buildsOrder = new ArrayList<>();

        buildings.add(BuildingType.FIRST_FLOOR);
        builds.put(point13, buildings);
        buildsOrder.add(point13);

        PacketBuild packetBuild = new PacketBuild("Not In The Match",AndreaW1.getID(),builds, buildsOrder);

        try {
            turnLogic.consumePacketBuild("Not In The Match",packetBuild);
        } catch (InvalidPacketException e) {
            assert true;
        }

        assertEquals(model.getBoard().getCell(point13).getTopBuilding(), LevelType.GROUND);
        assertEquals(Andrea.getState(), PlayerState.MOVED);

        //A PLAYER NOT IN THE MATCH TRIES THE BUILD WITH ANDREA'S PACKET

        packetBuild = new PacketBuild(Andrea.getNickname(),AndreaW1.getID(),builds, buildsOrder);

        try {
            turnLogic.consumePacketBuild("Not In The Match",packetBuild);
        } catch (InvalidPacketException e) {
            assert true;
        }

        assertEquals(model.getBoard().getCell(point13).getTopBuilding(), LevelType.GROUND);
        assertEquals(Andrea.getState(), PlayerState.MOVED);

        //ANDREA TRIES THE BUILD WITH THE PLAYER NOT IN THE MATCH'S PACKET

        packetBuild = new PacketBuild("Not In The Match",AndreaW1.getID(),builds, buildsOrder);

        try {
            turnLogic.consumePacketBuild(Andrea.getNickname(),packetBuild);
        } catch (InvalidPacketException e) {
            assert true;
        }

        assertEquals(model.getBoard().getCell(point13).getTopBuilding(), LevelType.GROUND);
        assertEquals(Andrea.getState(), PlayerState.MOVED);
    }

    /**
     * A Player during his turn tries to move another Player's worker.
     * A Player during his turn tries to build with another Player's worker.
     */
    @Test
    void notTheirWorker(){
         /*

                  0    1     2    3    4   X
                +----+----+----+----+----+
            0   |    |    |    |    |    |
                +----+----+----+----+----+
            1   |    |    |    |    |    |
                |    | D1 |    | M1 |    |
                +----+----+----+----+----+
            2   |    |    |    |    |    |
                |A2  |    | D2 |    |    |
                +----+----+----+----+----+
            3   |    | A1 |    | M2 |    |
                +----+----+----+----+----+
            4   |    |    |    |    |    |
                +----+----+----+----+----+
            Y
        */

        CardFile pan = cardFactory.getCards().stream().filter(x -> x.getName().equals("Pan")).findFirst().orElse(null);
        CardFile hephaestus = cardFactory.getCards().stream().filter(x -> x.getName().equals("Hephaestus")).findFirst().orElse(null);
        CardFile minotaur = cardFactory.getCards().stream().filter(x -> x.getName().equals("Minotaur")).findFirst().orElse(null);
        Matteo.setCard(pan); //MX
        Andrea.setCard(hephaestus); //AX
        Mirko.setCard(minotaur); //DX
        model.compileCardStrategy();

        model.getBoard().getCell(point11).setWorker(MirkoW1.getID());
        MirkoW1.setPosition(point11);
        model.getBoard().getCell(point22).setWorker(MirkoW2.getID());
        MirkoW2.setPosition(point22);

        model.getBoard().getCell(point31).setWorker(MatteoW1.getID());
        MatteoW1.setPosition(point31);
        model.getBoard().getCell(point33).setWorker(MatteoW2.getID());
        MatteoW2.setPosition(point33);

        model.getBoard().getCell(point13).setWorker(AndreaW1.getID());
        AndreaW1.setPosition(point13);
        model.getBoard().getCell(point02).setWorker(AndreaW2.getID());
        AndreaW2.setPosition(point02);

        turnLogic.start();

        PacketDoAction packetDoAction = mockView.getPacketDoAction();
        assertEquals(packetDoAction.getTo(), Andrea.getNickname());
        assertEquals(packetDoAction.getActionType(), ActionType.MOVE);

        List<Point> moves = new ArrayList<>();
        moves.add(point20);
        PacketMove packetMove = new PacketMove(Andrea.getNickname(), MatteoW1.getID(), moves);

        //ANDREA TRIES THE MOVE WITH MATTEO'S WORKER

        try {
            turnLogic.consumePacketMove(Andrea.getNickname(),packetMove);
        } catch (InvalidPacketException e) {
            assert true;
        }

        assertEquals(MatteoW1.getPosition(), point31);
        assertEquals(model.getBoard().getCell(point31).getWorkerID(), MatteoW1.getID());

        assertEquals(AndreaW1.getPosition(), point13);
        assertEquals(model.getBoard().getCell(point13).getWorkerID(), AndreaW1.getID());

        assertEquals(AndreaW2.getPosition(), point02);
        assertEquals(model.getBoard().getCell(point02).getWorkerID(), AndreaW2.getID());

        //first a correct move by Andrea
        moves.clear();
        moves.add(point12);
        packetMove = new PacketMove(Andrea.getNickname(), AndreaW1.getID(), moves);

        try {
            turnLogic.consumePacketMove(Andrea.getNickname(),packetMove);
        } catch (InvalidPacketException e) {
            assert false;
        }

        assertEquals(Andrea.getState(), PlayerState.MOVED);

        //ANDREA TRIES THE BUILD WITH MATTEO'S WORKER

        List<BuildingType> buildings = new ArrayList<>();
        Map<Point, List<BuildingType>> builds = new HashMap<>();
        List<Point> buildsOrder = new ArrayList<>();

        buildings.add(BuildingType.FIRST_FLOOR);
        builds.put(point23, buildings);
        buildsOrder.add(point23);

        PacketBuild packetBuild = new PacketBuild(Andrea.getNickname(),MatteoW1.getID(),builds, buildsOrder);

        try {
            turnLogic.consumePacketBuild(Andrea.getNickname(),packetBuild);
        } catch (InvalidPacketException e) {
            assert true;
        }

        assertEquals(model.getBoard().getCell(point23).getTopBuilding(), LevelType.GROUND);
        assertEquals(Andrea.getState(), PlayerState.MOVED);
        assertEquals(Matteo.getState(), PlayerState.TURN_STARTED);

    }

    /**
     * Another Player tries to send a packet generated by the Player that is currently trying the action.
     */
    @Test
    void notTheRightPlayerWithTheCorrectWorker(){
         /*

                  0    1     2    3    4   X
                +----+----+----+----+----+
            0   |    |    |    |    |    |
                +----+----+----+----+----+
            1   |    |    |    |    |    |
                |    | D1 |    | M1 |    |
                +----+----+----+----+----+
            2   |    |    |    |    |    |
                |A2  |    | D2 |    |    |
                +----+----+----+----+----+
            3   |    | A1 |    | M2 |    |
                +----+----+----+----+----+
            4   |    |    |    |    |    |
                +----+----+----+----+----+
            Y
        */

        CardFile pan = cardFactory.getCards().stream().filter(x -> x.getName().equals("Pan")).findFirst().orElse(null);
        CardFile hephaestus = cardFactory.getCards().stream().filter(x -> x.getName().equals("Hephaestus")).findFirst().orElse(null);
        CardFile minotaur = cardFactory.getCards().stream().filter(x -> x.getName().equals("Minotaur")).findFirst().orElse(null);
        Matteo.setCard(pan); //MX
        Andrea.setCard(hephaestus); //AX
        Mirko.setCard(minotaur); //DX
        model.compileCardStrategy();

        model.getBoard().getCell(point11).setWorker(MirkoW1.getID());
        MirkoW1.setPosition(point11);
        model.getBoard().getCell(point22).setWorker(MirkoW2.getID());
        MirkoW2.setPosition(point22);

        model.getBoard().getCell(point31).setWorker(MatteoW1.getID());
        MatteoW1.setPosition(point31);
        model.getBoard().getCell(point33).setWorker(MatteoW2.getID());
        MatteoW2.setPosition(point33);

        model.getBoard().getCell(point13).setWorker(AndreaW1.getID());
        AndreaW1.setPosition(point13);
        model.getBoard().getCell(point02).setWorker(AndreaW2.getID());
        AndreaW2.setPosition(point02);

        turnLogic.start();

        PacketDoAction packetDoAction = mockView.getPacketDoAction();
        assertEquals(packetDoAction.getTo(), Andrea.getNickname());
        assertEquals(packetDoAction.getActionType(), ActionType.MOVE);

        List<Point> moves = new ArrayList<>();
        moves.add(point12);
        PacketMove packetMove = new PacketMove(Andrea.getNickname(), AndreaW1.getID(), moves);

        //MATTEO TRIES TO SEND A PACKET MOVE GENERATED BY ANDREA

        try {
            turnLogic.consumePacketMove(Matteo.getNickname(),packetMove);
        } catch (InvalidPacketException e) {
            assert true;
        }

        assertEquals(AndreaW1.getPosition(), point13);
        assertEquals(model.getBoard().getCell(point13).getWorkerID(), AndreaW1.getID());

        //first a correct move by Andrea
        moves.clear();
        moves.add(point12);
        packetMove = new PacketMove(Andrea.getNickname(), AndreaW1.getID(), moves);

        try {
            turnLogic.consumePacketMove(Andrea.getNickname(),packetMove);
        } catch (InvalidPacketException e) {
            assert false;
        }

        assertEquals(Andrea.getState(), PlayerState.MOVED);

        //MATTEO TRIES TO SEND A PACKET BUILD GENERATED BY ANDREA

        List<BuildingType> buildings = new ArrayList<>();
        Map<Point, List<BuildingType>> builds = new HashMap<>();
        List<Point> buildsOrder = new ArrayList<>();

        buildings.add(BuildingType.FIRST_FLOOR);
        builds.put(point13, buildings);
        buildsOrder.add(point13);

        PacketBuild packetBuild = new PacketBuild(Andrea.getNickname(),AndreaW1.getID(),builds, buildsOrder);

        try {
            turnLogic.consumePacketBuild(Matteo.getNickname(),packetBuild);
        } catch (InvalidPacketException e) {
            assert true;
        }

        assertEquals(model.getBoard().getCell(point13).getTopBuilding(), LevelType.GROUND);
        assertEquals(Andrea.getState(), PlayerState.MOVED);
        assertEquals(Matteo.getState(), PlayerState.TURN_STARTED);

    }

    /**
     * A Player during his turn tries to move with a non-existing worker.
     * A Player during his turn tries to build with a non-existing worker.
     */
    @Test
    void notValidWorker(){
         /*

                  0    1     2    3    4   X
                +----+----+----+----+----+
            0   |    |    |    |    |    |
                +----+----+----+----+----+
            1   |    |    |    |    |    |
                |    | D1 |    | M1 |    |
                +----+----+----+----+----+
            2   |    |    |    |    |    |
                |A2  |    | D2 |    |    |
                +----+----+----+----+----+
            3   |    | A1 |    | M2 |    |
                +----+----+----+----+----+
            4   |    |    |    |    |    |
                +----+----+----+----+----+
            Y
        */

        CardFile pan = cardFactory.getCards().stream().filter(x -> x.getName().equals("Pan")).findFirst().orElse(null);
        CardFile hephaestus = cardFactory.getCards().stream().filter(x -> x.getName().equals("Hephaestus")).findFirst().orElse(null);
        CardFile minotaur = cardFactory.getCards().stream().filter(x -> x.getName().equals("Minotaur")).findFirst().orElse(null);
        Matteo.setCard(pan); //MX
        Andrea.setCard(hephaestus); //AX
        Mirko.setCard(minotaur); //DX
        model.compileCardStrategy();

        model.getBoard().getCell(point11).setWorker(MirkoW1.getID());
        MirkoW1.setPosition(point11);
        model.getBoard().getCell(point22).setWorker(MirkoW2.getID());
        MirkoW2.setPosition(point22);

        model.getBoard().getCell(point31).setWorker(MatteoW1.getID());
        MatteoW1.setPosition(point31);
        model.getBoard().getCell(point33).setWorker(MatteoW2.getID());
        MatteoW2.setPosition(point33);

        model.getBoard().getCell(point13).setWorker(AndreaW1.getID());
        AndreaW1.setPosition(point13);
        model.getBoard().getCell(point02).setWorker(AndreaW2.getID());
        AndreaW2.setPosition(point02);

        turnLogic.start();

        PacketDoAction packetDoAction = mockView.getPacketDoAction();
        assertEquals(packetDoAction.getTo(), Andrea.getNickname());
        assertEquals(packetDoAction.getActionType(), ActionType.MOVE);

        List<Point> moves = new ArrayList<>();
        moves.add(point12);
        PacketMove packetMove = new PacketMove(Andrea.getNickname(), "non-existing Worker", moves);

        //ANDREA TRIES TO MOVE WITH A NON-EXISTING WORKER

        try {
            turnLogic.consumePacketMove(Andrea.getNickname(),packetMove);
        } catch (InvalidPacketException e) {
            assert true;
        }

        assertEquals(AndreaW1.getPosition(), point13);
        assertEquals(model.getBoard().getCell(point13).getWorkerID(), AndreaW1.getID());

        //first a correct move by Andrea
        moves.clear();
        moves.add(point12);
        packetMove = new PacketMove(Andrea.getNickname(), AndreaW1.getID(), moves);

        try {
            turnLogic.consumePacketMove(Andrea.getNickname(),packetMove);
        } catch (InvalidPacketException e) {
            assert false;
        }

        assertEquals(Andrea.getState(), PlayerState.MOVED);

        //ANDREA TRIES TO BUILD WITH A NON-EXISTING WORKER

        List<BuildingType> buildings = new ArrayList<>();
        Map<Point, List<BuildingType>> builds = new HashMap<>();
        List<Point> buildsOrder = new ArrayList<>();

        buildings.add(BuildingType.FIRST_FLOOR);
        builds.put(point13, buildings);
        buildsOrder.add(point13);

        PacketBuild packetBuild = new PacketBuild(Andrea.getNickname(),"non-existing Worker",builds, buildsOrder);

        try {
            turnLogic.consumePacketBuild(Andrea.getNickname(),packetBuild);
        } catch (InvalidPacketException e) {
            assert true;
        }

        assertEquals(model.getBoard().getCell(point13).getTopBuilding(), LevelType.GROUND);
        assertEquals(Andrea.getState(), PlayerState.MOVED);
    }


}