package it.polimi.ingsw.packets;

public enum ConnectionMessages {

    INVALID_PACKET_MESSAGE("Invalid packet"),
    INSERT_USERNAME("Insert your username"),
    INSERT_NUMBER_OF_PLAYERS("Insert the number of players"),
    IS_IT_HARDCORE("Do you want to play in hardcore mode?"),
    CONNECTION_CLOSED("Connection closed");

    private final String message;

    ConnectionMessages(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
