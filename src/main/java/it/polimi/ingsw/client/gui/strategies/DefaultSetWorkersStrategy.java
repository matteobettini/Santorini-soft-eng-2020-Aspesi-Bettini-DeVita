package it.polimi.ingsw.client.gui.strategies;

import it.polimi.ingsw.client.gui.controllers.MatchActiveController;
import it.polimi.ingsw.client.gui.graphical.GraphicalCell;
import it.polimi.ingsw.client.gui.graphical.GraphicalWorker;
import it.polimi.ingsw.client.gui.match_data.MatchData;
import it.polimi.ingsw.common.packets.PacketWorkersPositions;

import java.awt.*;
import java.util.*;

/**
 * Default strategy for placing workers on the board.
 */
public class DefaultSetWorkersStrategy implements SetWorkersStrategy {
    private MatchData matchData = MatchData.getInstance();
    private final MatchActiveController controller;

    private Deque<String> workersToPlace; //Workers that must be placed on the map
    private Map<String, Point> data; //Workers' position data
    private boolean isEnabled = false;

    public DefaultSetWorkersStrategy(MatchActiveController controller) {
        this.controller = controller;
    }

    /**
     * Handler of PacketDoAction of type SetWorkers
     * @param activePlayer Addressee of the packet
     * @param isRetry True if last response was not valid
     */
    @Override
    public void handleSetWorkers(String activePlayer, boolean isRetry) {
        if (matchData.getUsername().equals(activePlayer)){ //If the user is the addressee
            assert !isRetry; //Workers should always be placed correctly
            this.workersToPlace = new LinkedList<>(matchData.getWorkersIds(matchData.getUsername())); //Get my workers ids
            this.data = new HashMap<>();
            isEnabled = true;
            controller.inputChangeState(true); //Enable Confirm/Revert
            controller.showMessage("Select a cell to start placing your workers");
        }else{
            isEnabled = false;
            controller.showMessage(activePlayer + " is setting his workers' position");
        }
    }

    /**
     * Handler of Cell Clicked event
     * @param cell Cell that was clicked
     */
    @Override
    public void handleCellClicked(GraphicalCell cell) {
        if (!isEnabled) return;
        if (workersToPlace.size() > 0){ //Until has workers to place
            if (cell.isOccupied()){ //Cannot place on a dome (not possible at game starting) or on a other worker's place
                controller.showWait("The cell is occupied, choose another",true);
                return;
            }
            String workerID = workersToPlace.pop(); //Get this worker id
            controller.addWorker(workerID, cell.getPosition()); //Add worker to 3D space
            data.put(workerID, cell.getPosition()); //Populate positioning data
            if (workersToPlace.size() == 0)
                controller.showMessage("Now click Confirm or Revert");
        }else{
            controller.showMessage("You have no more workers to place. Click confirm, or restart operation");
        }
    }

    /**
     * Mock handler of worker clicked.
     * No worker needed to be selected in this phase
     * @param worker Worker selected
     */
    @Override
    public void handleWorkerClicked(GraphicalWorker worker) { }

    /**
     * Handler of the confirm button clicked
     */
    @Override
    public void handleConfirm() {
        if (!isEnabled) return;
        if (workersToPlace.size() > 0){ //If any worker is still present
            controller.showWait("You must place all workers first",true);
            return;
        }
        controller.showWait("Sending positions to Game Server ...", false);
        PacketWorkersPositions packet = new PacketWorkersPositions(data); //Send data
        matchData.getClient().send(packet);
    }
}
