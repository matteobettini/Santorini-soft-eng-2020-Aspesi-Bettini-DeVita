package it.polimi.ingsw.client.gui.strategies;

import it.polimi.ingsw.common.enums.BuildingType;
import it.polimi.ingsw.common.packets.PacketPossibleBuilds;

public interface MakeBuildStrategy extends InteractionStrategy {
    void handleBuildAction(String activePlayer, boolean isRetry);
    void handlePossibleActions(PacketPossibleBuilds data);
    void handleBuildingClicked(BuildingType building);
}
