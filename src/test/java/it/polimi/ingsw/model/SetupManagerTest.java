package it.polimi.ingsw.model;

import it.polimi.ingsw.model.cardReader.CardFactory;
import it.polimi.ingsw.model.cardReader.CardFile;
import it.polimi.ingsw.model.cardReader.exceptions.CardLoadingException;
import it.polimi.ingsw.model.cardReader.exceptions.InvalidCardException;
import it.polimi.ingsw.model.enums.SetupPhase;
import it.polimi.ingsw.packets.InvalidPacketException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SetupManagerTest {

    private static CardFactory cardFactory;
    private InternalModel model;
    private SetupManager setupManager;
    private List<CardFile> cardFiles;
    private List<String> selectedCards;
    private Player Matteo;
    private Player Mirko;
    private Player Andrea;

    @BeforeAll
    static void init() throws CardLoadingException, InvalidCardException {
        //CardFactory
        cardFactory = CardFactory.getInstance();
    }

    @BeforeEach
    void createModel(){
        List<String> players = new ArrayList<>();
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
    }

    /**
     * After starting the Setup.Phase should change from starting to wait_cards.
     */
    @Test
    void testStart(){
        assertEquals(setupManager.getSetupPhase(), SetupPhase.STARTING);
        setupManager.start();
        assertEquals(setupManager.getSetupPhase(), SetupPhase.WAIT_CARDS);
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
        try{
            setupManager.setSelectedCards(null, selectedCards);
            assertEquals(setupManager.getSetupPhase(), SetupPhase.WAIT_CARDS);
        } catch (InvalidPacketException e) {
            assert false;
        }
    }

    /**
     * The chosenCards List is null -> Exception.
     */
    /*@Test
    void testSelectedCards3(){
        setupManager.start();
        try{
            setupManager.setSelectedCards(Mirko.getNickname(), null);
            assert false;
        } catch (InvalidPacketException e) {
            assert true;
        }
        assertEquals(setupManager.getSetupPhase(), SetupPhase.WAIT_CARDS);
    }*/

    /**
     * The chosenCards.size != players.size.
     */
    @Test
    void testSelectedCards4(){
        setupManager.start();
        CardFile apollo = cardFactory.getCards().stream().filter(x -> x.getName().equals("Apollo")).findFirst().orElse(null);
        selectedCards.clear();
        assert apollo != null;
        selectedCards.add(apollo.getName());
        try{
            setupManager.setSelectedCards(Mirko.getNickname(), selectedCards);
            setupManager.setSelectedCards(Matteo.getNickname(), selectedCards);
            setupManager.setSelectedCards(Andrea.getNickname(), selectedCards);
            assert false;
        } catch (InvalidPacketException e) {
            assert true;
        }
    }

    /**
     * In the chosen cards there is a card that is part of the possible choices.
     */
    @Test
    void testSelectedCards5(){
        setupManager.start();
        CardFile pan = cardFactory.getCards().stream().filter(x -> x.getName().equals("Pan")).findFirst().orElse(null);
        selectedCards.remove("Apollo");
        assert pan != null;
        selectedCards.add(pan.getName());
        try{
            setupManager.setSelectedCards(Mirko.getNickname(), selectedCards);
            setupManager.setSelectedCards(Matteo.getNickname(), selectedCards);
            setupManager.setSelectedCards(Andrea.getNickname(), selectedCards);
            assert false;
        } catch (InvalidPacketException e) {
            assert true;
        }
    }

    /*@Test
    void testSelectedCards6(){
        setupManager.start();
        try{
            setupManager.setSelectedCards(Mirko.getNickname(), selectedCards);
            setupManager.setSelectedCards(Matteo.getNickname(), selectedCards);
            setupManager.setSelectedCards(Andrea.getNickname(), selectedCards);
            assert true;
        } catch (InvalidPacketException e) {
            assert false;
        }
        assertEquals(setupManager.getSetupPhase(), SetupPhase.WAIT_CARDS);
        try{
            setupManager.setSelectedCards(Mirko.getNickname(), selectedCards.subList(0,0));
            setupManager.setSelectedCards(Matteo.getNickname(), selectedCards.subList(1,1));
            setupManager.setSelectedCards(Andrea.getNickname(), selectedCards.subList(2,2));
            assert true;
        } catch (InvalidPacketException e) {
            assert false;
        }
        assertEquals(setupManager.getSetupPhase(), SetupPhase.WAIT_CARDS);
        try{
            setupManager.setSelectedCards(Mirko.getNickname(), selectedCards.subList(0,0));
            setupManager.setSelectedCards(Matteo.getNickname(), selectedCards.subList(1,1));
            setupManager.setSelectedCards(Andrea.getNickname(), selectedCards.subList(2,2));
            assert true;
        } catch (InvalidPacketException e) {
            assert false;
        }
        assertEquals(setupManager.getSetupPhase(), SetupPhase.WAIT_CARDS);
        try{
            setupManager.setSelectedCards(Mirko.getNickname(), selectedCards.subList(0,0));
            setupManager.setSelectedCards(Matteo.getNickname(), selectedCards.subList(1,1));
            setupManager.setSelectedCards(Andrea.getNickname(), selectedCards.subList(2,2));
            assert true;
        } catch (InvalidPacketException e) {
            assert false;
        }
        assertEquals(setupManager.getSetupPhase(), SetupPhase.WAIT_START_PLAYER);
    }*/


}