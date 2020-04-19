package it.polimi.ingsw.model;

import it.polimi.ingsw.model.cardReader.CardFactory;
import it.polimi.ingsw.model.cardReader.CardFile;
import it.polimi.ingsw.model.cardReader.exceptions.CardLoadingException;
import it.polimi.ingsw.model.cardReader.exceptions.InvalidCardException;
import it.polimi.ingsw.model.enums.ActionType;
import it.polimi.ingsw.model.enums.SetupPhase;
import it.polimi.ingsw.observe.Observer;
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

import static org.junit.jupiter.api.Assertions.*;

class Client implements Observer<PacketContainer> {
    private PacketContainer packetContainer;
    private PacketSetup packetSetup;
    private PacketDoAction packetDoAction;
    public void update(PacketContainer packet){
        this.packetContainer = packet;
        if(packetContainer.getPacketSetup() != null) this.packetSetup = packetContainer.getPacketSetup();
        if(packetContainer.getPacketDoAction() != null) this.packetDoAction = packetContainer.getPacketDoAction();
    }
    public PacketContainer container(){
        return packetContainer;
    }
    public PacketSetup setup(){
        return packetSetup;
    }
    public PacketDoAction action(){
        return packetDoAction;
    }
}

class SetupManagerTest {

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
        client = new Client();
        setupManager.addObserver(client);

    }

    /**
     * After starting the Setup.Phase should change from starting to wait_cards.
     */
    @Test
    void testStart(){
        assertEquals(setupManager.getSetupPhase(), SetupPhase.STARTING);
        setupManager.start();
        assertEquals(setupManager.getSetupPhase(), SetupPhase.WAIT_CARDS);
        PacketContainer packetContainer = client.container();
        assert packetContainer != null;
        PacketCardsFromServer packetCardsFromServer = packetContainer.getPacketCardsFromServer();
        assert packetCardsFromServer != null;
        assert packetContainer.getPacketDoAction() == null;
        assert packetContainer.getPacketSetup() == null;
        assert packetContainer.getPacketUpdateBoard() == null;
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
        PacketCardsFromServer packet = client.container().getPacketCardsFromServer();
        assert packet != null;
        String challenger = packet.getTo();
        String notChallenger = null;
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
        PacketCardsFromServer packet = client.container().getPacketCardsFromServer();
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
        PacketCardsFromServer packet = client.container().getPacketCardsFromServer();
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
        PacketCardsFromServer packet = client.container().getPacketCardsFromServer();
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
     * In the selectCard method:
     * - The challenger chooses 3 cards among the available ones.
     * - The other players choose 1 card each and they ave to be all different.
     * - The challenger has the remaining card that isn't chosen by the others.
     * - All the players receive the association between each player and his card, the association between workers and players,
     * the association between player and his color.
     * - The challenger receives a packetDoAction with the ActionType.CHOOSE_START_PLAYER.
     */
    @Test
    void testSelectedCards6(){
        setupManager.start();
        PacketCardsFromServer packet = client.container().getPacketCardsFromServer();
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
        players.removeIf(other -> other.equals(challenger));
        String next = null;
        packet = client.container().getPacketCardsFromServer();
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

            String finalNext = next;
            players.removeIf(other -> other.equals(finalNext));

            packet = client.container().getPacketCardsFromServer();
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


}