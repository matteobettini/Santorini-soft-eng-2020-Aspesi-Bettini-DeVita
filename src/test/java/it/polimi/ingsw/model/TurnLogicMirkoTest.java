package it.polimi.ingsw.model;

import it.polimi.ingsw.model.cardReader.CardFactory;
import it.polimi.ingsw.model.cardReader.CardFile;
import it.polimi.ingsw.model.cardReader.exceptions.CardLoadingException;
import it.polimi.ingsw.model.cardReader.exceptions.InvalidCardException;
import it.polimi.ingsw.model.enums.ActionType;
import it.polimi.ingsw.model.enums.BuildingType;
import it.polimi.ingsw.model.enums.LevelType;
import it.polimi.ingsw.model.enums.PlayerState;
import it.polimi.ingsw.model.turnInfo.BuildData;
import it.polimi.ingsw.model.turnInfo.MoveData;
import it.polimi.ingsw.observe.Observer;
import it.polimi.ingsw.packets.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TurnLogicMirkoTest {

    static class Client implements Observer<PacketContainer> {
        private Queue<PacketPossibleBuilds> packetPossibleBuilds;
        private Queue<PacketPossibleMoves> packetPossibleMoves;
        private Queue<PacketDoAction> packetDoAction;
        private Queue<PacketUpdateBoard> packetUpdateBoard;
        public Client(){
            packetDoAction = new LinkedList<>();
            packetPossibleBuilds = new LinkedList<>();
            packetPossibleMoves = new LinkedList<>();
            packetUpdateBoard = new LinkedList<>();
        }
        public void update(PacketContainer packetContainer){
            if(packetContainer.getPacketPossibleMoves() != null)
                this.packetPossibleMoves.add(packetContainer.getPacketPossibleMoves());
            if(packetContainer.getPacketDoAction() != null)
                this.packetDoAction.add(packetContainer.getPacketDoAction());
            if(packetContainer.getPacketUpdateBoard() != null)
                this.packetUpdateBoard.add(packetContainer.getPacketUpdateBoard());
            if(packetContainer.getPacketPossibleBuilds() != null)
                this.packetPossibleBuilds.add(packetContainer.getPacketPossibleBuilds());
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
        model = new InternalModel(players, cardFactory, false);
        turnLogic = new TurnLogic(model);
        mockView = new Client();
        turnLogic.addObserver(mockView);
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


    @Test
    void pan_demeter_minotaur(){

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

        packetMove = new PacketMove(Andrea.getNickname(),AndreaW1.getID(),moves);
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

        packetDoAction = mockView.getPacketDoAction();
        assertEquals(packetDoAction.getTo(), Andrea.getNickname());
        assertEquals(packetDoAction.getActionType(), ActionType.BUILD);

        packetBuild = new PacketBuild(Andrea.getNickname(), AndreaW1.getID(),builds, buildsOrder);
        turnLogic.getPossibleBuilds(Andrea.getNickname(), packetBuild);
        PacketPossibleBuilds packetPossibleBuilds = mockView.getPacketPossibleBuilds();
        assertEquals(packetPossibleBuilds.getTo(), Andrea.getNickname());

        for(String worker : packetPossibleBuilds.getPossibleBuilds().keySet()){
            if(worker.equals(AndreaW1.getID())) assertEquals(packetPossibleBuilds.getPossibleBuilds().get(worker), possibleBuilds);
            else if(worker.equals(AndreaW2.getID())) assertEquals(packetPossibleBuilds.getPossibleBuilds().get(worker).size(), 0);
            else assert false;
        }

        //WE CHECK THAT A SECOND FLOOR IS POSSIBLE
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
            assertEquals(model.getBoard().getCell(pos).getHeight(), helper.size());
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

        packetMove = new PacketMove(Matteo.getNickname(),MatteoW1.getID(),moves);
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

        //MATTEO W1 BUILDS INTO 2,0
        /*

                  0    1     2    3    4   X
                +----+----+----+----+----+
            0   |    |  x |  FF| x  |    |
                +----+----+----+----+----+
            1   |    |    |    |    |    |
                |    | D1 | M1 | x  |    |
                +----+----+----+----+----+
            2   |    |    |    |    |    |
                |A2  | A1 | D2 | x  |    |
                +----+----+----+----+----+
            3   |    | FF | FF | M2 |    |
                +----+----+----+----+----+
            4   |    |    |    |    |    |
                +----+----+----+----+----+
            Y
        */

        //FIRST WE CHECK ALL THE POSSIBLE BUILDS
        buildsHelper.add(BuildingType.FIRST_FLOOR);
        possibleBuilds.put(point10, buildsHelper);
        possibleBuilds.put(point20, buildsHelper);
        possibleBuilds.put(point30, buildsHelper);
        possibleBuilds.put(point31, buildsHelper);
        possibleBuilds.put(point32, buildsHelper);

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
        buildings.add(BuildingType.FIRST_FLOOR);
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
        assertSame(model.getBoard().getCell(point20).getTopBuilding(), LevelType.FIRST_FLOOR);

        packetUpdateBoard = mockView.getPacketUpdateBoard();
        for(Point pos : packetUpdateBoard.getNewBuildings().keySet()){
            List<BuildingType> helper = packetUpdateBoard.getNewBuildings().get(pos);
            assertEquals(model.getBoard().getCell(pos).getHeight(), helper.size());
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


        //WE CHECK THE NEXT PLAYER. IT SHOULD BE MIRKO

        packetDoAction = mockView.getPacketDoAction();
        assertEquals(packetDoAction.getTo(), Mirko.getNickname());
        assertEquals(packetDoAction.getActionType(), ActionType.MOVE);

        //WE PUT A TF ON 1,3 SO THAT A1 CAN BE PUSHED ONTO

        model.getBoard().getCell(point13).addBuilding(BuildingType.SECOND_FLOOR);
        model.getBoard().getCell(point13).addBuilding(BuildingType.THIRD_FLOOR);

        //MIRKO'S W1 MOVES INTO 1,2 AND PUSHES A1 TO 1,3
        /*

                  0    1     2    3    4   X
                +----+----+----+----+----+
            0   | x  | x  | x  |    |    |
                +----+----+----+----+----+
            1   |    |    |    |    |    |
                | x  | D1 | xM1| x  |    |
                +----+-↓--+----+----+----+
            2   |    |    |    |    |    |
                | A2 | xA1| D2 | x  |    |
                +----+-↓--+----+----+----+
            3   |    | TF | xFF| xM2|    |
                +----+----+----+----+----+
            4   |    |    |    |    |    |
                +----+----+----+----+----+
            Y
        */

        possibleMovesW1.add(point00);
        possibleMovesW1.add(point10);
        possibleMovesW1.add(point01);
        possibleMovesW1.add(point21);
        possibleMovesW1.add(point12);
        possibleMovesW1.add(point20);

        possibleMovesW2.add(point21);
        possibleMovesW2.add(point31);
        possibleMovesW2.add(point32);
        possibleMovesW2.add(point23);
        possibleMovesW2.add(point33);

        packetMove = new PacketMove(Mirko.getNickname(),MirkoW1.getID(),moves);
        turnLogic.getPossibleMoves(Mirko.getNickname(), packetMove);
        packetPossibleMoves = mockView.getPacketPossibleMoves();
        assertEquals(packetPossibleMoves.getTo(), Mirko.getNickname());

        for(String worker : packetPossibleMoves.getPossibleMoves().keySet()){
            if(worker.equals(MirkoW1.getID())) assertEquals(packetPossibleMoves.getPossibleMoves().get(worker), possibleMovesW1);
            else if(worker.equals(MirkoW2.getID())) assertEquals(packetPossibleMoves.getPossibleMoves().get(worker), possibleMovesW2);
            else assert false;
        }

        //WE CHECK THAT AFTER THE WANTED MOVE THERE ARE NO MORE POSSIBLE MOVES

        moves.add(point12);
        packetMove = new PacketMove(Mirko.getNickname(), MirkoW1.getID(), moves);
        turnLogic.getPossibleMoves(Mirko.getNickname(), packetMove);
        packetPossibleMoves = mockView.getPacketPossibleMoves();
        assertEquals(packetPossibleMoves.getTo(), Mirko.getNickname());
        for(String worker : packetPossibleMoves.getPossibleMoves().keySet()){
            assertEquals(packetPossibleMoves.getPossibleMoves().get(worker).size(), 0);
        }

        //WE CONSUME THE MOVE

        try {
            turnLogic.consumePacketMove(Mirko.getNickname(),packetMove);
        } catch (InvalidPacketException e) {
            assert false;
        }
        assertEquals(Mirko.getState(), PlayerState.MOVED);
        assertEquals(point12, MirkoW1.getPosition());
        assertEquals(MirkoW1.getID(), model.getBoard().getCell(point12).getWorkerID());
        assertNull(model.getBoard().getCell(point11).getWorkerID());

        //WE CHECK THAT A1 IS CORRECTLY PUSHED
        assertEquals(point13, AndreaW1.getPosition());
        assertEquals(AndreaW1.getID(), model.getBoard().getCell(point13).getWorkerID());

        packetUpdateBoard = mockView.getPacketUpdateBoard();
        for(String worker : packetUpdateBoard.getWorkersPositions().keySet()){
            assertEquals(model.getWorkerByID(worker).getPosition(), packetUpdateBoard.getWorkersPositions().get(worker));
        }

        //WE SET THE BUILDINGS SO THAT MIRKO'S WORKER CAN'T BUILD




        //MIRKO'S W1 CAN'T BUILD
        /*

                  0    1     2    3    4   X
                +----+----+----+----+----+
            0   |    |    |    |    |    |
                +----+----+----+----+----+
            1   |    |    |    |    |    |
                | C  |  C |  M1|    |    |
                +----+----+----+----+----+
            2   |    |    |    |    |    |
                | A2 | D1 | D2 |    |    |
                +----+----+----+----+----+
            3   | C  |A1TF| C  |  M2|    |
                +----+----+----+----+----+
            4   |    |    |    |    |    |
                +----+----+----+----+----+
            Y
        */



    }



}