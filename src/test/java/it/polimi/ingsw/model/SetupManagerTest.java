package it.polimi.ingsw.model;

import it.polimi.ingsw.model.cardReader.CardFactory;
import it.polimi.ingsw.model.cardReader.CardFile;
import it.polimi.ingsw.model.cardReader.exceptions.CardLoadingException;
import it.polimi.ingsw.model.cardReader.exceptions.InvalidCardException;
import it.polimi.ingsw.model.enums.ActionType;
import it.polimi.ingsw.model.enums.SetupPhase;
import it.polimi.ingsw.packets.*;
import javafx.util.Pair;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class SetupManagerTest {

    static class Client  {
      
        private PacketSetup packetSetup;
        private PacketDoAction packetDoAction;
        private PacketUpdateBoard packetUpdateBoard;
        private PacketCardsFromServer packetCardsFromServer;
        
       
        public Client(SetupManager setupManager){
            setupManager.addPacketCardsFromServerObserver((packetCardsFromServer)->{
                this.packetCardsFromServer = packetCardsFromServer;
            });
            setupManager.addPacketDoActionObserver((packetDoAction)->{
                this.packetDoAction = packetDoAction;
            });
            setupManager.addPacketSetupObserver((packetSetup)->{
                this.packetSetup = packetSetup;
            });
            setupManager.addPacketUpdateBoardObserver((packetUpdateBoard)->{
                this.packetUpdateBoard = packetUpdateBoard;
            });
        }

        public PacketCardsFromServer cards() {
            return packetCardsFromServer;
        }

        public PacketSetup setup() {
            return packetSetup;
        }

        public PacketDoAction action() {
            return packetDoAction;
        }

        public PacketUpdateBoard board() {
            return packetUpdateBoard;
        }
    }

    private static CardFactory cardFactory;
    private InternalModel model;
    private SetupManager setupManager;
    private List<CardFile> cardFiles;
    private List<String> selectedCards;
    private Player Matteo;
    private Player Mirko;
    private Player Andrea;
    private Client client;
    private List<String> players;

    @BeforeAll
    static void init() throws CardLoadingException, InvalidCardException {
        //CardFactory
        cardFactory = CardFactory.getInstance();
    }

    @BeforeEach
    void createModel(){
        players = new ArrayList<>();
        selectedCards = new ArrayList<>();
        cardFiles = new ArrayList<>();
        players.add("Andrea");
        players.add("Matteo");
        players.add("Mirko");
        CardFile prometheus = cardFactory.getCards().stream().filter(x -> x.getName().equals("Prometheus")).findFirst().orElse(null);
        CardFile atlas = cardFactory.getCards().stream().filter(x -> x.getName().equals("Atlas")).findFirst().orElse(null);
        CardFile apollo = cardFactory.getCards().stream().filter(x -> x.getName().equals("Apollo")).findFirst().orElse(null);
        cardFiles.add(prometheus);
        cardFiles.add(atlas);
        cardFiles.add(apollo);
        assert prometheus != null;
        selectedCards.add(prometheus.getName());
        assert atlas != null;
        selectedCards.add(atlas.getName());
        assert apollo != null;
        selectedCards.add(apollo.getName());
        model = new InternalModel(players, cardFactory);
        Andrea = model.getPlayerByNick("Andrea");
        Matteo = model.getPlayerByNick("Matteo");
        Mirko = model.getPlayerByNick("Mirko");
        setupManager = new SetupManager(model, cardFiles);
        client = new Client(setupManager);

    }

    /**
     * After starting the Setup.Phase should change from starting to wait_cards.
     */
    @Test
    void testStart(){
        assertEquals(setupManager.getSetupPhase(), SetupPhase.STARTING);
        setupManager.start();
        assertEquals(setupManager.getSetupPhase(), SetupPhase.WAIT_CARDS);
        
        PacketCardsFromServer packetCardsFromServer = client.cards();
        assert packetCardsFromServer != null;
       
        for(String card : packetCardsFromServer.getAllCards().keySet()){
            boolean found = false;
            for(CardFile cardFile : cardFiles){
                if(cardFile.getName().equals(card) && cardFile.getDescription().equals(packetCardsFromServer.getAllCards().get(card))) found = true;
            }
            assert found;
        }
        assertEquals(packetCardsFromServer.getAvailableCards(),selectedCards);
        assertTrue(players.contains(packetCardsFromServer.getTo()));
        assertEquals(packetCardsFromServer.getNumberToChoose(),3);
        assertEquals(packetCardsFromServer.getAvailableCards(),selectedCards);
    }

    /**
     * The start method is not called, therefore it is impossible to select the cards.
     */
    @Test
    void testSetSelectedCards1(){
        try{
            setupManager.setSelectedCards(Mirko.getNickname(), selectedCards);
            assertEquals(setupManager.getSetupPhase(), SetupPhase.STARTING);
            setupManager.setSelectedCards(Matteo.getNickname(), selectedCards);
            assertEquals(setupManager.getSetupPhase(), SetupPhase.STARTING);
            setupManager.setSelectedCards(Andrea.getNickname(), selectedCards);
            assertEquals(setupManager.getSetupPhase(), SetupPhase.STARTING);
        } catch (InvalidPacketException e) {
            assert false;
        }
    }

    /**
     * The sender id is null.
     */
    @Test
    void testSelectedCards2(){
        setupManager.start();
        PacketCardsFromServer packet = client.cards();
        assert packet != null;
        String challenger = packet.getTo();
        String notChallenger;
        if(challenger.equals(players.get(0))) notChallenger = players.get(1);
        else if (challenger.equals(players.get(1))) notChallenger = players.get(2);
        else notChallenger = players.get(0);
        assert notChallenger != null;
        try{
            setupManager.setSelectedCards(null, selectedCards);
            assertEquals(setupManager.getSetupPhase(), SetupPhase.WAIT_CARDS);
            setupManager.setSelectedCards(notChallenger, selectedCards);
            assertEquals(setupManager.getSetupPhase(), SetupPhase.WAIT_CARDS);

        } catch (InvalidPacketException e) {
            assert false;
        }
    }

    /**
     * The chosenCards List is null -> Exception.
     */
    @Test
    void testSelectedCards3(){
        setupManager.start();
        PacketCardsFromServer packet = client.cards();
        assert packet != null;
        String challenger = packet.getTo();
        try{
            setupManager.setSelectedCards(challenger, null);
            assert false;
        } catch (InvalidPacketException e) {
            assert true;
        }
        assertEquals(setupManager.getSetupPhase(), SetupPhase.WAIT_CARDS);
    }

    /**
     * The chosenCards.size != players.size.
     */
    @Test
    void testSelectedCards4(){
        setupManager.start();
        CardFile apollo = cardFactory.getCards().stream().filter(x -> x.getName().equals("Apollo")).findFirst().orElse(null);
        selectedCards.clear();
        PacketCardsFromServer packet = client.cards();
        assert packet != null;
        String challenger = packet.getTo();
        assert apollo != null;
        selectedCards.add(apollo.getName());
        try{
            setupManager.setSelectedCards(challenger, selectedCards);
            assert false;
        } catch (InvalidPacketException e) {
            assert true;
        }
    }

    /**
     * In the chosen cards there is a card that is not part of the possible choices.
     */
    @Test
    void testSelectedCards5(){
        setupManager.start();
        CardFile pan = cardFactory.getCards().stream().filter(x -> x.getName().equals("Pan")).findFirst().orElse(null);
        selectedCards.remove("Apollo");
        assert pan != null;
        selectedCards.add(pan.getName());
        PacketCardsFromServer packet = client.cards();
        assert packet != null;
        String challenger = packet.getTo();
        try{
            setupManager.setSelectedCards(challenger, selectedCards);
            assert false;
        } catch (InvalidPacketException e) {
            assert true;
        }
    }

    /**
     * One player chooses a card chosen before -> Exception.
     */
    @Test
    void testSelectedCards6(){
        setupManager.start();
        PacketCardsFromServer packet = client.cards();
        assert packet != null;
        String challenger = packet.getTo();
        try{
            setupManager.setSelectedCards(challenger, selectedCards);
            assert true;
        } catch (InvalidPacketException e) {
            assert false;
        }
        String next;
        packet = client.cards();
        List<String> availableCards = new ArrayList<>(packet.getAvailableCards());
        try{
            next = packet.getTo();
            setupManager.setSelectedCards(next, availableCards.subList(0,1));
            packet = client.cards();
            next = packet.getTo();
            setupManager.setSelectedCards(next, availableCards.subList(0,1));
            assert false;
        } catch (InvalidPacketException e) {
            assert true;
        }
    }

    /**
     * In the selectCard method:
     * - The challenger chooses 3 cards among the available ones.
     * - The other players choose 1 card each and they ave to be all different.
     * - The challenger has the remaining card that isn't chosen by the others.
     * - All the players receive the association between each player and his card, the association between workers and players,
     * the association between player and his color.
     * - The challenger receives a packetDoAction with the ActionType.CHOOSE_START_PLAYER.
     */
    @Test
    void testSelectedCards7(){
        setupManager.start();
        PacketCardsFromServer packet = client.cards();
        assert packet != null;
        String challenger = packet.getTo();
        try{
            setupManager.setSelectedCards(challenger, selectedCards);
            assert true;
        } catch (InvalidPacketException e) {
            assert false;
        }

        Map<String, Pair<String, String>> playerCardAssociation = new HashMap<>();

        assertEquals(setupManager.getSetupPhase(), SetupPhase.WAIT_CARDS);
        String next;
        packet = client.cards();
        List<String> availableCards = new ArrayList<>(packet.getAvailableCards());

        Pair<String,String> helperCards;
        try{
            next = packet.getTo();
            assertNotEquals(next, challenger);
            assertTrue(players.contains(next));
            assertEquals(availableCards,selectedCards);
            assertEquals(availableCards.size(),3);
            assertEquals(packet.getNumberToChoose(),1);
            for(String card : packet.getAllCards().keySet()){
                boolean found = false;
                for(CardFile cardFile : cardFiles){
                    if(cardFile.getName().equals(card) && cardFile.getDescription().equals(packet.getAllCards().get(card))) found = true;
                }
                assert found;
            }
            assertEquals(packet.getAllCards().size(),3);
            setupManager.setSelectedCards(next, availableCards.subList(0,1));
            helperCards = new Pair<>(availableCards.get(0),packet.getAllCards().get(availableCards.get(0)));
            playerCardAssociation.put(next,helperCards);
            assertEquals(setupManager.getSetupPhase(), SetupPhase.WAIT_CARDS);
            packet = client.cards();
            next = packet.getTo();
            assertNotEquals(next, challenger);
            assertTrue(players.contains(next));
            availableCards = new ArrayList<>(packet.getAvailableCards());
            for(String card : availableCards){
                assertTrue(selectedCards.contains(card));
            }
            assertEquals(availableCards.size(),2);
            assertEquals(packet.getNumberToChoose(),1);
            for(String card : packet.getAllCards().keySet()){
                boolean found = false;
                for(CardFile cardFile : cardFiles){
                    if(cardFile.getName().equals(card) && cardFile.getDescription().equals(packet.getAllCards().get(card))) found = true;
                }
                assert found;
            }
            assertEquals(packet.getAllCards().size(),3);
            setupManager.setSelectedCards(next, availableCards.subList(0,1));
            helperCards = new Pair<>(availableCards.get(0),packet.getAllCards().get(availableCards.get(0)));
            playerCardAssociation.put(next,helperCards);
            helperCards = new Pair<>(availableCards.get(1),packet.getAllCards().get(availableCards.get(1)));
            playerCardAssociation.put(challenger,helperCards);
            assert true;
        } catch (InvalidPacketException e) {
            assert false;
        }
        assertEquals(setupManager.getSetupPhase(), SetupPhase.WAIT_START_PLAYER);
        PacketSetup setup = client.setup();
        PacketDoAction action = client.action();
        assertEquals(setup.getCards(),playerCardAssociation);
        List<Color> colorList = new ArrayList<>();
        Map<String, Color> colors = new HashMap<>(setup.getColors());
        for(String player : colors.keySet()){
            assert !colorList.contains(colors.get(player));
            colorList.add(colors.get(player));
        }
        assertEquals(colorList.size(),3);
        Map<String , List<String>> ids = new HashMap<>(setup.getIds());
        for(String player : ids.keySet()){
            assertEquals(ids.get(player).size(),2);
            assert ids.get(player).get(0).equals(player + ".1") && ids.get(player).get(1).equals(player + ".2");
        }
        assertEquals(action.getActionType(), ActionType.CHOOSE_START_PLAYER);
        assertEquals(action.getTo(),challenger);
    }

    /**
     * The setStartPlayer method can't work because the previous steps weren't done.
     */
    @Test
    void setStartPlayerTest1(){
        try {
            setupManager.setStartPlayer(Mirko.getNickname(), Mirko.getNickname());
            assertEquals(setupManager.getSetupPhase(),SetupPhase.STARTING);
        } catch (InvalidPacketException e) {
            assert false;
        }
        setupManager.start();
        try {
            setupManager.setStartPlayer(Mirko.getNickname(), Mirko.getNickname());
            assertEquals(setupManager.getSetupPhase(),SetupPhase.WAIT_CARDS);
        } catch (InvalidPacketException e) {
            assert false;
        }
    }

    /**
     * The senderID is null.
     */
    @Test
    void setStartPlayerTest2(){
        setupManager.start();
        PacketCardsFromServer packet = client.cards();
        String challenger = packet.getTo();
        try{
            setupManager.setSelectedCards(challenger, selectedCards);
            assert true;
        } catch (InvalidPacketException e) {
            assert false;
        }
        String next;
        packet = client.cards();
        try{
            next = packet.getTo();
            setupManager.setSelectedCards(next, selectedCards.subList(0,1));
            packet = client.cards();
            next = packet.getTo();
            setupManager.setSelectedCards(next, selectedCards.subList(1,2));
            assertEquals(setupManager.getSetupPhase(), SetupPhase.WAIT_START_PLAYER);
            setupManager.setStartPlayer(null, Mirko.getNickname());
            assertEquals(setupManager.getSetupPhase(), SetupPhase.WAIT_START_PLAYER);
            assert true;
        } catch (InvalidPacketException e) {
            assert false;
        }



    }

    /**
     * The senderID is not the challenger.
     */
    @Test
    void setStartPlayerTest3(){
        try {
            setupManager.setStartPlayer(Mirko.getNickname(), Mirko.getNickname());
            assertEquals(setupManager.getSetupPhase(),SetupPhase.STARTING);
        } catch (InvalidPacketException e) {
            assert false;
        }
        setupManager.start();
        try {
            setupManager.setStartPlayer(Mirko.getNickname(), Mirko.getNickname());
            assertEquals(setupManager.getSetupPhase(),SetupPhase.WAIT_CARDS);
        } catch (InvalidPacketException e) {
            assert false;
        }
        PacketCardsFromServer packet = client.cards();
        String challenger = packet.getTo();
        try{
            setupManager.setSelectedCards(challenger, selectedCards);
            assert true;
        } catch (InvalidPacketException e) {
            assert false;
        }
        String next;
        packet = client.cards();
        try{
            next = packet.getTo();
            setupManager.setSelectedCards(next, selectedCards.subList(0,1));
            packet = client.cards();
            next = packet.getTo();
            setupManager.setSelectedCards(next, selectedCards.subList(1,2));
            assertEquals(setupManager.getSetupPhase(), SetupPhase.WAIT_START_PLAYER);
            String notChallenger;
            if(challenger.equals(players.get(0))) notChallenger = players.get(1);
            else if (challenger.equals(players.get(1))) notChallenger = players.get(2);
            else notChallenger = players.get(0);
            setupManager.setStartPlayer(notChallenger, Mirko.getNickname());
            assertEquals(setupManager.getSetupPhase(), SetupPhase.WAIT_START_PLAYER);
            assert true;
        } catch (InvalidPacketException e) {
            assert false;
        }



    }

    /**
     * The startPlayer is null.
     */
    @Test
    void setStartPlayerTest4(){
        try {
            setupManager.setStartPlayer(Mirko.getNickname(), Mirko.getNickname());
            assertEquals(setupManager.getSetupPhase(),SetupPhase.STARTING);
        } catch (InvalidPacketException e) {
            assert false;
        }
        setupManager.start();
        try {
            setupManager.setStartPlayer(Mirko.getNickname(), Mirko.getNickname());
            assertEquals(setupManager.getSetupPhase(),SetupPhase.WAIT_CARDS);
        } catch (InvalidPacketException e) {
            assert false;
        }
        PacketCardsFromServer packet = client.cards();
        String challenger = packet.getTo();
        try{
            setupManager.setSelectedCards(challenger, selectedCards);
            assert true;
        } catch (InvalidPacketException e) {
            assert false;
        }
        String next;
        packet = client.cards();
        try{
            next = packet.getTo();
            setupManager.setSelectedCards(next, selectedCards.subList(0,1));
            packet = client.cards();
            next = packet.getTo();
            setupManager.setSelectedCards(next, selectedCards.subList(1,2));
            assertEquals(setupManager.getSetupPhase(), SetupPhase.WAIT_START_PLAYER);
            setupManager.setStartPlayer(challenger, null);
            assert false;
        } catch (InvalidPacketException e) {
            assert true;
        }



    }

    /**
     * The startPlayer is not one of the players.
     */
    @Test
    void setStartPlayerTest5(){
        try {
            setupManager.setStartPlayer(Mirko.getNickname(), Mirko.getNickname());
            assertEquals(setupManager.getSetupPhase(),SetupPhase.STARTING);
        } catch (InvalidPacketException e) {
            assert false;
        }
        setupManager.start();
        try {
            setupManager.setStartPlayer(Mirko.getNickname(), Mirko.getNickname());
            assertEquals(setupManager.getSetupPhase(),SetupPhase.WAIT_CARDS);
        } catch (InvalidPacketException e) {
            assert false;
        }
        PacketCardsFromServer packet = client.cards();
        String challenger = packet.getTo();
        try{
            setupManager.setSelectedCards(challenger, selectedCards);
            assert true;
        } catch (InvalidPacketException e) {
            assert false;
        }
        String next;
        packet = client.cards();
        try{
            next = packet.getTo();
            setupManager.setSelectedCards(next, selectedCards.subList(0,1));
            packet = client.cards();
            next = packet.getTo();
            setupManager.setSelectedCards(next, selectedCards.subList(1,2));
            assertEquals(setupManager.getSetupPhase(), SetupPhase.WAIT_START_PLAYER);
            setupManager.setStartPlayer(challenger, "Giovanni");
            assert false;
        } catch (InvalidPacketException e) {
            assert true;
        }



    }

    /**
     * Everything should be fine.
     */
    @Test
    void setStartPlayerTest6(){
        try {
            setupManager.setStartPlayer(Mirko.getNickname(), Mirko.getNickname());
            assertEquals(setupManager.getSetupPhase(),SetupPhase.STARTING);
        } catch (InvalidPacketException e) {
            assert false;
        }
        setupManager.start();
        try {
            setupManager.setStartPlayer(Mirko.getNickname(), Mirko.getNickname());
            assertEquals(setupManager.getSetupPhase(),SetupPhase.WAIT_CARDS);
        } catch (InvalidPacketException e) {
            assert false;
        }
        PacketCardsFromServer packet = client.cards();
        String challenger = packet.getTo();
        try{
            setupManager.setSelectedCards(challenger, selectedCards);
            assert true;
        } catch (InvalidPacketException e) {
            assert false;
        }
        String next;
        packet = client.cards();
        try{
            next = packet.getTo();
            setupManager.setSelectedCards(next, selectedCards.subList(0,1));
            packet = client.cards();
            next = packet.getTo();
            setupManager.setSelectedCards(next, selectedCards.subList(1,2));
            assertEquals(setupManager.getSetupPhase(), SetupPhase.WAIT_START_PLAYER);
            setupManager.setStartPlayer(challenger, Mirko.getNickname());
            assertEquals(setupManager.getSetupPhase(), SetupPhase.WAIT_WORKERS_CHOICE);
            PacketDoAction packetDoAction = client.action();
            assertEquals(packetDoAction.getTo(),Mirko.getNickname());
            assertEquals(packetDoAction.getActionType(),ActionType.SET_WORKERS_POSITION);
        } catch (InvalidPacketException e) {
            assert false;
        }



    }

    /**
     * Start, setSelectedCards,setStartPlayer must be called before, otherwise nothing changes.
     */
    @Test
    void testSetWorkersPosition1(){
        Map<String, Point> mirkoWorkersPosition = new HashMap<>();
        mirkoWorkersPosition.put(Mirko.getWorkers().get(0).getID(), new Point(0,0));
        mirkoWorkersPosition.put(Mirko.getWorkers().get(1).getID(), new Point(0,1));
        try {
            setupManager.setWorkersPositions(Mirko.getNickname(), mirkoWorkersPosition);
            assertEquals(setupManager.getSetupPhase(), SetupPhase.STARTING);
        } catch (InvalidPacketException e) {
            assert false;
        }
        setupManager.start();
        try {
            setupManager.setWorkersPositions(Mirko.getNickname(), mirkoWorkersPosition);
            assertEquals(setupManager.getSetupPhase(), SetupPhase.WAIT_CARDS);
        } catch (InvalidPacketException e) {
            assert false;
        }
        PacketCardsFromServer packet = client.cards();
        String challenger = packet.getTo();
        try{
            setupManager.setSelectedCards(challenger, selectedCards);
            setupManager.setWorkersPositions(Mirko.getNickname(), mirkoWorkersPosition);
            assertEquals(setupManager.getSetupPhase(), SetupPhase.WAIT_CARDS);
            assert true;
        } catch (InvalidPacketException e) {
            assert false;
        }
        String next;
        packet = client.cards();
        try{
            next = packet.getTo();
            setupManager.setSelectedCards(next, selectedCards.subList(0,1));
            setupManager.setWorkersPositions(Mirko.getNickname(), mirkoWorkersPosition);
            assertEquals(setupManager.getSetupPhase(), SetupPhase.WAIT_CARDS);
            packet = client.cards();
            next = packet.getTo();
            setupManager.setSelectedCards(next, selectedCards.subList(1,2));
            setupManager.setWorkersPositions(Mirko.getNickname(), mirkoWorkersPosition);
            assertEquals(setupManager.getSetupPhase(), SetupPhase.WAIT_START_PLAYER);

        } catch (InvalidPacketException e) {
            assert false;
        }
    }

    /**
     * The senderID is null.
     */
    @Test
    void testSetWorkersPosition2(){
        Map<String, Point> mirkoWorkersPosition = new HashMap<>();
        mirkoWorkersPosition.put(Mirko.getWorkers().get(0).getID(), new Point(0,0));
        mirkoWorkersPosition.put(Mirko.getWorkers().get(1).getID(), new Point(0,1));
        setupManager.start();
        PacketCardsFromServer packet = client.cards();
        String challenger = packet.getTo();
        try{
            setupManager.setSelectedCards(challenger, selectedCards);
        } catch (InvalidPacketException e) {
            assert false;
        }
        String next;
        packet = client.cards();
        try{
            next = packet.getTo();
            setupManager.setSelectedCards(next, selectedCards.subList(0,1));
            packet = client.cards();
            next = packet.getTo();
            setupManager.setSelectedCards(next, selectedCards.subList(1,2));
            setupManager.setStartPlayer(challenger, Mirko.getNickname());
        } catch (InvalidPacketException e) {
            assert false;
        }
        assertEquals(setupManager.getSetupPhase(), SetupPhase.WAIT_WORKERS_CHOICE);
        try {
            setupManager.setWorkersPositions(null, mirkoWorkersPosition);
            assertEquals(setupManager.getSetupPhase(), SetupPhase.WAIT_WORKERS_CHOICE);
        } catch (InvalidPacketException e) {
            assert false;
        }
    }

    /**
     * The senderID is not the starting player.
     */
    @Test
    void testSetWorkersPosition3(){
        Map<String, Point> mirkoWorkersPosition = new HashMap<>();
        mirkoWorkersPosition.put(Mirko.getWorkers().get(0).getID(), new Point(0,0));
        mirkoWorkersPosition.put(Mirko.getWorkers().get(1).getID(), new Point(0,1));
        setupManager.start();
        PacketCardsFromServer packet = client.cards();
        String challenger = packet.getTo();
        try{
            setupManager.setSelectedCards(challenger, selectedCards);
        } catch (InvalidPacketException e) {
            assert false;
        }
        String next;
        packet = client.cards();
        try{
            next = packet.getTo();
            setupManager.setSelectedCards(next, selectedCards.subList(0,1));
            packet = client.cards();
            next = packet.getTo();
            setupManager.setSelectedCards(next, selectedCards.subList(1,2));
            setupManager.setStartPlayer(challenger, Mirko.getNickname());
        } catch (InvalidPacketException e) {
            assert false;
        }
        assertEquals(setupManager.getSetupPhase(), SetupPhase.WAIT_WORKERS_CHOICE);
        String notTheStartingPlayer = Andrea.getNickname();
        try {
            setupManager.setWorkersPositions(notTheStartingPlayer, mirkoWorkersPosition);
            assertEquals(setupManager.getSetupPhase(), SetupPhase.WAIT_WORKERS_CHOICE);
        } catch (InvalidPacketException e) {
            assert false;
        }
    }

    /**
     * myWorkersPositions Map is null.
     */
    @Test
    void testSetWorkersPosition4(){
        setupManager.start();
        PacketCardsFromServer packet = client.cards();
        String challenger = packet.getTo();
        try{
            setupManager.setSelectedCards(challenger, selectedCards);
        } catch (InvalidPacketException e) {
            assert false;
        }
        String next;
        packet = client.cards();
        try{
            next = packet.getTo();
            setupManager.setSelectedCards(next, selectedCards.subList(0,1));
            packet = client.cards();
            next = packet.getTo();
            setupManager.setSelectedCards(next, selectedCards.subList(1,2));
            setupManager.setStartPlayer(challenger, Mirko.getNickname());
        } catch (InvalidPacketException e) {
            assert false;
        }
        assertEquals(setupManager.getSetupPhase(), SetupPhase.WAIT_WORKERS_CHOICE);
        String startingPlayer = Mirko.getNickname();
        try {
            setupManager.setWorkersPositions(startingPlayer, null);
        } catch (InvalidPacketException e) {
            assert true;
            assertEquals(setupManager.getSetupPhase(), SetupPhase.WAIT_WORKERS_CHOICE);
        }
    }

    /**
     * myWorkersPositions Map 's size is not 2.
     */
    @Test
    void testSetWorkersPosition5(){
        Map<String, Point> mirkoWorkersPosition = new HashMap<>();
        mirkoWorkersPosition.put(Mirko.getWorkers().get(0).getID(), new Point(0,0));
        setupManager.start();
        PacketCardsFromServer packet = client.cards();
        String challenger = packet.getTo();
        try{
            setupManager.setSelectedCards(challenger, selectedCards);
        } catch (InvalidPacketException e) {
            assert false;
        }
        String next;
        packet = client.cards();
        try{
            next = packet.getTo();
            setupManager.setSelectedCards(next, selectedCards.subList(0,1));
            packet = client.cards();
            next = packet.getTo();
            setupManager.setSelectedCards(next, selectedCards.subList(1,2));
            setupManager.setStartPlayer(challenger, Mirko.getNickname());
        } catch (InvalidPacketException e) {
            assert false;
        }
        assertEquals(setupManager.getSetupPhase(), SetupPhase.WAIT_WORKERS_CHOICE);
        String startingPlayer = Mirko.getNickname();
        try {
            setupManager.setWorkersPositions(startingPlayer, mirkoWorkersPosition);
        } catch (InvalidPacketException e) {
            assert true;
            assertEquals(setupManager.getSetupPhase(), SetupPhase.WAIT_WORKERS_CHOICE);
        }
    }

    /**
     * One WorkerID is null.
     */
    @Test
    void testSetWorkersPosition6(){
        Map<String, Point> mirkoWorkersPosition = new HashMap<>();
        mirkoWorkersPosition.put(Mirko.getWorkers().get(0).getID(), new Point(0,0));
        mirkoWorkersPosition.put(null, new Point(0,0));
        setupManager.start();
        PacketCardsFromServer packet = client.cards();
        String challenger = packet.getTo();
        try{
            setupManager.setSelectedCards(challenger, selectedCards);
        } catch (InvalidPacketException e) {
            assert false;
        }
        String next;
        packet = client.cards();
        try{
            next = packet.getTo();
            setupManager.setSelectedCards(next, selectedCards.subList(0,1));
            packet = client.cards();
            next = packet.getTo();
            setupManager.setSelectedCards(next, selectedCards.subList(1,2));
            setupManager.setStartPlayer(challenger, Mirko.getNickname());
        } catch (InvalidPacketException e) {
            assert false;
        }
        assertEquals(setupManager.getSetupPhase(), SetupPhase.WAIT_WORKERS_CHOICE);
        String startingPlayer = Mirko.getNickname();
        try {
            setupManager.setWorkersPositions(startingPlayer, mirkoWorkersPosition);
        } catch (InvalidPacketException e) {
            assert true;
            assertEquals(setupManager.getSetupPhase(), SetupPhase.WAIT_WORKERS_CHOICE);
        }
    }

    /**
     * A Worker's position is already occupied.
     */
    @Test
    void testSetWorkersPosition7(){
        Map<String, Point> workersPosition = new HashMap<>();
        Point alreadyOccPos = new Point(0,0);
        workersPosition.put(Mirko.getWorkers().get(0).getID(), alreadyOccPos);
        workersPosition.put(Mirko.getWorkers().get(1).getID(), new Point(0,1));
        setupManager.start();
        PacketCardsFromServer packet = client.cards();
        String challenger = packet.getTo();
        try{
            setupManager.setSelectedCards(challenger, selectedCards);
        } catch (InvalidPacketException e) {
            assert false;
        }
        String next;
        packet = client.cards();
        try{
            next = packet.getTo();
            setupManager.setSelectedCards(next, selectedCards.subList(0,1));
            packet = client.cards();
            next = packet.getTo();
            setupManager.setSelectedCards(next, selectedCards.subList(1,2));
            setupManager.setStartPlayer(challenger, Mirko.getNickname());
        } catch (InvalidPacketException e) {
            assert false;
        }
        assertEquals(setupManager.getSetupPhase(), SetupPhase.WAIT_WORKERS_CHOICE);
        String startingPlayer = Mirko.getNickname();
        try {
            setupManager.setWorkersPositions(startingPlayer, workersPosition);
            PacketDoAction packetDoAction = client.action();
            next = packetDoAction.getTo();
            workersPosition = new HashMap<>();
            workersPosition.put(model.getPlayerByNick(next).getWorkers().get(0).getID(), alreadyOccPos);
            workersPosition.put(model.getPlayerByNick(next).getWorkers().get(1).getID(), new Point(0,2));
            setupManager.setWorkersPositions(next, workersPosition);
        } catch (InvalidPacketException e) {
            assert true;
            assertEquals(setupManager.getSetupPhase(), SetupPhase.WAIT_WORKERS_CHOICE);
        }
    }

    /**
     * A Worker belongs to another Player.
     */
    @Test
    void testSetWorkersPosition8(){
        Map<String, Point> workersPosition = new HashMap<>();
        workersPosition.put(Mirko.getWorkers().get(0).getID(), new Point(0,0));
        workersPosition.put(Mirko.getWorkers().get(1).getID(), new Point(0,1));
        setupManager.start();
        PacketCardsFromServer packet = client.cards();
        String challenger = packet.getTo();
        try{
            setupManager.setSelectedCards(challenger, selectedCards);
        } catch (InvalidPacketException e) {
            assert false;
        }
        String next;
        packet = client.cards();
        try{
            next = packet.getTo();
            setupManager.setSelectedCards(next, selectedCards.subList(0,1));
            packet = client.cards();
            next = packet.getTo();
            setupManager.setSelectedCards(next, selectedCards.subList(1,2));
            setupManager.setStartPlayer(challenger, Mirko.getNickname());
        } catch (InvalidPacketException e) {
            assert false;
        }
        assertEquals(setupManager.getSetupPhase(), SetupPhase.WAIT_WORKERS_CHOICE);
        String startingPlayer = Mirko.getNickname();
        try {
            setupManager.setWorkersPositions(startingPlayer, workersPosition);
            PacketDoAction packetDoAction = client.action();
            next = packetDoAction.getTo();
            workersPosition = new HashMap<>();
            workersPosition.put(model.getPlayerByNick(startingPlayer).getWorkers().get(0).getID(), new Point(0,3));
            workersPosition.put(model.getPlayerByNick(next).getWorkers().get(1).getID(), new Point(0,2));
            setupManager.setWorkersPositions(next, workersPosition);
        } catch (InvalidPacketException e) {
            assert true;
            assertEquals(setupManager.getSetupPhase(), SetupPhase.WAIT_WORKERS_CHOICE);
        }
    }

    /**
     * A position is not on the board.
     */
    @Test
    void testSetWorkersPosition9(){
        Map<String, Point> workersPosition = new HashMap<>();
        workersPosition.put(Mirko.getWorkers().get(0).getID(), new Point(0,0));
        workersPosition.put(Mirko.getWorkers().get(1).getID(), new Point(0,1));
        setupManager.start();
        PacketCardsFromServer packet = client.cards();
        String challenger = packet.getTo();
        try{
            setupManager.setSelectedCards(challenger, selectedCards);
        } catch (InvalidPacketException e) {
            assert false;
        }
        String next;
        packet = client.cards();
        try{
            next = packet.getTo();
            setupManager.setSelectedCards(next, selectedCards.subList(0,1));
            packet = client.cards();
            next = packet.getTo();
            setupManager.setSelectedCards(next, selectedCards.subList(1,2));
            setupManager.setStartPlayer(challenger, Mirko.getNickname());
        } catch (InvalidPacketException e) {
            assert false;
        }
        assertEquals(setupManager.getSetupPhase(), SetupPhase.WAIT_WORKERS_CHOICE);
        String startingPlayer = Mirko.getNickname();
        try {
            setupManager.setWorkersPositions(startingPlayer, workersPosition);
            PacketDoAction packetDoAction = client.action();
            next = packetDoAction.getTo();
            workersPosition = new HashMap<>();
            workersPosition.put(model.getPlayerByNick(next).getWorkers().get(0).getID(), new Point(5,3));
            workersPosition.put(model.getPlayerByNick(next).getWorkers().get(1).getID(), new Point(0,2));
            setupManager.setWorkersPositions(next, workersPosition);
        } catch (InvalidPacketException e) {
            assert true;
        }
    }

    /**
     * Everything should be fine.
     */
    @Test
    void testSetWorkersPosition10(){
        Map<String, Point> workersPosHelper = new HashMap<>();
        workersPosHelper.put(Mirko.getWorkers().get(0).getID(), new Point(0,0));
        workersPosHelper.put(Mirko.getWorkers().get(1).getID(), new Point(0,1));
        setupManager.start();
        PacketCardsFromServer packet = client.cards();
        String challenger = packet.getTo();
        try{
            setupManager.setSelectedCards(challenger, selectedCards);
        } catch (InvalidPacketException e) {
            assert false;
        }
        String next;
        List<String> playersOrder = model.getPlayers().stream().map(Player::getNickname).collect(Collectors.toList());
        packet = client.cards();
        try{
            next = packet.getTo();
            setupManager.setSelectedCards(next, selectedCards.subList(0,1));
            packet = client.cards();
            next = packet.getTo();
            setupManager.setSelectedCards(next, selectedCards.subList(1,2));
            setupManager.setStartPlayer(challenger, Mirko.getNickname());
        } catch (InvalidPacketException e) {
            assert false;
        }
        int curr = playersOrder.indexOf(Mirko.getNickname());
        int nextPlayer;
        if(curr == playersOrder.size() - 1) nextPlayer = 0;
        else nextPlayer = curr + 1;
        assertEquals(setupManager.getSetupPhase(), SetupPhase.WAIT_WORKERS_CHOICE);
        String startingPlayer = Mirko.getNickname();
        assertEquals(client.packetDoAction.getTo(),startingPlayer);
        Map<String, Point> mapForCheckUpdateBoard = new HashMap<>(workersPosHelper);
        try {
            setupManager.setWorkersPositions(startingPlayer, workersPosHelper);
            PacketUpdateBoard updateBoard = client.board();
            assertEquals(mapForCheckUpdateBoard,updateBoard.getWorkersPositions());
            PacketDoAction packetDoAction = client.action();
            assertEquals(packetDoAction.getTo(), playersOrder.get(nextPlayer));
            workersPosHelper = new HashMap<>();
            workersPosHelper.put(model.getPlayerByNick(playersOrder.get(nextPlayer)).getWorkers().get(0).getID(), new Point(0,3));
            workersPosHelper.put(model.getPlayerByNick(playersOrder.get(nextPlayer)).getWorkers().get(1).getID(), new Point(0,2));
            mapForCheckUpdateBoard.put(model.getPlayerByNick(playersOrder.get(nextPlayer)).getWorkers().get(0).getID(), new Point(0,3));
            mapForCheckUpdateBoard.put(model.getPlayerByNick(playersOrder.get(nextPlayer)).getWorkers().get(1).getID(), new Point(0,2));
            setupManager.setWorkersPositions(playersOrder.get(nextPlayer), workersPosHelper);
            updateBoard = client.board();
            assertEquals(mapForCheckUpdateBoard,updateBoard.getWorkersPositions());
            if(nextPlayer == playersOrder.size() - 1) nextPlayer = 0;
            else nextPlayer++;
            packetDoAction = client.action();
            assertEquals(packetDoAction.getTo(), playersOrder.get(nextPlayer));
            workersPosHelper = new HashMap<>();
            workersPosHelper.put(model.getPlayerByNick(playersOrder.get(nextPlayer)).getWorkers().get(0).getID(), new Point(1,3));
            workersPosHelper.put(model.getPlayerByNick(playersOrder.get(nextPlayer)).getWorkers().get(1).getID(), new Point(1,2));
            mapForCheckUpdateBoard.put(model.getPlayerByNick(playersOrder.get(nextPlayer)).getWorkers().get(0).getID(), new Point(1,3));
            mapForCheckUpdateBoard.put(model.getPlayerByNick(playersOrder.get(nextPlayer)).getWorkers().get(1).getID(), new Point(1,2));
            setupManager.setWorkersPositions(playersOrder.get(nextPlayer), workersPosHelper);
            updateBoard = client.board();
            assertEquals(mapForCheckUpdateBoard,updateBoard.getWorkersPositions());
            assertEquals(setupManager.getSetupPhase(), SetupPhase.SETUP_FINISHED);
        } catch (InvalidPacketException e) {
            assert false;
        }
    }



}