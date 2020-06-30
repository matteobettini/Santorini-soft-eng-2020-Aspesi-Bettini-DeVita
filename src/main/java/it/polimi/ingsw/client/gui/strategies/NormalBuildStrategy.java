package it.polimi.ingsw.client.gui.strategies;

import it.polimi.ingsw.client.gui.controllers.MatchActiveController;
import it.polimi.ingsw.client.gui.graphical.GraphicalBoard;
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
 * Normal strategy for building on the board.
 * Uses server suggestion to show user graphically aid, and permit only legit builds.
 * In this mode, user cannot lose by violating card's must rules.
 */
public class NormalBuildStrategy implements MakeBuildStrategy {

    private MatchData matchData = MatchData.getInstance();

    //References to 3D object and controller
    private final List<GraphicalWorker> workers;
    private final GraphicalBoard graphicalBoard;
    private final MatchActiveController controller;

    //Build info
    private Map<Point, List<BuildingType>> builds;
    private List<Point> buildOrder;

    private Map<String, Map<Point, List<BuildingType>>> possibleBuilds; //Last possible build info (WorkerID->(Point->Buildings))

    private GraphicalWorker selected; //Last selected worker
    private boolean isEnabled = false; //True if input is enabled

    private List<String> possibleWorkers; //Workers that can be chosen at this point of the turn
    private GraphicalCell lastClicked; //Last cell clicked before opening building menu
    private boolean wasWorkerChoicePossible; //True if at this turn start was possible to change worker

    public NormalBuildStrategy(List<GraphicalWorker> workers, GraphicalBoard graphicalBoard, MatchActiveController controller) {
        this.graphicalBoard = graphicalBoard;
        this.controller = controller;
        this.workers = workers;
    }

    /**
     * Handler of PacketDoAction type BUILD
     * @param activePlayer Addressee of the packet
     * @param isRetry True if last action was invalid
     */
    @Override
    public void handleBuildAction(String activePlayer, boolean isRetry) {
        if (matchData.getUsername().equals(activePlayer)){
            assert !isRetry;
            //Reset data
            builds = new HashMap<>();
            buildOrder = new LinkedList<>();
            possibleBuilds = new HashMap<>();
            possibleWorkers = new LinkedList<>();
            lastClicked = null;
            selected = null;
            isEnabled = true;
            controller.inputChangeState(true); //Enable Confirm/Revert
            wasWorkerChoicePossible = false;
            //Retrieve build info
            controller.showWait("Asking builds to Game Server ...", false);
            matchData.getClient().send(new PacketBuild(activePlayer));
        }else{
            isEnabled = false;
            controller.showMessage(activePlayer + " is making his build");
        }
    }

    /**
     * Handler of Build info data
     * @param data Data from server
     */
    @Override
    public void handlePossibleActions(PacketPossibleBuilds data) {
        if (!isEnabled) return;

        possibleBuilds = data.getPossibleBuilds();
        possibleWorkers = new LinkedList<>();

        //Get selectable workers
        for(String wID : possibleBuilds.keySet()){
            Set<Point> wPossible = possibleBuilds.get(wID).keySet();
            if (wPossible.size() > 0)
                possibleWorkers.add(wID); //If a worker can move, mark as selectable
        }

        if (selected == null) { //If none selected
            assert possibleWorkers.size() > 0; //Must always be, otherwise the player has lost
            if (possibleWorkers.size() == 1){ //If just one, preselect it
                selected = workers.stream().filter(w->w.getID().equals(possibleWorkers.get(0))).findFirst().orElse(null);
                assert selected != null;
                selected.setSelected(true);
            }else { //Else make user select
                wasWorkerChoicePossible = true;
                controller.showMessage("Select a worker by clicking on it");
            }
        }

        if (selected != null){ //If a worker is selected, show his possible cells
            Set<Point> availableCells = possibleBuilds.get(selected.getID()).keySet();
            graphicalBoard.selectCells(availableCells);
            if (availableCells.size() > 0) {
                String message = "Select next adjacent cell to build";
                if (buildOrder.size() > 0)
                    message += " or click Confirm/Revert";
                controller.showMessage(message);
            }else //If no other cells available
                controller.showMessage("No more builds are possible, click Confirm or Revert");
        }
    }

    /**
     * Handler for Building type clicked from selection list
     * @param building Building type selected
     */
    @Override
    public void handleBuildingClicked(BuildingType building) {
        assert lastClicked != null;
        buildHere(lastClicked,building);
        lastClicked = null;
    }

    /**
     * Handler of Cell clicked event
     * @param cell Cell that was clicked
     */
    @Override
    public void handleCellClicked(GraphicalCell cell) {
        if (!isEnabled || lastClicked != null) return;
        if (selected == null){
            controller.showWait("You must select a worker first", true);
            return;
        }
        //Check if its position is one of the possible one for the selected worker
        Set<Point> possible = possibleBuilds.get(selected.getID()).keySet();
        if (possible.contains(cell.getPosition())){
            List<BuildingType> canBuildHere = possibleBuilds.get(selected.getID()).get(cell.getPosition());
            if (canBuildHere.size() == 1){
                //No need to ask, just build
                buildHere(cell,canBuildHere.get(0));
            }else{
                //Should ask the user
                lastClicked = cell;
                controller.showMessage("Select one building from the ones showed on the left");
                controller.showBuildings(canBuildHere);
            }
        }else{
            controller.showWait("Invalid position selected", true);
        }
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
        //Ask next data
        controller.showWait("Asking builds to Game Server ...", false);
        matchData.getClient().send(new PacketBuild(matchData.getUsername(), selected.getID(), builds, buildOrder));
    }

    /**
     * Handler of worker clicked event
     * @param worker Worker clicked
     */
    @Override
    public void handleWorkerClicked(GraphicalWorker worker) {
        if (!isEnabled) return;
        if (selected == null){ //If none selected
            if (!possibleWorkers.contains(worker.getID())){
                controller.showWait("The worker is not available, because it cannot build", true);
                return;
            }
            //Select worker
            selected = worker;
            worker.setSelected(true);
            graphicalBoard.selectCells(possibleBuilds.get(worker.getID()).keySet()); //Update selection
            controller.showMessage("Select an adjacent cell to build there");
        }else if (wasWorkerChoicePossible){
            controller.showWait("Worker already selected, click Revert to change selection", true);
        }else{
            controller.showWait("You cannot change worker selection at this point", true);
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
