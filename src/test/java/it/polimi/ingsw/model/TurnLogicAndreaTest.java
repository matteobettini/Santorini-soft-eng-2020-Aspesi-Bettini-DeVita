package it.polimi.ingsw.model;

import it.polimi.ingsw.model.cardReader.CardFactory;
import it.polimi.ingsw.model.cardReader.exceptions.CardLoadingException;
import it.polimi.ingsw.model.cardReader.exceptions.InvalidCardException;
import it.polimi.ingsw.model.enums.ActionType;
import it.polimi.ingsw.model.enums.BuildingType;
import it.polimi.ingsw.observe.Observer;
import it.polimi.ingsw.packets.*;
import org.junit.jupiter.api.*;

import java.awt.*;
import java.util.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TurnLogicAndreaTest {

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

        /* AT THE BEGINNING OF EACH TEST IT IS RECOMMENDED TO SET A CARD FILE FOR EACH PLAYER
           AND THEN CALL COMPILE CARD STRATEGY IN THE MODEL
        */
    }

    /**
     * Using only default strategy.
     *
     *
     * Let's start the turn logic component, checking that the first package
     * is sent to the first player (the first of the list in internal model)
     *
     * First player is Andrea by construction
     */
    @Test
    void testStart(){
        //Init the model
        /*
                  0    1     2    3    4    X
                +----+----+----+----+----+
            0   | A1 |    |    |    | A2 |
                +----+----+----+----+----+
            1   |    | D1 |    |    |    |
                +----+----+----+----+----+
            2   |    |    |    | D2 |    |
                +----+----+----+----+----+
            3   |    | B1 |    |    |    |
                +----+----+----+----+----+
            4   |    |    |    | B2 |    |
                +----+----+----+----+----+
            Y
        */
        simpleInit();

        //START TEST
        turnLogic.start();

        /*
         * FIRST MOVE ANDREA
         */
        Point p10 = new Point(1,0);
        simpleMoveNoWin(Andrea, AndreaW1, p10);
        /*
                  0    1     2    3    4    X
                +----+----+----+----+----+
            0   |    | A1 |    |    | A2 |
                +----+----+----+----+----+
            1   |    | D1 |    |    |    |
                +----+----+----+----+----+
            2   |    |    |    | D2 |    |
                +----+----+----+----+----+
            3   |    | B1 |    |    |    |
                +----+----+----+----+----+
            4   |    |    |    | B2 |    |
                +----+----+----+----+----+
            Y
        */
        /*
         * FIRST BUILD ANDREA
         */
        Point p01 = new Point(0,1);
        BuildingType building = BuildingType.FIRST_FLOOR;
        simpleBuildNoWin(Andrea, AndreaW1, p01, building);
        /*
                  0    1     2    3    4    X
                +----+----+----+----+----+
            0   |    | A1 |    |    | A2 |
                +----+----+----+----+----+
            1   | FF | D1 |    |    |    |
                +----+----+----+----+----+
            2   |    |    |    | D2 |    |
                +----+----+----+----+----+
            3   |    | B1 |    |    |    |
                +----+----+----+----+----+
            4   |    |    |    | B2 |    |
                +----+----+----+----+----+
            Y
        */
        /*
         * FIRST MOVE MATTEO
         */
        Point p03 = new Point(0,3);
        simpleMoveNoWin(Matteo, MatteoW1, p03);
        /*
                  0    1     2    3    4    X
                +----+----+----+----+----+
            0   |    | A1 |    |    | A2 |
                +----+----+----+----+----+
            1   | FF | D1 |    |    |    |
                +----+----+----+----+----+
            2   |    |    |    | D2 |    |
                +----+----+----+----+----+
            3   | B1 |    |    |    |    |
                +----+----+----+----+----+
            4   |    |    |    | B2 |    |
                +----+----+----+----+----+
            Y
        */
        /*
         * FIRST BUILD MATTEO
         */
        Point p02 = new Point(0,2);
        building = BuildingType.FIRST_FLOOR;
        simpleBuildNoWin(Matteo, MatteoW1, p02, building);
        /*
                  0    1     2    3    4    X
                +----+----+----+----+----+
            0   |    | A1 |    |    | A2 |
                +----+----+----+----+----+
            1   | FF | D1 |    |    |    |
                +----+----+----+----+----+
            2   | FF |    |    | D2 |    |
                +----+----+----+----+----+
            3   | B1 |    |    |    |    |
                +----+----+----+----+----+
            4   |    |    |    | B2 |    |
                +----+----+----+----+----+
            Y
        */
        /*
         * FIRST MOVE MIRKO
         */
        Point p42 = new Point(4,2);
        simpleMoveNoWin(Mirko, MirkoW2, p42);
        /*
                  0    1     2    3    4    X
                +----+----+----+----+----+
            0   |    | A1 |    |    | A2 |
                +----+----+----+----+----+
            1   | FF | D1 |    |    |    |
                +----+----+----+----+----+
            2   | FF |    |    |    | D2 |
                +----+----+----+----+----+
            3   | B1 |    |    |    |    |
                +----+----+----+----+----+
            4   |    |    |    | B2 |    |
                +----+----+----+----+----+
            Y
        */
        /*
         * FIRST BUILD MIRKO
         */
        Point p43 = new Point(4,3);
        building = BuildingType.FIRST_FLOOR;
        simpleBuildNoWin(Mirko, MirkoW2, p43, building);
        /*
                  0    1     2    3    4    X
                +----+----+----+----+----+
            0   |    | A1 |    |    | A2 |
                +----+----+----+----+----+
            1   | FF | D1 |    |    |    |
                +----+----+----+----+----+
            2   | FF |    |    |    | D2 |
                +----+----+----+----+----+
            3   | B1 |    |    |    | FF |
                +----+----+----+----+----+
            4   |    |    |    | B2 |    |
                +----+----+----+----+----+
            Y
        */
        /*
         * SECOND MOVE ANDREA
         */
        simpleMoveNoWin(Andrea, AndreaW1, p01);
        /*
                  0    1     2    3    4    X
                +----+----+----+----+----+
            0   |    |    |    |    | A2 |
                +----+----+----+----+----+
            1   | FF |    |    |    |    |
                | A1 | D1 |    |    |    |
                +----+----+----+----+----+
            2   | FF |    |    |    | D2 |
                +----+----+----+----+----+
            3   | B1 |    |    |    | FF |
                +----+----+----+----+----+
            4   |    |    |    | B2 |    |
                +----+----+----+----+----+
            Y
        */
        /*
         * SECOND BUILD ANDREA
         */
        building = BuildingType.SECOND_FLOOR;
        simpleBuildNoWin(Andrea, AndreaW1, p02, building);
        /*
                  0    1     2    3    4    X
                +----+----+----+----+----+
            0   |    |    |    |    | A2 |
                +----+----+----+----+----+
            1   | FF |    |    |    |    |
                | A1 | D1 |    |    |    |
                +----+----+----+----+----+
            2   | SF |    |    |    | D2 |
                +----+----+----+----+----+
            3   | B1 |    |    |    | FF |
                +----+----+----+----+----+
            4   |    |    |    | B2 |    |
                +----+----+----+----+----+
            Y
        */
        /*
         * SECOND MOVE MATTEO
         */
        Point p12 = new Point(1,2);
        simpleMoveNoWin(Matteo, MatteoW1, p12);
        /*
                  0    1     2    3    4    X
                +----+----+----+----+----+
            0   |    |    |    |    | A2 |
                +----+----+----+----+----+
            1   | FF |    |    |    |    |
                | A1 | D1 |    |    |    |
                +----+----+----+----+----+
            2   | SF | B1 |    |    | D2 |
                +----+----+----+----+----+
            3   |    |    |    |    | FF |
                +----+----+----+----+----+
            4   |    |    |    | B2 |    |
                +----+----+----+----+----+
            Y
        */
        /*
         * SECOND BUILD MATTEO
         */
        building = BuildingType.THIRD_FLOOR;
        simpleBuildNoWin(Matteo, MatteoW1, p02, building);
        /*
                  0    1     2    3    4    X
                +----+----+----+----+----+
            0   |    |    |    |    | A2 |
                +----+----+----+----+----+
            1   | FF |    |    |    |    |
                | A1 | D1 |    |    |    |
                +----+----+----+----+----+
            2   | TF | B1 |    |    | D2 |
                +----+----+----+----+----+
            3   |    |    |    |    | FF |
                +----+----+----+----+----+
            4   |    |    |    | B2 |    |
                +----+----+----+----+----+
            Y
        */
        /*
         * SECOND MOVE MIRKO
         */
        simpleMoveNoWin(Mirko, MirkoW2, p43);
        /*
                  0    1     2    3    4    X
                +----+----+----+----+----+
            0   |    |    |    |    | A2 |
                +----+----+----+----+----+
            1   | FF |    |    |    |    |
                | A1 | D1 |    |    |    |
                +----+----+----+----+----+
            2   | TF | B1 |    |    |    |
                +----+----+----+----+----+
            3   |    |    |    |    | FF |
                |    |    |    |    | D2 |
                +----+----+----+----+----+
            4   |    |    |    | B2 |    |
                +----+----+----+----+----+
            Y
        */
        /*
         * SECOND BUILD MIRKO
         */
        Point p33 = new Point(3,3);
        building = BuildingType.FIRST_FLOOR;
        simpleBuildNoWin(Mirko, MirkoW2, p33, building);
        /*
                  0    1     2    3    4    X
                +----+----+----+----+----+
            0   |    |    |    |    | A2 |
                +----+----+----+----+----+
            1   | FF |    |    |    |    |
                | A1 | D1 |    |    |    |
                +----+----+----+----+----+
            2   | TF | B1 |    |    |    |
                +----+----+----+----+----+
            3   |    |    |    | FF | FF |
                |    |    |    |    | D2 |
                +----+----+----+----+----+
            4   |    |    |    | B2 |    |
                +----+----+----+----+----+
            Y
        */
        /*
         * THIRD MOVE ANDREA
         */
        Point p31 = new Point(3,1);
        simpleMoveNoWin(Andrea,AndreaW2,p31);
        /*
                  0    1     2    3    4    X
                +----+----+----+----+----+
            0   |    |    |    |    |    |
                +----+----+----+----+----+
            1   | FF |    |    |    |    |
                | A1 | D1 |    | A2 |    |
                +----+----+----+----+----+
            2   | TF | B1 |    |    |    |
                +----+----+----+----+----+
            3   |    |    |    | FF | FF |
                |    |    |    |    | D2 |
                +----+----+----+----+----+
            4   |    |    |    | B2 |    |
                +----+----+----+----+----+
            Y
        */
        /*
         * THIRD BUILD ANDREA
         */
        Point p32 = new Point(3,2);
        building = BuildingType.FIRST_FLOOR;
        simpleBuildNoWin(Andrea, AndreaW2, p32, building);
        /*
                  0    1     2    3    4    X
                +----+----+----+----+----+
            0   |    |    |    |    |    |
                +----+----+----+----+----+
            1   | FF |    |    |    |    |
                | A1 | D1 |    | A2 |    |
                +----+----+----+----+----+
            2   | TF | B1 |    | FF |    |
                +----+----+----+----+----+
            3   |    |    |    | FF | FF |
                |    |    |    |    | D2 |
                +----+----+----+----+----+
            4   |    |    |    | B2 |    |
                +----+----+----+----+----+
            Y
        */
        /*
         * THIRD MOVE MATTEO
         */
        Point p13 = new Point(1,3);
        simpleMoveNoWin(Matteo, MatteoW1,p13);
        /*
                  0    1     2    3    4    X
                +----+----+----+----+----+
            0   |    |    |    |    |    |
                +----+----+----+----+----+
            1   | FF |    |    |    |    |
                | A1 | D1 |    | A2 |    |
                +----+----+----+----+----+
            2   | TF |    |    | FF |    |
                +----+----+----+----+----+
            3   |    |    |    | FF | FF |
                |    | B1 |    |    | D2 |
                +----+----+----+----+----+
            4   |    |    |    | B2 |    |
                +----+----+----+----+----+
            Y
        */
        /*
         * THIRD BUILD MATTEO
         */
        building = BuildingType.FIRST_FLOOR;
        simpleBuildNoWin(Matteo, MatteoW1,p03, building);
        /*
                  0    1     2    3    4    X
                +----+----+----+----+----+
            0   |    |    |    |    |    |
                +----+----+----+----+----+
            1   | FF |    |    |    |    |
                | A1 | D1 |    | A2 |    |
                +----+----+----+----+----+
            2   | TF |    |    | FF |    |
                +----+----+----+----+----+
            3   | FF |    |    | FF | FF |
                |    | B1 |    |    | D2 |
                +----+----+----+----+----+
            4   |    |    |    | B2 |    |
                +----+----+----+----+----+
            Y
        */
        /*
         * THIRD MOVE MIRKO
         */
        simpleMoveNoWin(Mirko, MirkoW2, p33);
        /*
                  0    1     2    3    4    X
                +----+----+----+----+----+
            0   |    |    |    |    |    |
                +----+----+----+----+----+
            1   | FF |    |    |    |    |
                | A1 | D1 |    | A2 |    |
                +----+----+----+----+----+
            2   | TF |    |    | FF |    |
                +----+----+----+----+----+
            3   | FF |    |    | FF | FF |
                |    | B1 |    | D2 |    |
                +----+----+----+----+----+
            4   |    |    |    | B2 |    |
                +----+----+----+----+----+
            Y
        */
        /*
         * THIRD BUILD MIRKO
         */
        building = BuildingType.SECOND_FLOOR;
        simpleBuildNoWin(Mirko, MirkoW2, p43, building);
        /*
                  0    1     2    3    4    X
                +----+----+----+----+----+
            0   |    |    |    |    |    |
                +----+----+----+----+----+
            1   | FF |    |    |    |    |
                | A1 | D1 |    | A2 |    |
                +----+----+----+----+----+
            2   | TF |    |    | FF |    |
                +----+----+----+----+----+
            3   | FF |    |    | FF | SF |
                |    | B1 |    | D2 |    |
                +----+----+----+----+----+
            4   |    |    |    | B2 |    |
                +----+----+----+----+----+
            Y
        */
        /*
         * FORTH MOVE ANDREA
         */
        simpleMoveNoWin(Andrea, AndreaW2, p32);
        /*
                  0    1     2    3    4    X
                +----+----+----+----+----+
            0   |    |    |    |    |    |
                +----+----+----+----+----+
            1   | FF |    |    |    |    |
                | A1 | D1 |    |    |    |
                +----+----+----+----+----+
            2   | TF |    |    | FF |    |
                |    |    |    | A2 |    |
                +----+----+----+----+----+
            3   | FF |    |    | FF | SF |
                |    | B1 |    | D2 |    |
                +----+----+----+----+----+
            4   |    |    |    | B2 |    |
                +----+----+----+----+----+
            Y
        */
        /*
         * FORTH BUILD ANDREA
         */
        building = BuildingType.FIRST_FLOOR;
        simpleBuildNoWin(Andrea, AndreaW2, p42, building);
        /*
                  0    1     2    3    4    X
                +----+----+----+----+----+
            0   |    |    |    |    |    |
                +----+----+----+----+----+
            1   | FF |    |    |    |    |
                | A1 | D1 |    |    |    |
                +----+----+----+----+----+
            2   | TF |    |    | FF | FF |
                |    |    |    | A2 |    |
                +----+----+----+----+----+
            3   | FF |    |    | FF | SF |
                |    | B1 |    | D2 |    |
                +----+----+----+----+----+
            4   |    |    |    | B2 |    |
                +----+----+----+----+----+
            Y
        */
        /*
         * FORTH MOVE MATTEO
         */
        Point p44 = new Point(4,4);
        simpleMoveNoWin(Matteo, MatteoW2, p44);
        /*
                  0    1     2    3    4    X
                +----+----+----+----+----+
            0   |    |    |    |    |    |
                +----+----+----+----+----+
            1   | FF |    |    |    |    |
                | A1 | D1 |    |    |    |
                +----+----+----+----+----+
            2   | TF |    |    | FF | FF |
                |    |    |    | A2 |    |
                +----+----+----+----+----+
            3   | FF |    |    | FF | SF |
                |    | B1 |    | D2 |    |
                +----+----+----+----+----+
            4   |    |    |    |    | B2 |
                +----+----+----+----+----+
            Y
        */
        /*
         * FORTH BUILD MATTEO
         */
        Point p34 = new Point(3,4);
        building = BuildingType.FIRST_FLOOR;
        simpleBuildNoWin(Matteo, MatteoW2, p34, building);
        /*
                  0    1     2    3    4    X
                +----+----+----+----+----+
            0   |    |    |    |    |    |
                +----+----+----+----+----+
            1   | FF |    |    |    |    |
                | A1 | D1 |    |    |    |
                +----+----+----+----+----+
            2   | TF |    |    | FF | FF |
                |    |    |    | A2 |    |
                +----+----+----+----+----+
            3   | FF |    |    | FF | SF |
                |    | B1 |    | D2 |    |
                +----+----+----+----+----+
            4   |    |    |    | FF | B2 |
                +----+----+----+----+----+
            Y
        */
        /*
         * FORTH MOVE MIRKO
         */
        simpleMoveNoWin(Mirko, MirkoW2,p43);
        /*
                  0    1     2    3    4    X
                +----+----+----+----+----+
            0   |    |    |    |    |    |
                +----+----+----+----+----+
            1   | FF |    |    |    |    |
                | A1 | D1 |    |    |    |
                +----+----+----+----+----+
            2   | TF |    |    | FF | FF |
                |    |    |    | A2 |    |
                +----+----+----+----+----+
            3   | FF |    |    | FF | SF |
                |    | B1 |    |    | D2 |
                +----+----+----+----+----+
            4   |    |    |    | FF | B2 |
                +----+----+----+----+----+
            Y
        */
        /*
         * FORTH BUILD MIRKO
         */
        building = BuildingType.SECOND_FLOOR;
        simpleBuildNoWin(Mirko,MirkoW2, p33, building);
        /*
                  0    1     2    3    4    X
                +----+----+----+----+----+
            0   |    |    |    |    |    |
                +----+----+----+----+----+
            1   | FF |    |    |    |    |
                | A1 | D1 |    |    |    |
                +----+----+----+----+----+
            2   | TF |    |    | FF | FF |
                |    |    |    | A2 |    |
                +----+----+----+----+----+
            3   | FF |    |    | SF | SF |
                |    | B1 |    |    | D2 |
                +----+----+----+----+----+
            4   |    |    |    | FF | B2 |
                +----+----+----+----+----+
            Y
        */
        /*
         * FIFTH MOVE ANDREA
         */
        simpleMoveNoWin(Andrea,AndreaW2,p33);
        /*
                  0    1     2    3    4    X
                +----+----+----+----+----+
            0   |    |    |    |    |    |
                +----+----+----+----+----+
            1   | FF |    |    |    |    |
                | A1 | D1 |    |    |    |
                +----+----+----+----+----+
            2   | TF |    |    | FF | FF |
                +----+----+----+----+----+
            3   | FF |    |    | SF | SF |
                |    | B1 |    | A2 | D2 |
                +----+----+----+----+----+
            4   |    |    |    | FF | B2 |
                +----+----+----+----+----+
            Y
        */
        /*
         * FIFTH BUILD ANDREA
         */
        building = BuildingType.SECOND_FLOOR;
        simpleBuildNoWin(Andrea, AndreaW2, p42, building);
        /*
                  0    1     2    3    4    X
                +----+----+----+----+----+
            0   |    |    |    |    |    |
                +----+----+----+----+----+
            1   | FF |    |    |    |    |
                | A1 | D1 |    |    |    |
                +----+----+----+----+----+
            2   | TF |    |    | FF | SF |
                +----+----+----+----+----+
            3   | FF |    |    | SF | SF |
                |    | B1 |    | A2 | D2 |
                +----+----+----+----+----+
            4   |    |    |    | FF | B2 |
                +----+----+----+----+----+
            Y
        */
        /*
         * FIFTH MOVE MATTEO
         */
        simpleMoveNoWin(Matteo,MatteoW1,p03);
        /*
                  0    1     2    3    4    X
                +----+----+----+----+----+
            0   |    |    |    |    |    |
                +----+----+----+----+----+
            1   | FF |    |    |    |    |
                | A1 | D1 |    |    |    |
                +----+----+----+----+----+
            2   | TF |    |    | FF | SF |
                +----+----+----+----+----+
            3   | FF |    |    | SF | SF |
                | B1 |    |    | A2 | D2 |
                +----+----+----+----+----+
            4   |    |    |    | FF | B2 |
                +----+----+----+----+----+
            Y
        */
        /*
         * FIFTH BUILD MATTEO
         */
        building = BuildingType.DOME;
        simpleBuildNoWin(Matteo,MatteoW1, p02, building);
        /*
                  0    1     2    3    4    X
                +----+----+----+----+----+
            0   |    |    |    |    |    |
                +----+----+----+----+----+
            1   | FF |    |    |    |    |
                | A1 | D1 |    |    |    |
                +----+----+----+----+----+
            2   | DD |    |    | FF | SF |
                +----+----+----+----+----+
            3   | FF |    |    | SF | SF |
                | B1 |    |    | A2 | D2 |
                +----+----+----+----+----+
            4   |    |    |    | FF | B2 |
                +----+----+----+----+----+
            Y
        */
        /*
         * FIFTH MOVE MIRKO
         */
        simpleMoveNoWin(Mirko, MirkoW2,p42);
        /*
                  0    1     2    3    4    X
                +----+----+----+----+----+
            0   |    |    |    |    |    |
                +----+----+----+----+----+
            1   | FF |    |    |    |    |
                | A1 | D1 |    |    |    |
                +----+----+----+----+----+
            2   | DD |    |    | FF | SF |
                |    |    |    |    | D2 |
                +----+----+----+----+----+
            3   | FF |    |    | SF | SF |
                | B1 |    |    | A2 |    |
                +----+----+----+----+----+
            4   |    |    |    | FF | B2 |
                +----+----+----+----+----+
            Y
        */
        /*
         * FIFTH BUILD MIRKO
         */
        building = BuildingType.THIRD_FLOOR;
        simpleBuildNoWin(Mirko, MirkoW2, p43, building);
        /*
                  0    1     2    3    4    X
                +----+----+----+----+----+
            0   |    |    |    |    |    |
                +----+----+----+----+----+
            1   | FF |    |    |    |    |
                | A1 | D1 |    |    |    |
                +----+----+----+----+----+
            2   | DD |    |    | FF | SF |
                |    |    |    |    | D2 |
                +----+----+----+----+----+
            3   | FF |    |    | SF | TF |
                | B1 |    |    | A2 |    |
                +----+----+----+----+----+
            4   |    |    |    | FF | B2 |
                +----+----+----+----+----+
            Y
        */
        /*
         * SIXTH MOVE ANDREA
         */
        simpleMoveWin(Andrea, AndreaW2, p43);
        assertEquals(model.getWinner(), Andrea);
    }

    void simpleInit(){
        //Init the model
        /*
                  0    1     2    3    4    X
                +----+----+----+----+----+
            0   | A1 |    |    |    | A2 |
                +----+----+----+----+----+
            1   |    | D1 |    |    |    |
                +----+----+----+----+----+
            2   |    |    |    | D2 |    |
                +----+----+----+----+----+
            3   |    | B1 |    |    |    |
                +----+----+----+----+----+
            4   |    |    |    | B2 |    |
                +----+----+----+----+----+
            Y
        */
        model.compileCardStrategy();
        //Init start positions
        Board board = model.getBoard();

        Point p00 = new Point(0,0);
        AndreaW1.setPosition(p00);
        board.getCell(p00).setWorker(AndreaW1.getID());
        Point p40 = new Point(4,0);
        AndreaW2.setPosition(p40);
        board.getCell(p40).setWorker(AndreaW2.getID());

        Point p11 = new Point(1,1);
        MirkoW1.setPosition(p11);
        board.getCell(p11).setWorker(MirkoW1.getID());

        Point p32 = new Point(3,2);
        MirkoW2.setPosition(p32);
        board.getCell(p32).setWorker(MirkoW2.getID());

        Point p13 = new Point(1,3);
        MatteoW1.setPosition(p13);
        board.getCell(p13).setWorker(MatteoW1.getID());

        Point p34 = new Point(3,4);
        MatteoW2.setPosition(p34);
        board.getCell(p34).setWorker(MatteoW2.getID());
    }
    void simpleMoveNoWin(Player player, Worker worker, Point point){
        simpleMove(player,worker,point);
        //Check updated board
        PacketUpdateBoard update = mockView.getPacketUpdateBoard();
        assertNotNull(update);
        assert update.getWorkersPositions().get(worker.getID()).equals(point);
        assertNull(update.getNewBuildings());
        assertNull(update.getPlayerLostID());
        assertNull(update.getPlayerWonID());
    }
    void simpleMoveWin(Player player, Worker worker, Point point){
        simpleMove(player,worker,point);
        //Check updated board
        PacketUpdateBoard update = mockView.getPacketUpdateBoard();
        assertNotNull(update);
        assert update.getWorkersPositions().get(worker.getID()).equals(point);
        assertNull(update.getNewBuildings());
        assertNull(update.getPlayerLostID());
        assertEquals(update.getPlayerWonID(), player.getNickname());
    }

    void simpleMove(Player player, Worker worker, Point point){
        //Expecting packet do action (MOVE) to Andrea
        PacketDoAction packetAction = mockView.getPacketDoAction();
        assertNotNull(packetAction);
        assertEquals(packetAction.getTo(), player.getNickname());
        assertEquals(packetAction.getActionType(), ActionType.MOVE);
        //Ask move info
        List<Point> playerMove = new LinkedList<>();
        PacketMove packetMove = new PacketMove(player.getNickname(), worker.getID(), playerMove);
        turnLogic.getPossibleMoves(player.getNickname(), packetMove);
        PacketPossibleMoves moves = mockView.getPacketPossibleMoves();
        assertNotNull(moves);
        assertEquals(moves.getTo(), player.getNickname());
        Map<String, Set<Point>> helpDataMove = moves.getPossibleMoves();
        //Search if move is allowed
        assert helpDataMove.get(worker.getID()).contains(point);
        //Make the move
        playerMove.add(point);
        packetMove = new PacketMove(player.getNickname(), worker.getID(), playerMove);
        //Check other moves
        turnLogic.getPossibleMoves(player.getNickname(), packetMove);
        moves = mockView.getPacketPossibleMoves();
        assertNotNull(moves);
        assertEquals(moves.getTo(), player.getNickname());
        helpDataMove = moves.getPossibleMoves();
        assert helpDataMove.get(worker.getID()).size() == 0;
        assert helpDataMove.get(worker.getID()).size() == 0;

        try{
            turnLogic.consumePacketMove(player.getNickname(), packetMove);
        } catch (InvalidPacketException e) {
            assert false;
        }
    }

    void simpleBuildNoWin(Player player, Worker worker, Point point, BuildingType building){
        PacketDoAction packetAction = mockView.getPacketDoAction();
        assertNotNull(packetAction);
        assertEquals(packetAction.getTo(), player.getNickname());
        assertEquals(packetAction.getActionType(), ActionType.BUILD);
        //Ask build info
        List<Point> buildOrder = new LinkedList<>();
        List<BuildingType> buildings = new LinkedList<>();
        Map<Point, List<BuildingType>> buildData = new HashMap<>();

        PacketBuild packetBuild = new PacketBuild(player.getNickname(), worker.getID(), buildData, buildOrder);
        turnLogic.getPossibleBuilds(player.getNickname(), packetBuild);
        PacketPossibleBuilds builds = mockView.getPacketPossibleBuilds();
        assertNotNull(builds);
        assertEquals(builds.getTo(), player.getNickname());
        Map<String, Map<Point, List<BuildingType>>> helpDataBuild = builds.getPossibleBuilds();

        //Search if build is allowed
        assert helpDataBuild.get(worker.getID()).get(point).contains(building);
        //Make the build
        buildOrder.add(point);
        buildings.add(building);
        buildData.put(point, buildings);
        packetBuild = new PacketBuild(player.getNickname(), worker.getID(), buildData, buildOrder);
        //Check other builds
        turnLogic.getPossibleBuilds(player.getNickname(), packetBuild);
        builds = mockView.getPacketPossibleBuilds();
        assertNotNull(builds);
        assertEquals(builds.getTo(), player.getNickname());
        helpDataBuild = builds.getPossibleBuilds();
        assert helpDataBuild.get(worker.getID()).size() == 0;
        assert helpDataBuild.get(worker.getID()).size() == 0;

        try{
            turnLogic.consumePacketBuild(player.getNickname(), packetBuild);
        } catch (InvalidPacketException e) {
            assert false;
        }
        //Check updated board
        PacketUpdateBoard update = mockView.getPacketUpdateBoard();
        assertNotNull(update);
        assertNull(update.getWorkersPositions());
        assertNotNull(update.getNewBuildings());
        assert update.getNewBuildings().get(point).contains(building);
        assertNull(update.getPlayerLostID());
        assertNull(update.getPlayerWonID());
    }
}