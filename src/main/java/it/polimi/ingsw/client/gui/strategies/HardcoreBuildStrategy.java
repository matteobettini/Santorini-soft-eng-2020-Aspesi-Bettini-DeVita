package it.polimi.ingsw.client.gui.strategies;

import it.polimi.ingsw.client.gui.controllers.MatchActiveController;
import it.polimi.ingsw.client.gui.graphical.GraphicalCell;
import it.polimi.ingsw.client.gui.graphical.GraphicalWorker;
import it.polimi.ingsw.client.gui.match_data.MatchData;
import it.polimi.ingsw.common.enums.BuildingType;
import it.polimi.ingsw.common.packets.PacketBuild;
import it.polimi.ingsw.common.packets.PacketPossibleBuilds;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Hardcore strategy for building on the board.
 * No suggestion supplied, and player loses violating card's must rules.
 */
public class HardcoreBuildStrategy implements MakeBuildStrategy {
    private MatchData matchData = MatchData.getInstance();

    private final MatchActiveController controller;

    //Building data info
    private Map<Point, List<BuildingType>> builds;
    private List<Point> buildOrder;

    //Worker info
    private GraphicalWorker selected;

    private boolean isEnabled = false;
    private GraphicalCell lastClicked; //Last cell clicked before opening the menu of buildings choosing

    public HardcoreBuildStrategy(MatchActiveController controller) {
        this.controller = controller;
    }

    /**
     * Handler of PacketDoAction type BUILD
     * @param activePlayer Addressee of the packet
     * @param isRetry True if last action was invalid
     */
    @Override
    public void handleBuildAction(String activePlayer, boolean isRetry) {
        if (matchData.getUsername().equals(activePlayer)){
            if (isRetry){
                controller.adjustModel(); //Undo changes to Graphical Model (and so 3D)
                controller.showWait("Your last action was invalid, redo", true);
                //Cannot distinguish if last action was MOVE or BUILD in case of Prometheus
            }
            //Init building info
            builds = new HashMap<>();
            buildOrder = new LinkedList<>();
            lastClicked = null;
            selected = null;
            isEnabled = true;
            controller.inputChangeState(true); //Enable Confirm/Revert
            controller.showMessage("Select a worker to perform your build");
        }else{
            isEnabled = false;
            controller.showMessage(activePlayer + " is making his build");
        }
    }

    /**
     * Mock handler, no possible actions are requested in this mode
     * @param data Data from server
     */
    @Override
    public void handlePossibleActions(PacketPossibleBuilds data) {

    }

    /**
     * Handler for Building type clicked from selection list
     * @param building Building type selected
     */
    @Override
    public void handleBuildingClicked(BuildingType building) {
        assert lastClicked != null;
        buildHere(lastClicked, building);
        lastClicked = null;
    }

    /**
     * Handler of Cell clicked event
     * @param cell Cell that was clicked
     */
    @Override
    public void handleCellClicked(GraphicalCell cell) {
        if (!isEnabled || lastClicked != null) return; //Only if building menu is not already opened
        if (selected == null){
            controller.showWait("You must select a worker first", true);
            return;
        }
        //General checks on position
        if (cell.containsDome()){
            controller.showWait("Selected position must contain no dome", true);
            return;
        }else if (!cell.isAdjacent(selected.getPosition(),true)){
            controller.showWait("Position must be adjacent to worker", true);
            return;
        }
        //Get possible buildings (the ones not already built)
        lastClicked = cell;
        controller.showMessage("Select one building from the ones showed on the left");
        controller.showBuildings(cell.getPossibleBuildings()); //Show buildings menu
    }

    /**
     * This method is used to build a building on a cell and populating building info
     * @param cell Cell where to build
     * @param building Building type to be built
     */
    private void buildHere(GraphicalCell cell, BuildingType building){
        Point point = cell.getPosition();
        //Save info
        if (builds.containsKey(point)){
            builds.get(point).add(building);
        }else{
            List<BuildingType> buildings = new LinkedList<>();
            buildings.add(building);
            builds.put(point,buildings);
        }
        buildOrder.add(point); //One point for every building
        //Show on graphics
        cell.addBuilding(building);
        //Decrement buildings counter
        matchData.setBuildingAsUsed(building);
        controller.showMessage("Select next adjacent cell or click Confirm/Revert");
    }

    /**
     * Handler of worker clicked event
     * @param worker Worker clicked
     */
    @Override
    public void handleWorkerClicked(GraphicalWorker worker) {
        if (!isEnabled) return;
        if (selected == null){ //If none already selected, select one
            selected = worker;
            worker.setSelected(true);
            controller.showMessage("Select an adjacent cell to build there");
        }else{
            controller.showWait("Worker already selected, click Revert to change selection", true);
        }
    }

    /**
     * Handler of Confirm button clicked
     */
    @Override
    public void handleConfirm() {
        if (!isEnabled) return;
        if (buildOrder.size() < 1){ //If no building is present, return
            controller.showWait("You must perform your build first",true);
            return;
        }
        assert selected != null;
        selected.setSelected(false); //Clear selected worker
        controller.showWait("Sending build to Game Server ...", false);
        PacketBuild packet = new PacketBuild(matchData.getUsername(), selected.getID(), false, builds, buildOrder);
        matchData.getClient().send(packet); //Send response to server
    }
}
