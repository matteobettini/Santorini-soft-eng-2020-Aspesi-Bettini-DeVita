package it.polimi.ingsw.common.packets;

import java.io.Serializable;

public class PacketStartPlayer implements Serializable {

    private static final long serialVersionUID = 5925758321795390900L;
    private final String startPlayer;

    public PacketStartPlayer(String startPlayer) {
        assert(startPlayer != null);
        this.startPlayer = startPlayer;
    }

    public String getStartPlayer() {
        return startPlayer;
    }
}
