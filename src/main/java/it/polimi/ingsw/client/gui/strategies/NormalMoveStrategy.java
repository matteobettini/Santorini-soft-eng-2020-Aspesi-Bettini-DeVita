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

public class NormalMoveStrategy implements MakeMoveStrategy {
    private MatchData matchData = MatchData.getInstance();

    private final GraphicalBoard graphicalBoard;
    private final List<GraphicalWorker> workers;
    private final MatchActiveController controller;

    private List<Point> moves;
    private Map<String, Set<Point>> possibleMoves;
    private GraphicalWorker selected;
    private boolean isEnabled = false;

    private List<String> possibleWorkers;
    private boolean wasWorkerChoicePossible;

    public NormalMoveStrategy(List<GraphicalWorker> workers, GraphicalBoard graphicalBoard, MatchActiveController controller) {
        this.workers = workers;
        this.graphicalBoard = graphicalBoard;
        this.controller = controller;
    }

    @Override
    public void handleMoveAction(String activePlayer, boolean isRetry) {
        if (matchData.getUsername().equals(activePlayer)){
            assert !isRetry;
            moves = new LinkedList<>();
            possibleWorkers = new LinkedList<>();
            selected = null;
            isEnabled = true;
            wasWorkerChoicePossible = false;
            //Retrieve move info
            controller.showWait("Asking moves to Game Server ...", false);
            matchData.getClient().send(new PacketMove(activePlayer));
        }else{
            isEnabled = false;
            controller.showMessage(activePlayer + " is making his move");
        }
    }

    @Override
    public void handlePossibleActions(PacketPossibleMoves data) {
        if (!isEnabled) return;

        possibleMoves = data.getPossibleMoves();
        possibleWorkers = new LinkedList<>();

        Set<Point> allPossible = new HashSet<>();
        for(String wID : possibleMoves.keySet()){
            Set<Point> wPossible = possibleMoves.get(wID);
            allPossible.addAll(wPossible);
            if (wPossible.size() > 0)
                possibleWorkers.add(wID);
        }

        //graphicalBoard.selectCells(allPossible);

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
                String message = "Select next adjacent cell where to move";
                if (moves.size() > 0)
                    message += " or click Confirm/Revert";
                controller.showMessage(message);
            }else
                controller.showMessage("No more moves are possible, click Confirm or Revert");
        }
    }

    @Override
    public void handleCellClicked(GraphicalCell cell) {
        if (!isEnabled) return;
        if (selected == null){
            controller.showWait("You must select a worker first", true);
            return;
        }
        Set<Point> possible = possibleMoves.get(selected.getWorkerID());
        if (possible.contains(cell.getPosition())){
            selected.move(cell.getPosition());
            moves.add(cell.getPosition());
            //Ask next move
            controller.showWait("Asking moves to Game Server ...", false);
            matchData.getClient().send(new PacketMove(matchData.getUsername(), selected.getWorkerID(), moves));
        }else{
            controller.showWait("Invalid position selected", true);
        }
    }

    @Override
    public void handleWorkerClicked(GraphicalWorker worker) {
        if (!isEnabled) return;
        if (selected == null){
            if (!possibleWorkers.contains(worker.getWorkerID())){
                controller.showWait("The worker is not available, because it cannot move", true);
                return;
            }
            selected = worker;
            worker.setSelected(true);
            graphicalBoard.selectCells(possibleMoves.get(worker.getWorkerID())); //Update selection
            controller.showMessage("Select an adjacent cell to move there");
        }else if (wasWorkerChoicePossible){
            controller.showWait("Worker already selected, click Revert to change selection", true);
        }else{
            controller.showWait("You cannot change worker selection at this point", true);
        }
    }

    @Override
    public void handleConfirm() {
        if (!isEnabled) return;
        if (moves.size() < 1){
            controller.showWait("You must perform your move first",true);
            return;
        }
        if (selected != null)
            selected.setSelected(false);
        controller.showWait("Sending move to Game Server ...", false);
        PacketMove packet = new PacketMove(matchData.getUsername(), selected.getWorkerID(), false, moves);
        matchData.getClient().send(packet);
    }
}
