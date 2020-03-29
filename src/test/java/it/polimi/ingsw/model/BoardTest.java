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

        //If there is availability the building can be used
        for(int i = 1; i <= 22; ++i){
            assertTrue(boardT.canUseBuilding(BuildingType.FIRST_FLOOR));
            assertTrue(boardT.useBuilding(BuildingType.FIRST_FLOOR));
        }
    }

}