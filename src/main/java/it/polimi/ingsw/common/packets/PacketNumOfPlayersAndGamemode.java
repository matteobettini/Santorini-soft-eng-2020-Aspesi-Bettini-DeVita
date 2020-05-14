package it.polimi.ingsw.common.packets;

import java.io.Serializable;

public class PacketNumOfPlayersAndGamemode implements Serializable {

    private static final long serialVersionUID = 8762206520023041748L;
    private final int desiredNumOfPlayers;
    private final boolean isDesiredHardcore;

    public PacketNumOfPlayersAndGamemode(int desiredNumOfPlayers, boolean isDesiredHardcore) {
        this.desiredNumOfPlayers = desiredNumOfPlayers;
        this.isDesiredHardcore = isDesiredHardcore;
    }

    public int getDesiredNumOfPlayers() {
        return desiredNumOfPlayers;
    }

    public boolean isDesiredHardcore() {
        return isDesiredHardcore;
    }
}
