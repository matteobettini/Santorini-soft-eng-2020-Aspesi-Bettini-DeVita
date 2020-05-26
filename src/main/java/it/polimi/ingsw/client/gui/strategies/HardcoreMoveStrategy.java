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

public class HardcoreMoveStrategy implements MakeMoveStrategy {
    private MatchData matchData = MatchData.getInstance();

    private final MatchActiveController controller;

    private List<Point> moves;
    private GraphicalWorker selected;
    private boolean isEnabled = false;

    public HardcoreMoveStrategy(MatchActiveController controller) {
        this.controller = controller;
    }

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
            controller.showMessage("Select a worker to perform your move");
        }else{
            isEnabled = false;
            controller.showMessage(activePlayer + " is making his move");
        }
    }

    @Override
    public void handlePossibleActions(PacketPossibleMoves data) { }

    @Override
    public void handleCellClicked(GraphicalCell cell) {
        if (!isEnabled) return;

        if (selected == null){
            controller.showWait("You must select a worker first", true);
            return;
        }
        //Only check no dome and no current cell
        if (cell.containsDome() || cell.containsWorker(selected.getWorkerID())){
            controller.showWait("Invalid position selected", true);
            return;
        }
        selected.move(cell.getPosition());
        moves.add(cell.getPosition());
        controller.showMessage("Select next adjacent cell or click Confirm/Revert");
    }

    @Override
    public void handleWorkerClicked(GraphicalWorker worker) {
        if (!isEnabled) return;

        if (selected == null){
            selected = worker;
            selected.setSelected(true);
            controller.showMessage("Select an adjacent cell to move there");
        }else{
            controller.showWait("Worker already selected, click Revert to change selection", true);
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
