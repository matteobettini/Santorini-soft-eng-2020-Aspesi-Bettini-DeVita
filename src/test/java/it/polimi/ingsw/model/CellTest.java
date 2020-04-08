package it.polimi.ingsw.model;

import it.polimi.ingsw.model.enums.BuildingType;
import it.polimi.ingsw.model.enums.LevelType;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

//Test on canBuild(List) is missing
class CellTest {

    /**
     * Verify that data provided is reachable via getters
     * Position
     * GetTopBuilding
     */
    @Test
    void testGetters() {
        Point pointT = new Point(0,0);
        Cell cellT = new Cell(pointT);
        assertEquals(cellT.getPosition(), pointT);

        //Test empty cell top building.
        assertEquals(cellT.getTopBuilding(), LevelType.GROUND);
        cellT.addBuilding(BuildingType.FIRST_FLOOR);
        assertEquals(cellT.getTopBuilding(), LevelType.FIRST_FLOOR);
        cellT.addBuilding(BuildingType.SECOND_FLOOR);
        assertEquals(cellT.getTopBuilding(), LevelType.SECOND_FLOOR);
        cellT.addBuilding(BuildingType.THIRD_FLOOR);
        assertEquals(cellT.getTopBuilding(), LevelType.THIRD_FLOOR);
        cellT.addBuilding(BuildingType.DOME);
        assertEquals(cellT.getTopBuilding(), LevelType.DOME);
        //Test empty cell getWorkerID.
        assertNull(cellT.getWorkerID());

    }


    /**
     * This test checks that it is possible to place a Worker on an empty Cell
     * at any level(except DOME)
     */
    @Test
    void testSetWorkerOnEmptyCell() {
        Cell cellT = new Cell(new Point(0,0));
        String workerID = "WW1";

        assertFalse(cellT.isOccupied());
        assertFalse(cellT.hasWorker());
        cellT.setWorker(workerID);
        assertEquals(cellT.getWorkerID(),workerID);
        assertTrue(cellT.isOccupied());
        assertTrue(cellT.hasWorker());
    }

    /**
     * This test checks that it is not possible to place a Worker on an occupied Cell
     * at any level.
     */
    @Test
    void testSetWorkerOnOccupiedCell() {
        Cell cellT = new Cell(new Point(0,0));
        String worker1 = "WW1";
        String worker2 = "WW2";
        cellT.setWorker(worker1);

        assertTrue(cellT.isOccupied());
        assertTrue(cellT.hasWorker());
        assertFalse(cellT.setWorker(worker2));
        assertEquals(cellT.getWorkerID(), worker1);

    }

    /**
     * This test checks that it is always not possible to place a Worker on a DOME.
     */
    @Test
    void testSetWorkerOnDome(){
        Cell cellT = new Cell(new Point(0,0));
        String workerID = "WW1";
        cellT.addBuilding(BuildingType.DOME);

        assertTrue(cellT.isOccupied());
        assertFalse(cellT.hasWorker());
        assertFalse(cellT.setWorker(workerID));
        assertNull(cellT.getWorkerID());

    }

    @Test
    void testRemoverWorker() {
        Cell cellT = new Cell(new Point(0, 0));
        String workerID = "WW1";
        assertFalse(cellT.removeWorker());
        cellT.setWorker(workerID);
        assertTrue(cellT.removeWorker());

        assertFalse(cellT.isOccupied());
        assertFalse(cellT.hasWorker());
    }

    /**
     * Verify that it is possible to build only in the correct order.
     */
    @Test
    void testAddBuilding(){
        Point pointT = new Point(0,0);
        Cell cellT = new Cell(pointT);

        //Check building on level GROUND.
        assertEquals(cellT.getTopBuilding(), LevelType.GROUND);
        for(BuildingType buildingType : BuildingType.values()){
            if(buildingType != BuildingType.FIRST_FLOOR && buildingType != BuildingType.DOME){
                assertFalse(cellT.canBuild(buildingType));
                assertFalse(cellT.addBuilding(buildingType));
                assertEquals(cellT.getTopBuilding(), LevelType.GROUND);
            }
            else{
                assertTrue(cellT.canBuild(buildingType));
            }
        }

        //Check building on level FIRST_FLOOR.
        assertTrue(cellT.addBuilding(BuildingType.FIRST_FLOOR));
        assertEquals(cellT.getTopBuilding(), LevelType.FIRST_FLOOR);
        for(BuildingType buildingType : BuildingType.values()){
            if(buildingType != BuildingType.SECOND_FLOOR && buildingType != BuildingType.DOME){
                assertFalse(cellT.canBuild(buildingType));
                assertFalse(cellT.addBuilding(buildingType));
                assertEquals(cellT.getTopBuilding(), LevelType.FIRST_FLOOR);
            }
            else{
                assertTrue(cellT.canBuild(buildingType));
            }
        }

        //Check building on level SECOND_FLOOR.
        assertTrue(cellT.addBuilding(BuildingType.SECOND_FLOOR));
        assertEquals(cellT.getTopBuilding(), LevelType.SECOND_FLOOR);
        for(BuildingType buildingType : BuildingType.values()){
            if(buildingType != BuildingType.THIRD_FLOOR && buildingType != BuildingType.DOME){
                assertFalse(cellT.canBuild(buildingType));
                assertFalse(cellT.addBuilding(buildingType));
                assertEquals(cellT.getTopBuilding(), LevelType.SECOND_FLOOR);
            }
            else{
                assertTrue(cellT.canBuild(buildingType));
            }
        }

        //Check building on level THIRD_FLOOR.
        assertTrue(cellT.addBuilding(BuildingType.THIRD_FLOOR));
        assertEquals(cellT.getTopBuilding(), LevelType.THIRD_FLOOR);
        for(BuildingType buildingType : BuildingType.values()){
            if(buildingType != BuildingType.DOME){
                assertFalse(cellT.canBuild(buildingType));
                assertFalse(cellT.addBuilding(buildingType));
                assertEquals(cellT.getTopBuilding(), LevelType.THIRD_FLOOR);
            }
            else{
                assertTrue(cellT.canBuild(buildingType));
            }
        }

        //Check building on level DOME.
        assertTrue(cellT.addBuilding(BuildingType.DOME));
        assertEquals(cellT.getTopBuilding(), LevelType.DOME);
        for(BuildingType buildingType : BuildingType.values()){
            assertFalse(cellT.canBuild(buildingType));
            assertFalse(cellT.addBuilding(buildingType));
            assertEquals(cellT.getTopBuilding(), LevelType.DOME);
        }

    }

    /**
     * This test verifies the correctness of both of the canBuild methods.
     */
    @Test
    void testCanBuild(){
        Cell cellT = new Cell(new Point(0,0));
        cellT.setWorker("WORKER");

        //CASE 1: There is a Worker on the Cell.
        assertFalse(cellT.canBuild(BuildingType.FIRST_FLOOR));
        List<BuildingType> buildings = new ArrayList<>();
        buildings.add(BuildingType.FIRST_FLOOR);
        buildings.add(BuildingType.SECOND_FLOOR);
        assertFalse(cellT.canBuild(buildings));

        cellT.removeWorker();
        buildings.clear();
        //CASE 2: There is not a Worker on the Cell.
        //2.A The Cell is completely Empty.
        assertTrue(cellT.canBuild(BuildingType.FIRST_FLOOR));
        assertFalse(cellT.canBuild(BuildingType.SECOND_FLOOR));
        assertFalse(cellT.canBuild(BuildingType.THIRD_FLOOR));
        assertTrue(cellT.canBuild(BuildingType.DOME));

        //Test with some possible combinations:
        //FIRST FIRST
        buildings.add(BuildingType.FIRST_FLOOR);
        assertTrue(cellT.canBuild(buildings));
        buildings.add(BuildingType.FIRST_FLOOR);
        assertFalse(cellT.canBuild(buildings));
        //FIRST SECOND
        buildings.clear();
        buildings.add(BuildingType.FIRST_FLOOR);
        buildings.add(BuildingType.SECOND_FLOOR);
        assertTrue(cellT.canBuild(buildings));
        //FIRST THIRD
        buildings.clear();
        buildings.add(BuildingType.FIRST_FLOOR);
        buildings.add(BuildingType.THIRD_FLOOR);
        assertFalse(cellT.canBuild(buildings));
        //FIRST DOME
        buildings.clear();
        buildings.add(BuildingType.FIRST_FLOOR);
        buildings.add(BuildingType.DOME);
        assertTrue(cellT.canBuild(buildings));
        //SECOND FIRST
        buildings.clear();
        buildings.add(BuildingType.SECOND_FLOOR);
        buildings.add(BuildingType.FIRST_FLOOR);
        assertFalse(cellT.canBuild(buildings));
        //SECOND SECOND
        buildings.clear();
        buildings.add(BuildingType.SECOND_FLOOR);
        buildings.add(BuildingType.SECOND_FLOOR);
        assertFalse(cellT.canBuild(buildings));
        //SECOND THIRD
        buildings.clear();
        buildings.add(BuildingType.SECOND_FLOOR);
        buildings.add(BuildingType.THIRD_FLOOR);
        assertFalse(cellT.canBuild(buildings));
        //SECOND DOME
        buildings.clear();
        buildings.add(BuildingType.SECOND_FLOOR);
        buildings.add(BuildingType.DOME);
        assertFalse(cellT.canBuild(buildings));
        //THIRD SECOND
        buildings.clear();
        buildings.add(BuildingType.THIRD_FLOOR);
        buildings.add(BuildingType.SECOND_FLOOR);
        assertFalse(cellT.canBuild(buildings));
        //THIRD FIRST
        buildings.clear();
        buildings.add(BuildingType.THIRD_FLOOR);
        buildings.add(BuildingType.FIRST_FLOOR);
        assertFalse(cellT.canBuild(buildings));
        //THIRD THIRD
        buildings.clear();
        buildings.add(BuildingType.THIRD_FLOOR);
        buildings.add(BuildingType.THIRD_FLOOR);
        assertFalse(cellT.canBuild(buildings));
        //THIRD DOME
        buildings.clear();
        buildings.add(BuildingType.THIRD_FLOOR);
        buildings.add(BuildingType.DOME);
        assertFalse(cellT.canBuild(buildings));
        //DOME FIRST
        buildings.clear();
        buildings.add(BuildingType.DOME);
        assertTrue(cellT.canBuild(buildings));
        buildings.add(BuildingType.FIRST_FLOOR);
        assertFalse(cellT.canBuild(buildings));
        //DOME SECOND
        buildings.clear();
        buildings.add(BuildingType.DOME);
        buildings.add(BuildingType.SECOND_FLOOR);
        assertFalse(cellT.canBuild(buildings));
        //DOME THIRD
        buildings.clear();
        buildings.add(BuildingType.DOME);
        buildings.add(BuildingType.THIRD_FLOOR);
        assertFalse(cellT.canBuild(buildings));
        //DOME DOME
        buildings.clear();
        buildings.add(BuildingType.DOME);
        buildings.add(BuildingType.DOME);
        assertFalse(cellT.canBuild(buildings));

        //FIRST SECOND THIRD
        buildings.clear();
        buildings.add(BuildingType.FIRST_FLOOR);
        buildings.add(BuildingType.SECOND_FLOOR);
        buildings.add(BuildingType.THIRD_FLOOR);
        assertTrue(cellT.canBuild(buildings));
        //FIRST SECOND DOME
        buildings.clear();
        buildings.add(BuildingType.FIRST_FLOOR);
        buildings.add(BuildingType.SECOND_FLOOR);
        buildings.add(BuildingType.DOME);
        assertTrue(cellT.canBuild(buildings));
        //FIRST DOME SECOND
        buildings.clear();
        buildings.add(BuildingType.FIRST_FLOOR);
        buildings.add(BuildingType.DOME);
        buildings.add(BuildingType.SECOND_FLOOR);
        assertFalse(cellT.canBuild(buildings));
        //SECOND FIRST THIRD
        buildings.clear();
        buildings.add(BuildingType.SECOND_FLOOR);
        buildings.add(BuildingType.FIRST_FLOOR);
        buildings.add(BuildingType.THIRD_FLOOR);
        assertFalse(cellT.canBuild(buildings));
        //FIRST FIRST SECOND
        buildings.clear();
        buildings.add(BuildingType.FIRST_FLOOR);
        buildings.add(BuildingType.FIRST_FLOOR);
        buildings.add(BuildingType.SECOND_FLOOR);
        assertFalse(cellT.canBuild(buildings));
        //FIRST FIRST DOME
        buildings.clear();
        buildings.add(BuildingType.FIRST_FLOOR);
        buildings.add(BuildingType.FIRST_FLOOR);
        buildings.add(BuildingType.DOME);
        assertFalse(cellT.canBuild(buildings));
        //THIRD SECOND FIRST
        buildings.clear();
        buildings.add(BuildingType.THIRD_FLOOR);
        buildings.add(BuildingType.SECOND_FLOOR);
        buildings.add(BuildingType.FIRST_FLOOR);
        assertFalse(cellT.canBuild(buildings));

        //FIRST SECOND THIRD DOME
        buildings.clear();
        buildings.add(BuildingType.FIRST_FLOOR);
        buildings.add(BuildingType.SECOND_FLOOR);
        buildings.add(BuildingType.THIRD_FLOOR);
        buildings.add(BuildingType.DOME);
        assertTrue(cellT.canBuild(buildings));
        //FIRST SECOND DOME THIRD
        buildings.clear();
        buildings.add(BuildingType.FIRST_FLOOR);
        buildings.add(BuildingType.SECOND_FLOOR);
        buildings.add(BuildingType.DOME);
        buildings.add(BuildingType.THIRD_FLOOR);
        assertFalse(cellT.canBuild(buildings));
        //FIRST DOME SECOND THIRD
        buildings.clear();
        buildings.add(BuildingType.FIRST_FLOOR);
        buildings.add(BuildingType.DOME);
        buildings.add(BuildingType.SECOND_FLOOR);
        buildings.add(BuildingType.THIRD_FLOOR);
        assertFalse(cellT.canBuild(buildings));
        //SECOND FIRST THIRD DOME
        buildings.clear();
        buildings.add(BuildingType.SECOND_FLOOR);
        buildings.add(BuildingType.FIRST_FLOOR);
        buildings.add(BuildingType.THIRD_FLOOR);
        buildings.add(BuildingType.DOME);
        assertFalse(cellT.canBuild(buildings));
        //FIRST SECOND THIRD THIRD
        buildings.clear();
        buildings.add(BuildingType.FIRST_FLOOR);
        buildings.add(BuildingType.SECOND_FLOOR);
        buildings.add(BuildingType.THIRD_FLOOR);
        buildings.add(BuildingType.THIRD_FLOOR);
        assertFalse(cellT.canBuild(buildings));
        //DOME FIRST SECOND THIRD
        buildings.clear();
        buildings.add(BuildingType.DOME);
        buildings.add(BuildingType.FIRST_FLOOR);
        buildings.add(BuildingType.SECOND_FLOOR);
        buildings.add(BuildingType.THIRD_FLOOR);
        assertFalse(cellT.canBuild(buildings));

    }

    /**
     * Testing if the getHeight method returns the right integer corresponding to the number
     * of buildings on the Cell.
     */
    @Test
    void testGetHeight(){
        Cell cellT = new Cell(new Point(0,0));

        assertEquals(cellT.getHeight(),0);

        cellT.addBuilding(BuildingType.FIRST_FLOOR);
        assertEquals(cellT.getHeight(), 1);
        cellT.addBuilding(BuildingType.FIRST_FLOOR);
        assertEquals(cellT.getHeight(), 1);
        cellT.addBuilding(BuildingType.SECOND_FLOOR);
        assertEquals(cellT.getHeight(), 2);
        cellT.addBuilding(BuildingType.SECOND_FLOOR);
        assertEquals(cellT.getHeight(), 2);
        cellT.addBuilding(BuildingType.THIRD_FLOOR);
        assertEquals(cellT.getHeight(), 3);
        cellT.addBuilding(BuildingType.THIRD_FLOOR);
        assertEquals(cellT.getHeight(), 3);
        cellT.addBuilding(BuildingType.DOME);
        assertEquals(cellT.getHeight(), 4);
        cellT.addBuilding(BuildingType.DOME);
        assertEquals(cellT.getHeight(), 4);

        cellT = new Cell(new Point(1,1));
        cellT.addBuilding(BuildingType.DOME);
        assertEquals(cellT.getHeight(), 1);

        cellT = new Cell(new Point(2,2));
        cellT.addBuilding(BuildingType.FIRST_FLOOR);
        cellT.addBuilding(BuildingType.DOME);
        assertEquals(cellT.getHeight(), 2);

        cellT = new Cell(new Point(3,3));
        cellT.addBuilding(BuildingType.FIRST_FLOOR);
        cellT.addBuilding(BuildingType.SECOND_FLOOR);
        cellT.addBuilding(BuildingType.DOME);
        assertEquals(cellT.getHeight(), 3);
    }

    /**
     * Check edge case where i want to check an empty list
     */
    @Test
    void testCanBuildEmpty(){
        Cell cellT = new Cell(new Point(0,0));
        assertFalse(cellT.canBuild(new LinkedList<>()));
    }

    /**
     * Verify that it is not possible to build onto a DOME.
     */
    @Test
    void testAddBuildingOnDome(){
        Point pointT = new Point(0,0);
        Cell cellT = new Cell(pointT);

        assertEquals(cellT.getTopBuilding(), LevelType.GROUND);

        //Check after building DOME on level GROUND.
        assertTrue(cellT.addBuilding(BuildingType.DOME));
        assertEquals(cellT.getTopBuilding(), LevelType.DOME);
        for(BuildingType buildingType : BuildingType.values()){
            assertFalse(cellT.canBuild(buildingType));
            assertFalse(cellT.addBuilding(buildingType));
            assertEquals(cellT.getTopBuilding(), LevelType.DOME);
        }

        //Check after building DOME on FIRST_FLOOR.
        cellT = new Cell(pointT);
        cellT.addBuilding(BuildingType.FIRST_FLOOR);
        assertTrue(cellT.addBuilding(BuildingType.DOME));
        assertEquals(cellT.getTopBuilding(), LevelType.DOME);
        for(BuildingType buildingType : BuildingType.values()){
            assertFalse(cellT.canBuild(buildingType));
            assertFalse(cellT.addBuilding(buildingType));
            assertEquals(cellT.getTopBuilding(), LevelType.DOME);
        }


        //Check after building DOME on SECOND_FLOOR.
        cellT = new Cell(pointT);
        cellT.addBuilding(BuildingType.FIRST_FLOOR);
        cellT.addBuilding(BuildingType.SECOND_FLOOR);
        assertTrue(cellT.addBuilding(BuildingType.DOME));
        assertEquals(cellT.getTopBuilding(), LevelType.DOME);
        for(BuildingType buildingType : BuildingType.values()){
            assertFalse(cellT.canBuild(buildingType));
            assertFalse(cellT.addBuilding(buildingType));
            assertEquals(cellT.getTopBuilding(), LevelType.DOME);
        }

        //Check after building DOME on THIRD_FLOOR.
        cellT = new Cell(pointT);
        cellT.addBuilding(BuildingType.FIRST_FLOOR);
        cellT.addBuilding(BuildingType.SECOND_FLOOR);
        cellT.addBuilding(BuildingType.THIRD_FLOOR);
        assertTrue(cellT.addBuilding(BuildingType.DOME));
        assertEquals(cellT.getTopBuilding(), LevelType.DOME);
        for(BuildingType buildingType : BuildingType.values()){
            assertFalse(cellT.canBuild(buildingType));
            assertFalse(cellT.addBuilding(buildingType));
            assertEquals(cellT.getTopBuilding(), LevelType.DOME);
        }


    }

    /**
     * This test verifies that two Cells with the same position and different Workers are equal.
     */
    @Test
    void testEqualsWithDifferentWorkers() {
        Point pointT = new Point(0,0);
        String workerID1 = "WW1";
        String workerID2 = "WW2";
        Cell cell1 = new Cell(pointT);

        //Cells should be equal even if they have different buildings
        cell1.addBuilding(BuildingType.FIRST_FLOOR);
        cell1.setWorker(workerID1);

        Cell cell2 = new Cell(pointT);

        cell2.addBuilding(BuildingType.FIRST_FLOOR);
        cell2.addBuilding(BuildingType.SECOND_FLOOR);
        cell2.setWorker(workerID2);

        assertEquals(cell1,cell2);
        assertEquals(cell1.hashCode(), cell2.hashCode());
    }

    /**
     * This test verifies that two Cells with the same position and at least one null Worker are equal.
     */
    @Test
    void testEqualsWithNullWorkers() {
        Point pointT = new Point(0,0);
        Cell cell1 = new Cell(pointT);
        Cell cell2 = new Cell(pointT);

        //CASE 1: Both null.

        cell1.addBuilding(BuildingType.FIRST_FLOOR);
        cell2.addBuilding(BuildingType.FIRST_FLOOR);
        cell2.addBuilding(BuildingType.SECOND_FLOOR);
        cell2.addBuilding(BuildingType.THIRD_FLOOR);
        assertEquals(cell1,cell2);
        assertEquals(cell1.hashCode(), cell2.hashCode());

        //CASE 2: Only one null.
        String workerID1 = "WW1";
        cell1.setWorker(workerID1);

        assertEquals(cell1,cell2);
        assertEquals(cell1.hashCode(), cell2.hashCode());
    }

    /**
     * This test checks that the cloned Cell and the Original Cell have not the same reference.
     */
    @Test
    void testClone() {
        Cell cellT = new Cell(new Point(0,0));
        assertNull(cellT.getWorkerID());
        cellT.addBuilding(BuildingType.FIRST_FLOOR);

        Cell clonedCell = cellT.clone();

        assertNotSame(cellT, clonedCell);
        assertEquals(cellT,clonedCell);

        //Check building remains.
        assertEquals(cellT.getTopBuilding(), clonedCell.getTopBuilding());

        assertNull(clonedCell.getWorkerID());

        //Check player remains
        String workerID1 = "WW1";
        cellT = new Cell(new Point(0,0));
        cellT.setWorker(workerID1);

        clonedCell = cellT.clone();

        assertNotSame(cellT, clonedCell);
        assertEquals(cellT,clonedCell);
        assertEquals(cellT.getWorkerID(), workerID1);
    }




}