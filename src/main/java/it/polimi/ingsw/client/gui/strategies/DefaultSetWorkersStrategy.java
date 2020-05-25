package it.polimi.ingsw.client.gui.strategies;

import it.polimi.ingsw.client.gui.controllers.MatchActiveController;
import it.polimi.ingsw.client.gui.graphical.GraphicalCell;
import it.polimi.ingsw.client.gui.graphical.GraphicalWorker;
import it.polimi.ingsw.client.gui.match_data.MatchData;
import it.polimi.ingsw.common.packets.PacketWorkersPositions;

import java.awt.*;
import java.util.*;

public class DefaultSetWorkersStrategy implements SetWorkersStrategy {
    private MatchData matchData = MatchData.getInstance();
    private final MatchActiveController controller;

    private Deque<String> workersToPlace;
    private Map<String, Point> data;
    private boolean isEnabled = false;

    public DefaultSetWorkersStrategy(MatchActiveController controller) {
        this.controller = controller;
    }

    @Override
    public void handleSetWorkers(String activePlayer, boolean isRetry) {
        assert !isRetry;
        if (matchData.getUsername().equals(activePlayer)){
            this.workersToPlace = new LinkedList<>(matchData.getWorkersIds(matchData.getUsername())); //Get my workers ids
            this.data = new HashMap<>();
            isEnabled = true;
            controller.showMessage("Select a cell to start placing your workers");
        }else{
            isEnabled = false;
            controller.showMessage(activePlayer + " is setting his workers' position");
        }
    }

    @Override
    public void handleCellClicked(GraphicalCell cell) {
        if (!isEnabled) return;
        if (workersToPlace.size() > 0){
            if (cell.isOccupied()){
                controller.showWait("The cell is occupied, choose another",true);
                return;
            }
            String workerID = workersToPlace.pop();
            controller.addWorker(workerID, cell.getPosition());
            data.put(workerID, cell.getPosition());
            if (workersToPlace.size() == 0)
                controller.showMessage("Now click Confirm or Revert");
        }else{
            controller.showMessage("You have no more workers to place. Click confirm, or restart operation");
        }
    }

    @Override
    public void handleWorkerClicked(GraphicalWorker worker) { }

    @Override
    public void handleConfirm() {
        if (!isEnabled) return;
        if (workersToPlace.size() > 0){
            controller.showWait("You must place all workers first",true);
            return;
        }
        controller.showWait("Sending positions to Game Server ...", false);
        PacketWorkersPositions packet = new PacketWorkersPositions(data);
        matchData.getClient().send(packet);
    }
}
