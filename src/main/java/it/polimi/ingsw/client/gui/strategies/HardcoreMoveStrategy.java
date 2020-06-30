package it.polimi.ingsw.client.gui.strategies;

import it.polimi.ingsw.client.gui.controllers.MatchActiveController;
import it.polimi.ingsw.client.gui.graphical.GraphicalCell;
import it.polimi.ingsw.client.gui.graphical.GraphicalWorker;
import it.polimi.ingsw.client.gui.match_data.MatchData;
import it.polimi.ingsw.common.packets.PacketMove;
import it.polimi.ingsw.common.packets.PacketPossibleMoves;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Hardcore strategy for moving on the board.
 * No suggestion supplied, and player loses violating card's must rules.
 */
public class HardcoreMoveStrategy implements MakeMoveStrategy {
    private MatchData matchData = MatchData.getInstance();

    private final MatchActiveController controller;

    //Moves info
    private List<Point> moves;

    //Worker info
    private GraphicalWorker selected;

    private boolean isEnabled = false;

    public HardcoreMoveStrategy(MatchActiveController controller) {
        this.controller = controller;
    }

    /**
     * Handler of PacketDoAction type MOVE
     * @param activePlayer Addressee of the packet
     * @param isRetry True if last action was invalid
     */
    @Override
    public void handleMoveAction(String activePlayer, boolean isRetry) {
        if (matchData.getUsername().equals(activePlayer)){
            if (isRetry){
                controller.adjustModel(); //Undo changes
                controller.showWait("Your last action was invalid, redo", true);
            }
            moves = new LinkedList<>();
            selected = null;
            isEnabled = true;
            controller.inputChangeState(true);
            controller.showMessage("Select a worker to perform your move");
        }else{
            isEnabled = false;
            controller.showMessage(activePlayer + " is making his move");
        }
    }

    /**
     * Mock handler, no possible actions are requested in this mode
     * @param data Data from server
     */
    @Override
    public void handlePossibleActions(PacketPossibleMoves data) { }

    /**
     * Handler of Cell clicked event
     * @param cell Cell that was clicked
     */
    @Override
    public void handleCellClicked(GraphicalCell cell) {
        if (!isEnabled) return;

        if (selected == null){
            controller.showWait("You must select a worker first", true);
            return;
        }

        //General checks on position
        if (cell.containsDome()){
            controller.showWait("Selected position must contain no dome", true);
            return;
        }else if (cell.containsWorker(selected.getID())){
            controller.showWait("Cannot select current position", true);
            return;
        }else if (!cell.isAdjacent(selected.getPosition(),false)){
            controller.showWait("Position must be adjacent to worker", true);
            return;
        }

        selected.move(cell.getPosition()); //Move 3D worker
        moves.add(cell.getPosition()); //Add data
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
            selected.setSelected(true);
            controller.showMessage("Select an adjacent cell to move there");
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
        if (moves.size() < 1){ //If no move present, return
            controller.showWait("You must perform your move first",true);
            return;
        }
        assert selected != null;
        selected.setSelected(false); //Clear selected worker
        controller.showWait("Sending move to Game Server ...", false);
        PacketMove packet = new PacketMove(matchData.getUsername(), selected.getID(), false, moves);
        matchData.getClient().send(packet); //Send response to server
    }
}
