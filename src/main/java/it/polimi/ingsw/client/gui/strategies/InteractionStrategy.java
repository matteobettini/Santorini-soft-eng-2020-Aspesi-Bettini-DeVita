package it.polimi.ingsw.client.gui.strategies;

import it.polimi.ingsw.client.gui.graphical.GraphicalCell;
import it.polimi.ingsw.client.gui.graphical.GraphicalWorker;

public interface InteractionStrategy {
    void handleCellClicked(GraphicalCell cell);
    void handleWorkerClicked(GraphicalWorker worker);
    void handleConfirm();
}
