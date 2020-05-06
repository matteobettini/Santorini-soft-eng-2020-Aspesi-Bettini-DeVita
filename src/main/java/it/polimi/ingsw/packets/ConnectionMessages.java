package it.polimi.ingsw.packets;

import java.io.Serializable;

public enum ConnectionMessages implements Serializable {

    INVALID_PACKET("Invalid packet"),
    INSERT_NICKNAME("Insert your nickname"),
    INVALID_NICKNAME("The chosen nickname is invalid, enter a new one"),
    TAKEN_NICKNAME("The chosen nickname is already taken, enter a new one"),
    INSERT_NUMBER_OF_PLAYERS("Insert the desired number of players (2 or 3)"),
    IS_IT_HARDCORE("Do you want to play in hardcore mode?"),
    INSERT_NUMBER_OF_PLAYERS_AND_GAMEMODE("Insert the desired number of players (2 or 3) and the desired gamemode (normal or hardcore)"),
    CONNECTION_CLOSED("Connection closed because of errors in your connection"),
    MATCH_ENDED("Match ended due to disconnected or not responding clients"),
    TIMER_ENDED("You took too long to make your decision");

    private final String message;
    private static final long serialVersionUID = -68031328223481106L;

    ConnectionMessages(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
