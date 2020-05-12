package it.polimi.ingsw;

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
