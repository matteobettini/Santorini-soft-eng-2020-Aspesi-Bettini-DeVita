package it.polimi.ingsw.CLI;

import it.polimi.ingsw.CLI.strategies.*;
import it.polimi.ingsw.CLI.strategies.game_mode_strategy.GameModeStrategy;
import it.polimi.ingsw.CLI.strategies.game_mode_strategy.HardcoreStrategy;
import it.polimi.ingsw.CLI.strategies.game_mode_strategy.NormalStrategy;
import it.polimi.ingsw.Client;
import it.polimi.ingsw.model.enums.ActionType;

import java.util.regex.Pattern;

public class CLI {
    private static final String zeroTo255 = "([01]?[0-9]{1,2}|2[0-4][0-9]|25[0-5])";

    private static final String IP_REGEXP = zeroTo255 + "\\." + zeroTo255 + "\\." + zeroTo255 + "\\." + zeroTo255;

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

    private ViewModel viewModel;


    public CLI(){
        this.viewModel = ViewModel.getInstance();
        this.askConnectionParameters = true;

        CharStream stream = new CharStream(159, 30);
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

        setInitialStrategies();

        viewModel.setClient();

        Client client = viewModel.getClient();

        client.addConnectionStatusObserver( connectionStatus -> connectionStrategy.handleConnection(connectionStatus, this));

        client.addInsertNickRequestObserver( (message, isRetry) -> nicknameStrategy.handleNickname(message));

        client.addInsertNumOfPlayersAndGamemodeRequestObserver( ((message, isRetry) -> requestNumberOfPlayersGameModeStrategy.handleRequestNumberOfPlayerGameMode(message)));

        client.addPacketMatchStartedObserver( packetMatchStarted -> matchStartedStrategy.handleMatchStarted(packetMatchStarted));

        client.addPacketCardsFromServerObserver( (packetCardsFromServer, isRetry) -> selectCardStrategy.handleCardStrategy(packetCardsFromServer, isRetry));

        client.addPacketSetupObserver( packetSetup -> setupStrategy.handleSetup(packetSetup, this));

        client.addPacketDoActionObserver( (packetDoAction, isRetry) -> {
            viewModel.setCurrentActivePlayer(packetDoAction.getTo());
            if(!packetDoAction.getTo().equals(viewModel.getPlayerName())){
                System.out.println("\nIt's " + packetDoAction.getTo() + "'s turn...");
                return;
            }

            if(packetDoAction.getActionType() == ActionType.CHOOSE_START_PLAYER){
                chooseStarterStrategy.handleChooseStartPlayer();
            }
            else if (packetDoAction.getActionType() == ActionType.SET_WORKERS_POSITION){
                setWorkersPositionStrategy.handleSetWorkersPosition(isRetry);
            }
            else{
                gameModeStrategy.handleAction(packetDoAction, isRetry);
            }
        });

        client.addPacketUpdateBoardObserver( packetUpdateBoard -> updateBoardStrategy.handleUpdateBoard(packetUpdateBoard, this));

        client.addPacketPossibleMovesObserver( packetPossibleMoves -> gameModeStrategy.handlePossibleMoves(packetPossibleMoves));

        if(askConnectionParameters) setConnectionParameters();
        client.start(address, port);
    }

    public ConnectionStrategy getConnectionStrategy() {
        return connectionStrategy;
    }

    public void setAskConnectionParameters(boolean askConnectionParameters) {
        this.askConnectionParameters = askConnectionParameters;
    }

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

    public void setConnectionInGameStrategy(){
        connectionStrategy = new ConnectionInGameStrategy();
    }

    public void setGameModeStrategy(boolean hardcore){
        if(hardcore) gameModeStrategy = new HardcoreStrategy();
        else gameModeStrategy = new NormalStrategy();
    }

    private void setConnectionParameters(){
        String address;
        Integer port;

        do{
            System.out.print("Enter the server's IP address: ");
            address = InputUtilities.getLine();
            System.out.print("Enter the server's port: ");
            port = InputUtilities.getInt("Not a number, retry\nEnter the server's port: ");
            if(port == null) port = - 1;
        }while (!addressIsValid(address, port));

        this.address = address;
        this.port = port;
    }

    private boolean addressIsValid(String address, int port) {
        if(address == null || port == -1) return false;
        return IP_PATTERN.matcher(address).matches() && port >= 1 && port <= 65535;
    }

}