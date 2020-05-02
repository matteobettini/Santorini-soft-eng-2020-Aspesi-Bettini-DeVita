package it.polimi.ingsw.packets;

import java.io.Serializable;

public enum ConnectionMessages implements Serializable {

    INVALID_PACKET("Invalid packet"),
    INSERT_NICKNAME("Insert your nickname"),
    INSERT_NUMBER_OF_PLAYERS("Insert the desired number of players (2 or 3)"),
    IS_IT_HARDCORE("Do you want to play in hardcore mode?"),
    CONNECTION_CLOSED("Connection closed because of errors in your connection"),
    MATCH_ENDED("Match ended due to disconnected or not responding clients"),
    TIMER_ENDED("Your timer has expired");

    private final String message;
    private static final long serialVersionUID = -68031328223481106L;

    ConnectionMessages(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
