package it.polimi.ingsw.model;

import it.polimi.ingsw.model.enums.BuildingType;
import it.polimi.ingsw.model.enums.LevelType;
import org.junit.jupiter.api.Test;

import java.awt.*;

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

        //Test empty cell getWorkerID.
        try{
            cellT.getWorkerID();
            assert false;
        } catch (NoWorkerPresentException e) {
            assert true;
        }

    }


    /**
     * This test checks that it is possible to place a Worker on an empty Cell
     * at any level(except DOME)
     */
    @Test
    void testSetWorkerOnEmptyCell() throws NoWorkerPresentException, WorkerAlreadyPresentException, DomeException {
        Cell cellT = new Cell(new Point(0,0));
        String workerID = "WW1";

        cellT.setWorker(workerID);
        assertEquals(cellT.getWorkerID(),workerID);
    }

    /**
     * This test checks that it is not possible to place a Worker on an occupied Cell
     * at any level.
     */
    @Test
    void testSetWorkerOnOccupiedCell() throws WorkerAlreadyPresentException, DomeException {
        Cell cellT = new Cell(new Point(0,0));
        String worker1 = "WW1";
        String worker2 = "WW2";
        cellT.setWorker(worker1);

        try{
            cellT.setWorker(worker2);
            assert false;
        }
        catch(WorkerAlreadyPresentException e){
            assert true;
        }

    }

    /**
     * This test checks that it is always not possible to place a Worker on a DOME.
     */
    @Test
    void testSetWorkerOnDome(){
        Cell cellT = new Cell(new Point(0,0));
        String workerID = "WW1";
        cellT.addBuilding(BuildingType.DOME);

        try{
            cellT.setWorker(workerID);
            assert false;
        } catch (DomeException e) {
            assert true;
        } catch (WorkerAlreadyPresentException e) {
            assert false;
        }

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
    void testEqualsWithDifferentWorkers() throws DomeException{
        Point pointT = new Point(0,0);
        String workerID1 = "WW1";
        String workerID2 = "WW2";
        Cell cell1 = new Cell(pointT);
        try {
            cell1.setWorker(workerID1);
        } catch (WorkerAlreadyPresentException e) {
            assert false;
        }
        Cell cell2 = new Cell(pointT);
        try {
            cell2.setWorker(workerID2);
        } catch (WorkerAlreadyPresentException e) {
            assert false;
        }

        assertEquals(cell1,cell2);
        assertEquals(cell1.hashCode(), cell2.hashCode());
    }

    /**
     * This test verifies that two Cells with the same position and at least one null Worker are equal.
     */
    @Test
    void testEqualsWithNullWorkers() throws DomeException{
        Point pointT = new Point(0,0);
        Cell cell1 = new Cell(pointT);
        Cell cell2 = new Cell(pointT);

        //CASE 1: Both null.
        assertEquals(cell1,cell2);
        assertEquals(cell1.hashCode(), cell2.hashCode());

        //CASE 2: Only one null.
        String workerID1 = "WW1";
        try {
            cell1.setWorker(workerID1);
        } catch (WorkerAlreadyPresentException e) {
            assert false;
        }

        assertEquals(cell1,cell2);
        assertEquals(cell1.hashCode(), cell2.hashCode());
    }

    /**
     * This test checks that the cloned Cell and the Original Cell have not the same reference.
     */
    @Test
    void testClone() throws NoWorkerPresentException {
        Cell cellT = new Cell(new Point(0,0));
        assertNull(cellT.getWorkerID());

        Cell clonedcell = cellT.clone();

        assertNotSame(cellT, clonedcell);
        assertEquals(cellT,clonedcell);

        //Check building remains.
        assertEquals(cellT.getTopBuilding(), clonedcell.getTopBuilding());

       assertNull(clonedcell.getWorkerID());
    }




}