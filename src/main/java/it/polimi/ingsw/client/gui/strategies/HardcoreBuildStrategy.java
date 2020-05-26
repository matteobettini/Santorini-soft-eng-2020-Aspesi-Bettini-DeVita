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

public class HardcoreBuildStrategy implements MakeBuildStrategy {
    private MatchData matchData = MatchData.getInstance();

    private final MatchActiveController controller;

    private Map<Point, List<BuildingType>> builds;
    private List<Point> buildOrder;

    private GraphicalWorker selected;
    private boolean isEnabled = false;

    private GraphicalCell lastClicked;

    public HardcoreBuildStrategy(MatchActiveController controller) {
        this.controller = controller;
    }

    @Override
    public void handleBuildAction(String activePlayer, boolean isRetry) {
        if (matchData.getUsername().equals(activePlayer)){
            if (isRetry){
                controller.adjustModel(); //Undo changes
                controller.showWait("Your last action was invalid, redo", true);
            }
            builds = new HashMap<>();
            buildOrder = new LinkedList<>();
            lastClicked = null;
            selected = null;
            isEnabled = true;
            controller.showMessage("Select a worker to perform your build");
        }else{
            isEnabled = false;
            controller.showMessage(activePlayer + " is making his build");
        }
    }

    @Override
    public void handlePossibleActions(PacketPossibleBuilds data) { }

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
        //Check no dome
        if (cell.containsDome()){
            controller.showWait("Invalid position selected", true);
            return;
        }
        //Get possible buildings
        lastClicked = cell;
        controller.showMessage("Select one building from the ones showed on the right");
        controller.showBuildings(cell.getPossibleBuildings());
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
        //Decrement buildings counter
        matchData.setBuildingForUse(building);
        controller.showMessage("Select next adjacent cell or click Confirm/Revert");
    }

    @Override
    public void handleWorkerClicked(GraphicalWorker worker) {
        if (!isEnabled) return;
        if (selected == null){
            selected = worker;
            worker.setSelected(true);
            controller.showMessage("Select an adjacent cell to build there");
        }else{
            controller.showWait("Worker already selected, click Revert to change selection", true);
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
