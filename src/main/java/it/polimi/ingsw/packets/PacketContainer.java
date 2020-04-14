package it.polimi.ingsw.packets;

public class PacketContainer {

    private final PacketCardsFromServer packetCardsFromServer;
    private final PacketSetup packetSetup;
    private final PacketUpdateBoard packetUpdateBoard;
    private final PacketDoAction packetDoAction;
    private final PacketPossiblePoints packetPossiblePoints;

    public PacketContainer(PacketCardsFromServer packetCardsFromServer, PacketSetup packetSetup, PacketUpdateBoard packetUpdateBoard, PacketDoAction packetDoAction, PacketPossiblePoints packetPossiblePoints) {
        this.packetCardsFromServer = packetCardsFromServer;
        this.packetSetup = packetSetup;
        this.packetUpdateBoard = packetUpdateBoard;
        this.packetDoAction = packetDoAction;
        this.packetPossiblePoints = packetPossiblePoints;
    }

    public PacketCardsFromServer getPacketCardsFromServer() {
        return packetCardsFromServer;
    }

    public PacketSetup getPacketSetup() {
        return packetSetup;
    }

    public PacketUpdateBoard getPacketUpdateBoard() {
        return packetUpdateBoard;
    }

    public PacketDoAction getPacketDoAction() {
        return packetDoAction;
    }
}
