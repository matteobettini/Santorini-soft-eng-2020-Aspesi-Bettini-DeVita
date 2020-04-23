package it.polimi.ingsw.packets;

import it.polimi.ingsw.model.enums.ActionType;

import java.io.Serializable;

public class PacketDoAction implements Serializable {

    private static final long serialVersionUID = 5586378055260580426L;
    private final String to;
    private final ActionType actionType;

    public PacketDoAction(String to, ActionType actionType) {
        this.to = to;
        this.actionType = actionType;
    }

    public String getTo() {
        return to;
    }

    public ActionType getActionType() {
        return actionType;
    }
}
