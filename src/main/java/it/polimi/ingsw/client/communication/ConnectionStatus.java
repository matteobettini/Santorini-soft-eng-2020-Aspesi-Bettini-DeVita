package it.polimi.ingsw.client.communication;

import it.polimi.ingsw.client.communication.enums.ConnectionState;

public class ConnectionStatus {

    private final ConnectionState state;
    private final String reasonOfClosure;

    ConnectionStatus(ConnectionState state, String reasonOfClosure) {
        this.state = state;
        this.reasonOfClosure = reasonOfClosure;
    }

    public ConnectionState getState() {
        return state;
    }

    public String getReasonOfClosure() {
        return reasonOfClosure;
    }
}
