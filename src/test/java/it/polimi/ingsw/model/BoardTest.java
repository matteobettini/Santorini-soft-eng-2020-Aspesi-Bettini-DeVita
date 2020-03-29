package it.polimi.ingsw.model;

import it.polimi.ingsw.model.enums.BuildingType;
import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

class BoardTest {

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

    @Test
    void testUseBuilding(){
        Board boardT = new Board();

        //If there is availability the building can be used and the availability decreases by 1.
        for(int i = 1; i <= 22; ++i){
            assertTrue(boardT.canUseBuilding(BuildingType.FIRST_FLOOR));
            assertTrue(boardT.useBuilding(BuildingType.FIRST_FLOOR));
            assertEquals(boardT.availableBuildings(BuildingType.FIRST_FLOOR),22 - i);
        }

        for(int i = 1; i <= 18; ++i){
            assertTrue(boardT.canUseBuilding(BuildingType.SECOND_FLOOR));
            assertTrue(boardT.useBuilding(BuildingType.SECOND_FLOOR));
            assertEquals(boardT.availableBuildings(BuildingType.SECOND_FLOOR),18 - i);
        }

        for(int i = 1; i <= 14; ++i){
            assertTrue(boardT.canUseBuilding(BuildingType.THIRD_FLOOR));
            assertTrue(boardT.useBuilding(BuildingType.THIRD_FLOOR));
            assertEquals(boardT.availableBuildings(BuildingType.THIRD_FLOOR),14 - i);
        }

        for(int i = 1; i <= 18; ++i){
            assertTrue(boardT.canUseBuilding(BuildingType.DOME));
            assertTrue(boardT.useBuilding(BuildingType.DOME));
            assertEquals(boardT.availableBuildings(BuildingType.DOME),18 - i);
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
    }

}