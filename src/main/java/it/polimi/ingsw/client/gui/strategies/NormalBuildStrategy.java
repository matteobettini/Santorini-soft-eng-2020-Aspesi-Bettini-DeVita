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

public class NormalBuildStrategy implements MakeBuildStrategy {

    private MatchData matchData = MatchData.getInstance();

    private final List<GraphicalWorker> workers;
    private final GraphicalBoard graphicalBoard;
    private final MatchActiveController controller;

    private Map<Point, List<BuildingType>> builds;
    private List<Point> buildOrder;

    private Map<String, Map<Point, List<BuildingType>>> possibleBuilds;

    private GraphicalWorker selected;
    private boolean isEnabled = false;

    private List<String> possibleWorkers;
    private GraphicalCell lastClicked;
    private boolean wasWorkerChoicePossible;

    public NormalBuildStrategy(List<GraphicalWorker> workers, GraphicalBoard graphicalBoard, MatchActiveController controller) {
        this.graphicalBoard = graphicalBoard;
        this.controller = controller;
        this.workers = workers;
    }

    @Override
    public void handleBuildAction(String activePlayer, boolean isRetry) {
        if (matchData.getUsername().equals(activePlayer)){
            assert !isRetry;
            builds = new HashMap<>();
            buildOrder = new LinkedList<>();
            possibleBuilds = new HashMap<>();
            lastClicked = null;
            selected = null;
            isEnabled = true;
            wasWorkerChoicePossible = false;
            //Retrieve move info
            controller.showWait("Asking builds to Game Server ...", false);
            matchData.getClient().send(new PacketBuild(activePlayer));
        }else{
            isEnabled = false;
            controller.showMessage(activePlayer + " is making his build");
        }
    }

    @Override
    public void handlePossibleActions(PacketPossibleBuilds data) {
        if (!isEnabled) return;

        possibleBuilds = data.getPossibleBuilds();
        possibleWorkers = new LinkedList<>();

        Set<Point> allPossible = new HashSet<>();
        for(String wID : possibleBuilds.keySet()){
            Set<Point> wPossible = possibleBuilds.get(wID).keySet();
            allPossible.addAll(wPossible);
            if (wPossible.size() > 0)
                possibleWorkers.add(wID);
        }

        graphicalBoard.selectCells(allPossible);

        if (selected == null) {
            assert possibleWorkers.size() > 0;
            if (possibleWorkers.size() == 1){
                selected = workers.stream().filter(w->w.getWorkerID().equals(possibleWorkers.get(0))).findFirst().orElse(null);
                assert selected != null;
                selected.setSelected(true);
            }else {
                wasWorkerChoicePossible = true;
                controller.showMessage("Select a worker by clicking on it");
            }
        }

        if (selected != null){
            if (allPossible.size() > 0) {
                String message = "Select next adjacent cell to build";
                if (buildOrder.size() > 0)
                    message += " or click Confirm/Revert";
                controller.showMessage(message);
            }else
                controller.showMessage("No more builds are possible, click Confirm or Revert");
        }
    }

    @Override
    public void handleBuildingClicked(BuildingType building) {
        assert lastClicked != null;
        buildHere(lastClicked,building);
        lastClicked = null;
    }

    @Override
    public void handleCellClicked(GraphicalCell cell) {
        if (!isEnabled || lastClicked != null) return;
        if (selected == null){
            controller.showWait("You must select a worker first", true);
            return;
        }
        Set<Point> possible = possibleBuilds.get(selected.getWorkerID()).keySet();
        if (possible.contains(cell.getPosition())){
            List<BuildingType> canBuildHere = possibleBuilds.get(selected.getWorkerID()).get(cell.getPosition());
            if (canBuildHere.size() == 1){
                //No need to ask, just build
                buildHere(cell,canBuildHere.get(0));
            }else{
                //Should ask the user
                lastClicked = cell;
                controller.showMessage("Select one building from the ones showed on the right");
                controller.showBuildings(canBuildHere);
            }
        }else{
            controller.showWait("Invalid position selected", true);
        }
    }

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
        buildOrder.add(point);
        //Show on graphics
        cell.addBuilding(building);
        //Set building for use
        matchData.setBuildingForUse(building);
        //Ask next data
        askNextPossibles();
    }
    private void askNextPossibles(){
        //Ask next build
        controller.showWait("Asking builds to Game Server ...", false);
        matchData.getClient().send(new PacketBuild(matchData.getUsername(), selected.getWorkerID(), builds, buildOrder));
    }

    @Override
    public void handleWorkerClicked(GraphicalWorker worker) {
        if (!isEnabled) return;
        if (selected == null){
            if (!possibleWorkers.contains(worker.getWorkerID())){
                controller.showWait("The worker is not available, because it cannot build", true);
                return;
            }
            selected = worker;
            worker.setSelected(true);
            graphicalBoard.selectCells(possibleBuilds.get(worker.getWorkerID()).keySet()); //Update selection
            controller.showMessage("Select an adjacent cell to build there");
        }else if (wasWorkerChoicePossible){
            controller.showWait("Worker already selected, click Revert to change selection", true);
        }else{
            controller.showWait("You cannot change worker selection at this point", true);
        }
    }

    @Override
    public void handleConfirm() {
        if (!isEnabled) return;
        if (buildOrder.size() < 1){
            controller.showWait("You must perform your build first",true);
            return;
        }
        if (selected != null)
            selected.setSelected(false);

        controller.showWait("Sending build to Game Server ...", false);
        PacketBuild packet = new PacketBuild(matchData.getUsername(), selected.getWorkerID(), false, builds, buildOrder);
        matchData.getClient().send(packet);
    }
}
