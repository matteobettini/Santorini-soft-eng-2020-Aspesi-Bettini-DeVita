package it.polimi.ingsw.CLI;

import it.polimi.ingsw.CLI.Strategies.*;
import it.polimi.ingsw.Client;
import it.polimi.ingsw.ClientImpl;
import it.polimi.ingsw.model.enums.ActionType;

import java.util.regex.Pattern;

public class CLI {
    private static final String zeroTo255 = "([01]?[0-9]{1,2}|2[0-4][0-9]|25[0-5])";

    private static final String IP_REGEXP = zeroTo255 + "\\." + zeroTo255 + "\\." + zeroTo255 + "\\." + zeroTo255;

    private static final Pattern IP_PATTERN = Pattern.compile(IP_REGEXP);

    private static final String NICKNAME_REGEXP = "[a-zA-Z0-9._\\-]{3,}";

    private static final Pattern NICKNAME_PATTERN = Pattern.compile(NICKNAME_REGEXP);

    private Client client;
    private String address;
    private int port;
    private boolean askConnectionParameters;
    private boolean isToRestart;

    private ConnectionStrategy connectionStrategy;
    private ActionStrategy actionStrategy;
    private NicknameStrategy nicknameStrategy;
    private NumberOfPlayersStrategy numberOfPlayersStrategy;
    private RequestGameModeStrategy requestGameModeStrategy;
    private MatchStartedStrategy matchStartedStrategy;
    private SelectCardStrategy selectCardStrategy;
    private SetupStrategy setupStrategy;
    private ChooseStarterStrategy chooseStarterStrategy;
    private SetWorkersPositionStrategy setWorkersPositionStrategy;
    private UpdateBoardStrategy updateBoardStrategy;

    private Board board;
    private GraphicalBoard graphicalBoard;
    private GraphicalMatchMenu graphicalMatchMenu;
    private CharStream stream;

    public CLI(){
        this.askConnectionParameters = true;
        this.board = new Board();

        this.stream = new CharStream(159, 30);
        GraphicalStartMenu graphicalStartMenu = new GraphicalStartMenu(stream,159, 30);

        graphicalStartMenu.draw();
        stream.print(System.out);
        stream.reset();
    }

    public static void main(String[] args){
        CLI cli = new CLI();
        cli.run();

    }

    public void run(){

        stream = new CharStream(159, 50);
        graphicalBoard = new GraphicalBoard(stream);
        graphicalMatchMenu = new GraphicalMatchMenu(stream);

        setInitialStrategies();

        client = new ClientImpl();

        client.addConnectionStatusObserver( connectionStatus -> {
            connectionStrategy.handleConnection(connectionStatus, this);
        });

        client.addInsertNickRequestObserver( (message, isRetry) -> {
            nicknameStrategy.handleNickname(message, this);
        });

        client.addInsertNumOfPlayersRequestObserver( (message, isRetry) -> {
            numberOfPlayersStrategy.handNumberOfPlayers(message, this);
        });

        client.addInsertGamemodeRequestObserver( message -> {
            requestGameModeStrategy.handleRequestGameMode(message, this);
        });

        client.addPacketMatchStartedObserver( packetMatchStarted -> {
            matchStartedStrategy.handleMatchStarted(packetMatchStarted, this);
        });

        client.addPacketCardsFromServerObserver( (packetCardsFromServer, isRetry) -> {
            selectCardStrategy.handleCardStrategy(packetCardsFromServer, this);
        });

        client.addPacketSetupObserver( packetSetup -> {
            setupStrategy.handleSetup(packetSetup, this);
        });

        client.addPacketDoActionObserver( (packetDoAction, isRetry) -> {
            //WE UPDATE THE CURRENT PLAYER IN THE MATCH MENU
            graphicalMatchMenu.setActivePlayer(packetDoAction.getTo());
            if(!packetDoAction.getTo().equals(board.getPlayerName())){
                System.out.println("\nIt's " + packetDoAction.getTo() + "'s turn...");
                return;
            }

            if(packetDoAction.getActionType() == ActionType.CHOOSE_START_PLAYER){
                chooseStarterStrategy.handleChooseStartPlayer(this);
            }
            else if (packetDoAction.getActionType() == ActionType.SET_WORKERS_POSITION){
                setWorkersPositionStrategy.handleSetWorkersPosition(this, isRetry);
            }
        });

        client.addPacketUpdateBoardObserver( packetUpdateBoard -> {
            updateBoardStrategy.handleUpdateBoard(packetUpdateBoard, this);
        });

        if(askConnectionParameters) setConnectionParameters();
        client.start(address, port);
    }

    public void setAskConnectionParameters(boolean askConnectionParameters) {
        this.askConnectionParameters = askConnectionParameters;
    }

    private void setInitialStrategies(){
        connectionStrategy = new ConnectionSetupStrategy();
        nicknameStrategy = new DefaultNicknameStrategy();
        numberOfPlayersStrategy = new DefaultNumberOfPlayers();
        requestGameModeStrategy = new DefaultRequestGameModeStrategy();
        matchStartedStrategy = new DefaultMatchStartedStrategy();
        selectCardStrategy = new DefaultSelectCardStrategy();
        setupStrategy = new DefaultSetupStrategy();
        chooseStarterStrategy = new DefaultChooseStarterStrategy();
        setWorkersPositionStrategy = new DefaultSetWorkersPositionStrategy();
        updateBoardStrategy = new DefaultUpdateBoardStrategy();
    }

    private void setConnectionParameters(){
        String address;
        Integer port;

        do{
            System.out.print("Enter the server's IP address: ");
            address = InputUtilities.getLine();
            System.out.print("Enter the server's port: ");
            port = InputUtilities.getInt();
            if(port == null) port = - 1;
        }while (!addressIsValid(address, port));

        this.address = address;
        this.port = port;
    }

    public void setConnectionInGameStrategy(){
        connectionStrategy = new ConnectionInGameStrategy();
    }

    public void setActionStrategy(boolean hardcore){
        if(hardcore) actionStrategy = new HardcoreStrategy();
        else actionStrategy = new NormalStrategy();
    }

    public static boolean addressIsValid(String address, int port) {
        if(address == null || port == -1) return false;
        return IP_PATTERN.matcher(address).matches() && port >= 1 && port <= 65535;
    }

    public GraphicalBoard getGraphicalBoard() {
        return graphicalBoard;
    }

    public GraphicalMatchMenu getGraphicalMatchMenu() {
        return graphicalMatchMenu;
    }

    public CharStream getStream() {
        return stream;
    }

    public Board getBoard() {
        return board;
    }

    public Client getClient() {
        return client;
    }

}

  /*CharStream stream = new CharStream(159, 50);
        GraphicalBoard graphicalBoard = new GraphicalBoard(stream);
        GraphicalOcean graphicalOcean = new GraphicalOcean(stream,159, 50);
        graphicalBoard.getCell(new Point(0,0)).addBuilding(BuildingType.FIRST_FLOOR);
        graphicalBoard.getCell(new Point(0,0)).addBuilding(BuildingType.SECOND_FLOOR);
        graphicalBoard.getCell(new Point(0,0)).addBuilding(BuildingType.THIRD_FLOOR);
        graphicalBoard.getCell(new Point(1,2)).addBuilding(BuildingType.FIRST_FLOOR);
        graphicalBoard.getCell(new Point(1,2)).addBuilding(BuildingType.SECOND_FLOOR);
        graphicalBoard.getCell(new Point(1,2)).addBuilding(BuildingType.THIRD_FLOOR);
        graphicalBoard.getCell(new Point(1,2)).addBuilding(BuildingType.DOME);
        graphicalBoard.getCell(new Point(0, 0)).setWorker( Color.WHITE ,'1', "Mirko");
        graphicalBoard.getCell(new Point(0,1)).addBuilding(BuildingType.FIRST_FLOOR);
        graphicalBoard.getCell(new Point(0,2)).addBuilding(BuildingType.DOME);
        graphicalBoard.getCell(new Point(1,1)).addBuilding(BuildingType.FIRST_FLOOR);
        graphicalBoard.getCell(new Point(1,1)).addBuilding(BuildingType.SECOND_FLOOR);
        graphicalBoard.getCell(new Point(4,4)).addBuilding(BuildingType.FIRST_FLOOR);
        graphicalBoard.getCell(new Point(4,4)).addBuilding(BuildingType.SECOND_FLOOR);
        graphicalBoard.getCell(new Point(1, 1)).setWorker(Color.ORANGE,'1', "Matteo");
        graphicalBoard.getCell(new Point(0, 1)).setWorker(Color.ORANGE, '2', "Matteo");
        graphicalBoard.getCell(new Point(4, 4)).setWorker(Color.CYAN, '1', "Andrea");
        graphicalBoard.getCell(new Point(4, 2)).setWorker(Color.CYAN, '2', "Andrea");
        graphicalOcean.draw();
        List<Point> possiblePositions = new ArrayList<>();
        possiblePositions.add(new Point(1,1));
        graphicalBoard.setPossibleActions(possiblePositions);
        List<Point> notPossiblePositions = new ArrayList<>();
        notPossiblePositions.add(new Point(1,2));
        graphicalBoard.draw();
        Map<String , Color> players = new HashMap<>();
        players.put("123456789012345", Color.CYAN);
        players.put("Andrea", Color.ORANGE);
        players.put("Matteo", Color.WHITE);
        Map<String , String> playerGodCard = new HashMap<>();
        playerGodCard.put("123456789012345", "123456789012345");
        playerGodCard.put("Andrea", "Prometheus");
        playerGodCard.put("Matteo", "Persephone");
        GraphicalMatchMenu graphicalMatchMenu = new GraphicalMatchMenu(stream, players, playerGodCard);
        graphicalMatchMenu.setActivePlayer("Matteo");
        graphicalMatchMenu.setLoser("Andrea");
        graphicalMatchMenu.setGameOver(false);
        graphicalMatchMenu.setYouWin(false);
        graphicalMatchMenu.draw();
        stream.print(System.out);

        stream.reset();
        Map<String, String> godCards = new HashMap<>();
        godCards.put("Athena", "Opponent’s Turn: If one of your Workers moved up on your last turn, opponent Workers cannot move up this turn.");
        godCards.put("Apollo","Your Move: Your Worker may move into an opponent Worker’s space by forcing their Worker to the space yours just vacated.");
        godCards.put("Artemis", "Your Move: Your Worker may move one additional time, but not back to its initial space.");
        godCards.put("Atlas", "Your Build: Your Worker may build a dome at any level.");
        godCards.put("Demeter", "Your Build: Your Worker may build one additional time, but not on the same space.");
        godCards.put("Hephaestus", "Your Build: Your Worker may build one additional block (not dome) on top of your first block.");
        godCards.put("Minotaur", "Your Move: Your Worker may move into an opponent Worker’s space, if their Worker can be forced one space straight backwards to an unoccupied space at any level.");
        godCards.put("Pan", "Win Condition: You also win if your Worker moves down two or more levels.");
        godCards.put("Prometheus", "Your Turn: If your Worker does not move up, it may build both before and after moving.");

        GraphicalCardsMenu graphicalCardsMenu = new GraphicalCardsMenu(godCards);
        List<String> chosenCards = new ArrayList<>();
        chosenCards.add("Athena");
        chosenCards.add("Apollo");
        chosenCards.add("Pan");
        graphicalCardsMenu.setChosenCards(chosenCards);
        stream = new CharStream(graphicalCardsMenu.getRequiredWidth(),graphicalCardsMenu.getRequiredHeight());
        graphicalCardsMenu.setStream(stream);
        graphicalCardsMenu.draw();
        stream.print(System.out);
        stream.reset();


        stream = new CharStream(159, 30);
        GraphicalStartMenu graphicalStartMenu = new GraphicalStartMenu(stream,159, 30);
        graphicalStartMenu.draw();
        stream.print(System.out);*/