package it.polimi.ingsw.model;

import it.polimi.ingsw.model.turnInfo.BuildData;
import it.polimi.ingsw.model.turnInfo.MoveData;
import it.polimi.ingsw.packets.PacketBuild;
import it.polimi.ingsw.packets.PacketMove;

import java.lang.reflect.WildcardType;
import java.util.List;

public abstract class InternalModel {

    public abstract Board getBoard();

    public abstract List<Player> getPlayers();

    public abstract Player getPlayerByNick(String playerNick);

    public abstract Worker getWorkerByID(String workerID);

    public abstract MoveData packetMoveToMoveData(PacketMove packetMove);

    public abstract BuildData packetBuildToBuildData(PacketBuild packetBuild);



}
