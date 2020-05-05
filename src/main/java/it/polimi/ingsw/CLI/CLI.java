package it.polimi.ingsw.CLI;

import it.polimi.ingsw.Client;
import it.polimi.ingsw.ClientImpl;
import it.polimi.ingsw.model.enums.ActionType;
import it.polimi.ingsw.model.enums.BuildingType;
import it.polimi.ingsw.packets.PacketCardsFromClient;
import it.polimi.ingsw.packets.PacketStartPlayer;
import it.polimi.ingsw.packets.PacketWorkersPositions;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.regex.Pattern;

public class CLI {
    private static final String zeroTo255 = "([01]?[0-9]{1,2}|2[0-4][0-9]|25[0-5])";

    private static final String IP_REGEXP = zeroTo255 + "\\." + zeroTo255 + "\\." + zeroTo255 + "\\." + zeroTo255;

    private static final Pattern IP_PATTERN = Pattern.compile(IP_REGEXP);

    private static final String NICKNAME_REGEXP = "[a-zA-Z0-9._\\-]{3,}";

    private static final Pattern NICKNAME_PATTERN = Pattern.compile(NICKNAME_REGEXP);

    private Client client;
    private ConnectionStrategy connectionStrategy;
    private ActionStrategy actionStrategy;
    private Board board;
    private GraphicalBoard graphicalBoard;
    private GraphicalMatchMenu graphicalMatchMenu;
    private CharStream stream;

    public CLI(){
        this.board = new Board();
    }


    public static void main(String[] args){
        CLI cli = new CLI();
        cli.run(false);

    }

    public void run(boolean restart){
        if(!restart){
            CharStream stream = new CharStream(159, 30);
            GraphicalStartMenu graphicalStartMenu = new GraphicalStartMenu(stream,159, 30);

            graphicalStartMenu.draw();
            stream.print(System.out);
            stream.reset();

        }

        stream = new CharStream(159, 50);
        graphicalBoard = new GraphicalBoard(stream);
        graphicalMatchMenu = new GraphicalMatchMenu(stream);

        start();
        setup();
        match();
        startConnection();
    }

    public void match(){
        client.addPacketUpdateBoardObserver( packetUpdateBoard -> {

            //FIRST WE UPDATE THE BOTH THE BOARD AND THE GRAPHICAL ONE
            if(packetUpdateBoard.getNewBuildings() != null){
                for(Point pos : packetUpdateBoard.getNewBuildings().keySet()){
                    for(BuildingType building : packetUpdateBoard.getNewBuildings().get(pos)){
                        board.getCell(pos).addBuilding(building);
                        graphicalBoard.getCell(pos).addBuilding(building);
                        graphicalMatchMenu.decrementCounter(building, 1);
                    }
                }

            }

            //RESET GAME OVER AND YOU WIN
            graphicalMatchMenu.setGameOver(false);
            graphicalMatchMenu.setYouWin(false);


            //SET UPDATED WORKERS' POSITIONS
            if(packetUpdateBoard.getWorkersPositions() != null){
                //RESET WORKERS' POSITIONS
                board.resetWorkers();
                graphicalBoard.resetWorkers();

                for(String worker : packetUpdateBoard.getWorkersPositions().keySet()){
                    board.getCell(packetUpdateBoard.getWorkersPositions().get(worker)).setWorker(worker);

                    //UPDATE FOR THE GRAPHICAL BOARD
                    String workerOwner = "";
                    char workerNumber = '\0';
                    for(String player : board.getIds().keySet()){
                        for(int i = 1; i <= 2; ++i){
                            if(board.getIds().get(player).get(i).equals(worker)){
                                if(i == 1) workerNumber = '1';
                                else workerNumber = '2';
                                workerOwner = player;
                            }
                        }

                    }
                    Color colorOwner = board.getPlayersColor().get(workerOwner);
                    graphicalBoard.getCell(packetUpdateBoard.getWorkersPositions().get(worker)).setWorker(colorOwner, workerNumber, workerOwner);
                }
            }


            //IF THERE IS A LOSER OR A WINNER WE SET IT
            if(packetUpdateBoard.getPlayerLostID() != null){
                String loser = packetUpdateBoard.getPlayerLostID();
                board.setLoser(loser);

                //WE ALSO SET IT IN THE MATCH MENU
                graphicalMatchMenu.setLoser(loser);
                if(loser.equals(board.getPlayerName())) graphicalMatchMenu.setGameOver(true);
            }
            if(packetUpdateBoard.getPlayerWonID() != null){
                String winner = packetUpdateBoard.getPlayerWonID();
                board.setWinner(winner);

                //IF THE ACTIVE PLAYER WON WE SET YOU WIN
                if(winner.equals(board.getPlayerName())) graphicalMatchMenu.setYouWin(true);
            }

            GraphicalOcean graphicalOcean = new GraphicalOcean(stream,159, 50);
            graphicalOcean.draw();
            graphicalBoard.draw();
            graphicalMatchMenu.draw();
            stream.print(System.out);
            stream.reset();

        });
    }

    public void setup(){
        client.addInsertNickRequestObserver(message -> {
            String nickname;
            do{
                System.out.print("Insert your nickname: ");
                nickname = InputUtilities.getLine();
                if(nickname == null) nickname = "";
            }while(!NICKNAME_PATTERN.matcher(nickname).matches());
            board.setPlayerName(nickname);
            client.sendString(nickname);
        });

        client.addInsertNumOfPlayersRequestObserver( message -> {
            Integer number;
            do{
                System.out.print("Insert the number of players: ");
                number = InputUtilities.getInt();
                if(number == null) number = 0;
            }while(!(number == 2 || number == 3));
            client.sendInt(number);
        });

        client.addInsertGamemodeRequestObserver( message -> {
            String choice;
            do{
                System.out.println("Do you want to play in hardcore mode? (y | n)");
                choice = InputUtilities.getLine();
                if(choice == null) choice = "";
            }while(!(choice.equals("y") || choice.equals("n") || choice.equals("Y") || choice.equals("N")));

            if(choice.equals("y") || choice.equals("Y")){
                client.sendBoolean(true);

            }
            else{
                client.sendBoolean(false);
            }
        });

        client.addPacketMatchStartedObserver( packetMatchStarted -> {
            System.out.println("\n" +"The match has started!");
            System.out.println("Players in game: ");
            for(String player : packetMatchStarted.getPlayers()){
                System.out.println("- " + player);
            }
            System.out.print("Selected mode: ");
            if(packetMatchStarted.isHardcore()) System.out.println("Hardcore");
            else System.out.println("Normal");
            board.setHardcore(packetMatchStarted.isHardcore());
        });

        client.addPacketCardsFromServerObserver( packetCardsFromServer -> {
            if(packetCardsFromServer.getAllCards() != null) board.setAllCards(packetCardsFromServer.getAllCards());
            if(!packetCardsFromServer.getTo().equals(board.getPlayerName())) return;
            GraphicalCardsMenu graphicalCardsMenu = new GraphicalCardsMenu();
            graphicalCardsMenu.setGodCards(board.getAllCards());
            if(packetCardsFromServer.getAvailableCards().size() <= 3) graphicalCardsMenu.setAvailableCards(packetCardsFromServer.getAvailableCards());
            CharStream stream = new CharStream(graphicalCardsMenu.getRequiredWidth(),graphicalCardsMenu.getRequiredHeight());
            graphicalCardsMenu.setStream(stream);
            graphicalCardsMenu.draw();
            stream.print(System.out);
            stream.reset();
            int number = packetCardsFromServer.getNumberToChoose();
            if(number > 1) System.out.println("\n" +"You are the challenger!");
            String chosenCards;
            Set<String> chosenCardsSet;
            boolean check = false;
            do{
                System.out.println("Choose " + number + " " + (number == 1 ? "card" : "cards (ex. Athena, Apollo, ...)"));
                chosenCards = InputUtilities.getLine();
                if(chosenCards == null) chosenCards = "";
                chosenCardsSet = new HashSet<>(Arrays.asList(chosenCards.split("\\s*,\\s*")));
                if(packetCardsFromServer.getAvailableCards().containsAll(chosenCardsSet) && chosenCardsSet.size() == number) check = true;
            }while(!check);
            List<String> chosenCardsList = new ArrayList<>(chosenCardsSet);
            PacketCardsFromClient packetCardsFromClient = new PacketCardsFromClient(chosenCardsList);
            client.send(packetCardsFromClient);
        });

        client.addPacketSetupObserver( packetSetup -> {
            board.setIds(packetSetup.getIds());
            board.setPlayersColor(packetSetup.getColors());
            board.setPlayersCards(packetSetup.getCards());
            board.setHardcore(packetSetup.isHardcore());
            setActionStrategy(packetSetup.isHardcore());

            //UPDATE THE MATH MENU
            graphicalMatchMenu.setPlayers(packetSetup.getColors());
            Map<String, String> playersCardAssociation = new HashMap<>();
            for(String player : packetSetup.getCards().keySet()){
                playersCardAssociation.put(player, packetSetup.getCards().get(player).getKey());
            }
            graphicalMatchMenu.setPlayersGodCardAssociation(playersCardAssociation);
        });

        client.addPacketDoActionObserver( packetDoAction -> {
            //WE UPDATE THE CURRENT PLAYER IN THE MATCH MENU
            graphicalMatchMenu.setActivePlayer(packetDoAction.getTo());
            if(!packetDoAction.getTo().equals(board.getPlayerName())) return;
            if(packetDoAction.getActionType() == ActionType.CHOOSE_START_PLAYER){
                String startPlayer;
                do{
                    System.out.print("\n" + "Choose a start player by writing his name ( ");
                    Set<String> players = board.getIds().keySet();
                    int size = players.size();
                    int count = 1;
                    for(String player : players){
                        if(count != size) System.out.print(player + ", ");
                        else System.out.print(player + " ");
                        ++count;
                    }
                    System.out.print("): ");
                    startPlayer = InputUtilities.getLine();
                    if(startPlayer == null) startPlayer = "";
                }while (!board.getIds().containsKey(startPlayer));

                PacketStartPlayer packetStartPlayer = new PacketStartPlayer(startPlayer);
                client.send(packetStartPlayer);
            }
            else if (packetDoAction.getActionType() == ActionType.SET_WORKERS_POSITION){
                Map<String, Point> positions = new HashMap<>();
                List<String> workersID = board.getIds().get(board.getPlayerName());
                for(int i = 1; i <= 2; ++i){
                    String pos;
                    List<String> coordinates;
                    boolean check = false;
                    do{
                        System.out.print("Choose your worker" + i + "'s position" + (i == 1 ? " (ex. 1, 2)" : "") + ": ");
                        pos = InputUtilities.getLine();
                        if(pos == null) pos = "";
                        coordinates = Arrays.asList(pos.split("\\s*,\\s*"));
                        if(coordinates.size() == 2){
                            int x = Integer.parseInt(coordinates.get(0));
                            int y = Integer.parseInt(coordinates.get(1));
                            Point helper = new Point(x, y);
                            if(positions.containsValue(helper)) System.out.println("Position already chosen!");
                            if(board.getCell(helper) != null && !positions.containsValue(helper)){
                                positions.put(workersID.get(i - 1), helper);
                                check = true;
                            }
                        }
                    }while(!check);
                }

                PacketWorkersPositions packetWorkersPositions = new PacketWorkersPositions(positions);
                client.send(packetWorkersPositions);

            }
        });
    }

    public void start(){
        connectionStrategy = new ConnectionSetupStrategy();
        client = new ClientImpl();

        client.addConnectionStatusObserver(connectionStatus -> {
            if(connectionStrategy.handleConnection(connectionStatus)) run(true);
        });

    }

    public void startConnection(){
        String address;
        Integer port;

        do{
            System.out.print("Enter the server's IP address: ");
            address = InputUtilities.getLine();
            System.out.print("Enter the server's port: ");
            port = InputUtilities.getInt();
            if(port == null) port = - 1;
        }while (!addressIsValid(address, port));

        client.start(address, port);
    }

    public void setActionStrategy(boolean hardcore){
        if(hardcore) actionStrategy = new HardcoreStrategy();
        else actionStrategy = new NormalStrategy();
    }

    public static boolean addressIsValid(String address, int port) {
        if(address == null || port == -1) return false;
        return IP_PATTERN.matcher(address).matches() && port >= 1 && port <= 65535;
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