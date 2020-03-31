package it.polimi.ingsw.model;

import static org.junit.jupiter.api.Assertions.*;

import it.polimi.ingsw.model.cardReader.CardFile;
import it.polimi.ingsw.model.cardReader.CardRule;
import it.polimi.ingsw.model.cardReader.CardRuleTest;
import it.polimi.ingsw.model.enums.PlayerFlag;
import it.polimi.ingsw.model.enums.PlayerState;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

class PlayerTest {

    /**
     * Verifies that initially the Player has only
     * the given nickname and two different Workers associated to him.
     */
    @Test
    void testGetters(){
        String playerNick = "N1";
        Player playerT = new Player(playerNick);

        assertEquals(playerNick, playerT.getNickname());
        assertNull(playerT.getCard());
        assertEquals(playerT.getState(), PlayerState.TURN_STARTED);
        assertNotNull(playerT.getWorkers());
        assertEquals(playerT.getWorkers().size(), 2);
        assertNotEquals(playerT.getWorkers().get(0),playerT.getWorkers().get(1));
    }

    /**
     * Test if the method setCard correctly set the Player's Card.
     * Test if the method setState correctly set the Player's State.
     */
    @Test
    void testSetters(){
        String playerNick = "N1";
        Player playerT = new Player(playerNick);

        String nameTest = "TEST01";
        String descrTest = "DESCR01";

        List<CardRule> rules = new ArrayList<>();
        rules.add(CardRuleTest.getEmptyCardRule());
        CardFile cardFile = new CardFile(nameTest, descrTest, rules);

        //Test for the Card setter
        playerT.setCard(cardFile);
        assertNotNull(playerT.getCard());
        assertEquals(cardFile, playerT.getCard());

        //Test for the state setter
        PlayerState[] playerStates = PlayerState.values();
        for(PlayerState s : playerStates){
            playerT.setPlayerState(s);
            assertEquals(s, playerT.getState());
        }

    }

    /**
     * Test if addFlag method correctly adds a given flag to the Player.
     * Test if hasFlag method correctly return true if the given flag i contained, false otherwise.
     * Test if clearFlags method correctly deletes all the flags attached to the Player.
     */
    @Test
    void testFlags(){
        String playerNick = "N1";
        Player playerT = new Player(playerNick);
        PlayerFlag[] flags = PlayerFlag.values();

        //Add all the possible flags to the player.
        for(PlayerFlag f : flags){
            playerT.addFlag(f);
            assertTrue(playerT.hasFlag(f));
        }

        //Clear all the flags.
        playerT.clearFlags();
        for(PlayerFlag f : flags){
            assertFalse(playerT.hasFlag(f));
        }
    }

    /**
     * Test if the cloned Player has the same attributes but it is not identical to the original one.
     */
    @Test
    void testClone(){
        String playerNick = "N1";
        Player playerT = new Player(playerNick);

        Player clonedPlayer = playerT.clone();

        assertNotSame(playerT, clonedPlayer);
        assertEquals(playerT, clonedPlayer);

        //Check that the cloned Player and the original one have the same Workers but not identical.
        for(int i = 0; i < playerT.getWorkers().size(); ++i){
            assertNotSame(playerT.getWorkers().get(i), clonedPlayer.getWorkers().get(i));
            assertEquals(playerT.getWorkers().get(i), clonedPlayer.getWorkers().get(i));
        }
    }

    /**
     * Test if two instances of Player with the same nickname are equal and have the same hashCode.
     */
    @Test
    void testEquals(){
        String nickname = "NICK";
        Player p1 = new Player(nickname);
        Player p2 = new Player(nickname);


        assertEquals(p1,p2);
        assertEquals(p1.hashCode(),p2.hashCode());



    }

}