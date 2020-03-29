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
     * If the position is not contained in the Board the setter does not associate the position to the Player.
     */
    @Test
    void testSetters(){
        String nickname = "NICK";
        Player playerT = new Player(nickname);
        String workerID = "WW1";
        Worker workerT = new Worker(workerID, playerT);
        Point rightPosition = new Point(0,0);
        Point positionOutOfBound = new Point(6,7);

        //Position out of bound -> the getter should return null.
        workerT.setPosition(positionOutOfBound);
        assertNull(workerT.getPosition());

        //Position on the board -> the getter should return the set position.
        workerT.setPosition(rightPosition);
        assertEquals(rightPosition, workerT.getPosition());

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
        assertEquals(worker1.hashCode(), worker2.hashCode());

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
        assertEquals(worker1.hashCode(), worker2.hashCode());

        //Two different workers can't have the same position or the same ID.

    }

    @Test
    void testClone(){
        String nickname = "NICK";
        Player playerT = new Player(nickname);
        String workerID = "WW1";
        Worker workerT = new Worker(workerID, playerT);

        Worker clonedWorker = workerT.clone();

        //Clone should work also with a null position.
        assertNotSame(workerT, clonedWorker);
        assertEquals(workerT, clonedWorker);

        Point position = new Point(0,0);
        workerT.setPosition(position);
        clonedWorker = workerT.clone();

        //Clone with a set position.
        assertNotSame(workerT, clonedWorker);
        assertEquals(workerT, clonedWorker);

    }

}