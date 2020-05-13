package it.polimi.ingsw.model;

import it.polimi.ingsw.cards.CardFile;
import it.polimi.ingsw.model.enums.ActionType;
import it.polimi.ingsw.model.enums.SetupPhase;
import it.polimi.ingsw.utils.Pair;
import it.polimi.ingsw.utils.observe.Observer;
import it.polimi.ingsw.packets.*;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

class SetupManager{

    private List<Player> players;
    private SetupPhase setupPhase;
    private int activePlayerIndex;
    private Player challenger;
    private Player startingPlayer;
    private final Map<Player, CardFile> cardAssociations;
    private final InternalModel model;
    private final List<CardFile> cards;
    private List<String> availableCards;
    private final Map<String, String> allCards;
    private int numberOfCardsToChoose;

    private final List<Observer<PacketSetup>> packetSetupObservers;
    private final List<Observer<PacketCardsFromServer>> packetCardsFromServerObservers;
    private final List<Observer<PacketDoAction>> packetDoActionObservers;
    private final List<Observer<PacketUpdateBoard>> packetUpdateBoardObservers;

    

    SetupManager(InternalModel model, List<CardFile> cards) {
        super();
        this.setupPhase = SetupPhase.STARTING;
        this.challenger = null;
        this.cardAssociations = new HashMap<>();
        this.activePlayerIndex = 0;
        this.model = model;
        this.cards = cards;
        this.players = model.getPlayers();
        this.numberOfCardsToChoose = players.size();
        this.startingPlayer = null;

        this.packetCardsFromServerObservers = new ArrayList<>();
        this.packetDoActionObservers = new ArrayList<>();
        this.packetSetupObservers = new ArrayList<>();
        this.packetUpdateBoardObservers = new ArrayList<>();



        assert (players.size() == 2 || players.size() == 3);

        //HERE WE PUT CONTROLS ON THE GODS AVAILABLE FOR THE NUMBER OF PLAYERS IN THE GAME
        /*if(players.size() == 3)
           cards = cards.stream().filter(x -> x.isPlayableIn3).collect(Collectors.toList());*/

        this.allCards = new HashMap<>();
        for (CardFile card : cards)
            this.allCards.put(card.getName(), card.getDescription());

        this.availableCards = cards.stream()
                .map(CardFile::getName)
                .collect(Collectors.toList());
    }

    public void start(){
        assert(setupPhase == SetupPhase.STARTING);

        //CHOOSING THE CHALLENGER
        chooseChallenger();

        //SENDING THE CARDS TO CHOOSE TO THE CHALLENGER
        PacketCardsFromServer packetCardsFromServer = new PacketCardsFromServer(challenger.getNickname(), numberOfCardsToChoose, allCards ,availableCards);
        notifyPacketCardsFromServerObservers(packetCardsFromServer);
        this.setupPhase = SetupPhase.WAIT_CARDS;

    }
    public void setSelectedCards(String SenderID, List<String> chosenCards) throws InvalidPacketException {
        if(setupPhase != SetupPhase.WAIT_CARDS)
            return;

        // IF THE SENDER IS WRONG -> IGNORE
        if( SenderID == null || !SenderID.equals(players.get(activePlayerIndex).getNickname()))
            return;

        // IF THE LIST IS NULL -> INVALID
        if(chosenCards == null )
            throw new InvalidPacketException();

        // IF THE NUMBER OF CARDS CHOSEN IS NOT RIGHT -> INVALID
        if(chosenCards.size() != numberOfCardsToChoose)
            throw new InvalidPacketException();

        //IF THERE ARE IDENTICAL CARDS -> INVALID
        if(chosenCards.stream().distinct().count() != chosenCards.size())
            throw new InvalidPacketException();

        // IF ONE OF THE CARDS IS NOT AVAILABLE -> INVALID
        for(String card : chosenCards)
            if(!availableCards.contains(card))
                throw new InvalidPacketException();

         //IF I DIDN'T RECEIVED THEM FORM THE CHALLENGER I SET THE ASSOCIATION AND UPDATE AVAILABLE CARDS
        if(!(SenderID.equals(challenger.getNickname()))) {
            assert(chosenCards.size() == 1);
            cardAssociations.put(players.get(activePlayerIndex), cards.stream().filter(x -> x.getName().equals(chosenCards.get(0))).findAny().orElse(null));
            availableCards.remove(chosenCards.get(0));
        } else{ //IF I DID RECEIVE THEM FROM THE CHALLENGER I SET THE NEXT CARDS TO CHOOSE TO 1 AND UPDATE THE AVAILABLE CARDS
            numberOfCardsToChoose = 1;
            availableCards = availableCards.stream().filter(chosenCards::contains).collect(Collectors.toList());
        }
        //INCREMENT THE PLAYER INDEX
        incrementActivePlayerIndex();

        /*
            IF THE NEXT PLAYER IS THE CHALLENGER HE DOESN'T CHOOSE
            SO I ASSIGN HIM THE REMAINING CARD AND I ASK HIM THE STARTING PLAYER
        */
        if(players.get(activePlayerIndex).equals(challenger)){
            assert(availableCards.size() == 1);
            cardAssociations.put(players.get(activePlayerIndex), cards.stream().filter(x -> x.getName().equals(availableCards.get(0))).findAny().orElse(null));
            availableCards.clear();

            assert(cardAssociations.size() == players.size());

            Map<String, Pair<String, String>> playersAndTheirCards = new HashMap<>();
            for( Player p : cardAssociations.keySet()){
                CardFile hisCard = cardAssociations.get(p);
                p.setCard(hisCard);
                Pair<String, String> hisCardWithDesc = new Pair<>(hisCard.getName(), hisCard.getDescription());
                playersAndTheirCards.put(p.getNickname(), hisCardWithDesc);
            }

            Map<String, List<String>> ids = new HashMap<>();
            for( Player p : players){
                List<String> hisWorkers = new ArrayList<>();
                for(Worker w : p.getWorkers()){
                    hisWorkers.add(w.getID());
                }
                ids.put(p.getNickname(), hisWorkers);
            }

            Map<String, Color> colors = new HashMap<>();
            List<Color> possibleColors = new ArrayList<>();
            possibleColors.add(Color.CYAN);
            possibleColors.add(Color.WHITE);
            possibleColors.add(Color.ORANGE);
            int i = 0;
            for(Player p : players){
                colors.put(p.getNickname(), possibleColors.get(i));
                i++;
            }

            PacketSetup packetSetup = new PacketSetup(ids, colors, playersAndTheirCards, model.getBoard().getBuildingsCounter());
            notifyPacketSetupObservers(packetSetup);

            PacketDoAction packetDoAction = new PacketDoAction(challenger.getNickname(), ActionType.CHOOSE_START_PLAYER);
            notifyPacketDoActionObservers(packetDoAction);


            this.setupPhase = SetupPhase.WAIT_START_PLAYER;
        }else{ //IF THE NEXT PLAYER IS NOT THE CHALLENGER I ASK HIM TO CHOOSE
            PacketCardsFromServer packetCardsFromServer = new PacketCardsFromServer(players.get(activePlayerIndex).getNickname(), numberOfCardsToChoose, allCards ,availableCards);
            notifyPacketCardsFromServerObservers(packetCardsFromServer);
        }

    }
    public void setStartPlayer(String SenderID, String startPlayer) throws InvalidPacketException{

        if(setupPhase != SetupPhase.WAIT_START_PLAYER)
            return;

        assert(challenger.equals(players.get(activePlayerIndex)));

        //IF I AM NOT RECEIVING IT FROM THE CHALLENGER  -> IGNORE
        if( SenderID == null || !(SenderID.equals(players.get(activePlayerIndex).getNickname())))
            return;

        // IF THE START PLAYER IS NULL -> INVALID
        if(startPlayer == null )
            throw new InvalidPacketException();

        //IF THE CHOSEN PLAYER IS NOT ONE OF THE PLAYERS
        if(players.stream().filter(x -> x.getNickname().equals(startPlayer)).count() != 1){
            throw new InvalidPacketException();
        }
        //Setting start player
        startingPlayer = model.getPlayerByNick(startPlayer);
        model.setStartPlayer(startingPlayer);

        //Reload data and check
        players = model.getPlayers();
        activePlayerIndex = players.indexOf(startingPlayer);
        assert (activePlayerIndex == 0);

        PacketDoAction packetDoAction = new PacketDoAction(players.get(activePlayerIndex).getNickname(), ActionType.SET_WORKERS_POSITION);
        notifyPacketDoActionObservers(packetDoAction);

        this.setupPhase = SetupPhase.WAIT_WORKERS_CHOICE;

    }
    public void setWorkersPositions(String SenderID, Map<String, Point> myWorkersPositions) throws InvalidPacketException{
        if(setupPhase != SetupPhase.WAIT_WORKERS_CHOICE)
            return;

        Player activePlayer = players.get(activePlayerIndex);

        // IF THE SENDER IS WRONG -> IGNORE
        if( SenderID == null || !SenderID.equals(activePlayer.getNickname()))
            return;

        // IF THE MAP IS NULL -> INVALID
        if(myWorkersPositions == null)
            throw new InvalidPacketException();

        // IF THE NUMBER OF POSITIONS IS NOT RIGHT -> INVALID
        if(myWorkersPositions.size() != activePlayer.getWorkers().size())
            throw new InvalidPacketException();

        //ID DUPLICATED POSITIONS IN PACKET -> INVALID
        if (myWorkersPositions.values().stream().distinct().count() != myWorkersPositions.values().size())
            throw new InvalidPacketException();

        for(String workerID : myWorkersPositions.keySet()){
            if(workerID == null)
                throw new InvalidPacketException();
            // IF ONE OF THE WORKERS IS NOT ONE OF MINE -> INVALID
            if(activePlayer.getWorkers().stream().noneMatch(w->w.getID().equals(workerID)))
                throw new InvalidPacketException();
            // IF ONE OF THE CELLS I WANT TO SET HIM IN IS OUT OF THE BOARD OR HAS ANOTHER WORKER -> INVALID
            if(model.getBoard().getCell(myWorkersPositions.get(workerID)) == null || model.getBoard().getCell(myWorkersPositions.get(workerID)).isOccupied())
                throw new InvalidPacketException();
        }

        // IF EVERYTHING IS CORRECT I SET THE WORKERS ON THE BOARD
        for( String workerID : myWorkersPositions.keySet()){
            model.getWorkerByID(workerID).setPosition(myWorkersPositions.get(workerID));
            model.getBoard().getCell(myWorkersPositions.get(workerID)).setWorker(workerID);
        }

        //BUILDING PACKET UPDATE BOARD
        Map<String, Point> workersPositions = new HashMap<>();
        for(Player p: players){
            for(Worker w : p.getWorkers()){
                if(w.getPosition() != null)
                    workersPositions.put(w.getID(), w.getPosition());
            }
        }
        PacketUpdateBoard packetUpdateBoard = new PacketUpdateBoard(workersPositions, null, null, null);
        notifyPacketUpdateBoardObservers(packetUpdateBoard);


        incrementActivePlayerIndex();

        //IF THE NEXT PLAYER IS THE STARTING PLAYER WE HAVE FINISHED SETUP
        if(players.get(activePlayerIndex) == startingPlayer){
            this.setupPhase = SetupPhase.SETUP_FINISHED;
            model.compileCardStrategy();
        }else{ // IF NOT WE ASK FOR OTHER WORKERS POSITIONING
            PacketDoAction packetDoAction = new PacketDoAction(players.get(activePlayerIndex).getNickname(), ActionType.SET_WORKERS_POSITION);
            notifyPacketDoActionObservers(packetDoAction);
        }

    }

    public SetupPhase getSetupPhase() {
        return setupPhase;
    }
    private void chooseChallenger(){
        //CHOOSING THE CHALLENGER
        Random random = new Random();
        activePlayerIndex = random.nextInt(players.size());
        this.challenger = players.get(activePlayerIndex);
    }
    private void incrementActivePlayerIndex(){
        activePlayerIndex ++;
        if(activePlayerIndex > (players.size() - 1 ))
            activePlayerIndex = 0;
    }

    public void addPacketSetupObserver(Observer<PacketSetup> o){
        this.packetSetupObservers.add(o);
    }
    public void addPacketDoActionObserver(Observer<PacketDoAction> o){
        this.packetDoActionObservers.add(o);
    }
    public void addPacketUpdateBoardObserver(Observer<PacketUpdateBoard> o){
        this.packetUpdateBoardObservers.add(o);
    }
    public void addPacketCardsFromServerObserver(Observer<PacketCardsFromServer> o){
        this.packetCardsFromServerObservers.add(o);
    }

    public void notifyPacketSetupObservers(PacketSetup p){
        for(Observer<PacketSetup> o : packetSetupObservers){
            o.update(p);
        }
    }
    public void notifyPacketUpdateBoardObservers(PacketUpdateBoard p){
        for(Observer<PacketUpdateBoard> o : packetUpdateBoardObservers){
            o.update(p);
        }
    }
    public void notifyPacketCardsFromServerObservers(PacketCardsFromServer p){
        for(Observer<PacketCardsFromServer> o : packetCardsFromServerObservers){
            o.update(p);
        }
    }
    public void notifyPacketDoActionObservers(PacketDoAction p){
        for(Observer<PacketDoAction> o : packetDoActionObservers){
            o.update(p);
        }
    }
 
}
