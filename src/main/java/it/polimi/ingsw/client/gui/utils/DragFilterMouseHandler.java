package it.polimi.ingsw.client.gui.utils;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

/**
 * This class filters mouse drag in a user click event.
 * If the user clicks and start dragging, the event is not fired
 * @param <T> Class linked to click event
 */
public class DragFilterMouseHandler <T> implements EventHandler<MouseEvent> {
    private boolean dragged = false;
    private final ClickedHandler<T> handler;
    private final T target;

    public DragFilterMouseHandler(ClickedHandler<T> handler, T target) {
        assert handler != null && target != null;
        this.handler = handler;
        this.target = target;
    }

    @Override
    public void handle(MouseEvent event) {
        if (event.getEventType() == MouseEvent.MOUSE_PRESSED){
            dragged = false;
        }else if (event.getEventType() == MouseEvent.DRAG_DETECTED){
            dragged = true;
        }else if (event.getEventType() == MouseEvent.MOUSE_RELEASED){
            if (!dragged)
                handler.handle(target);
        }
    }
}
