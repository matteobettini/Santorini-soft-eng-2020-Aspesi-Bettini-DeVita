package it.polimi.ingsw.client.gui.strategies;

import it.polimi.ingsw.client.gui.controllers.MatchActiveController;
import it.polimi.ingsw.client.gui.graphical.GraphicalBoard;
import it.polimi.ingsw.client.gui.graphical.GraphicalCell;
import it.polimi.ingsw.client.gui.graphical.GraphicalWorker;
import it.polimi.ingsw.client.gui.match_data.MatchData;
import it.polimi.ingsw.common.packets.PacketMove;
import it.polimi.ingsw.common.packets.PacketPossibleMoves;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Normal strategy for moving on the board.
 * Uses server suggestion to show user graphically aid, and permit only legit moves.
 * In this mode, user cannot lose by violating card's must rules.
 */
public class NormalMoveStrategy implements MakeMoveStrategy {
    private MatchData matchData = MatchData.getInstance();

    //References to 3D object and controller
    private final GraphicalBoard graphicalBoard;
    private final List<GraphicalWorker> workers;
    private final MatchActiveController controller;

    //Move info
    private List<Point> moves;

    private Map<String, Set<Point>> possibleMoves; //Last possible move info (WorkerID->Points)

    private GraphicalWorker selected; //Last selected worker
    private boolean isEnabled = false; //True if input is enabled

    private List<String> possibleWorkers; //Workers that can be chosen at this point of the turn
    private boolean wasWorkerChoicePossible; //True if at this turn start was possible to change worker

    public NormalMoveStrategy(List<GraphicalWorker> workers, GraphicalBoard graphicalBoard, MatchActiveController controller) {
        this.workers = workers;
        this.graphicalBoard = graphicalBoard;
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
            assert !isRetry;
            //Reset data
            moves = new LinkedList<>();
            possibleWorkers = new LinkedList<>();
            selected = null;
            isEnabled = true;
            controller.inputChangeState(true);
            wasWorkerChoicePossible = false;
            //Retrieve move info
            controller.showWait("Asking moves to Game Server ...", false);
            matchData.getClient().send(new PacketMove(activePlayer));
        }else{
            isEnabled = false;
            controller.showMessage(activePlayer + " is making his move");
        }
    }

    /**
     * Handler of Move info data
     * @param data Data from server
     */
    @Override
    public void handlePossibleActions(PacketPossibleMoves data) {
        if (!isEnabled) return;

        possibleMoves = data.getPossibleMoves();
        possibleWorkers = new LinkedList<>();

        //Get selectable workers
        for(String wID : possibleMoves.keySet()){
            Set<Point> wPossible = possibleMoves.get(wID);
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
            Set<Point> availableCells = possibleMoves.get(selected.getID());
            graphicalBoard.selectCells(availableCells);
            if (availableCells.size() > 0) {
                String message = "Select next adjacent cell where to move";
                if (moves.size() > 0)
                    message += " or click Confirm/Revert";
                controller.showMessage(message);
            }else
                controller.showMessage("No more moves are possible, click Confirm or Revert");
        }
    }

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
        //Check if its position is one of the possible one for the selected worker
        Set<Point> possible = possibleMoves.get(selected.getID());
        if (possible.contains(cell.getPosition())){
            //If so, move
            selected.move(cell.getPosition());
            moves.add(cell.getPosition());
            //Ask next move
            controller.showWait("Asking moves to Game Server ...", false);
            matchData.getClient().send(new PacketMove(matchData.getUsername(), selected.getID(), moves));
        }else{
            controller.showWait("Invalid position selected", true);
        }
    }

    /**
     * Handler of worker clicked event
     * @param worker Worker clicked
     */
    @Override
    public void handleWorkerClicked(GraphicalWorker worker) {
        if (!isEnabled) return;
        if (selected == null){
            if (!possibleWorkers.contains(worker.getID())){
                controller.showWait("The worker is not available, because it cannot move", true);
                return;
            }
            selected = worker;
            worker.setSelected(true);
            graphicalBoard.selectCells(possibleMoves.get(worker.getID())); //Update selection
            controller.showMessage("Select an adjacent cell to move there");
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
        if (moves.size() < 1){ //If no move is present, return
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
