package it.polimi.ingsw.server.model;

import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

class WorkerTest {

    /**
     * Test if the getters work correctly.
     * Test if the initial position is set to null.
     */
    @Test
    void testGetters(){
        String nickname = "NICK";
        String workerID = "WW1";
        Worker workerT = new Worker(workerID, nickname);

        assertEquals(workerT.getID(), workerID);
        assertEquals(workerT.getPlayerID(), nickname);
        assertNull(workerT.getPosition());
    }

    /**
     * Test if the setPosition method correctly set a position contained in the Board.
     */
    @Test
    void testSetters(){
        String nickname = "NICK";
        String workerID = "WW1";
        Worker workerT = new Worker(workerID, nickname);
        Point position = new Point(0,0);

        assertNull(workerT.getPosition());

        //Position on the board -> the getter should return the set position.
        workerT.setPosition(position);
        assertEquals(position, workerT.getPosition());

    }

    /**
     * Test if two Workers with the same ID and Player are the equal.
     * Test if two Workers with different ID and position but same Player are not equal.
     * Test if two Workers with the same ID, position and Player are equal.
     */
    @Test
    void testEquals(){
        String nickname = "NICK";
        Player player1 = new Player(nickname);
        String workerID = "WW1";
        Worker worker1 = new Worker(workerID, player1.getNickname());

        Player player2 = player1.clone();
        Worker worker2 = new Worker(workerID, player2.getNickname());

        //Test with the position se to null.
        assertEquals(worker1, worker2);
        assertEquals(worker1.hashCode(),worker2.hashCode());

        //Test with different position and id but not Player.
        Worker worker3 = new Worker("WW3", player1.getNickname());
        Point position = new Point(0,0);
        Point position2 = new Point(1,0);

        worker1.setPosition(position);
        worker3.setPosition(position2);

        assertNotEquals(worker1, worker3);
        assertNotEquals(worker1.hashCode(), worker3.hashCode());

        //Test with the same position on the Board. (Like first case but with a set and equal position)

        worker2.setPosition(position);

        assertEquals(worker1, worker2);
    }

    /**
     * Test clone method
     */
    @Test
    void testClone(){
        String w1ID = "WORKER1";
        String w2ID = "WORKER1";
        String nickPlayer1 = "NICK1";
        Point point = new Point(1,1);

        //With null position
        Worker w1 = new Worker(w1ID,nickPlayer1);
        Worker cloned1 = w1.clone();

        assertEquals(w1,cloned1);
        assertNotSame(w1,cloned1);
        assertNull(w1.getPosition());
        assertNull(cloned1.getPosition());
        assertEquals(w1.getPlayerID(),cloned1.getPlayerID());
        assertEquals(w1.getID(),cloned1.getID());

        //With not null position
        Worker w2 = new Worker(w2ID, nickPlayer1);
        w2.setPosition(point);
        Worker cloned2 = w2.clone();

        assertEquals(w2,cloned2);
        assertNotSame(w2,cloned2);
        assertEquals(w2.getPosition(),cloned2.getPosition());
        assertNotSame(w2.getPosition(),cloned2.getPosition());
        assertEquals(w2.getPlayerID(),cloned2.getPlayerID());
        assertEquals(w2.getID(),cloned2.getID());
    }
}