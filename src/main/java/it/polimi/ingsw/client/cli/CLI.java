package it.polimi.ingsw.client.cli;


import it.polimi.ingsw.client.cli.match_data.MatchData;
import it.polimi.ingsw.client.cli.strategies.*;
import it.polimi.ingsw.client.cli.strategies.game_mode_strategy.GameModeStrategy;
import it.polimi.ingsw.client.cli.strategies.game_mode_strategy.HardcoreStrategy;
import it.polimi.ingsw.client.cli.strategies.game_mode_strategy.NormalStrategy;
import it.polimi.ingsw.client.cli.utilities.InputUtilities;
import it.polimi.ingsw.client.cli.utilities.OutputUtilities;
import it.polimi.ingsw.client.communication.Client;
import it.polimi.ingsw.common.enums.ActionType;

import java.util.regex.Pattern;

public class CLI {
    private static final String zeroTo255 = "([01]?[0-9]{1,2}|2[0-4][0-9]|25[0-5])";

    private static final String IP_REGEXP = "^(" + zeroTo255 + "\\." + zeroTo255 + "\\." + zeroTo255 + "\\." + zeroTo255 + ")$";

    private static final Pattern IP_PATTERN = Pattern.compile(IP_REGEXP);

    private String address;
    private int port;
    private boolean askConnectionParameters;

    private ConnectionStrategy connectionStrategy;
    private GameModeStrategy gameModeStrategy;
    private NicknameStrategy nicknameStrategy;
    private RequestNumberOfPlayersGameModeStrategy requestNumberOfPlayersGameModeStrategy;
    private MatchStartedStrategy matchStartedStrategy;
    private SelectCardStrategy selectCardStrategy;
    private SetupStrategy setupStrategy;
    private ChooseStarterStrategy chooseStarterStrategy;
    private SetWorkersPositionStrategy setWorkersPositionStrategy;
    private UpdateBoardStrategy updateBoardStrategy;

    private final MatchData matchData;


    /**
     * This constructor initialized the singleton instance of MatchData and prints the GraphicalStartMenu.
     */
    public CLI(){
        this.matchData = MatchData.getInstance();
        this.askConnectionParameters = true;

        OutputUtilities.printStartMenu();
    }

    /**
     * The main creates a new instance of CLI and calls the CLI's run method.
     * @param args Arguments
     */
    public static void main(String[] args){
        CLI cli = new CLI();
        cli.run();
    }

    /**
     * This method sets the initial strategies, then adds all the needed client's observers
     * and finally asks the server's address and port to the user.
     */
    public void run(){
        setInitialStrategies();

        matchData.reset();

        matchData.setNewClient();

        Client client = matchData.getClient();

        client.addConnectionStatusObserver( connectionStatus -> connectionStrategy.handleConnection(connectionStatus, this));

        client.addInsertNickRequestObserver( (message, isRetry) -> nicknameStrategy.handleNickname(message));

        client.addInsertNumOfPlayersAndGamemodeRequestObserver( ((message, isRetry) -> requestNumberOfPlayersGameModeStrategy.handleRequestNumberOfPlayerGameMode(message, isRetry)));

        client.addPacketMatchStartedObserver( packetMatchStarted -> matchStartedStrategy.handleMatchStarted(packetMatchStarted, this));

        client.addPacketCardsFromServerObserver( (packetCardsFromServer, isRetry) -> selectCardStrategy.handleCardStrategy(packetCardsFromServer, isRetry));

        client.addPacketSetupObserver( packetSetup -> setupStrategy.handleSetup(packetSetup));

        client.addPacketDoActionObserver( (packetDoAction, isRetry) -> {
            String activePlayer = packetDoAction.getTo();
            matchData.setCurrentActivePlayer(activePlayer);
            ActionType actionType = packetDoAction.getActionType();

            if(actionType == ActionType.CHOOSE_START_PLAYER) chooseStarterStrategy.handleChooseStartPlayer(activePlayer,isRetry);
            else if (actionType == ActionType.SET_WORKERS_POSITION) setWorkersPositionStrategy.handleSetWorkersPosition(activePlayer, isRetry);
            else gameModeStrategy.handleAction(packetDoAction, isRetry);
        });

        client.addPacketUpdateBoardObserver( packetUpdateBoard -> updateBoardStrategy.handleUpdateBoard(packetUpdateBoard));

        client.addPacketPossibleMovesObserver( packetPossibleMoves -> gameModeStrategy.handlePossibleMoves(packetPossibleMoves));

        client.addPacketPossibleBuildsObserver( packetPossibleBuilds -> gameModeStrategy.handlePossibleBuilds(packetPossibleBuilds));

        if(askConnectionParameters) setConnectionParameters();
        client.asyncStart(address, port,false);
    }

    /**
     * This method sets the boolean used to indicating the need of setting new connection parameters.
     * @param askConnectionParameters is true if parameters should be set, false otherwise,
     */
    public void setAskConnectionParameters(boolean askConnectionParameters) {
        this.askConnectionParameters = askConnectionParameters;
    }

    /**
     * This method sets the initial strategies.
     */
    private void setInitialStrategies(){
        connectionStrategy = new ConnectionSetupStrategy();
        nicknameStrategy = new DefaultNicknameStrategy();
        requestNumberOfPlayersGameModeStrategy = new DefaultRequestNumberOfPlayersGameModeStrategy();
        matchStartedStrategy = new DefaultMatchStartedStrategy();
        selectCardStrategy = new DefaultSelectCardStrategy();
        setupStrategy = new DefaultSetupStrategy();
        chooseStarterStrategy = new DefaultChooseStarterStrategy();
        setWorkersPositionStrategy = new DefaultSetWorkersPositionStrategy();
        updateBoardStrategy = new DefaultUpdateBoardStrategy();
    }

    /**
     * This method sets the connection strategy to the ConnectionInGameStrategy.
     */
    public void setConnectionInGameStrategy(){
        connectionStrategy = new ConnectionInGameStrategy();
    }

    /**
     * This method sets the game-mode strategy to the HardcoreStrategy or the NormalStrategy based on the given boolean.
     * @param hardcore is true if the game-mode is hardcore, false if it is normal.
     */
    public void setGameModeStrategy(boolean hardcore){
        if(hardcore) gameModeStrategy = new HardcoreStrategy();
        else gameModeStrategy = new NormalStrategy();
    }

    /**
     * This methods ask the server's connection parameters to the user.
     */
    private void setConnectionParameters(){
        String address;
        Integer port;

        boolean firstLoop = true;
        do{
            if(firstLoop) System.out.print("Enter the server's IP address or d (default configuration): ");
            else  System.out.print("IP address not valid, enter the server's IP address: ");
            address = InputUtilities.getLine();
            assert address != null;
            firstLoop = false;
            if(address.toLowerCase().equals("d")){
                this.address = MatchData.DEFAULT_ADDRESS;
                this.port = MatchData.DEFAULT_PORT;
                return;
            }
        }while (!addressIsValid(address));


        firstLoop = true;

        do{
            if(firstLoop) System.out.print("Enter the server's port: ");
            else System.out.print("Enter a valid server's port: ");
            port = InputUtilities.getInt("Not valid, enter the server's port: ");
            assert port != null;
            firstLoop = false;
        }while (!portIsValid(port));

        this.address = address;
        this.port = port;
    }

    /**
     * This method checks through a regexp if the given IP address is valid.
     * @param address is the IP address to check.
     * @return true if the IP is valid, false otherwise.
     */
    private boolean addressIsValid(String address) {
        return address != null && IP_PATTERN.matcher(address).matches();
    }

    /**
     * This method checks if the given number of port is valid.
     * @param port is the port to validate.
     * @return true if the port is valid, false otherwise.
     */
    private boolean portIsValid(int port){
        return port >= 1024 && port <= 65535;
    }
}
