package it.polimi.ingsw.obsolete;

import it.polimi.ingsw.common.packets.*;

public class PacketContainer {

    private final PacketCardsFromServer packetCardsFromServer;
    private final PacketSetup packetSetup;
    private final PacketUpdateBoard packetUpdateBoard;
    private final PacketDoAction packetDoAction;
    private final PacketPossibleMoves packetPossibleMoves;
    private final PacketPossibleBuilds packetPossibleBuilds;


    private PacketContainer(PacketCardsFromServer packetCardsFromServer, PacketSetup packetSetup, PacketUpdateBoard packetUpdateBoard, PacketDoAction packetDoAction, PacketPossibleMoves packetPossibleMoves, PacketPossibleBuilds packetPossibleBuilds) {
        this.packetCardsFromServer = packetCardsFromServer;
        this.packetSetup = packetSetup;
        this.packetUpdateBoard = packetUpdateBoard;
        this.packetDoAction = packetDoAction;
        this.packetPossibleMoves = packetPossibleMoves;
        this.packetPossibleBuilds = packetPossibleBuilds;
    }
    public PacketContainer(PacketCardsFromServer packetCardsFromServer) {
        this(packetCardsFromServer,null,null,null,null,null);
    }
    public PacketContainer(PacketSetup packetSetup) {
        this(null,packetSetup,null,null,null,null);
    }
    public PacketContainer(PacketUpdateBoard packetUpdateBoard) {
        this(null,null,packetUpdateBoard,null,null,null);
    }
    public PacketContainer(PacketDoAction packetDoAction) {
        this(null,null,null,packetDoAction,null,null);
    }
    public PacketContainer(PacketPossibleMoves packetPossibleMoves) {
        this(null,null,null,null,packetPossibleMoves,null);
    }
    public PacketContainer(PacketPossibleBuilds packetPossibleBuilds) {
        this(null,null,null,null,null,packetPossibleBuilds);
    }

    public PacketPossibleBuilds getPacketPossibleBuilds() {
        return packetPossibleBuilds;
    }

    public PacketPossibleMoves getPacketPossibleMoves() {
        return packetPossibleMoves;
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
