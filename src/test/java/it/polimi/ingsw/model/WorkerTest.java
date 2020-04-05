package it.polimi.ingsw.model;

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
        Player playerT = new Player(nickname);
        String workerID = "WW1";
        Worker workerT = new Worker(workerID, playerT);

        assertEquals(workerT.getID(), workerID);
        assertEquals(workerT.getPlayer(), playerT);
        assertNull(workerT.getPosition());
    }

    /**
     * Test if the setPosition method correctly set a position contained in the Board.
     */
    @Test
    void testSetters(){
        String nickname = "NICK";
        Player playerT = new Player(nickname);
        String workerID = "WW1";
        Worker workerT = new Worker(workerID, playerT);
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
        Worker worker1 = new Worker(workerID, player1);

        Player player2 = player1.clone();
        Worker worker2 = new Worker(workerID, player2);

        //Test with the position se to null.
        assertEquals(worker1, worker2);

        //Test with different position and id but not Player.
        Worker worker3 = new Worker("WW3", player1);
        Point position = new Point(0,0);
        Point position2 = new Point(1,0);

        worker1.setPosition(position);
        worker3.setPosition(position2);

        assertNotEquals(worker1, worker3);
        assertNotEquals(worker1.hashCode(), worker3.hashCode());

        //Test with the same position on the Board. (Like first case but with a set and equal position)

        worker2.setPosition(position);

        assertEquals(worker1, worker2);

        //Two different workers can't have the same position or the same ID.

    }

}