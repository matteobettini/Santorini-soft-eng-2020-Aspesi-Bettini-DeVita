package it.polimi.ingsw;

public class ConnectionStatus {

    private final boolean closed;
    private final String reasonOfClosure;

    public ConnectionStatus(boolean closed, String  reasonOfClosure) {
        this.closed = closed;
        this.reasonOfClosure = reasonOfClosure;
    }

    public boolean isClosed() {
        return closed;
    }

    public String getReasonOfClosure() {
        return reasonOfClosure;
    }
}
