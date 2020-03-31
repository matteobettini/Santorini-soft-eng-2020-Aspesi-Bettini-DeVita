package it.polimi.ingsw.model;

import it.polimi.ingsw.model.enums.BuildingType;
import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

class BoardTest {

    /**
     * Test if all the Cells in the Board are not null.
     * Test if the getCell method returns only Cell in the correct position.
     * Test if the getCell returns null if the given position is out of Board's bounds.
     */
    @Test
    void testGetters(){
        Board boardT = new Board();

        //Each Cell in the Board is never null
        for(int i = 0; i < 5; ++i){
            for(int j = 0; j < 5; ++j){
                Point p = new Point(i,j);
                assertNotNull(boardT.getCell(p));
            }
        }

        //Each Cell has the right position in the Board
        for(int i = 0; i < 5; ++i){
            for(int j = 0; j < 5; ++j){
                Point p = new Point(i,j);
                assertEquals(boardT.getCell(p).getPosition(), p);
            }
        }

        //A Cell out of bound is always null
        Point p = new Point(6,7);
        assertNull(boardT.getCell(p));


    }

    /**
     * Test if it is possible to build provided that there are enough buildings.
     * Test if the availableBuildings and restockBuilding methods return the correct numbers.
     */
    @Test
    void testUseBuilding(){
        Board boardT = new Board();

        //If there is availability the building can be used and the availability decreases by 1.
        assertEquals(boardT.availableBuildings(BuildingType.FIRST_FLOOR),Board.NUM_OF_FIRST_FLOOR);
        for(int i = 1; i <= Board.NUM_OF_FIRST_FLOOR; ++i){
            assertTrue(boardT.canUseBuilding(BuildingType.FIRST_FLOOR));
            assertTrue(boardT.useBuilding(BuildingType.FIRST_FLOOR));
            assertEquals(boardT.availableBuildings(BuildingType.FIRST_FLOOR),Board.NUM_OF_FIRST_FLOOR - i);
        }

        assertEquals(boardT.availableBuildings(BuildingType.SECOND_FLOOR),Board.NUM_OF_SECOND_FLOOR);
        for(int i = 1; i <= Board.NUM_OF_SECOND_FLOOR; ++i){
            assertTrue(boardT.canUseBuilding(BuildingType.SECOND_FLOOR));
            assertTrue(boardT.useBuilding(BuildingType.SECOND_FLOOR));
            assertEquals(boardT.availableBuildings(BuildingType.SECOND_FLOOR),Board.NUM_OF_SECOND_FLOOR - i);
        }

        assertEquals(boardT.availableBuildings(BuildingType.THIRD_FLOOR),Board.NUM_OF_THIRD_FLOOR);
        for(int i = 1; i <= Board.NUM_OF_THIRD_FLOOR; ++i){
            assertTrue(boardT.canUseBuilding(BuildingType.THIRD_FLOOR));
            assertTrue(boardT.useBuilding(BuildingType.THIRD_FLOOR));
            assertEquals(boardT.availableBuildings(BuildingType.THIRD_FLOOR),Board.NUM_OF_THIRD_FLOOR - i);
        }

        assertEquals(boardT.availableBuildings(BuildingType.DOME),Board.NUM_OF_DOME);
        for(int i = 1; i <= Board.NUM_OF_DOME; ++i){
            assertTrue(boardT.canUseBuilding(BuildingType.DOME));
            assertTrue(boardT.useBuilding(BuildingType.DOME));
            assertEquals(boardT.availableBuildings(BuildingType.DOME),Board.NUM_OF_DOME - i);
        }

        //If there is no availability the building can't be used.
        assertFalse(boardT.canUseBuilding(BuildingType.FIRST_FLOOR));
        assertFalse(boardT.useBuilding(BuildingType.FIRST_FLOOR));
        assertEquals(boardT.availableBuildings(BuildingType.FIRST_FLOOR),0);

        assertFalse(boardT.canUseBuilding(BuildingType.SECOND_FLOOR));
        assertFalse(boardT.useBuilding(BuildingType.SECOND_FLOOR));
        assertEquals(boardT.availableBuildings(BuildingType.SECOND_FLOOR),0);

        assertFalse(boardT.canUseBuilding(BuildingType.THIRD_FLOOR));
        assertFalse(boardT.useBuilding(BuildingType.THIRD_FLOOR));
        assertEquals(boardT.availableBuildings(BuildingType.THIRD_FLOOR),0);

        assertFalse(boardT.canUseBuilding(BuildingType.DOME));
        assertFalse(boardT.useBuilding(BuildingType.DOME));
        assertEquals(boardT.availableBuildings(BuildingType.DOME),0);

        //Checks if the restock method works and only restocks the given building type.
        boardT.restockBuilding(BuildingType.FIRST_FLOOR, Board.NUM_OF_FIRST_FLOOR);
        assertEquals(boardT.availableBuildings(BuildingType.FIRST_FLOOR), Board.NUM_OF_FIRST_FLOOR);
        assertEquals(boardT.availableBuildings(BuildingType.SECOND_FLOOR),0);
        assertEquals(boardT.availableBuildings(BuildingType.THIRD_FLOOR),0);
        assertEquals(boardT.availableBuildings(BuildingType.DOME),0);

        boardT.restockBuilding(BuildingType.SECOND_FLOOR, Board.NUM_OF_SECOND_FLOOR);
        assertEquals(boardT.availableBuildings(BuildingType.FIRST_FLOOR),Board.NUM_OF_FIRST_FLOOR);
        assertEquals(boardT.availableBuildings(BuildingType.SECOND_FLOOR),Board.NUM_OF_SECOND_FLOOR);
        assertEquals(boardT.availableBuildings(BuildingType.THIRD_FLOOR),0);
        assertEquals(boardT.availableBuildings(BuildingType.DOME),0);

        boardT.restockBuilding(BuildingType.THIRD_FLOOR, Board.NUM_OF_THIRD_FLOOR);
        assertEquals(boardT.availableBuildings(BuildingType.FIRST_FLOOR),Board.NUM_OF_FIRST_FLOOR);
        assertEquals(boardT.availableBuildings(BuildingType.SECOND_FLOOR),Board.NUM_OF_SECOND_FLOOR);
        assertEquals(boardT.availableBuildings(BuildingType.THIRD_FLOOR),Board.NUM_OF_THIRD_FLOOR);
        assertEquals(boardT.availableBuildings(BuildingType.DOME),0);

        boardT.restockBuilding(BuildingType.DOME, Board.NUM_OF_DOME);
        assertEquals(boardT.availableBuildings(BuildingType.FIRST_FLOOR),Board.NUM_OF_FIRST_FLOOR);
        assertEquals(boardT.availableBuildings(BuildingType.SECOND_FLOOR),Board.NUM_OF_SECOND_FLOOR);
        assertEquals(boardT.availableBuildings(BuildingType.THIRD_FLOOR),Board.NUM_OF_THIRD_FLOOR);
        assertEquals(boardT.availableBuildings(BuildingType.DOME),Board.NUM_OF_DOME);


    }

    /**
     * Test if the cloned Board equals the original one but it is not the same instance.
     * Test if all the Cells have the same properties but they are not identical.
     */
    @Test
    void testClone(){
        Board boardT = new Board();

        Board clonedBoard = boardT.clone();

        assertNotSame(boardT, clonedBoard);
        assertEquals(boardT,clonedBoard);

        //Check that the number of available buildings stays the same.
        assertEquals(boardT.availableBuildings(BuildingType.FIRST_FLOOR), clonedBoard.availableBuildings(BuildingType.FIRST_FLOOR));
        assertEquals(boardT.availableBuildings(BuildingType.SECOND_FLOOR), clonedBoard.availableBuildings(BuildingType.SECOND_FLOOR));
        assertEquals(boardT.availableBuildings(BuildingType.THIRD_FLOOR), clonedBoard.availableBuildings(BuildingType.THIRD_FLOOR));
        assertEquals(boardT.availableBuildings(BuildingType.DOME), clonedBoard.availableBuildings(BuildingType.DOME));

        //Check that all the Cells are the same but not identical.
        for(int i = 0; i < 5; ++i){
            for(int j = 0; j < 5; ++j){
                Point p = new Point(i,j);
                assertNotSame(boardT.getCell(p),clonedBoard.getCell(p));
                assertEquals(boardT.getCell(p),clonedBoard.getCell(p));
            }
        }
    }

}