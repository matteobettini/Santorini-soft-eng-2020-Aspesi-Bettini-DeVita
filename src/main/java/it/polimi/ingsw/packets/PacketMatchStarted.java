package it.polimi.ingsw.packets;

import java.io.Serializable;
import java.util.List;

public class PacketMatchStarted implements Serializable {

    private static final long serialVersionUID = -4874795609437494670L;
    private final List<String> players;
    private final boolean hardcore;

    public PacketMatchStarted(List<String> players, boolean hardcore) {
        this.players = players;
        this.hardcore = hardcore;
    }

    public List<String> getPlayers() {
        return players;
    }

    public boolean isHardcore() {
        return hardcore;
    }
}
