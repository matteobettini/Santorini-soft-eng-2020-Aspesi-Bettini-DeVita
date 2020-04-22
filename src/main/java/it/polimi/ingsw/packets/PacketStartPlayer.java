package it.polimi.ingsw.packets;

public class PacketStartPlayer {

    private final String startPlayer;

    public PacketStartPlayer(String startPlayer) {
        assert(startPlayer != null);
        this.startPlayer = startPlayer;
    }

    public String getStartPlayer() {
        return startPlayer;
    }
}
